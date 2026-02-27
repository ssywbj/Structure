package com.suheng.structure.wallpaperpicker;

import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media.MediaBrowserServiceCompat;

import java.util.ArrayList;
import java.util.List;

//https://developer.android.com/media/legacy/audio/mediabrowserservice?hl=zh-cn#java
//https://github.com/android/uamp.git
//public class MediaPlayService extends MediaBrowserService {
public class MediaPlayService extends MediaBrowserServiceCompat {
    public static final String TAG = MediaPlayService.class.getSimpleName();
    private static final String MY_MEDIA_ROOT_ID = "media_root_id";
    private static final String MY_EMPTY_MEDIA_ROOT_ID = "empty_root_id";

    private MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;

    @Override
    public void onCreate() {
        super.onCreate();

        // Create a MediaSessionCompat
        mMediaSession = new MediaSessionCompat(this, TAG);

        // Enable callbacks from MediaButtons and TransportControls
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);
        mMediaSession.setPlaybackState(mStateBuilder.build());

        // MySessionCallback() has methods that handle callbacks from a media controller
        mMediaSession.setCallback(new MySessionCallback());

        // Set the session's token so that client activities can communicate with it.
        setSessionToken(mMediaSession.getSessionToken());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaSession != null) {
            mMediaSession.release();
        }
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        // (Optional) Control the level of access for the specified package name.
        // You'll need to write your own logic to do this.
        Log.i(TAG, "onGetRoot: " + clientPackageName + ", " + clientUid);
        if (allowBrowsing(clientPackageName, clientUid)) {
            // Returns a root ID that clients can use with onLoadChildren() to retrieve
            // the content hierarchy.
            return new BrowserRoot(MY_MEDIA_ROOT_ID, null);
        } else {
            // Clients can connect, but this BrowserRoot is an empty root
            // so onLoadChildren returns nothing. This disables the ability to browse for content.
            return new BrowserRoot(MY_EMPTY_MEDIA_ROOT_ID, null);
        }
    }

    private boolean allowBrowsing(String clientPackageName, int clientUid) {
        // Implement your permission logic here
        return true;
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        Log.i(TAG, "onLoadChildren: " + parentId);
        // Browsing not allowed
        if (TextUtils.equals(MY_EMPTY_MEDIA_ROOT_ID, parentId)) {
            result.sendResult(null);
            return;
        }

        // Assume for example that the music catalog is already loaded/cached.

        List<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();

        // Check if this is the root menu:
        if (MY_MEDIA_ROOT_ID.equals(parentId)) {
            // Build the MediaItem objects for the top level,
            // and put them in the mediaItems list...
            // For example:
            // mediaItems.add(createMediaItem("song_1", "Song Title 1", "Artist 1"));
            // mediaItems.add(createMediaItem("song_2", "Song Title 2", "Artist 2"));
        } else {
            // Examine the passed parentMediaId to see which submenu we're at,
            // and put the children of that menu in the mediaItems list...
        }
        result.sendResult(mediaItems);
    }

    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            super.onPlay();
            Log.i(TAG, "onPlay");
            // 模拟开始播放
            setPlaybackState(PlaybackStateCompat.STATE_PLAYING);
            // 这里应该调用实际的播放器逻辑，例如 mediaPlayer.start()
        }

        @Override
        public void onPause() {
            super.onPause();
            Log.i(TAG, "onPause");
            // 模拟暂停播放
            setPlaybackState(PlaybackStateCompat.STATE_PAUSED);
            // 这里应该调用实际的播放器逻辑，例如 mediaPlayer.pause()
        }

        @Override
        public void onStop() {
            super.onStop();
            Log.i(TAG, "onStop");
            // 模拟停止播放
            setPlaybackState(PlaybackStateCompat.STATE_STOPPED);
            // 这里应该调用实际的播放器逻辑，例如 mediaPlayer.stop()
        }
    }

    private void setPlaybackState(int state) {
        long position = PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN;
        mStateBuilder.setState(state, position, 1.0f);
        mMediaSession.setPlaybackState(mStateBuilder.build());
    }

}
