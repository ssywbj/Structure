package com.suheng.structure.wallpaperpicker;

import android.app.WallpaperInfo;
import android.media.MediaRoute2Info;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
                tvExistPlayingPlayer.setText(mediaDataRepository.existPlayingPlayer() ? "有" : "无");
            }

            @Override
            public void onMediaAdded(@NonNull MediaData data) {
                mMediaControllerList.add(data);
                mediaControllerAdapter.notifyItemInserted(mMediaControllerList.indexOf(data));
                tvExistPlayingPlayer.setText(mediaDataRepository.existPlayingPlayer() ? "有" : "无");
            }

            @Override
            public void onMediaRemoved(@NonNull MediaData data) {
                final int position = mMediaControllerList.indexOf(data);
                mediaControllerAdapter.notifyItemRemoved(position);
                mMediaControllerList.remove(data);
                tvExistPlayingPlayer.setText(mediaDataRepository.existPlayingPlayer() ? "有" : "无");
            }
        };
        List<MediaData> mediaControllers = mediaDataRepository.getMediaDataList(onDataChangedListener);
        mediaDataRepository.addOnActiveSessionsChangedListener(onDataChangedListener);
        mMediaControllerList.addAll(mediaControllers);
        mediaControllerAdapter.notifyItemRangeChanged(0, mMediaControllerList.size());
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
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG, "onBackPressed()");
    }

}
