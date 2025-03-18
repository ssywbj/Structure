package com.suheng.structure.wallpaperpicker;

import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadata;
import android.media.Session2Token;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.os.Build;
import android.os.Bundle;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.suheng.structure.wallpaperpicker.adapter.RecyclerAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WallpaperPickActivity extends AppCompatActivity {

    private String mTag = "Wbj";
    private List<WallpaperInfo> mWallpaperInfoList = new ArrayList<>();
    private LivePaperAdapter mLivePaperAdapter;

    private final Set<MediaController> mMediaControllerSet = new HashSet<>();
    private Button mBtnState;

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

            if (v.getId() == R.id.btn_previous) {
                for (MediaController mediaController : mMediaControllerSet) {
                    mediaController.getTransportControls().skipToPrevious();
                }
            } else if (v.getId() == R.id.btn_state) {
                for (MediaController mediaController : mMediaControllerSet) {
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
                for (MediaController mediaController : mMediaControllerSet) {
                    mediaController.getTransportControls().skipToNext();
                }
            }
        };
        findViewById(R.id.btn_set_one).setOnClickListener(onClickListener);
        findViewById(R.id.btn_set_two).setOnClickListener(onClickListener);
        mBtnState = findViewById(R.id.btn_state);
        mBtnState.setOnClickListener(onClickListener);
        findViewById(R.id.btn_previous).setOnClickListener(onClickListener);
        findViewById(R.id.btn_next).setOnClickListener(onClickListener);

        MediaSessionManager msManager = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);
        msManager.addOnActiveSessionsChangedListener(new MediaSessionManager.OnActiveSessionsChangedListener() {
            @Override
            public void onActiveSessionsChanged(@Nullable List<MediaController> list) {
                Log.i(mTag, "onActiveSessionsChanged, mediaControllers: " + list);
                if (list != null) {
                    for (MediaController mediaController : list) {
                        StringBuilder logStr = buildMediaController(mediaController);
                        Log.i(mTag, "onActiveSessionsChanged, " + logStr);
                    }
                }
            }
        }, null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            msManager.addOnSession2TokensChangedListener(new MediaSessionManager.OnSession2TokensChangedListener() {
                @Override
                public void onSession2TokensChanged(@NonNull List<Session2Token> list) {
                    Log.i(mTag, "onSession2TokensChanged, list: " + list);
                }
            });
        }
        List<MediaController> mediaControllers = msManager.getActiveSessions(null);
        Log.d(mTag, "getActiveSessions, mediaControllers: " + mediaControllers.size());
        for (MediaController mediaController : mediaControllers) {
            StringBuilder logStr = buildMediaController(mediaController);
            Log.d(mTag, "getActiveSessions, " + logStr);
        }
    }

    @NonNull
    private StringBuilder buildMediaController(@NonNull MediaController mediaController) {
        mMediaControllerSet.add(mediaController);

        String pkg = mediaController.getPackageName();
        StringBuilder logStr = new StringBuilder("pkg: " + pkg);

        final MediaMetadata metadata = mediaController.getMetadata();
        if (metadata != null) {
            String mediaMetadata = parseMediaMetadata(metadata);
            logStr.append(", ").append(mediaMetadata);
        }

        final PlaybackState playbackState = mediaController.getPlaybackState();
        if (playbackState != null) {
            String pState = parsePlaybackState(playbackState);
            logStr.append(", ").append(pState);
        }

        final MediaController.Callback callback = new MediaController.Callback() {
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
        };
        mediaController.registerCallback(callback);

        return logStr;
    }

    @NonNull
    private String parsePlaybackState(@NonNull PlaybackState playbackState) {
        final String playbackStateStr = playbackState.toString();
        final String stateFlag = "state=";
        final int startIndex = playbackStateStr.indexOf(stateFlag);
        final int endIndex = playbackStateStr.indexOf(")");
        final String state = playbackStateStr.substring(startIndex + stateFlag.length(), endIndex + 1);
        mBtnState.setText(state);
        return stateFlag + state;
    }

    @NonNull
    private String parseMediaMetadata(@NonNull MediaMetadata metadata) {
        StringBuilder info = new StringBuilder();
        CharSequence text = metadata.getText(MediaMetadata.METADATA_KEY_TITLE);
        info.append("title: ").append(text);
        CharSequence artist = metadata.getText(MediaMetadata.METADATA_KEY_ARTIST);
        info.append(", artist: ").append(artist);
        long duration = metadata.getLong(MediaMetadata.METADATA_KEY_DURATION);
        info.append(", duration: ").append(duration);
        Bitmap bitmap = metadata.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART);
        info.append(", bitmap: ").append(System.identityHashCode(bitmap));
        return info.toString();
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(mTag, "onDestroy()");
        mWallpaperInfoList.clear();
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

}
