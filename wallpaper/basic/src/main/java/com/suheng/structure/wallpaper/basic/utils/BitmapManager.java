package com.suheng.structure.wallpaper.basic.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.suheng.structure.wallpaper.basic.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class BitmapManager {

    private Context mContext;
    private Map<Integer, Bitmap> mMapBitmap = new HashMap<>();

    public BitmapManager(Context context) {
        mContext = context;
    }

    public Bitmap get(@DrawableRes int resId) {
        if (mMapBitmap.containsKey(resId)) {
            return mMapBitmap.get(resId);
        } else {
            Bitmap bitmap = get(mContext, resId);
            mMapBitmap.put(resId, bitmap);
            return bitmap;
        }
    }

    public void clear() {
        for (Map.Entry<Integer, Bitmap> bitmapEntry : mMapBitmap.entrySet()) {
            Bitmap bitmap = bitmapEntry.getValue();
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }

        mMapBitmap.clear();
    }

    private Bitmap get(Context context, @DrawableRes int resId) {
        Drawable drawable = ContextCompat.getDrawable(context, resId);
        if (drawable == null) {
            return null;
        }

        /*if (resId == R.drawable.basic_icon_weather_day_duoyun) {
            drawable.setTint(getResources().getColor(R.color.colorPrimary));
        } else if (resId == R.drawable.notify) {
            drawable.setTint(getResources().getColor(R.color.colorPrimary));
        }*/

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof VectorDrawable || drawable instanceof VectorDrawableCompat) {
            int intrinsicWidth = drawable.getIntrinsicWidth();
            int intrinsicHeight = drawable.getIntrinsicHeight();
            Bitmap bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            int width = canvas.getWidth();
            int height = canvas.getHeight();
            drawable.setBounds(0, 0, width, height);
            drawable.draw(canvas);
            return bitmap;
        } else {
            throw new IllegalArgumentException("unsupported drawable type");
        }
    }

    public Bitmap getWeekBitmap() {
        int resId;

        Calendar instance = Calendar.getInstance();
        switch (instance.get(Calendar.DAY_OF_WEEK)) {
            case 1:
                resId = R.drawable.basic_text_day;
                break;
            case 2:
                resId = R.drawable.basic_text_1;
                break;
            case 3:
                resId = R.drawable.basic_text_2;
                break;
            case 4:
                resId = R.drawable.basic_text_3;
                break;
            case 5:
                resId = R.drawable.basic_text_4;
                break;
            case 6:
                resId = R.drawable.basic_text_5;
                break;
            case 7:
                resId = R.drawable.basic_text_6;
                break;
            default:
                resId = R.drawable.basic_text_day;
        }

        return get(resId);
    }

    public Bitmap getNumberBitmap(int number) {
        int resId;
        switch (number) {
            case 1:
                resId = R.drawable.basic_number_1;
                break;
            case 2:
                resId = R.drawable.basic_number_2;
                break;
            case 3:
                resId = R.drawable.basic_number_3;
                break;
            case 4:
                resId = R.drawable.basic_number_4;
                break;
            case 5:
                resId = R.drawable.basic_number_5;
                break;
            case 6:
                resId = R.drawable.basic_number_6;
                break;
            case 7:
                resId = R.drawable.basic_number_7;
                break;
            case 8:
                resId = R.drawable.basic_number_8;
                break;
            case 9:
                resId = R.drawable.basic_number_9;
                break;
            default:
                resId = R.drawable.basic_number_0;
        }

        return get(resId);
    }

    public static Bitmap rotate(Bitmap src, float degrees) {
        Matrix matrix = new Matrix();
        matrix.setRotate(degrees);
        Bitmap dst = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, false);
        if (dst.equals(src)) {
            return src;
        }
        return dst;
    }

}
