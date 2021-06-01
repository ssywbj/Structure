package com.wiz.watch.facexsports;

import android.content.Context;
import android.graphics.Bitmap;

import com.structure.wallpaper.basic.utils.BitmapManager;

public class XSportsBitmapManager extends BitmapManager {

    public XSportsBitmapManager(Context context) {
        super(context);
    }

    public Bitmap getNumberBitmap(int number, int color) {
        switch (number) {
            case 1:
                return get(R.drawable.paint_number_1, color);
            case 2:
                return get(R.drawable.paint_number_2, color);
            case 3:
                return get(R.drawable.paint_number_3, color);
            case 4:
                return get(R.drawable.paint_number_4, color);
            case 5:
                return get(R.drawable.paint_number_5, color);
            case 6:
                return get(R.drawable.paint_number_6, color);
            case 7:
                return get(R.drawable.paint_number_7, color);
            case 8:
                return get(R.drawable.paint_number_8, color);
            case 9:
                return get(R.drawable.paint_number_9, color);
            default:
                return get(R.drawable.paint_number_0, color);
        }
    }

    public Bitmap getSmallNumberBitmap(int number, int color) {
        switch (number) {
            case 1:
                return getRotate(R.drawable.paint_number_1_small, color, 90);
            case 2:
                return getRotate(R.drawable.paint_number_2_small, color, 90);
            case 3:
                return getRotate(R.drawable.paint_number_3_small, color, 90);
            case 4:
                return getRotate(R.drawable.paint_number_4_small, color, 90);
            case 5:
                return getRotate(R.drawable.paint_number_5_small, color, 90);
            case 6:
                return getRotate(R.drawable.paint_number_6_small, color, 90);
            case 7:
                return getRotate(R.drawable.paint_number_7_small, color, 90);
            case 8:
                return getRotate(R.drawable.paint_number_8_small, color, 90);
            case 9:
                return getRotate(R.drawable.paint_number_9_small, color, 90);
            default:
                return getRotate(R.drawable.paint_number_0_small, color, 90);
        }
    }

}
