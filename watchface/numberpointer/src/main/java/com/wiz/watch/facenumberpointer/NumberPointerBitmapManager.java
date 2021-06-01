package com.wiz.watch.facenumberpointer;

import android.content.Context;
import android.graphics.Bitmap;

import com.structure.wallpaper.basic.utils.BitmapManager;

public class NumberPointerBitmapManager extends BitmapManager {

    public NumberPointerBitmapManager(Context context) {
        super(context);
    }

    public Bitmap getNumber(int number, int color) {
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

}
