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
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

//https://www.kancloud.cn/alex_wsc/android_art/1828599
//https://blog.csdn.net/lmj623565791/article/details/43752383
public class CheckedDrawable extends Drawable {
    //private static final int ANIM_DURATION = 500;
    private static final int ANIM_DURATION = 2000;
    private static final float FRAMES_BORDER_RATIO = 0.4f;
    private static final float FRAMES_TICK_RATIO_MAX = 0.5f;
    private static final float FRAMES_TICK_RATIO = 0.1f;

    private static final String PVH_LEFT = "pvh_left";
    private static final String PVH_TOP = "pvh_top";
    private static final String PVH_RADIUS = "pvh_radius";
    private static final String PVH_ALPHA = "pvh_alpha";
    private static final String PVH_TICK = "pvh_tick";
    private static final String PVH_PATH_START = "pvh_path_start";

    private final ValueAnimator mValueAnimator;
    private final Context mContext;

    private Bitmap mNormalBitmap, mCheckedBitmap;
    private boolean mChecked;

    private int mCurrentLeft, mCurrentTop;
    private int mAlpha = 255, mCurrentAlpha;
    private float mRadius = 10.67f * 3, mCurrentRadius;
    private float mTickLength, mDeltaTickLength;
    private float mStartLength, mDeltaStartLength;

    private final Paint mPaintTick, mPaintPosEnd;
    private final Path mPathBorder;
    private final Path mPathTick, mPathStart, mPathFullTick;
    private final PathMeasure mMeasurePathFullTick, mPathStartMeasure;

    private final float[] mPosEnd = new float[2], mPosStart = new float[2];
    private final float[] mTan = new float[2], mTanStart = new float[2];
    private float y, y1;

    private float mExecFraction = 1;

