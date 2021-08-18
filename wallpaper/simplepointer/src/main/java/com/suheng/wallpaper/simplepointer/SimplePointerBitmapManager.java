package com.suheng.wallpaper.simplepointer;

import android.content.Context;

import com.suheng.wallpaper.basic.manager.BitmapManager;

import java.util.Calendar;

public class SimplePointerBitmapManager extends BitmapManager {

    public SimplePointerBitmapManager(Context context) {
        super(context);
    }

    public int getWeekResId() {
        Calendar instance = Calendar.getInstance();
        switch (instance.get(Calendar.DAY_OF_WEEK)) {
            case 2:
                return R.drawable.paint_text_1;
            case 3:
                return R.drawable.paint_text_2;
            case 4:
                return R.drawable.paint_text_3;
            case 5:
                return R.drawable.paint_text_4;
            case 6:
                return R.drawable.paint_text_5;
            case 7:
                return R.drawable.paint_text_6;
            default:
                return R.drawable.paint_text_day;
        }
    }

    public int getNumberResId(int number) {
        switch (number) {
            case 1:
                return R.drawable.paint_number_1;
            case 2:
                return R.drawable.paint_number_2;
            case 3:
                return R.drawable.paint_number_3;
            case 4:
                return R.drawable.paint_number_4;
            case 5:
                return R.drawable.paint_number_5;
            case 6:
                return R.drawable.paint_number_6;
            case 7:
                return R.drawable.paint_number_7;
            case 8:
                return R.drawable.paint_number_8;
            case 9:
                return R.drawable.paint_number_9;
            default:
                return R.drawable.paint_number_0;
        }
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
