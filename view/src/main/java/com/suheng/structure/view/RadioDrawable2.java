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

public class RadioDrawable2 extends Drawable {
    private static final String PVH_INNER_RADIUS = "pvh_inner_radius";
    private static final String PVH_OUTER_RADIUS = "pvh_outer_radius";
    private static final String PVH_STROKE_COLOR = "pvh_stroke_color";

    private static final float OUTER_MIN_RADIUS = .1f;
    private final Paint mPaint;
    private final Context mContext;

    private Bitmap mNormalBitmap, mCheckedBitmap;
    private final Path mPathCheckedOuter, mPathCheckedInner;
    private boolean mChecked;
    private int mStrokeColor, mStartColor = 0xFFA1A1A1, mEndColor = 0xFF2278FF;
    private float mOuterRadius, mOuterStartRadius;
    private float mInnerRadius, mInnerEndRadius;
    float mExecFraction;

    private final ValueAnimator mValueAnimator;

    public RadioDrawable2(Context context, boolean isChecked) {
        mContext = context;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

        mPathCheckedOuter = new Path();
        mPathCheckedInner = new Path();

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

    public void setAnimParams(int strokeColor, float outerRadius, float innerRadius, float execFraction) {
        final Rect bounds = getBounds();
        final int centerX = bounds.centerX();
        final int centerY = bounds.centerY();
        Log.v(AnimRadioButton.TAG, "startAnim, centerX: " + centerX + ", centerY: " + centerY
                + ", bounds: " + bounds.toShortString() + ", mChecked: " + mChecked + ", execFraction: " + execFraction + ", this:" + this);
        Log.v(AnimRadioButton.TAG, "startAnim, strokeColor-mStrokeColor: (" + strokeColor + ")-(" + mStrokeColor
                + "), outerRadius-mOuterRadius: " + outerRadius + "-" + mOuterRadius
                + ", innerRadius-mInnerRadius: " + innerRadius + "-" + mInnerRadius);

        final int scRedStart = Color.red(strokeColor), scGreenStart = Color.green(strokeColor), scBlueStart = Color.blue(strokeColor);
        int scRedDelta, scGreenDelta, scBlueDelta;
        List<PropertyValuesHolder> pValuesHolderList = new ArrayList<>();
        PropertyValuesHolder pvhStrokeColor, pvhOuterRadius, pvhInnerRadius;
        if (mChecked) {
            scRedDelta = Color.red(mEndColor) - scRedStart;
            scGreenDelta = Color.green(mEndColor) - scGreenStart;
            scBlueDelta = Color.blue(mEndColor) - scBlueStart;
            pvhStrokeColor = PropertyValuesHolder.ofKeyframe(PVH_STROKE_COLOR, Keyframe.ofFloat(0, 0)
                    , Keyframe.ofFloat(0.2f, 1), Keyframe.ofFloat(1, 1));
            pValuesHolderList.add(pvhStrokeColor);

            mPathCheckedOuter.reset();
            mPathCheckedOuter.addCircle(centerX, centerY, mOuterStartRadius, Path.Direction.CCW);
            pvhOuterRadius = PropertyValuesHolder.ofKeyframe(PVH_OUTER_RADIUS, Keyframe.ofFloat(0, outerRadius)
                    , Keyframe.ofFloat(0.2f, outerRadius), Keyframe.ofFloat(0.6f, 0), Keyframe.ofFloat(1, 0));
            pValuesHolderList.add(pvhOuterRadius);

            mPathCheckedInner.reset();
            pvhInnerRadius = PropertyValuesHolder.ofKeyframe(PVH_INNER_RADIUS
                    , Keyframe.ofFloat(0, innerRadius), Keyframe.ofFloat(0.6f, innerRadius), Keyframe.ofFloat(0.9f, mOuterStartRadius / 3 * 2)
                    , Keyframe.ofFloat(1f, mOuterStartRadius / 2));
            pValuesHolderList.add(pvhInnerRadius);
        } else {
            mStrokeColor = strokeColor;
            scRedDelta = Color.red(mStartColor) - scRedStart;
            scGreenDelta = Color.green(mStartColor) - scGreenStart;
            scBlueDelta = Color.blue(mStartColor) - scBlueStart;
            int pvhCount = 1;
            if (execFraction > 0.2f) {
                pvhCount++;
                mPathCheckedOuter.reset();
                mPathCheckedOuter.addCircle(centerX, centerY, OUTER_MIN_RADIUS, Path.Direction.CCW);
                if (execFraction > 0.6f) {
                    pvhCount++;
                }
            }

            Keyframe sckfStart = Keyframe.ofFloat(0, 0), sckfEnd = Keyframe.ofFloat(1, 1);
            if (pvhCount == 3) {
                pvhInnerRadius = PropertyValuesHolder.ofKeyframe(PVH_INNER_RADIUS
                        , Keyframe.ofFloat(0, innerRadius), Keyframe.ofFloat(0.4f, 0), Keyframe.ofFloat(1f, 0));
                pValuesHolderList.add(pvhInnerRadius);

                pvhOuterRadius = PropertyValuesHolder.ofKeyframe(PVH_OUTER_RADIUS, Keyframe.ofFloat(0, outerRadius)
                        , Keyframe.ofFloat(0.4f, outerRadius), Keyframe.ofFloat(0.8f, mOuterStartRadius), Keyframe.ofFloat(1, mOuterStartRadius));
                pValuesHolderList.add(pvhOuterRadius);

                pvhStrokeColor = PropertyValuesHolder.ofKeyframe(PVH_STROKE_COLOR, sckfStart, Keyframe.ofFloat(0.8f, 0), sckfEnd);
            } else if (pvhCount == 2) {
                pvhOuterRadius = PropertyValuesHolder.ofKeyframe(PVH_OUTER_RADIUS, Keyframe.ofFloat(0, outerRadius)
                        , Keyframe.ofFloat(0.4f, mOuterStartRadius), Keyframe.ofFloat(1, mOuterStartRadius));
                pValuesHolderList.add(pvhOuterRadius);

                pvhStrokeColor = PropertyValuesHolder.ofKeyframe(PVH_STROKE_COLOR, sckfStart, Keyframe.ofFloat(0.4f, 0), sckfEnd);
            } else {
                pvhStrokeColor = PropertyValuesHolder.ofKeyframe(PVH_STROKE_COLOR, sckfStart, sckfEnd);
            }

            pValuesHolderList.add(pvhStrokeColor);
        }

        mValueAnimator.setValues(pValuesHolderList.toArray(new PropertyValuesHolder[0]));
        mValueAnimator.setDuration(2000);
        final ValueAnimator.AnimatorUpdateListener updateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mExecFraction = animation.getAnimatedFraction();
                float strokeColorRadio = (float) animation.getAnimatedValue(PVH_STROKE_COLOR);
                int red = (int) (scRedStart + scRedDelta * strokeColorRadio);
                int green = (int) (scGreenStart + scGreenDelta * strokeColorRadio);
                int blue = (int) (scBlueStart + scBlueDelta * strokeColorRadio);
                mStrokeColor = Color.argb(0xFF, red, green, blue);

                Object objOuterRadius = animation.getAnimatedValue(PVH_OUTER_RADIUS);
                if (objOuterRadius instanceof Float) {
                    mOuterRadius = (float) objOuterRadius;
                }
                Object objInnerRadius = animation.getAnimatedValue(PVH_INNER_RADIUS);
                if (objInnerRadius instanceof Float) {
                    mInnerRadius = (float) objInnerRadius;
                }
                /*Log.v(AnimRadioButton.TAG, "keyframe, mStrokeColor: " + mStrokeColor + ", mOuterRadius: " + mOuterRadius
                        + ", mInnerRadius: " + mInnerRadius + ", mExecFraction: " + mExecFraction + ", object: " + RadioDrawable2.this);*/

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
                mValueAnimator.removeListener(this);
                mValueAnimator.removeUpdateListener(updateListener);
                Log.d(AnimRadioButton.TAG, "onAnimationEnd, mStrokeColor: " + mStrokeColor + ", mOuterRadius: " + mOuterRadius
                        + ", mInnerRadius: " + mInnerRadius + ", mExecFraction: " + mExecFraction + ", object: " + RadioDrawable2.this);
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

    public int getStrokeColor() {
        return mStrokeColor;
    }

    public float getOuterRadius() {
        return mOuterRadius;
    }

    public float getInnerRadius() {
        return mInnerRadius;
    }

    public float getExecFraction() {
        return mExecFraction;
    }

}
