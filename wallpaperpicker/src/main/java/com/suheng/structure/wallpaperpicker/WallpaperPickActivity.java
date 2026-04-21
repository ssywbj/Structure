package com.suheng.structure.wallpaperpicker;

import android.app.WallpaperInfo;
import android.content.Intent;
import android.media.MediaRoute2Info;
import android.media.Rating;
import android.media.Session2Token;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.suheng.structure.wallpaperpicker.adapter.LivePaperAdapter;
import com.suheng.structure.wallpaperpicker.adapter.MediaControllerAdapter;
import com.suheng.structure.wallpaperpicker.adapter.MediaRouteAdapter;
import com.suheng.structure.wallpaperpicker.bean.MediaData;
import com.suheng.structure.wallpaperpicker.bean.RouteData;

import java.util.ArrayList;
import java.util.List;

public class WallpaperPickActivity extends AppCompatActivity {

    private static final String TAG = "WallpaperPickActivity";
    private final List<WallpaperInfo> mWallpaperInfoList = new ArrayList<>();
    private final List<RouteData> mRouteDataList = new ArrayList<>();
    private final List<MediaData> mMediaControllerList = new ArrayList<>();

    /*@Nullable
    private ComponentName mComponentNotification;*/
    //private @Nullable MediaRouter2 mMediaRouter2;

    private MediaSessionHelper mMediaSessionHelper;

    private final MediaSessionManager.OnSession2TokensChangedListener mSessionTokensChangedListener = tokens -> {
        Log.d(TAG, "tokens.size(): " + tokens.size());
        for (Session2Token token : tokens) {
            Log.d(TAG, "token uid: " + token.getUid() + ", " + token);
        }
    };

