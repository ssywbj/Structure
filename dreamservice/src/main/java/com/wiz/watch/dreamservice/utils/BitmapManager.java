package com.wiz.watch.dreamservice.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
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

    private Context mContext;
    private Map<String, Bitmap> mMapBitmap = new HashMap<>();

    public BitmapManager(Context context) {
        mContext = context;
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

    public Bitmap get(@DrawableRes int resId) {
        return get(resId, Integer.MAX_VALUE);
    }

    /**
     * Drawable或SVG转Bitmap
     */
    public static Bitmap get(Context context, @DrawableRes int resId, int tintColor) {
        Drawable drawable = ContextCompat.getDrawable(context, resId);
        if (drawable == null) {
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof VectorDrawable || drawable instanceof VectorDrawableCompat) {
            if (tintColor != Integer.MAX_VALUE) {
                drawable.setTint(tintColor);
            }

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

    public Bitmap getMerge(@DrawableRes int leftId, int leftColor, @DrawableRes int rightId, int rightColor) {
        String key = "mergeLR_" + leftId + "" + leftColor + "" + rightId + "" + rightColor;
        if (mMapBitmap.containsKey(key)) {
            return mMapBitmap.get(key);
        } else {
            Bitmap bitmap = mergeLeftRight(this.get(leftId, leftColor), this.get(rightId, rightColor), true);
            mMapBitmap.put(key, bitmap);
            return bitmap;
        }
    }

    public Bitmap getMerge(@DrawableRes int leftId, @DrawableRes int rightId) {
        return getMerge(leftId, Integer.MAX_VALUE, rightId, Integer.MAX_VALUE);
    }

    public Bitmap getMerge(@DrawableRes int leftId, int leftColor, @DrawableRes int centerId
            , int centerColor, @DrawableRes int rightId, int rightColor) {
        String key = "mergeLR_" + leftId + "" + leftColor + "" + centerId + "" + centerColor + "" + rightId + "" + rightColor;
        if (mMapBitmap.containsKey(key)) {
            return mMapBitmap.get(key);
        } else {
            Bitmap bitmap = mergeLeftRight(this.get(leftId, leftColor), this.get(centerId, centerColor), true);
            Bitmap dst = mergeLeftRight(bitmap, this.get(rightId, rightColor), true);
            mMapBitmap.put(key, dst);
            bitmap.recycle();
            return dst;
        }
    }

    public Bitmap getMerge(@DrawableRes int leftId, @DrawableRes int centerId, @DrawableRes int rightId) {
        return getMerge(rightId, Integer.MAX_VALUE, centerId, Integer.MAX_VALUE, rightId, Integer.MAX_VALUE);
    }

    public Bitmap getRotate(@DrawableRes int resId, int color, float degrees) {
        String key = "rotate_" + resId + "" + color + "_" + degrees;
        if (mMapBitmap.containsKey(key)) {
            return mMapBitmap.get(key);
        } else {
            Bitmap bitmap = this.get(resId, color);
            if (degrees != 0) {
                bitmap = rotate(bitmap, degrees);
            }
            mMapBitmap.put(key, bitmap);
            return bitmap;
        }
    }

    public Bitmap getRotate(@DrawableRes int resId, float degrees) {
        return getRotate(resId, Integer.MAX_VALUE, degrees);
    }

    /**
     * 图片左右拼接
     */
    public static Bitmap mergeLeftRight(Bitmap left, Bitmap right, boolean baseMax) {
        int height;//拼接后的高度，按照参数取大或取小
        if (baseMax) {
            height = Math.max(left.getHeight(), right.getHeight());
        } else {
            height = Math.min(left.getHeight(), right.getHeight());
        }

        Bitmap tempLeft = left;//缩放后的bitmap
        Bitmap tempRight = right;
        if (left.getHeight() != height) {
            tempLeft = Bitmap.createScaledBitmap(left, (int) (1.0f * left.getWidth() / left.getHeight() * height), height, false);
        } else if (right.getHeight() != height) {
            tempRight = Bitmap.createScaledBitmap(right, (int) (1.0f * right.getWidth() / right.getHeight() * height), height, false);
        }

        int width = tempLeft.getWidth() + tempRight.getWidth();//拼接后的宽度
        Bitmap dst = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);//定义输出的bitmap
        Canvas canvas = new Canvas(dst);

        //缩放后两个bitmap需要绘制的参数
        Rect leftRect = new Rect(0, 0, tempLeft.getWidth(), tempLeft.getHeight());
        Rect rightRect = new Rect(0, 0, tempRight.getWidth(), tempRight.getHeight());

        //右边图需要绘制的位置，往右边偏移左边图的宽度，高度相同
        Rect rightRectT = new Rect(tempLeft.getWidth(), 0, width, height);
        canvas.drawBitmap(tempLeft, leftRect, leftRect, null);
        canvas.drawBitmap(tempRight, rightRect, rightRectT, null);
        return dst;
    }

    /**
     * 图片旋转
     */
    public static Bitmap rotate(Bitmap src, float degrees) {
        Matrix matrix = new Matrix();
        matrix.setRotate(degrees);
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, false);
    }

    public static Bitmap scale(Drawable drawable, float scale) {
        return scale(drawable, scale, Color.WHITE);
    }

    public static Bitmap scale(Drawable drawable, float scale, int color) {
        drawable.setTint(color);

        final int intrinsicWidth = drawable.getIntrinsicWidth();
        final int intrinsicHeight = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap((int) (intrinsicWidth * scale), (int) (intrinsicHeight * scale), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.FILTER_BITMAP_FLAG));
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

}
