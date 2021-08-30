package com.suheng.wallpaper.basic.manager;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.DrawableRes;

import com.suheng.wallpaper.basic.utils.BitmapHelper;

import java.util.HashMap;
import java.util.Map;

public class BitmapManager {

    private final Context mContext;
    private final Map<String, Bitmap> mMapBitmap;

    public BitmapManager(Context context) {
        mContext = context;
        mMapBitmap = new HashMap<>();
    }

    public Bitmap get(@DrawableRes int resId, int color, float scale, float degrees) {
        String key = resId + "_" + color + "_" + scale + "_" + degrees;
        if (mMapBitmap.containsKey(key)) {
            return mMapBitmap.get(key);
        } else {
            Bitmap bitmap = BitmapHelper.get(mContext, resId, color, scale, degrees);
            mMapBitmap.put(key, bitmap);
            return bitmap;
        }
    }

    public Bitmap get(@DrawableRes int resId, int color, float scale) {
        return get(resId, color, scale, 0);
    }

    public Bitmap get(@DrawableRes int resId, int color) {
        return get(resId, color, 1);
    }

    public Bitmap get(@DrawableRes int resId, float scale) {
        return get(resId, Integer.MAX_VALUE, scale);
    }

    public Bitmap get(@DrawableRes int resId) {
        return get(resId, Integer.MAX_VALUE);
    }

    public Bitmap getRotate(@DrawableRes int resId, int color, float scale, float degrees) {
        return get(resId, color, scale, degrees);
    }

    public Bitmap getRotate(@DrawableRes int resId, float scale, float degrees) {
        return getRotate(resId, Integer.MAX_VALUE, scale, degrees);
    }

    public Bitmap getRotate(@DrawableRes int resId, float degrees) {
        return getRotate(resId, 1, degrees);
    }

    public Bitmap getMerge(@DrawableRes int leftId, int leftColor, @DrawableRes int rightId, int rightColor, float scale) {
        String key = leftId + "_" + leftColor + "_" + rightId + "_" + rightColor + "_" + scale;
        if (mMapBitmap.containsKey(key)) {
            return mMapBitmap.get(key);
        } else {
            Bitmap bitmap = BitmapHelper.mergeLeftRight(get(leftId, leftColor, scale), get(rightId, rightColor, scale));
            mMapBitmap.put(key, bitmap);
            return bitmap;
        }
    }

    public Bitmap getMerge(@DrawableRes int leftId, int leftColor, @DrawableRes int rightId, int rightColor) {
        return getMerge(leftId, leftColor, rightId, rightColor, 1);
    }

    public Bitmap getMerge(@DrawableRes int leftId, @DrawableRes int rightId) {
        return getMerge(leftId, Integer.MAX_VALUE, rightId, Integer.MAX_VALUE, 1);
    }

    public void clear() {
        for (Map.Entry<String, Bitmap> bitmapEntry : mMapBitmap.entrySet()) {
            Bitmap bitmap = bitmapEntry.getValue();
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }

        mMapBitmap.clear();
    }

}
