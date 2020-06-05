package com.suheng.structure.wallpaper.myhealth;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
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

public class MyHealthWatchFace extends WallpaperService {
    public static final String TAG = MyHealthWatchFace.class.getSimpleName();

    @Override
    public Engine onCreateEngine() {
        return new LiveWallpaperEngine();
    }

    private final class LiveWallpaperEngine extends Engine {
        private PointF mPointScreenCenter = new PointF();//屏幕中心点
        private float mRadiusScreen;

        private Paint mPaint;
        private RectF mRectF = new RectF();

        private Context mContext;
        private boolean mVisible;
        private boolean mAmbientMode;

        private MyHealthBitmapManager mBitmapManager;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            Log.d(TAG, "onCreate, surfaceHolder = " + surfaceHolder);

            mContext = MyHealthWatchFace.this;

            mBitmapManager = new MyHealthBitmapManager(mContext);

            mPaint = new Paint();
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            Log.d(TAG, "onSurfaceChanged, format = " + format + ", width = " + width + ", height = " + height);
            mPointScreenCenter.x = 1.0f * width / 2;//屏幕中心X坐标
            mPointScreenCenter.y = 1.0f * height / 2;//屏幕中心Y坐标
            mRadiusScreen = Math.min(mPointScreenCenter.x, mPointScreenCenter.y)
                    - DimenUtil.dip2px(mContext, 3);//屏幕半径

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

            //屏幕中间红粗线
            mPaint.setAntiAlias(true);
            mPaint.setColor(Color.parseColor("#FF3333"));
            mPaint.setStyle(Paint.Style.FILL);
            float lineWidth = DimenUtil.dip2px(mContext, 6), lineLen = DimenUtil.dip2px(mContext, 196);
            mRectF.set(mPointScreenCenter.x - lineLen / 2, mPointScreenCenter.y - lineWidth / 2,
                    mPointScreenCenter.x + lineLen / 2, mPointScreenCenter.y + lineWidth / 2);
            canvas.drawRoundRect(mRectF, lineWidth, lineWidth, mPaint);

            this.paintDate(canvas);
            this.paintIconInfo(canvas);
            this.paintBattery(canvas);
        }

        private void paintIconInfo(Canvas canvas) {
            int color = R.color.basic_number_color;
            int temperature = 30;
            int units = temperature % 10;//个位
            int tens = temperature / 10;//十位
        }

        private void paintBattery(Canvas canvas) {
            int color = android.R.color.white;
            mPaint.setAntiAlias(true);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(ContextCompat.getColor(mContext, color));

            float percent = 1.0f * 160 / 360;
            int percentage = (int) (percent * 100);

            int units = percentage % 10;//个位
            int tens = percentage / 10;//十位

            int marginBottom = DimenUtil.dip2px(mContext, 11);
            float start = DimenUtil.dip2px(mContext, 45), maxLineLen = 2 * (mPointScreenCenter.x - start);
            float lineHeight = DimenUtil.dip2px(mContext, 3), lineLen = maxLineLen * percent;
            float right = start + lineLen;
            mRectF.set(start, 2 * mPointScreenCenter.y - lineHeight / 2 - marginBottom,
                    right, 2 * mPointScreenCenter.y + lineHeight / 2 - marginBottom);
            canvas.drawRoundRect(mRectF, lineHeight, lineHeight, mPaint);
        }

        private void paintDate(Canvas canvas) {
            /*canvas.drawLine(mPointScreenCenter.x, 0, mPointScreenCenter.x, 2 * mPointScreenCenter.y, mPaint);
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(mPointScreenCenter.x, mPointScreenCenter.y, mRadiusScreen, mPaint);*/

            mPaint.reset();
            int color = android.R.color.white;
            mPaint.setColor(getColor(color));
            mPaint.setShadowLayer(4, 0, 0, Color.parseColor("#2E42FF"));//外围阴影效果

            Calendar instance = Calendar.getInstance();
            int month = instance.get(Calendar.MONTH) + 1;
            int day = instance.get(Calendar.DAY_OF_MONTH);

            final float marginTop = DimenUtil.dip2px(mContext, 10);
            float top = mPointScreenCenter.y + marginTop;

            //号数
            int units = day % 10;//个位
            int tens = day / 10;//十位
            Bitmap bitmap = mBitmapManager.getMerge(mBitmapManager.getMiddleNumberResId(tens), color
                    , mBitmapManager.getMiddleNumberResId(units), color);
            float left = mPointScreenCenter.x + DimenUtil.dip2px(mContext, 1) - bitmap.getWidth();
            canvas.drawBitmap(bitmap.extractAlpha(), left, top, mPaint);

            //月份
            units = month % 10;//个位
            tens = month / 10;//十位
            bitmap = mBitmapManager.getMerge(mBitmapManager.getMiddleNumberResId(tens), color
                    , mBitmapManager.getMiddleNumberResId(units), color);
            left -= bitmap.getWidth();
            canvas.drawBitmap(bitmap.extractAlpha(), left, top, mPaint);

            mPaint.clearShadowLayer();

            mPaint.setShadowLayer(8, 0, 0, Color.parseColor("#2E42FF"));//外围阴影效果

            //分钟
            int minute = instance.get(Calendar.MINUTE);
            units = minute % 10;//个位
            tens = minute / 10;//十位
            bitmap = mBitmapManager.getMerge(mBitmapManager.getBigNumberResId(tens), color, mBitmapManager.getBigNumberResId(units), color);
            top = mPointScreenCenter.y - bitmap.getHeight() - marginTop - marginTop / 4;
            canvas.drawBitmap(bitmap.extractAlpha(), left, top, mPaint);

            //时钟
            int hour = instance.get(Calendar.HOUR);
            units = hour % 10;//个位
            tens = hour / 10;//十位
            bitmap = mBitmapManager.getMerge(mBitmapManager.getBigNumberResId(tens), color, mBitmapManager.getBigNumberResId(units), color);
            left = mPointScreenCenter.x - bitmap.getWidth() - DimenUtil.dip2px(mContext, 3);
            canvas.drawBitmap(bitmap.extractAlpha(), left, top, mPaint);

            mPaint.clearShadowLayer();
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
