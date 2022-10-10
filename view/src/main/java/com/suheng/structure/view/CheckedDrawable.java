package com.suheng.structure.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

//https://www.kancloud.cn/alex_wsc/android_art/1828599
//https://blog.csdn.net/lmj623565791/article/details/43752383
public class CheckedDrawable extends Drawable {
    private static final String PVH_LEFT = "pvh_left";
    private static final String PVH_TOP = "pvh_top";
    private static final String PVH_RADIUS = "pvh_radius";
    private static final String PVH_ALPHA = "pvh_alpha";
    private final Paint mPaint;
    private final ValueAnimator mValueAnimator;
    private final Context mContext;

    private Bitmap mNormalBitmap, mCheckedBitmap;
    private final Path mPath;
    private boolean mChecked;

    private int mCurrentLeft, mCurrentTop;
    private int mAlpha = 255, mCurrentAlpha;
    private float mRadius = 10.67f * 3, mCurrentRadius;

    public CheckedDrawable(Context context, boolean isChecked) {
        mContext = context;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

        mPath = new Path();
        mValueAnimator = ValueAnimator.ofFloat();

        this.setBitmap();
        this.setChecked(isChecked);
    }

    public CheckedDrawable(Context context) {
        this(context, false);
    }

    private void setChecked(boolean checked) {
        mChecked = checked;

        mCurrentLeft = checked ? this.getIntrinsicWidth() / 2 : 0;
        mCurrentTop = checked ? this.getIntrinsicHeight() / 2 : 0;
        mCurrentAlpha = checked ? mAlpha : 0;
        mCurrentRadius = checked ? 0 : mRadius;
    }

    private void setBitmap() {
        mNormalBitmap = CheckedDrawable.drawable2Bitmap(ContextCompat.getDrawable(mContext, R.drawable.checkbox_unchecked));
        mCheckedBitmap = CheckedDrawable.drawable2Bitmap(ContextCompat.getDrawable(mContext, R.drawable.checkbox_checked_bg));
        /*mCheckedBitmap = Bitmap.createBitmap(mNormalBitmap.getWidth(), mNormalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mCheckedBitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        paint.setColor(Color.BLUE);
        canvas.drawRoundRect(0, 0, mNormalBitmap.getWidth(), mNormalBitmap.getHeight(), mRadius, mRadius, paint);*/
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
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public int getIntrinsicHeight() {
        return mNormalBitmap == null ? mContext.getResources().getDimensionPixelSize(R.dimen.checked_box_wh) : mNormalBitmap.getHeight();
    }

    @Override
    public int getIntrinsicWidth() {
        return mNormalBitmap == null ? mContext.getResources().getDimensionPixelSize(R.dimen.checked_box_wh) : mNormalBitmap.getWidth();
    }

    /*@Override
    public void setBounds(@NonNull Rect bounds) {
        super.setBounds(bounds);
        Log.v(AnimCheckBox.TAG, "setBounds, bounds: " + bounds.toShortString() + ", mChecked: " + mChecked);
    }*/

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (mNormalBitmap == null || mCheckedBitmap == null) {
            return;
        }

        canvas.drawBitmap(mNormalBitmap, 0, 0, null);

        int cc = canvas.saveLayerAlpha(0, 0, mNormalBitmap.getWidth(), mNormalBitmap.getHeight(), mCurrentAlpha, Canvas.ALL_SAVE_FLAG);
        //int cc = canvas.saveLayerAlpha(0, 0, mNormalBitmap.getWidth(), mNormalBitmap.getHeight(), 255, Canvas.ALL_SAVE_FLAG);
        canvas.clipPath(mPath, Region.Op.DIFFERENCE);
        canvas.drawBitmap(mCheckedBitmap, 0, 0, null);
        canvas.restoreToCount(cc);
    }

