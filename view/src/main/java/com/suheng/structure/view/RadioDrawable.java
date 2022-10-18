package com.suheng.structure.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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

import java.util.ArrayList;
import java.util.List;

public class RadioDrawable extends Drawable {
    private static final String PVH_INNER_RADIUS = "pvh_inner_radius";
    private static final String PVH_OUTER_RADIUS = "pvh_outer_radius";
    private static final String PVH_STROKE_COLOR = "pvh_stroke_color";

    private static final int FRAMES_TOTAL = 30;
    private static final float FRAMES_STROKE_COLOR_RATIO = 5f / FRAMES_TOTAL;
    private static final float FRAMES_OUTER_RADIUS_RATIO = 6f / FRAMES_TOTAL;
    private static final float FRAMES_INNER_MAX_RADIUS_RATIO = 9f / FRAMES_TOTAL;
    private static final float FRAMES_INNER_MIN_RADIUS_RATIO = 5f / FRAMES_TOTAL;
    private static final float FRAMES_INNER_RADIUS_RATIO = 5f / FRAMES_TOTAL;

    //private static final int ANIM_DURATION = 500;
    private static final int ANIM_DURATION = 3000;
    private static final float OUTER_MIN_RADIUS = .1f;

    private final Paint mPaint;
    private final Context mContext;

    private Bitmap mNormalBitmap, mCheckedBitmap;
    private final Path mPathCheckedOuter, mPathCheckedInner;
    private boolean mChecked;
    private int mStrokeColor, mStartColor = 0xFFA1A1A1, mEndColor = 0xFF2278FF;
    private float mOuterRadius, mOuterStartRadius;
    private float mInnerRadius, mInnerEndRadius, mInnerMaxRadius, mInnerMinRadius;
    private float mExecFraction = 1;

    private final ValueAnimator mValueAnimator;

