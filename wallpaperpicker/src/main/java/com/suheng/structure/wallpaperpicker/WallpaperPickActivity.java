package com.suheng.structure.wallpaperpicker;

import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
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

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager == null) {
            Log.w(mTag, "SensorManager is null !");
        } else {
            List<Sensor> list = sensorManager.getSensorList(Sensor.TYPE_ALL);
            for (Sensor sensor : list) {
                Log.d(mTag, "sensor: " + sensor + ", name: " + sensor.getName());
            }

            Sensor stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            if (stepCounterSensor == null) {
                Log.w(mTag, "StepCounterSensor is null !");
            } else {
                sensorManager.registerListener(null, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);
            }
        }

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

        String packageName;
        String service;
        Drawable drawable;
        List<WallpaperInfo> wallpaperInfos = new ArrayList<>();
        for (WallpaperInfo wallpaper : wallpaperInfoList) {
            packageName = wallpaper.getPackageName();
            service = wallpaper.getServiceName();
            drawable = wallpaper.loadThumbnail(packageManager);
            Log.d(mTag, "package: " + packageName + ", service: " + service
                    + ", drawable = " + drawable + ", label = " + wallpaper.loadLabel(packageManager)
                    + ", setting activity: " + wallpaper.getSettingsActivity());

            if (drawable != null) {
                wallpaperInfos.add(wallpaper);
            }
        }

        mWallpaperInfoList.addAll(wallpaperInfos);

        this.initRecyclerView();
    }

    private void initRecyclerView() {
        mLivePaperAdapter = new LivePaperAdapter(mWallpaperInfoList);
        mLivePaperAdapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener<WallpaperInfo>() {
            @Override
            public void onItemClick(View view, WallpaperInfo data, int position) {
                setLiveWallPaper(data.getPackageName(), data.getServiceName());
            }
        });
        //https://blog.csdn.net/u010687392/article/details/47950199?utm_medium=distribute.pc_relevant.none-task-blog-baidujs-2
        RecyclerView recyclerView = findViewById(R.id.recycler_view_wallpaper);
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

    /**
     * 设置动态壁纸（需是系统级应用和"android.permission.SET_WALLPAPER_COMPONENT"权限）
     *
     * @param packageName 壁纸所包名
     * @param service     壁纸服务类，格式：包.类名称，如com.xxx.yyy.zzzz.XxxWallpaperService
     */
    public void setLiveWallPaper(String packageName, String service) {
        try {
            Log.d(mTag, "pkg: " + packageName + ", service: " + service);
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
            if ("com.wiz.watch.facesimplepointer".equals(packageName)) {
                wallpaperManager.setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.wallpaper_default));
            }
            Method method = WallpaperManager.class.getMethod("setWallpaperComponent", ComponentName.class);
            method.invoke(wallpaperManager, new ComponentName(packageName, service));
            Toast.makeText(this, "表盘设置成功", Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            Log.e(mTag, "set live wallpaper fail", e);
            Toast.makeText(this, "表盘设置失败", Toast.LENGTH_SHORT).show();
        }
    }

    //deletePackage(@NonNull String packageName, @Nullable IPackageDeleteObserver observer, @DeleteFlags int flags);
    public void deleteLiveWallPaper(String packageName) throws Exception {
        Method method = PackageManager.class.getMethod("deletePackage", String.class, Object.class, int.class);
        method.invoke(getPackageManager(), packageName, null, 0);
    }

    private String execCommand(String... command) {
        String result;
        try {
            Process process = new ProcessBuilder().command(command).start();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int read;
            InputStream inputStream = process.getErrorStream();
            while ((read = inputStream.read()) != -1) {
                byteArrayOutputStream.write(read);
            }
            InputStream processInputStream = process.getInputStream();
            while ((read = processInputStream.read()) != -1) {
                byteArrayOutputStream.write(read);
            }
            result = new String(byteArrayOutputStream.toByteArray());
            processInputStream.close();
            inputStream.close();
            process.destroy();
        } catch (IOException e) {
            result = e.getMessage();
        }
        return result;
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

    private final class LivePaperAdapter extends RecyclerAdapter<WallpaperInfo> {

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
