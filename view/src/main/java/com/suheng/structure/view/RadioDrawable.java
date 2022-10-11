package com.suheng.structure.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class RadioDrawable extends Drawable {
    private static final String PVH_LEFT = "pvh_left";
    private static final String PVH_TOP = "pvh_top";
    private static final String PVH_RADIUS = "pvh_radius";
    private static final String PVH_ALPHA = "pvh_alpha";
    private final Paint mPaint;
    private final ValueAnimator mAnimatorStrokeColor, mAnimatorCheckedOuter, mAnimatorCheckedInner;
    private AnimatorSet mAnimatorSet;
    private final Context mContext;

    private Bitmap mNormalBitmap, mCheckedBitmap;
    private final Path mPathCheckedOuter, mPathCheckedInner;
    private boolean mChecked;
    private int mStrokeColor = 0xFF000000;
    private float mRadius, mCurrentRadius;

    public RadioDrawable(Context context, boolean isChecked) {
        mContext = context;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

        mPathCheckedOuter = new Path();
        mPathCheckedInner = new Path();

        mAnimatorSet = new AnimatorSet();
        mAnimatorStrokeColor = ValueAnimator.ofArgb(0);
        mAnimatorCheckedOuter = ValueAnimator.ofFloat(0);
        mAnimatorCheckedInner = ValueAnimator.ofFloat(0);

        this.setBitmap();
        this.setChecked(isChecked);
    }

    public RadioDrawable(Context context) {
        this(context, false);
    }

    private void setChecked(boolean checked) {
        mChecked = checked;

        mCurrentRadius = checked ? 0 : mRadius;
    }

    private void setBitmap() {
        mNormalBitmap = CheckedDrawable.drawable2Bitmap(ContextCompat.getDrawable(mContext, R.drawable.radio_btn_unchecked));
        //mCheckedBitmap = RadioDrawable.drawable2Bitmap(ContextCompat.getDrawable(mContext, R.drawable.radio_btn_checked));
        int width = mNormalBitmap.getWidth();
        int height = mNormalBitmap.getHeight();
        float cx = width / 2f, cy = height / 2f;
        mRadius = Math.min(cx, cy);
        mCheckedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mCheckedBitmap);
        canvas.drawCircle(cx, cy, mRadius, mPaint);
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

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (mNormalBitmap == null || mCheckedBitmap == null) {
            return;
        }

        mPaint.setColor(mStrokeColor);
        canvas.drawBitmap(mNormalBitmap.extractAlpha(), 0, 0, mPaint);

        if (!mPathCheckedOuter.isEmpty()) {
            int cc = canvas.saveLayer(0, 0, mNormalBitmap.getWidth(), mNormalBitmap.getHeight(), mPaint, Canvas.ALL_SAVE_FLAG);
            canvas.clipPath(mPathCheckedOuter, Region.Op.DIFFERENCE);
            canvas.drawBitmap(mCheckedBitmap.extractAlpha(), 0, 0, mPaint);
            canvas.restoreToCount(cc);
        }

        mPaint.setColor(Color.WHITE);
        int cc = canvas.saveLayer(0, 0, mNormalBitmap.getWidth(), mNormalBitmap.getHeight(), mPaint, Canvas.ALL_SAVE_FLAG);
        canvas.clipPath(mPathCheckedInner);
        canvas.drawBitmap(mCheckedBitmap.extractAlpha(), 0, 0, mPaint);
        canvas.restoreToCount(cc);
    }

    public void setAnimParams() {
        final Rect bounds = getBounds();
        final int exactCenterX = bounds.centerX();
        final int exactCenterY = bounds.centerY();
        /*Log.v(AnimCheckBox.TAG, "startAnim, centerX: " + exactCenterX + ", centerY: " + exactCenterY
                + ", bounds: " + bounds.toShortString() + ", mChecked: " + mChecked);*/

        if (mChecked) {
        } else {
        }

        mAnimatorStrokeColor.setDuration(1000);
        mAnimatorStrokeColor.setIntValues(0xFF000000, 0xFFFF0000);
        final ValueAnimator.AnimatorUpdateListener animatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mStrokeColor = (int) animation.getAnimatedValue();
                invalidateSelf();
            }
        };
        mAnimatorStrokeColor.addUpdateListener(animatorUpdateListener);
        mAnimatorStrokeColor.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mAnimatorStrokeColor.removeUpdateListener(animatorUpdateListener);
                mAnimatorStrokeColor.removeListener(this);
            }
        });

        mAnimatorCheckedOuter.setFloatValues(mRadius, 0);
        mAnimatorCheckedOuter.setDuration(1000);
        ValueAnimator.AnimatorUpdateListener animatorUpdateListener1 = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float radius = (float) animation.getAnimatedValue();
                mPathCheckedOuter.reset();
                mPathCheckedOuter.addCircle(exactCenterX, exactCenterY, radius, Path.Direction.CCW);
                invalidateSelf();
            }
        };
        mAnimatorCheckedOuter.addUpdateListener(animatorUpdateListener1);
        mAnimatorCheckedOuter.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mAnimatorCheckedOuter.removeUpdateListener(animatorUpdateListener1);
                mAnimatorCheckedOuter.removeListener(this);
                mPathCheckedOuter.addCircle(exactCenterX, exactCenterY, .1f, Path.Direction.CCW);
            }
        });

        float v = mRadius / 2;
        mAnimatorCheckedInner.setFloatValues(0, v * 1.4f, v * 0.9f);
        mAnimatorCheckedInner.setDuration(1300);
        mAnimatorCheckedInner.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mPathCheckedInner.reset();
                float radius = (float) animation.getAnimatedValue();
                mPathCheckedInner.addCircle(exactCenterX, exactCenterY, radius, Path.Direction.CCW);
                invalidateSelf();
            }
        });

        mAnimatorSet.playSequentially(mAnimatorStrokeColor, mAnimatorCheckedOuter, mAnimatorCheckedInner);
        mAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mPathCheckedOuter.reset();
                mPathCheckedInner.reset();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mAnimatorSet.removeListener(this);
            }
        });
        mAnimatorSet.start();
    }

    public void startAnim() {
        mAnimatorStrokeColor.start();
    }

    public void cancelAnim() {
        if (this.isAnimRunning()) {
            mAnimatorStrokeColor.cancel();
        }
    }

    public boolean isAnimRunning() {
        return mAnimatorStrokeColor.isRunning();
    }

    public float getCurrentRadius() {
        return mCurrentRadius;
    }
}
