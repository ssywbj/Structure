package com.wiz.watch.facesimplenumber;

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

public class SimpleNumberWatchFace extends NumberBeatView {
    public static final String AUTHORITY = "com.wiz.watch.weather";
    public final static Uri WEATHER_URI = Uri.parse("content://" + AUTHORITY + "/weathers");
    public final String QUERY_WEATHER_INFO = "query_weather_info";

    private float mRadiusOuter;

    private Paint mPaintDate;
    private Paint mPaintMiddleLine;
    private final RectF mRectF = new RectF();

    private SimpleNumberBitmapManager mBitmapManager;

    private int mWeatherIconRes = R.drawable.paint_weather_no_data;
    private int mTemperatureUnitRes = R.drawable.paint_temperature_unit;
    private int mTemperature = Integer.MAX_VALUE;
    private float mMarginBottomTimeDivideLine, mLineWidth;

    private final ContentObserver mContentObserver = new ContentObserver(null) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            queryWeatherInfo();
        }
    };

    public SimpleNumberWatchFace(Context context, boolean isEditMode) {
        super(context, isEditMode);
        this.init();
    }

    public SimpleNumberWatchFace(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public void init() {
        setDefaultTime(8, 36);
        mBitmapManager = new SimpleNumberBitmapManager(mContext);

        mPaintDate = new Paint();

        mPaintMiddleLine = new Paint();
        mPaintMiddleLine.setAntiAlias(true);
        mPaintMiddleLine.setColor(Color.parseColor("#FF3333"));
        mPaintMiddleLine.setStyle(Paint.Style.FILL);
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

        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRadiusOuter = Math.min(mPointScreenCenter.x, mPointScreenCenter.y)
                - DimenUtil.dip2px(mContext, 3);//绘制半径
        mMarginBottomTimeDivideLine = DimenUtil.dip2px(mContext, 10);

        mLineWidth = DimenUtil.dip2px(mContext, 6);
        float lineLen = DimenUtil.dip2px(mContext, 240);
        float left = mPointScreenCenter.x - lineLen / 2;
        float right = mPointScreenCenter.x + lineLen / 2;
        mRectF.set(left, mPointScreenCenter.y - mLineWidth / 2,
                right, mPointScreenCenter.y + mLineWidth / 2);

        invalidate();
    }

    @Override
    public void onVisibilityChanged(boolean visible) {
        super.onVisibilityChanged(visible);
        if (visible) {
            this.queryWeatherInfo();
            try {
                mContext.getContentResolver().registerContentObserver(WEATHER_URI, true, mContentObserver);
            } catch (Exception e) {
                Log.e(mTAG, "register content observer error: " + e.toString(), new Exception());
            }
        } else {
            mContext.getContentResolver().unregisterContentObserver(mContentObserver);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        mBitmapManager.clear();
    }

    @Override
    protected void onTimeTick() {
        super.onTimeTick();
        if (mIsPlayingAppearanceAnim) {
            return;
        }

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(ContextCompat.getColor(mContext, android.R.color.transparent), PorterDuff.Mode.CLEAR);
        canvas.drawColor(ContextCompat.getColor(mContext, android.R.color.black));

        //屏幕中间红粗线
        canvas.drawRoundRect(mRectF, mLineWidth, mLineWidth, mPaintMiddleLine);

        this.paintDate(canvas, mHour, mMinute);
        this.paintIconInfo(canvas);
    }

    private void paintIconInfo(Canvas canvas) {
        int color = Color.parseColor("#CCCCCC");

        float left;
        if (mTemperature < 0) {
            left = mPointScreenCenter.x + DimenUtil.dip2px(mContext, 4);
        } else {
            left = mPointScreenCenter.x;
        }
        Bitmap bitmap = mBitmapManager.get(mTemperatureUnitRes, color);
        float top = mPointScreenCenter.y + mRadiusOuter - bitmap.getHeight() - DimenUtil.dip2px(mContext, 30);
        canvas.drawBitmap(bitmap, left, top, null);
        if (mTemperature == Integer.MAX_VALUE) {
            bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_a, color);
            left -= bitmap.getWidth();
            canvas.drawBitmap(bitmap, left, top, null);
            bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_n, color);
        } else {
            int temperature = Math.abs(mTemperature);
            bitmap = mBitmapManager.get(mBitmapManager.getSmallNumberResId(temperature % 10), color);
            left -= bitmap.getWidth();
            canvas.drawBitmap(bitmap, left, top, null);
            bitmap = mBitmapManager.get(mBitmapManager.getSmallNumberResId(temperature / 10), color);
        }
        left -= bitmap.getWidth();
        canvas.drawBitmap(bitmap, left, top, null);
        if (mTemperature < 0) {
            bitmap = mBitmapManager.get(R.drawable.paint_sign_minus, color);
            left -= (bitmap.getWidth() + DimenUtil.dip2px(mContext, 2));
            canvas.drawBitmap(bitmap, left, top + mContext.getResources()
                    .getDimension(R.dimen.number_small_height) / 2.3f, null);
        }

        bitmap = mBitmapManager.get(mWeatherIconRes, color);
        left = mPointScreenCenter.x - 1.0f * bitmap.getWidth() / 2 - DimenUtil.dip2px(mContext, 3);
        top -= (bitmap.getHeight() + DimenUtil.dip2px(mContext, 2));
        canvas.drawBitmap(bitmap, left, top, null);
    }

    private void paintDate(Canvas canvas, int hour, int minute) {
        int color = ContextCompat.getColor(mContext, android.R.color.white);
        mPaintDate.setColor(color);
        int shadowColor = Color.parseColor("#2E42FF");
        mPaintDate.setShadowLayer(5, 0, 0, shadowColor);//外围阴影效果

        Locale locale = Locale.getDefault();
        String language = locale.getLanguage();
        String country = locale.getCountry();
        Log.d(mTAG, "language: " + language + ", country:" + country);//zh，CN；zh，HK
        if ("zh".equals(language)) {
            this.paintZhDate(canvas, color);
        } else {
            this.paintEnDate(canvas, color);
        }
        mPaintDate.clearShadowLayer();

        mPaintDate.setShadowLayer(10, 0, 0, shadowColor);//外围阴影效果

        //冒号
        Bitmap bitmap = mBitmapManager.get(R.drawable.paint_sign_colon, color);
        float top = mPointScreenCenter.y - bitmap.getHeight() - 2.6f * mMarginBottomTimeDivideLine;
        canvas.drawBitmap(bitmap.extractAlpha(), mPointScreenCenter.x - 1.0f * bitmap.getWidth() / 2, top, mPaintDate);

        //分钟
        int units = minute % 10;//个位
        int tens = minute / 10;//十位
        float left = mRectF.right;
        bitmap = mBitmapManager.get(mBitmapManager.getBigNumberResId(units), color);
        top = mPointScreenCenter.y - bitmap.getHeight() - 1.25f * mMarginBottomTimeDivideLine;
        left -= bitmap.getWidth();
        canvas.drawBitmap(bitmap.extractAlpha(), left, top, mPaintDate);
        bitmap = mBitmapManager.get(mBitmapManager.getBigNumberResId(tens), color);
        left -= bitmap.getWidth();
        canvas.drawBitmap(bitmap.extractAlpha(), left, top, mPaintDate);

        //时钟
        units = hour % 10;//个位
        tens = hour / 10;//十位
        left = mRectF.left;
        bitmap = mBitmapManager.get(mBitmapManager.getBigNumberResId(tens), color);
        canvas.drawBitmap(bitmap.extractAlpha(), left, top, mPaintDate);
        left += bitmap.getWidth();
        bitmap = mBitmapManager.get(mBitmapManager.getBigNumberResId(units), color);
        canvas.drawBitmap(bitmap.extractAlpha(), left, top, mPaintDate);

        mPaintDate.clearShadowLayer();
    }

    private void paintZhDate(Canvas canvas, int color) {
        Calendar instance = Calendar.getInstance();
        int month = instance.get(Calendar.MONTH) + 1;
        int day = instance.get(Calendar.DAY_OF_MONTH);

        //星期
        float left = mRectF.right;
        float top = mPointScreenCenter.y + mMarginBottomTimeDivideLine;
        Bitmap bitmap = mBitmapManager.get(mBitmapManager.getWeekResId(), color);
        canvas.drawBitmap(bitmap.extractAlpha(), left -= bitmap.getWidth(), top, mPaintDate);
        bitmap = mBitmapManager.get(R.drawable.paint_text_week_middle, color);
        canvas.drawBitmap(bitmap.extractAlpha(), left - bitmap.getWidth(), top, mPaintDate);

        //月份
        left = mRectF.left;
        int units = month % 10;//个位
        int tens = month / 10;//十位
        bitmap = mBitmapManager.get(mBitmapManager.getMiddleNumberResId(tens), color);
        canvas.drawBitmap(bitmap.extractAlpha(), left, top, mPaintDate);
        left += bitmap.getWidth();
        bitmap = mBitmapManager.get(mBitmapManager.getMiddleNumberResId(units), color);
        canvas.drawBitmap(bitmap.extractAlpha(), left, top, mPaintDate);
        left += bitmap.getWidth();
        bitmap = mBitmapManager.get(R.drawable.paint_text_month_middle, color);
        canvas.drawBitmap(bitmap.extractAlpha(), left, top, mPaintDate);
        left += bitmap.getWidth();

        //号数
        units = day % 10;//个位
        tens = day / 10;//十位
        bitmap = mBitmapManager.get(mBitmapManager.getMiddleNumberResId(tens), color);
        canvas.drawBitmap(bitmap.extractAlpha(), left, top, mPaintDate);
        left += bitmap.getWidth();
        bitmap = mBitmapManager.get(mBitmapManager.getMiddleNumberResId(units), color);
        canvas.drawBitmap(bitmap.extractAlpha(), left, top, mPaintDate);
        left += bitmap.getWidth();
        bitmap = mBitmapManager.get(R.drawable.paint_text_day_middle, color);
        canvas.drawBitmap(bitmap.extractAlpha(), left, top, mPaintDate);
    }

    private void paintEnDate(Canvas canvas, int color) {
        Calendar instance = Calendar.getInstance();

        //月份
        float left = mRectF.left;
        float top = mPointScreenCenter.y + mMarginBottomTimeDivideLine;
        int month = instance.get(Calendar.MONTH) + 1;
        int units = month % 10;//个位
        int tens = month / 10;//十位
        Bitmap bitmap = mBitmapManager.get(mBitmapManager.getMiddleNumberResId(tens), color);
        canvas.drawBitmap(bitmap.extractAlpha(), left, top, mPaintDate);
        left += bitmap.getWidth();
        bitmap = mBitmapManager.get(mBitmapManager.getMiddleNumberResId(units), color);
        canvas.drawBitmap(bitmap.extractAlpha(), left, top, mPaintDate);
        left += bitmap.getWidth();

        bitmap = mBitmapManager.get(R.drawable.paint_number_ic_point, color);
        float offset = 1.0f * bitmap.getWidth() / 5;
        left -= offset;
        canvas.drawBitmap(bitmap.extractAlpha(), left, top + DimenUtil.dip2px(mContext, 2), mPaintDate);
        left += (bitmap.getWidth() - offset);

        //号数
        int day = instance.get(Calendar.DAY_OF_MONTH);
        units = day % 10;//个位
        tens = day / 10;//十位
        bitmap = mBitmapManager.get(mBitmapManager.getMiddleNumberResId(tens), color);
        canvas.drawBitmap(bitmap.extractAlpha(), left, top, mPaintDate);
        left += bitmap.getWidth();
        bitmap = mBitmapManager.get(mBitmapManager.getMiddleNumberResId(units), color);
        canvas.drawBitmap(bitmap.extractAlpha(), left, top, mPaintDate);

        left = mRectF.right;
        switch (instance.get(Calendar.DAY_OF_WEEK)) {
            case 2:
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_n_middle, color);
                left -= bitmap.getWidth();
                canvas.drawBitmap(bitmap.extractAlpha(), left, top, mPaintDate);
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_o, color);
                left -= bitmap.getWidth();
                canvas.drawBitmap(bitmap.extractAlpha(), left, top, mPaintDate);
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_m, color);
                left -= bitmap.getWidth();
                canvas.drawBitmap(bitmap.extractAlpha(), left, top, mPaintDate);
                break;
            case 3:
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_s, color);
                left -= bitmap.getWidth();
                canvas.drawBitmap(bitmap.extractAlpha(), left, top, mPaintDate);
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_e, color);
                left -= bitmap.getWidth();
                canvas.drawBitmap(bitmap.extractAlpha(), left, top, mPaintDate);
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_u, color);
                left -= bitmap.getWidth();
                canvas.drawBitmap(bitmap.extractAlpha(), left, top, mPaintDate);
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_t, color);
                left -= bitmap.getWidth();
                canvas.drawBitmap(bitmap.extractAlpha(), left, top, mPaintDate);
                break;
            case 4:
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_d, color);
                left -= bitmap.getWidth();
                canvas.drawBitmap(bitmap.extractAlpha(), left, top, mPaintDate);
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_e, color);
                left -= bitmap.getWidth();
                canvas.drawBitmap(bitmap.extractAlpha(), left, top, mPaintDate);
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_w, color);
                left -= bitmap.getWidth();
                canvas.drawBitmap(bitmap.extractAlpha(), left, top, mPaintDate);
                break;
            case 5:
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_r, color);
                left -= bitmap.getWidth();
                canvas.drawBitmap(bitmap.extractAlpha(), left, top, mPaintDate);
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_u, color);
                left -= bitmap.getWidth();
                canvas.drawBitmap(bitmap.extractAlpha(), left, top, mPaintDate);
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_h, color);
                left -= bitmap.getWidth();
                canvas.drawBitmap(bitmap.extractAlpha(), left, top, mPaintDate);
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_t, color);
                left -= bitmap.getWidth();
                canvas.drawBitmap(bitmap.extractAlpha(), left, top, mPaintDate);
                break;
            case 6:
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_i, color);
                left -= bitmap.getWidth();
                canvas.drawBitmap(bitmap.extractAlpha(), left, top, mPaintDate);
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_r, color);
                left -= bitmap.getWidth();
                canvas.drawBitmap(bitmap.extractAlpha(), left, top, mPaintDate);
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_f, color);
                left -= bitmap.getWidth();
                canvas.drawBitmap(bitmap.extractAlpha(), left, top, mPaintDate);
                break;
            case 7:
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_t, color);
                left -= bitmap.getWidth();
                canvas.drawBitmap(bitmap.extractAlpha(), left, top, mPaintDate);
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_a_middle, color);
                left -= bitmap.getWidth();
                canvas.drawBitmap(bitmap.extractAlpha(), left, top, mPaintDate);
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_s, color);
                left -= bitmap.getWidth();
                canvas.drawBitmap(bitmap.extractAlpha(), left, top, mPaintDate);
                break;
            default:
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_n_middle, color);
                left -= bitmap.getWidth();
                canvas.drawBitmap(bitmap.extractAlpha(), left, top, mPaintDate);
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_u, color);
                left -= bitmap.getWidth();
                canvas.drawBitmap(bitmap.extractAlpha(), left, top, mPaintDate);
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_s, color);
                left -= bitmap.getWidth();
                canvas.drawBitmap(bitmap.extractAlpha(), left, top, mPaintDate);
        }
    }

}
