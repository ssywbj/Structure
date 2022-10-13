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
import android.util.Log;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class RadioDrawable extends Drawable {
    private static final float OUTER_MIN_RADIUS = .1f;
    private final Paint mPaint;
    private final AnimatorSet mAnimatorSet;
    private final Context mContext;

    private Bitmap mNormalBitmap, mCheckedBitmap;
    private final Path mPathCheckedOuter, mPathCheckedInner;
    private boolean mChecked;
    private int mStrokeColor, mStartColor = 0xFFA1A1A1, mEndColor = 0xFF2278FF;
    private float mOuterRadius, mOuterStartRadius;
    private float mInnerRadius, mInnerEndRadius;

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

        mStrokeColor = checked ? mEndColor : mStartColor;
        mOuterRadius = checked ? OUTER_MIN_RADIUS : mOuterStartRadius;
        mInnerRadius = checked ? mOuterStartRadius / 2 : 0;
        Log.v(AnimRadioButton.TAG, "setChecked, mStrokeColor-mStartColor-mEndColor: (" + mStrokeColor + ")-(" + mStartColor + ")-(" + mEndColor + ")" + ", checked: " + checked + ", this: " + this);
        Log.v(AnimRadioButton.TAG, "setChecked, mOuterRadius-mStartRadius-mEndRadius: (" + mOuterRadius + ")-(" + OUTER_MIN_RADIUS + ")-(" + mOuterStartRadius + ")");
        Log.v(AnimRadioButton.TAG, "setChecked, mInnerRadius-mStartRadius-mEndRadius: (" + mInnerRadius + ")-(" + (mOuterStartRadius / 2) + ")-(" + 0 + ")");

        float cx = getIntrinsicWidth() / 2f, cy = getIntrinsicHeight() / 2f;
        mPathCheckedOuter.reset();
        mPathCheckedOuter.addCircle(cx, cy, mOuterRadius, Path.Direction.CCW);
        mPathCheckedInner.reset();
        mPathCheckedInner.addCircle(cx, cy, mInnerRadius, Path.Direction.CCW);
    }

    private void setBitmap() {
        mNormalBitmap = CheckedDrawable.drawable2Bitmap(ContextCompat.getDrawable(mContext, R.drawable.radio_btn_unchecked));
        //mCheckedBitmap = RadioDrawable.drawable2Bitmap(ContextCompat.getDrawable(mContext, R.drawable.radio_btn_checked));
        int width = mNormalBitmap.getWidth();
        int height = mNormalBitmap.getHeight();
        float cx = width / 2f, cy = height / 2f;
        mOuterStartRadius = Math.min(cx, cy) - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, mContext.getResources().getDisplayMetrics());
        mCheckedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mCheckedBitmap);
        canvas.drawCircle(cx, cy, mOuterStartRadius, mPaint);
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

        int cc = canvas.saveLayer(0, 0, getIntrinsicWidth(), getIntrinsicHeight(), mPaint, Canvas.ALL_SAVE_FLAG);
        canvas.clipPath(mPathCheckedOuter, Region.Op.DIFFERENCE);
        canvas.drawBitmap(mCheckedBitmap.extractAlpha(), 0, 0, mPaint);
        canvas.restoreToCount(cc);

        mPaint.setColor(Color.WHITE);
        int cc2 = canvas.saveLayer(0, 0, getIntrinsicWidth(), getIntrinsicHeight(), mPaint, Canvas.ALL_SAVE_FLAG);
        canvas.clipPath(mPathCheckedInner);
        canvas.drawBitmap(mCheckedBitmap.extractAlpha(), 0, 0, mPaint);
        canvas.restoreToCount(cc2);
    }

    private final List<Animator> mAnimators = new ArrayList<>();

    public void setAnimParams(int strokeColor, float outerRadius, float innerRadius) {
        final Rect bounds = getBounds();
        final int centerX = bounds.centerX();
        final int centerY = bounds.centerY();
        Log.v(AnimRadioButton.TAG, "startAnim, centerX: " + centerX + ", centerY: " + centerY
                + ", bounds: " + bounds.toShortString() + ", mChecked: " + mChecked + ", this:" + this);
        Log.v(AnimRadioButton.TAG, "startAnim, strokeColor-mStrokeColor: (" + strokeColor + ")-(" + mStrokeColor
                + "), outerRadius-mOuterRadius: " + outerRadius + "-" + mOuterRadius
                + ", innerRadius-mInnerRadius: " + innerRadius + "-" + mInnerRadius);

        ValueAnimator animatorStrokeColor, animatorCheckedOuter, animatorCheckedInner;
        if (mChecked) {
            animatorStrokeColor = ValueAnimator.ofArgb(strokeColor, mEndColor);
            animatorStrokeColor.setDuration(1000);

            mPathCheckedOuter.reset();
            mPathCheckedOuter.addCircle(centerX, centerY, mOuterStartRadius, Path.Direction.CCW);
            animatorCheckedOuter = ValueAnimator.ofFloat(outerRadius, 0);
            animatorCheckedOuter.setDuration(1000);
            animatorCheckedOuter.setStartDelay(animatorStrokeColor.getDuration());

            mPathCheckedInner.reset();
            animatorCheckedInner = ValueAnimator.ofFloat(innerRadius, mOuterStartRadius / 3 * 2, mOuterStartRadius / 2);
            animatorCheckedInner.setDuration(1000);
            animatorCheckedInner.setStartDelay(animatorStrokeColor.getDuration() + animatorCheckedOuter.getDuration() / 2);
        } else {
            animatorCheckedInner = ValueAnimator.ofFloat(innerRadius, 0);
            animatorCheckedInner.setDuration(1000);

            mPathCheckedOuter.reset();
            mPathCheckedOuter.addCircle(centerX, centerY, OUTER_MIN_RADIUS, Path.Direction.CCW);
            animatorCheckedOuter = ValueAnimator.ofFloat(outerRadius, mOuterStartRadius);
            animatorCheckedOuter.setDuration(1000);
            animatorCheckedOuter.setStartDelay(animatorCheckedInner.getDuration() / 2);

            mStrokeColor = strokeColor;
            animatorStrokeColor = ValueAnimator.ofArgb(strokeColor, mStartColor);
            animatorStrokeColor.setDuration(1000);
            animatorStrokeColor.setStartDelay(animatorCheckedInner.getDuration() + animatorCheckedOuter.getDuration());
        }

        animatorStrokeColor.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mStrokeColor = (int) animation.getAnimatedValue();

                invalidateSelf();
            }
        });
        animatorStrokeColor.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                Log.i(AnimRadioButton.TAG, "onAnimationStart, animatorStrokeColor: " + RadioDrawable.this);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                Log.i(AnimRadioButton.TAG, "onAnimationEnd, animatorStrokeColor: " + RadioDrawable.this);
            }
        });

        animatorCheckedOuter.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mOuterRadius = Math.max((float) animation.getAnimatedValue(), OUTER_MIN_RADIUS);
                mPathCheckedOuter.reset();
                mPathCheckedOuter.addCircle(centerX, centerY, mOuterRadius, Path.Direction.CCW);

                invalidateSelf();
            }
        });
        animatorCheckedOuter.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                Log.d(AnimRadioButton.TAG, "onAnimationStart, animatorCheckedOuter: " + RadioDrawable.this);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                Log.d(AnimRadioButton.TAG, "onAnimationEnd, animatorCheckedOuter: " + RadioDrawable.this);
            }
        });

        animatorCheckedInner.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mInnerRadius = (float) animation.getAnimatedValue();
                mPathCheckedInner.reset();
                mPathCheckedInner.addCircle(centerX, centerY, mInnerRadius, Path.Direction.CCW);

                invalidateSelf();
            }
        });
        animatorCheckedInner.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                Log.v(AnimRadioButton.TAG, "onAnimationStart, animatorCheckedInner: " + RadioDrawable.this);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                Log.v(AnimRadioButton.TAG, "onAnimationEnd, animatorCheckedInner: " + RadioDrawable.this);
            }
        });

        mAnimatorSet.playTogether(animatorCheckedInner, animatorCheckedOuter, animatorStrokeColor);

        mAnimatorSet.addListener(new AnimatorListenerAdapter() {
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

    public int getStrokeColor() {
        return mStrokeColor;
    }

    public float getOuterRadius() {
        return mOuterRadius;
    }

    public float getInnerRadius() {
        return mInnerRadius;
    }
}
