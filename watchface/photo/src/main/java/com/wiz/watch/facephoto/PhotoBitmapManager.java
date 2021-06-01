package com.wiz.watch.facephoto;

import android.content.Context;
import android.graphics.Bitmap;

import com.structure.wallpaper.basic.utils.BitmapManager2;

public class PhotoBitmapManager extends BitmapManager2 {
    private boolean mIsRoundScreen;

    public PhotoBitmapManager(Context context) {
        super(context);
    }

    public void setRoundScreen(boolean roundScreen) {
        mIsRoundScreen = roundScreen;
    }

    public Bitmap getNumberBitmap(int number, int color, float scale) {
        switch (number) {
            case 1:
                if (mIsRoundScreen) {
                    return get(R.drawable.round_number_1, color, scale);
                } else {
                    return get(R.drawable.number_1, color, scale);
                }
            case 2:
                if (mIsRoundScreen) {
                    return get(R.drawable.round_number_2, color, scale);
                } else {
                    return get(R.drawable.number_2, color, scale);
                }
            case 3:
                if (mIsRoundScreen) {
                    return get(R.drawable.round_number_3, color, scale);
                } else {
                    return get(R.drawable.number_3, color, scale);
                }
            case 4:
                if (mIsRoundScreen) {
                    return get(R.drawable.round_number_4, color, scale);
                } else {
                    return get(R.drawable.number_4, color, scale);
                }
            case 5:
                if (mIsRoundScreen) {
                    return get(R.drawable.round_number_5, color, scale);
                } else {
                    return get(R.drawable.number_5, color, scale);
                }
            case 6:
                if (mIsRoundScreen) {
                    return get(R.drawable.round_number_6, color, scale);
                } else {
                    return get(R.drawable.number_6, color, scale);
                }
            case 7:
                if (mIsRoundScreen) {
                    return get(R.drawable.round_number_7, color, scale);
                } else {
                    return get(R.drawable.number_7, color, scale);
                }
            case 8:
                if (mIsRoundScreen) {
                    return get(R.drawable.round_number_8, color, scale);
                } else {
                    return get(R.drawable.number_8, color, scale);
                }
            case 9:
                if (mIsRoundScreen) {
                    return get(R.drawable.round_number_9, color, scale);
                } else {
                    return get(R.drawable.number_9, color, scale);
                }
            default:
                if (mIsRoundScreen) {
                    return get(R.drawable.round_number_0, color, scale);
                } else {
                    return get(R.drawable.number_0, color, scale);
                }
        }
    }

    public Bitmap getPointBitmap(int color, float scale) {
        if (mIsRoundScreen) {
            return get(R.drawable.round_point_sign, color, scale);
        } else {
            return get(R.drawable.point_sign, color, scale);
        }
    }

}
