package com.wiz.watch.faceshuttle;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.structure.wallpaper.basic.utils.BitmapManager;
import com.structure.wallpaper.basic.utils.Constants;
import com.structure.wallpaper.basic.utils.IntentHelper;
import com.wiz.watch.faceshuttle.view.ClockPanel;
import com.wiz.watch.faceshuttle.view.PanelView;

public class ShuttleWatchFace extends FrameLayout {
    private PanelView mPanelView;
    private ClockPanel mClockPanel;
    private ImageButton mIBtnTopLeft, mIBtnTopRight, mIBtnBottomLeft, mIBtnBottomRight;
    private View mRootTopLeft, mRootTopRight, mRootBottomLeft, mRootBottomRight;
    private boolean mIsEditMode;
    private int mStyle;

    public ShuttleWatchFace(Context context) {
        super(context);
        this.init();
    }

    public ShuttleWatchFace(Context context, int style, boolean isEditMode) {
        super(context);
        mIsEditMode = isEditMode;
        mStyle = style;
        this.init();
    }

    public ShuttleWatchFace(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public void init() {
        View.inflate(getContext(), R.layout.view_shuttle_face, this);

        mPanelView = findViewById(R.id.panel_view);
        mClockPanel = findViewById(R.id.clock_panel);
        mIBtnTopLeft = findViewById(R.id.ibtn_tl);
        mIBtnTopRight = findViewById(R.id.ibtn_tr);
        mIBtnBottomLeft = findViewById(R.id.ibtn_bl);
        mIBtnBottomRight = findViewById(R.id.ibtn_br);
        mIBtnTopLeft.setOnClickListener(mOnClickListener);
        mIBtnTopRight.setOnClickListener(mOnClickListener);
        mIBtnBottomLeft.setOnClickListener(mOnClickListener);
        mIBtnBottomRight.setOnClickListener(mOnClickListener);

        mRootTopLeft = findViewById(R.id.root_ibtn_tl);
        mRootTopRight = findViewById(R.id.root_ibtn_tr);
        mRootBottomLeft = findViewById(R.id.root_ibtn_bl);
        mRootBottomRight = findViewById(R.id.root_ibtn_br);

        mPrefs = getContext().getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        mClockPanel.setEditMode(mIsEditMode);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.setViewsStyle();
        this.registerReceiver();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.unregisterReceiver();
    }

    public void onVisibilityChanged(boolean visible) {
        mClockPanel.onVisibilityChanged(visible);
        mPanelView.onVisibilityChanged(visible);
        if (visible) {
            if (mIsEditMode) {
                this.registerReceiver();
            }

            setBtnDrawable(R.id.ibtn_tl);
            setBtnDrawable(R.id.ibtn_tr);
            setBtnDrawable(R.id.ibtn_bl);
            setBtnDrawable(R.id.ibtn_br);
        } else {
            this.unregisterReceiver();
        }
    }


    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        this.onVisibilityChanged(visibility == VISIBLE);
    }

    public void destroy() {
        mClockPanel.destroy();
        mPanelView.destroy();
    }

    private void setViewsStyle() {
        if (mStyle == 1) {
            this.setBtnStyle(R.drawable.face_panel_1, R.drawable.pointer_hour_1, R.drawable.pointer_minute_1, R.drawable.pointer_second_1
                    , R.drawable.ibtn_bg_1);
        } else if (mStyle == 2) {
            this.setBtnStyle(R.drawable.face_panel_2, R.drawable.pointer_hour_2, R.drawable.pointer_minute_2, R.drawable.pointer_second_2
                    , R.drawable.ibtn_bg_2);
        } else if (mStyle == 3) {
            this.setBtnStyle(R.drawable.face_panel_3, R.drawable.pointer_hour_3, R.drawable.pointer_minute_3, R.drawable.pointer_second_3
                    , R.drawable.ibtn_bg_3);
        } else if (mStyle == 4) {
            this.setBtnStyle(R.drawable.face_panel_4, R.drawable.pointer_hour_4, R.drawable.pointer_minute_4, R.drawable.pointer_second_4
                    , R.drawable.ibtn_bg_4);
        } else {
            this.setBtnStyle(R.drawable.face_panel, R.drawable.pointer_hour, R.drawable.pointer_minute, R.drawable.pointer_second
                    , R.drawable.ibtn_bg_2);
        }

        if (mIsEditMode) {
            if (mStyle == 1 || mStyle == 3) {
                mRootTopLeft.setBackgroundResource(R.drawable.corner_btn_dash_hollow_black_bg);
                mRootTopRight.setBackgroundResource(R.drawable.corner_btn_dash_hollow_black_bg);
                mRootBottomLeft.setBackgroundResource(R.drawable.corner_btn_dash_hollow_black_bg);
                mRootBottomRight.setBackgroundResource(R.drawable.corner_btn_dash_hollow_black_bg);
            } else {
                mRootTopLeft.setBackgroundResource(R.drawable.corner_btn_dash_hollow_bg);
                mRootTopRight.setBackgroundResource(R.drawable.corner_btn_dash_hollow_bg);
                mRootBottomLeft.setBackgroundResource(R.drawable.corner_btn_dash_hollow_bg);
                mRootBottomRight.setBackgroundResource(R.drawable.corner_btn_dash_hollow_bg);
            }
        } else {
            mRootTopLeft.setBackgroundResource(R.drawable.transl);
            mRootTopRight.setBackgroundResource(R.drawable.transl);
            mRootBottomLeft.setBackgroundResource(R.drawable.transl);
            mRootBottomRight.setBackgroundResource(R.drawable.transl);
        }
    }

