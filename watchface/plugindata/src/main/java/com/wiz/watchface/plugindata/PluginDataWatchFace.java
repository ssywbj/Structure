package com.wiz.watchface.plugindata;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.structure.wallpaper.basic.bean.FacePluginParams;
import com.structure.wallpaper.basic.utils.Constants;
import com.wiz.watchface.plugindata.view.HorizontalDateView;
import com.wiz.watchface.plugindata.view.HorizontalTimeView;

public class PluginDataWatchFace extends FrameLayout {
    private static final String TAG = PluginDataWatchFace.class.getSimpleName();
    public static final String PREFS_FILE = "plugin_data_watch_face_config";
    private boolean mIsEditMode, mIsDimMode;
    private SharedPreferences mPrefs;
    private int mViewId;
    private HorizontalTimeView mTimeView;
    private HorizontalDateView mDateView;
    private boolean isVisible;
    private ViewGroup mViewGroup1, mViewGroup2;

    public PluginDataWatchFace(Context context, boolean isEditMode, boolean isDimMode) {
        super(context);
        mIsEditMode = isEditMode;
        mIsDimMode = isDimMode;
        this.init();
    }

    public PluginDataWatchFace(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public void init() {
        setBackgroundColor(Color.BLACK);
        View.inflate(getContext(), R.layout.view_plugin_data_face, this);

        mTimeView = findViewById(R.id.view_horizontal_time);
        mTimeView.setEditMode(mIsEditMode);
        mTimeView.setDimMode(mIsDimMode);
        mDateView = findViewById(R.id.view_horizontal_date);
        mDateView.setEditMode(mIsEditMode);
        mDateView.setDimMode(mIsDimMode);
        mViewGroup1 = findViewById(R.id.root_plugin_1);
        mViewGroup2 = findViewById(R.id.root_plugin_2);
        View parentPluginLayout1 = findViewById(R.id.parent_plugin_1);
        View parentPluginLayout2 = findViewById(R.id.parent_plugin_2);
        View btnShade1 = findViewById(R.id.btn_shade_plugin_1);
        View btnShade2 = findViewById(R.id.btn_shade_plugin_2);

        if (mIsEditMode) {
            parentPluginLayout1.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rect_btn_dash_hollow_bg));
            parentPluginLayout2.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rect_btn_dash_hollow_bg));
            btnShade1.setVisibility(VISIBLE);
            btnShade2.setVisibility(VISIBLE);
        } else {
            parentPluginLayout1.setBackground(null);
            parentPluginLayout2.setBackground(null);

            btnShade1.setOnClickListener(null);
            btnShade2.setOnClickListener(null);
            btnShade1.setVisibility(GONE);
            btnShade2.setVisibility(GONE);
        }

        mPrefs = getContext().getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);

        if (mIsEditMode || mIsDimMode) {
            setBtnDrawable(R.id.root_plugin_1);
            setBtnDrawable(R.id.root_plugin_2);
        } else {
            postDelayed(() -> {
                setBtnDrawable(R.id.root_plugin_1);
                setBtnDrawable(R.id.root_plugin_2);
            }, 200);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.registerReceiver();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.unregisterReceiver();
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
    }

    private void onVisibilityChanged(int viewId, boolean visible) {
        try {
            final String pkgInfo = mPrefs.getString(String.valueOf(viewId), "");
            if (TextUtils.isEmpty(pkgInfo) || !pkgInfo.contains(Constants.REGEX_SPILT)) {
                return;
            }

            String[] split = pkgInfo.split(Constants.REGEX_SPILT);
            if (split.length > 1) {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onDestroy(int viewId) {
        try {
            final String pkgInfo = mPrefs.getString(String.valueOf(viewId), "");
            if (TextUtils.isEmpty(pkgInfo) || !pkgInfo.contains(Constants.REGEX_SPILT)) {
                return;
            }

            String[] split = pkgInfo.split(Constants.REGEX_SPILT);
            if (split.length > 1) {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setBtnDrawable(int viewId) {
        try {
            final String key = String.valueOf(viewId);
            String pkgInfo = mPrefs.getString(key, "");
            if (TextUtils.isEmpty(pkgInfo) || !pkgInfo.contains(Constants.REGEX_SPILT)) {
                if (viewId == R.id.root_plugin_1) {
                    pkgInfo = "com.wiz.watch.health" + Constants.REGEX_SPILT
                            + "com.wiz.watch.health.step.plugin.SportHeartRatePlugin";
                } else if (viewId == R.id.root_plugin_2) {
                    pkgInfo = "com.wiz.watch.weather" + Constants.REGEX_SPILT
                            + "com.wiz.watch.weather.widget.WizWeatherDeskViewPlugin";
                }

                if (!TextUtils.isEmpty(pkgInfo)) {
                    mPrefs.edit().putString(key, pkgInfo).apply();
                }
            }

            String[] split = pkgInfo.split(Constants.REGEX_SPILT);
            //Log.d(TAG, "setBtnDrawable(int), split: " + split.length + ", viewId: " + viewId + ", pkgInfo: " + pkgInfo);
            if (split.length > 1) {
                FacePluginParams faceParams = new FacePluginParams(1, "");
                String json = new Gson().toJson(faceParams);
                //Log.d(TAG, "setBtnDrawable(int), pkg: " + split[0] + ", clazz: " + split[1] + ", pluginView: " + pluginView);
            }
        } catch (Exception e) {
            Log.e(TAG, "setBtnDrawable(int), error: " + e, e);
        }
    }

    private ViewGroup getViewGroup(int viewId) {
        if (viewId == R.id.root_plugin_1) {
            return mViewGroup1;
        } else if (viewId == R.id.root_plugin_2) {
            return mViewGroup2;
        } else {
            return null;
        }
    }


    private boolean mIsRegisterReceiver;

    private void registerReceiver() {
        if (mIsRegisterReceiver || mIsDimMode) {
            return;
        }
        mIsRegisterReceiver = true;

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ACTION_UPDATE_FACE_WIDGET);
        /*if (mIsEditMode) {
            intentFilter.addAction(Constants.ACTION_UPDATE_FACE_WIDGET);
        } else {
            intentFilter.addAction(Constants.ACTION_UPDATE_VIEW_ID);
        }*/
        getContext().registerReceiver(mReceiver, intentFilter);
    }

    private void unregisterReceiver() {
        if (mIsRegisterReceiver) {
            mIsRegisterReceiver = false;

            getContext().unregisterReceiver(mReceiver);
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //if (Constants.ACTION_UPDATE_FACE_WIDGET.equals(intent.getAction())) {
            String pkg = intent.getStringExtra(Constants.KEY_FACE_WIDGET_PKG);
            String clz = intent.getStringExtra(Constants.KEY_FACE_WIDGET_CLZ);
            //Log.d(TAG, "pkg: " + pkg + ", clz:" + clz + ", mViewId: " + mViewId + ", action: " + intent.getAction());
            SharedPreferences.Editor edit = mPrefs.edit();
            edit.putString(String.valueOf(mViewId), pkg + Constants.REGEX_SPILT + clz).apply();
            setBtnDrawable(mViewId);

                /*Intent intentId = new Intent(Constants.ACTION_UPDATE_VIEW_ID);
                intentId.putExtra(Constants.KEY_UPDATE_VIEW_ID, mViewId);
                context.sendBroadcast(intentId);
            }*/

            /*if (Constants.ACTION_UPDATE_VIEW_ID.equals(intent.getAction())) {
                int viewId = intent.getIntExtra(Constants.KEY_UPDATE_VIEW_ID, 0);
                //Log.d(FaceConfigFragment.TAG, "viewId: " + viewId);
                setBtnDrawable(viewId);
            }*/
        }
    };

}
