package com.suheng.structure.wallpaper.basic.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
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

    public Bitmap get(Context context, @DrawableRes int resId, int color) {
        Drawable drawable = ContextCompat.getDrawable(context, resId);
        if (drawable == null) {
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof VectorDrawable || drawable instanceof VectorDrawableCompat) {
            drawable.setTint(ContextCompat.getColor(mContext, color));

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

    public Bitmap get(@DrawableRes int resId, int color) {
        if (mMapBitmap.containsKey(resId)) {
            return mMapBitmap.get(resId);
        } else {
            Bitmap bitmap = get(mContext, resId, color);
            mMapBitmap.put(resId, bitmap);
            return bitmap;
        }
    }

    public Bitmap get(@DrawableRes int resId) {
        return get(resId, R.color.basic_number_color);
    }

    public Bitmap getScale(@DrawableRes int resId, int color, float ratio) {
        if (mMapBitmap.containsKey(resId)) {
            return mMapBitmap.get(resId);
        } else {
            Bitmap bitmap = get(mContext, resId, color);
            if (ratio > 0) {
                bitmap = scale(bitmap, ratio);
            }
            mMapBitmap.put(resId, bitmap);
            return bitmap;
        }
    }

    public Bitmap getScale(@DrawableRes int resId, float ratio) {
        return getScale(resId, android.R.color.holo_red_light, ratio);
    }

    public Bitmap getRotate(@DrawableRes int resId, int color, float degrees) {
        if (mMapBitmap.containsKey(resId)) {
            return mMapBitmap.get(resId);
        } else {
            Bitmap bitmap = get(mContext, resId, color);
            if (degrees != 0) {
                bitmap = rotate(bitmap, degrees);
            }
            mMapBitmap.put(resId, bitmap);
            return bitmap;
        }
    }

    public Bitmap getRotate(@DrawableRes int resId, float degrees) {
        return getRotate(resId, R.color.basic_number_color, degrees);
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

    public Bitmap getWeekBitmap() {
        return get(this.getWeekResId());
    }

    public int getWeekResId() {
        Calendar instance = Calendar.getInstance();
        switch (instance.get(Calendar.DAY_OF_WEEK)) {
            case 1:
                return R.drawable.basic_text_day;
            case 2:
                return R.drawable.basic_text_1;
            case 3:
                return R.drawable.basic_text_2;
            case 4:
                return R.drawable.basic_text_3;
            case 5:
                return R.drawable.basic_text_4;
            case 6:
                return R.drawable.basic_text_5;
            case 7:
                return R.drawable.basic_text_6;
            default:
                return R.drawable.basic_text_day;
        }
    }

    public Bitmap getNumberBitmap(int number) {
        return get(this.getNumberResId(number));
    }

    public int getNumberResId(int number) {
        switch (number) {
            case 1:
                return R.drawable.basic_number_1;
            case 2:
                return R.drawable.basic_number_2;
            case 3:
                return R.drawable.basic_number_3;
            case 4:
                return R.drawable.basic_number_4;
            case 5:
                return R.drawable.basic_number_5;
            case 6:
                return R.drawable.basic_number_6;
            case 7:
                return R.drawable.basic_number_7;
            case 8:
                return R.drawable.basic_number_8;
            case 9:
                return R.drawable.basic_number_9;
            default:
                return R.drawable.basic_number_0;
        }
    }

    public static Bitmap rotate(Bitmap src, float degrees) {
        Matrix matrix = new Matrix();
        matrix.setRotate(degrees);
        Bitmap dst = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, false);
        if (dst.equals(src)) {
            return dst;
        }
        //src.recycle();
        return dst;
    }

    /**
     * 按比例缩放图片
     */
    public static Bitmap scale(Bitmap src, float ratio) {
        Matrix matrix = new Matrix();
        matrix.preScale(ratio, ratio);
        Bitmap dst = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, false);
        if (dst.equals(src)) {
            return dst;
        }
        src.recycle();
        return dst;
    }

    /**
     * 两个等高的位图左右拼接
     */
    public static Bitmap mergeLeftRight(Bitmap left, Bitmap right) {
        if (left.getHeight() != right.getHeight()) {
            return null;
        }

        int height = left.getHeight();//拼接后的高度
        int width = left.getWidth() + right.getWidth();//拼接后的宽度

        Bitmap dst = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(dst);

        //缩放后两个bitmap需要绘制的参数
        Rect leftRect = new Rect(0, 0, left.getWidth(), left.getHeight());
        Rect rightRect = new Rect(0, 0, right.getWidth(), right.getHeight());

        //右边图需要绘制的位置，往右边偏移左边图的宽度，高度是相同的
        Rect rightRectT = new Rect(left.getWidth(), 0, width, height);

        canvas.drawBitmap(left, leftRect, leftRect, null);
        canvas.drawBitmap(right, rightRect, rightRectT, null);
        return dst;
    }

}
