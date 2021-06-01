package com.wiz.watch.facedigitalbeat;

import android.content.Context;
import android.graphics.Bitmap;

import com.structure.wallpaper.basic.utils.BitmapManager;

public class DigitalBeatBitmapManager extends BitmapManager {

    public DigitalBeatBitmapManager(Context context) {
        super(context);
    }

    public int[] divideTwoUnit(int number) {
        int[] units = new int[2];
        if (number > 99) {
            return units;
        } else {
            units[0] = number / 10;
            units[1] = number % 10;
        }
        return units;
    }

    public int getSecondRes(int number) {
        switch (number) {
            case 1:
                return R.drawable.number_second_1;
            case 2:
                return R.drawable.number_second_2;
            case 3:
                return R.drawable.number_second_3;
            case 4:
                return R.drawable.number_second_4;
            case 5:
                return R.drawable.number_second_5;
            case 6:
                return R.drawable.number_second_6;
            case 7:
                return R.drawable.number_second_7;
            case 8:
                return R.drawable.number_second_8;
            case 9:
                return R.drawable.number_second_9;
            default:
                return R.drawable.number_second_0;
        }
    }

    public int getMinuteRes(int number) {
        switch (number) {
            case 1:
                return R.drawable.number_minute_1;
            case 2:
                return R.drawable.number_minute_2;
            case 3:
                return R.drawable.number_minute_3;
            case 4:
                return R.drawable.number_minute_4;
            case 5:
                return R.drawable.number_minute_5;
            case 6:
                return R.drawable.number_minute_6;
            case 7:
                return R.drawable.number_minute_7;
            case 8:
                return R.drawable.number_minute_8;
            case 9:
                return R.drawable.number_minute_9;
            default:
                return R.drawable.number_minute_0;
        }
    }

    public int getHourRes(int number) {
        switch (number) {
            case 1:
                return R.drawable.number_hour_1;
            case 2:
                return R.drawable.number_hour_2;
            case 3:
                return R.drawable.number_hour_3;
            case 4:
                return R.drawable.number_hour_4;
            case 5:
                return R.drawable.number_hour_5;
            case 6:
                return R.drawable.number_hour_6;
            case 7:
                return R.drawable.number_hour_7;
            case 8:
                return R.drawable.number_hour_8;
            case 9:
                return R.drawable.number_hour_9;
            default:
                return R.drawable.number_hour_0;
        }
    }

    public int getDateRes(int number) {
        switch (number) {
            case 1:
                return R.drawable.number_date_1;
            case 2:
                return R.drawable.number_date_2;
            case 3:
                return R.drawable.number_date_3;
            case 4:
                return R.drawable.number_date_4;
            case 5:
                return R.drawable.number_date_5;
            case 6:
                return R.drawable.number_date_6;
            case 7:
                return R.drawable.number_date_7;
            case 8:
                return R.drawable.number_date_8;
            case 9:
                return R.drawable.number_date_9;
            default:
                return R.drawable.number_date_0;
        }
    }

    public Bitmap getSecondBitmap(int number, int color) {
        int[] units = this.divideTwoUnit(number);
        return getMerge(getSecondRes(units[0]), color, getSecondRes(units[1]), color);
    }

    public Bitmap getMinuteBitmap(int number, int color) {
        int[] units = this.divideTwoUnit(number);
        return getMerge(getMinuteRes(units[0]), color, getMinuteRes(units[1]), color);
    }

    public Bitmap getHourBitmap(int number, int color) {
        int[] units = this.divideTwoUnit(number);
        return getMerge(getHourRes(units[0]), color, getHourRes(units[1]), color);
    }

    public Bitmap getDateBitmap(int number, int color) {
        int[] units = this.divideTwoUnit(number);
        return getMerge(getDateRes(units[0]), color, getDateRes(units[1]), color);
    }
}
