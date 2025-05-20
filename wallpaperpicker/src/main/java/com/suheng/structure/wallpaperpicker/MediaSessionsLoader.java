package com.suheng.structure.wallpaperpicker;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class MediaSessionsLoader {
    public static final String TAG = MediaSessionsLoader.class.getSimpleName();
    private static final long UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1);
    private static volatile MediaSessionsLoader sInstance;

    @NonNull
    private final MediaSessionManager mSessionManager;
    @NonNull
    private final PackageManager mPackageManager;
    @Nullable
    private MediaSessionManager.OnActiveSessionsChangedListener mSessionListener;
    @Nullable
    private MediaSessionManager.OnMediaKeyEventSessionChangedListener mOnKeyEventChangedListener;
    @Nullable
    private MediaSessionManager.OnSession2TokensChangedListener mOnTokensChangedListener;

    private MediaSessionsLoader(@NonNull Context context) {
        mSessionManager = (MediaSessionManager) context.getSystemService(Context.MEDIA_SESSION_SERVICE);
        mPackageManager = context.getPackageManager();
    }

    public static MediaSessionsLoader getInstance(@NonNull Context context) {
        if (sInstance == null) {
            synchronized (MediaSessionsLoader.class) {
                if (sInstance == null) {
                    sInstance = new MediaSessionsLoader(context);
                }
            }
        }
        return sInstance;
    }

    @Nullable
    private MediaController.Callback mControllerCallback;
    @Nullable
    private MediaController mMediaController;
    @Nullable
    private Handler mHandler;
    @Nullable
    private Runnable mRunUpdatePst;

    public Handler getHandler() {
        if (mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper());
        }
        return mHandler;
    }

    public @NonNull List<MediaController> getActiveSessions(@Nullable ComponentName notificationListener) {
        return mSessionManager.getActiveSessions(notificationListener);
    }

    public void addOnActiveSessionsChangedListener(@Nullable ComponentName notificationListener) {
        if (mSessionListener == null) {
            mSessionListener = controllers -> {
                if (controllers == null) {
                    Log.w(TAG, "onActiveSessionsChanged, controllers object is null");
                } else {
                    Log.i(TAG, "onActiveSessionsChanged, controllers size is " + controllers.size());
                }
            };
            mSessionManager.addOnActiveSessionsChangedListener(mSessionListener, notificationListener);
        }
    }

    public void addOnSession2TokensChangedListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (mOnTokensChangedListener == null) {
                mOnTokensChangedListener = session2Tokens -> Log.i(TAG, "onSession2TokensChanged, session2Tokens: " + session2Tokens);
                mSessionManager.addOnSession2TokensChangedListener(mOnTokensChangedListener);
            }
        }
    }

    public void addOnMediaKeyEventSessionChangedListener(@NonNull Executor executor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (mOnKeyEventChangedListener == null) {
                mOnKeyEventChangedListener = (pkg, sessionToken) -> Log.i(TAG, "onMediaKeyEventSessionChanged, pkg: " + pkg + ", sessionToken: " + sessionToken);
                mSessionManager.addOnMediaKeyEventSessionChangedListener(executor, mOnKeyEventChangedListener);
            }
        }
    }

    public void removeChangedListeners() {
        Log.d(TAG, "removeChangedListeners()");
        removeMsgUpdatePst();
        if (mSessionListener != null) {
            mSessionManager.removeOnActiveSessionsChangedListener(mSessionListener);
        }
        if (mOnTokensChangedListener != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                mSessionManager.removeOnSession2TokensChangedListener(mOnTokensChangedListener);
            }
        }
        if (mOnKeyEventChangedListener != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                mSessionManager.removeOnMediaKeyEventSessionChangedListener(mOnKeyEventChangedListener);
            }
        }
    }

    public void resolveMediaController(@NonNull MediaController mediaController) {
        mMediaController = mediaController;

        registerCallback(mediaController);

        parseAppInfo(mediaController.getPackageName());

        final MediaMetadata metadata = mediaController.getMetadata();
        if (metadata != null) {
            parseMediaMetadata(metadata);
        }

        final PlaybackState playbackState = mediaController.getPlaybackState();
        if (playbackState != null) {
            parsePlaybackState(playbackState);
        }
    }

    private void parseAppInfo(@NonNull String pkg) {
        StringBuilder logInfo = new StringBuilder("AppInfo->pkg: " + pkg);
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = mPackageManager.getApplicationInfo(pkg, 0);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "getApplicationInfo error", e);
        }
        if (applicationInfo != null) {
            Drawable icon = applicationInfo.loadIcon(mPackageManager);
            logInfo.append(", icon: ").append(System.identityHashCode(icon));
            CharSequence label = applicationInfo.loadLabel(mPackageManager);
            logInfo.append(", label: ").append(label);
        }
        Log.i(TAG, logInfo.toString());
    }

    private void parsePlaybackState(@NonNull PlaybackState playbackState) {
        final String playbackStateStr = playbackState.toString();
        StringBuilder logInfo = new StringBuilder("PlaybackState->" + playbackStateStr);
        final String stateFlag = "state";
        final int startIndex = playbackStateStr.indexOf(stateFlag);
        final int endIndex = playbackStateStr.indexOf(")");
        final String state = playbackStateStr.substring(startIndex + stateFlag.length() + 1, endIndex + 1);
        logInfo.append("\n").append(stateFlag).append(": ").append(state);
        long position = playbackState.getPosition();
        String formatPst = Utils.formatDuration(position);
        logInfo.append(", position: ").append(position).append("(").append(formatPst).append(")");
        Log.i(TAG, logInfo.toString());

        removeMsgUpdatePst();
        if (playbackState.getState() == PlaybackState.STATE_PLAYING) {
            sendMstUpdatePst();
        }
    }

    private void sendMstUpdatePst() {
        if (mRunUpdatePst == null) {
            mRunUpdatePst = () -> {
                if (mMediaController == null) {
                    return;
                }

                PlaybackState playbackState = mMediaController.getPlaybackState();
                if ((mControllerCallback instanceof MediaUpdateListener) && playbackState != null) {
                    ((MediaUpdateListener) mControllerCallback).onProgressUpdate(playbackState);
                    sendMstUpdatePst();
                }
            };
        }

        final long delayMillis = UPDATE_RATE_MS - (System.currentTimeMillis() % UPDATE_RATE_MS);
        getHandler().postDelayed(mRunUpdatePst, delayMillis);
    }

    private void removeMsgUpdatePst() {
        if (mRunUpdatePst == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (getHandler().hasCallbacks(mRunUpdatePst)) {
                getHandler().removeCallbacks(mRunUpdatePst);
            }
        } else {
            getHandler().removeCallbacks(mRunUpdatePst);
        }
    }

    private void parseMediaMetadata(@NonNull MediaMetadata metadata) {
        StringBuilder logInfo = new StringBuilder("MediaMetadata->");
        CharSequence text = metadata.getText(MediaMetadata.METADATA_KEY_TITLE);
        logInfo.append("title: ").append(text);
        CharSequence artist = metadata.getText(MediaMetadata.METADATA_KEY_ARTIST);
        logInfo.append(", artist: ").append(artist);
        long duration = metadata.getLong(MediaMetadata.METADATA_KEY_DURATION);
        String formatDuration = Utils.formatDuration(duration);
        logInfo.append(", duration: ").append(duration).append("(").append(formatDuration).append(")");
        Bitmap bitmap = metadata.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART);
        logInfo.append(", bitmap: ").append(System.identityHashCode(bitmap));
        Log.i(TAG, logInfo.toString());
    }

    private void registerCallback(@NonNull MediaController mediaController) {
        final MediaController.Callback callback = new MediaUpdateListener() {
            @Override
            public void onSessionDestroyed() {
                super.onSessionDestroyed();
                Log.d(TAG, "onSessionDestroyed");
                removeChangedListeners();
            }

            @Override
            public void onSessionEvent(@NonNull String event, @Nullable Bundle extras) {
                super.onSessionEvent(event, extras);
                Log.d(TAG, "onSessionEvent, event: " + event);
            }

            @Override
            public void onPlaybackStateChanged(@Nullable PlaybackState state) {
                super.onPlaybackStateChanged(state);
                Log.d(TAG, "onPlaybackStateChanged, state: " + state);
                if (state != null) {
                    parsePlaybackState(state);
                }
            }

            @Override
            public void onMetadataChanged(@Nullable MediaMetadata metadata) {
                super.onMetadataChanged(metadata);
                Log.d(TAG, "onMetadataChanged, metadata: " + metadata);
                if (metadata != null) {
                    parseMediaMetadata(metadata);
                }
            }

            @Override
            public void onQueueChanged(@Nullable List<MediaSession.QueueItem> queue) {
                super.onQueueChanged(queue);
                Log.d(TAG, "onQueueChanged, queue: " + queue);
            }

            @Override
            public void onQueueTitleChanged(@Nullable CharSequence title) {
                super.onQueueTitleChanged(title);
                Log.d(TAG, "onQueueTitleChanged, title: " + title);
            }

            @Override
            public void onExtrasChanged(@Nullable Bundle extras) {
                super.onExtrasChanged(extras);
                Log.d(TAG, "onExtrasChanged, extras: " + extras);
            }

            @Override
            public void onAudioInfoChanged(MediaController.PlaybackInfo info) {
                super.onAudioInfoChanged(info);
                Log.d(TAG, "onAudioInfoChanged, info: " + info);
            }

            @Override
            public void onProgressUpdate(@NonNull PlaybackState state) {
                Log.d(TAG, "onProgressUpdate, state: " + state);
                parsePlaybackState(state);
            }
        };
        mediaController.registerCallback(callback, new Handler(Looper.getMainLooper()));
        mControllerCallback = callback;
    }

    private static class MediaUpdateListener extends MediaController.Callback {
        public void onProgressUpdate(@NonNull PlaybackState state) {
        }
    }

}
