package com.wiz.watch.facesimplepointer;

import android.content.Context;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.structure.wallpaper.basic.NumberBeatView;
import com.structure.wallpaper.basic.utils.DimenUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class SimplePointerWatchFace extends NumberBeatView {
    public static final String AUTHORITY = "com.wiz.watch.weather";
    public final static Uri WEATHER_URI = Uri.parse("content://" + AUTHORITY + "/weathers");
    public final static String QUERY_WEATHER_INFO = "query_weather_info";

    private static final int SCALES = 12;//12个刻度

    private float mRadiusOuter;//刻度外半径长度
    private float mRadiusInner;//刻度内半径长度
    private float mMarginHorizontal, mMarginVertical;
    private float mTextMarginLR, mTextOffset;
    private int mMarginIconText;//信息图标与其下方文案的留白

    private Paint mPaintMinute, mPaintMinuteInner;
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

    private int mWeatherIconRes = R.drawable.paint_weather_no_data;
    private int mTemperatureUnitRes = R.drawable.paint_temperature_unit;
    private int mTemperature = Integer.MAX_VALUE;

    private final ContentObserver mContentObserver = new ContentObserver(null) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            Log.d(mTAG, "onChange, selfChange: " + selfChange + ", uri: " + uri);
            queryWeatherInfo();
        }
    };

    public SimplePointerWatchFace(Context context, boolean isEditMode) {
        super(context, isEditMode);
        this.init();
    }

    public SimplePointerWatchFace(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    private void init() {
        setDefaultTime(10, 10, 30);
        setNeedPropertyAnim(true);

        mBitmapManager = new SimplePointerBitmapManager(mContext);

        mMarginIconText = DimenUtil.dip2px(mContext, 2);
        mMarginHorizontal = DimenUtil.dip2px(mContext, 3);

        mTextMarginLR = DimenUtil.dip2px(mContext, 2);
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
        mPaintMinuteInner = new Paint();
        mPaintMinuteInner.setAntiAlias(true);
        mPaintMinuteInner.setStyle(Paint.Style.FILL);
        mPaintMinuteInner.setColor(ContextCompat.getColor(mContext, R.color.basic_wallpaper_bg_black));

        mPaintSecond = new Paint(mPaintMinute);
        mPaintSecond.setShadowLayer(mSecondPointerWidth, 0, 0, Color.parseColor("#FF2E2E"));
        mPaintSecond.setColor(Color.parseColor("#FF3333"));

        mCenterCircleRadius = DimenUtil.dip2px(mContext, 6f);
        mPaintCenterCircle = new Paint();
        mPaintCenterCircle.setAntiAlias(true);
        mPaintCenterCircle.setStyle(Paint.Style.FILL);
        mPaintCenterCircle.setColor(Color.WHITE);
        mPaintCenterCircle.setShadowLayer(mCenterCircleRadius, 0, 0, shadowColor);//外围阴影效果
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
                mTemperatureUnitRes = R.drawable.paint_temperature_unit;
            } else {
                mTemperatureUnitRes = R.drawable.paint_temperature_unit_f;
            }

            mTemperature = Integer.parseInt(temperature);
        } catch (Exception e) {
            Log.e(mTAG, "query weather info error: " + e.toString(), new Exception());
            mTemperature = Integer.MAX_VALUE;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
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
    }

    @Override
    public void onVisibilityChanged(boolean visible) {
        super.onVisibilityChanged(visible);
        if (visible) {
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
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        mBitmapManager.clear();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(ContextCompat.getColor(mContext, android.R.color.transparent), PorterDuff.Mode.CLEAR);
        canvas.drawColor(ContextCompat.getColor(mContext, android.R.color.black));

        this.paintScale(canvas);
        this.paintIconInfo(canvas);
        this.paintDate(canvas);
        this.paintPointer(canvas, mHourAnimatorValue, mMinuteAnimatorValue, mSecondAnimatorValue);
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
        bitmap = mBitmapManager.get(R.drawable.boneblack_sacle_number_12, color);
        left = mPointScreenCenter.x - 1.0f * bitmap.getWidth() / 2;
        canvas.drawBitmap(bitmap, left, top, null);

        bitmap = mBitmapManager.get(R.drawable.boneblack_scale_number_6, color);
        left = mPointScreenCenter.x - 1.0f * bitmap.getWidth() / 2;
        top = mPointScreenCenter.y + mRadiusOuter - bitmap.getHeight();
        canvas.drawBitmap(bitmap, left, top, null);
    }

    private void paintIconInfo(Canvas canvas) {
        int color = ContextCompat.getColor(mContext, R.color.scale_paperclip);
        float margin = mMarginHorizontal * 3;

        //天气
        float left = margin;
        float top = mPointScreenCenter.y + mMarginIconText;
        Bitmap bitmap;
        if (mTemperature == Integer.MAX_VALUE) {
            bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_n, color);
            canvas.drawBitmap(bitmap, left, top, null);
            left += bitmap.getWidth();
            bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_a, color);
        } else {
            if (mTemperature < 0) {
                bitmap = mBitmapManager.get(R.drawable.paint_sign_minus, color);
                canvas.drawBitmap(bitmap, left - DimenUtil.dip2px(mContext, 2),
                        top + getResources().getDimension(R.dimen.number_height) / 2.2f, null);
                left += bitmap.getWidth();
            }
            int temperature = Math.abs(mTemperature);
            bitmap = mBitmapManager.get(mBitmapManager.getNumberResId(temperature / 10), color);
            canvas.drawBitmap(bitmap, left, top, null);
            left += bitmap.getWidth();
            bitmap = mBitmapManager.get(mBitmapManager.getNumberResId(temperature % 10), color);
        }
        canvas.drawBitmap(bitmap, left, top, null);
        left += bitmap.getWidth();
        bitmap = mBitmapManager.get(mTemperatureUnitRes, color);
        canvas.drawBitmap(bitmap, left, top, null);

        bitmap = mBitmapManager.get(mWeatherIconRes, color);
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
        bitmap = mBitmapManager.get(R.drawable.paint_sign_percentage, color);
        left = (2 * mPointScreenCenter.x - bitmap.getWidth() - margin);
        top = mPointScreenCenter.y + mMarginIconText;
        canvas.drawBitmap(bitmap, left, top, null);
        bitmap = mBitmapManager.get(mBitmapManager.getNumberResId(units), color);
        canvas.drawBitmap(bitmap, left -= bitmap.getWidth(), top, null);
        if (mBatteryLevel >= 10) {
            bitmap = mBitmapManager.get(mBitmapManager.getNumberResId(tens), color);
            canvas.drawBitmap(bitmap, left -= bitmap.getWidth(), top, null);
            if (mBatteryLevel == 100) {
                bitmap = mBitmapManager.get(R.drawable.paint_number_1, color);
                canvas.drawBitmap(bitmap, left - bitmap.getWidth(), top, null);
            }
        }

        bitmap = mBitmapManager.get(R.drawable.paint_battary, color);
        top = mPointScreenCenter.y - mMarginIconText - bitmap.getHeight();
        left = (2 * mPointScreenCenter.x - bitmap.getWidth() - 1.6f * margin);
        canvas.drawBitmap(bitmap, left, top, null);
    }

    private void paintDate(Canvas canvas) {
        Locale locale = Locale.getDefault();
        String language = locale.getLanguage();
        //Log.d(mTAG, "language: " + language + ", country:" + locale.getCountry());//zh，CN；zh，HK
        if ("zh".equals(language)) {
            this.paintZhDate(canvas);
        } else {
            this.paintEnDate(canvas);
        }
    }

    private void paintZhDate(Canvas canvas) {
        Calendar instance = Calendar.getInstance();
        int month = instance.get(Calendar.MONTH) + 1;
        int day = instance.get(Calendar.DAY_OF_MONTH);

        int color = ContextCompat.getColor(mContext, R.color.scale_paperclip);

        float offset = mPointScreenCenter.x + mTextOffset;

        float left = offset + mTextMarginLR;
        Bitmap bitmap = mBitmapManager.get(R.drawable.paint_text_day, color);
        float top = mPointScreenCenter.y + (mRadiusInner - bitmap.getHeight()) / 1.8f;
        canvas.drawBitmap(bitmap, left, top, null);

        //星期
        left += (bitmap.getWidth() + DimenUtil.dip2px(mContext, 4));
        bitmap = mBitmapManager.get(R.drawable.paint_text_week, color);//星期
        canvas.drawBitmap(bitmap, left, top, null);
        left += bitmap.getWidth();
        bitmap = mBitmapManager.get(mBitmapManager.getWeekResId(), color);
        canvas.drawBitmap(bitmap, left, top, null);

        //号数
        int units = day % 10;//个位
        int tens = day / 10;//十位
        left = offset;
        bitmap = mBitmapManager.get(mBitmapManager.getNumberResId(units), color);
        canvas.drawBitmap(bitmap, left -= bitmap.getWidth(), top, null);
        bitmap = mBitmapManager.get(mBitmapManager.getNumberResId(tens), color);
        canvas.drawBitmap(bitmap, left -= bitmap.getWidth(), top, null);

        //月份
        bitmap = mBitmapManager.get(R.drawable.paint_text_month, color);
        canvas.drawBitmap(bitmap, left -= (bitmap.getWidth() + mTextMarginLR), top, null);
        units = month % 10;//个位
        tens = month / 10;//十位
        bitmap = mBitmapManager.get(mBitmapManager.getNumberResId(units), color);
        canvas.drawBitmap(bitmap, left -= (bitmap.getWidth() + mTextMarginLR), top, null);
        bitmap = mBitmapManager.get(mBitmapManager.getNumberResId(tens), color);
        canvas.drawBitmap(bitmap, left - bitmap.getWidth(), top, null);
    }

    private void paintEnDate(Canvas canvas) {
        Calendar instance = Calendar.getInstance();
        int color = ContextCompat.getColor(mContext, R.color.scale_paperclip);

        //号数
        int day = instance.get(Calendar.DAY_OF_MONTH);
        int units = day % 10;//个位
        int tens = day / 10;//十位
        float left = mPointScreenCenter.x;
        Bitmap bitmap = mBitmapManager.get(mBitmapManager.getNumberResId(units), color);
        float top = mPointScreenCenter.y + (mRadiusInner - bitmap.getHeight()) / 1.8f;
        canvas.drawBitmap(bitmap, left -= bitmap.getWidth(), top, null);
        bitmap = mBitmapManager.get(mBitmapManager.getNumberResId(tens), color);
        canvas.drawBitmap(bitmap, left -= bitmap.getWidth(), top, null);

        bitmap = mBitmapManager.get(R.drawable.paint_number_ic_point, color);
        float pointOffset = 1.0f * bitmap.getWidth() / 5;
        canvas.drawBitmap(bitmap, left -= (bitmap.getWidth() - pointOffset), top, null);

        //月份
        int month = instance.get(Calendar.MONTH) + 1;
        units = month % 10;//个位
        tens = month / 10;//十位
        bitmap = mBitmapManager.get(mBitmapManager.getNumberResId(units), color);
        canvas.drawBitmap(bitmap, left -= (bitmap.getWidth() - pointOffset), top, null);
        bitmap = mBitmapManager.get(mBitmapManager.getNumberResId(tens), color);
        canvas.drawBitmap(bitmap, left - bitmap.getWidth(), top, null);

        left = mPointScreenCenter.x + getResources().getDimension(R.dimen.number_width);
        switch (instance.get(Calendar.DAY_OF_WEEK)) {
            case 2:
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_m, color);
                canvas.drawBitmap(bitmap, left, top, null);
                left += bitmap.getWidth();
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_o, color);
                canvas.drawBitmap(bitmap, left, top, null);
                left += bitmap.getWidth();
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_n, color);
                canvas.drawBitmap(bitmap, left, top, null);
                break;
            case 3:
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_t, color);
                canvas.drawBitmap(bitmap, left, top, null);
                left += bitmap.getWidth();
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_u, color);
                canvas.drawBitmap(bitmap, left, top, null);
                left += bitmap.getWidth();
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_e, color);
                canvas.drawBitmap(bitmap, left, top, null);
                left += bitmap.getWidth();
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_s, color);
                canvas.drawBitmap(bitmap, left, top, null);
                break;
            case 4:
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_w, color);
                canvas.drawBitmap(bitmap, left, top, null);
                left += bitmap.getWidth();
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_e, color);
                canvas.drawBitmap(bitmap, left, top, null);
                left += bitmap.getWidth();
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_d, color);
                canvas.drawBitmap(bitmap, left, top, null);
                break;
            case 5:
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_t, color);
                canvas.drawBitmap(bitmap, left, top, null);
                left += bitmap.getWidth();
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_h, color);
                canvas.drawBitmap(bitmap, left, top, null);
                left += bitmap.getWidth();
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_u, color);
                canvas.drawBitmap(bitmap, left, top, null);
                left += bitmap.getWidth();
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_r, color);
                canvas.drawBitmap(bitmap, left, top, null);
                break;
            case 6:
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_f, color);
                canvas.drawBitmap(bitmap, left, top, null);
                left += bitmap.getWidth();
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_r, color);
                canvas.drawBitmap(bitmap, left, top, null);
                left += bitmap.getWidth();
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_i, color);
                canvas.drawBitmap(bitmap, left, top, null);
                break;
            case 7:
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_s, color);
                canvas.drawBitmap(bitmap, left, top, null);
                left += bitmap.getWidth();
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_a, color);
                canvas.drawBitmap(bitmap, left, top, null);
                left += bitmap.getWidth();
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_t, color);
                canvas.drawBitmap(bitmap, left, top, null);
                break;
            default:
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_s, color);
                canvas.drawBitmap(bitmap, left, top, null);
                left += bitmap.getWidth();
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_u, color);
                canvas.drawBitmap(bitmap, left, top, null);
                left += bitmap.getWidth();
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_n, color);
                canvas.drawBitmap(bitmap, left, top, null);
        }
    }

    private void paintPointer(Canvas canvas, float rateHour, float rateMinute, float rateSecond) {
        canvas.save();
        canvas.rotate(rateHour * 360, mPointScreenCenter.x, mPointScreenCenter.y);
        canvas.drawRoundRect(mRectFHour, mHourPointerWidth, mHourPointerWidth, mPaintMinute);
        canvas.drawRoundRect(mRectFHourInner, mHourPointerInnerWidth, mHourPointerInnerWidth, mPaintMinuteInner);
        canvas.restore();

        canvas.save();
        canvas.rotate(rateMinute * 360, mPointScreenCenter.x, mPointScreenCenter.y);
        canvas.drawRoundRect(mRectFMinute, mMinutePointerWidth, mMinutePointerWidth, mPaintMinute);
        canvas.drawRoundRect(mRectFMinuteInner, mMinutePointerInnerWidth, mMinutePointerInnerWidth, mPaintMinuteInner);
        canvas.restore();

        canvas.save();
        canvas.rotate(rateSecond * 360, mPointScreenCenter.x, mPointScreenCenter.y);
        canvas.drawRoundRect(mRectFSecond, mSecondPointerWidth, mSecondPointerWidth, mPaintSecond);
        canvas.restore();

        canvas.drawCircle(mPointScreenCenter.x, mPointScreenCenter.y, mCenterCircleRadius, mPaintCenterCircle);
    }

    @Override
    protected void onAppearanceAnimFinished() {
        super.onAppearanceAnimFinished();
        startSecondPointerAnim();
    }
}
