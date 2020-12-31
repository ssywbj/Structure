package com.wiz.watch.dreamservice;

import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wiz.watch.dreamservice.utils.DateUtil;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class WatchFaceView extends View {
    public static final int MSG_UPDATE_TIME_PER_SECOND = 11;
    private static final long UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1);
    /**
     * 出场动画执行时间
     */
    public static final int ANIM_DURATION = 800;
    /**
     * 出场动画启动延时时间，目的是为了确保用初始值画完一次界面再启动动画
     */
    public static final int ANIM_DELAY = 100;
    /**
     * 当前电量百分比的值，如若是80%，那么level就是80
     */
    protected int mBatteryLevel;
    /**
     * 每秒钟更新一次时间并立即执行
     */
    private boolean mUpdateTimePerSecondImmediately;

    protected Context mContext;
    protected String mTAG;
    protected PointF mPointScreenCenter = new PointF();//屏幕中心点
    private boolean mReportedVisible;
    private final MyHandler mHandler = new MyHandler(this);
    private boolean mRegisteredTimeZoneReceiver, mRegisteredTimeChangeReceiver, mRegisteredBatteryChangeReceiver;
    protected boolean mIsEditMode;
    protected int mHour, mMinute, mSecond;
    protected float mHourAnimatorValue, mMinuteAnimatorValue, mSecondAnimatorValue;

    protected PaintFlagsDrawFilter mPaintFlagsDrawFilter;

    private final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(mTAG, "TimeZoneReceiver, action = " + intent.getAction());
            Calendar.getInstance().setTimeZone(TimeZone.getDefault());
            invalidate();
        }
    };

    private final BroadcastReceiver mTimeChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(mTAG, "TimeChangedReceiver, action = " + intent.getAction());
            onTimeTick();
        }
    };

    private final BroadcastReceiver mBatteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                mBatteryLevel = intent.getIntExtra("level", 0);//当前电量，百分比
                int total = intent.getIntExtra("scale", 100);//总电量，百分之百
                Log.d(mTAG, "battery, level = " + mBatteryLevel + ", total = " + total);
                invalidate();
            }
        }
    };

    private static final class MyHandler extends Handler {
        private final WeakReference<WatchFaceView> mWeakReference;

        private MyHandler(WatchFaceView reference) {
            mWeakReference = new WeakReference<>(reference);
        }

        @Override
        public void dispatchMessage(@NonNull Message msg) {
            super.dispatchMessage(msg);
            WatchFaceView engine = mWeakReference.get();
            if (engine != null) {
                if (msg.what == MSG_UPDATE_TIME_PER_SECOND) {
                    engine.handleUpdateTimePerSecond();
                } else {
                    engine.dispatchMsg(msg);
                }
            }
        }
    }

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
        mPaintFlagsDrawFilter = new PaintFlagsDrawFilter(0, Paint.FILTER_BITMAP_FLAG);
        mTAG = getClass().getSimpleName();
        //mTAG = "WatchFaceView";
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(mTAG, "onSizeChanged, w = " + w + ", h = " + h + ", oldw = " + oldw + ", oldh = " + oldh);
        mPointScreenCenter.x = w / 2f;//屏幕中心X坐标
        mPointScreenCenter.y = h / 2f;//屏幕中心Y坐标
    }

    public void onVisibilityChanged(boolean visible) {
        Log.d(mTAG, "onVisibilityChanged, visible = " + visible);
        mReportedVisible = visible;
        if (visible) {
            this.updateTime();

            this.registerTimeZoneReceiver();
            this.registerTimeChangeReceiver();

            Calendar.getInstance().setTimeZone(TimeZone.getDefault());
            invalidate();
        } else {
            this.unregisterTimeZoneReceiver();
            this.unregisterTimeChangeReceiver();
        }

        this.notifyMsgUpdateTimePerSecond();
    }

    public void updateTime() {
        mHour = getHour();
        mMinute = Calendar.getInstance().get(Calendar.MINUTE);
        mSecond = Calendar.getInstance().get(Calendar.SECOND);

        mHourAnimatorValue = (mHour + mMinute / 60f) / 12;
        mMinuteAnimatorValue = (mMinute + mSecond / 60f) / 60;
        mSecondAnimatorValue = mSecond / 60f;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.d(mTAG, "-----onAttachedToWindow-----");
        this.onVisibilityChanged(true);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.d(mTAG, "-----onDetachedFromWindow-----");
        this.destroy();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.setDrawFilter(mPaintFlagsDrawFilter);
    }

    public void destroy() {
        if (mReportedVisible) {
            this.onVisibilityChanged(false);
        }
    }

    public void notifyMsgUpdateTimePerSecond() {
        mHandler.removeMessages(MSG_UPDATE_TIME_PER_SECOND);
        if (this.isVisible() && mUpdateTimePerSecondImmediately) {
            mHandler.sendEmptyMessage(MSG_UPDATE_TIME_PER_SECOND);
        }
    }

    /**
     * 每秒钟更新一次时间
     */
    private void handleUpdateTimePerSecond() {
        this.updateTime();

        invalidate();
        if (this.isVisible()) {
            long delayMs = UPDATE_RATE_MS - (System.currentTimeMillis() % UPDATE_RATE_MS);
            mHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME_PER_SECOND, delayMs);
        }
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

    public int getHour() {
        return DateUtil.getHour(mContext);
    }

    protected void releaseAnim(ValueAnimator animator) {
        if (animator == null) {
            return;
        }
        if (animator.isRunning()) {
            animator.cancel();
        }
    }

    public void setUpdateTimePerSecondImmediately(boolean updateTimePerSecondImmediately) {
        mUpdateTimePerSecondImmediately = updateTimePerSecondImmediately;
    }

    public boolean isUpdateTimePerSecondImmediately() {
        return mUpdateTimePerSecondImmediately;
    }

    public Handler getHandler() {
        return mHandler;
    }

    public boolean isVisible() {
        return mReportedVisible;
    }

    public boolean isEditMode() {
        return mIsEditMode;
    }

    public void setEditMode(boolean editMode) {
        mIsEditMode = editMode;
    }

    /**
     * Handler分发消息
     */
    protected void dispatchMsg(Message msg) {
    }

    /**
     * 每分钟或时间改变（如修改设置里的时间）时调用该方法
     */
    protected void onTimeTick() {
        this.updateTime();
    }
}
