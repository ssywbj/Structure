package com.suheng.structure.wallpaper.photoface;

import android.content.Context;

import com.suheng.structure.wallpaper.basic.utils.BitmapManager;

public class PhotoBitmapManager extends BitmapManager {

    public PhotoBitmapManager(Context context) {
        super(context);
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

    public int getBigNumberResId(int number) {
        switch (number) {
            case 1:
                return R.drawable.paint_number_1_big;
            case 2:
                return R.drawable.paint_number_2_big;
            case 3:
                return R.drawable.paint_number_3_big;
            case 4:
                return R.drawable.paint_number_4_big;
            case 5:
                return R.drawable.paint_number_5_big;
            case 6:
                return R.drawable.paint_number_6_big;
            case 7:
                return R.drawable.paint_number_7_big;
            case 8:
                return R.drawable.paint_number_8_big;
            case 9:
                return R.drawable.paint_number_9_big;
            default:
                return R.drawable.paint_number_0_big;
        }
    }


    public int getMiddleNumberResId(int number) {
        switch (number) {
            case 1:
                return R.drawable.paint_number_1_middle;
            case 2:
                return R.drawable.paint_number_2_middle;
            case 3:
                return R.drawable.paint_number_3_middle;
            case 4:
                return R.drawable.paint_number_4_middle;
            case 5:
                return R.drawable.paint_number_5_middle;
            case 6:
                return R.drawable.paint_number_6_middle;
            case 7:
                return R.drawable.paint_number_7_middle;
            case 8:
                return R.drawable.paint_number_8_middle;
            case 9:
                return R.drawable.paint_number_9_middle;
            default:
                return R.drawable.paint_number_0_middle;
        }
    }
}
