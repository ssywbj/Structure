package com.wiz.watchface.datastatistics.view;

import android.content.Context;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.Nullable;

import com.structure.wallpaper.basic.WatchFaceView;
import com.structure.wallpaper.basic.utils.BitmapManager2;
import com.structure.wallpaper.basic.utils.DimenUtil;
import com.wiz.watchface.datastatistics.R;

import java.util.ArrayList;

public class WeatherView extends WatchFaceView {
    private final static String AUTHORITY = "com.wiz.watch.weather";
    private final static Uri WEATHER_URI = Uri.parse("content://" + AUTHORITY + "/weathers");

    private Bitmap mBitmapTmp;
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Rect mRect = new Rect();

    private int mWeatherIconRes = R.drawable.paint_weather_no_data;
    private int mTemperature = Integer.MAX_VALUE, mTemperatureUnitRes = R.string.tpe_unit_c;
    private BitmapManager2 mBitmapManager;
    private float mScale;

    private final ContentObserver mContentObserver = new ContentObserver(null) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            queryWeatherInfo();
        }
    };

    public WeatherView(Context context) {
        super(context);
        this.init();
    }

    public WeatherView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    private void init() {
        //setBackgroundColor(Color.RED);
        mBitmapManager = new BitmapManager2(mContext);
        mScale = 1.2f;
        mBitmapTmp = mBitmapManager.get(R.drawable.paint_weather_no_data, Color.WHITE, mScale);

        mPaint.setTextSize(DimenUtil.dip2px(getContext(), 38));
        mPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
        mPaint.setColor(Color.WHITE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final String text = getResources().getString(R.string.tpe_unit_c, String.valueOf(-58));
        mPaint.getTextBounds(text, 0, text.length(), mRect);
        final int measuredWidth = Math.max(mBitmapTmp.getWidth(), mRect.width()) + DimenUtil.dip2px(getContext(), 4);
        final int measuredHeight = mBitmapTmp.getHeight() + mRect.height() + DimenUtil.dip2px(getContext(), 6);
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    public void destroy() {
        super.destroy();
        mBitmapManager.clear();
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
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(getWidth() / 2f, 0);
        Bitmap bitmap = mBitmapManager.get(mWeatherIconRes, Color.WHITE, mScale);
        canvas.drawBitmap(bitmap, -bitmap.getWidth() / 2f, 0, null);
        String weather;
        //mTemperature = -88;
        if (mTemperature == Integer.MAX_VALUE) {
            weather = String.format(mContext.getString(mTemperatureUnitRes), "NA");
        } else {
            weather = mContext.getString(mTemperatureUnitRes, String.valueOf(mTemperature));
        }
        mPaint.getTextBounds(weather, 0, weather.length(), mRect);
        canvas.drawText(weather, -mRect.width() / 2f, getHeight() - DimenUtil.dip2px(getContext(), 1), mPaint);
    }

    private void queryWeatherInfo() {
        try {
            Bundle extras = new Bundle();
            extras.putLong("current_time_millis", System.currentTimeMillis());
            String queryWeatherInfo = "query_weather_info";
            Bundle bundle = mContext.getContentResolver().call(WEATHER_URI, queryWeatherInfo, null, extras);
            if (bundle == null) {
                Log.e(mTAG, "query weather info error: bundle is null");
                return;
            }
            ArrayList<String> data = bundle.getStringArrayList(queryWeatherInfo);
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
                mWeatherIconRes = this.getWeatherResId(Integer.parseInt(weather));
            } else {
                mWeatherIconRes = this.getOverseasWeatherResId(Integer.parseInt(weather));
            }
            if ("C".equals(temperatureUnit)) {
                mTemperatureUnitRes = R.string.tpe_unit_c;
            } else {
                mTemperatureUnitRes = R.string.tpe_unit_f;
            }

            mTemperature = Integer.parseInt(temperature);
        } catch (Exception e) {
            Log.e(mTAG, "query weather info error: " + e.toString(), new Exception());
            mTemperature = Integer.MAX_VALUE;
        }

        postInvalidate();
    }

    //http://apidoc.weathercn.com/developers/best-practices.html，华风爱科Api
    public int getWeatherResId(int type) {
        switch (type) {
            case 0://晴 Sunny
                return R.drawable.paint_weather_day_qing;
            case 1://多云 Cloudy
                return R.drawable.paint_weather_day_duoyun;
            case 2://阴	Overcast
                return R.drawable.paint_weather_yintian;
            case 3://阵雨 Shower
            case 301://降雨 Rain
                return R.drawable.paint_weather_chenyu;
            case 4://雷阵雨 Thundershower
                return R.drawable.paint_weather_leizhenyu;
            case 5://雷阵雨伴有冰雹 Thundershower with hail
                return R.drawable.paint_weather_leizhenyubingbao;
            case 6://雨夹雪 Sleet
                return R.drawable.paint_weather_yujiaxue;
            case 7://小雨 Light rain
                return R.drawable.paint_weather_xiaoyu;
            case 8://中雨 Moderate rain
            case 21://小到中雨 Light to moderate rain
                return R.drawable.paint_weather_zhongyu;
            case 9://大雨 Heavy rain
            case 22://中到大雨 Moderate to heavy rain
                return R.drawable.paint_weather_dayu;
            case 10://暴雨 Storm
            case 23://大到暴雨 Heavy rain to storm
                return R.drawable.paint_weather_baoyu;
            case 11://大暴雨 Heavy storm
            case 12://特大暴雨 Severe storm
            case 24://暴雨到大暴雨 Storm to heavy storm
            case 25://大暴雨到特大暴雨 Heavy to severe storm
                return R.drawable.paint_weather_dabaoyu;
            case 13://阵雪 Snow flurry
            case 302://降雪 Snow
                return R.drawable.paint_weather_chenxue;
            case 14://小雪 Light snow
                return R.drawable.paint_weather_xiaoxue;
            case 15://中雪 Moderate snow
            case 26://小到中雪 Light to moderate snow
                return R.drawable.paint_weather_zhongxue;
            case 16://大雪 Heavy snow
            case 27://中到大雪 Moderate to heavy snow
                return R.drawable.paint_weather_daxue;
            case 17://暴雪 Snowstorm
            case 28://大到暴雪 Heavy snow to snowstorm
                return R.drawable.paint_weather_baoxue;
            case 18://雾 Foggy
                return R.drawable.paint_weather_wu;
            case 19://冻雨 Ice rain
                return R.drawable.paint_weather_dongyu;
            case 20://沙尘暴	Dust storm
                return R.drawable.paint_weather_shachenbao;
            case 29://浮尘 Dust
                return R.drawable.paint_weather_fuchen;
            case 30://扬沙 Sand
                return R.drawable.paint_weather_yangsha;
            case 31://强沙尘暴 Sandstorm
                return R.drawable.paint_weather_qiangshachenbao;
            case 32://浓雾 Dense foggy
            case 57://大雾 Heavy foggy
                return R.drawable.paint_weather_nongwu;
            case 33://雪 Snow
                return R.drawable.paint_weather_xue;
            case 49://强浓雾	Moderate foggy
            case 58://特强浓雾 Severe foggy
                return R.drawable.paint_weather_qiangnongwu;
            case 53://霾 Haze
                return R.drawable.paint_weather_mai;
            case 54://中度霾	Moderate haze
                return R.drawable.paint_weather_zhongmai;
            case 55://重度霾	Heavy haze
                return R.drawable.paint_weather_damai;
            case 56://严重霾	Severe haze
                return R.drawable.paint_weather_yanzhongmai;
            default:
                return R.drawable.paint_weather_no_data;
        }
    }

    //http://apidoc.weathercn.com/developers/weatherIcons.html，华风爱科Api
    public int getOverseasWeatherResId(int type) {
        switch (type) {
            case 1://Sunny 晴
            case 2://Mostly Sunny 大部分晴
            case 3://Partly Sunny 部分晴
                return R.drawable.paint_weather_day_qing;
            case 4://Intermittent Clouds 间歇性多云
            case 5://Hazy Sunshine 晴，空气质量差
            case 6://Mostly Cloudy 大部分多云
                return R.drawable.paint_weather_day_duoyun;
            case 7://Cloudy 多云
                return R.drawable.paint_weather_yintian;
            case 8://Dreary 阴郁(Overcast阴)
                return R.drawable.paint_weather_wu;
            case 11://Fog 雾
                return R.drawable.paint_weather_nongwu;
            case 12://Showers 阵雨
            case 13://Mostly Cloudy w/ Showers 多云，有时有阵雨
            case 14://Partly Sunny w/ Showers部分晴，有时有阵雨
                return R.drawable.paint_weather_chenyu;
            case 15://T-Storms 雷雨
            case 16://Mostly Cloudy w/ T-Storms 多云，有时有雷雨
            case 17://Partly Sunny w/ T-Storms 部分晴，有时有雷雨
                return R.drawable.paint_weather_leizhenyu;
            case 18://Rain 雨
                return R.drawable.paint_weather_dayu;
            case 19://Flurries 轻雪
            case 43://Mostly Cloudy w/ Flurries 大部分多云，有时有阵雪
            case 44://Mostly Cloudy w/ Snow 大部分多云，有雪
                return R.drawable.paint_weather_zhongxue;
            case 20://Mostly Cloudy w/ Flurries 多云，有时有轻雪
            case 21://Partly Sunny w/ Flurries 部分晴，有时有轻雪
            case 23://Mostly Cloudy w/ Snow 多云，有时有雪
                return R.drawable.paint_weather_chenxue;
            case 22://Snow 雪
                return R.drawable.paint_weather_xue;
            case 24://Ice 冻雪
                return R.drawable.paint_weather_shuangdong;
            case 25://Sleet 冰霰
                return R.drawable.paint_weather_bingbao;
            case 26://Freezing Rain 冻雨
                return R.drawable.paint_weather_dongyu;
            case 29://Rain and Snow雨和雪
                return R.drawable.paint_weather_yujiaxue;
            case 30://Hot 热
                return R.drawable.paint_weather_re;
            case 31://Cold 冷
                return R.drawable.paint_weather_leng;
            case 32://Windy 风
                return R.drawable.paint_weather_yangsha;
            case 33://Clear 晴
            case 34://Mostly Clear 大部分晴朗
                return R.drawable.paint_weather_night_qing;
            case 35://Partly Cloudy部分多云
            case 36://Intermittent Clouds间歇性多云
            case 37://Hazy Moonlight 晴，空气质量差
            case 38://Mostly Cloudy 大部分多云
                return R.drawable.paint_weather_night_duoyun;
            case 39://Partly Cloudy w/ Showers 部分多云，有时有阵雨
            case 40://Mostly Cloudy w/ Showers 大部分多云，有时有阵雨
                return R.drawable.paint_weather_zhongyu;
            case 41://Partly Cloudy w/ T-Storms 部分多云，有时有雷雨
            case 42://Mostly Cloudy w/ T-Storms 大部分多云，有时有雷雨
                return R.drawable.paint_weather_duoyunleiyu;
            default:
                return R.drawable.paint_weather_no_data;
        }
    }

}
