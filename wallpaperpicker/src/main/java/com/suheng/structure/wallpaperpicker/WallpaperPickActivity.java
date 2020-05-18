package com.suheng.structure.wallpaperpicker;

import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.suheng.structure.wallpaperpicker.adapter.RecyclerAdapter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class WallpaperPickActivity extends AppCompatActivity {

    private String mTag = "Wbj";
    private List<WallpaperInfo> mWallpaperInfoList = new ArrayList<>();

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
            wallpaperManager.setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.bg));
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
        /*mWallpaperInfoList.addAll(wallpaperInfos);
        mWallpaperInfoList.addAll(wallpaperInfos);
        mWallpaperInfoList.addAll(wallpaperInfos);
        mWallpaperInfoList.addAll(wallpaperInfos);
        mWallpaperInfoList.addAll(wallpaperInfos);
        mWallpaperInfoList.addAll(wallpaperInfos);
        mWallpaperInfoList.addAll(wallpaperInfos);
        mWallpaperInfoList.addAll(wallpaperInfos);*/

        this.initRecyclerView();
    }

    private void initRecyclerView() {
        LivePaperAdapter livePaperAdapter = new LivePaperAdapter(mWallpaperInfoList);
        livePaperAdapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener<WallpaperInfo>() {
            @Override
            public void onItemClick(View view, WallpaperInfo data, int position) {
                setLiveWallPaper(data.getPackageName(), data.getServiceName());
            }
        });
        RecyclerView recyclerView = findViewById(R.id.recycler_view_wallpaper);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(livePaperAdapter);
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
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
            Method method = WallpaperManager.class.getMethod("getIWallpaperManager");
            Object invoke = method.invoke(wallpaperManager);
            Class[] param = new Class[1];
            param[0] = ComponentName.class;
            method = invoke.getClass().getMethod("setWallpaperComponent", param);

            Intent intent = new Intent(WallpaperService.SERVICE_INTERFACE);
            intent.setClassName(packageName, service);
            method.invoke(invoke, intent.getComponent());

            /*ComponentName componentName = new ComponentName(packageName, service);
            method.invoke(invoke, componentName);*/
            Toast.makeText(this, "表盘设置成功", Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            Log.e(mTag, "set live wall paper fail", e);
            Toast.makeText(this, "表盘设置失败", Toast.LENGTH_SHORT).show();
        }
    }

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
                            Intent intent = new Intent();
                            intent.setClassName(data.getPackageName(), settingsActivity);
                            startActivity(intent);
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
