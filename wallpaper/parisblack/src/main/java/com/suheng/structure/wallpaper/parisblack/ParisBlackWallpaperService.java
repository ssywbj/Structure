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
import com.suheng.structure.wallpaper.basic.utils.BitmapManager;

import java.util.Calendar;

public class ParisBlackWallpaperService extends WallpaperService {
    public static final String TAG = ParisBlackWallpaperService.class.getSimpleName();

    @Override
    public Engine onCreateEngine() {
        return new LiveWallpaperEngine();
    }

    private final class LiveWallpaperEngine extends Engine {
        private PointF mPointScreenCenter = new PointF();//屏幕中心点
        private float mRadiusScreen;//刻度外半径长度

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
            mPaint.setAntiAlias(true);
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

            mPaint.setColor(Color.parseColor("#FF3333"));
            mPaint.setStyle(Paint.Style.FILL);
            float lineWidth = DimenUtil.dip2px(mContext, 6), lineLen = DimenUtil.dip2px(mContext, 184);
            mRectF.set(mPointScreenCenter.x - lineLen / 2, mPointScreenCenter.y - lineWidth / 2,
                    mPointScreenCenter.x + lineLen / 2, mPointScreenCenter.y + lineWidth / 2);
            canvas.drawRoundRect(mRectF, lineWidth, lineWidth, mPaint);

            this.paintDate(canvas);
            this.paintIconInfo(canvas);
            //this.paintBattery(canvas);
        }

        private void paintIconInfo(Canvas canvas) {
            int temperature = 30;
            int units = temperature % 10;//个位
            int tens = temperature / 10;//十位
            Bitmap bitmap = mBitmapManager.getMerge(mBitmapManager.getSmallNumberResId(tens)
                    , mBitmapManager.getSmallNumberResId(units), R.drawable.paint_temperature_unit);
            float left = mPointScreenCenter.x - 1.0f * bitmap.getWidth() / 2;
            float top = mPointScreenCenter.y + mRadiusScreen - bitmap.getHeight() - DimenUtil.dip2px(mContext, 30);
            canvas.drawBitmap(bitmap, left, top, null);

            bitmap = mBitmapManager.get(R.drawable.paint_weather_day_duoyun);
            left = mPointScreenCenter.x - 1.0f * bitmap.getWidth() / 2;
            top -= (bitmap.getHeight() + DimenUtil.dip2px(mContext, 2));
            canvas.drawBitmap(bitmap, left, top, null);
        }

        private void paintBattery(Canvas canvas) {
            mPaint.setStrokeWidth(DimenUtil.dip2px(mContext, 5));
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(Color.parseColor("#2D2D2D"));

            int margin = DimenUtil.dip2px(mContext, 8);
            float percent = 1.0f * 298 / 360;
            int percentage = (int) (percent * 100);
            float sweepAngle = percent * 360;
            canvas.drawArc(margin, margin, 2 * mPointScreenCenter.x - margin, 2 * mPointScreenCenter.y - margin
                    , -90, sweepAngle, false, mPaint);//-90：最顶部，0：最右侧，90：最底部，180：最左侧

            int units = percentage % 10;//个位
            int tens = percentage / 10;//十位

            Bitmap dst = mBitmapManager.getMerge(R.drawable.paint_number_8, R.drawable.paint_number_2);
            if (dst != null) {
                /*dst = BitmapManager.mergeLeftRight(dst, mBitmapManager.get(mContext, R.drawable.paint_sign_percentage, R.color.basic_number_color));
                if (dst != null) {
                    canvas.drawBitmap(dst, mPointScreenCenter.x - 1.0f * dst.getWidth() / 2,
                            (mPointScreenCenter.y - 1.0f * dst.getHeight()) / 2, null);
                }*/
            }

            if (dst == null) {
                return;
            }

            Log.d(TAG, "percentage = " + percentage + ", units = " + units + ", tens = " + tens);
            Bitmap bitmap;
            canvas.save();
            canvas.rotate(sweepAngle, mPointScreenCenter.x, mPointScreenCenter.y);//旋转后的图片变模糊？
            bitmap = BitmapManager.rotate(dst, -sweepAngle);
            canvas.drawBitmap(bitmap, mPointScreenCenter.x, 0, null);
            canvas.restore();
        }

