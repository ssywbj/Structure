package com.suheng.structure.wallpaperpicker;

import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.media.MediaRoute2Info;
import android.media.MediaRouter2;
import android.media.RouteDiscoveryPreference;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.suheng.structure.wallpaperpicker.adapter.LivePaperAdapter;
import com.suheng.structure.wallpaperpicker.adapter.MediaControllerAdapter;
import com.suheng.structure.wallpaperpicker.adapter.RecyclerAdapter;
import com.suheng.structure.wallpaperpicker.bean.MediaData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WallpaperPickActivity extends AppCompatActivity {

    private static final String TAG = "WallpaperPickActivity";
    private final List<WallpaperInfo> mWallpaperInfoList = new ArrayList<>();
    private MediaRouteAdapter mMediaRouteAdapter;
    private MediaControllerAdapter mMediaControllerAdapter;
    private final List<MediaRoute2Info> mMediaRoute2InfoList = new ArrayList<>();

    private final List<MediaData> mMediaControllerList = new ArrayList<>();

    /*@Nullable
    private ComponentName mComponentNotification;*/
    private @Nullable MediaRouter2 mMediaRouter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wallpaperpick_activity_wallpaper_pick);
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        /*try {
            wallpaperManager.setBitmap(BitmapFactory.decodeResource(getResources(), android.R.drawable.menu_frame));
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        /*Intent intent = new Intent(
                WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
        intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                new ComponentName(this, ZhipuWallpaperService.class));
        startActivity(intent);*/

        WallpaperRepository wallpaperRepository = WallpaperRepository.getInstance(this);
        mWallpaperInfoList.addAll(wallpaperRepository.getPaperList(this));

        this.initRecyclerView();

        MediaDataRepository mediaDataRepository = MediaDataRepository.getInstance(this);
        OnDataChangedListener onDataChangedListener = new OnDataChangedListener() {
            @Override
            public void onDataChanged(@NonNull MediaData data) {
                final int position = mMediaControllerList.indexOf(data);
                mMediaControllerAdapter.notifyItemChanged(position, data);
            }

            @Override
            public void onDataAdded(@NonNull MediaData data) {
                mMediaControllerList.add(data);
                mMediaControllerAdapter.notifyItemRangeChanged(0, mMediaControllerList.size());
            }

            @Override
            public void onDataRemoved(@NonNull MediaData data) {
                final int position = mMediaControllerList.indexOf(data);
                mMediaControllerAdapter.notifyItemRemoved(position);
                mMediaControllerList.remove(data);
            }
        };
        List<MediaData> mediaControllers = mediaDataRepository.getMediaDataList(null, onDataChangedListener);
        mediaDataRepository.addOnActiveSessionsChangedListener(onDataChangedListener);
        mMediaControllerList.addAll(mediaControllers);
        mMediaControllerAdapter.notifyItemRangeChanged(0, mMediaControllerList.size());
        /*mMediaSessionHelper.addOnActiveSessionsChangedListener(null);
        mMediaSessionHelper.addOnSession2TokensChangedListener();
        mMediaSessionHelper.addOnMediaKeyEventSessionChangedListener(ContextCompat.getMainExecutor(this));*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            this.getWifiList();
        } else {
            Log.w(TAG, "Don't get wifi list because version is lower than R");
        }

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Log.d("NotificationListenerServiceImpl", "requestRebind NotificationListenerService");
            mComponentNotification = new ComponentName(this, NotificationListenerServiceImpl.class);
            NotificationListenerService.requestRebind(mComponentNotification);
        }*/
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private void getWifiList() {
        // 定义希望发现的路由类型（例如音频路由）
        mMediaRouter2 = MediaRouter2.getInstance(this);
        List<String> preferredFeatures = Collections.singletonList(MediaRoute2Info.FEATURE_LIVE_AUDIO);
        // 构造RouteDiscoveryPreference对象
        RouteDiscoveryPreference discoveryPreference = new RouteDiscoveryPreference.Builder(preferredFeatures, true).build();

        List<MediaRoute2Info> routes = mMediaRouter2.getRoutes();
        List<MediaRouter2.RoutingController> controllers = mMediaRouter2.getControllers();
        Log.d(TAG, "controllers.size(): " + controllers.size() + ", routes.size(): " + routes.size());
        for (MediaRouter2.RoutingController controller : controllers) {
            String controllerId = controller.getId();
            for (MediaRoute2Info route : controller.getSelectedRoutes()) {
                String routeId = route.getId();
                String name = route.getName().toString();
                int type = -1;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    type = route.getType();
                }
                Log.i(TAG, "selectedRoutes, controllerId: " + controllerId + ", routeId: " + routeId + ", name: " + name + ", type: " + type);
            }

            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                it.transferableRoutes.forEach {
                    val routeId = it.id
                    val name = it.name
                    val type = it.type
                    Log.i("Wbj", "transferableRoutes, controllerId:$controllerId, routeId:$routeId, name:$name, type:$type")
                }
            }*/

            for (MediaRoute2Info route : controller.getSelectableRoutes()) {
                String routeId = route.getId();
                String name = route.getName().toString();
                int type = -1;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    type = route.getType();
                }
                Log.d(TAG, "selectableRoutes, controllerId: " + controllerId + ", routeId: " + routeId + ", name: " + name + ", type: " + type);
            }

            for (MediaRoute2Info route : controller.getDeselectableRoutes()) {
                String routeId = route.getId();
                String name = route.getName().toString();
                int type = -1;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    type = route.getType();
                }
                Log.d(TAG, "deselectableRoutes, controllerId: " + controllerId + ", routeId: " + routeId + ", name: " + name + ", type: " + type);
            }
        }

        MediaRouter2.RouteCallback routeCallback = new MediaRouter2.RouteCallback() {
            @Override
            public void onRoutesUpdated(@NonNull List<MediaRoute2Info> routes) {
                super.onRoutesUpdated(routes);
                Log.i(TAG, "onRoutesUpdated, routes.size: " + routes.size() + ", mediaRouter2.routes.size: " + mMediaRouter2.getRoutes().size() + ", thread: " + Thread.currentThread().getName());
                mMediaRoute2InfoList.clear();
                mMediaRoute2InfoList.addAll(routes);
                for (MediaRoute2Info route : routes) {
                    Log.d(TAG, "onRoutesUpdated, route HashCode: " + System.identityHashCode(route) + ", id " + route.getId());
                }
                mMediaRouteAdapter.notifyItemRangeChanged(0, mMediaRoute2InfoList.size());
            }
        };
        mMediaRouter2.registerRouteCallback(getMainExecutor(), routeCallback, discoveryPreference);
        //mMediaRouter2.unregisterRouteCallback(routeCallback);

        mMediaRouter2.registerTransferCallback(getMainExecutor(), new MediaRouter2.TransferCallback() {
            @Override
            public void onTransfer(@NonNull MediaRouter2.RoutingController oldController, @NonNull MediaRouter2.RoutingController newController) {
                super.onTransfer(oldController, newController);
                Log.i("Wbj", "onTransfer, oldController: " + oldController + ", newController: " + newController);
            }

            @Override
            public void onTransferFailure(@NonNull MediaRoute2Info requestedRoute) {
                super.onTransferFailure(requestedRoute);
                Log.w("Wbj", "onTransferFailure: " + requestedRoute);
            }

            @Override
            public void onStop(@NonNull MediaRouter2.RoutingController controller) {
                super.onStop(controller);
                Log.i("Wbj", "onStop, controller");
            }
        });
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

        RecyclerView rvRouteList = findViewById(R.id.recycler_route_list);
        mMediaRouteAdapter = new MediaRouteAdapter(mMediaRoute2InfoList);
        mMediaRouteAdapter.setOnItemClickListener((view, data, position) -> {
            if (mMediaRouter2 != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (data.getVolumeHandling() == MediaRoute2Info.PLAYBACK_VOLUME_VARIABLE) {
                        return;
                    }
                    Log.d(TAG, "transferTo, route: " + data);
                    mMediaRouter2.transferTo(data);
                }
            }
        });
        //https://blog.csdn.net/u010687392/article/details/47950199?utm_medium=distribute.pc_relevant.none-task-blog-baidujs-2
        rvRouteList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rvRouteList.setItemAnimator(new DefaultItemAnimator());
        rvRouteList.setAdapter(mMediaRouteAdapter);

        RecyclerView rvMediaList = findViewById(R.id.recycler_media_list);
        mMediaControllerAdapter = new MediaControllerAdapter(mMediaControllerList);
        //https://blog.csdn.net/u010687392/article/details/47950199?utm_medium=distribute.pc_relevant.none-task-blog-baidujs-2
        rvMediaList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rvMediaList.setItemAnimator(new DefaultItemAnimator());
        rvMediaList.setAdapter(mMediaControllerAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
        mWallpaperInfoList.clear();
        //mMediaSessionHelper.removeChangedListeners();
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

    private static final class MediaRouteAdapter extends RecyclerAdapter<MediaRoute2Info, RecyclerView.ViewHolder> {

        MediaRouteAdapter(List<MediaRoute2Info> dataList) {
            super(dataList);
        }

        @Override
        protected void bindView(RecyclerView.ViewHolder viewHolder, final int position, final MediaRoute2Info data) {
            if (viewHolder instanceof ContentHolder) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    ((ContentHolder) viewHolder).textName.setText(data.getName());
                    //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                    String status = "Idle"; //
                    if (data.getVolumeHandling() == MediaRoute2Info.PLAYBACK_VOLUME_VARIABLE) {
                        status = "Using";
                    }
                    String info = "(" + data.getVolume() + ", " + data.getVolumeMax() + ")" + ", " + data.getConnectionState() + ", " + status/* + ", " + data.getSuitabilityStatus() + "," + data.getType()*/;
                    ((ContentHolder) viewHolder).textInfo.setText(info);
                    //}
                }
            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ContentHolder(getItemLayout(parent.getContext(), R.layout.wallpaperpick_activity_route_adt));
        }

        static class ContentHolder extends RecyclerView.ViewHolder {
            TextView textName, textInfo;

            ContentHolder(View view) {
                super(view);
                textName = view.findViewById(R.id.text_route_name);
                textInfo = view.findViewById(R.id.text_route_info);
            }
        }
    }

}
