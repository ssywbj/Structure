package com.wiz.watch.facepeach;

import android.content.Context;
import android.graphics.Bitmap;

import com.structure.wallpaper.basic.utils.BitmapManager;
import com.wiz.watch.facepeach.interfaces.INumberBitmapManager;
import com.wiz.watch.facepeach.interfaces.IPunctuationBitmapManager;

public class PeachBitmapManager extends BitmapManager implements IPunctuationBitmapManager, INumberBitmapManager {
    public PeachBitmapManager(Context context) {
        super(context);
    }

    @Override
    public Bitmap getNumber(int number, int type, int color) {
        int id;
        switch (number) {
            case 1:
                id = R.drawable.ic_number_1;
                break;
            case 2:
                id = R.drawable.ic_number_2;
                break;
            case 3:
                id = R.drawable.ic_number_3;
                break;
            case 4:
                id = R.drawable.ic_number_4;
                break;
            case 5:
                id = R.drawable.ic_number_5;
                break;
            case 6:
                id = R.drawable.ic_number_6;
                break;
            case 7:
                id = R.drawable.ic_number_7;
                break;
            case 8:
                id = R.drawable.ic_number_8;
                break;
            case 9:
                id = R.drawable.ic_number_9;
                break;
            default:
                id = R.drawable.ic_number_0;
                break;
        }
        return get(id, color);
    }

    @Override
    public Bitmap getPunctuation(char sign, int type, int color) {
        int id;
        switch (sign) {
            case ':':
                id = R.drawable.ic_colon;
                break;
            default:
                id = R.drawable.ic_point;
                break;
        }
        return get(id, color);
    }
}
