package com.wiz.watch.facesundiary;

import android.content.Context;
import android.graphics.Bitmap;

import com.structure.wallpaper.basic.utils.BitmapManager;

public class SunDiaryBitmapManager extends BitmapManager {

    public SunDiaryBitmapManager(Context context) {
        super(context);
    }

    public Bitmap getTimeNumber(int number, int color) {
        switch (number) {
            case 1:
                return get(R.drawable.number_1, color);
            case 2:
                return get(R.drawable.number_2, color);
            case 3:
                return get(R.drawable.number_3, color);
            case 4:
                return get(R.drawable.number_4, color);
            case 5:
                return get(R.drawable.number_5, color);
            case 6:
                return get(R.drawable.number_6, color);
            case 7:
                return get(R.drawable.number_7, color);
            case 8:
                return get(R.drawable.number_8, color);
            case 9:
                return get(R.drawable.number_9, color);
            default:
                return get(R.drawable.number_0, color);
        }
    }

    public Bitmap getDateNumber(int number, int color) {
        switch (number) {
            case 1:
                return get(R.drawable.number_1_small, color);
            case 2:
                return get(R.drawable.number_2_small, color);
            case 3:
                return get(R.drawable.number_3_small, color);
            case 4:
                return get(R.drawable.number_4_small, color);
            case 5:
                return get(R.drawable.number_5_small, color);
            case 6:
                return get(R.drawable.number_6_small, color);
            case 7:
                return get(R.drawable.number_7_small, color);
            case 8:
                return get(R.drawable.number_8_small, color);
            case 9:
                return get(R.drawable.number_9_small, color);
            default:
                return get(R.drawable.number_0_small, color);
        }
    }

}
