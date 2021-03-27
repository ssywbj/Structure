package com.suheng.watchface.basic.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PointF;
import android.os.BatteryManager;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.suheng.watchface.basic.utils.DateUtil;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class WatchFaceView extends View {
    private static final long UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1);
    protected Context mContext;
    protected String mTAG;
    protected PointF mPointScreenCenter = new PointF(); //屏幕中心点

    private boolean mRegisteredTimeZoneReceiver, mRegisteredTimeChangeReceiver, mRegisteredBatteryChangeReceiver;
    protected boolean mIsEditMode;
    protected boolean mIsRoundScreen, mIsDimMode;

    protected int mBatteryLevel; //当前电量百分比值，如若是80%，那么level就是80
    protected int mHour, mMinute, mSecond;
    protected float mHourRatio, mMinuteRatio, mSecondRatio;

    private final BroadcastReceiver mTimeChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Log.d(mTAG, "TimeChangeReceiver: " + intent.getAction());
            onTimeTick();
        }
    };

    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            updateTime();
            invalidate();

            long delayMillis = UPDATE_RATE_MS - (System.currentTimeMillis() % UPDATE_RATE_MS);
            getHandler().postDelayed(mRunnable, delayMillis);
        }
    };

    private final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Calendar.getInstance().setTimeZone(TimeZone.getDefault());
        }
    };

    private final BroadcastReceiver mBatteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                mBatteryLevel = intent.getIntExtra("level", 0);//当前电量，百分比
            }
        }
    };

    public WatchFaceView(Context context) {
        super(context);
        this.initView();
    }

    public WatchFaceView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.initView();
    }

    private void initView() {
        mContext = getContext();
        mTAG = getClass().getSimpleName();

        mIsRoundScreen = mContext.getResources().getConfiguration().isScreenRound();

        BatteryManager batterymanager = (BatteryManager) mContext.getSystemService(Context.BATTERY_SERVICE);
        if (batterymanager != null) {
            mBatteryLevel = batterymanager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPointScreenCenter.x = w / 2f;//屏幕中心X坐标
        mPointScreenCenter.y = h / 2f;//屏幕中心Y坐标
    }

    @Override
    public void onScreenStateChanged(int screenState) {
        super.onScreenStateChanged(screenState);
        boolean screenOn = (screenState == SCREEN_STATE_ON);
        //Log.d(mTAG, "Changed Screen: " + screenState + ", " + screenOn + ", visibility: " + getVisibility());
        this.onVisibilityChanged(screenOn && (getVisibility() == VISIBLE));
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        boolean visible = (visibility == VISIBLE);
        //Log.d(mTAG, "Changed Visibility: " + visibility + ", " + visible);
        this.onVisibilityChanged(visible);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        //this.onVisibilityChanged(true);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getHandler().removeCallbacksAndMessages(null);
    }

    public void onVisibilityChanged(boolean visible) {
        if (mIsDimMode) {
            this.updateTime();
            return;
        }

        //mReportedVisible = visible;
        if (visible) {
            this.registerTimeZoneReceiver();
        } else {
            this.unregisterTimeZoneReceiver();
        }
        //Log.d(mTAG, "onVisibilityChanged, visible: " + visible);
    }

    protected void updateTime() {
        mHour = DateUtil.getHour(mContext);
        Calendar instance = Calendar.getInstance();
        mMinute = instance.get(Calendar.MINUTE);
        mSecond = instance.get(Calendar.SECOND);

        mHourRatio = (mHour + mMinute / 60f) / 12;
        mMinuteRatio = (mMinute + mSecond / 60f) / 60;
        mSecondRatio = mSecond / 60f;
    }

    public void updateTimePerSecond() {
        getHandler().removeCallbacks(mRunnable);
        getHandler().post(mRunnable);
    }

    private void registerTimeZoneReceiver() {
        if (mRegisteredTimeZoneReceiver) {
            return;
        }
        mRegisteredTimeZoneReceiver = true;

        IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
        mContext.registerReceiver(mTimeZoneReceiver, filter);
    }

    private void unregisterTimeZoneReceiver() {
        if (mRegisteredTimeZoneReceiver) {
            mRegisteredTimeZoneReceiver = false;
            mContext.unregisterReceiver(mTimeZoneReceiver);
        }
    }

    private void registerTimeChangeReceiver() {
        if (mRegisteredTimeChangeReceiver) {
            return;
        }
        mRegisteredTimeChangeReceiver = true;

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        mContext.registerReceiver(mTimeChangeReceiver, intentFilter);
    }

    private void unregisterTimeChangeReceiver() {
        if (mRegisteredTimeChangeReceiver) {
            mRegisteredTimeChangeReceiver = false;
            mContext.unregisterReceiver(mTimeChangeReceiver);
        }
    }

    protected void registerBatteryChangeReceiver() {
        if (mRegisteredBatteryChangeReceiver) {
            return;
        }
        mRegisteredBatteryChangeReceiver = true;

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        mContext.registerReceiver(mBatteryReceiver, intentFilter);
    }

    protected void unregisterBatteryChangeReceiver() {
        if (mRegisteredBatteryChangeReceiver) {
            mRegisteredBatteryChangeReceiver = false;
            mContext.unregisterReceiver(mBatteryReceiver);
        }
    }

    public boolean isEditMode() {
        return mIsEditMode;
    }

    public void setEditMode(boolean editMode) {
        mIsEditMode = editMode;
    }

    public void setDimMode(boolean dimMode) {
        mIsDimMode = dimMode;
    }

    /**
     * 每分钟或时间改变（如修改设置里的时间）时调用该方法
     */
    protected void onTimeTick() {
    }

}