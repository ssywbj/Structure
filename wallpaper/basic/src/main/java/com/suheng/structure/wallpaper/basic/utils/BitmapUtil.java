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

public class BitmapUtil {

    /*public static Bitmap getBitmap(Context context, int vectorDrawableId) {
        Bitmap bitmap;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            Drawable vectorDrawable = context.getDrawable(vectorDrawableId);
            bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                    vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            vectorDrawable.draw(canvas);
        } else {
            bitmap = BitmapFactory.decodeResource(context.getResources(), vectorDrawableId);
        }
        return bitmap;
    }*/

    public static Bitmap getFromDrawable(Context context, @DrawableRes int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (drawable == null) {
            return null;
        }

        /*if (drawableId == R.drawable.boneblack_icon_weather_day_duoyun) {
            drawable.setTint(getResources().getColor(R.color.colorPrimary));
        } else if (drawableId == R.drawable.notify) {
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

    public static Bitmap getWeekBitmap(Context context) {
        Calendar instance = Calendar.getInstance();
        switch (instance.get(Calendar.DAY_OF_WEEK)) {
            case 1:
                return getFromDrawable(context, R.drawable.basic_text_day);
            case 2:
                return getFromDrawable(context, R.drawable.basic_text_1);
            case 3:
                return getFromDrawable(context, R.drawable.basic_text_2);
            case 4:
                return getFromDrawable(context, R.drawable.basic_text_3);
            case 5:
                return getFromDrawable(context, R.drawable.basic_text_4);
            case 6:
                return getFromDrawable(context, R.drawable.basic_text_5);
            case 7:
                return getFromDrawable(context, R.drawable.basic_text_6);
            default:
                return getFromDrawable(context, R.drawable.basic_text_day);
        }
    }

    public static Bitmap rotate(Bitmap src, float degrees) {
        Matrix matrix = new Matrix();
        matrix.setRotate(degrees);
        Bitmap dst = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, false);
        if (dst.equals(src)) {
            return dst;
        }
        return dst;
    }

}
