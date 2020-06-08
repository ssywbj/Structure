package com.suheng.structure.wallpaper.myhealth;

import android.content.Context;

import com.suheng.structure.wallpaper.basic.utils.BitmapManager;

public class MyHealthBitmapManager extends BitmapManager {

    public MyHealthBitmapManager(Context context) {
        super(context);
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

    public int getBigNumberResId(int number) {
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

    public int getSmallNumberResId(int number) {
        switch (number) {
            case 1:
                return R.drawable.paint_number_1_small;
            case 2:
                return R.drawable.paint_number_2_small;
            case 3:
                return R.drawable.paint_number_3_small;
            case 4:
                return R.drawable.paint_number_4_small;
            case 5:
                return R.drawable.paint_number_5_small;
            case 6:
                return R.drawable.paint_number_6_small;
            case 7:
                return R.drawable.paint_number_7_small;
            case 8:
                return R.drawable.paint_number_8_small;
            case 9:
                return R.drawable.paint_number_9_small;
            default:
                return R.drawable.paint_number_0_small;
        }
    }

    public int getSmallerNumberResId(int number) {
        switch (number) {
            case 1:
                return R.drawable.paint_number_1_smaller;
            case 2:
                return R.drawable.paint_number_2_smaller;
            case 3:
                return R.drawable.paint_number_3_smaller;
            case 4:
                return R.drawable.paint_number_4_smaller;
            case 5:
                return R.drawable.paint_number_5_smaller;
            case 6:
                return R.drawable.paint_number_6_smaller;
            case 7:
                return R.drawable.paint_number_7_smaller;
            case 8:
                return R.drawable.paint_number_8_smaller;
            case 9:
                return R.drawable.paint_number_9_smaller;
            default:
                return R.drawable.paint_number_0_smaller;
        }
    }
}
