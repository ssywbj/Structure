package com.suheng.structure.wallpaperpicker;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import java.util.Locale;

public class Utils {

    public static String formatDuration(long millis) {
        // 将毫秒转换为总秒数
        final long totalSeconds = millis / 1000;

        // 计算分钟和秒
        final long minutes = totalSeconds / 60;
        final long seconds = totalSeconds % 60;

        // 格式化为 "MM:SS" 的形式
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    public static Drawable getIconFromPackage(Context context, String packageName, int resId) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo appInfo = packageManager.getApplicationInfo(packageName, 0);
            Resources resources = packageManager.getResourcesForApplication(appInfo);
            return resources.getDrawable(resId, context.getTheme());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