        private void paintDate(Canvas canvas) {
            /*canvas.drawLine(mPointScreenCenter.x, 0, mPointScreenCenter.x, 2 * mPointScreenCenter.y, mPaint);
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(mPointScreenCenter.x, mPointScreenCenter.y, mRadiusScreen, mPaint);*/

            int color = android.R.color.white;

            Calendar instance = Calendar.getInstance();
            int month = instance.get(Calendar.MONTH) + 1;
            int day = instance.get(Calendar.DAY_OF_MONTH);
            int week = instance.get(Calendar.DAY_OF_WEEK);

            Bitmap bitmap = mBitmapManager.get(R.drawable.paint_text_day_middle, color);
            final float marginTop = DimenUtil.dip2px(mContext, 10);
            float left = mPointScreenCenter.x;
            float top = mPointScreenCenter.y + marginTop;
            canvas.drawBitmap(bitmap, left, top, null);
            left += bitmap.getWidth();

            //星期
            bitmap = mBitmapManager.getMerge(R.drawable.paint_text_week_middle, color, mBitmapManager.getWeekResId(), color);
            left += DimenUtil.dip2px(mContext, 7);
            canvas.drawBitmap(bitmap, left, top, null);

            //号数
            int units = day % 10;//个位
            int tens = day / 10;//十位
            bitmap = mBitmapManager.getMerge(mBitmapManager.getMiddleNumberResId(tens), color
                    , mBitmapManager.getMiddleNumberResId(units), color);
            left = mPointScreenCenter.x - bitmap.getWidth();
            canvas.drawBitmap(bitmap, left, top, null);

            //月
            bitmap = mBitmapManager.get(R.drawable.paint_text_month_middle, color);
            left -= bitmap.getWidth();
            canvas.drawBitmap(bitmap, left, top, null);
            //月份
            units = month % 10;//个位
            tens = month / 10;//十位
            bitmap = mBitmapManager.getMerge(mBitmapManager.getMiddleNumberResId(tens), color
                    , mBitmapManager.getMiddleNumberResId(units), color);
            left -= bitmap.getWidth();
            canvas.drawBitmap(bitmap, left, top, null);

            Log.d(TAG, "date, month = " + month + ", day = " + day + ", week = " + week
                    + ", tens = " + tens + ", units = " + units);

            //冒号
            bitmap = mBitmapManager.get(R.drawable.paint_sign_colon, color);
            float colonWidth = 1.0f * bitmap.getWidth();
            top = mPointScreenCenter.y - bitmap.getHeight() - marginTop - marginTop / 2;
            canvas.drawBitmap(bitmap, mPointScreenCenter.x - colonWidth / 2, top, null);
            left = mPointScreenCenter.x + colonWidth / 2;

            //分钟
            int minute = instance.get(Calendar.MINUTE);
            units = minute % 10;//个位
            tens = minute / 10;//十位
            bitmap = mBitmapManager.getMerge(mBitmapManager.getBigNumberResId(tens), color, mBitmapManager.getBigNumberResId(units), color);
            top = mPointScreenCenter.y - bitmap.getHeight() - marginTop - marginTop / 4;
            canvas.drawBitmap(bitmap, left, top, null);

            //时钟
            int hour = instance.get(Calendar.HOUR);
            units = hour % 10;//个位
            tens = hour / 10;//十位
            bitmap = mBitmapManager.getMerge(mBitmapManager.getBigNumberResId(tens), color, mBitmapManager.getBigNumberResId(units), color);
            left = mPointScreenCenter.x - colonWidth / 2 - bitmap.getWidth() - DimenUtil.dip2px(mContext, 3);
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