    private final MediaSessionManager.OnMediaKeyEventSessionChangedListener mMediaKeyEventSessionChangedListener = (packageName, sessionToken) -> {
        Log.d(TAG, "sessionToken pkg: " + packageName + ", " + sessionToken);
        if (sessionToken != null) {
            WallpaperPickActivity context = WallpaperPickActivity.this;
            final MediaController mediaController = new MediaController(context, sessionToken);
            final MediaSession mediaSession = new MediaSession(context, TAG, mediaController.getSessionInfo());
            boolean active = mediaSession.isActive();
            Log.d(TAG, "sessionToken, active: " + active);
            mediaSession.setCallback(new MediaSession.Callback() {
                @Override
                public void onCommand(@NonNull String command, @Nullable Bundle args, @Nullable ResultReceiver cb) {
                    super.onCommand(command, args, cb);
                    MediaSessionManager.RemoteUserInfo currentControllerInfo = mediaSession.getCurrentControllerInfo();
                    final int uid = currentControllerInfo.getUid();
                    final int pid = currentControllerInfo.getPid();
                    Log.d(TAG, "onCommand, command: " + command + ", pid: " + pid + ", uid: " + uid);
                }

                @Override
                public void onCustomAction(@NonNull String action, @Nullable Bundle extras) {
                    super.onCustomAction(action, extras);
                    Log.d(TAG, "onCustomAction, action: " + action);
                }

                @Override
                public void onFastForward() {
                    super.onFastForward();
                }

                @Override
                public boolean onMediaButtonEvent(@NonNull Intent mediaButtonIntent) {
                    return super.onMediaButtonEvent(mediaButtonIntent);
                }

                @Override
                public void onPause() {
                    super.onPause();
                    MediaSessionManager.RemoteUserInfo currentControllerInfo = mediaSession.getCurrentControllerInfo();
                    final int uid = currentControllerInfo.getUid();
                    final int pid = currentControllerInfo.getPid();
                    Log.d(TAG, "onPause, uid: " + uid + ", pid: " + pid);
                }

                @Override
                public void onPlay() {
                    super.onPlay();
                    MediaSessionManager.RemoteUserInfo currentControllerInfo = mediaSession.getCurrentControllerInfo();
                    final int uid = currentControllerInfo.getUid();
                    final int pid = currentControllerInfo.getPid();
                    Log.d(TAG, "onPlay, uid: " + uid + ", pid: " + pid);
                }

                @Override
                public void onPlayFromMediaId(String mediaId, Bundle extras) {
                    super.onPlayFromMediaId(mediaId, extras);
                }

                @Override
                public void onPlayFromSearch(String query, Bundle extras) {
                    super.onPlayFromSearch(query, extras);
                }

                @Override
                public void onPlayFromUri(Uri uri, Bundle extras) {
                    super.onPlayFromUri(uri, extras);
                }

                @Override
                public void onPrepare() {
                    super.onPrepare();
                }

                @Override
                public void onPrepareFromMediaId(String mediaId, Bundle extras) {
                    super.onPrepareFromMediaId(mediaId, extras);
                }

                @Override
                public void onPrepareFromSearch(String query, Bundle extras) {
                    super.onPrepareFromSearch(query, extras);
                }

                @Override
                public void onPrepareFromUri(Uri uri, Bundle extras) {
                    super.onPrepareFromUri(uri, extras);
                }

                @Override
                public void onRewind() {
                    super.onRewind();
                }

                @Override
                public void onSeekTo(long pos) {
                    super.onSeekTo(pos);
                }

                @Override
                public void onSetPlaybackSpeed(float speed) {
                    super.onSetPlaybackSpeed(speed);
                }

                @Override
                public void onSetRating(@NonNull Rating rating) {
                    super.onSetRating(rating);
                }

                @Override
                public void onSkipToNext() {
                    super.onSkipToNext();
                }

                @Override
                public void onSkipToPrevious() {
                    super.onSkipToPrevious();
                }

                @Override
                public void onSkipToQueueItem(long id) {
                    super.onSkipToQueueItem(id);
                }

                @Override
                public void onStop() {
                    super.onStop();
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wallpaperpick_activity_wallpaper_pick);
        /*Intent intent = new Intent(
                WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
        intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                new ComponentName(this, ZhipuWallpaperService.class));
        startActivity(intent);*/

        this.initRecyclerView();

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Log.d("NotificationListenerServiceImpl", "requestRebind NotificationListenerService");
            mComponentNotification = new ComponentName(this, NotificationListenerServiceImpl.class);
            NotificationListenerService.requestRebind(mComponentNotification);
        }*/

        mMediaSessionHelper = new MediaSessionHelper(this);
        mMediaSessionHelper.addOnSession2TokensChangedListener(mSessionTokensChangedListener);
        mMediaSessionHelper.addOnMediaKeyEventSessionChangedListener(getMainExecutor(), mMediaKeyEventSessionChangedListener);
    }

    private void initRecyclerView() {
        final RecyclerView recyclerView = findViewById(R.id.recycler_view_wallpaper);
        LivePaperAdapter livePaperAdapter = new LivePaperAdapter(mWallpaperInfoList);
        livePaperAdapter.setOnItemClickListener((view, data, position) -> {
            Utils.setLiveWallpaper(this, data.getPackageName(), data.getServiceName());
            recyclerView.postDelayed(this::finish, 100);
        });
        //https://blog.csdn.net/u010687392/article/details/47950199?utm_medium=distribute.pc_relevant.none-task-blog-baidujs-2
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(livePaperAdapter);
        WallpaperRepository wallpaperRepository = WallpaperRepository.getInstance(this);
        mWallpaperInfoList.addAll(wallpaperRepository.getPaperList(this));
        livePaperAdapter.notifyItemRangeChanged(0, mWallpaperInfoList.size());

        MediaRouteRepository routeRepository = MediaRouteRepository.getInstance(this);
        RecyclerView rvRouteList = findViewById(R.id.recycler_route_list);
        MediaRouteAdapter mediaRouteAdapter = new MediaRouteAdapter(mRouteDataList);
        mediaRouteAdapter.setOnItemClickListener((view, data, position) -> {
            if (data.getVolumeHandling() == MediaRoute2Info.PLAYBACK_VOLUME_VARIABLE) {
                return;
            }
            routeRepository.transferTo(data);
        });
        //https://blog.csdn.net/u010687392/article/details/47950199?utm_medium=distribute.pc_relevant.none-task-blog-baidujs-2
        rvRouteList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rvRouteList.setItemAnimator(new DefaultItemAnimator());
        rvRouteList.setAdapter(mediaRouteAdapter);
        List<RouteData> routeList = routeRepository.getRouteList(getMainExecutor(), new MediaRouteRepository.OnDataLChangedListener() {
            @Override
            public void onRouteRemoved(@NonNull RouteData routeData) {
                final int index = mRouteDataList.indexOf(routeData);
                mediaRouteAdapter.notifyItemRemoved(index);
                mRouteDataList.remove(routeData);
            }

            @Override
            public void onRouteAdded(@NonNull RouteData routeData) {
                mRouteDataList.add(routeData);
                mediaRouteAdapter.notifyItemInserted(mRouteDataList.indexOf(routeData));
            }

            @Override
            public void onRouteUpdated(@NonNull RouteData routeData) {
                final int index = mRouteDataList.indexOf(routeData);
                mediaRouteAdapter.notifyItemChanged(index, routeData);
            }
        });
        mRouteDataList.addAll(routeList);
        mediaRouteAdapter.notifyItemRangeChanged(0, mRouteDataList.size());

        TextView tvExistPlayingPlayer = findViewById(R.id.existPlayingPlayer);
        RecyclerView rvMediaList = findViewById(R.id.recycler_media_list);
        MediaControllerAdapter mediaControllerAdapter = new MediaControllerAdapter(mMediaControllerList);
        //https://blog.csdn.net/u010687392/article/details/47950199?utm_medium=distribute.pc_relevant.none-task-blog-baidujs-2
        rvMediaList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rvMediaList.setItemAnimator(new DefaultItemAnimator());
        rvMediaList.setAdapter(mediaControllerAdapter);
        MediaDataRepository mediaDataRepository = MediaDataRepository.getInstance(this);
        MediaDataRepository.OnDataChangedListener onDataChangedListener = new MediaDataRepository.OnDataChangedListener() {
            @Override
            public void onMediaUpdated(@NonNull MediaData data) {
                final int position = mMediaControllerList.indexOf(data);
                mediaControllerAdapter.notifyItemChanged(position, data);
                updateUI(tvExistPlayingPlayer, mediaDataRepository);
            }

            @Override
            public void onMediaAdded(@NonNull MediaData data) {
                mMediaControllerList.add(data);
                mediaControllerAdapter.notifyItemInserted(mMediaControllerList.indexOf(data));
                updateUI(tvExistPlayingPlayer, mediaDataRepository);
            }

            @Override
            public void onMediaRemoved(@NonNull MediaData data) {
                final int position = mMediaControllerList.indexOf(data);
                mediaControllerAdapter.notifyItemRemoved(position);
                mMediaControllerList.remove(data);
                updateUI(tvExistPlayingPlayer, mediaDataRepository);
            }
        };
        List<MediaData> mediaControllers = mediaDataRepository.getMediaDataList(onDataChangedListener);
        mediaDataRepository.addOnActiveSessionsChangedListener(onDataChangedListener);
        mMediaControllerList.addAll(mediaControllers);
        mediaControllerAdapter.notifyItemRangeChanged(0, mMediaControllerList.size());
        updateUI(tvExistPlayingPlayer, mediaDataRepository);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
        MediaRouteRepository.getInstance(this).destroy();
        mWallpaperInfoList.clear();
        mMediaControllerList.clear();
        mRouteDataList.clear();
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (mComponentNotification != null) {
                NotificationListenerService.requestRebind(mComponentNotification);
            }
        }*/
        mMediaSessionHelper.removeOnSession2TokensChangedListener(mSessionTokensChangedListener);
        mMediaSessionHelper.removeOnMediaKeyEventSessionChangedListener(mMediaKeyEventSessionChangedListener);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG, "onBackPressed()");
    }

    private void updateUI(TextView tvExistPlayingPlayer, MediaDataRepository mediaDataRepository) {
        tvExistPlayingPlayer.setText(String.format(getString(R.string.exist_playing_player)
                , mediaDataRepository.existPlayingPlayer() ? "Yes" : "No"));
    }
}
