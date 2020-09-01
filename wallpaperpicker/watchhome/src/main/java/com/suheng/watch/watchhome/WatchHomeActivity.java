package com.suheng.watch.watchhome;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.AnalogClock;

import androidx.annotation.Nullable;

public class WatchHomeActivity extends Activity {
    private static final String APP_TAG = "WatchHome";
    private static final String REQUEST_STATE_ACTION =
            "com.google.android.wearable.watchfaces.action.REQUEST_STATE";
    private static final int REQUEST_CODE_SIM_STATE_PIN_VALIDATE = 107;
    private static final int REQUEST_CODE_SIM_STATE_PUK_VALIDATE = 108;
    private AnalogClock mAnalogClock;
    private WallpaperManager mWallpaperManager;

    private boolean mInContextualMode = false;
    private boolean mPINRequired = false;
    private boolean mPUKRequired = false;

    private void updateWallpaperState(boolean shouldDoContextualUpdate) {
        Bundle bundle = new Bundle();

        if (mInContextualMode) {
            bundle.putBoolean("ambient_mode", true);
        } else {
            bundle.putBoolean("ambient_mode", false);
        }

        /*final View layout = (View) findViewById(com.qualcomm.qti.watchhome.R.id.watch_home);
        if (layout.getWindowToken() != null) {
            mWallpaperManager.sendWallpaperCommand(
                    layout.getWindowToken(),
                    "com.google.android.wearable.action.BACKGROUND_ACTION",
                    0, 0, 0, bundle);

            if (shouldDoContextualUpdate) {
                mWallpaperManager.sendWallpaperCommand(
                        layout.getWindowToken(),
                        "com.google.android.wearable.action.AMBIENT_UPDATE",
                        0, 0, 0, null);
                Log.i(APP_TAG, "Sending ambient update command to wallpaper");
            } else {
                Log.i(APP_TAG, "Sent ambient_mode enter command to wallpaper");
            }
        }*/
    }

