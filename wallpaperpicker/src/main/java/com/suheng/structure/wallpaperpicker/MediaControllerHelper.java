package com.suheng.structure.wallpaperpicker;

import android.content.Context;
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

import com.suheng.structure.wallpaperpicker.bean.MediaData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MediaControllerHelper {
    private static final String TAG = MediaControllerHelper.class.getSimpleName();
    private static final long UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1);
    private final PackageManager mPackageManager;
    private final Map<PlayProgressListener, MediaController> mControllerCallbackMap = new HashMap<>();
    private final Map<MediaController, MediaData> mControllerMediaDataMap = new HashMap<>();
    private Handler mHandler;

    MediaControllerHelper(@NonNull Context context) {
        mPackageManager = context.getPackageManager();
    }

    public Handler getHandler() {
        if (mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper());
        }
        return mHandler;
    }

    public MediaData resolveMediaController(@NonNull MediaController mediaController) {
        MediaData mediaData = new MediaData();
        mediaData.mediaController = mediaController;
        mediaData.transportControls = mediaController.getTransportControls();
        mediaData.pkg = mediaController.getPackageName();
        mControllerMediaDataMap.put(mediaController, mediaData);

        parseAppInfo(mediaData.pkg, mediaData);

        final MediaMetadata metadata = mediaController.getMetadata();
        if (metadata != null) {
            parseMediaMetadata(metadata, mediaData);
        }

        final PlaybackState playbackState = mediaController.getPlaybackState();
        if (playbackState != null) {
            parsePlaybackState(playbackState, mediaData);
        }
        return mediaData;
    }

    /*public List<MediaData> resolveMediaControllers(@NonNull List<MediaController> mediaControllers) {
        List<MediaData> mediaDataList = new ArrayList<>();
        for (MediaController mediaController : mediaControllers) {
            mediaDataList.add(resolveMediaController(mediaController));
        }
        return mediaDataList;
    }*/

    private void parseAppInfo(@NonNull String pkg, @Nullable MediaData dest) {
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

    private void parseMediaMetadata(@NonNull MediaMetadata metadata, @Nullable MediaData mediaData) {
        StringBuilder logInfo = new StringBuilder("MediaMetadata->");
        String title = metadata.getText(MediaMetadata.METADATA_KEY_TITLE).toString();
        logInfo.append("title: ").append(title);
        String artist = metadata.getText(MediaMetadata.METADATA_KEY_ARTIST).toString();
        logInfo.append(", artist: ").append(artist);
        long duration = metadata.getLong(MediaMetadata.METADATA_KEY_DURATION);
        String formatDuration = Utils.formatDuration(duration);
        logInfo.append(", duration: ").append(duration).append("(").append(formatDuration).append(")");
        Bitmap albumArt = metadata.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART);
        logInfo.append(", albumArt: ").append(System.identityHashCode(albumArt));
        Log.i(TAG, logInfo.toString());

        if (mediaData != null) {
            mediaData.title = title;
            mediaData.artist = artist;
            mediaData.duration = duration;
            mediaData.progressMax = (int) (mediaData.duration / 1000);
            mediaData.albumArt = albumArt;
        }
    }

    private void parsePlaybackState(@NonNull PlaybackState playbackState, @Nullable MediaData mediaData) {
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

        if (mediaData != null) {
            mediaData.stateText = state;
            mediaData.state = playbackState.getState();
            mediaData.position = position;
            mediaData.progress = (int) (mediaData.position / 1000);
        }
    }

    public void registerCallback(@NonNull MediaController mediaController, @Nullable OnDataChangedListener onDataChangedListener) {
        PlayProgressListener controllerCallback = new PlayProgressListener();
        controllerCallback.setOnDataChangedListener(onDataChangedListener);
        mediaController.registerCallback(controllerCallback, getHandler());

        mControllerCallbackMap.put(controllerCallback, mediaController);

        controllerCallback.sendMsgProgressChanged();
    }

    public void unregisterCallback(@NonNull MediaController mediaController) {
        PlayProgressListener progressListener = null;
        for (Map.Entry<PlayProgressListener, MediaController> entry : mControllerCallbackMap.entrySet()) {
            MediaController controller = entry.getValue();
            if (mediaController == controller) {
                progressListener = entry.getKey();
                controller.unregisterCallback(progressListener);
                progressListener.removeMsgProgressChanged();
                break;
            }
        }

        if (progressListener != null) {
            mControllerCallbackMap.remove(progressListener, mediaController);
        }
    }

    public void unregisterCallbacks() {
        for (Map.Entry<PlayProgressListener, MediaController> entry : mControllerCallbackMap.entrySet()) {
            MediaController controller = entry.getValue();
            PlayProgressListener progressListener = entry.getKey();
            controller.unregisterCallback(progressListener);
            progressListener.removeMsgProgressChanged();

        }
        mControllerCallbackMap.clear();
    }

    public void seekTo(MediaController mediaController, long pst) {
        for (Map.Entry<PlayProgressListener, MediaController> entry : mControllerCallbackMap.entrySet()) {
            if (entry.getValue() == mediaController) {
                entry.getKey().seekTo(pst);
                break;
            }
        }
    }

    public @Nullable MediaData getCacheMediaData(MediaController mediaController) {
        return mControllerMediaDataMap.get(mediaController);
    }

    public @Nullable List<MediaData> getCacheMediaData() {
        if (mControllerMediaDataMap.isEmpty()) {
            return null;
        }
        List<MediaData> mediaDataList = new ArrayList<>();
        for (Map.Entry<MediaController, MediaData> mediaDataEntry : mControllerMediaDataMap.entrySet()) {
            mediaDataList.add(mediaDataEntry.getValue());
        }
        return mediaDataList;
    }

    private final class PlayProgressListener extends PlayProgressCallback {
        @Nullable
        private Runnable mRunProgressChanged;
        @Nullable
        private OnDataChangedListener mOnDataChangedListener;

        @Override
        public void onSessionDestroyed() {
            super.onSessionDestroyed();
            Log.d(TAG, "onSessionDestroyed");
            MediaController mediaController = mControllerCallbackMap.get(this);
            if (mediaController != null) {
                unregisterCallback(mediaController);
            }
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
                MediaController mediaController = mControllerCallbackMap.get(this);
                MediaData mediaData = getCacheMediaData(mediaController);
                parsePlaybackState(state, mediaData);

                if (mOnDataChangedListener != null) {
                    mOnDataChangedListener.onDataChanged(mediaData);
                }

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
                MediaController mediaController = mControllerCallbackMap.get(this);
                MediaData mediaData = getCacheMediaData(mediaController);
                parseMediaMetadata(metadata, mediaData);

                if (mOnDataChangedListener != null) {
                    mOnDataChangedListener.onDataChanged(mediaData);
                }
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
        public void onProgressChanged(@Nullable PlaybackState state) {
            super.onProgressChanged(state);
            Log.d(TAG, "onProgressChanged, state: " + state);
            if (state != null) {
                MediaController mediaController = mControllerCallbackMap.get(this);
                MediaData mediaData = getCacheMediaData(mediaController);
                parsePlaybackState(state, mediaData);

                if (mOnDataChangedListener != null) {
                    mOnDataChangedListener.onDataChanged(mediaData);
                }
            }
        }

        public void sendMsgProgressChanged() {
            if (mRunProgressChanged == null) {
                mRunProgressChanged = () -> {
                    MediaController mediaController = mControllerCallbackMap.get(this);
                    if (mediaController == null) {
                        return;
                    }
                    onProgressChanged(mediaController.getPlaybackState());
                    sendMsgProgressChanged();
                };
            }

            final long delayMillis = UPDATE_RATE_MS - (System.currentTimeMillis() % UPDATE_RATE_MS);
            getHandler().postDelayed(mRunProgressChanged, delayMillis);
        }

        public void removeMsgProgressChanged() {
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
            mRunProgressChanged = null;
        }

        public void setOnDataChangedListener(@Nullable OnDataChangedListener onDataChangedListener) {
            mOnDataChangedListener = onDataChangedListener;
        }

        public void seekTo(long pst) {
            MediaController mediaController = mControllerCallbackMap.get(this);
            if (mediaController == null) {
                return;
            }

            PlaybackState remoteState = mediaController.getPlaybackState();
            if (remoteState == null) {
                return;
            }

            removeMsgProgressChanged();

            PlaybackState.Builder builder = new PlaybackState.Builder(remoteState);
            PlaybackState localState = builder.setState(remoteState.getState(), pst
                    , remoteState.getPlaybackSpeed(), System.currentTimeMillis()).build();
            onProgressChanged(localState);

            sendMsgProgressChanged();
        }

    }

    public abstract static class PlayProgressCallback extends MediaController.Callback {
        void onProgressChanged(@Nullable PlaybackState state) {
        }
    }

}
