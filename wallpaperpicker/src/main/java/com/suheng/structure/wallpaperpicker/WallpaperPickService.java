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
import android.os.Build;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Method;

public class WallpaperPickService extends Service {
    private static final String TAG = "Wbj";
    private static final String ACTION_PICK_WALLPAPER = "com.wiz.watch.action.PICK_WALLPAPER";

    private final BroadcastReceiver mWallpaperPickReceiver = new BroadcastReceiver() {
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
        final String pkg = wallpaperInfo.getPackageName();
        final String cls = wallpaperInfo.getServiceName();
        setLiveWallPaper(this, pkg, cls,false);
    }

    //deletePackage(@NonNull String packageName, @Nullable IPackageDeleteObserver observer, @DeleteFlags int flags);
    public void deleteLiveWallPaper(String packageName) throws Exception {
        Method method = PackageManager.class.getMethod("deletePackage", String.class, Object.class, int.class);
        method.invoke(getPackageManager(), packageName, null, 0);
    }

    public static void setLiveWallPaper(@NonNull Context context, @NonNull String pkg, @NonNull String cls, boolean isClearLockWallpaper) {
        Log.d(TAG, "pkg: " + pkg + ", service: " + cls);
        try {
            final WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
            if (isClearLockWallpaper) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    if (wallpaperManager.getWallpaperId(WallpaperManager.FLAG_LOCK) > 0) {
                        wallpaperManager.clear(WallpaperManager.FLAG_LOCK);
                    }
                }
            }
            //通过反射找到系统设置壁纸的方法
            final Method method = WallpaperManager.class.getMethod("setWallpaperComponent", ComponentName.class);
            //设置壁纸。pkg：壁纸所包名；cls：壁纸服务类，格式：包.类名称，如com.xxx.yyy.XxxWallpaperService
            method.invoke(wallpaperManager, new ComponentName(pkg, cls));
            Toast.makeText(context, "setting success", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "set live wallpaper fail", e);
            Toast.makeText(context, "setting fail", Toast.LENGTH_SHORT).show();
        }
    }

}
