package com.suheng.structure.view;

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

import java.util.HashMap;
import java.util.Map;

public class BitmapManager {

    private final Context mContext;
    private final Map<String, Bitmap> mMapBitmap = new HashMap<>();

    public BitmapManager(Context context) {
        mContext = context;
    }

    public static Bitmap get(Context context, @DrawableRes int resId, int color) {
        Drawable drawable = ContextCompat.getDrawable(context, resId);
        if (drawable == null) {
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof VectorDrawable || drawable instanceof VectorDrawableCompat) {
            drawable.setTint(ContextCompat.getColor(context, color));

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
        String key = resId + "" + color;
        if (mMapBitmap.containsKey(key)) {
            return mMapBitmap.get(key);
        } else {
            Bitmap bitmap = get(mContext, resId, color);
            mMapBitmap.put(key, bitmap);
            return bitmap;
        }
    }

    public Bitmap get(Context context, @DrawableRes int resId) {
        Drawable drawable = ContextCompat.getDrawable(context, resId);
        if (drawable == null) {
            return null;
        }

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

    public Bitmap get(@DrawableRes int resId) {
        String key = resId + "";
        if (mMapBitmap.containsKey(key)) {
            return mMapBitmap.get(key);
        } else {
            Bitmap bitmap = get(mContext, resId);
            mMapBitmap.put(key, bitmap);
            return bitmap;
        }
    }

    public Bitmap getScale(@DrawableRes int resId, int color, float ratio) {
        String key = "scale_" + resId + "" + color + "_" + ratio;
        if (mMapBitmap.containsKey(key)) {
            return mMapBitmap.get(key);
        } else {
            Bitmap bitmap = get(mContext, resId, color);
            if (ratio > 0) {
                bitmap = scale(bitmap, ratio);
            }
            mMapBitmap.put(key, bitmap);
            return bitmap;
        }
    }

    public Bitmap getScale(@DrawableRes int resId, float ratio) {
        return getScale(resId, android.R.color.white, ratio);
    }

    public Bitmap getRotate(@DrawableRes int resId, int color, float degrees) {
        String key = "rotate_" + resId + "" + color + "_" + degrees;
        if (mMapBitmap.containsKey(key)) {
            return mMapBitmap.get(key);
        } else {
            Bitmap bitmap = get(mContext, resId, color);
            if (degrees != 0) {
                bitmap = rotate(bitmap, degrees);
            }
            mMapBitmap.put(key, bitmap);
            return bitmap;
        }
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

    public static Bitmap rotate(Bitmap src, float degrees) {
        Matrix matrix = new Matrix();
        matrix.setRotate(degrees);
        Bitmap dst = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        if (dst.equals(src)) {
            return dst;
        }
        src.recycle();
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

    public Bitmap getMerge(@DrawableRes int leftId, int leftColor, @DrawableRes int rightId, int rightColor, float scale) {
        String key = leftId + "_" + leftColor + "_" + rightId + "_" + rightColor + "_" + scale;
        if (mMapBitmap.containsKey(key)) {
            return mMapBitmap.get(key);
        } else {
            Bitmap bitmap = mergeLeftRight(getScale(leftId, leftColor, scale), getScale(rightId, rightColor, scale));
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
}
