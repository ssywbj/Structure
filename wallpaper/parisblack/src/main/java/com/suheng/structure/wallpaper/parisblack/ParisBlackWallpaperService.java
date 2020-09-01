package com.suheng.structure.wallpaper.parisblack;

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

public class ParisBlackWallpaperService extends WallpaperService {
    public static final String TAG = ParisBlackWallpaperService.class.getSimpleName();

    @Override
    public Engine onCreateEngine() {
        return new LiveWallpaperEngine();
    }

    private final class LiveWallpaperEngine extends Engine {
        private PointF mPointScreenCenter = new PointF();//屏幕中心点
        private float mRadiusOuter;

        private Paint mPaint;
        private RectF mRectF = new RectF();

        private Context mContext;
        private boolean mVisible;
        private boolean mAmbientMode;

        private ParisBitmapManager mBitmapManager;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            Log.d(TAG, "onCreate, surfaceHolder = " + surfaceHolder);

            mContext = ParisBlackWallpaperService.this;

            mBitmapManager = new ParisBitmapManager(mContext);

            mPaint = new Paint();
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            Log.d(TAG, "onSurfaceChanged, format = " + format + ", width = " + width + ", height = " + height);
            mPointScreenCenter.x = 1.0f * width / 2;//屏幕中心X坐标
            mPointScreenCenter.y = 1.0f * height / 2;//屏幕中心Y坐标
            mRadiusOuter = Math.min(mPointScreenCenter.x, mPointScreenCenter.y)
                    - DimenUtil.dip2px(mContext, 3);//绘制半径

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

            Bitmap bitmap = mBitmapManager.get(R.drawable.paint_temperature_unit, color);
            float left = mPointScreenCenter.x;
            float top = mPointScreenCenter.y + mRadiusOuter - bitmap.getHeight() - DimenUtil.dip2px(mContext, 30);
            canvas.drawBitmap(bitmap, left, top, null);
            bitmap = mBitmapManager.get(mBitmapManager.getSmallNumberResId(units), color);
            left -= bitmap.getWidth();
            canvas.drawBitmap(bitmap, left, top, null);
            bitmap = mBitmapManager.get(mBitmapManager.getSmallNumberResId(tens), color);
            left -= bitmap.getWidth();
            canvas.drawBitmap(bitmap, left, top, null);

            bitmap = mBitmapManager.get(R.drawable.paint_weather_day_duoyun, color);
            left = mPointScreenCenter.x - 1.0f * bitmap.getWidth() / 2 - DimenUtil.dip2px(mContext, 2);
            top -= (bitmap.getHeight() + DimenUtil.dip2px(mContext, 2));
            canvas.drawBitmap(bitmap, left, top, null);
        }

        private void paintBattery(Canvas canvas) {
            int color = R.color.text_battery;
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

            Bitmap bitmap = mBitmapManager.get(R.drawable.paint_sign_percentage, color);
            float left = right - bitmap.getWidth() + DimenUtil.dip2px(mContext, 4);
            float top = mPointScreenCenter.y * 2 - bitmap.getHeight() - marginBottom - DimenUtil.dip2px(mContext, 6);
            canvas.drawBitmap(bitmap, left, top, null);
            bitmap = mBitmapManager.get(mBitmapManager.getSmallNumberResId(units), color);
            canvas.drawBitmap(bitmap, left -= bitmap.getWidth(), top, null);
            bitmap = mBitmapManager.get(mBitmapManager.getSmallNumberResId(tens), color);
            canvas.drawBitmap(bitmap, left - bitmap.getWidth(), top, null);
        }

