package com.suheng.wallpaper.basic.service;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

import com.suheng.wallpaper.basic.utils.DateUtil;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public abstract class CanvasWallpaperService extends WallpaperService {
    private static final long UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1);

    protected Context mContext;
    protected String mTAG;
    private Handler mHandler;
    protected int mHour, mMinute, mSecond;
    protected float mHourRatio, mMinuteRatio, mSecondRatio;
    protected boolean mIsHour24Scale;

    public abstract class CanvasEngine extends Engine {

        public abstract void onDraw(Canvas canvas);

        private final Runnable mRunnableSecondTicker = new Runnable() {
            @Override
            public void run() {
                updateTime();
                invalidate();

                long delayMillis = UPDATE_RATE_MS - (System.currentTimeMillis() % UPDATE_RATE_MS);
                mHandler.postDelayed(mRunnableSecondTicker, delayMillis);
            }
        };

        public void invalidate() {
            SurfaceHolder surfaceHolder = getSurfaceHolder();
            Canvas canvas = null;
            try {
                canvas = surfaceHolder.lockCanvas();
                if (canvas != null) {
                    this.onDraw(canvas);
                }
            } catch (Exception e) {
                Log.e(mTAG, "draw live wallpaper exception: " + e, new Exception());
            } finally {
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }

        protected void registerSecondTicker() {
            this.unregisterSecondTicker();
            mHandler.post(mRunnableSecondTicker);
        }

        protected void unregisterSecondTicker() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (mHandler.hasCallbacks(mRunnableSecondTicker)) {
                    mHandler.removeCallbacks(mRunnableSecondTicker);
                }
            } else {
                mHandler.removeCallbacks(mRunnableSecondTicker);
            }
        }

        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            Log.d(mTAG, "onCreate, surfaceHolder = " + surfaceHolder);
        }

        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            Log.d(mTAG, "onSurfaceChanged, format = " + format + ", width = " + width
                    + ", height = " + height + ", holder = " + holder);
        }

        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            Log.d(mTAG, "onVisibilityChanged, visible = " + visible + ", holder = " + getSurfaceHolder());
        }

        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            Log.d(mTAG, "onSurfaceDestroyed, holder: " + holder);
        }

        @Override
        public Bundle onCommand(String action, int x, int y, int z, Bundle extras, boolean resultRequested) {
            super.onCommand(action, x, y, z, extras, resultRequested);
            if ("com.google.android.wearable.action.BACKGROUND_ACTION".equals(action)) {
                boolean ambientMode = extras.getBoolean("ambient_mode", false);
                Log.d(mTAG, "onCommand, ambientMode = " + ambientMode);
            } else if ("com.google.android.wearable.action.AMBIENT_UPDATE".equals(action)) {
                Log.d(mTAG, "onCommand, action = " + action);
            } else if ("android.wallpaper.tap".equals(action)) {
                Log.d(mTAG, "onCommand, isPreview = " + isPreview() + ", isRestricted = " + isRestricted());
            }
            Log.d(mTAG, "onCommand, action = " + action + ", x = " + x + ", y = " + y
                    + ", extras = " + extras + ", resultRequested = " + resultRequested);
            return extras;
        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {
            super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep, xPixelOffset, yPixelOffset);
            Log.d(mTAG, "onOffsetsChanged, xOffset = " + xOffset + ", yOffset = " + yOffset + ", xOffsetStep " + xOffsetStep
                    + ", yOffsetStep = " + yOffsetStep + ", xPixelOffset = " + xPixelOffset + ", yPixelOffset = " + yPixelOffset);
        }

        @Override
        public void onDesiredSizeChanged(int desiredWidth, int desiredHeight) {
            super.onDesiredSizeChanged(desiredWidth, desiredHeight);
            Log.d(mTAG, "onDesiredSizeChanged, desiredWidth = " + desiredWidth + ", desiredHeight = " + desiredHeight);
        }

        /*@Override
        public void onTouchEvent(MotionEvent event) {
            super.onTouchEvent(event);
            Log.d(mTAG, "onTouchEvent, action = " + event.getAction());
        }*/
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        mTAG = getClass().getSimpleName();
        mHandler = new Handler();
        Log.d(mTAG, "onCreate, service: " + this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(mTAG, "onDestroy");
    }

    public void updateTime() {
        Calendar calendar = Calendar.getInstance();

        mHour = mIsHour24Scale ? calendar.get(Calendar.HOUR_OF_DAY) : DateUtil.getHour(this);
        mMinute = calendar.get(Calendar.MINUTE);
        mSecond = calendar.get(Calendar.SECOND);

        mHourRatio = ((mIsHour24Scale ? mHour : mHour % 12) + mMinute / 60f) / (mIsHour24Scale ? 24 : 12);
        mMinuteRatio = (mMinute + mSecond / 60f) / 60;
        mSecondRatio = (mSecond + calendar.get(Calendar.MILLISECOND) / 1000f) / 60f;
    }

    /*public CanvasHandler getHandler() {
        return mHandler;
    }*/
}
