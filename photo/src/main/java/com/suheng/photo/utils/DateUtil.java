package com.suheng.photo.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    public static String parseYearMonthDay(long milliseconds) {
        return new SimpleDateFormat("yyyy年MM月dd日").format(new Date(milliseconds));
    }

    public static boolean isSameDay(long millisecondsA, long millisecondsB) {
        Calendar calendarA = Calendar.getInstance();
        calendarA.setTime(new Date(millisecondsA));

        Calendar calendarB = Calendar.getInstance();
        calendarB.setTime(new Date(millisecondsB));

        return (calendarA.get(Calendar.DAY_OF_MONTH) == calendarB.get(Calendar.DAY_OF_MONTH))
                && (calendarA.get(Calendar.MONTH) == calendarB.get(Calendar.MONTH))
                && (calendarA.get(Calendar.YEAR) == calendarB.get(Calendar.YEAR));
    }

}

