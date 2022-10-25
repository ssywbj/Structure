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
import android.graphics.PathMeasure;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.suheng.structure.view.utils.MathUtils;

//https://www.kancloud.cn/alex_wsc/android_art/1828599
//https://blog.csdn.net/lmj623565791/article/details/43752383
public class CheckedDrawable extends Drawable {
    //private static final int ANIM_DURATION = 500;
    private static final int ANIM_DURATION = 2000;
    private static final float FRAMES_BORDER_RATIO = 0.4f;
    private static final float FRAMES_TICK_RATIO_MAX = 0.5f;
    private static final float FRAMES_TICK_RATIO = 0.1f;

    private static final String PVH_BORDER = "pvh_border";
    private static final String PVH_TICK = "pvh_tick";
    private static final String PVH_PATH_START = "pvh_path_start";

    private final ValueAnimator mValueAnimator;
    private final Context mContext;

    private Bitmap mNormalBitmap, mCheckedBitmap;
    private boolean mChecked;

    private int mCurrentLeft, mCurrentTop;
    private int mAlpha = 255, mCurrentAlpha;
    private float mRadius, mCurrentRadius;
    private float mTickLength, mDeltaTickLength;

    private final Paint mPaintTick;
    private final Path mPathBorder;
    private final Path mPathTick;
    private final PathMeasure mMeasurePathFullTick;

    private final float[] mPointStart = new float[2];
    private final float[] mTanPointStart = new float[2];
    private final Path mPathPointStart;
    private final PathMeasure mMeasurePathPointStart;
    private float mPathPointStartLength, mDeltaPathPointStartLength;

    private final float[] mPointEnd = new float[2];
    private final float[] mTanPointEnd = new float[2];

    private float mPointTickBottomX, mPointTickBottomY;

    private float mExecFraction = 1;

