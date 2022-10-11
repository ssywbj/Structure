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
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class RadioDrawable extends Drawable {
    private final Paint mPaint;
    private final AnimatorSet mAnimatorSet;
    private final Context mContext;

    private Bitmap mNormalBitmap, mCheckedBitmap;
    private final Path mPathCheckedOuter, mPathCheckedInner;
    private boolean mChecked;
    private int mStrokeColor = 0xFF000000;
    private float mRadius;

    public RadioDrawable(Context context, boolean isChecked) {
        mContext = context;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

        mPathCheckedOuter = new Path();
        mPathCheckedInner = new Path();

        mAnimatorSet = new AnimatorSet();

        this.setBitmap();
        this.setChecked(isChecked);
    }

    public RadioDrawable(Context context) {
        this(context, false);
    }

    private void setChecked(boolean checked) {
        mChecked = checked;
    }

    private void setBitmap() {
        mNormalBitmap = CheckedDrawable.drawable2Bitmap(ContextCompat.getDrawable(mContext, R.drawable.radio_btn_unchecked));
        //mCheckedBitmap = RadioDrawable.drawable2Bitmap(ContextCompat.getDrawable(mContext, R.drawable.radio_btn_checked));
        int width = mNormalBitmap.getWidth();
        int height = mNormalBitmap.getHeight();
        float cx = width / 2f, cy = height / 2f;
        mRadius = Math.min(cx, cy) - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, mContext.getResources().getDisplayMetrics());
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

        mPaint.setColor(Color.BLUE);
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

        ValueAnimator animatorStrokeColor = ValueAnimator.ofArgb(0xFF000000, 0xFFFF0000);
        animatorStrokeColor.setDuration(1000);
        animatorStrokeColor.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mStrokeColor = (int) animation.getAnimatedValue();
                invalidateSelf();
            }
        });

        ValueAnimator animatorCheckedOuter = ValueAnimator.ofFloat(mRadius, 0);
        animatorCheckedOuter.setDuration(1000);
        animatorCheckedOuter.setStartDelay(animatorStrokeColor.getDuration());
        animatorCheckedOuter.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float radius = (float) animation.getAnimatedValue();
                mPathCheckedOuter.reset();
                mPathCheckedOuter.addCircle(exactCenterX, exactCenterY, radius, Path.Direction.CCW);
                invalidateSelf();
            }
        });
        animatorCheckedOuter.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mPathCheckedOuter.addCircle(exactCenterX, exactCenterY, .1f, Path.Direction.CCW);
            }
        });

        ValueAnimator animatorCheckedInner = ValueAnimator.ofFloat(0, mRadius / 3 * 2, mRadius / 2);
        animatorCheckedInner.setDuration(1300);
        animatorCheckedInner.setStartDelay(animatorStrokeColor.getDuration() + animatorCheckedOuter.getDuration() / 2);
        animatorCheckedInner.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float radius = (float) animation.getAnimatedValue();
                mPathCheckedInner.reset();
                mPathCheckedInner.addCircle(exactCenterX, exactCenterY, radius, Path.Direction.CCW);

                if (!animatorCheckedOuter.isRunning()) {
                    invalidateSelf();
                }
            }
        });

        mAnimatorSet.playTogether(animatorStrokeColor, animatorCheckedOuter, animatorCheckedInner);
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
                ArrayList<Animator> animations = mAnimatorSet.getChildAnimations();
                for (Animator animator : animations) {
                    animator.removeAllListeners();
                    ((ValueAnimator) animator).removeAllUpdateListeners();
                }
                mAnimatorSet.removeListener(this);
            }
        });
    }

    public void startAnim() {
        mAnimatorSet.start();
    }

    public void cancelAnim() {
        if (this.isAnimRunning()) {
            mAnimatorSet.cancel();
        }
    }

    public boolean isAnimRunning() {
        return mAnimatorSet.isRunning();
    }

}
