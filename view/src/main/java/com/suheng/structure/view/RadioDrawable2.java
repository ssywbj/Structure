package com.suheng.structure.view;

import android.animation.Keyframe;
import android.animation.PropertyValuesHolder;
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

public class RadioDrawable2 extends Drawable {
    private static final float OUTER_MIN_RADIUS = .1f;
    private final Paint mPaint;
    //private final AnimatorSet mAnimatorSet;
    private final Context mContext;

    private Bitmap mNormalBitmap, mCheckedBitmap;
    private final Path mPathCheckedOuter, mPathCheckedInner;
    private boolean mChecked;
    private int mStrokeColor, mStartColor = 0xFFA1A1A1, mEndColor = 0xFF2278FF;
    private float mOuterRadius, mOuterStartRadius;
    private float mInnerRadius, mInnerEndRadius;

    private final ValueAnimator mValueAnimator;

    public RadioDrawable2(Context context, boolean isChecked) {
        mContext = context;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

        mPathCheckedOuter = new Path();
        mPathCheckedInner = new Path();

        //mAnimatorSet = new AnimatorSet();
        mValueAnimator = ValueAnimator.ofFloat();

        this.setBitmap();
        this.setChecked(isChecked);
    }

    public RadioDrawable2(Context context) {
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

    public void setAnimParams(int strokeColor, float outerRadius, float innerRadius) {
        final Rect bounds = getBounds();
        final int centerX = bounds.centerX();
        final int centerY = bounds.centerY();
        Log.v(AnimRadioButton.TAG, "startAnim, centerX: " + centerX + ", centerY: " + centerY
                + ", bounds: " + bounds.toShortString() + ", mChecked: " + mChecked + ", this:" + this);
        Log.v(AnimRadioButton.TAG, "startAnim, strokeColor-mStrokeColor: (" + strokeColor + ")-(" + mStrokeColor
                + "), outerRadius-mOuterRadius: " + outerRadius + "-" + mOuterRadius
                + ", innerRadius-mInnerRadius: " + innerRadius + "-" + mInnerRadius);

        /*int startColor = 0, endColor = 99;
        PropertyValuesHolder pvhStrokeColor = PropertyValuesHolder.ofKeyframe("phv_stroke_color", Keyframe.ofInt(0, startColor)
                , Keyframe.ofInt(0.2f, endColor), Keyframe.ofInt(1, endColor));
        float startOuterRadius = 21, endOuterRadius = 0;
        PropertyValuesHolder pvhOuterRadius = PropertyValuesHolder.ofKeyframe("phv_outer_radius", Keyframe.ofFloat(0, startOuterRadius)
                , Keyframe.ofFloat(0.2f, startOuterRadius), Keyframe.ofFloat(0.6f, endOuterRadius), Keyframe.ofFloat(1, endOuterRadius));
        float startInnerRadius = 0, middleInnerRadius = 18.5f, endInnerRadius = 13.4f;
        PropertyValuesHolder pvhInnerRadius = PropertyValuesHolder.ofKeyframe("phv_inner_radius"
                , Keyframe.ofFloat(0, startInnerRadius), Keyframe.ofFloat(0.6f, startInnerRadius), Keyframe.ofFloat(0.9f, middleInnerRadius), Keyframe.ofFloat(1f, endInnerRadius));
        mValueAnimator.setValues(pvhStrokeColor, pvhOuterRadius, pvhInnerRadius);
        mValueAnimator.setDuration(2000);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                long currentPlayTime = animation.getCurrentPlayTime();
                float fraction = animation.getAnimatedFraction();
                Object objSColor = animation.getAnimatedValue("phv_stroke_color");
                Object objORadius = animation.getAnimatedValue("phv_outer_radius");
                Object objIRadius = animation.getAnimatedValue("phv_inner_radius");
                Log.v(AnimRadioButton.TAG, "keyframe, objSColor: " + objSColor + ", objORadius: " + objORadius + ", objIRadius: " + objIRadius + ", currentPlayTime: " + currentPlayTime + ", fraction: " + fraction);

                invalidateSelf();
            }
        });
        mValueAnimator.start();*/

        ValueAnimator animatorStrokeColor, animatorCheckedOuter, animatorCheckedInner;
        if (mChecked) {
            /*animatorStrokeColor = ValueAnimator.ofArgb(strokeColor, mEndColor);
            animatorStrokeColor.setDuration(1000);

            mPathCheckedOuter.reset();
            mPathCheckedOuter.addCircle(centerX, centerY, mOuterStartRadius, Path.Direction.CCW);
            animatorCheckedOuter = ValueAnimator.ofFloat(outerRadius, 0);
            animatorCheckedOuter.setDuration(1000);
            animatorCheckedOuter.setStartDelay(animatorStrokeColor.getDuration());

            mPathCheckedInner.reset();
            animatorCheckedInner = ValueAnimator.ofFloat(innerRadius, mOuterStartRadius / 3 * 2, mOuterStartRadius / 2);
            animatorCheckedInner.setDuration(1000);
            animatorCheckedInner.setStartDelay(animatorStrokeColor.getDuration() + animatorCheckedOuter.getDuration() / 2);*/

            PropertyValuesHolder pvhStrokeColor = PropertyValuesHolder.ofKeyframe("phv_stroke_color", Keyframe.ofInt(0, strokeColor)
                    , Keyframe.ofInt(0.2f, mEndColor), Keyframe.ofInt(1, mEndColor));

            mPathCheckedOuter.reset();
            mPathCheckedOuter.addCircle(centerX, centerY, mOuterStartRadius, Path.Direction.CCW);
            PropertyValuesHolder pvhOuterRadius = PropertyValuesHolder.ofKeyframe("phv_outer_radius", Keyframe.ofFloat(0, outerRadius)
                    , Keyframe.ofFloat(0.2f, outerRadius), Keyframe.ofFloat(0.6f, 0), Keyframe.ofFloat(1, 0));

            mPathCheckedInner.reset();
            PropertyValuesHolder pvhInnerRadius = PropertyValuesHolder.ofKeyframe("phv_inner_radius"
                    , Keyframe.ofFloat(0, innerRadius), Keyframe.ofFloat(0.6f, innerRadius), Keyframe.ofFloat(0.9f, mOuterStartRadius / 3 * 2)
                    , Keyframe.ofFloat(1f, mOuterStartRadius / 2));

            mValueAnimator.setValues(pvhStrokeColor, pvhOuterRadius, pvhInnerRadius);
        } else {
            /*animatorCheckedInner = ValueAnimator.ofFloat(innerRadius, 0);
            animatorCheckedInner.setDuration(1000);

            mPathCheckedOuter.reset();
            mPathCheckedOuter.addCircle(centerX, centerY, OUTER_MIN_RADIUS, Path.Direction.CCW);
            animatorCheckedOuter = ValueAnimator.ofFloat(outerRadius, mOuterStartRadius);
            animatorCheckedOuter.setDuration(1000);
            animatorCheckedOuter.setStartDelay(animatorCheckedInner.getDuration() / 2);

            mStrokeColor = strokeColor;
            animatorStrokeColor = ValueAnimator.ofArgb(strokeColor, mStartColor);
            animatorStrokeColor.setDuration(1000);
            animatorStrokeColor.setStartDelay(animatorCheckedInner.getDuration() + animatorCheckedOuter.getDuration());*/

            PropertyValuesHolder pvhInnerRadius = PropertyValuesHolder.ofKeyframe("phv_inner_radius"
                    , Keyframe.ofFloat(0, innerRadius), Keyframe.ofFloat(0.4f, 0), Keyframe.ofFloat(1f, 0));

            mPathCheckedOuter.reset();
            mPathCheckedOuter.addCircle(centerX, centerY, OUTER_MIN_RADIUS, Path.Direction.CCW);
            PropertyValuesHolder pvhOuterRadius = PropertyValuesHolder.ofKeyframe("phv_outer_radius", Keyframe.ofFloat(0, outerRadius)
                    , Keyframe.ofFloat(0, outerRadius), Keyframe.ofFloat(0.4f, outerRadius), Keyframe.ofFloat(0.8f, mOuterStartRadius), Keyframe.ofFloat(1, mOuterStartRadius));

            mStrokeColor = strokeColor;
            PropertyValuesHolder pvhStrokeColor = PropertyValuesHolder.ofKeyframe("phv_stroke_color", Keyframe.ofInt(0, strokeColor)
                    , Keyframe.ofInt(0.8f, strokeColor), Keyframe.ofInt(1, mStartColor));

            mValueAnimator.setValues(pvhStrokeColor, pvhOuterRadius, pvhInnerRadius);
        }

        /*animatorStrokeColor.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mStrokeColor = (int) animation.getAnimatedValue();

                invalidateSelf();
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
        animatorCheckedInner.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mInnerRadius = (float) animation.getAnimatedValue();
                mPathCheckedInner.reset();
                mPathCheckedInner.addCircle(centerX, centerY, mInnerRadius, Path.Direction.CCW);

                invalidateSelf();
            }
        });*/

        /*mAnimatorSet.playTogether(animatorCheckedInner, animatorCheckedOuter, animatorStrokeColor);

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
        });*/

        mValueAnimator.setDuration(2000);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                long currentPlayTime = animation.getCurrentPlayTime();
                float fraction = animation.getAnimatedFraction();
                mStrokeColor = (int) animation.getAnimatedValue("phv_stroke_color");
                mOuterRadius = (float) animation.getAnimatedValue("phv_outer_radius");
                mInnerRadius = (float) animation.getAnimatedValue("phv_inner_radius");
                Log.v(AnimRadioButton.TAG, "keyframe, mStrokeColor: " + mStrokeColor + ", mOuterRadius: " + mOuterRadius + ", mInnerRadius: "
                        + mInnerRadius + ", currentPlayTime: " + currentPlayTime + ", fraction: " + fraction);

                mPathCheckedOuter.reset();
                mPathCheckedOuter.addCircle(centerX, centerY, mOuterRadius, Path.Direction.CCW);

                mPathCheckedInner.reset();
                mPathCheckedInner.addCircle(centerX, centerY, mInnerRadius, Path.Direction.CCW);
                invalidateSelf();
            }
        });
        mValueAnimator.start();
    }

    public void startAnim() {
        //mAnimatorSet.start();
        mValueAnimator.start();
    }

    public void cancelAnim() {
        if (this.isAnimRunning()) {
            //mAnimatorSet.cancel();
            mValueAnimator.cancel();
        }
    }

    public boolean isAnimRunning() {
        //return mAnimatorSet.isRunning();
        return mValueAnimator.isRunning();
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
