package com.suheng.structure.module1;

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

import com.alibaba.android.arouter.facade.annotation.Route;
import com.suheng.structure.common.arouter.RouteTable;
import com.suheng.structure.ui.architecture.basic.BasicActivity;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Route(path = RouteTable.MODULE1_ATY_MODULE1_MAIN)
public class Module1MainActivity extends BasicActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module1_aty_module1_main);

        SocketClient.getInstance().connect(RouteTable.SOCKET_HOST, RouteTable.SOCKET_PORT);

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

        WallpaperInfo wallpaperInfo = wallpaperManager.getWallpaperInfo();
        if (wallpaperInfo == null) {
            Log.w(mTag, "WallpaperInfo is null !");
        } else {//不为空说明当前系统使用的是动态壁纸
            Log.d(mTag, "wallpaperInfo, name: " + wallpaperInfo.getServiceName());
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

        String className;
        Drawable drawable = null;
        ComponentName component = null;
        for (WallpaperInfo wallpaper : wallpaperInfoList) {
            component = wallpaper.getComponent();
            className = component.getClassName();
            drawable = wallpaper.loadThumbnail(packageManager);
            Log.d(mTag, "className: " + className + ", drawable = " + drawable + ", label = "
                    + wallpaper.loadLabel(packageManager));
        }

        /*try {
            Method method = WallpaperManager.class.getMethod("setWallpaperComponent", ComponentName.class, int.class);
            method.invoke(wallpaperManager, component, UUID.randomUUID().version());

            *//*Method method = WallpaperManager.class.getMethod("setBitmap", Bitmap.class);
            method.invoke(wallpaperManager, BitmapFactory.decodeResource(getResources(), R.drawable.bg));*//*
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(mTag, "e: " + e.toString());
        }*/

        try {
            WallpaperManager manager = WallpaperManager.getInstance(this);
            Method method = WallpaperManager.class.getMethod("getIWallpaperManager", new Class[]{});
            Object objIWallpaperManager = method.invoke(manager, new Object[]{});
            Class[] param = new Class[1];
            param[0] = ComponentName.class;
            method = objIWallpaperManager.getClass().getMethod("setWallpaperComponent", param);
            method.invoke(objIWallpaperManager, component);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(mTag, "e: " + e.toString());
        }

        this.setLiveWallPaper("FF");
    }

    //https://www.jianshu.com/p/626867f896aa，需系统权限
    public void setLiveWallPaper(String wallPaper) {
        try {
            WallpaperManager manager = WallpaperManager.getInstance(this);
            Method method = WallpaperManager.class.getMethod("getIWallpaperManager", new Class[]{});
            Object objIWallpaperManager = method.invoke(manager, new Object[]{});
            Class[] param = new Class[1];
            param[0] = ComponentName.class;
            method = objIWallpaperManager.getClass().getMethod("setWallpaperComponent", param);

            //get the intent of the desired wallpaper service. Note: I created my own
            //custom wallpaper service. You'll need a class reference and package
            //of the desired live wallpaper
            Intent intent = new Intent(WallpaperService.SERVICE_INTERFACE);
            intent.setClassName(getPackageName(), LiveWallpaperService.class.getName());
            //set the live wallpaper (throws security exception if you're not system-privileged app)
            method.invoke(objIWallpaperManager, intent.getComponent());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(mTag, "e: " + e.toString());
        }
    }

    public void sendString(View view) {
        SocketClient.getInstance().sendRequest("ni hao");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SocketClient.getInstance().disconnect();
    }
}
