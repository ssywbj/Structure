package com.suheng.structure.wallpaper.photoface;

import android.content.Context;

import com.suheng.structure.wallpaper.basic.utils.BitmapManager;

public class PhotoBitmapManager extends BitmapManager {

    public PhotoBitmapManager(Context context) {
        super(context);
    }

    public int getBigNumberResId(int number) {
        switch (number) {
            case 1:
                return R.drawable.paint_number_ic_1;
            case 2:
                return R.drawable.paint_number_ic_2;
            case 3:
                return R.drawable.paint_number_ic_3;
            case 4:
                return R.drawable.paint_number_ic_4;
            case 5:
                return R.drawable.paint_number_ic_5;
            case 6:
                return R.drawable.paint_number_ic_6;
            case 7:
                return R.drawable.paint_number_ic_7;
            case 8:
                return R.drawable.paint_number_ic_8;
            case 9:
                return R.drawable.paint_number_ic_9;
            default:
                return R.drawable.paint_number_ic_0;
        }
    }

    public int getMiddleNumberResId(int number) {
        switch (number) {
            case 1:
                return R.drawable.paint_number_ic_1_middle;
            case 2:
                return R.drawable.paint_number_ic_2_middle;
            case 3:
                return R.drawable.paint_number_ic_3_middle;
            case 4:
                return R.drawable.paint_number_ic_4_middle;
            case 5:
                return R.drawable.paint_number_ic_5_middle;
            case 6:
                return R.drawable.paint_number_ic_6_middle;
            case 7:
                return R.drawable.paint_number_ic_7_middle;
            case 8:
                return R.drawable.paint_number_ic_8_middle;
            case 9:
                return R.drawable.paint_number_ic_9_middle;
            default:
                return R.drawable.paint_number_ic_0_middle;
        }
    }

}
