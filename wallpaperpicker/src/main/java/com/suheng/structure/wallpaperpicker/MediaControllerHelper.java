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
import java.util.Objects;
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

    public @NonNull MediaData resolveMediaController(@NonNull MediaController mediaController) {
        MediaData mediaData = new MediaData();
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

    private void parseAppInfo(@NonNull String pkg, @Nullable MediaData mediaData) {
        try {
            StringBuilder logInfo = new StringBuilder("AppInfo->pkg: " + pkg);
            ApplicationInfo applicationInfo = mPackageManager.getApplicationInfo(pkg, 0);
            Drawable icon = applicationInfo.loadIcon(mPackageManager);
            logInfo.append(", icon: ").append(System.identityHashCode(icon));
            CharSequence label = applicationInfo.loadLabel(mPackageManager);
            logInfo.append(", label: ").append(label);
            Log.i(TAG, logInfo.toString());

            if (mediaData != null) {
                mediaData.icon = icon;
                mediaData.label = label.toString();
            }
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
        String mediaId = metadata.getString(MediaMetadata.METADATA_KEY_MEDIA_ID);
        logInfo.append(", mediaId: ").append(mediaId);
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
        customActionsLog(playbackState, logInfo);
        final long actions = playbackState.getActions();
        boolean existsPrevious = includesAction(actions, PlaybackState.ACTION_SKIP_TO_PREVIOUS);
        boolean existsNext = includesAction(actions, PlaybackState.ACTION_SKIP_TO_NEXT);
        logInfo.append(", actions: ").append(actions).append(", existsPrevious: ").append(existsPrevious)
                .append(", existsNext: ").append(existsNext);

        Log.i(TAG, logInfo.toString());

        if (mediaData != null) {
            mediaData.stateText = state;
            mediaData.state = playbackState.getState();
            mediaData.position = position;
            mediaData.progress = (int) (mediaData.position / 1000);
            mediaData.customActions = playbackState.getCustomActions();
            mediaData.existsPrevious = existsPrevious;
            mediaData.existsNext = existsNext;
        }
    }

    private void customActionsLog(@NonNull PlaybackState playbackState, StringBuilder logInfo) {
        List<PlaybackState.CustomAction> customActions = playbackState.getCustomActions();
        if (customActions != null) {
            logInfo.append(", customActions: ").append(customActions.size()).append(" ");
            for (PlaybackState.CustomAction customAction : customActions) {
                String action = customAction.getAction();
                CharSequence name = customAction.getName();
                int icon = customAction.getIcon();
                logInfo.append("(").append(action).append(",").append(name).append(",").append(icon).append(")");
                logInfo.append("-");
            }
            logInfo.deleteCharAt(logInfo.length() - 1);
        }
    }

    public void registerCallback(@NonNull MediaController mediaController, @Nullable MediaDataRepository.OnDataChangedListener onDataChangedListener) {
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
        if (mediaController == null) {
            return null;
        }
        for (Map.Entry<MediaController, MediaData> mediaDataEntry : mControllerMediaDataMap.entrySet()) {
            MediaController controller = mediaDataEntry.getKey();
            if (Objects.equals(controller.getPackageName(), mediaController.getPackageName())) {
                return mControllerMediaDataMap.get(controller);
            }
        }
        return null;
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

    public @Nullable MediaController getCacheController(String pkg) {
        for (Map.Entry<PlayProgressListener, MediaController> mediaDataEntry : mControllerCallbackMap.entrySet()) {
            MediaController controller = mediaDataEntry.getValue();
            if (Objects.equals(pkg, controller.getPackageName())) {
                return controller;
            }
        }
        return null;
    }

    private boolean includesAction(long stateActions, long action) {
        if ((action == PlaybackState.ACTION_PLAY || action == PlaybackState.ACTION_PAUSE)
                && (stateActions & PlaybackState.ACTION_PLAY_PAUSE) > 0L) {
            return true;
        }
        return (stateActions & action) != 0L;
    }

    public void sendCustomAction(String pkg, @NonNull String action, @Nullable Bundle args) {
        MediaController mediaController = getCacheController(pkg);
        if (mediaController == null) {
            return;
        }
        //test app: Spotify、PocketFM、Music player(com.search.music.mp3.musicplayer)、YT Music
        mediaController.getTransportControls().sendCustomAction(action, args);
    }

    public void seekTo(String pkg, long pos) {
        MediaController mediaController = getCacheController(pkg);
        if (mediaController == null) {
            return;
        }
        mediaController.getTransportControls().seekTo(pos);
        seekTo(mediaController, pos);
    }

    public void play(String pkg) {
        MediaController mediaController = getCacheController(pkg);
        if (mediaController == null) {
            return;
        }
        mediaController.getTransportControls().play();
    }

    public void pause(String pkg) {
        MediaController mediaController = getCacheController(pkg);
        if (mediaController == null) {
            return;
        }
        mediaController.getTransportControls().pause();
    }

    public void skipToPrevious(String pkg) {
        MediaController mediaController = getCacheController(pkg);
        if (mediaController == null) {
            return;
        }
        mediaController.getTransportControls().skipToPrevious();
    }

    public void skipToNext(String pkg) {
        MediaController mediaController = getCacheController(pkg);
        if (mediaController == null) {
            return;
        }
        mediaController.getTransportControls().skipToNext();
    }

    private final class PlayProgressListener extends PlayProgressCallback {
        @Nullable
        private Runnable mRunProgressChanged;
        @Nullable
        private MediaDataRepository.OnDataChangedListener mOnDataChangedListener;

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

                if (mOnDataChangedListener != null && mediaData != null) {
                    mOnDataChangedListener.onMediaUpdated(mediaData);
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

                if (mOnDataChangedListener != null && mediaData != null) {
                    mOnDataChangedListener.onMediaUpdated(mediaData);
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

                if (mOnDataChangedListener != null && mediaData != null) {
                    mOnDataChangedListener.onMediaUpdated(mediaData);
                }
            }
        }

        public void sendMsgProgressChanged() {
            final MediaController mediaController = mControllerCallbackMap.get(this);
            if (mediaController == null) {
                Log.w(TAG, "don't sendMsgProgressChanged, because MediaController is null");
                return;
            }
            final PlaybackState playbackState = mediaController.getPlaybackState();
            if (playbackState == null) {
                Log.w(TAG, "don't sendMsgProgressChanged, because PlaybackState is null");
                return;
            }

            if (mRunProgressChanged == null) {
                mRunProgressChanged = () -> {
                    if (playbackState.getState() == PlaybackState.STATE_PLAYING) {
                        onProgressChanged(playbackState);
                        sendMsgProgressChanged();
                    } else {
                        Log.i(TAG, "don't sendMsgProgressChanged, because isn't in playing status ");
                    }
                };
            }

            final long delayMillis = UPDATE_RATE_MS - (System.currentTimeMillis() % UPDATE_RATE_MS);
            getHandler().postDelayed(mRunProgressChanged, delayMillis);
        }

        public void removeMsgProgressChanged() {
            if (mRunProgressChanged == null) {
                return;
            }
            if (getHandler().hasCallbacks(mRunProgressChanged)) {
                getHandler().removeCallbacks(mRunProgressChanged);
            }
            mRunProgressChanged = null;
        }

        public void setOnDataChangedListener(@Nullable MediaDataRepository.OnDataChangedListener onDataChangedListener) {
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
