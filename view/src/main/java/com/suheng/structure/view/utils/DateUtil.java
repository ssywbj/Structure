package com.suheng.structure.view.utils;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    public static boolean isSameDay(long millisecondsA, long millisecondsB) {
        if (millisecondsA <= 0 || millisecondsB <= 0) {
            return false;
        }

        Calendar calendarA = Calendar.getInstance();
        calendarA.setTime(new Date(millisecondsA));

        Calendar calendarB = Calendar.getInstance();
        calendarB.setTime(new Date(millisecondsB));

        return (calendarA.get(Calendar.DAY_OF_MONTH) == calendarB.get(Calendar.DAY_OF_MONTH))
                && (calendarA.get(Calendar.MONTH) == calendarB.get(Calendar.MONTH))
                && (calendarA.get(Calendar.YEAR) == calendarB.get(Calendar.YEAR));
    }

}