    public void setAnimParams(int currentLeft, int currentTop, int currentAlpha, float currentRadius) {
        final Rect bounds = getBounds();
        final int centerX = bounds.centerX();
        final int centerY = bounds.centerY();
        /*Log.v(AnimCheckBox.TAG, "startAnim, centerX: " + centerX + ", centerY: " + centerY
                + ", bounds: " + bounds.toShortString() + ", mChecked: " + mChecked);*/

        int endLeft, endTop, endAlpha;
        float endRadius;
        if (mChecked) {
            /*currentLeft = bounds.left;
            currentTop = bounds.top;*/
            endLeft = centerX;
            endTop = centerY;
            endAlpha = mAlpha;
            endRadius = 0f;
        } else {
            /*currentLeft = centerX;
            currentTop = centerY;*/
            endLeft = bounds.left;
            endTop = bounds.top;
            endAlpha = 0;
            endRadius = mRadius;
        }
        /*Log.v(AnimCheckBox.TAG, "startAnim, currentLeft-endLeft: " + currentLeft + "-" + endLeft
                + ", currentTop-endTop: " + currentTop + "-" + endTop
                + ", currentAlpha-endAlpha: " + currentAlpha + "-" + endAlpha
                + ", currentRadius-endRadius: " + currentRadius + "-" + endRadius);*/
        mValueAnimator.setValues(PropertyValuesHolder.ofInt(PVH_LEFT, currentLeft, endLeft)
                , PropertyValuesHolder.ofInt(PVH_TOP, currentTop, endTop)
                , PropertyValuesHolder.ofInt(PVH_ALPHA, currentAlpha, endAlpha)
                , PropertyValuesHolder.ofFloat(PVH_RADIUS, currentRadius, endRadius));
        //mValueAnimator.setDuration(1500);
        mValueAnimator.setDuration(500);

        final ValueAnimator.AnimatorUpdateListener animatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Object valueLeft = animation.getAnimatedValue(PVH_LEFT);
                Object valueTop = animation.getAnimatedValue(PVH_TOP);
                Object valueRadius = animation.getAnimatedValue(PVH_RADIUS);
                Object valueAlpha = animation.getAnimatedValue(PVH_ALPHA);
                if (!((valueLeft instanceof Integer) && (valueTop instanceof Integer) && (valueRadius instanceof Float)
                        && (valueAlpha instanceof Integer))) {
                    return;
                }

                mCurrentLeft = (int) valueLeft;
                mCurrentTop = (int) valueTop;
                mCurrentAlpha = (int) valueAlpha;
                mCurrentRadius = (float) valueRadius;
                //Log.d(AnimCheckBox.TAG, "mCurrentRadius: " + mCurrentRadius + ", mCurrentAlpha: " + mCurrentAlpha + ", mCurrentLeft: " + mCurrentLeft + ", mCurrentTop: " + mCurrentTop + ", this: " + this);

                mPath.reset();
                mPath.addRoundRect(mCurrentLeft, mCurrentTop, bounds.right - mCurrentLeft, bounds.bottom - mCurrentTop
                        , mCurrentRadius, mCurrentRadius, Path.Direction.CCW);

                invalidateSelf();
            }
        };
        mValueAnimator.addUpdateListener(animatorUpdateListener);
        mValueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mValueAnimator.removeUpdateListener(animatorUpdateListener);
                mValueAnimator.removeListener(this);
            }
        });
    }

    public void startAnim() {
        mValueAnimator.start();
    }

    public void cancelAnim() {
        if (this.isAnimRunning()) {
            mValueAnimator.cancel();
        }
    }

    public boolean isAnimRunning() {
        return mValueAnimator.isRunning();
    }

    public int getCurrentLeft() {
        return mCurrentLeft;
    }

    public int getCurrentTop() {
        return mCurrentTop;
    }

    public float getCurrentRadius() {
        return mCurrentRadius;
    }

    public int getCurrentAlpha() {
        return mCurrentAlpha;
    }

}
