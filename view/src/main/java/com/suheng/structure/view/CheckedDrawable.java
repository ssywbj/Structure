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
    private final Path mPath;
    private boolean mChecked;

    private int mCurrentLeft, mCurrentTop;
    private int mAlpha = 255, mCurrentAlpha;
    private float mRadius = 10.67f * 3, mCurrentRadius;
    private float mTickLength, mDeltaTickLength;
    private float mStartLength, mDeltaStartLength;

    private final Paint mPaintTick, mPaintPosEnd;
    private final Path mPathTick;
    private final Path pathTick = new Path(), mPathStart = new Path();
    private final PathMeasure mPathMeasure, mPathStartMeasure;

    private final float[] mPosEnd = new float[2];
    private final float[] mPosStart = new float[2];
    private final float[] mTan = new float[2];

    public CheckedDrawable(Context context, boolean isChecked) {
        mContext = context;

        mPaintTick = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        mPaintTick.setStyle(Paint.Style.STROKE);
        mPaintTick.setStrokeCap(Paint.Cap.ROUND);
        mPaintTick.setStrokeJoin(Paint.Join.ROUND);
        //mPaintTick.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, context.getResources().getDisplayMetrics()));
        mPaintTick.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, context.getResources().getDisplayMetrics()));

        mPaintPosEnd = new Paint();
        mPaintPosEnd.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, context.getResources().getDisplayMetrics()));

        mPath = new Path();
        mValueAnimator = ValueAnimator.ofFloat();

        mPathTick = new Path();
        mPathMeasure = new PathMeasure();

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

        pathTick.reset();
        float dx = getIntrinsicWidth() / 2f;
        mDeltaTickLength = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 7, mContext.getResources().getDisplayMetrics());
        //mDeltaTickLength = 0;
        float x = -dx / 2 - mDeltaTickLength;
        float y = 0 - mDeltaTickLength / 2;
        pathTick.moveTo(x, y);
        float y1 = dx / 2 - mDeltaTickLength / 2;
        pathTick.lineTo(y, y1);
        float x1 = dx / 2 + mDeltaTickLength;
        float y2 = 0 - 2 * mDeltaTickLength;
        pathTick.lineTo(x1, y2);
        mPathMeasure.setPath(pathTick, false);

        mPathStart.reset();
        mPathMeasure.getSegment(0, 40, mPathStart, true);
        mDeltaStartLength = 13;

        mPathStartMeasure.setPath(mPathStart, false);
        mStartLength = mPathStartMeasure.getLength();

        mPosEnd[0] = x;
        mPosEnd[1] = y;

        mPosStart[0] = x;
        mPosStart[1] = y;
        Log.d(AnimCheckBox.TAG, "setChecked, check: " + checked + ", (" + x + ", " + y + "), (" + y + ", " + y1 + "), (" + x1 + ", " + y2 + ")"
                + ", mPos(x, y): mPos(" + x + ", " + y + ")");

        mTickLength = checked ? mPathMeasure.getLength() - mDeltaTickLength : mDeltaTickLength;
        mPathMeasure.getSegment(mDeltaTickLength, mTickLength, mPathTick, true);
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
        canvas.clipPath(mPath, Region.Op.DIFFERENCE);
        canvas.drawBitmap(mCheckedBitmap, 0, 0, null);
        canvas.restoreToCount(cc);

        canvas.save();
        canvas.translate(getIntrinsicWidth() / 2f, getIntrinsicHeight() / 2f);
        mPaintTick.setColor(Color.WHITE);
        canvas.drawPath(pathTick, mPaintTick);
        //mPaintTick.setColor(Color.RED);
        //canvas.drawPath(mPathTick, mPaintTick);
        mPaintPosEnd.setColor(Color.BLACK);
        canvas.drawPoints(mPosEnd, mPaintPosEnd);
        mPaintPosEnd.setColor(Color.YELLOW);
        canvas.drawPoint(mPosStart[0], mPosStart[1], mPaintPosEnd);
        mPaintTick.setColor(Color.GRAY);
        canvas.drawPath(mPathStart, mPaintTick);
        canvas.restore();
    }

    public void setAnimParams(CheckedDrawable reverseDrawable) {
        this.setAnimParams(reverseDrawable.getCurrentLeft(), reverseDrawable.getCurrentTop()
                , reverseDrawable.getCurrentAlpha(), reverseDrawable.getCurrentRadius());
    }

    private void setAnimParams(int currentLeft, int currentTop, int currentAlpha, float currentRadius) {
        final Rect bounds = getBounds();
        final int centerX = bounds.centerX();
        final int centerY = bounds.centerY();
        Log.v(AnimCheckBox.TAG, "startAnim, centerX: " + centerX + ", centerY: " + centerY
                + ", bounds: " + bounds.toShortString() + ", mChecked: " + mChecked);

        int endLeft, endTop, endAlpha;
        float endRadius, startDTickMax, stopDTickMax, statTickFractionMax;
        float stopDTick, statTickFraction;
        float startDStartPathMax, stopDStartPath, stopDStartPathMax;
        if (mChecked) {
            /*currentLeft = bounds.left;
            currentTop = bounds.top;*/
            endLeft = centerX;
            endTop = centerY;
            endAlpha = mAlpha;
            endRadius = 0;

            startDTickMax = 0;
            statTickFractionMax = FRAMES_BORDER_RATIO;
            stopDTickMax = mPathMeasure.getLength();
            statTickFraction = statTickFractionMax + FRAMES_TICK_RATIO_MAX;
            stopDTick = stopDTickMax - mDeltaTickLength;

            startDStartPathMax = 0;
            stopDStartPathMax = mPathStartMeasure.getLength();
            stopDStartPath = stopDStartPathMax - mDeltaStartLength;

            mValueAnimator.setValues(PropertyValuesHolder.ofKeyframe(PVH_LEFT, Keyframe.ofInt(0, currentLeft), Keyframe.ofInt(1, endLeft))
                    , PropertyValuesHolder.ofKeyframe(PVH_TOP, Keyframe.ofInt(0, currentTop), Keyframe.ofInt(1, endTop))
                    , PropertyValuesHolder.ofKeyframe(PVH_ALPHA, Keyframe.ofInt(0, currentAlpha), Keyframe.ofInt(1, endAlpha))
                    , PropertyValuesHolder.ofKeyframe(PVH_RADIUS, Keyframe.ofFloat(0, currentRadius), Keyframe.ofFloat(1, endRadius))
                    , PropertyValuesHolder.ofKeyframe(PVH_TICK
                            , Keyframe.ofFloat(0, startDTickMax)
                            , Keyframe.ofFloat(statTickFractionMax, startDTickMax)
                            , Keyframe.ofFloat(statTickFraction, stopDTickMax)
                            , Keyframe.ofFloat(1, stopDTick))
                    , PropertyValuesHolder.ofKeyframe(PVH_PATH_START
                            , Keyframe.ofFloat(0, startDStartPathMax)
                            , Keyframe.ofFloat(statTickFractionMax, startDStartPathMax)
                            , Keyframe.ofFloat(statTickFraction, stopDStartPathMax)
                            , Keyframe.ofFloat(1, stopDStartPath)));

        } else {
            /*currentLeft = centerX;
            currentTop = centerY;*/
            endLeft = bounds.left;
            endTop = bounds.top;
            endAlpha = 0;
            endRadius = mRadius;

            startDTickMax = mPathMeasure.getLength();
            stopDTick = startDTickMax - mDeltaTickLength;
            stopDTickMax = 0;
            statTickFractionMax = FRAMES_TICK_RATIO + FRAMES_TICK_RATIO_MAX;


            startDStartPathMax = mPathStartMeasure.getLength();
            stopDStartPath = startDStartPathMax - mDeltaStartLength;
            stopDStartPathMax = 0;

            mValueAnimator.setValues(PropertyValuesHolder.ofKeyframe(PVH_LEFT, Keyframe.ofInt(0, currentLeft)
                    , Keyframe.ofInt(statTickFractionMax, currentLeft), Keyframe.ofInt(1, endLeft))
                    , PropertyValuesHolder.ofKeyframe(PVH_TOP, Keyframe.ofInt(0, currentTop)
                            , Keyframe.ofInt(statTickFractionMax, currentTop), Keyframe.ofInt(1, endTop))
                    , PropertyValuesHolder.ofKeyframe(PVH_ALPHA, Keyframe.ofInt(0, currentAlpha)
                            , Keyframe.ofInt(statTickFractionMax, currentAlpha), Keyframe.ofInt(1, endAlpha))
                    , PropertyValuesHolder.ofKeyframe(PVH_RADIUS, Keyframe.ofFloat(0, currentRadius)
                            , Keyframe.ofFloat(statTickFractionMax, currentRadius), Keyframe.ofFloat(1, endRadius))
                    , PropertyValuesHolder.ofKeyframe(PVH_TICK
                            , Keyframe.ofFloat(0, stopDTick)
                            , Keyframe.ofFloat(FRAMES_TICK_RATIO, startDTickMax)
                            , Keyframe.ofFloat(statTickFractionMax, stopDTickMax)
                            , Keyframe.ofFloat(1, stopDTickMax))
                    , PropertyValuesHolder.ofKeyframe(PVH_PATH_START
                            , Keyframe.ofFloat(0, stopDStartPath)
                            , Keyframe.ofFloat(FRAMES_TICK_RATIO, startDStartPathMax)
                            , Keyframe.ofFloat(statTickFractionMax, stopDStartPathMax)
                            , Keyframe.ofFloat(1, stopDStartPathMax)));
        }
        /*Log.v(AnimCheckBox.TAG, "startAnim, currentLeft-endLeft: " + currentLeft + "-" + endLeft
                + ", currentTop-endTop: " + currentTop + "-" + endTop
                + ", currentAlpha-endAlpha: " + currentAlpha + "-" + endAlpha
                + ", currentRadius-endRadius: " + currentRadius + "-" + endRadius);*/

        /*mValueAnimator.setValues(PropertyValuesHolder.ofInt(PVH_LEFT, currentLeft, endLeft)
                , PropertyValuesHolder.ofInt(PVH_TOP, currentTop, endTop)
                , PropertyValuesHolder.ofInt(PVH_ALPHA, currentAlpha, endAlpha)
                , PropertyValuesHolder.ofFloat(PVH_RADIUS, currentRadius, endRadius));*/

        //mValueAnimator.setDuration(1500);
        mValueAnimator.setDuration(ANIM_DURATION);

        final ValueAnimator.AnimatorUpdateListener animatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
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
                mPathTick.reset();
                //mPathMeasure.getSegment(mDeltaTickLength, mTickLength, mPathTick, true);
                mPathMeasure.getSegment(0, mTickLength, mPathTick, true);

                mPathMeasure.getPosTan(mTickLength, mPosEnd, mTan);
                Log.d(AnimCheckBox.TAG, "mTickLength: " + mTickLength + ", mPos x: " + mPosEnd[0] + ", y: " + mPosEnd[1] + ", mTan x: " + mTan[0] + ", y: " + mTan[1]);

                mPathStartMeasure.getPosTan(mStartLength, mPosStart, mTan);
                Log.d(AnimCheckBox.TAG, "mTickLength: " + mTickLength + ", mPos x: " + mPosEnd[0] + ", y: " + mPosEnd[1] + ", mTan x: " + mTan[0] + ", y: " + mTan[1]);

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

    private int getCurrentLeft() {
        return mCurrentLeft;
    }

    private int getCurrentTop() {
        return mCurrentTop;
    }

    private float getCurrentRadius() {
        return mCurrentRadius;
    }

    private int getCurrentAlpha() {
        return mCurrentAlpha;
    }

}
