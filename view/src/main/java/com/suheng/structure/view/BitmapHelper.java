package com.suheng.structure.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

public class BitmapHelper {

    public static Bitmap get(Context context, @DrawableRes int resId, int color, float scale) {
        Drawable drawable = ContextCompat.getDrawable(context, resId);
        if (color != Integer.MAX_VALUE) {
            drawable.setTint(color);
        }
        return drawableToBitmap(drawable, scale);
    }

    public static Bitmap get(Context context, @DrawableRes int resId, int color) {
        return get(context, resId, color, 1);
    }

    public static Bitmap get(Context context, @DrawableRes int resId, float scale) {
        return get(context, resId, Integer.MAX_VALUE, scale);
    }

    public static Bitmap get(Context context, @DrawableRes int resId) {
        return get(context, resId, Integer.MAX_VALUE);
    }

    public static Bitmap drawableToBitmap(Drawable drawable, float scale) {
        final int intrinsicWidth = drawable.getIntrinsicWidth();
        final int intrinsicHeight = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap((int) (intrinsicWidth * scale), (int) (intrinsicHeight * scale), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.FILTER_BITMAP_FLAG));
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        return drawableToBitmap(drawable, 1);
    }

    public static Bitmap rotate(Bitmap src, float degrees) {
        Matrix matrix = new Matrix();
        matrix.setRotate(degrees);
        Bitmap dst = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, false);
        if (dst.equals(src)) {
            return dst;
        }
        src.recycle();
        return dst;
    }

    /**
     * 按比例缩放图片
     */
    public static Bitmap scale(Bitmap src, float ratio, boolean isRecycle) {
        Matrix matrix = new Matrix();
        matrix.preScale(ratio, ratio);
        Bitmap dst = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, false);
        if (dst.equals(src)) {
            return dst;
        }

        if (isRecycle) {
            src.recycle();
        }
        return dst;
    }

    public static Bitmap scale(Bitmap src, float ratio) {
        return scale(src, ratio, false);
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
