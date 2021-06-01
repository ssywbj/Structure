package com.wiz.watch.facepeach;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.structure.wallpaper.basic.WatchFaceView;
import com.structure.wallpaper.basic.utils.DateUtil;

import java.lang.ref.WeakReference;
import java.util.Calendar;

public class TimeView extends View {
    private static final String TAG = "TimeView";

    private static final int UP_TIME_ANIMATION_WHAT = 0x01;

    private static final int UP_TIME_WHAT = 0x02;

    private static final long UP_TIME_ANIMATION_INTERVAL = 50L;

    private static final int DEFAULT_HOUR = 8;
    private static final int DEFAULT_MINUTE = 36;
    private int mHour = -1;
    private int mMinute = -1;

    private static final int DEFAULT_NUMBER_COLOR = 0xFFFFFFFF;

    private int mNumberColor = DEFAULT_NUMBER_COLOR;

    private PeachBitmapManager mBitmapManager;

    private int mWidthView, mHeightView;

    private Paint mPaint;

    private boolean mRegisteredTimeChangeReceiver;

    private boolean isFirstCreateView = true;

    private final UIHandler mUIHandler = new UIHandler(this);

    private BroadcastReceiver mTimeChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "mTimeChangeReceiver");
            updateTime();
        }
    };


    public TimeView(Context context) {
        super(context);
        init(null);
    }

    public TimeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setFilterBitmap(true);
        mBitmapManager = new PeachBitmapManager(getContext());
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.TimeView);
        int count = a.getIndexCount();
        for (int i = 0; i < count; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.TimeView_time_color:
                    mNumberColor = a.getColor(attr, DEFAULT_NUMBER_COLOR);
                    break;
            }
        }
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        mWidthView = MeasureSpec.getSize(widthMeasureSpec);
        mHeightView = MeasureSpec.getSize(heightMeasureSpec);
        Log.d(TAG, "onMeasure mWidthView = " + mWidthView + ",mHeightView = " + mHeightView + ",wMode = " + wMode + ",hMode = " + hMode);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.TRANSPARENT);
        if (mHour == -1 || mMinute == -1) {
            drawTime(canvas, 0, 8, 3, 6);
        } else {
            int hourTen = mHour / 10;
            int hourUnit = mHour % 10;
            int minuTen = mMinute / 10;
            int minuUnit = mMinute % 10;
            drawTime(canvas, hourTen, hourUnit, minuTen, minuUnit);
        }

    }

    private void drawTime(Canvas canvas, int hourTen, int hourUnit, int minuTen, int minuUnit) {
        float offset = 0;
        float dateBitmapLenght = 0;
        Bitmap bitmap = mBitmapManager.getPunctuation(':', 0, mNumberColor);
        dateBitmapLenght += bitmap.getWidth();
        bitmap = mBitmapManager.getNumber(hourTen, 0, mNumberColor);
        dateBitmapLenght += bitmap.getWidth() * 4;
        float scale = mWidthView / dateBitmapLenght;
        canvas.scale(scale, scale);

        canvas.drawBitmap(bitmap, offset, 0, mPaint);
        offset += bitmap.getWidth();

        bitmap = mBitmapManager.getNumber(hourUnit, 0, mNumberColor);
        canvas.drawBitmap(bitmap, offset, 0, mPaint);
        offset += bitmap.getWidth();

        bitmap = mBitmapManager.getPunctuation(':', 0, mNumberColor);
        canvas.drawBitmap(bitmap, offset, 0, mPaint);
        offset += bitmap.getWidth();

        bitmap = mBitmapManager.getNumber(minuTen, 0, mNumberColor);
        canvas.drawBitmap(bitmap, offset, 0, mPaint);
        offset += bitmap.getWidth();

        bitmap = mBitmapManager.getNumber(minuUnit, 0, mNumberColor);
        canvas.drawBitmap(bitmap, offset, 0, mPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidthView = w;
        mHeightView = h;
        Log.d(TAG, "onSizeChanged w = " + w + ",h = " + h + ",oldw = " + oldw + ",oldh = " + oldh);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.d(TAG, "onAttachedToWindow");
    }

    private void registerTimeChangeReceiver() {
        if (mRegisteredTimeChangeReceiver)
            return;
        mRegisteredTimeChangeReceiver = true;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        getContext().registerReceiver(mTimeChangeReceiver, intentFilter);
    }

    private void unRegisterTimeChangeReceiver() {
        if (!mRegisteredTimeChangeReceiver)
            return;
        mRegisteredTimeChangeReceiver = false;
        getContext().unregisterReceiver(mTimeChangeReceiver);
    }

    public void updateTime() {
        mHour = DateUtil.getHour(getContext());
        mMinute = Calendar.getInstance().get(Calendar.MINUTE);
        invalidate();
    }

    public void startListenTime() {
        if (isFirstCreateView) {
            if (!mUIHandler.hasMessages(UP_TIME_ANIMATION_WHAT)) {
                mUIHandler.sendEmptyMessageDelayed(UP_TIME_ANIMATION_WHAT, UP_TIME_ANIMATION_INTERVAL);
            }
        } else {
            registerTimeChangeReceiver();
            mUIHandler.sendEmptyMessage(UP_TIME_WHAT);
        }
    }

    public void stopListenTime() {
        unRegisterTimeChangeReceiver();
    }

    private boolean upTimeAnimation() {
        if (mHour == -1 || mMinute == -1) {
            mHour = DEFAULT_HOUR;
            mMinute = DEFAULT_MINUTE;
        }
        int hour = DateUtil.getHour(getContext());
        int minute = Calendar.getInstance().get(Calendar.MINUTE);
        Log.d(TAG, "upTimeAnimation hour = " + hour + ",minute = " + minute);
        if (hour < mHour) {
            mHour--;
        } else if (hour > mHour) {
            mHour++;
        }
        if (minute < mMinute) {
            mMinute--;
        } else if (minute > mMinute) {
            mMinute++;
        }
        if (hour == mHour && minute == mMinute) {
            return false;
        }
        return true;
    }

    private static class UIHandler extends Handler {
        private WeakReference<TimeView> reference;

        public UIHandler(TimeView timeView) {
            super(Looper.getMainLooper());
            reference = new WeakReference<>(timeView);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (reference == null)
                return;
            TimeView timeView = reference.get();
            if (timeView == null)
                return;
            switch (msg.what) {
                case UP_TIME_ANIMATION_WHAT:
                    if (timeView.upTimeAnimation()) {
                        sendEmptyMessageDelayed(UP_TIME_ANIMATION_WHAT, UP_TIME_ANIMATION_INTERVAL);
                    } else {
                        timeView.isFirstCreateView = false;
                        timeView.registerTimeChangeReceiver();
                    }
                    timeView.invalidate();
                    break;
                case UP_TIME_WHAT:
                    timeView.updateTime();
                    break;
            }

        }
    }
}
