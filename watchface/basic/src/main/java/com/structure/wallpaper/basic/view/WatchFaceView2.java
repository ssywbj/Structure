package com.structure.wallpaper.basic.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.structure.wallpaper.basic.utils.DateUtil;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class WatchFaceView2 extends View {
    private static final long UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1);
    protected Context mContext;
    protected String mTAG;
    private boolean mRegisteredChangedZoneReceiver, mRegisteredTimeTickReceiver, mRegisteredBatteryChangeReceiver;
    protected boolean mIsEditMode, mIsDimMode, mIsHour24Scale;

    protected int mHour, mMinute, mSecond;
    protected float mHourRatio, mMinuteRatio, mSecondRatio;

    private final BroadcastReceiver mTimeTickReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Log.d(mTAG, "TimeTickReceiver: " + intent.getAction());
            onTimeTick();
        }
    };

    protected final Runnable mRunnableSecond = new Runnable() {
        @Override
        public void run() {
            updateTime();
            invalidate();

            if (getHandler() != null) {
                long delayMillis = UPDATE_RATE_MS - (System.currentTimeMillis() % UPDATE_RATE_MS);
                getHandler().postDelayed(mRunnableSecond, delayMillis);
            }
        }
    };

    private final BroadcastReceiver mTimeChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Log.d(mTAG, "TimeZoneReceiver: " + intent.getAction());
            Calendar.getInstance().setTimeZone(TimeZone.getDefault());
            onTimeChanged();
        }
    };

    private final BroadcastReceiver mBatteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                int level = intent.getIntExtra("level", 0);//当前电量，百分比的值
                onBatteryChange(level);
            }
        }
    };

    public WatchFaceView2(Context context) {
        super(context);
        this.initView();
    }

    public WatchFaceView2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.initView();
    }

    private void initView() {
        mContext = getContext();
        mTAG = getClass().getSimpleName();
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

    public void onVisibilityChanged(boolean visible) {
    }

    public void updateTime() {
        Calendar calendar = Calendar.getInstance();

        mHour = mIsHour24Scale ? calendar.get(Calendar.HOUR_OF_DAY) : DateUtil.getHour(mContext);
        mMinute = calendar.get(Calendar.MINUTE);
        mSecond = calendar.get(Calendar.SECOND);

        mHourRatio = ((mIsHour24Scale ? mHour : mHour % 12) + mMinute / 60f) / (mIsHour24Scale ? 24 : 12);
        mMinuteRatio = (mMinute + mSecond / 60f) / 60;
        mSecondRatio = (mSecond + calendar.get(Calendar.MILLISECOND) / 1000f) / 60f;
    }

    protected void registerSecondTicker() {
        if (getHandler() != null) {
            getHandler().removeCallbacks(mRunnableSecond);
            getHandler().post(mRunnableSecond);
        }
    }

    protected void unregisterSecondTicker() {
        if (getHandler() != null) {
            getHandler().removeCallbacks(mRunnableSecond);
        }
    }

    protected void registerTimeChangedReceiver() {
        if (mRegisteredChangedZoneReceiver) {
            return;
        }
        mRegisteredChangedZoneReceiver = true;

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        mContext.registerReceiver(mTimeChangedReceiver, filter);
    }

    protected void unregisterTimeChangedReceiver() {
        if (mRegisteredChangedZoneReceiver) {
            mRegisteredChangedZoneReceiver = false;
            mContext.unregisterReceiver(mTimeChangedReceiver);
        }
    }

    protected void registerTimeTickReceiver() {
        if (mRegisteredTimeTickReceiver) {
            return;
        }
        mRegisteredTimeTickReceiver = true;

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        mContext.registerReceiver(mTimeTickReceiver, intentFilter);
    }

    protected void unregisterTimeTickReceiver() {
        if (mRegisteredTimeTickReceiver) {
            mRegisteredTimeTickReceiver = false;
            mContext.unregisterReceiver(mTimeTickReceiver);
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

    protected int getBatteryLevel() {
        BatteryManager batterymanager = (BatteryManager) mContext.getSystemService(Context.BATTERY_SERVICE);
        if (batterymanager == null) {
            return 0;
        } else {
            return batterymanager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        }
    }

    protected boolean isScreenRound() {
        return mContext.getResources().getConfiguration().isScreenRound();
    }

    public void setEditMode(boolean editMode) {
        mIsEditMode = editMode;
    }

    public void setDimMode(boolean dimMode) {
        mIsDimMode = dimMode;
    }

    /**
     * 每分钟调用一次该方法。注：注册后生效
     */
    protected void onTimeTick() {
        updateTime();
        invalidate();
    }

    /**
     * 时间改变（如修改设置里的时间）时调用该方法。注：注册后生效
     */
    protected void onTimeChanged() {
        updateTime();
        invalidate();
    }

    /**
     * 电量改变时调用该方法。注：注册后生效
     */
    protected void onBatteryChange(int level) {
    }
}
