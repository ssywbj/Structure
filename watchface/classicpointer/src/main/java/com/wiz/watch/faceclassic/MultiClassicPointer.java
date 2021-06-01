package com.wiz.watch.faceclassic;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.structure.wallpaper.basic.utils.BitmapManager;
import com.structure.wallpaper.basic.utils.Constants;

public class MultiClassicPointer extends RelativeLayout  {
    private ClassicWatchFace mClassicWatchFace;

    private boolean mIsEditMode;
    public static final String PREFS_FILE = "classic_pointer_watch_face_config";
    private SharedPreferences mPrefs;
    private int mViewId;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Log.d(FaceConfigFragment.TAG, "action: " + intent.getAction());
            if (Constants.ACTION_UPDATE_WATCH_FACE.equals(intent.getAction())) {
                Parcelable parcelable = intent.getParcelableExtra(Constants.KEY_DATA_COMPONENT_NAME);
                if (parcelable instanceof ComponentName) {
                    parseComponentName((ComponentName) parcelable);
                }
            }
        }
    };

    public MultiClassicPointer(Context context, boolean isEditMode) {
        super(context);
        this.initView();
        mIsEditMode = isEditMode;
        this.initData();
    }

    public MultiClassicPointer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.initView();
        this.initData();
    }

    private void initView() {
        View.inflate(getContext(), R.layout.view_classic_pointer_watch_face, this);
        mClassicWatchFace = findViewById(R.id.classic_pointer_watch_face);
    }

    private void initData() {
        mPrefs = getContext().getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        mClassicWatchFace.setEditMode(mIsEditMode);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.setCornerBtnBg();
        this.registerReceiver();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.unregisterReceiver();
    }

    private void setCornerBtnBg() {
        ImageButton btnTopLeft = findViewById(R.id.btn_top_left);
        ImageButton btnTopRight = findViewById(R.id.btn_top_right);
        ImageButton btnBottomLeft = findViewById(R.id.btn_bottom_left);
        ImageButton btnBottomRight = findViewById(R.id.btn_bottom_right);
        if (mIsEditMode) {
            btnTopLeft.setBackgroundResource(R.drawable.corner_btn_dash_bg);
            btnTopRight.setBackgroundResource(R.drawable.corner_btn_dash_bg);
            btnBottomLeft.setBackgroundResource(R.drawable.corner_btn_dash_bg);
            btnBottomRight.setBackgroundResource(R.drawable.corner_btn_dash_bg);
        } else {
            btnTopLeft.setBackgroundResource(R.drawable.corner_btn_bg);
            btnTopRight.setBackgroundResource(R.drawable.corner_btn_bg);
            btnBottomLeft.setBackgroundResource(R.drawable.corner_btn_bg);
            btnBottomRight.setBackgroundResource(R.drawable.corner_btn_bg);
        }

        postDelayed(new Runnable() {
            @Override
            public void run() {
                setBtnDrawable(R.id.btn_top_left);
                setBtnDrawable(R.id.btn_top_right);
                setBtnDrawable(R.id.btn_bottom_left);
                setBtnDrawable(R.id.btn_bottom_right);
            }
        }, 400);
    }

    private boolean mIsRegisterReceiver;

    private void registerReceiver() {
        if (mIsRegisterReceiver) {
            return;
        }
        mIsRegisterReceiver = true;

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ACTION_UPDATE_WATCH_FACE);
        getContext().registerReceiver(mReceiver, intentFilter);
    }

    private void unregisterReceiver() {
        if (mIsRegisterReceiver) {
            mIsRegisterReceiver = false;

            getContext().unregisterReceiver(mReceiver);
        }
    }

    private void parseComponentName(ComponentName componentName) {
        if (componentName == null) {
            return;
        }

        PackageManager packageManager = getContext().getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setComponent(componentName);
        ResolveInfo resolveInfo = packageManager.resolveActivity(intent, PackageManager.GET_META_DATA);
        ActivityInfo activityInfo = resolveInfo.activityInfo;
        Bundle metaData = activityInfo.metaData;
        if (metaData != null) {
            int resId = metaData.getInt(Constants.KEY_ICON_FACE);
            if (resId != 0) {
                Drawable drawable = packageManager.getDrawable(activityInfo.packageName, resId, activityInfo.applicationInfo);
                //Log.i(FaceConfigFragment.TAG, "selected drawable: " + drawable + ", mViewId: " + mViewId + ", this: " + this);
                if (mViewId != 0) {
                    this.setBtnDrawable(drawable, mViewId);
                    mPrefs.edit().putString(mViewId + ""
                            , componentName.getPackageName() + Constants.REGEX_SPILT + componentName.getClassName()).apply();
                }
            }
        }
    }

    private void setBtnDrawable(int viewId) {
        try {
            final String key = viewId + "";
            String component = mPrefs.getString(key, "");
            if (TextUtils.isEmpty(component) || !component.contains(Constants.REGEX_SPILT)) {
                ComponentName componentName = null;
                if (viewId == R.id.btn_top_left) {
                    componentName = new ComponentName("com.wiz.watch.health"
                            , "com.wiz.watch.health.HealthActivity");
                } else if (viewId == R.id.btn_top_right) {
                    componentName = new ComponentName("com.wiz.watch.health"
                            , "com.wiz.watch.health.HeartRateActivity");
                } else if (viewId == R.id.btn_bottom_left) {
                    componentName = new ComponentName("com.android.contacts"
                            , "com.android.contacts.activities.DialtactsActivity");
                } else if (viewId == R.id.btn_bottom_right) {
                    componentName = new ComponentName("com.wiz.watch.health"
                            , "com.wiz.watch.health.ui.activity.MainActivity");
                }

                if (componentName != null) {
                    component = componentName.getPackageName() + Constants.REGEX_SPILT + componentName.getClassName();
                    mPrefs.edit().putString(key, component).apply();
                }
            }

            String[] split = component.split(Constants.REGEX_SPILT);
            if (split.length > 1) {
                ComponentName componentName = new ComponentName(split[0], split[1]);
                PackageManager packageManager = getContext().getPackageManager();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setComponent(componentName);
                ResolveInfo resolveInfo = packageManager.resolveActivity(intent, PackageManager.GET_META_DATA);
                if (resolveInfo == null) {
                    return;
                }
                ActivityInfo activityInfo = resolveInfo.activityInfo;
                if (activityInfo == null) {
                    return;
                }
                Bundle metaData = activityInfo.metaData;
                if (metaData != null) {
                    int resId = metaData.getInt(Constants.KEY_ICON_FACE);
                    Drawable drawable = packageManager.getDrawable(activityInfo.packageName, resId, activityInfo.applicationInfo);
                    this.setBtnDrawable(drawable, viewId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setBtnDrawable(Drawable drawable, int viewId) {
        ImageButton imageButton = findViewById(viewId);
        if (drawable == null || imageButton == null) {
            return;
        }

        //Log.d("FaceConfigFragment", "viewId: " + viewId + ", this: " + this);
        imageButton.setImageBitmap(null);
        if (mIsEditMode) {
            imageButton.setImageBitmap(BitmapManager.scale(drawable, 0.8f));
        } else {
            imageButton.setImageBitmap(BitmapManager.scale(drawable, 0.9f));
        }
    }

}
