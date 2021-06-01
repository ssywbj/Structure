package com.wiz.watch.faceclassic;

import android.content.Context;
import android.graphics.Bitmap;

import com.structure.wallpaper.basic.utils.BitmapManager;

public class ClassicBitmapManager extends BitmapManager {

    public ClassicBitmapManager(Context context) {
        super(context);
    }

    public Bitmap getNumberBitmap(int number, int color) {
        switch (number) {
            case 1:
                return get(R.drawable.paint_number_ic_1, color);
            case 2:
                return get(R.drawable.paint_number_ic_2, color);
            case 3:
                return get(R.drawable.paint_number_ic_3, color);
            case 4:
                return get(R.drawable.paint_number_ic_4, color);
            case 5:
                return get(R.drawable.paint_number_ic_5, color);
            case 6:
                return get(R.drawable.paint_number_ic_6, color);
            case 7:
                return get(R.drawable.paint_number_ic_7, color);
            case 8:
                return get(R.drawable.paint_number_ic_8, color);
            case 9:
                return get(R.drawable.paint_number_ic_9, color);
            case 10:
                return getMerge(R.drawable.paint_number_ic_1, color, R.drawable.paint_number_ic_0, color);
            case 11:
                return getMerge(R.drawable.paint_number_ic_1, color, R.drawable.paint_number_ic_1, color);
            case 12:
                return getMerge(R.drawable.paint_number_ic_1, color, R.drawable.paint_number_ic_2, color);
            default:
                return get(R.drawable.paint_number_ic_0, color);
        }
    }

}