    private void setBtnStyle(int p, int p2, int p3, int p4, int p5) {
        mPanelView.setBackgroundResource(p);

        mClockPanel.setHourPointer(BitmapFactory.decodeResource(getResources(), p2));
        mClockPanel.setMinutePointer(BitmapFactory.decodeResource(getResources(), p3));
        mClockPanel.setSecondPointer(BitmapFactory.decodeResource(getResources(), p4));

        mIBtnTopLeft.setBackgroundResource(p5);
        mIBtnTopRight.setBackgroundResource(p5);
        mIBtnBottomLeft.setBackgroundResource(p5);
        mIBtnBottomRight.setBackgroundResource(p5);
    }

    public static final String PREFS_FILE = "shuttle_watch_face_config";
    private int mViewId;
    private SharedPreferences mPrefs;


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
                    mPrefs.edit().putString(mViewId + Constants.REGEX_SPILT
                            , componentName.getPackageName() + Constants.REGEX_SPILT + componentName.getClassName()).apply();
                }
            }
        }
    }

    private void setBtnDrawable(Drawable drawable, int viewId) {
        ImageButton imageButton = findViewById(viewId);
        if (drawable == null || imageButton == null) {
            return;
        }

        //Log.d("FaceConfigFragment", "viewId: " + viewId + ", this: " + this);
        imageButton.setImageBitmap(null);
        if (mStyle == 1 || mStyle == 3) {
            imageButton.setImageBitmap(BitmapManager.scale(drawable, 0.9f, Color.BLACK));
        } else {
            imageButton.setImageBitmap(BitmapManager.scale(drawable, 0.9f, Color.WHITE));
        }
    }

    private void setBtnDrawable(int viewId) {
        final String key = viewId + Constants.REGEX_SPILT;
        String component = mPrefs.getString(key, "");
        if (TextUtils.isEmpty(component) || !component.contains(Constants.REGEX_SPILT)) {
            ComponentName componentName = null;
            if (viewId == R.id.ibtn_tl) {
                componentName = new ComponentName("com.wiz.watch.health"
                        , "com.wiz.watch.health.HeartRateActivity");
            } else if (viewId == R.id.ibtn_tr) {
                componentName = new ComponentName("com.wiz.watch.camera"
                        , "com.mediatek.camera.CameraLauncher");
            } else if (viewId == R.id.ibtn_bl) {
                componentName = new ComponentName("com.wiz.watch.health"
                        , "com.wiz.watch.health.ui.activity.MainActivity");
            } else if (viewId == R.id.ibtn_br) {
                componentName = new ComponentName("com.wiz.watch.weather"
                        , "com.wiz.watch.weather.MainActivity");
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
    }

    private final View.OnClickListener mOnClickListener = (View v) -> {
        mViewId = v.getId();
        String component = mPrefs.getString(mViewId + Constants.REGEX_SPILT, "");
        if (mIsEditMode) {
            Bundle bundle = new Bundle();
            bundle.putString(Constants.KEY_SELECTED_COMPONENT, component);
        } else {
            if (TextUtils.isEmpty(component) || !component.contains(Constants.REGEX_SPILT)) {
                return;
            }
            String[] split = component.split(Constants.REGEX_SPILT);
            if (split.length > 1) {
                ComponentName componentName = new ComponentName(split[0], split[1]);
                try {
                    getContext().startActivity(IntentHelper.makeLaunchIntent(componentName));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };
}
