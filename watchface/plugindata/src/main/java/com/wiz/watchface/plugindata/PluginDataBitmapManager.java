package com.wiz.watchface.plugindata;

import android.content.Context;
import android.graphics.Bitmap;

import com.structure.wallpaper.basic.utils.BitmapManager2;

public class PluginDataBitmapManager extends BitmapManager2 {

    public PluginDataBitmapManager(Context context) {
        super(context);
    }

    public Bitmap getNumberBitmap(int number, int color, float scale) {
        switch (number) {
            case 1:
                return get(R.drawable.number_1, color, scale);
            case 2:
                return get(R.drawable.number_2, color, scale);
            case 3:
                return get(R.drawable.number_3, color, scale);
            case 4:
                return get(R.drawable.number_4, color, scale);
            case 5:
                return get(R.drawable.number_5, color, scale);
            case 6:
                return get(R.drawable.number_6, color, scale);
            case 7:
                return get(R.drawable.number_7, color, scale);
            case 8:
                return get(R.drawable.number_8, color, scale);
            case 9:
                return get(R.drawable.number_9, color, scale);
            default:
                return get(R.drawable.number_0, color, scale);
        }
    }

}
