package com.structure.wallpaper.basic.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

public class IntentHelper {
    private static final String TAG = IntentHelper.class.getSimpleName();
    private static final String ACTION_MUSIC = "com.wiz.watch.music.Action.Enter";
    private static final String ACTION_HEALTH = "com.wiz.watch.health.action.HEART_RATE_LAUNCHER";

    /**
     * 打开相机app，备注：纯粹的打开，不返回数据，相当于点击其应用图标。
     */
    public static void openCameraApp(Context context) {
        try {
            Intent intent = new Intent();
            intent.setClassName("com.wiz.watch.camera", "com.mediatek.camera.CameraActivity");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "open camera app error: " + e);
        }
    }

    /**
     * 打开心率app，备注：同上
     */
    public static void openHeartRateApp(Context context) {
        openApp(context, ACTION_HEALTH);
    }

    /**
     * 打开联系人app，备注：同上
     */
    public static void openContactsApp(Context context) {
        try {
            Intent intent = new Intent();
            String pkg = "com.android.contacts";
            intent.setClassName(pkg, pkg + ".activities.DialtactsActivity");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "open contacts app error: " + e);
        }
    }

    /**
     * 打开音乐app，备注：同上
     */
    public static void openMusicApp(Context context) {
        openApp(context, ACTION_MUSIC);
    }

    public static void openApp(Context context, String action) {
        try {
            Intent intent = new Intent(action);
            ResolveInfo resolveInfo = context.getPackageManager().resolveActivity(
                    intent, PackageManager.GET_META_DATA);
            if (resolveInfo == null) {
                return;
            }
            String pkg = resolveInfo.activityInfo.packageName;
            String name = resolveInfo.activityInfo.name;
            intent.setComponent(new ComponentName(pkg, name));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "open app error: " + e);
        }
    }

    public static Intent makeLaunchIntent(ComponentName componentName) {
        return new Intent(Intent.ACTION_MAIN)
                .addCategory(Intent.CATEGORY_LAUNCHER)
                .setComponent(componentName)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
    }

}
