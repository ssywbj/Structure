package com.suheng.structure.wallpaperpicker;

import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadata;
import android.media.MediaRoute2Info;
import android.media.MediaRouter2;
import android.media.RouteDiscoveryPreference;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.suheng.structure.wallpaperpicker.adapter.RecyclerAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class WallpaperPickActivity extends AppCompatActivity {

    private String mTag = "WallpaperPickActivity";
    private static final long UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1);
    private final List<WallpaperInfo> mWallpaperInfoList = new ArrayList<>();
    private LivePaperAdapter mLivePaperAdapter;
    private MediaRouteAdapter mMediaRouteAdapter;
    private MediaControllerAdapter mMediaControllerAdapter;
    private final List<MediaRoute2Info> mMediaRoute2InfoList = new ArrayList<>();

    private final List<MediaController> mMediaControllerList = new ArrayList<>();
    //private final Map<MediaController, MediaController.Callback> mMediaCallbackMap = new HashMap<>();

    private final Handler mHandler = new Handler(Looper.getMainLooper());
    /*private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            for (Map.Entry<MediaController, MediaController.Callback> mediaControllerCallbackEntry : mMediaCallbackMap.entrySet()) {
                MediaController.Callback callback = mediaControllerCallbackEntry.getValue();
                if (callback instanceof MediaUpdateListener) {
                    MediaController mediaController = mediaControllerCallbackEntry.getKey();
                    PlaybackState playbackState = mediaController.getPlaybackState();
                    if (playbackState != null) {
                        long position = playbackState.getPosition();
                        ((MediaUpdateListener) callback).onProgressUpdate(position, "", "");
                    }
                }
            }
            long delayMillis = UPDATE_RATE_MS - (System.currentTimeMillis() % UPDATE_RATE_MS);
            mHandler.postDelayed(mRunnable, delayMillis);
        }
    };*/

    private final MediaSessionManager.OnActiveSessionsChangedListener mSessionListener = controllers -> {
        if (controllers == null) {
            Log.w(mTag, "onActiveSessionsChanged, controllers object is null");
        } else {
            Log.i(mTag, "onActiveSessionsChanged, controllers size is " + controllers.size());
            mMediaControllerList.clear();
            mMediaControllerList.addAll(controllers);
            mMediaControllerAdapter.notifyItemRangeChanged(0, mMediaControllerList.size());
            /*for (MediaController mediaController : controllers) {
                StringBuilder logStr = buildMediaController(mediaController);
                Log.i(mTag, "onActiveSessionsChanged, " + logStr);
            }*/
        }
    };

    @Nullable
    private MediaSessionManager.OnSession2TokensChangedListener mOnTokensChangedListener;
    @Nullable
    private MediaSessionManager.OnMediaKeyEventSessionChangedListener mOnKeyEventChangedListener;

    /*@Nullable
    private ComponentName mComponentNotification;*/
    private @Nullable MediaRouter2 mMediaRouter2;
    private MediaSessionManager mSessionManager;

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

            /*if (v.getId() == R.id.btn_previous) {
                for (MediaController mediaController : mMediaControllerList) {
                    mediaController.getTransportControls().skipToPrevious();
                }
            } else if (v.getId() == R.id.btn_state) {
                for (MediaController mediaController : mMediaControllerList) {
                    PlaybackState playbackState = mediaController.getPlaybackState();
                    if (playbackState != null) {
                        MediaController.TransportControls transportControls = mediaController.getTransportControls();
                        if (playbackState.getState() == PlaybackState.STATE_PLAYING) {
                            transportControls.pause();
                        } else if (playbackState.getState() == PlaybackState.STATE_PAUSED
                                || playbackState.getState() == PlaybackState.STATE_NONE) {
                            transportControls.play();
                        } else {
                            Log.w(mTag, "Neither in play state nor in pause/none state");
                        }
                    }
                }
            } else if (v.getId() == R.id.btn_next) {
                for (MediaController mediaController : mMediaControllerList) {
                    mediaController.getTransportControls().skipToNext();
                }
            }*/
        };
        findViewById(R.id.btn_set_one).setOnClickListener(onClickListener);
        findViewById(R.id.btn_set_two).setOnClickListener(onClickListener);
        /*mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //Log.d(mTag, "onProgressChanged, progress: " + progress + ", fromUser: " + fromUser);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.i(mTag, "onStartTrackingTouch");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                removeProgressMsg();
                final int progress = mSeekBar.getProgress();
                final int max = mSeekBar.getMax();
                Object tag = mTvDuration.getTag();
                long duration = 0;
                if (tag instanceof Long) {
                    duration = (Long) tag;
                }
                if (duration <= 0) {
                    Log.e(mTag, "onStopTrackingTouch, error duration");
                    return;
                }
                final long position = (long) (1.0 * progress / max * duration);
                String positionFormat = Utils.formatDuration(position);
                int mediaControllerSize = mMediaControllerSet.size();
                Log.i(mTag, "onStopTrackingTouch, mediaControllerSize:" + mediaControllerSize);
                for (MediaController mediaController : mMediaControllerSet) {
                    Log.i(mTag, "onStopTrackingTouch, progress:" + progress + ", max: " + max
                            + ", position: " + position+ "(" + positionFormat + ")" + ", duration: " + duration
                            + ", mediaController: " + mediaController);
                    mediaController.getTransportControls().seekTo(position);
                }
            }
        });*/

        mSessionManager = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);
        mSessionManager.addOnActiveSessionsChangedListener(mSessionListener, null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (mOnTokensChangedListener == null) {
                mOnTokensChangedListener = session2Tokens -> Log.i(mTag, "onSession2TokensChanged, session2Tokens: " + session2Tokens);
                mSessionManager.addOnSession2TokensChangedListener(mOnTokensChangedListener);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (mOnKeyEventChangedListener == null) {
                mOnKeyEventChangedListener = (pkg, sessionToken) -> Log.i(mTag, "onMediaKeyEventSessionChanged, pkg: " + pkg + ", sessionToken: " + sessionToken);
                mSessionManager.addOnMediaKeyEventSessionChangedListener(ContextCompat.getMainExecutor(this), mOnKeyEventChangedListener);
            }
        }
        List<MediaController> mediaControllers = mSessionManager.getActiveSessions(null);
        Log.d(mTag, "getActiveSessions, mediaControllers: " + mediaControllers.size());
        mMediaControllerList.addAll(mediaControllers);
        mMediaControllerAdapter.notifyItemRangeChanged(0, mMediaControllerList.size());
        /*for (MediaController mediaController : mediaControllers) {
            StringBuilder logStr = buildMediaController(mediaController);
            Log.d(mTag, "getActiveSessions, " + logStr);
        }*/

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

    @NonNull
    private StringBuilder buildMediaController(@NonNull MediaController mediaController) {
        String pkg = mediaController.getPackageName();
        StringBuilder logStr = new StringBuilder("AppInfo->pkg: " + pkg);

        ApplicationInfo applicationInfo = null;
        PackageManager packageManager = getPackageManager();
        try {
            applicationInfo = packageManager.getApplicationInfo(pkg, 0);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(mTag, "getApplicationInfo error", e);
        }
        if (applicationInfo != null) {
            Drawable icon = applicationInfo.loadIcon(packageManager);
            logStr.append(", icon: ").append(System.identityHashCode(icon));
            CharSequence label = applicationInfo.loadLabel(packageManager);
            logStr.append(", label: ").append(label);
        }

        final MediaMetadata metadata = mediaController.getMetadata();
        if (metadata != null) {
            String mediaMetadata = parseMediaMetadata(metadata);
            logStr.append(", Metadata->").append(mediaMetadata);
        }

        final PlaybackState playbackState = mediaController.getPlaybackState();
        if (playbackState != null) {
            String pState = parsePlaybackState(playbackState);
            logStr.append(", PlaybackState->").append(pState);
        }

        final MediaController.Callback callback = new MediaUpdateListener() {
            @Override
            public void onSessionDestroyed() {
                super.onSessionDestroyed();
                Log.i(mTag, "onSessionDestroyed");
            }

            @Override
            public void onSessionEvent(@NonNull String event, @Nullable Bundle extras) {
                super.onSessionEvent(event, extras);
                Log.d(mTag, "onSessionEvent, event: " + event);
            }

            @Override
            public void onPlaybackStateChanged(@Nullable PlaybackState state) {
                super.onPlaybackStateChanged(state);
                Log.i(mTag, "onPlaybackStateChanged, state: " + state);
                if (state != null) {
                    String playbackState = parsePlaybackState(state);
                    //Log.i(mTag, "onPlaybackStateChanged, playbackState: " + playbackState);
                }
            }

            @Override
            public void onMetadataChanged(@Nullable MediaMetadata metadata) {
                super.onMetadataChanged(metadata);
                String mediaMetadata = null;
                if (metadata != null) {
                    mediaMetadata = parseMediaMetadata(metadata);
                }
                Log.i(mTag, "onMetadataChanged, metadata: " + System.identityHashCode(metadata)
                        + " {" + mediaMetadata + "}");
            }

            @Override
            public void onQueueChanged(@Nullable List<MediaSession.QueueItem> queue) {
                super.onQueueChanged(queue);
                Log.d(mTag, "onQueueChanged, queue: " + queue);
            }

            @Override
            public void onQueueTitleChanged(@Nullable CharSequence title) {
                super.onQueueTitleChanged(title);
                Log.d(mTag, "onQueueTitleChanged, title: " + title);
            }

            @Override
            public void onExtrasChanged(@Nullable Bundle extras) {
                super.onExtrasChanged(extras);
                Log.d(mTag, "onExtrasChanged, extras: " + extras);
            }

            @Override
            public void onAudioInfoChanged(MediaController.PlaybackInfo info) {
                super.onAudioInfoChanged(info);
                Log.d(mTag, "onAudioInfoChanged, info: " + info);
            }

            @Override
            public void onProgressUpdate(long newPst, String oldPst, String duration) {
                String fDuration = Utils.formatDuration(newPst);
                String text = fDuration + "(" + newPst + ")";
                Log.d(mTag, "newPst: " + text + ", thread: " + Thread.currentThread().getName());
                //mTvPst.setText(text);
                //mSeekBar.setProgress((int) (newPst / 1000));
            }
        };
        mediaController.registerCallback(callback, mHandler);
        //mMediaCallbackMap.put(mediaController, callback);

        return logStr;
    }

    @NonNull
    private String parsePlaybackState(@NonNull PlaybackState playbackState) {
        //Log.d(mTag, "parsePlaybackState, playbackState: " + playbackState);
        final String playbackStateStr = playbackState.toString();
        final String stateFlag = "state=";
        final int startIndex = playbackStateStr.indexOf(stateFlag);
        final int endIndex = playbackStateStr.indexOf(")");
        final String state = playbackStateStr.substring(startIndex + stateFlag.length(), endIndex + 1);
        //mBtnState.setText(state);
        long position = playbackState.getPosition();
        String fDuration = Utils.formatDuration(position);
        //mTvPst.setText(fDuration + "(" + position + ")");
        //mSeekBar.setProgress((int) (position / 1000));

        if (playbackState.getState() == PlaybackState.STATE_PLAYING) {
            removeProgressMsg();
            long delayMillis = UPDATE_RATE_MS - (System.currentTimeMillis() % UPDATE_RATE_MS);
            //mHandler.postDelayed(mRunnable, delayMillis);
        } else {
            removeProgressMsg();
        }
        return stateFlag + state;
    }

    private void removeProgressMsg() {
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (mHandler.hasCallbacks(mRunnable)) {
                mHandler.removeCallbacks(mRunnable);
            }
        } else {
            mHandler.removeCallbacks(mRunnable);
        }*/
    }

    @NonNull
    private String parseMediaMetadata(@NonNull MediaMetadata metadata) {
        StringBuilder info = new StringBuilder();
        CharSequence text = metadata.getText(MediaMetadata.METADATA_KEY_TITLE);
        info.append("title: ").append(text);
        //mTvTitle.setText(text);
        CharSequence artist = metadata.getText(MediaMetadata.METADATA_KEY_ARTIST);
        info.append(", artist: ").append(artist);
        //mTvArtist.setText(artist);
        long duration = metadata.getLong(MediaMetadata.METADATA_KEY_DURATION);
        info.append(", duration: ").append(duration);
        String fDuration = Utils.formatDuration(duration);
        //mTvDuration.setText(fDuration + "(" + duration + ")");
        //mTvDuration.setTag(duration);
        //mSeekBar.setMax((int) (duration / 1000));
        Bitmap bitmap = metadata.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART);
        info.append(", bitmap: ").append(System.identityHashCode(bitmap));
        //mIvAlbumArt.setImageBitmap(bitmap);
        return info.toString();
    }

    private static class MediaUpdateListener extends MediaController.Callback {
        public void onProgressUpdate(long newPst, String oldPst, String duration) {
        }
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
        removeProgressMsg();
        mSessionManager.removeOnActiveSessionsChangedListener(mSessionListener);
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

    final class MediaControllerAdapter extends RecyclerAdapter<MediaController, RecyclerView.ViewHolder> {

        MediaControllerAdapter(List<MediaController> dataList) {
            super(dataList);
        }

        @Override
        protected void bindView(RecyclerView.ViewHolder viewHolder, final int position, final MediaController data) {
            if (viewHolder instanceof ContentHolder) {
                ContentHolder holder = (ContentHolder) viewHolder;
                PlaybackState playbackState = data.getPlaybackState();
                if (playbackState == null) {
                    return;
                }
                final String playbackStateStr = playbackState.toString();
                final String stateFlag = "state=";
                final int startIndex = playbackStateStr.indexOf(stateFlag);
                final int endIndex = playbackStateStr.indexOf(")");
                final String state = playbackStateStr.substring(startIndex + stateFlag.length(), endIndex + 1);
                holder.mBtnState.setText(state);
                long pst = playbackState.getPosition();
                String fPst = Utils.formatDuration(pst);
                holder.mTvPst.setText(fPst + "(" + pst + ")");
                holder.mSeekBar.setProgress((int) (pst / 1000));
                long actions = playbackState.getActions();
                List<PlaybackState.CustomAction> customActions = playbackState.getCustomActions();
                for (PlaybackState.CustomAction customAction : customActions) {
                    CharSequence name = customAction.getName();
                    int icon = customAction.getIcon();
                    String action = customAction.getAction();
                    Log.d(mTag, "customAction, name: " + name + ", icon: " + icon
                            + ", action: " + action+ ", actions: " + actions);
                }

                MediaMetadata metadata = data.getMetadata();
                if (metadata == null) {
                    return;
                }
                CharSequence text = metadata.getText(MediaMetadata.METADATA_KEY_TITLE);
                holder.mTvTitle.setText(text);
                CharSequence artist = metadata.getText(MediaMetadata.METADATA_KEY_ARTIST);
                holder.mTvArtist.setText(artist);
                long duration = metadata.getLong(MediaMetadata.METADATA_KEY_DURATION);
                String fDuration = Utils.formatDuration(duration);
                holder.mTvDuration.setText(fDuration + "(" + duration + ")");
                holder.mTvDuration.setTag(duration);
                holder.mSeekBar.setMax((int) (duration / 1000));
                Bitmap bitmap = metadata.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART);
                holder.mIvAlbumArt.setImageBitmap(bitmap);

                final MediaController.Callback callback = new MediaUpdateListener() {
                    @Override
                    public void onSessionDestroyed() {
                        super.onSessionDestroyed();
                        Log.i(mTag, "onSessionDestroyed");
                    }

                    @Override
                    public void onSessionEvent(@NonNull String event, @Nullable Bundle extras) {
                        super.onSessionEvent(event, extras);
                        Log.d(mTag, "onSessionEvent, event: " + event);
                    }

                    @Override
                    public void onPlaybackStateChanged(@Nullable PlaybackState state) {
                        super.onPlaybackStateChanged(state);
                        Log.i(mTag, "onPlaybackStateChanged, state: " + state);
                        if (state != null) {
                            notifyItemRangeChanged(0, mMediaControllerList.size());
                            //String playbackState = parsePlaybackState(state);
                            //Log.i(mTag, "onPlaybackStateChanged, playbackState: " + playbackState);
                        }
                    }

                    @Override
                    public void onMetadataChanged(@Nullable MediaMetadata metadata) {
                        super.onMetadataChanged(metadata);
                        /*String mediaMetadata = null;
                        if (metadata != null) {
                            mediaMetadata = parseMediaMetadata(metadata);
                        }
                        Log.i(mTag, "onMetadataChanged, metadata: " + System.identityHashCode(metadata)
                                + " {" + mediaMetadata + "}");*/
                        notifyItemRangeChanged(0, mMediaControllerList.size());
                    }

                    @Override
                    public void onQueueChanged(@Nullable List<MediaSession.QueueItem> queue) {
                        super.onQueueChanged(queue);
                        Log.d(mTag, "onQueueChanged, queue: " + queue);
                    }

                    @Override
                    public void onQueueTitleChanged(@Nullable CharSequence title) {
                        super.onQueueTitleChanged(title);
                        Log.d(mTag, "onQueueTitleChanged, title: " + title);
                    }

                    @Override
                    public void onExtrasChanged(@Nullable Bundle extras) {
                        super.onExtrasChanged(extras);
                        Log.d(mTag, "onExtrasChanged, extras: " + extras);
                    }

                    @Override
                    public void onAudioInfoChanged(MediaController.PlaybackInfo info) {
                        super.onAudioInfoChanged(info);
                        Log.d(mTag, "onAudioInfoChanged, info: " + info);
                    }

                    @Override
                    public void onProgressUpdate(long newPst, String oldPst, String duration) {
                        String fDuration = Utils.formatDuration(newPst);
                        String text = fDuration + "(" + newPst + ")";
                        Log.d(mTag, "newPst: " + text + ", thread: " + Thread.currentThread().getName());
                        //mTvPst.setText(text);
                        //mSeekBar.setProgress((int) (newPst / 1000));
                    }
                };
                data.registerCallback(callback, mHandler);
            }
        }

        public Drawable getIconFromPackage(Context context, String packageName, int resId) {
            try {
                PackageManager packageManager = context.getPackageManager();
                ApplicationInfo appInfo = packageManager.getApplicationInfo(packageName, 0);
                Resources resources = packageManager.getResourcesForApplication(appInfo);
                return resources.getDrawable(resId, context.getTheme());
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(mTag, "Package not found: " + packageName, e);
            } catch (Resources.NotFoundException e) {
                Log.e(mTag, "Resource ID not found: " + resId, e);
            } catch (Exception e) {
                Log.e(mTag, "Unexpected error occurred", e);
            }
            return null;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ContentHolder(getItemLayout(parent.getContext(), R.layout.wallpaperpick_activity_media_controller_adt));
        }

        class ContentHolder extends RecyclerView.ViewHolder {
            Button mBtnState;
            TextView mTvPst;
            TextView mTvDuration;
            TextView mTvTitle;
            TextView mTvArtist;
            ImageView mIvAlbumArt;
            SeekBar mSeekBar;

            ContentHolder(View view) {
                super(view);
                mBtnState = view.findViewById(R.id.btn_state);
                //mBtnState.setOnClickListener(onClickListener);
                //findViewById(R.id.btn_previous).setOnClickListener(onClickListener);
                //findViewById(R.id.btn_next).setOnClickListener(onClickListener);
                mTvPst = view.findViewById(R.id.tv_pst);
                mTvDuration = view.findViewById(R.id.tv_duration);
                mTvTitle = view.findViewById(R.id.tv_title);
                mTvArtist = view.findViewById(R.id.tv_artist);
                mIvAlbumArt = view.findViewById(R.id.tv_album_art);
                mSeekBar = view.findViewById(R.id.seekBar);
            }
        }
    }

}
