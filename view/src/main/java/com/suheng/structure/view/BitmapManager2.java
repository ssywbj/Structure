package com.suheng.structure.view;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.DrawableRes;

import java.util.HashMap;
import java.util.Map;

public class BitmapManager2 {

    private final Context mContext;
    private final Map<String, Bitmap> mMapBitmap;

    public BitmapManager2(Context context) {
        mContext = context;
        mMapBitmap = new HashMap<>();
    }

    public Bitmap get(@DrawableRes int resId, int color, float scale) {
        String key = resId + "_" + color + "_" + scale;
        if (mMapBitmap.containsKey(key)) {
            return mMapBitmap.get(key);
        } else {
            Bitmap bitmap = BitmapHelper.get(mContext, resId, color, scale);
            mMapBitmap.put(key, bitmap);
            return bitmap;
        }
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
