package com.suheng.wallpaper.basic.utils;

import android.content.Context;
import android.text.format.DateFormat;

import java.util.Calendar;

public class DateUtil {

    /*public static String getWeekText(Context context) {
        String[] weeks = context.getResources().getStringArray(R.array.weeks);
        String week;
        try {
            week = weeks[Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1];
        } catch (Exception e) {
            e.printStackTrace();
            week = weeks[0];
        }
        return week;
    }*/

    public static int getWeekIndex() {
        Calendar calendar = Calendar.getInstance();
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case 2://星期－
                return 0;
            case 3:
                return 1;
            case 4:
                return 2;
            case 5:
                return 3;
            case 6:
                return 4;
            case 7://星期六
                return 5;
            default://星期天
                return 6;
        }
    }

    public static String getTimeText(Context context) {
        StringBuilder stringBuilder = new StringBuilder();
        Calendar calendar = Calendar.getInstance();
        int hour = getHour(context);
        stringBuilder.append(hour / 10).append(hour % 10).append(".");
        int minute = calendar.get(Calendar.MINUTE);
        stringBuilder.append(minute / 10).append(minute % 10);
        return stringBuilder.toString();
    }

    public static int getHour(Context context) {
        final boolean is24HourFormat = is24HourFormat(context);
        Calendar instance = Calendar.getInstance();
        int hour;
        if (is24HourFormat) {
            hour = instance.get(Calendar.HOUR_OF_DAY);//24小时制：0～23
        } else {
            hour = instance.get(Calendar.HOUR);//12小时制：1～12
            if (hour == 0) {//0是12点
                hour = 12;
            }
        }

        return hour;
    }

    public static boolean is24HourFormat(Context context) {
        return DateFormat.is24HourFormat(context);
    }

    public static boolean isAm() {
        return Calendar.getInstance().get(Calendar.AM_PM) == Calendar.AM;//0：上午，1：下午
    }
}
