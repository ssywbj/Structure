package com.suheng.structure.wallpaperpicker;

import android.app.Service;
import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.lang.reflect.Method;

public class WallpaperPickService extends Service {
    private static final String TAG = "Wbj";
    private static final String ACTION_PICK_WALLPAPER = "com.wiz.watch.action.PICK_WALLPAPER";

    private BroadcastReceiver mWallpaperPickReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_PICK_WALLPAPER.equals(intent.getAction())) {
                Parcelable parcelable = intent.getParcelableExtra("wallpaper_info");
                if (parcelable instanceof WallpaperInfo) {
                    setLiveWallPaper((WallpaperInfo) parcelable);
                }
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_PICK_WALLPAPER);
        registerReceiver(mWallpaperPickReceiver, intentFilter);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mWallpaperPickReceiver);
    }

    /**
     * 设置动态壁纸（需是系统级应用和"android.permission.SET_WALLPAPER_COMPONENT"权限）
     */
    public void setLiveWallPaper(WallpaperInfo wallpaperInfo) {
        try {
            Log.d(TAG, "pkg: " + wallpaperInfo.getPackageName() + ", service: " + wallpaperInfo.getServiceName());
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
            //wallpaperManager.clearWallpaper();

            if (wallpaperInfo.getServiceInfo().metaData.getBoolean("recycle_life")) {
                wallpaperManager.setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.wallpaper_default));
            }

            //通过反射找到系统设置壁纸的方法
            Method method = WallpaperManager.class.getMethod("setWallpaperComponent", ComponentName.class);
            //设置壁纸。packageName：壁纸所包名；service：壁纸服务类，格式：包.类名称，如com.xxx.yyy.zzzz.XxxWallpaperService
            method.invoke(wallpaperManager, new ComponentName(wallpaperInfo.getPackageName(), wallpaperInfo.getServiceName()));
            Toast.makeText(this, "表盘设置成功", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "set live wallpaper fail", e);
            Toast.makeText(this, "表盘设置失败", Toast.LENGTH_SHORT).show();
        }
    }

    //deletePackage(@NonNull String packageName, @Nullable IPackageDeleteObserver observer, @DeleteFlags int flags);
    public void deleteLiveWallPaper(String packageName) throws Exception {
        Method method = PackageManager.class.getMethod("deletePackage", String.class, Object.class, int.class);
        method.invoke(getPackageManager(), packageName, null, 0);
    }

}