    private BroadcastReceiver mWatchFaceBroadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (REQUEST_STATE_ACTION.equals(intent.getAction())) {
                updateWallpaperState(false);
            }
            if ("android.intent.action.SIM_STATE_CHANGED".equals(intent.getAction())) {
                handleSIMStateChanged();
            }
        }
    };

    //verify if SIM PIN is required and start PIN/PUK entry.
    private void handleSIMStateChanged() {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        int simState = tm.getSimState();
        Log.i(APP_TAG, "SIM state " + simState);
        switch (simState) {
            case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                Log.i(APP_TAG, "SIM PIN Required");
                mPINRequired = true;
                mPUKRequired = false;
                validateSIMCard();
                break;
            case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                Log.i(APP_TAG, "SIM PUK Required");
                mPINRequired = false;
                mPUKRequired = true;
                validateSIMCard();
                break;
            default:
                break;
        }
    }

    private boolean validateSIMCard() {
        /*if (mPINRequired) {
            Intent validatePINIntent = new Intent(this, ValidateSIMPIN.class);
            startActivityForResult(validatePINIntent, REQUEST_CODE_SIM_STATE_PIN_VALIDATE);
            return mPINRequired;
        } else if (mPUKRequired) {
            Intent validatePUKIntent = new Intent(this, ValidateSIMPUK.class);
            startActivityForResult(validatePUKIntent, REQUEST_CODE_SIM_STATE_PUK_VALIDATE);
            return mPUKRequired;
        } else {
            return false;
        }*/

        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SIM_STATE_PIN_VALIDATE) {
            if (resultCode == Activity.RESULT_OK) {
                mPINRequired = false;
                openLauncher();
            }
        } else if (requestCode == REQUEST_CODE_SIM_STATE_PUK_VALIDATE) {
            if (resultCode == Activity.RESULT_OK) {
                mPUKRequired = false;
                openLauncher();
            }
        }
    }

    public void openLauncher() {
        Log.i(APP_TAG, "Launching the custom launcher");
        PackageManager pm = getPackageManager();
        final String LAUNCHER_TAG = getResources().getString(R.string.launcher_package_name);
        Intent launchIntent = pm.getLaunchIntentForPackage(LAUNCHER_TAG);
        if (launchIntent == null) {
            Log.e(APP_TAG, "Launch intent for custom launcher not found");
        } else if (!validateSIMCard()) {
            startActivity(launchIntent);
        }
    }

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(APP_TAG, "OnCreate");
        boolean provisioned = Settings.Global.getInt(getContentResolver(), Settings.Global.DEVICE_PROVISIONED, 0) != 0;
        Log.d(APP_TAG, "provisioned=" + provisioned);

        if (!provisioned) {
            // Add a persistent setting to allow other apps to know the device has been provisioned.
            Settings.Global.putInt(getContentResolver(), Settings.Global.DEVICE_PROVISIONED, 1);
            //Settings.Secure.putInt(getContentResolver(), Settings.Secure.USER_SETUP_COMPLETE, 1);
        }

        setContentView(R.layout.activity_home);
        mAnalogClock = findViewById(R.id.analogClock);

        SharedPreferences wallpaper = this.getSharedPreferences("wallpaper", MODE_PRIVATE);
        final SharedPreferences.Editor editor = wallpaper.edit();

        //Set wallpaper window parameters
        int wpFlags = WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER;
        int currFlags = getWindow().getAttributes().flags & (WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER);

        if (wpFlags != currFlags) {
            getWindow().setFlags(wpFlags, WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER);
        }

        final WindowManager.LayoutParams params =
                new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                        PixelFormat.TRANSLUCENT);

        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;

        getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        //Get WallpaperManager instance
        mWallpaperManager = WallpaperManager.getInstance(this);

        //Get WatchHome activity layout
        final View layout = findViewById(R.id.watch_home);

        //Remember wallpaper selected for future boot ups
        boolean wallpaperSetState = wallpaper.getBoolean("key", false);

        //Get currently set wallpaper info
        if (null != mWallpaperManager) {
            WallpaperInfo wallpaperInfo = mWallpaperManager.getWallpaperInfo();
            Log.i(APP_TAG, "wallpaperInfo" + wallpaperInfo);
            *//*
             * To avoid setting default AOSP static image wallpaper which is heavier for wearable if LMK kills
             * watchhome(adj 0) and watchface(adj 100) during memory crunch situations
             *//*
            if (null == wallpaperInfo) {
                Log.e(APP_TAG, "OOPS...!!! No wallpaper is set...!!! Forcefully setting watchface wallpaper...");
                wallpaperSetState = false;
            }
        }

        *//*if (!wallpaperSetState) {
            Log.i(APP_TAG, "Starting wallpaper service");
            final String watchFacePackage = getResources().getString(R.string.anadigclock_package_name);
            final String watchFaceClass = getResources().getString(R.string.anadigclock_class_name);
            final ComponentName cn = new ComponentName(watchFacePackage, watchFaceClass);
            try {
                mWallpaperManager.getIWallpaperManager().setWallpaperComponent(cn);
                mWallpaperManager.setWallpaperOffsetSteps(0.5f, 0.0f);
                mWallpaperManager.setWallpaperOffsets(layout.getWindowToken(), 0.5f, 0.0f);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //Once wallpaper is set, use the same for subsequent boot ups
            editor.putBoolean("key", true).apply();
        }*//*

        IntentFilter filter = new IntentFilter();
        filter.addAction(REQUEST_STATE_ACTION);
        filter.addAction("android.intent.action.SIM_STATE_CHANGED");
        registerReceiver(mWatchFaceBroadReceiver, filter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(APP_TAG, "Started");
    }

    @Override
    protected void onResume() {
        super.onResume();
        final View layout = findViewById(R.id.watch_home);
        //Set activity background to null
        layout.setBackground(null);

        //Get currently set wallpaper info
        WallpaperInfo wallpaperInfo = mWallpaperManager.getWallpaperInfo();
        if (wallpaperInfo != null) {
            String mSetWallpaper = wallpaperInfo.getServiceName().toString();
            *//*
             * On device reboot, based on wallpaper set,
             * check if analog clock is to be shown or not
             *//*
            if (mSetWallpaper.equals(getResources()
                    .getString(R.string.anadigclock_class_name)) ||
                    mSetWallpaper.equals(getResources().getString(R.string.anaclock_class_name))) {
                mAnalogClock.setVisibility(View.GONE);
            } else {
                mAnalogClock.setVisibility(View.VISIBLE);
            }
        }
        Log.i(APP_TAG, "Resumed and background set to null");

        //Get layout width and height and use it for fling distance threshold
        final int width = layout.getWidth();
        final int height = layout.getHeight();

        final PackageManager mPackageManager =
                getApplicationContext().getPackageManager();

        //Populate list with all available live wallpaper services
        final List<ResolveInfo> mWallpaperList =
                mPackageManager.queryIntentServices(
                        new Intent(WallpaperService.SERVICE_INTERFACE),
                        PackageManager.GET_META_DATA);

        //Get total number of wallpapers available
        final int size = mWallpaperList.size();

        //Arrays for storing live wallpaper info
        final String[] packageName = new String[size];
        final String[] serviceName = new String[size];

        //Get Package and Service names of all available live wallpapers
        retrieveLiveWallpaperInfo(mWallpaperList, size, packageName, serviceName);
    }

    //Populate package and service name of available live wallpapers
    private void retrieveLiveWallpaperInfo(List<ResolveInfo> wallpaperList,
                                           int count, String[] packageName, String[] serviceName) {
        //Populate package name and service name from wallpaper info
        for (int i = 0; i < count; i++) {
            WallpaperInfo info;
            ResolveInfo resolveInfo = wallpaperList.get(i);
            try {
                info = new WallpaperInfo(getApplicationContext(),
                        resolveInfo);
                packageName[i] = info.getPackageName();
                serviceName[i] = info.getServiceName();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    //Set chosen wallpaper as new watchface
    private void setWallpaper(View layout, int index, String packageName, String serviceName) {
        //Check if analog clock is to be made visible or not
        *//*if (serviceName.equals(getResources()
                .getString(R.string.anadigclock_class_name)) ||
                serviceName.equals(getResources().getString(R.string.anaclock_class_name))) {
            mAnalogClock.setVisibility(View.VISIBLE);
        } else {
            mAnalogClock.setVisibility(View.GONE);
        }

        Log.i(APP_TAG, "Set live wallpaper as new watchface.Package name : " +
                packageName);
        final String watchfacePackage = packageName;
        final String watchfaceClass = serviceName;
        final ComponentName cn = new ComponentName(watchfacePackage, watchfaceClass);
        try {
            mWallpaperManager.getIWallpaperManager().setWallpaperComponent(cn);
            mWallpaperManager.setWallpaperOffsetSteps(0.5f, 0.0f);
            mWallpaperManager.setWallpaperOffsets(layout.getWindowToken(),
                    0.5f, 0.0f);
        } catch (Exception e) {
            e.printStackTrace();
        }*//*
    }

    //Unregister broadcast receiver while stopping activity
    @Override
    protected void onStop() {
        super.onStop();
        Log.i(APP_TAG, "Stopped");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(APP_TAG, "onDestroy");
        unregisterReceiver(mWatchFaceBroadReceiver);
    }

    //Override back button for watchhome as it's home screen
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }*/

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        /*setContentView(R.layout.activity_home);*/
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("Wbj", event.getAction() + ", " + event.getX());
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        IBinder windowToken = getWindow().getDecorView().getWindowToken();
        wallpaperManager.sendWallpaperCommand(windowToken, "Wbj"
                , (int) event.getX(), (int) event.getY(), 0, null);
        wallpaperManager.setWallpaperOffsets(windowToken, 0, 8);
        return super.onTouchEvent(event);
    }
}
