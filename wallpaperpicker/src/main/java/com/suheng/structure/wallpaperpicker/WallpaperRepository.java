package com.suheng.structure.wallpaperpicker;

import android.app.WallpaperInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.service.wallpaper.WallpaperService;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class WallpaperRepository {
    private static final String TAG = WallpaperRepository.class.getSimpleName();

    private static volatile WallpaperRepository sInstance;
    private final PackageManager mPackageManager;

    private WallpaperRepository(@NonNull Context context) {
        mPackageManager = context.getPackageManager();
    }

    public static WallpaperRepository getInstance(@NonNull Context context) {
        if (sInstance == null) {
            synchronized (WallpaperRepository.class) {
                if (sInstance == null) {
                    sInstance = new WallpaperRepository(context);
                }
            }
        }
        return sInstance;
    }

    public List<WallpaperInfo> getPaperList(@NonNull Context context) {
        List<ResolveInfo> resolveInfoList = mPackageManager.queryIntentServices(new Intent(WallpaperService.SERVICE_INTERFACE), PackageManager.GET_META_DATA);
        int size = resolveInfoList.size();
        Log.d(TAG, "wallpaperInfo, size: " + size);
        List<WallpaperInfo> wallpaperInfoList = new ArrayList<>();
        for (ResolveInfo resolveInfo : resolveInfoList) {
            try {
                wallpaperInfoList.add(new WallpaperInfo(context, resolveInfo));
            } catch (Exception e) {
                Log.e(TAG, "add WallpaperInfo error", e);
            }
        }

        List<WallpaperInfo> wallpaperInfos = new ArrayList<>();
        for (WallpaperInfo wallpaper : wallpaperInfoList) {
            String packageName = wallpaper.getPackageName();
            String service = wallpaper.getServiceName();
            Drawable drawable = wallpaper.loadThumbnail(mPackageManager);
            ServiceInfo serviceInfo = wallpaper.getServiceInfo();
            Bundle bundle = serviceInfo.metaData;
            Log.d(TAG, "package: " + packageName + ", service: " + service + ", drawable = " + drawable
                    + ", label = " + wallpaper.loadLabel(mPackageManager) + ", setting activity: "
                    + wallpaper.getSettingsActivity());
            Log.d(TAG, "service info, name: " + serviceInfo.name + ", recycle_life: "
                    + bundle.getBoolean("recycle_life"));
            if (drawable != null) {
                wallpaperInfos.add(wallpaper);
            }
        }

        return wallpaperInfos;
    }

}
