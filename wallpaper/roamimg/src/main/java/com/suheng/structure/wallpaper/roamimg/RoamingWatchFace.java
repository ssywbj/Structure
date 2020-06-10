package com.suheng.structure.wallpaper.roamimg;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.suheng.structure.wallpaper.basic.DimenUtil;

import java.util.Calendar;

public class RoamingWatchFace extends WallpaperService {
    public static final String TAG = RoamingWatchFace.class.getSimpleName();

    @Override
    public Engine onCreateEngine() {
        return new LiveWallpaperEngine();
    }

    private final class LiveWallpaperEngine extends Engine {
        private static final int SCALES = 6;
        private static final float SCALE_DEGREES = 1.0f * 360 / SCALES;
        private PointF mPointScreenCenter = new PointF();//屏幕中心点
        private float mRadiusOuter, mRadiusInner;

        private Paint mPaint;
        private RectF mRectF = new RectF();

        private Context mContext;
        private boolean mVisible;
        private boolean mAmbientMode;

        private RoamingBitmapManager mBitmapManager;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            Log.d(TAG, "onCreate, surfaceHolder = " + surfaceHolder);

            mContext = RoamingWatchFace.this;

            mBitmapManager = new RoamingBitmapManager(mContext);

            mPaint = new Paint();
            mPaint.setAntiAlias(true);
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            Log.d(TAG, "onSurfaceChanged, format = " + format + ", width = " + width + ", height = " + height);
            mPointScreenCenter.x = 1.0f * width / 2;//屏幕中心X坐标
            mPointScreenCenter.y = 1.0f * height / 2;//屏幕中心Y坐标
            mRadiusOuter = Math.min(mPointScreenCenter.x, mPointScreenCenter.y)
                    - DimenUtil.dip2px(mContext, 1);
            mRadiusInner = mRadiusOuter - DimenUtil.dip2px(mContext, 36);

            this.invalidate();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            Log.i(TAG, "onVisibilityChanged, visible = " + visible);
            mVisible = visible;
            Intent intent = new Intent("com.google.android.wearable.watchfaces.action.REQUEST_STATE");
            sendBroadcast(intent);
            if (visible) {
                this.invalidate();
            } else {
                mHandler.removeMessages(111);
            }
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            Log.i(TAG, "onSurfaceDestroyed");
            mVisible = false;
            mHandler.removeMessages(111);

            mBitmapManager.clear();
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            Log.i(TAG, "onDestroy");
            mVisible = false;
            mHandler.removeMessages(111);

            mBitmapManager.clear();
        }

        @Override
        public Bundle onCommand(String action, int x, int y, int z, Bundle extras, boolean resultRequested) {
            super.onCommand(action, x, y, z, extras, resultRequested);
            if (action.matches("com.google.android.wearable.action.BACKGROUND_ACTION")) {
                mAmbientMode = extras.getBoolean("ambient_mode", false);
                if (mAmbientMode) {
                    this.invalidate();
                } else if (mVisible) {//Redraw digital clock in green during non-ambient mode
                    this.invalidate();
                }
            } else if (action.matches("com.google.android.wearable.action.AMBIENT_UPDATE")) {
                this.invalidate();
            }
            Log.i(TAG, "onCommand, action = " + action + ", x = " + x + ", y = " + y + ", ambient_mode = " + mAmbientMode);
            return extras;
        }

        private void invalidate() {
            SurfaceHolder surfaceHolder = getSurfaceHolder();
            Canvas canvas = null;
            try {
                canvas = surfaceHolder.lockCanvas();
                if (canvas != null) {
                    this.onDraw(canvas);
                }
            } finally {
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }

            mHandler.removeMessages(111);
            mHandler.sendEmptyMessageDelayed(111, 1000);
        }

        private void onDraw(Canvas canvas) {
            canvas.drawColor(ContextCompat.getColor(mContext, R.color.basic_wallpaper_bg_black));//画面背景
            this.paintTime(canvas);
        }

        private void paintTime(Canvas canvas) {
            Calendar instance = Calendar.getInstance();
            //分钟
            int color = android.R.color.white;
            int minute = instance.get(Calendar.MINUTE);
            int units = minute % 10;//个位
            int tens = minute / 10;//十位
            Bitmap bitmap = mBitmapManager.getMerge(mBitmapManager.getBigNumberResId(tens), color, mBitmapManager.getBigNumberResId(units), color);
            float left = mPointScreenCenter.x - 1.0f * bitmap.getWidth() / 2;
            float hourBitmapHeight = 1.0f * bitmap.getHeight() / 2;
            float top = mPointScreenCenter.y - hourBitmapHeight;
            canvas.drawBitmap(bitmap, left, top, null);
        }

        private final Handler mHandler = new Handler() {
            @Override
            public void dispatchMessage(@NonNull Message msg) {
                super.dispatchMessage(msg);
                invalidate();
            }
        };
    }

}
