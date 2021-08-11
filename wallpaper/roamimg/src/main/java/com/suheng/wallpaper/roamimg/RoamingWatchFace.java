package com.suheng.wallpaper.roamimg;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.suheng.wallpaper.basic.DimenUtil;

import java.util.Calendar;
import java.util.TimeZone;

public class RoamingWatchFace extends WallpaperService {
    public static final String TAG = RoamingWatchFace.class.getSimpleName();

    @Override
    public Engine onCreateEngine() {
        return new LiveWallpaperEngine();
    }

    private final class LiveWallpaperEngine extends Engine {
        private PointF mPointScreenCenter = new PointF();//屏幕中心点
        private float mRadiusOuter, mRadiusInner;
        private float mMarginHorizontal, mMarginVertical;

        private Paint mPaint;

        private Paint mPaintCity;
        private Rect mRect = new Rect();
        private float mMarginTime;

        private Context mContext;
        private boolean mVisible;
        private boolean mAmbientMode;

        private RoamingBitmapManager mBitmapManager;
        private SharedPreferences mPrefs;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            Log.d(TAG, "onCreate, surfaceHolder = " + surfaceHolder);

            mContext = RoamingWatchFace.this;

            mBitmapManager = new RoamingBitmapManager(mContext);

            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setColor(Color.parseColor("#EB483F"));
            mPaint.setStyle(Paint.Style.FILL);

            mPaintCity = new Paint();
            mPaintCity.setAntiAlias(true);
            mPaintCity.setTypeface(Typeface.DEFAULT_BOLD);
            mPaintCity.setTextSize(DimenUtil.dip2px(mContext, 25));
            mPaintCity.setColor(ContextCompat.getColor(mContext, R.color.text_city));

            mPrefs = getSharedPreferences(RoamingWatchFaceConfigActivity.PREFS_FILE, MODE_PRIVATE);
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

            mMarginHorizontal = DimenUtil.dip2px(mContext, 26);
            mMarginVertical = DimenUtil.dip2px(mContext, 60);
            mMarginTime = DimenUtil.dip2px(mContext, 12);
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
            canvas.drawColor(ContextCompat.getColor(mContext, R.color.basic_wallpaper_bg_black), PorterDuff.Mode.CLEAR);//画面背景

            final float lineWidth = DimenUtil.dip2px(mContext, 3.5f);
            canvas.save();
            canvas.rotate(44f, mPointScreenCenter.x, mPointScreenCenter.y);
            canvas.drawRoundRect(mPointScreenCenter.x - lineWidth / 2, 0, mPointScreenCenter.x + lineWidth / 2
                    , 2 * mPointScreenCenter.y, lineWidth, lineWidth, mPaint);
            canvas.restore();

            this.paintCity1(canvas);
            this.paintCity2(canvas);
        }

        private void paintCity1(Canvas canvas) {
            //String city = "纽约";
            String city = mPrefs.getString(RoamingWatchFaceConfigActivity.PREFS_KEY_CITY, "纽约");
            mPaintCity.getTextBounds(city, 0, city.length(), mRect);
            canvas.drawText(city, 2 * mPointScreenCenter.x - mRect.width() - mMarginHorizontal,
                    mPointScreenCenter.y + mRect.height(), mPaintCity);

            //纽约，美国 GMT-4:00；伦敦，英国 GMT+1:00
            //String timeZone = "GMT-4:00";
            String timeZone = mPrefs.getString(RoamingWatchFaceConfigActivity.PREFS_KEY_GMT, "GMT-4:00");
            Calendar instance = Calendar.getInstance(TimeZone.getTimeZone(timeZone));

            //分
            int color = android.R.color.white;
            int minute = instance.get(Calendar.MINUTE);
            int units = minute % 10;//个位
            int tens = minute / 10;//十位
            Bitmap bitmap = mBitmapManager.get(mBitmapManager.getBigNumberResId(units), color);
            float left = 2 * mPointScreenCenter.x - 1.0f * bitmap.getWidth() - mMarginHorizontal;
            float top = mPointScreenCenter.y + mRect.height() + mMarginTime;
            canvas.drawBitmap(bitmap, left, top, null);
            bitmap = mBitmapManager.get(mBitmapManager.getBigNumberResId(tens), color);
            left -= bitmap.getWidth();
            canvas.drawBitmap(bitmap, left, top, null);

            //冒号
            bitmap = mBitmapManager.get(R.drawable.paint_sign_colon, color);
            left -= bitmap.getWidth();
            canvas.drawBitmap(bitmap, left, top, null);

            //时
            int hour = instance.get(Calendar.HOUR_OF_DAY);
            units = hour % 10;//个位
            tens = hour / 10;//十位
            bitmap = mBitmapManager.get(mBitmapManager.getBigNumberResId(units), color);
            left -= bitmap.getWidth();
            canvas.drawBitmap(bitmap, left, top, null);
            bitmap = mBitmapManager.get(mBitmapManager.getBigNumberResId(tens), color);
            left -= bitmap.getWidth();
            canvas.drawBitmap(bitmap, left, top, null);

            //星期
            color = R.color.text_city;
            top += (bitmap.getHeight() + mMarginTime);
            bitmap = mBitmapManager.get(mBitmapManager.getWeekResId(timeZone), color);
            left = 2 * mPointScreenCenter.x - 1.0f * bitmap.getWidth() - mMarginHorizontal - DimenUtil.dip2px(mContext, 3);
            canvas.drawBitmap(bitmap, left, top, null);
            bitmap = mBitmapManager.get(R.drawable.paint_text_week_middle, color);
            left -= bitmap.getWidth();
            canvas.drawBitmap(bitmap, left, top, null);

            //日
            bitmap = mBitmapManager.get(R.drawable.paint_text_day_middle, color);
            left -= (bitmap.getWidth() + DimenUtil.dip2px(mContext, 10));
            canvas.drawBitmap(bitmap, left, top, null);
            int day = instance.get(Calendar.DAY_OF_MONTH);
            units = day % 10;//个位
            tens = day / 10;//十位
            bitmap = mBitmapManager.get(mBitmapManager.getSmallNumberResId(units), color);
            left -= bitmap.getWidth();
            canvas.drawBitmap(bitmap, left, top, null);
            bitmap = mBitmapManager.get(mBitmapManager.getSmallNumberResId(tens), color);
            left -= bitmap.getWidth();
            canvas.drawBitmap(bitmap, left, top, null);

            //月
            int month = instance.get(Calendar.MONTH) + 1;
            bitmap = mBitmapManager.get(R.drawable.paint_text_month_middle, color);
            left -= bitmap.getWidth();
            canvas.drawBitmap(bitmap, left, top, null);
            units = month % 10;//个位
            tens = month / 10;//十位
            bitmap = mBitmapManager.get(mBitmapManager.getSmallNumberResId(units), color);
            left -= bitmap.getWidth();
            canvas.drawBitmap(bitmap, left, top, null);
            bitmap = mBitmapManager.get(mBitmapManager.getSmallNumberResId(tens), color);
            left -= bitmap.getWidth();
            canvas.drawBitmap(bitmap, left, top, null);
        }