    public CheckedDrawable(Context context, boolean isChecked) {
        mContext = context;

        mPaintTick = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        mPaintTick.setStyle(Paint.Style.STROKE);
        mPaintTick.setStrokeCap(Paint.Cap.ROUND);
        mPaintTick.setStrokeJoin(Paint.Join.ROUND);
        mPaintTick.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4.5f, context.getResources().getDisplayMetrics()));
        //mPaintTick.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, context.getResources().getDisplayMetrics()));

        mPaintPosEnd = new Paint();
        mPaintPosEnd.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, context.getResources().getDisplayMetrics()));

        mPathBorder = new Path();
        mPathFullTick = new Path();
        mPathTick = new Path();
        mPathStart = new Path();
        mValueAnimator = ValueAnimator.ofFloat();

        mMeasurePathFullTick = new PathMeasure();

        mPathStartMeasure = new PathMeasure();

        this.setBitmap();
        this.setChecked(isChecked);
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

        mPathFullTick.reset();
        float dx = getIntrinsicWidth() / 2f;
        mDeltaTickLength = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, mContext.getResources().getDisplayMetrics());
        //mDeltaTickLength = 0;
        float x = -dx / 2 - mDeltaTickLength;
        y = 0 - mDeltaTickLength / 2;
        mPathFullTick.moveTo(x, y);
        y1 = dx / 2 - mDeltaTickLength / 2;
        mPathFullTick.lineTo(y, y1);
        float x1 = dx / 2 + mDeltaTickLength;
        float y2 = 0 - 2 * mDeltaTickLength;
        mPathFullTick.lineTo(x1, y2);
        mMeasurePathFullTick.setPath(mPathFullTick, false);

        mPathStart.reset();
        mMeasurePathFullTick.getSegment(0, 40, mPathStart, true);
        mDeltaStartLength = 16;
        mPathStartMeasure.setPath(mPathStart, false);

        mPosEnd[0] = x;
        mPosEnd[1] = y;

        mPosStart[0] = x;
        mPosStart[1] = y;
        Log.d(AnimCheckBox.TAG, "setChecked, check: " + checked + ", (" + x + ", " + y + "), (" + y + ", " + y1 + "), (" + x1 + ", " + y2 + ")"
                + ", mPos(x, y): mPos(" + x + ", " + y + ")" + ", this: " + this);

        mTickLength = checked ? mMeasurePathFullTick.getLength() - mDeltaTickLength : 0;
        mMeasurePathFullTick.getSegment(mDeltaTickLength, mTickLength, mPathTick, true);

        mStartLength = checked ? mPathStartMeasure.getLength() - mDeltaStartLength : 0;
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

        int cc = canvas.saveLayerAlpha(0, 0, getIntrinsicWidth(), getIntrinsicHeight(), mCurrentAlpha, Canvas.ALL_SAVE_FLAG);
        //int cc = canvas.saveLayerAlpha(0, 0, getIntrinsicWidth(), getIntrinsicHeight(), 255, Canvas.ALL_SAVE_FLAG);
        canvas.clipPath(mPathBorder, Region.Op.DIFFERENCE);
        canvas.drawBitmap(mCheckedBitmap, 0, 0, null);
        canvas.restoreToCount(cc);

        canvas.save();
        canvas.translate(getIntrinsicWidth() / 2f, getIntrinsicHeight() / 2f);

        /*mPaintTick.setColor(Color.GREEN);
        canvas.drawPath(mPathFullTick, mPaintTick);*/

        /*mPaintPosEnd.setColor(Color.BLACK);
        canvas.drawPoints(mPosEnd, mPaintPosEnd);
        mPaintPosEnd.setColor(Color.YELLOW);
        canvas.drawPoint(mPosStart[0], mPosStart[1], mPaintPosEnd);
        mPaintTick.setColor(Color.GRAY);
        canvas.drawPath(mPathStart, mPaintTick);*/

        mPaintTick.setColor(Color.WHITE);
        canvas.drawPath(mPathTick, mPaintTick);
        canvas.restore();
    }

    private void setAnimParams(CheckedDrawable reverse) {
        this.setAnimParams(reverse.mCurrentLeft, reverse.mCurrentTop, reverse.mCurrentAlpha, reverse.mCurrentRadius
                , reverse.mTickLength, reverse.mStartLength, reverse.mExecFraction);
    }

    private void setAnimParams(int currentLeft, int currentTop, int currentAlpha, float currentRadius, float tickLength, float startLength, float execFraction) {
        final Rect bounds = getBounds();
        final int centerX = bounds.centerX();
        final int centerY = bounds.centerY();
        Log.v(AnimCheckBox.TAG, "startAnim, centerX: " + centerX + ", centerY: " + centerY
                + ", bounds: " + bounds.toShortString() + ", mChecked: " + mChecked + ", execFraction: " + execFraction + ", this:" + this);
        Log.v(AnimCheckBox.TAG, "startAnim, tickLength: " + tickLength + ", startLength: " + startLength);

        int endLeft, endTop, endAlpha;
        float endRadius;
        int keyframeCount = 1;
        if (mChecked) {
            if (execFraction > FRAMES_TICK_RATIO) {
                keyframeCount++;
            }
            if (execFraction > FRAMES_TICK_RATIO + FRAMES_TICK_RATIO_MAX) {
                keyframeCount++;
            }

            endLeft = centerX;
            endTop = centerY;
            endAlpha = mAlpha;
            endRadius = 0;

            if (keyframeCount == 3) {
                float fractionBorder = (execFraction - FRAMES_TICK_RATIO - FRAMES_TICK_RATIO_MAX) / execFraction;
                float fractionTickMax = FRAMES_TICK_RATIO_MAX / execFraction + fractionBorder;
                float stopDTickMax = mMeasurePathFullTick.getLength();
                float stopDTick = stopDTickMax - mDeltaTickLength;

                float stopDStartMax = mPathStartMeasure.getLength();
                float stopDStart = stopDStartMax - mDeltaStartLength;

                mValueAnimator.setValues(
                        PropertyValuesHolder.ofKeyframe(PVH_LEFT
                                , Keyframe.ofInt(0, currentLeft)
                                , Keyframe.ofInt(fractionBorder, endLeft)
                                , Keyframe.ofInt(1, endLeft)),
                        PropertyValuesHolder.ofKeyframe(PVH_TOP
                                , Keyframe.ofInt(0, currentTop)
                                , Keyframe.ofInt(fractionBorder, endTop)
                                , Keyframe.ofInt(1, endTop)),
                        PropertyValuesHolder.ofKeyframe(PVH_ALPHA
                                , Keyframe.ofInt(0, currentAlpha)
                                , Keyframe.ofInt(fractionBorder, endAlpha)
                                , Keyframe.ofInt(1, endAlpha)),
                        PropertyValuesHolder.ofKeyframe(PVH_RADIUS
                                , Keyframe.ofFloat(0, currentRadius)
                                , Keyframe.ofFloat(fractionBorder, endRadius)
                                , Keyframe.ofFloat(1, endRadius)),
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

                float stopDStartPathMax = mPathStartMeasure.getLength();
                float stopDStartPath = stopDStartPathMax - mDeltaStartLength;

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
                float stopDStart = mPathStartMeasure.getLength() - mDeltaStartLength;

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

            endLeft = bounds.left;
            endTop = bounds.top;
            endAlpha = 0;
            endRadius = mRadius;

            if (keyframeCount == 3) {
                float fractionTick = (execFraction - FRAMES_BORDER_RATIO - FRAMES_TICK_RATIO_MAX) / execFraction;

                float fractionTickMax = FRAMES_TICK_RATIO_MAX / execFraction + fractionTick;
                float stopDTickMax = mMeasurePathFullTick.getLength();
                float stopDStartMax = mPathStartMeasure.getLength();

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
                        PropertyValuesHolder.ofKeyframe(PVH_LEFT
                                , Keyframe.ofInt(0, currentLeft)
                                , Keyframe.ofInt(fractionTickMax, currentLeft)
                                , Keyframe.ofInt(1, endLeft)),
                        PropertyValuesHolder.ofKeyframe(PVH_TOP
                                , Keyframe.ofInt(0, currentTop)
                                , Keyframe.ofInt(fractionTickMax, currentTop)
                                , Keyframe.ofInt(1, endTop)),
                        PropertyValuesHolder.ofKeyframe(PVH_ALPHA
                                , Keyframe.ofInt(0, currentAlpha)
                                , Keyframe.ofInt(fractionTickMax, currentAlpha)
                                , Keyframe.ofInt(1, endAlpha)),
                        PropertyValuesHolder.ofKeyframe(PVH_RADIUS
                                , Keyframe.ofFloat(0, currentRadius)
                                , Keyframe.ofFloat(fractionTickMax, currentRadius)
                                , Keyframe.ofFloat(1, endRadius)));
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
                        PropertyValuesHolder.ofKeyframe(PVH_LEFT
                                , Keyframe.ofInt(0, currentLeft)
                                , Keyframe.ofInt(fractionTickMax, currentLeft)
                                , Keyframe.ofInt(1, endLeft)),
                        PropertyValuesHolder.ofKeyframe(PVH_TOP
                                , Keyframe.ofInt(0, currentTop)
                                , Keyframe.ofInt(fractionTickMax, currentTop)
                                , Keyframe.ofInt(1, endTop)),
                        PropertyValuesHolder.ofKeyframe(PVH_ALPHA
                                , Keyframe.ofInt(0, currentAlpha)
                                , Keyframe.ofInt(fractionTickMax, currentAlpha)
                                , Keyframe.ofInt(1, endAlpha)),
                        PropertyValuesHolder.ofKeyframe(PVH_RADIUS
                                , Keyframe.ofFloat(0, currentRadius)
                                , Keyframe.ofFloat(fractionTickMax, currentRadius)
                                , Keyframe.ofFloat(1, endRadius)));
            } else {
                mValueAnimator.setValues(
                        PropertyValuesHolder.ofKeyframe(PVH_LEFT
                                , Keyframe.ofInt(0, currentLeft)
                                , Keyframe.ofInt(1, endLeft)),
                        PropertyValuesHolder.ofKeyframe(PVH_TOP
                                , Keyframe.ofInt(0, currentTop)
                                , Keyframe.ofInt(1, endTop)),
                        PropertyValuesHolder.ofKeyframe(PVH_ALPHA
                                , Keyframe.ofInt(0, currentAlpha)
                                , Keyframe.ofInt(1, endAlpha)),
                        PropertyValuesHolder.ofKeyframe(PVH_RADIUS
                                , Keyframe.ofFloat(0, currentRadius)
                                , Keyframe.ofFloat(1, endRadius)));
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

                Object objLeft = animation.getAnimatedValue(PVH_LEFT);
                Object objTop = animation.getAnimatedValue(PVH_TOP);
                Object objRadius = animation.getAnimatedValue(PVH_RADIUS);
                Object objAlpha = animation.getAnimatedValue(PVH_ALPHA);
                Object objTick = animation.getAnimatedValue(PVH_TICK);
                Object objPathStart = animation.getAnimatedValue(PVH_PATH_START);
                if (objLeft instanceof Integer) {
                    mCurrentLeft = (int) objLeft;
                }
                if (objTop instanceof Integer) {
                    mCurrentTop = (int) objTop;
                }
                if (objLeft instanceof Integer) {
                    mCurrentAlpha = (int) objAlpha;
                }
                if (objRadius instanceof Float) {
                    mCurrentRadius = (float) objRadius;
                }
                if (objTick instanceof Float) {
                    mTickLength = (float) objTick;
                }
                if (objPathStart instanceof Float) {
                    mStartLength = (float) objPathStart;
                }

                //Log.d(AnimCheckBox.TAG, "mCurrentRadius: " + mCurrentRadius + ", mCurrentAlpha: " + mCurrentAlpha + ", mCurrentLeft: " + mCurrentLeft + ", mCurrentTop: " + mCurrentTop + ", this: " + this);
                mMeasurePathFullTick.getPosTan(mTickLength, mPosEnd, mTan);
                Log.v(AnimCheckBox.TAG, "mTickLength: " + mTickLength + ", mPosEnd x: " + mPosEnd[0] + ", y: " + mPosEnd[1] + ", mTan x: " + mTan[0] + ", y: " + mTan[1] + ", mExecFraction: " + mExecFraction);
                mPathStartMeasure.getPosTan(mStartLength, mPosStart, mTanStart);
                Log.d(AnimCheckBox.TAG, "mStartLength: " + mStartLength + ", mPosStart x: " + mPosStart[0] + ", y: " + mPosStart[1] + ", mTan x: " + mTanStart[0] + ", y: " + mTanStart[1]);
                mPathTick.reset();
                if ((Math.abs(mPosStart[0] - mPosEnd[0]) > 0.0000001f && Math.abs(mPosStart[1] - mPosEnd[1]) > 0.0000001f)) {
                    mPathTick.moveTo(mPosStart[0], mPosStart[1]);
                    if (!(Math.abs(mTanStart[0] - mTan[0]) < 0.0000001f && Math.abs(mTanStart[1] - mTan[1]) < 0.0000001f)) {
                        mPathTick.lineTo(y, y1);
                    }
                    mPathTick.lineTo(mPosEnd[0], mPosEnd[1]);
                }

                mPathBorder.reset();
                mPathBorder.addRoundRect(mCurrentLeft, mCurrentTop, bounds.right - mCurrentLeft, bounds.bottom - mCurrentTop
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
                Log.v(AnimCheckBox.TAG, "onAnimationEnd, mTickLength: " + mTickLength + ", mPosEnd x: " + mPosEnd[0] + ", y: " + mPosEnd[1] + ", mTan x: " + mTan[0] + ", y: " + mTan[1]
                        + ", mExecFraction: " + mExecFraction + ", object: " + CheckedDrawable.this);
                Log.d(AnimCheckBox.TAG, "onAnimationEnd, mStartLength: " + mStartLength + ", mPosStart x: " + mPosStart[0] + ", y: " + mPosStart[1] + ", mTan x: " + mTanStart[0]
                        + ", y: " + mTanStart[1] + ", mTickLength: " + mTickLength + ", mStartLength: " + mStartLength);
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
