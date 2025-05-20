package com.suheng.structure.wallpaperpicker;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MediaControllerHelper {
    private static final String TAG = MediaControllerHelper.class.getSimpleName();
    private static final long UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1);
    @NonNull
    private final MediaController mMediaController;

    MediaControllerHelper(@NonNull MediaController mediaController) {
        mMediaController = mediaController;
    }

    @Nullable
    private MediaController.Callback mControllerCallback;
    @Nullable
    private Handler mHandler;
    @Nullable
    private Runnable mRunProgressChanged;

    public Handler getHandler() {
        if (mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper());
        }
        return mHandler;
    }

    private void sendMsgProgressChanged() {
        if (mRunProgressChanged == null) {
            mRunProgressChanged = () -> {
                PlaybackState playbackState = mMediaController.getPlaybackState();
                if ((mControllerCallback instanceof PlayProgressCallback) && playbackState != null) {
                    ((PlayProgressCallback) mControllerCallback).onProgressChanged(playbackState);
                    sendMsgProgressChanged();
                }
            };
        }

        final long delayMillis = UPDATE_RATE_MS - (System.currentTimeMillis() % UPDATE_RATE_MS);
        getHandler().postDelayed(mRunProgressChanged, delayMillis);
    }

    private void removeMsgProgressChanged() {
        if (mRunProgressChanged == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (getHandler().hasCallbacks(mRunProgressChanged)) {
                getHandler().removeCallbacks(mRunProgressChanged);
            }
        } else {
            getHandler().removeCallbacks(mRunProgressChanged);
        }
    }

    public void resolveMediaController() {
        parseAppInfo(mMediaController.getPackageName());

        final MediaMetadata metadata = mMediaController.getMetadata();
        if (metadata != null) {
            parseMediaMetadata(metadata);
        }

        final PlaybackState playbackState = mMediaController.getPlaybackState();
        if (playbackState != null) {
            parsePlaybackState(playbackState);
        }

        registerCallback();
    }

    private void parseAppInfo(@NonNull String pkg) {
        try {
            StringBuilder logInfo = new StringBuilder("AppInfo->pkg: " + pkg);
            ApplicationInfo applicationInfo = mPackageManager.getApplicationInfo(pkg, 0);
            Drawable icon = applicationInfo.loadIcon(mPackageManager);
            logInfo.append(", icon: ").append(System.identityHashCode(icon));
            CharSequence label = applicationInfo.loadLabel(mPackageManager);
            logInfo.append(", label: ").append(label);
            Log.i(TAG, logInfo.toString());
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "getApplicationInfo error", e);
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
    }

    public void registerCallback() {
        if (mControllerCallback == null) {
            mControllerCallback = new PlayProgressCallback() {
                @Override
                public void onSessionDestroyed() {
                    super.onSessionDestroyed();
                    Log.d(TAG, "onSessionDestroyed");
                    unregisterCallback();
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

                        removeMsgProgressChanged();
                        if (state.getState() == PlaybackState.STATE_PLAYING) {
                            sendMsgProgressChanged();
                        }
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
                public void onProgressChanged(@NonNull PlaybackState state) {
                    Log.d(TAG, "onProgressChanged, state: " + state);
                    parsePlaybackState(state);
                }
            };
        }
        mMediaController.registerCallback(mControllerCallback, getHandler());

        sendMsgProgressChanged();
    }

    public void unregisterCallback() {
        removeMsgProgressChanged();
        if (mControllerCallback != null) {
            mMediaController.unregisterCallback(mControllerCallback);
        }
    }

    private PackageManager mPackageManager;

    public void setPackageManager(PackageManager packageManager) {
        mPackageManager = packageManager;
    }

    public abstract static class PlayProgressCallback extends MediaController.Callback {
        void onProgressChanged(@NonNull PlaybackState state) {
        }
    }

}