        private void paintCity2(Canvas canvas) {
            String city = "北京";
            mPaintCity.getTextBounds(city, 0, city.length(), mRect);
            float top = mPointScreenCenter.y;
            canvas.drawText(city, mMarginHorizontal, top - DimenUtil.dip2px(mContext, 4), mPaintCity);
            top -= mRect.height();

            String timeZone = "GMT+8:00";
            Calendar instance = Calendar.getInstance(TimeZone.getTimeZone("GMT+8:00"));

            //时
            int color = android.R.color.white;
            int hour = instance.get(Calendar.HOUR_OF_DAY);
            int units = hour % 10;//个位
            int tens = hour / 10;//十位
            Bitmap bitmap = mBitmapManager.get(mBitmapManager.getBigNumberResId(tens), color);
            float left = mMarginHorizontal - DimenUtil.dip2px(mContext, 6);
            top -= (bitmap.getHeight() + mMarginTime);
            canvas.drawBitmap(bitmap, left, top, null);
            left += bitmap.getWidth();
            bitmap = mBitmapManager.get(mBitmapManager.getBigNumberResId(units), color);
            canvas.drawBitmap(bitmap, left, top, null);
            left += bitmap.getWidth();

            //冒号
            bitmap = mBitmapManager.get(R.drawable.paint_sign_colon, color);
            canvas.drawBitmap(bitmap, left, top, null);
            left += bitmap.getWidth();

            //分
            int minute = instance.get(Calendar.MINUTE);
            units = minute % 10;//个位
            tens = minute / 10;//十位
            bitmap = mBitmapManager.get(mBitmapManager.getBigNumberResId(tens), color);
            canvas.drawBitmap(bitmap, left, top, null);
            bitmap = mBitmapManager.get(mBitmapManager.getBigNumberResId(units), color);
            left += bitmap.getWidth();
            canvas.drawBitmap(bitmap, left, top, null);

            //月
            color = R.color.text_city;
            int month = instance.get(Calendar.MONTH) + 1;
            units = month % 10;//个位
            tens = month / 10;//十位
            bitmap = mBitmapManager.get(mBitmapManager.getSmallNumberResId(tens), color);
            top -= (bitmap.getHeight() + mMarginTime);
            left = mMarginHorizontal;
            canvas.drawBitmap(bitmap, left, top, null);
            left += bitmap.getWidth();
            bitmap = mBitmapManager.get(mBitmapManager.getSmallNumberResId(units), color);
            canvas.drawBitmap(bitmap, left, top, null);
            left += bitmap.getWidth();
            bitmap = mBitmapManager.get(R.drawable.paint_text_month_middle, color);
            canvas.drawBitmap(bitmap, left, top, null);
            left += bitmap.getWidth();

            //日
            int day = instance.get(Calendar.DAY_OF_MONTH);
            units = day % 10;//个位
            tens = day / 10;//十位
            bitmap = mBitmapManager.get(mBitmapManager.getSmallNumberResId(tens), color);
            canvas.drawBitmap(bitmap, left, top, null);
            left += bitmap.getWidth();
            bitmap = mBitmapManager.get(mBitmapManager.getSmallNumberResId(units), color);
            canvas.drawBitmap(bitmap, left, top, null);
            left += bitmap.getWidth();
            bitmap = mBitmapManager.get(R.drawable.paint_text_day_middle, color);
            canvas.drawBitmap(bitmap, left, top, null);
            left += (bitmap.getWidth() + DimenUtil.dip2px(mContext, 10));

            //周
            bitmap = mBitmapManager.get(R.drawable.paint_text_week_middle, color);
            canvas.drawBitmap(bitmap, left, top, null);
            left += bitmap.getWidth();
            bitmap = mBitmapManager.get(mBitmapManager.getWeekResId(timeZone), color);
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
