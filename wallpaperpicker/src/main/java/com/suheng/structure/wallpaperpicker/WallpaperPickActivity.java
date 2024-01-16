package com.suheng.structure.wallpaperpicker;

import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.suheng.structure.wallpaperpicker.adapter.RecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

public class WallpaperPickActivity extends AppCompatActivity {

    private String mTag = "Wbj";
    private List<WallpaperInfo> mWallpaperInfoList = new ArrayList<>();
    private LivePaperAdapter mLivePaperAdapter;

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
            } else {
                cls = pkg + ".VapWallpaper";
            }
            WallpaperPickService.setLiveWallPaper(v.getContext(), pkg, cls, true);
            finish();
        };
        findViewById(R.id.btn_set_one).setOnClickListener(onClickListener);
        findViewById(R.id.btn_set_two).setOnClickListener(onClickListener);
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
