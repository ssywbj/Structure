package com.suheng.structure.wallpaperpicker;

import android.content.ComponentName;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class MusicPlayerActivity extends AppCompatActivity {

    private MediaBrowserCompat mMediaBrowser;
    private MediaControllerCompat mMediaController;

    // 1. 连接回调
    private final MediaBrowserCompat.ConnectionCallback mConnectionCallback =
            new MediaBrowserCompat.ConnectionCallback() {
                @Override
                public void onConnected() {
                    // 连接成功，获取 SessionToken
                    MediaSessionCompat.Token token = mMediaBrowser.getSessionToken();
                    // 创建 MediaController
                    mMediaController = new MediaControllerCompat(MusicPlayerActivity.this, token);
                    MediaControllerCompat.setMediaController(MusicPlayerActivity.this, mMediaController);

                    // 注册回调监听播放状态变化
                    mMediaController.registerCallback(mControllerCallback);

                    // 触发 UI 更新
                    updateUI(mMediaController.getPlaybackState());

                    // 订阅媒体数据（触发 Service 的 onLoadChildren）
                    mMediaBrowser.subscribe(mMediaBrowser.getRoot(), mSubscriptionCallback);
                }

                @Override
                public void onConnectionFailed() {
                    Log.e("MusicPlayer", "连接服务失败");
                }
            };

    // 2. 媒体控制器回调
    private final MediaControllerCompat.Callback mControllerCallback =
            new MediaControllerCompat.Callback() {
                @Override
                public void onPlaybackStateChanged(PlaybackStateCompat state) {
                    updateUI(state); // 更新播放/暂停按钮状态
                }
            };

    private void updateUI(PlaybackStateCompat state) {

    }

    // 3. 媒体数据订阅回调
    private final MediaBrowserCompat.SubscriptionCallback mSubscriptionCallback =
            new MediaBrowserCompat.SubscriptionCallback() {
                @Override
                public void onChildrenLoaded(@NonNull String parentId,
                                             @NonNull List<MediaBrowserCompat.MediaItem> children) {
                    // 获取到媒体列表，更新 UI（如 RecyclerView）
                    // adapter.submitList(children);
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ... 初始化 UI ...

        // 初始化 MediaBrowser，绑定 MediaPlayService
        mMediaBrowser = new MediaBrowserCompat(this,
                new ComponentName(this, MediaPlayService.class),
                mConnectionCallback,
                null); // rootHints
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMediaBrowser.connect(); // 开始连接服务
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (MediaControllerCompat.getMediaController(this) != null) {
            MediaControllerCompat.getMediaController(this).unregisterCallback(mControllerCallback);
        }
        mMediaBrowser.disconnect(); // 断开连接
    }

    // 播放按钮点击事件
    private void onPlayButtonClick() {
        if (mMediaController != null) {
            PlaybackStateCompat state = mMediaController.getPlaybackState();
            if (state != null && state.getState() == PlaybackStateCompat.STATE_PLAYING) {
                mMediaController.getTransportControls().pause();
            } else {
                mMediaController.getTransportControls().play();
            }
        }
    }

}