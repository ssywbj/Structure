package com.suheng.wallpaper.roamimg;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.SurfaceHolder;

import androidx.core.content.ContextCompat;

import com.suheng.wallpaper.basic.DimenUtil;
import com.suheng.wallpaper.basic.service.CanvasWallpaperService;
import com.suheng.wallpaper.basic.utils.DateUtil;

import java.util.Calendar;
import java.util.TimeZone;

public class RoamingWatchFace extends CanvasWallpaperService {

    @Override
    public Engine onCreateEngine() {
        return new LiveEngine();
    }

    private final class LiveEngine extends CanvasEngine {
        private final PointF mPointScreenCenter = new PointF();//屏幕中心点
        private float mMarginHorizontal;

        private Paint mPaint;

        private Paint mPaintCity;
        private final Rect mRect = new Rect();
        private float mMarginTime;

        private RoamingBitmapManager mBitmapManager;
        private SharedPreferences mPrefs;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
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
            mPointScreenCenter.x = 1.0f * width / 2;//屏幕中心X坐标
            mPointScreenCenter.y = 1.0f * height / 2;//屏幕中心Y坐标
            mMarginHorizontal = DimenUtil.dip2px(mContext, 26);
            mMarginTime = DimenUtil.dip2px(mContext, 12);

            invalidate();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (visible) {
                registerReceiverTimeTick();
            } else {
                unregisterReceiverTimeTick();
            }
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            mBitmapManager.clear();
        }

        @Override
        public void updateTime() {
            Calendar calendar = Calendar.getInstance();

            mHour = DateUtil.getHour(mContext);
            mMinute = calendar.get(Calendar.MINUTE);
        }

        @Override
        public void onDraw(Canvas canvas) {
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
    }

}