    public CheckedDrawable(Context context, boolean isChecked) {
        mContext = context;

        mPaintTick = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        mPaintTick.setStyle(Paint.Style.STROKE);
        mPaintTick.setStrokeCap(Paint.Cap.ROUND);
        mPaintTick.setStrokeJoin(Paint.Join.ROUND);
        mPaintTick.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4.2f, context.getResources().getDisplayMetrics()));
        //mPaintTick.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, context.getResources().getDisplayMetrics()));

        mValueAnimator = ValueAnimator.ofFloat();

        mPathBorder = new Path();

        mPathTick = new Path();
        mMeasurePathFullTick = new PathMeasure();

        mPathPointStart = new Path();
        mMeasurePathPointStart = new PathMeasure();

        this.setBitmap();
        this.setChecked(isChecked);

        mRadius = 10.67f * 3;
        //mRadius = 10.67f * 1.5f;
    }

    public CheckedDrawable(Context context) {
        this(context, false);
    }

    private void setChecked(boolean checked) {
        mChecked = checked;

        mCurrentLeft = checked ? getIntrinsicWidth() / 2 : 0;
        mCurrentTop = checked ? getIntrinsicHeight() / 2 : 0;
        mCurrentAlpha = checked ? mAlpha : 0;
        mCurrentRadius = checked ? 0 : mRadius;

        Path pathFullTick = new Path();
        float dx = getIntrinsicWidth() / 2f;
        mDeltaTickLength = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, mContext.getResources().getDisplayMetrics());
        //mDeltaTickLength = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3.5f, mContext.getResources().getDisplayMetrics());
        float x = -dx / 2 - mDeltaTickLength;
        mPointTickBottomX = 0 - mDeltaTickLength / 2;
        pathFullTick.moveTo(x, mPointTickBottomX);
        mPointTickBottomY = dx / 2 - mDeltaTickLength / 2;
        mPointTickBottomX += 2;
        pathFullTick.lineTo(mPointTickBottomX, mPointTickBottomY);
        float x1 = dx / 2 + mDeltaTickLength;
        float y2 = 0 - 2 * mDeltaTickLength;
        pathFullTick.lineTo(x1, y2);
        mMeasurePathFullTick.setPath(pathFullTick, false);

        mPathPointStart.reset();
        mMeasurePathFullTick.getSegment(0, 40, mPathPointStart, true);
        mDeltaPathPointStartLength = 16;
        /*mMeasurePathFullTick.getSegment(0, 22, mPathPointStart, true);
        mDeltaPathPointStartLength = 9;*/
        mMeasurePathPointStart.setPath(mPathPointStart, false);

        mPointEnd[0] = x;
        mPointEnd[1] = mPointTickBottomX;

        mPointStart[0] = x;
        mPointStart[1] = mPointTickBottomX;
        Log.d(AnimCheckBox.TAG, "setChecked, check: " + checked + ", (" + x + ", " + mPointTickBottomX + "), (" + mPointTickBottomX + ", " + mPointTickBottomY + "), (" + x1 + ", " + y2 + ")" + ", mPos(x, y): mPos(" + x + ", " + mPointTickBottomX + ")" + ", this: " + this);

        mTickLength = checked ? mMeasurePathFullTick.getLength() - mDeltaTickLength : 0;
        mMeasurePathFullTick.getSegment(mDeltaTickLength, mTickLength, mPathTick, true);

        mPathPointStartLength = checked ? mMeasurePathPointStart.getLength() - mDeltaPathPointStartLength : 0;
    }

    private void setBitmap() {
        mNormalBitmap = MathUtils.drawable2Bitmap(ContextCompat.getDrawable(mContext, R.drawable.checkbox_unchecked));
        mCheckedBitmap = MathUtils.drawable2Bitmap(ContextCompat.getDrawable(mContext, R.drawable.checkbox_checked_bg));
        /*mCheckedBitmap = Bitmap.createBitmap(mNormalBitmap.getWidth(), mNormalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mCheckedBitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        paint.setColor(Color.BLUE);
        canvas.drawRoundRect(0, 0, mNormalBitmap.getWidth(), mNormalBitmap.getHeight(), mRadius, mRadius, paint);*/
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

        canvas.drawBitmap(mNormalBitmap, 0, 0, null);

        int cc = canvas.saveLayerAlpha(0, 0, getIntrinsicWidth(), getIntrinsicHeight(), mCurrentAlpha, Canvas.ALL_SAVE_FLAG);
        //int cc = canvas.saveLayerAlpha(0, 0, getIntrinsicWidth(), getIntrinsicHeight(), 255, Canvas.ALL_SAVE_FLAG);
        canvas.clipPath(mPathBorder, Region.Op.DIFFERENCE);
        canvas.drawBitmap(mCheckedBitmap, 0, 0, null);
        canvas.restoreToCount(cc);

        canvas.save();
        canvas.translate(getIntrinsicWidth() / 2f, getIntrinsicHeight() / 2f);

        mPaintTick.setColor(Color.WHITE);
        canvas.drawPath(mPathTick, mPaintTick);
        canvas.restore();
    }

    private void setAnimParams(CheckedDrawable reverse) {
        this.setAnimParams(reverse.mCurrentLeft, reverse.mCurrentTop, reverse.mCurrentAlpha, reverse.mCurrentRadius, reverse.mTickLength, reverse.mPathPointStartLength, reverse.mExecFraction);
    }

    private void setAnimParams(final int currentLeft, final int currentTop, final int currentAlpha, final float currentRadius
            , final float tickLength, final float startLength, final float execFraction) {
        final Rect bounds = getBounds();
        final int centerX = bounds.centerX();
        final int centerY = bounds.centerY();
        Log.v(AnimCheckBox.TAG, "startAnim, centerX: " + centerX + ", centerY: " + centerY + ", bounds: " + bounds.toShortString() + ", mChecked: " + mChecked + ", execFraction: " + execFraction + ", this:" + this);
        Log.v(AnimCheckBox.TAG, "startAnim, tickLength: " + tickLength + ", startLength: " + startLength);

        final int leftDelta, topDelta, alphaDelta;
        float radiusDelta;
        int keyframeCount = 1;
        if (mChecked) {
            if (execFraction > FRAMES_TICK_RATIO) {
                keyframeCount++;
            }
            if (execFraction > FRAMES_TICK_RATIO + FRAMES_TICK_RATIO_MAX) {
                keyframeCount++;
            }

            leftDelta = centerX - currentLeft;
            topDelta = centerY - currentTop;
            alphaDelta = mAlpha - currentAlpha;
            radiusDelta = -currentRadius;

            if (keyframeCount == 3) {
                float fractionBorder = (execFraction - FRAMES_TICK_RATIO - FRAMES_TICK_RATIO_MAX) / execFraction;
                float fractionTickMax = FRAMES_TICK_RATIO_MAX / execFraction + fractionBorder;
                float stopDTickMax = mMeasurePathFullTick.getLength();
                float stopDTick = stopDTickMax - mDeltaTickLength;

                float stopDStartMax = mMeasurePathPointStart.getLength();
                float stopDStart = stopDStartMax - mDeltaPathPointStartLength;

                mValueAnimator.setValues(
                        PropertyValuesHolder.ofKeyframe(PVH_BORDER
                                , Keyframe.ofFloat(0, 0)
                                , Keyframe.ofFloat(fractionBorder, 1)
                                , Keyframe.ofFloat(1, 1)),
                        PropertyValuesHolder.ofKeyframe(PVH_TICK
                                , Keyframe.ofFloat(0, tickLength)
                                , Keyframe.ofFloat(fractionBorder, tickLength)
                                , Keyframe.ofFloat(fractionTickMax, stopDTickMax)
                                , Keyframe.ofFloat(1, stopDTick)),
                        PropertyValuesHolder.ofKeyframe(PVH_PATH_START
                                , Keyframe.ofFloat(0, startLength)
                                , Keyframe.ofFloat(fractionBorder, startLength)
                                , Keyframe.ofFloat(fractionTickMax, stopDStartMax)
                                , Keyframe.ofFloat(1, stopDStart)));
            } else if (keyframeCount == 2) {
                float fractionTickMax = (execFraction - FRAMES_TICK_RATIO) / execFraction;
                float stopDTickMax = mMeasurePathFullTick.getLength();
                float stopDTick = stopDTickMax - mDeltaTickLength;

                float stopDStartPathMax = mMeasurePathPointStart.getLength();
                float stopDStartPath = stopDStartPathMax - mDeltaPathPointStartLength;

                mValueAnimator.setValues(
                        PropertyValuesHolder.ofKeyframe(PVH_TICK
                                , Keyframe.ofFloat(0, tickLength)
                                , Keyframe.ofFloat(fractionTickMax, stopDTickMax)
                                , Keyframe.ofFloat(1, stopDTick)),
                        PropertyValuesHolder.ofKeyframe(PVH_PATH_START
                                , Keyframe.ofFloat(0, startLength)
                                , Keyframe.ofFloat(fractionTickMax, stopDStartPathMax)
                                , Keyframe.ofFloat(1, stopDStartPath)));
            } else {
                float stopDTick = mMeasurePathFullTick.getLength() - mDeltaTickLength;
                float stopDStart = mMeasurePathPointStart.getLength() - mDeltaPathPointStartLength;

                mValueAnimator.setValues(
                        PropertyValuesHolder.ofKeyframe(PVH_TICK
                                , Keyframe.ofFloat(0, tickLength)
                                , Keyframe.ofFloat(1, stopDTick)),
                        PropertyValuesHolder.ofKeyframe(PVH_PATH_START
                                , Keyframe.ofFloat(0, startLength)
                                , Keyframe.ofFloat(1, stopDStart)));
            }
        } else {
            if (execFraction > FRAMES_BORDER_RATIO) {
                keyframeCount++;
            }
            if (execFraction > FRAMES_BORDER_RATIO + FRAMES_TICK_RATIO_MAX) {
                keyframeCount++;
            }

            leftDelta = bounds.left - currentLeft;
            topDelta = bounds.top - currentTop;
            alphaDelta = -currentAlpha;
            radiusDelta = mRadius - currentRadius;

            if (keyframeCount == 3) {
                float fractionTick = (execFraction - FRAMES_BORDER_RATIO - FRAMES_TICK_RATIO_MAX) / execFraction;

                float fractionTickMax = FRAMES_TICK_RATIO_MAX / execFraction + fractionTick;
                float stopDTickMax = mMeasurePathFullTick.getLength();
                float stopDStartMax = mMeasurePathPointStart.getLength();

                mValueAnimator.setValues(
                        PropertyValuesHolder.ofKeyframe(PVH_TICK
                                , Keyframe.ofFloat(0, tickLength)
                                , Keyframe.ofFloat(fractionTick, stopDTickMax)
                                , Keyframe.ofFloat(fractionTickMax, 0)
                                , Keyframe.ofFloat(1, 0)),
                        PropertyValuesHolder.ofKeyframe(PVH_PATH_START
                                , Keyframe.ofFloat(0, startLength)
                                , Keyframe.ofFloat(fractionTick, stopDStartMax)
                                , Keyframe.ofFloat(fractionTickMax, 0)
                                , Keyframe.ofFloat(1, 0)),
                        PropertyValuesHolder.ofKeyframe(PVH_BORDER
                                , Keyframe.ofFloat(0, 0)
                                , Keyframe.ofFloat(fractionTickMax, 0)
                                , Keyframe.ofFloat(1, 1)));
            } else if (keyframeCount == 2) {
                float fractionTickMax = (execFraction - FRAMES_BORDER_RATIO) / execFraction;

                mValueAnimator.setValues(
                        PropertyValuesHolder.ofKeyframe(PVH_TICK
                                , Keyframe.ofFloat(0, tickLength)
                                , Keyframe.ofFloat(fractionTickMax, 0)
                                , Keyframe.ofFloat(1, 0)),
                        PropertyValuesHolder.ofKeyframe(PVH_PATH_START
                                , Keyframe.ofFloat(0, startLength)
                                , Keyframe.ofFloat(fractionTickMax, 0)
                                , Keyframe.ofFloat(1, 0)),
                        PropertyValuesHolder.ofKeyframe(PVH_BORDER
                                , Keyframe.ofFloat(0, 0)
                                , Keyframe.ofFloat(fractionTickMax, 0)
                                , Keyframe.ofFloat(1, 1)));
            } else {
                mValueAnimator.setValues(
                        PropertyValuesHolder.ofKeyframe(PVH_BORDER
                                , Keyframe.ofFloat(0, 0)
                                , Keyframe.ofFloat(1, 1)));
            }
        }
        /*Log.v(AnimCheckBox.TAG, "startAnim, currentLeft-endLeft: " + currentLeft + "-" + endLeft
                + ", currentTop-endTop: " + currentTop + "-" + endTop
                + ", currentAlpha-endAlpha: " + currentAlpha + "-" + endAlpha
                + ", currentRadius-endRadius: " + currentRadius + "-" + endRadius);*/

        mValueAnimator.setDuration((long) (ANIM_DURATION * execFraction));

        final ValueAnimator.AnimatorUpdateListener animatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mExecFraction = animation.getAnimatedFraction();

                Object objBorderRatio = animation.getAnimatedValue(PVH_BORDER);
                Object objTick = animation.getAnimatedValue(PVH_TICK);
                Object objPathStart = animation.getAnimatedValue(PVH_PATH_START);
                if (objBorderRatio instanceof Float) {
                    float borderRatio = (float) objBorderRatio;
                    mCurrentLeft = (int) (currentLeft + leftDelta * borderRatio);
                    mCurrentTop = (int) (currentTop + topDelta * borderRatio);
                    mCurrentAlpha = (int) (currentAlpha + alphaDelta * borderRatio);
                    mCurrentRadius = (int) (currentRadius + radiusDelta * borderRatio);
                }
                if (objTick instanceof Float) {
                    mTickLength = (float) objTick;
                }
                if (objPathStart instanceof Float) {
                    mPathPointStartLength = (float) objPathStart;
                }

                //Log.d(AnimCheckBox.TAG, "mCurrentRadius: " + mCurrentRadius + ", mCurrentAlpha: " + mCurrentAlpha + ", mCurrentLeft: " + mCurrentLeft + ", mCurrentTop: " + mCurrentTop + ", this: " + this);
                mMeasurePathFullTick.getPosTan(mTickLength, mPointEnd, mTanPointEnd);
                //Log.v(AnimCheckBox.TAG, "mTickLength: " + mTickLength + ", mPosEnd x: " + mPointEnd[0] + ", y: " + mPointEnd[1] + ", mTan x: " + mTanPointEnd[0] + ", y: " + mTanPointEnd[1] + ", mExecFraction: " + mExecFraction);
                mMeasurePathPointStart.getPosTan(mPathPointStartLength, mPointStart, mTanPointStart);
                //Log.d(AnimCheckBox.TAG, "mStartLength: " + mPathPointStartLength + ", mPosStart x: " + mPointStart[0] + ", y: " + mPointStart[1] + ", mTan x: " + mTanPointStart[0] + ", y: " + mTanPointStart[1]);
                mPathTick.reset();
                if ((Math.abs(mPointStart[0] - mPointEnd[0]) > 0.0000001f && Math.abs(mPointStart[1] - mPointEnd[1]) > 0.0000001f)) {
                    mPathTick.moveTo(mPointStart[0], mPointStart[1]);
                    if (!(Math.abs(mTanPointStart[0] - mTanPointEnd[0]) < 0.0000001f && Math.abs(mTanPointStart[1] - mTanPointEnd[1]) < 0.0000001f)) {
                        mPathTick.lineTo(mPointTickBottomX, mPointTickBottomY);
                    }
                    mPathTick.lineTo(mPointEnd[0], mPointEnd[1]);
                }

                mPathBorder.reset();
                mPathBorder.addRoundRect(mCurrentLeft, mCurrentTop, bounds.right - mCurrentLeft, bounds.bottom - mCurrentTop, mCurrentRadius, mCurrentRadius, Path.Direction.CCW);

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
                Log.v(AnimCheckBox.TAG, "onAnimationEnd, mTickLength: " + mTickLength + ", mPosEnd x: " + mPointEnd[0] + ", y: " + mPointEnd[1] + ", mTan x: " + mTanPointEnd[0] + ", y: " + mTanPointEnd[1] + ", mExecFraction: " + mExecFraction + ", object: " + CheckedDrawable.this);
                Log.d(AnimCheckBox.TAG, "onAnimationEnd, mStartLength: " + mPathPointStartLength + ", mPosStart x: " + mPointStart[0] + ", y: " + mPointStart[1] + ", mTan x: " + mTanPointStart[0] + ", y: " + mTanPointStart[1] + ", mTickLength: " + mTickLength + ", mStartLength: " + mPathPointStartLength);
            }
        });
    }

    public void startAnim(CheckedDrawable reverse) {
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

