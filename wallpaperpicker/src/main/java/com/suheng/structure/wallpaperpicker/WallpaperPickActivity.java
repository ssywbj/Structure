package com.suheng.structure.wallpaperpicker;

import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.graphics.drawable.Drawable;
import android.media.MediaRoute2Info;
import android.media.MediaRouter2;
import android.media.RouteDiscoveryPreference;
import android.os.Build;
import android.os.Bundle;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.suheng.structure.wallpaperpicker.adapter.MediaControllerAdapter;
import com.suheng.structure.wallpaperpicker.adapter.RecyclerAdapter;
import com.suheng.structure.wallpaperpicker.bean.MediaData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WallpaperPickActivity extends AppCompatActivity {

    private String mTag = "WallpaperPickActivity";
    private final List<WallpaperInfo> mWallpaperInfoList = new ArrayList<>();
    private LivePaperAdapter mLivePaperAdapter;
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

        WallpaperInfo wallpaperInfo = wallpaperManager.getWallpaperInfo();
        if (wallpaperInfo == null) {
            Log.w(mTag, "live wallpaper isn't setting !");
        } else {//不为空说明当前系统使用的是动态壁纸
            Log.d(mTag, "current live wallpaper, name: " + wallpaperInfo.getServiceName()
                    + ", package: " + wallpaperInfo.getPackageName()
                    + ", label: " + wallpaperInfo.loadLabel(getPackageManager())
                    + ", setting activity: " + wallpaperInfo.getSettingsActivity());
        }

        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> resolveInfoList = packageManager.queryIntentServices(new Intent(WallpaperService.SERVICE_INTERFACE)
                , PackageManager.GET_META_DATA);
        int size = resolveInfoList.size();
        Log.d(mTag, "wallpaperInfo, size: " + size);
        List<WallpaperInfo> wallpaperInfoList = new ArrayList<>();
        for (ResolveInfo resolveInfo : resolveInfoList) {
            try {
                wallpaperInfoList.add(new WallpaperInfo(this, resolveInfo));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            ResolveInfo resolveInfo = packageManager.resolveService(new Intent("com.wiz.watch.FaceRoamingClock")
                    , PackageManager.GET_META_DATA);
            WallpaperInfo info = new WallpaperInfo(this, resolveInfo);
            Log.d(mTag, "custom resolveInfo: " + resolveInfo + "\nwallpaperInfo: " + info + "\n" +
                    "pkg: " + info.getPackageName() + ", service: " + info.getServiceName()
                    + ", recycle_life: " + info.getServiceInfo().metaData.getBoolean("recycle_life"));
        } catch (Exception e) {
            Log.e(mTag, "parse custom wallpaper info error:" + e.toString());
        }

        String packageName;
        String service;
        Drawable drawable;
        ServiceInfo serviceInfo;
        Bundle bundle;
        List<WallpaperInfo> wallpaperInfos = new ArrayList<>();
        for (WallpaperInfo wallpaper : wallpaperInfoList) {
            packageName = wallpaper.getPackageName();
            service = wallpaper.getServiceName();
            drawable = wallpaper.loadThumbnail(packageManager);
            serviceInfo = wallpaper.getServiceInfo();
            bundle = serviceInfo.metaData;
            Log.d(mTag, "package: " + packageName + ", service: " + service
                    + ", drawable = " + drawable + ", label = " + wallpaper.loadLabel(packageManager)
                    + ", setting activity: " + wallpaper.getSettingsActivity());
            Log.d(mTag, "service info, name: " + serviceInfo.name
                    + ", recycle_life: " + bundle.getBoolean("recycle_life"));

            /*if (drawable == null) {
                drawable = ContextCompat.getDrawable(this, R.drawable.watch_face_preview_default);
            }*/
            if (drawable != null) {
                wallpaperInfos.add(wallpaper);
            }
        }

        mWallpaperInfoList.addAll(wallpaperInfos);

        this.initRecyclerView();

        startService(new Intent(this, WallpaperPickService.class));

        final View.OnClickListener onClickListener = v -> {
            final String pkg = "com.suheng.wallpaper.myhealth";
            String cls;
            if (v.getId() == R.id.btn_set_one) {
                cls = pkg + ".MyHealthWatchFace";
                WallpaperPickService.setLiveWallPaper(v.getContext(), pkg, cls, true);
                finish();
            } else if (v.getId() == R.id.btn_set_two) {
                cls = pkg + ".VapWallpaper";
                WallpaperPickService.setLiveWallPaper(v.getContext(), pkg, cls, true);
                finish();
            }
        };
        findViewById(R.id.btn_set_one).setOnClickListener(onClickListener);
        findViewById(R.id.btn_set_two).setOnClickListener(onClickListener);
        List<MediaData> mediaControllers = MediaDataRepository.getInstance(this).getMediaDataList(null, new OnDataChangedListener() {
            @Override
            public void onDataChanged(MediaData data) {
                final int position = mMediaControllerList.indexOf(data);
                mMediaControllerAdapter.notifyItemChanged(position, data);
            }
        });
        mMediaControllerList.addAll(mediaControllers);
        mMediaControllerAdapter.notifyItemRangeChanged(0, mMediaControllerList.size());
        /*mMediaSessionHelper.addOnActiveSessionsChangedListener(null);
        mMediaSessionHelper.addOnSession2TokensChangedListener();
        mMediaSessionHelper.addOnMediaKeyEventSessionChangedListener(ContextCompat.getMainExecutor(this));*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            this.getWifiList();
        } else {
            Log.w(mTag, "Don't get wifi list because version is lower than R");
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
        Log.d(mTag, "controllers.size(): " + controllers.size() + ", routes.size(): " + routes.size());
        for (MediaRouter2.RoutingController controller : controllers) {
            String controllerId = controller.getId();
            for (MediaRoute2Info route : controller.getSelectedRoutes()) {
                String routeId = route.getId();
                String name = route.getName().toString();
                int type = -1;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    type = route.getType();
                }
                Log.i(mTag, "selectedRoutes, controllerId: " + controllerId
                        + ", routeId: " + routeId + ", name: " + name + ", type: " + type);
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
                Log.d(mTag, "selectableRoutes, controllerId: " + controllerId
                        + ", routeId: " + routeId + ", name: " + name + ", type: " + type);
            }

            for (MediaRoute2Info route : controller.getDeselectableRoutes()) {
                String routeId = route.getId();
                String name = route.getName().toString();
                int type = -1;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    type = route.getType();
                }
                Log.d(mTag, "deselectableRoutes, controllerId: " + controllerId
                        + ", routeId: " + routeId + ", name: " + name + ", type: " + type);
            }
        }

        MediaRouter2.RouteCallback routeCallback = new MediaRouter2.RouteCallback() {
            @Override
            public void onRoutesUpdated(@NonNull List<MediaRoute2Info> routes) {
                super.onRoutesUpdated(routes);
                Log.i(mTag, "onRoutesUpdated, routes.size: " + routes.size()
                        + ", mediaRouter2.routes.size: " + mMediaRouter2.getRoutes().size()
                        + ", thread: " + Thread.currentThread().getName());
                mMediaRoute2InfoList.clear();
                mMediaRoute2InfoList.addAll(routes);
                for (MediaRoute2Info route : routes) {
                    Log.d(mTag, "onRoutesUpdated, route HashCode: " + System.identityHashCode(route)
                            + ", id " + route.getId());
                }
                mMediaRouteAdapter.notifyItemRangeChanged(0, mMediaRoute2InfoList.size());
            }
        };
        mMediaRouter2.registerRouteCallback(getMainExecutor(), routeCallback, discoveryPreference);
        //mMediaRouter2.unregisterRouteCallback(routeCallback);

        mMediaRouter2.registerTransferCallback(getMainExecutor(), new MediaRouter2.TransferCallback() {
            @Override
            public void onTransfer(@NonNull MediaRouter2.RoutingController oldController
                    , @NonNull MediaRouter2.RoutingController newController) {
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
        mLivePaperAdapter = new LivePaperAdapter(mWallpaperInfoList);
        mLivePaperAdapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener<WallpaperInfo>() {
            @Override
            public void onItemClick(View view, final WallpaperInfo data, int position) {
                Intent intent = new Intent("com.wiz.watch.action.PICK_WALLPAPER");
                intent.putExtra("wallpaper_info", data);
                sendBroadcast(intent);

                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 100);

            }
        });

        //https://blog.csdn.net/u010687392/article/details/47950199?utm_medium=distribute.pc_relevant.none-task-blog-baidujs-2
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mLivePaperAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(mCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        RecyclerView rvRouteList = findViewById(R.id.recycler_route_list);
        mMediaRouteAdapter = new MediaRouteAdapter(mMediaRoute2InfoList);
        mMediaRouteAdapter.setOnItemClickListener((view, data, position) -> {
            if (mMediaRouter2 != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (data.getVolumeHandling() == MediaRoute2Info.PLAYBACK_VOLUME_VARIABLE) {
                        return;
                    }
                    Log.d(mTag, "transferTo, route: " + data);
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
        Log.d(mTag, "onDestroy()");
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
        Log.d(mTag, "onBackPressed()");
    }

    ItemTouchHelper.Callback mCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP, ItemTouchHelper.UP) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder
                , @NonNull RecyclerView.ViewHolder target) {
            /*int fromPst = viewHolder.getAdapterPosition();//得到拖动ViewHolder的position
            int toPosition = target.getAdapterPosition();//得到目标ViewHolder的position
            if (fromPst < toPosition) {
                //分别把中间所有的item的位置重新交换
                for (int i = fromPst; i < toPosition; i++) {
                    Collections.swap(mWallpaperInfoList, i, i + 1);
                }
            } else {
                for (int i = fromPst; i > toPosition; i--) {
                    Collections.swap(mWallpaperInfoList, i, i - 1);
                }
            }
            mLivePaperAdapter.notifyItemMoved(fromPst, toPosition);*/

            int fromPst = viewHolder.getAdapterPosition();
            mWallpaperInfoList.remove(mWallpaperInfoList.get(fromPst));
            mLivePaperAdapter.notifyDataSetChanged();
            Log.d(mTag, "onMove, from pst: " + fromPst);
            return true;//true表示执行拖动
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int pst = viewHolder.getAdapterPosition();
            WallpaperInfo wallpaper = mWallpaperInfoList.get(pst);

            /*try {
                deleteLiveWallPaper(wallpaper.getPackageName());

                mWallpaperInfoList.remove(wallpaper);
                mLivePaperAdapter.notifyDataSetChanged();
                Toast.makeText(WallpaperPickActivity.this, "表盘删除成功", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e(mTag, "delete live wallpaper fail", e);
                Toast.makeText(WallpaperPickActivity.this, "表盘删除失败", Toast.LENGTH_SHORT).show();
            }*/

            /*try {
                execCommand("pm", "uninstall", wallpaper.getPackageName());

                mWallpaperInfoList.remove(wallpaper);
                mLivePaperAdapter.notifyDataSetChanged();
                Toast.makeText(WallpaperPickActivity.this, "表盘删除成功", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e(mTag, "delete live wallpaper fail", e);
                Toast.makeText(WallpaperPickActivity.this, "表盘删除失败", Toast.LENGTH_SHORT).show();
            }*/

            /*Uri uri = Uri.fromParts("package", wallpaper.getPackageName(), null);
            Intent intent = new Intent(Intent.ACTION_DELETE, uri);
            startActivity(intent);
            mWallpaperInfoList.remove(wallpaper);
            mLivePaperAdapter.notifyDataSetChanged();*/

            mWallpaperInfoList.remove(wallpaper);
            mLivePaperAdapter.notifyDataSetChanged();
            Log.d(mTag, "onSwiped");
            if (mWallpaperInfoList.size() == 0) {
                Toast.makeText(WallpaperPickActivity.this, "表盘列表为空", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private final class LivePaperAdapter extends RecyclerAdapter<WallpaperInfo, RecyclerView.ViewHolder> {

        LivePaperAdapter(List<WallpaperInfo> dataList) {
            super(dataList);
        }

        @Override
        protected void bindView(RecyclerView.ViewHolder viewHolder, final int position, final WallpaperInfo data) {
            if (viewHolder instanceof ContentHolder) {
                ContentHolder contentHolder = (ContentHolder) viewHolder;
                contentHolder.textName.setText(data.loadLabel(getPackageManager()));
                contentHolder.imageThumb.setImageDrawable(data.loadThumbnail(getPackageManager()));

                final String settingsActivity = data.getSettingsActivity();
                if (settingsActivity == null) {
                    contentHolder.textSetting.setVisibility(View.GONE);
                    contentHolder.textSetting.setOnClickListener(null);
                } else {
                    contentHolder.textSetting.setVisibility(View.VISIBLE);
                    contentHolder.textSetting.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                Intent intent = new Intent();
                                intent.setClassName(data.getPackageName(), settingsActivity);
                                startActivity(intent);
                            } catch (Exception e) {
                                Log.e(mTag, "open " + settingsActivity + " fail", e);
                            }
                        }
                    });
                }
            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ContentHolder(getItemLayout(parent.getContext(), R.layout.wallpaperpick_activity_wallpaper_pick_adt));
        }

        class ContentHolder extends RecyclerView.ViewHolder {
            TextView textName, textSetting;
            ImageView imageThumb;

            ContentHolder(View view) {
                super(view);
                textName = view.findViewById(R.id.text_wallpaper_name);
                imageThumb = view.findViewById(R.id.image_wallpaper_thumb);
                textSetting = view.findViewById(R.id.text_wallpaper_setting);
            }
        }
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
                    String info = "(" + data.getVolume()
                            + ", " + data.getVolumeMax() + ")" + ", " + data.getConnectionState()
                            + ", " + status/* + ", " + data.getSuitabilityStatus() + "," + data.getType()*/;
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
