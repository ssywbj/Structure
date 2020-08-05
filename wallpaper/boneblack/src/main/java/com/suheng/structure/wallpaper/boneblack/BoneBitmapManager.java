package com.suheng.structure.wallpaper.boneblack;

import android.content.Context;
import android.graphics.Bitmap;

import com.suheng.structure.wallpaper.basic.utils.BitmapManager;

import java.util.Calendar;

public class BoneBitmapManager extends BitmapManager {

    public BoneBitmapManager(Context context) {
        super(context);
    }

    public Bitmap getWeekBitmap() {
        return get(this.getWeekResId());
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

    public Bitmap getNumberBitmap(int number) {
        return get(this.getNumberResId(number));
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
}
