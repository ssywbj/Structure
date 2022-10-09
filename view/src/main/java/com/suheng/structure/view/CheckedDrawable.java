package com.suheng.structure.view;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

//https://www.kancloud.cn/alex_wsc/android_art/1828599
//https://blog.csdn.net/lmj623565791/article/details/43752383
public class CheckedDrawable extends Drawable {
    private final Paint mPaint, mPaintChecked;
    //private final Context mContext;
    private final ValueAnimator mValueAnimator;
    private Bitmap mNormalBitmap, mCheckedBitmap;
    private float mRadius;
    private int mAlpha;
    private final Path mPath;
    private boolean mChecked;
    private RectF mRectCurrent;

    public CheckedDrawable(@ColorInt int color) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        mPaint.setColor(color);

        mPath = new Path();
        mPaintChecked = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

        mRectCurrent = new RectF();

        mValueAnimator = ValueAnimator.ofFloat();
    }

    public void setChecked(boolean checked) {
        mChecked = checked;
    }

    public void setCheckedBitmap(Bitmap checkedBitmap) {
        mCheckedBitmap = checkedBitmap;
    }

    public void setNormalBitmap(Bitmap normalBitmap) {
        if (normalBitmap == null) {
            return;
        }

        mNormalBitmap = normalBitmap;

        /*mCheckedBitmap = Bitmap.createBitmap(normalBitmap.getWidth(), normalBitmap.getHeight(), normalBitmap.getConfig());
        ByteBuffer byteBuffer = ByteBuffer.allocate(normalBitmap.getByteCount());
        normalBitmap.copyPixelsToBuffer(byteBuffer);
        mCheckedBitmap.eraseColor(Color.BLUE);
        mCheckedBitmap.copyPixelsFromBuffer(ByteBuffer.wrap(byteBuffer.array()));*/

        /*Bitmap bitmap = Bitmap.createBitmap(normalBitmap.getWidth(), normalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, bitmap.getWidth(), bitmap.getHeight(), paint);*/

        /*mCheckedBitmap = Bitmap.createBitmap(normalBitmap.getWidth(), normalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mCheckedBitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        paint.setColor(Color.BLUE);
        canvas.drawRect(0, 0, normalBitmap.getWidth(), normalBitmap.getHeight(), paint);*/
        /*paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        paint.setXfermode(null);*/
    }

    public static Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            return bitmapDrawable.getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public int getIntrinsicHeight() {
        return mNormalBitmap == null ? 72 * 3 : mNormalBitmap.getHeight();
    }

    @Override
    public int getIntrinsicWidth() {
        return mNormalBitmap == null ? 72 * 3 : mNormalBitmap.getWidth();
    }

    @Override
    public void setBounds(@NonNull Rect bounds) {
        super.setBounds(bounds);
        final float exactCenterX = bounds.exactCenterX();
        final float exactCenterY = bounds.exactCenterY();
        Log.v(AnimCheckBox.TAG, "setBounds, centerX: " + exactCenterX + ", centerY: " + exactCenterY + ", bounds: " + bounds.toShortString());

        final int left = bounds.left, top = bounds.top, right = bounds.right, bottom = bounds.bottom, rx = 16 * 3;
        if (mChecked) {
            mRectCurrent.set(left, top, right, bottom);
        } else {
            mRectCurrent.set(exactCenterX, exactCenterY, exactCenterX, exactCenterY);
        }

        PropertyValuesHolder dd = PropertyValuesHolder.ofFloat("dd");

        mValueAnimator.setFloatValues(1f);
        mValueAnimator.setDuration(2000);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Object animatedValue = animation.getAnimatedValue();
                if (!(animatedValue instanceof Float)) {
                    return;
                }

                mPath.reset();

                float ratio = (float) animatedValue, rratio = 1 - ratio;
                float deltaH = exactCenterX * ratio, deltaV = exactCenterY * ratio;
                float deltaRx = rx * rratio;

                float currentLeft, currentTop, currentRight, currentBottom;
                if (mChecked) {
                    mAlpha = (int) (255 * ratio);
                    currentLeft = deltaH;
                    currentRight = right - deltaH;
                    currentTop = deltaV;
                    currentBottom = bottom - deltaV;
                    mPath.addRoundRect(currentLeft, currentTop, currentRight, currentBottom, deltaRx, deltaRx, Path.Direction.CCW);
                } else {
                    mAlpha = (int) (255 * rratio);
                    currentLeft = exactCenterX - deltaH;
                    currentRight = exactCenterX + deltaH;
                    currentTop = exactCenterY - deltaV;
                    currentBottom = exactCenterY + deltaV;
                    mPath.addRoundRect(currentLeft, currentTop, currentRight, currentBottom, rx - deltaRx, rx - deltaRx, Path.Direction.CCW);
                }
                mRectCurrent.set(currentLeft, currentTop, currentRight, currentBottom);

                //Log.d(AnimCheckBox.TAG, "ratio: " + ratio + ", this: " + this);
                invalidateSelf();
            }
        });


        mValueAnimator.start();
    }

    @Override
    public void invalidateSelf() {
        super.invalidateSelf();
        Log.v(AnimCheckBox.TAG, "invalidateSelf: " + getBounds().toShortString());
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        //Log.d(AnimCheckBox.TAG, "draw, canvas: " + canvas);
        if (mNormalBitmap == null || mCheckedBitmap == null) {
            return;
        }

        canvas.drawBitmap(mNormalBitmap, 0, 0, null);

        //int cc = canvas.saveLayerAlpha(0, 0, mNormalBitmap.getWidth(), mNormalBitmap.getHeight(), mAlpha, Canvas.ALL_SAVE_FLAG);
        int cc = canvas.saveLayerAlpha(0, 0, mNormalBitmap.getWidth(), mNormalBitmap.getHeight(), 255, Canvas.ALL_SAVE_FLAG);
        canvas.clipPath(mPath, Region.Op.DIFFERENCE);
        //canvas.clipPath(mPath);
        canvas.drawBitmap(mCheckedBitmap, 0, 0, null);
        canvas.restoreToCount(cc);
    }

}
