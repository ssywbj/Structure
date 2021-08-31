package com.suheng.wallpaper.simplepointer;

import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;

import androidx.core.content.ContextCompat;

import com.suheng.wallpaper.basic.service.AnimWallpaperService;
import com.suheng.wallpaper.basic.utils.DateUtil;
import com.suheng.wallpaper.basic.utils.DimenUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class SimplePointerWatchFace extends AnimWallpaperService {

    @Override
    public Engine onCreateEngine() {
        return new LiveEngine();
    }

    private final class LiveEngine extends AnimEngine {
        public static final String AUTHORITY = "com.wiz.watch.weather";
        public final Uri WEATHER_URI = Uri.parse("content://" + AUTHORITY + "/weathers");
        public final static String QUERY_WEATHER_INFO = "query_weather_info";

        private static final int SCALES = 12;//12个刻度

        private float mRadiusOuter;//刻度外半径长度
        private float mRadiusInner;//刻度内半径长度
        private float mMarginHorizontal, mMarginVertical;
        private float mTextOffset;
        private int mMarginIconText;//信息图标与其下方文案的留白

        private Paint mPaintMinute;
        private final RectF mRectFMinute = new RectF();
        private final RectF mRectFMinuteInner = new RectF()/*分针内部小黑棒*/;
        private float mMinutePointerWidth/*分针宽度*/, mMinutePointerInnerWidth;

        private final RectF mRectFHour = new RectF();
        private final RectF mRectFHourInner = new RectF();
        private float mHourPointerWidth/*时针宽度*/, mHourPointerInnerWidth;

        private Paint mPaintSecond;
        private final RectF mRectFSecond = new RectF();
        private float mSecondPointerWidth/*秒针宽度*/;

        private Paint mPaintCenterCircle;
        private int mCenterCircleRadius;

        private SimplePointerBitmapManager mBitmapManager;

        private int mWeatherIconRes = R.drawable.weather_no_data;
        private int mTemperatureUnitRes = R.drawable.temperature_unit;
        private int mTemperature = Integer.MAX_VALUE;
        protected PointF mPointScreenCenter = new PointF();//屏幕中心点
        private int mBatteryLevel;

        private final Picture mPicture = new Picture();
        private float mScaleDate = 0.45f, mScalePoint;
        private final PorterDuffXfermode mXfermode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);

        private final ContentObserver mContentObserver = new ContentObserver(null) {
            @Override
            public void onChange(boolean selfChange, Uri uri) {
                super.onChange(selfChange, uri);
                Log.d(mTAG, "onChange, selfChange: " + selfChange + ", uri: " + uri);
                queryWeatherInfo();
            }
        };

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            setDefaultTime(10, 10, 30);
            needAppearAnimPointer();

            mBitmapManager = new SimplePointerBitmapManager(mContext);

            mMarginIconText = DimenUtil.dip2px(mContext, 2);
            mMarginHorizontal = DimenUtil.dip2px(mContext, 3);

            mTextOffset = DimenUtil.dip2px(mContext, 6);

            mMinutePointerWidth = DimenUtil.dip2px(mContext, 5.6f);
            mMinutePointerInnerWidth = mMinutePointerWidth / 5;
            mHourPointerWidth = mMinutePointerWidth * 1.5f;
            mHourPointerInnerWidth = mHourPointerWidth / 5;
            mSecondPointerWidth = mHourPointerWidth / 3f;

            mPaintMinute = new Paint();
            mPaintMinute.setAntiAlias(true);
            mPaintMinute.setStyle(Paint.Style.FILL);
            int shadowColor = Color.parseColor("#2E42FF");
            mPaintMinute.setShadowLayer(mMinutePointerWidth, 0, 0, shadowColor);//外围阴影效果
            mPaintMinute.setColor(Color.WHITE);

            mPaintSecond = new Paint(mPaintMinute);
            mPaintSecond.setShadowLayer(mSecondPointerWidth, 0, 0, Color.parseColor("#FF2E2E"));
            mPaintSecond.setColor(Color.parseColor("#FF3333"));

            mCenterCircleRadius = DimenUtil.dip2px(mContext, 6f);
            mPaintCenterCircle = new Paint();
            mPaintCenterCircle.setAntiAlias(true);
            mPaintCenterCircle.setStyle(Paint.Style.FILL);
            mPaintCenterCircle.setColor(Color.WHITE);
            mPaintCenterCircle.setShadowLayer(mCenterCircleRadius, 0, 0, shadowColor);//外围阴影效果

            mScaleDate = 0.45f;
            Bitmap bitmapNumber = mBitmapManager.get(R.drawable.alphabet_uppercase_a);
            Bitmap bitmapPoint = mBitmapManager.get(R.drawable.sign_point);
            mScalePoint = bitmapNumber.getHeight() * mScaleDate / bitmapPoint.getHeight();
        }

        private void queryWeatherInfo() {
            try {
                Bundle extras = new Bundle();
                extras.putLong("current_time_millis", System.currentTimeMillis());
                Bundle bundle = mContext.getContentResolver().call(WEATHER_URI, QUERY_WEATHER_INFO, null, extras);
                if (bundle == null) {
                    Log.e(mTAG, "query weather info error: bundle is null");
                    return;
                }
                ArrayList<String> data = bundle.getStringArrayList(QUERY_WEATHER_INFO);
                if (data == null || data.size() == 0) {
                    Log.e(mTAG, "query weather info error, data is null or empty");
                    return;
                }

                String country = data.get(0);
                String weather = data.get(1);
                String temperatureUnit = data.get(2);
                String temperature = data.get(3);
                Log.d(mTAG, "query weather info result, country: " + country + ", weather: " + weather
                        + ", temperature: " + temperatureUnit + ", temperature: " + temperature);

                if ("CN".equals(country)) {
                    mWeatherIconRes = mBitmapManager.getWeatherResId(Integer.parseInt(weather));
                } else {
                    mWeatherIconRes = mBitmapManager.getOverseasWeatherResId(Integer.parseInt(weather));
                }
                if ("C".equals(temperatureUnit)) {
                    mTemperatureUnitRes = R.drawable.temperature_unit;
                } else {
                    mTemperatureUnitRes = R.drawable.temperature_unit_f;
                }

                mTemperature = Integer.parseInt(temperature);
            } catch (Exception e) {
                Log.e(mTAG, "query weather info error: " + e.toString(), new Exception());
                mTemperature = Integer.MAX_VALUE;
            }
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            mPointScreenCenter.x = width / 2f;//屏幕中心X坐标
            mPointScreenCenter.y = height / 2f;//屏幕中心Y坐标
            float screenRadius = Math.min(mPointScreenCenter.x, mPointScreenCenter.y);//屏幕半径
            mRadiusOuter = screenRadius - mMarginHorizontal;
            mMarginVertical = mPointScreenCenter.y - mRadiusOuter;

            int pointerMarginBottom = DimenUtil.dip2px(mContext, 20);
            int margin = DimenUtil.dip2px(mContext, 11);
            float bottom = mPointScreenCenter.y - pointerMarginBottom;
            float top = mMarginVertical;
            mRadiusInner = mRadiusOuter - mContext.getResources().getDimension(R.dimen.scale_paperclip);
            top += (mRadiusOuter - mRadiusInner) + margin;
            mRectFMinute.set(mPointScreenCenter.x - mMinutePointerWidth / 2, top,
                    mPointScreenCenter.x + mMinutePointerWidth / 2, bottom);
            mRectFMinuteInner.set(mPointScreenCenter.x - mMinutePointerInnerWidth, top + pointerMarginBottom * 0.2f,
                    mPointScreenCenter.x + mMinutePointerInnerWidth, bottom - 1.4f * pointerMarginBottom);

            top += margin * 1.5;
            mRectFHour.set(mPointScreenCenter.x - mHourPointerWidth / 2, top,
                    mPointScreenCenter.x + mHourPointerWidth / 2, bottom);
            mRectFHourInner.set(mPointScreenCenter.x - mHourPointerInnerWidth, top + pointerMarginBottom * 0.2f,
                    mPointScreenCenter.x + mHourPointerInnerWidth, bottom - 1.4f * pointerMarginBottom);

            mRectFSecond.set(mPointScreenCenter.x - mSecondPointerWidth / 2, mMarginVertical,
                    mPointScreenCenter.x + mSecondPointerWidth / 2, bottom);

            invalidate();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (visible) {
                mBatteryLevel = getBatteryLevel();
                registerBatteryChangeReceiver();

                this.queryWeatherInfo();
                try {
                    mContext.getContentResolver().registerContentObserver(WEATHER_URI, true, mContentObserver);
                } catch (Exception e) {
                    Log.e(mTAG, "register content observer error: " + e.toString(), new Exception());
                }
            } else {
                unregisterBatteryChangeReceiver();

                mContext.getContentResolver().unregisterContentObserver(mContentObserver);

                stopSecondPointerAnim();
            }
        }

        @Override
        protected void onAppearAnimFinished() {
            startSecondPointerAnim();
        }

        @Override
        public void updateTime() {
            Calendar calendar = Calendar.getInstance();
            mHour = DateUtil.getHour(mContext);
            mMinute = calendar.get(Calendar.MINUTE);
            mSecond = calendar.get(Calendar.SECOND);
            mHourRatio = (mHour + mMinute / 60f) / 12;
            mMinuteRatio = (mMinute + mSecond / 60f) / 60;
        }

        @Override
        public void onDraw(Canvas canvas) {
            canvas.drawColor(ContextCompat.getColor(mContext, android.R.color.transparent), PorterDuff.Mode.CLEAR);
            canvas.drawColor(ContextCompat.getColor(mContext, android.R.color.black));

            this.paintScale(canvas);
            this.paintIconInfo(canvas);
            this.paintDate(canvas);
            this.paintPointer(canvas, mHourRatio, mMinuteRatio, mSecondRatio);
        }

        private void paintScale(Canvas canvas) {
            Bitmap bitmap;
            float left, top = mMarginVertical, degrees;
            final float scaleDegree = 1.0f * 360 / SCALES;//刻度角

            for (int index = 0; index < SCALES; index++) {
                degrees = scaleDegree * index;

                if (index % 3 == 0) {
                    continue;
                }

                canvas.save();
                canvas.rotate(degrees, mPointScreenCenter.x, mPointScreenCenter.y);

                bitmap = mBitmapManager.get(R.drawable.boneblack_scale_paperclip
                        , ContextCompat.getColor(mContext, R.color.scale_paperclip));
                left = mPointScreenCenter.x - 1.0f * bitmap.getWidth() / 2;
                canvas.drawBitmap(bitmap, left, top, null);

                canvas.restore();
            }

            int color = ContextCompat.getColor(mContext, R.color.scale_number);
            bitmap = mBitmapManager.getMerge(R.drawable.number_1, color, R.drawable.number_2, color, 0.6f);
            left = mPointScreenCenter.x - 1.0f * bitmap.getWidth() / 2;
            canvas.drawBitmap(bitmap, left, top, null);

            bitmap = mBitmapManager.get(R.drawable.number_6, color, 0.667f);
            left = mPointScreenCenter.x - 1.0f * bitmap.getWidth() / 2;
            top = mPointScreenCenter.y + mRadiusOuter - bitmap.getHeight();
            canvas.drawBitmap(bitmap, left, top, null);
        }

        private void paintIconInfo(Canvas canvas) {
            float scale = 0.44f;
            int color = ContextCompat.getColor(mContext, R.color.scale_paperclip);
            float margin = mMarginHorizontal * 3;

            //天气
            float left = margin;
            float top = mPointScreenCenter.y + mMarginIconText;
            Bitmap bitmap;
            if (mTemperature == Integer.MAX_VALUE) {
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_n, color, scale);
                canvas.drawBitmap(bitmap, left, top, null);
                left += bitmap.getWidth();
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_a, color, scale);
            } else {
                if (mTemperature < 0) {
                    bitmap = mBitmapManager.get(R.drawable.sign_minus, color, scale);
                    canvas.drawBitmap(bitmap, left - DimenUtil.dip2px(mContext, 2),
                            top + getResources().getDimension(R.dimen.number_height) / 2.2f, null);
                    left += bitmap.getWidth();
                }
                int temperature = Math.abs(mTemperature);
                bitmap = mBitmapManager.get(mBitmapManager.getNumberResId(temperature / 10), color, scale);
                canvas.drawBitmap(bitmap, left, top, null);
                left += bitmap.getWidth();
                bitmap = mBitmapManager.get(mBitmapManager.getNumberResId(temperature % 10), color, scale);
            }
            canvas.drawBitmap(bitmap, left, top, null);
            left += bitmap.getWidth();
            bitmap = mBitmapManager.get(mTemperatureUnitRes, color, scale);
            canvas.drawBitmap(bitmap, left, top, null);

            bitmap = mBitmapManager.get(mWeatherIconRes, color, scale * 1.6f);
            top = mPointScreenCenter.y - mMarginIconText - bitmap.getHeight();
            if (mTemperature < 0) {
                left = margin * 2f;
            } else {
                left = margin * 1.5f;
            }
            canvas.drawBitmap(bitmap, left, top, null);

            //电量
            int units = mBatteryLevel % 10;//个位
            int tens = mBatteryLevel / 10;//十位
            bitmap = mBitmapManager.get(R.drawable.sign_percentage, color, scale);
            left = (2 * mPointScreenCenter.x - bitmap.getWidth() - margin);
            top = mPointScreenCenter.y + mMarginIconText;
            canvas.drawBitmap(bitmap, left, top, null);
            bitmap = mBitmapManager.get(mBitmapManager.getNumberResId(units), color, scale);
            canvas.drawBitmap(bitmap, left -= bitmap.getWidth(), top, null);
            if (mBatteryLevel >= 10) {
                bitmap = mBitmapManager.get(mBitmapManager.getNumberResId(tens), color, scale);
                canvas.drawBitmap(bitmap, left -= bitmap.getWidth(), top, null);
                if (mBatteryLevel == 100) {
                    bitmap = mBitmapManager.get(R.drawable.number_1, color, scale);
                    canvas.drawBitmap(bitmap, left - bitmap.getWidth(), top, null);
                }
            }

            bitmap = mBitmapManager.get(R.drawable.icon_battary, color, scale * 1.6f);
            top = mPointScreenCenter.y - mMarginIconText - bitmap.getHeight();
            left = (2 * mPointScreenCenter.x - bitmap.getWidth() - 1.6f * margin);
            canvas.drawBitmap(bitmap, left, top, null);
        }

        private void paintDate(Canvas cvs) {
            String language = Locale.getDefault().getLanguage();
            final boolean isChinese = "zh".equalsIgnoreCase(language);

            Canvas canvas = mPicture.beginRecording(0, 0);
            Calendar calendar = Calendar.getInstance();

            float left = 0;
            int color = ContextCompat.getColor(mContext, R.color.scale_paperclip);
            //月份
            int month = calendar.get(Calendar.MONTH) + 1;
            Bitmap bitmap = mBitmapManager.get(mBitmapManager.getNumberResId(month / 10), color, mScaleDate);
            canvas.drawBitmap(bitmap, left, 0, null);
            left += bitmap.getWidth();
            bitmap = mBitmapManager.get(mBitmapManager.getNumberResId(month % 10), color, mScaleDate);
            canvas.drawBitmap(bitmap, left, 0, null);
            left += bitmap.getWidth();
            int offset = 0;
            if (isChinese) {
                bitmap = mBitmapManager.get(R.drawable.text_month, color, mScaleDate);
            } else {
                bitmap = mBitmapManager.get(R.drawable.sign_point, color, mScalePoint);
                offset = DimenUtil.dip2px(mContext, 3);
                left -= offset;
            }
            canvas.drawBitmap(bitmap, left, 0, null);
            left += bitmap.getWidth();
            left -= offset;

            //号数
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            bitmap = mBitmapManager.get(mBitmapManager.getNumberResId(day / 10), color, mScaleDate);
            canvas.drawBitmap(bitmap, left, 0, null);
            left += bitmap.getWidth();
            bitmap = mBitmapManager.get(mBitmapManager.getNumberResId(day % 10), color, mScaleDate);
            canvas.drawBitmap(bitmap, left, 0, null);
            left += bitmap.getWidth();
            if (isChinese) {
                bitmap = mBitmapManager.get(R.drawable.text_day, color, mScaleDate);
                canvas.drawBitmap(bitmap, left, 0, null);
                left += bitmap.getWidth();
            }


            left += mTextOffset;

            //星期
            if (isChinese) {
                bitmap = mBitmapManager.get(R.drawable.text_week, color, mScaleDate);//星期
                canvas.drawBitmap(bitmap, left, 0, null);
                left += bitmap.getWidth();
                bitmap = mBitmapManager.get(mBitmapManager.getWeekResId(), color, mScaleDate);
                canvas.drawBitmap(bitmap, left, 0, null);
            } else {
                switch (calendar.get(Calendar.DAY_OF_WEEK)) {
                    case 2:
                        bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_m, color, mScaleDate);
                        canvas.drawBitmap(bitmap, left, 0, null);
                        left += bitmap.getWidth();
                        bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_o, color, mScaleDate);
                        canvas.drawBitmap(bitmap, left, 0, null);
                        left += bitmap.getWidth();
                        bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_n, color, mScaleDate);
                        canvas.drawBitmap(bitmap, left, 0, null);
                        break;
                    case 3:
                        bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_t, color, mScaleDate);
                        canvas.drawBitmap(bitmap, left, 0, null);
                        left += bitmap.getWidth();
                        bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_u, color, mScaleDate);
                        canvas.drawBitmap(bitmap, left, 0, null);
                        left += bitmap.getWidth();
                        bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_e, color, mScaleDate);
                        canvas.drawBitmap(bitmap, left, 0, null);
                        left += bitmap.getWidth();
                        bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_s, color, mScaleDate);
                        canvas.drawBitmap(bitmap, left, 0, null);
                        break;
                    case 4:
                        bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_w, color, mScaleDate);
                        canvas.drawBitmap(bitmap, left, 0, null);
                        left += bitmap.getWidth();
                        bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_e, color, mScaleDate);
                        canvas.drawBitmap(bitmap, left, 0, null);
                        left += bitmap.getWidth();
                        bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_d, color, mScaleDate);
                        canvas.drawBitmap(bitmap, left, 0, null);
                        break;
                    case 5:
                        bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_t, color, mScaleDate);
                        canvas.drawBitmap(bitmap, left, 0, null);
                        left += bitmap.getWidth();
                        bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_h, color, mScaleDate);
                        canvas.drawBitmap(bitmap, left, 0, null);
                        left += bitmap.getWidth();
                        bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_u, color, mScaleDate);
                        canvas.drawBitmap(bitmap, left, 0, null);
                        left += bitmap.getWidth();
                        bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_r, color, mScaleDate);
                        canvas.drawBitmap(bitmap, left, 0, null);
                        break;
                    case 6:
                        bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_f, color, mScaleDate);
                        canvas.drawBitmap(bitmap, left, 0, null);
                        left += bitmap.getWidth();
                        bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_r, color, mScaleDate);
                        canvas.drawBitmap(bitmap, left, 0, null);
                        left += bitmap.getWidth();
                        bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_i, color, mScaleDate);
                        canvas.drawBitmap(bitmap, left, 0, null);
                        break;
                    case 7:
                        bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_s, color, mScaleDate);
                        canvas.drawBitmap(bitmap, left, 0, null);
                        left += bitmap.getWidth();
                        bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_a, color);
                        canvas.drawBitmap(bitmap, left, 0, null);
                        left += bitmap.getWidth();
                        bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_t, color);
                        canvas.drawBitmap(bitmap, left, 0, null);
                        break;
                    default:
                        bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_s, color, mScaleDate);
                        canvas.drawBitmap(bitmap, left, 0, null);
                        left += bitmap.getWidth();
                        bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_u, color, mScaleDate);
                        canvas.drawBitmap(bitmap, left, 0, null);
                        left += bitmap.getWidth();
                        bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_n, color, mScaleDate);
                        canvas.drawBitmap(bitmap, left, 0, null);
                }
            }
            left += bitmap.getWidth();
            mPicture.endRecording();

            cvs.save();
            cvs.translate(mPointScreenCenter.x - left / 2, mPointScreenCenter.y + (mRadiusInner - bitmap.getHeight()) / 2);
            cvs.drawPicture(mPicture);
            cvs.restore();
        }

        private void paintPointer(Canvas canvas, float rateHour, float rateMinute, float rateSecond) {
            canvas.save();
            canvas.rotate(rateHour * 360, mPointScreenCenter.x, mPointScreenCenter.y);
            int saveLayer = canvas.saveLayer(mRectFHour, null);
            canvas.drawRoundRect(mRectFHour, mHourPointerWidth, mHourPointerWidth, mPaintMinute);
            mPaintMinute.setXfermode(mXfermode);
            canvas.drawRoundRect(mRectFHourInner, mHourPointerInnerWidth, mHourPointerInnerWidth, mPaintMinute);
            mPaintMinute.setXfermode(null);
            canvas.restoreToCount(saveLayer);
            canvas.restore();

            canvas.save();
            canvas.rotate(rateMinute * 360, mPointScreenCenter.x, mPointScreenCenter.y);
            saveLayer = canvas.saveLayer(mRectFMinute, null);
            canvas.drawRoundRect(mRectFMinute, mMinutePointerWidth, mMinutePointerWidth, mPaintMinute);
            mPaintMinute.setXfermode(mXfermode);
            canvas.drawRoundRect(mRectFMinuteInner, mMinutePointerInnerWidth, mMinutePointerInnerWidth, mPaintMinute);
            mPaintMinute.setXfermode(null);
            canvas.restoreToCount(saveLayer);
            canvas.restore();

            canvas.save();
            canvas.rotate(rateSecond * 360, mPointScreenCenter.x, mPointScreenCenter.y);
            canvas.drawRoundRect(mRectFSecond, mSecondPointerWidth, mSecondPointerWidth, mPaintSecond);
            canvas.restore();

            canvas.drawCircle(mPointScreenCenter.x, mPointScreenCenter.y, mCenterCircleRadius, mPaintCenterCircle);
        }
    }

}