        private void paintDate(Canvas canvas) {
            mPaint.reset();
            int color = android.R.color.white;
            mPaint.setColor(getColor(color));
            mPaint.setShadowLayer(4, 0, 0, Color.parseColor("#2E42FF"));//外围阴影效果

            Calendar instance = Calendar.getInstance();
            int month = instance.get(Calendar.MONTH) + 1;
            int day = instance.get(Calendar.DAY_OF_MONTH);

            Bitmap bitmap = mBitmapManager.get(R.drawable.paint_text_day_middle, color);
            final float marginTop = DimenUtil.dip2px(mContext, 10);
            float left = mPointScreenCenter.x + DimenUtil.dip2px(mContext, 1);
            float top = mPointScreenCenter.y + marginTop;
            canvas.drawBitmap(bitmap.extractAlpha(), left, top, mPaint);//extractAlpha()：只抽取有透明度的位图用于绘制，否则画笔不生效
            left += bitmap.getWidth();

            //星期
            bitmap = mBitmapManager.get(R.drawable.paint_text_week_middle, color);
            left += DimenUtil.dip2px(mContext, 7);
            canvas.drawBitmap(bitmap.extractAlpha(), left, top, mPaint);
            left += bitmap.getWidth();
            bitmap = mBitmapManager.get(mBitmapManager.getWeekResId(), color);
            canvas.drawBitmap(bitmap.extractAlpha(), left, top, mPaint);

            //号数
            int units = day % 10;//个位
            int tens = day / 10;//十位
            bitmap = mBitmapManager.get(mBitmapManager.getMiddleNumberResId(units), color);
            left = mPointScreenCenter.x + DimenUtil.dip2px(mContext, 1) - bitmap.getWidth();
            canvas.drawBitmap(bitmap.extractAlpha(), left, top, mPaint);
            bitmap = mBitmapManager.get(mBitmapManager.getMiddleNumberResId(tens), color);
            canvas.drawBitmap(bitmap.extractAlpha(), left -= bitmap.getWidth(), top, mPaint);

            //月
            bitmap = mBitmapManager.get(R.drawable.paint_text_month_middle, color);
            canvas.drawBitmap(bitmap.extractAlpha(), left -= bitmap.getWidth(), top, mPaint);
            //月份
            units = month % 10;//个位
            tens = month / 10;//十位
            bitmap = mBitmapManager.get(mBitmapManager.getMiddleNumberResId(units), color);
            canvas.drawBitmap(bitmap.extractAlpha(), left -= bitmap.getWidth(), top, mPaint);
            bitmap = mBitmapManager.get(mBitmapManager.getMiddleNumberResId(tens), color);
            canvas.drawBitmap(bitmap.extractAlpha(), left - bitmap.getWidth(), top, mPaint);

            mPaint.clearShadowLayer();

            mPaint.setShadowLayer(8, 0, 0, Color.parseColor("#2E42FF"));//外围阴影效果

            //冒号
            bitmap = mBitmapManager.get(R.drawable.paint_sign_colon, color);
            float colonWidth = 1.0f * bitmap.getWidth();
            top = mPointScreenCenter.y - bitmap.getHeight() - marginTop - marginTop / 2;
            canvas.drawBitmap(bitmap.extractAlpha(), mPointScreenCenter.x - colonWidth / 2, top, mPaint);
            left = mPointScreenCenter.x + colonWidth / 1.5f;

            //分钟
            int minute = instance.get(Calendar.MINUTE);
            units = minute % 10;//个位
            tens = minute / 10;//十位
            bitmap = mBitmapManager.get(mBitmapManager.getBigNumberResId(tens), color);
            top = mPointScreenCenter.y - bitmap.getHeight() - marginTop - marginTop / 4;
            canvas.drawBitmap(bitmap.extractAlpha(), left, top, mPaint);
            left += bitmap.getWidth();
            bitmap = mBitmapManager.get(mBitmapManager.getBigNumberResId(units), color);
            canvas.drawBitmap(bitmap.extractAlpha(), left, top, mPaint);

            //时钟
            int hour = instance.get(Calendar.HOUR);
            units = hour % 10;//个位
            tens = hour / 10;//十位
            bitmap = mBitmapManager.get(mBitmapManager.getBigNumberResId(units), color);
            left = mPointScreenCenter.x - colonWidth / 2 - bitmap.getWidth() - DimenUtil.dip2px(mContext, 3);
            canvas.drawBitmap(bitmap.extractAlpha(), left, top, mPaint);
            left -= bitmap.getWidth();
            bitmap = mBitmapManager.get(mBitmapManager.getBigNumberResId(tens), color);
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