    public RadioDrawable(Context context, boolean isChecked) {
        mContext = context;
        mPaint = new Paint();

        mPathCheckedOuter = new Path();
        mPathCheckedInner = new Path();

        mValueAnimator = ValueAnimator.ofFloat();

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
        mInnerRadius = checked ? mInnerEndRadius : 0;
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
        float fullOuterRadius = Math.min(cx, cy);
        mOuterStartRadius = fullOuterRadius - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, mContext.getResources().getDisplayMetrics());
        mCheckedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mCheckedBitmap);
        Paint paint = new Paint(mPaint);
        paint.setColor(mEndColor);
        canvas.drawCircle(cx, cy, mOuterStartRadius, paint);

        mInnerMaxRadius = fullOuterRadius * 0.7f;
        mInnerMinRadius = fullOuterRadius * 0.45f;
        mInnerEndRadius = fullOuterRadius * 0.55f;
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
        return mNormalBitmap == null ? mContext.getResources().getDimensionPixelSize(R.dimen.radio_btn_wh) : mNormalBitmap.getHeight();
    }

    @Override
    public int getIntrinsicWidth() {
        return mNormalBitmap == null ? mContext.getResources().getDimensionPixelSize(R.dimen.radio_btn_wh) : mNormalBitmap.getWidth();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (mNormalBitmap == null || mCheckedBitmap == null) {
            return;
        }

        mPaint.setColor(mStrokeColor);
        canvas.drawBitmap(mNormalBitmap.extractAlpha(), 0, 0, mPaint);

        /*int cc = canvas.saveLayer(0, 0, getIntrinsicWidth(), getIntrinsicHeight(), mPaint, Canvas.ALL_SAVE_FLAG);
        canvas.clipPath(mPathCheckedOuter, Region.Op.DIFFERENCE);
        canvas.drawBitmap(mCheckedBitmap.extractAlpha(), 0, 0, mPaint);
        canvas.restoreToCount(cc);

        mPaint.setColor(Color.WHITE);
        int cc2 = canvas.saveLayer(0, 0, getIntrinsicWidth(), getIntrinsicHeight(), mPaint, Canvas.ALL_SAVE_FLAG);
        canvas.clipPath(mPathCheckedInner);
        canvas.drawBitmap(mCheckedBitmap.extractAlpha(), 0, 0, mPaint);
        canvas.restoreToCount(cc2);*/

        int cc = canvas.saveLayer(0, 0, getIntrinsicWidth(), getIntrinsicHeight(), null, Canvas.ALL_SAVE_FLAG);
        canvas.clipPath(mPathCheckedOuter, Region.Op.DIFFERENCE);
        canvas.drawBitmap(mCheckedBitmap, 0, 0, null);

        canvas.clipPath(mPathCheckedInner);
        canvas.drawColor(Color.WHITE);

        canvas.restoreToCount(cc);
    }

    private void setAnimParams(RadioDrawable reverse) {
        this.setAnimParams(reverse.mStrokeColor, reverse.mOuterRadius, reverse.mInnerRadius, reverse.mExecFraction);
    }

    private void setAnimParams(int strokeColor, float outerRadius, float innerRadius, float execFraction) {
        final Rect bounds = getBounds();
        final int centerX = bounds.centerX();
        final int centerY = bounds.centerY();
        Log.v(AnimRadioButton.TAG, "startAnim, centerX: " + centerX + ", centerY: " + centerY
                + ", bounds: " + bounds.toShortString() + ", mChecked: " + mChecked + ", execFraction: " + execFraction + ", this:" + this);

        final int scRedStart = Color.red(strokeColor), scGreenStart = Color.green(strokeColor), scBlueStart = Color.blue(strokeColor);
        int scRedDelta, scGreenDelta, scBlueDelta;
        List<PropertyValuesHolder> pValuesHolderList = new ArrayList<>();
        PropertyValuesHolder pvhStrokeColor, pvhOuterRadius, pvhInnerRadius;
        Keyframe kfScStart = Keyframe.ofFloat(0, 0), kfScEnd = Keyframe.ofFloat(1, 1);
        int keyframeCount = 1;
        if (mChecked) {
            scRedDelta = Color.red(mEndColor) - scRedStart;
            scGreenDelta = Color.green(mEndColor) - scGreenStart;
            scBlueDelta = Color.blue(mEndColor) - scBlueStart;

            if (execFraction > 0.4f) {
                keyframeCount++;
            }
            if (execFraction > 0.8f) {
                keyframeCount++;
            }

            final float fOuterRadius = FRAMES_STROKE_COLOR_RATIO + FRAMES_OUTER_RADIUS_RATIO;
            final float fInnerMaxRadius = FRAMES_INNER_MAX_RADIUS_RATIO + fOuterRadius;
            final float fInnerMinRadius = FRAMES_INNER_MIN_RADIUS_RATIO + fInnerMaxRadius;

            mPathCheckedOuter.reset();
            mPathCheckedOuter.addCircle(centerX, centerY, mOuterStartRadius, Path.Direction.CCW);
            mPathCheckedInner.reset();
            if (keyframeCount == 1) {
                pvhInnerRadius = PropertyValuesHolder.ofKeyframe(PVH_INNER_RADIUS
                        , Keyframe.ofFloat(0, innerRadius)
                        , Keyframe.ofFloat(0.9f, mInnerMaxRadius)
                        , Keyframe.ofFloat(1f, mInnerEndRadius));
            } else if (keyframeCount == 2) {
                pvhOuterRadius = PropertyValuesHolder.ofKeyframe(PVH_OUTER_RADIUS
                        , Keyframe.ofFloat(0, outerRadius)
                        , Keyframe.ofFloat(0.6f, 0)
                        , Keyframe.ofFloat(1, 0));
                pValuesHolderList.add(pvhOuterRadius);

                pvhInnerRadius = PropertyValuesHolder.ofKeyframe(PVH_INNER_RADIUS
                        , Keyframe.ofFloat(0, innerRadius)
                        , Keyframe.ofFloat(0.6f, innerRadius)
                        , Keyframe.ofFloat(0.9f, mInnerMaxRadius)
                        , Keyframe.ofFloat(1f, mInnerEndRadius));
            } else {
                pvhStrokeColor = PropertyValuesHolder.ofKeyframe(PVH_STROKE_COLOR
                        , Keyframe.ofFloat(0, 0)
                        , Keyframe.ofFloat(FRAMES_STROKE_COLOR_RATIO, 1)
                        , Keyframe.ofFloat(1, 1));
                pValuesHolderList.add(pvhStrokeColor);

                pvhOuterRadius = PropertyValuesHolder.ofKeyframe(PVH_OUTER_RADIUS
                        , Keyframe.ofFloat(0, outerRadius)
                        , Keyframe.ofFloat(FRAMES_STROKE_COLOR_RATIO, outerRadius)
                        , Keyframe.ofFloat(fOuterRadius, OUTER_MIN_RADIUS)
                        , Keyframe.ofFloat(1, OUTER_MIN_RADIUS));
                pValuesHolderList.add(pvhOuterRadius);

                pvhInnerRadius = PropertyValuesHolder.ofKeyframe(PVH_INNER_RADIUS
                        , Keyframe.ofFloat(0, innerRadius)
                        , Keyframe.ofFloat(fOuterRadius, innerRadius)
                        , Keyframe.ofFloat(fInnerMaxRadius, mInnerMaxRadius)
                        , Keyframe.ofFloat(fInnerMinRadius, mInnerMinRadius)
                        , Keyframe.ofFloat(1f, mInnerEndRadius));
            }
            pValuesHolderList.add(pvhInnerRadius);

            Log.d(AnimRadioButton.TAG, "checked anim, keyframeCount: " + keyframeCount + ", startStrokeColor-endStrokeColor: (" + strokeColor
                    + ")-(" + mEndColor + "), startOuterRadius-endOuterRadius: (" + outerRadius + "-" + OUTER_MIN_RADIUS
                    + "), startInnerRadius-endInnerRadius: (" + innerRadius + "-" + mInnerEndRadius + ")");
        } else {
            mStrokeColor = strokeColor;
            scRedDelta = Color.red(mStartColor) - scRedStart;
            scGreenDelta = Color.green(mStartColor) - scGreenStart;
            scBlueDelta = Color.blue(mStartColor) - scBlueStart;

            if (execFraction > FRAMES_STROKE_COLOR_RATIO) {
                keyframeCount++;
                mPathCheckedOuter.reset();
                mPathCheckedOuter.addCircle(centerX, centerY, OUTER_MIN_RADIUS, Path.Direction.CCW);
            }
            if (execFraction > FRAMES_STROKE_COLOR_RATIO + FRAMES_OUTER_RADIUS_RATIO) {
                keyframeCount++;
            }
            if (execFraction > FRAMES_STROKE_COLOR_RATIO + FRAMES_OUTER_RADIUS_RATIO + FRAMES_INNER_MAX_RADIUS_RATIO) {
                keyframeCount++;
            }
            if (execFraction > FRAMES_STROKE_COLOR_RATIO + FRAMES_OUTER_RADIUS_RATIO + FRAMES_INNER_MAX_RADIUS_RATIO + FRAMES_INNER_MIN_RADIUS_RATIO) {
                keyframeCount++;
            }

            if (keyframeCount == 5) {
                final float fInnerRadius = (execFraction - FRAMES_STROKE_COLOR_RATIO - FRAMES_OUTER_RADIUS_RATIO - FRAMES_INNER_MAX_RADIUS_RATIO - FRAMES_INNER_MIN_RADIUS_RATIO) / execFraction;
                final float fInnerMinRadius = FRAMES_INNER_MIN_RADIUS_RATIO / execFraction + fInnerRadius;
                final float fInnerMaxRadius = FRAMES_INNER_MAX_RADIUS_RATIO / execFraction + fInnerMinRadius;
                final float fOuterRadius = FRAMES_OUTER_RADIUS_RATIO / execFraction + fInnerMaxRadius;

                pvhInnerRadius = PropertyValuesHolder.ofKeyframe(PVH_INNER_RADIUS
                        , Keyframe.ofFloat(0, innerRadius)
                        , Keyframe.ofFloat(fInnerRadius, mInnerMinRadius)
                        , Keyframe.ofFloat(fInnerMinRadius, mInnerMaxRadius)
                        , Keyframe.ofFloat(fInnerMaxRadius, 0)
                        , Keyframe.ofFloat(1f, 0));
                pValuesHolderList.add(pvhInnerRadius);

                pvhOuterRadius = PropertyValuesHolder.ofKeyframe(PVH_OUTER_RADIUS
                        , Keyframe.ofFloat(0, outerRadius)
                        , Keyframe.ofFloat(fInnerMaxRadius, outerRadius)
                        , Keyframe.ofFloat(fOuterRadius, mOuterStartRadius)
                        , Keyframe.ofFloat(1, mOuterStartRadius));
                pValuesHolderList.add(pvhOuterRadius);

                pvhStrokeColor = PropertyValuesHolder.ofKeyframe(PVH_STROKE_COLOR
                        , kfScStart
                        , Keyframe.ofFloat(fOuterRadius, 0)
                        , kfScEnd);
            } else if (keyframeCount == 4) {
                final float fInnerMinRadius = (execFraction - FRAMES_STROKE_COLOR_RATIO - FRAMES_OUTER_RADIUS_RATIO - FRAMES_INNER_MAX_RADIUS_RATIO) / execFraction;
                final float fInnerMaxRadius = FRAMES_INNER_MAX_RADIUS_RATIO / execFraction + fInnerMinRadius;
                final float fOuterRadius = FRAMES_OUTER_RADIUS_RATIO / execFraction + fInnerMaxRadius;

                pvhInnerRadius = PropertyValuesHolder.ofKeyframe(PVH_INNER_RADIUS
                        , Keyframe.ofFloat(0, innerRadius)
                        , Keyframe.ofFloat(fInnerMinRadius, mInnerMaxRadius)
                        , Keyframe.ofFloat(fInnerMaxRadius, 0)
                        , Keyframe.ofFloat(1f, 0));
                pValuesHolderList.add(pvhInnerRadius);

                pvhOuterRadius = PropertyValuesHolder.ofKeyframe(PVH_OUTER_RADIUS
                        , Keyframe.ofFloat(0, outerRadius)
                        , Keyframe.ofFloat(fInnerMaxRadius, outerRadius)
                        , Keyframe.ofFloat(fOuterRadius, mOuterStartRadius)
                        , Keyframe.ofFloat(1, mOuterStartRadius));
                pValuesHolderList.add(pvhOuterRadius);

                pvhStrokeColor = PropertyValuesHolder.ofKeyframe(PVH_STROKE_COLOR
                        , kfScStart
                        , Keyframe.ofFloat(fOuterRadius, 0)
                        , kfScEnd);
            } else if (keyframeCount == 3) {
                final float fInnerMaxRadius = (execFraction - FRAMES_STROKE_COLOR_RATIO - FRAMES_OUTER_RADIUS_RATIO) / execFraction;
                final float fOuterRadius = FRAMES_OUTER_RADIUS_RATIO / execFraction + fInnerMaxRadius;

                pvhInnerRadius = PropertyValuesHolder.ofKeyframe(PVH_INNER_RADIUS
                        , Keyframe.ofFloat(0, innerRadius)
                        , Keyframe.ofFloat(fInnerMaxRadius, 0)
                        , Keyframe.ofFloat(1f, 0));
                pValuesHolderList.add(pvhInnerRadius);

                pvhOuterRadius = PropertyValuesHolder.ofKeyframe(PVH_OUTER_RADIUS
                        , Keyframe.ofFloat(0, outerRadius)
                        , Keyframe.ofFloat(fInnerMaxRadius, outerRadius)
                        , Keyframe.ofFloat(fOuterRadius, mOuterStartRadius)
                        , Keyframe.ofFloat(1, mOuterStartRadius));
                pValuesHolderList.add(pvhOuterRadius);

                pvhStrokeColor = PropertyValuesHolder.ofKeyframe(PVH_STROKE_COLOR
                        , kfScStart
                        , Keyframe.ofFloat(fOuterRadius, 0)
                        , kfScEnd);
            } else if (keyframeCount == 2) {
                final float fOuterRadius = (execFraction - FRAMES_STROKE_COLOR_RATIO) / execFraction;

                pvhOuterRadius = PropertyValuesHolder.ofKeyframe(PVH_OUTER_RADIUS
                        , Keyframe.ofFloat(0, outerRadius)
                        , Keyframe.ofFloat(fOuterRadius, mOuterStartRadius)
                        , Keyframe.ofFloat(1, mOuterStartRadius));
                pValuesHolderList.add(pvhOuterRadius);

                pvhStrokeColor = PropertyValuesHolder.ofKeyframe(PVH_STROKE_COLOR
                        , kfScStart
                        , Keyframe.ofFloat(fOuterRadius, 0)
                        , kfScEnd);
            } else {
                pvhStrokeColor = PropertyValuesHolder.ofKeyframe(PVH_STROKE_COLOR, kfScStart, kfScEnd);
            }
            pValuesHolderList.add(pvhStrokeColor);

            Log.d(AnimRadioButton.TAG, "unchecked anim, keyframeCount: " + keyframeCount + ", startStrokeColor-endStrokeColor: (" + strokeColor
                    + ")-(" + mStartColor + "), startOuterRadius-endOuterRadius: (" + outerRadius + "-" + mOuterStartRadius
                    + "), startInnerRadius-endInnerRadius: (" + innerRadius + "-" + 0 + ")");
        }

        mValueAnimator.setValues(pValuesHolderList.toArray(new PropertyValuesHolder[0]));
        //mValueAnimator.setDuration(ANIM_DURATION);
        mValueAnimator.setDuration((long) (ANIM_DURATION * execFraction));
        final ValueAnimator.AnimatorUpdateListener updateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mExecFraction = animation.getAnimatedFraction();

                Object objStrokeColor = animation.getAnimatedValue(PVH_STROKE_COLOR);
                if (objStrokeColor instanceof Float) {
                    float strokeColorRadio = (float) objStrokeColor;
                    int red = (int) (scRedStart + scRedDelta * strokeColorRadio);
                    int green = (int) (scGreenStart + scGreenDelta * strokeColorRadio);
                    int blue = (int) (scBlueStart + scBlueDelta * strokeColorRadio);
                    mStrokeColor = Color.argb(0xFF, red, green, blue);
                }

                Object objOuterRadius = animation.getAnimatedValue(PVH_OUTER_RADIUS);
                if (objOuterRadius instanceof Float) {
                    mOuterRadius = (float) objOuterRadius;
                }

                Object objInnerRadius = animation.getAnimatedValue(PVH_INNER_RADIUS);
                if (objInnerRadius instanceof Float) {
                    mInnerRadius = (float) objInnerRadius;
                }
                /*Log.v(AnimRadioButton.TAG, "keyframe, mStrokeColor: " + mStrokeColor + ", mOuterRadius: " + mOuterRadius
                        + ", mInnerRadius: " + mInnerRadius + ", mExecFraction: " + mExecFraction + ", object: " + this);*/

                mPathCheckedOuter.reset();
                mPathCheckedOuter.addCircle(centerX, centerY, mOuterRadius, Path.Direction.CCW);

                mPathCheckedInner.reset();
                mPathCheckedInner.addCircle(centerX, centerY, mInnerRadius, Path.Direction.CCW);

                invalidateSelf();
            }
        };
        mValueAnimator.addUpdateListener(updateListener);
        mValueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mValueAnimator.removeUpdateListener(updateListener);
                mValueAnimator.removeListener(this);
                Log.d(AnimRadioButton.TAG, "onAnimationEnd, mStrokeColor: " + mStrokeColor + ", mOuterRadius: " + mOuterRadius
                        + ", mInnerRadius: " + mInnerRadius + ", mExecFraction: " + mExecFraction + ", object: " + RadioDrawable.this);
            }
        });
    }

    public void startAnim(RadioDrawable reverse) {
        reverse.cancelAnim();
        this.setAnimParams(reverse);
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
}
