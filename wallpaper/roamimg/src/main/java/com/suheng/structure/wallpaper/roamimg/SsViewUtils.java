package com.suheng.structure.wallpaper.roamimg;

import android.text.TextUtils;

import java.util.Calendar;
import java.util.TimeZone;

public class SsViewUtils {
    private static final String WORLD_TIME_ZONE_SEPARATOR = "-";

    public static String formatTimeZoneId(String tz_id) {
        String currentId = tz_id;
        if (!TextUtils.isEmpty(currentId)) {
            if (currentId.contains(WORLD_TIME_ZONE_SEPARATOR)) {
                currentId = currentId.substring(0, currentId.indexOf(WORLD_TIME_ZONE_SEPARATOR));
            }
        }
        return currentId;
    }

    public static String getCityGmt(String timeZoneId) {
        final long date = Calendar.getInstance().getTimeInMillis();

        final TimeZone tz = TimeZone.getTimeZone(timeZoneId);
        final int offset = tz.getOffset(date);
        final int p = Math.abs(offset);
        final StringBuilder name = new StringBuilder();
        name.append("GMT");

        if (offset < 0) {
            name.append('-');
        } else {
            name.append('+');
        }

        name.append(p / (SsWorldTimeConstants.HOURS_1));
        name.append(':');

        int min = p / 60000;
        min %= 60;

        if (min < 10) {
            name.append('0');
        }
        name.append(min);

        return name.toString();
    }
}