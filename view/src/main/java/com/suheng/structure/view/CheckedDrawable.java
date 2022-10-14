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
    private static final String PVH_LEFT = "pvh_left";
    private static final String PVH_TOP = "pvh_top";
    private static final String PVH_RADIUS = "pvh_radius";
    private static final String PVH_ALPHA = "pvh_alpha";
    private static final String PVH_TICK = "pvh_tick";

    private final ValueAnimator mValueAnimator;
    private final Context mContext;

    private Bitmap mNormalBitmap, mCheckedBitmap;
    private final Path mPath;
    private boolean mChecked;

    private int mCurrentLeft, mCurrentTop;
    private int mAlpha = 255, mCurrentAlpha;
    private float mRadius = 10.67f * 3, mCurrentRadius;
    private float mTickLength;

    private final Paint mPaintTick;
    private final Path mPathTick;
    private final PathMeasure mPathMeasure;

    public CheckedDrawable(Context context, boolean isChecked) {
        mContext = context;

        mPaintTick = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        mPaintTick.setColor(Color.WHITE);
        mPaintTick.setStyle(Paint.Style.STROKE);
        mPaintTick.setStrokeCap(Paint.Cap.ROUND);
        mPaintTick.setStrokeJoin(Paint.Join.ROUND);
        mPaintTick.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, context.getResources().getDisplayMetrics()));

        mPath = new Path();
        mValueAnimator = ValueAnimator.ofFloat();

        mPathTick = new Path();
        mPathMeasure = new PathMeasure();

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

        Path pathTick = new Path();
        pathTick.reset();
        float dx = getIntrinsicWidth() / 2f;
        pathTick.moveTo(-dx / 2, 0);
        pathTick.lineTo(0, dx / 2);
        pathTick.lineTo(dx / 2, 0);
        mPathMeasure.setPath(pathTick, false);

        mTickLength = checked ? mPathMeasure.getLength() : 0;
        mPathMeasure.getSegment(0, mTickLength, mPathTick, true);
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
        canvas.drawPath(mPathTick, mPaintTick);
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
        /*Log.v(AnimCheckBox.TAG, "startAnim, centerX: " + centerX + ", centerY: " + centerY
                + ", bounds: " + bounds.toShortString() + ", mChecked: " + mChecked);*/

        int endLeft, endTop, endAlpha;
        float endRadius, startDTick, stopDTick, tickFraction;
        if (mChecked) {
            /*currentLeft = bounds.left;
            currentTop = bounds.top;*/
            endLeft = centerX;
            endTop = centerY;
            endAlpha = mAlpha;
            endRadius = 0;

            startDTick = 0;
            stopDTick = mPathMeasure.getLength();
            tickFraction = 0.4f;

            mValueAnimator.setValues(PropertyValuesHolder.ofKeyframe(PVH_LEFT, Keyframe.ofInt(0, currentLeft), Keyframe.ofInt(1, endLeft))
                    , PropertyValuesHolder.ofKeyframe(PVH_TOP, Keyframe.ofInt(0, currentTop), Keyframe.ofInt(1, endTop))
                    , PropertyValuesHolder.ofKeyframe(PVH_ALPHA, Keyframe.ofInt(0, currentAlpha), Keyframe.ofInt(1, endAlpha))
                    , PropertyValuesHolder.ofKeyframe(PVH_RADIUS, Keyframe.ofFloat(0, currentRadius), Keyframe.ofFloat(1, endRadius))
                    , PropertyValuesHolder.ofKeyframe(PVH_TICK
                            , Keyframe.ofFloat(0, startDTick)
                            , Keyframe.ofFloat(tickFraction, startDTick)
                            , Keyframe.ofFloat(1, stopDTick)));
        } else {
            /*currentLeft = centerX;
            currentTop = centerY;*/
            endLeft = bounds.left;
            endTop = bounds.top;
            endAlpha = 0;
            endRadius = mRadius;

            startDTick = mPathMeasure.getLength();
            stopDTick = 0;
            tickFraction = 0.6f;

            mValueAnimator.setValues(PropertyValuesHolder.ofKeyframe(PVH_LEFT, Keyframe.ofInt(0, currentLeft)
                    , Keyframe.ofInt(tickFraction, currentLeft), Keyframe.ofInt(1, endLeft))
                    , PropertyValuesHolder.ofKeyframe(PVH_TOP, Keyframe.ofInt(0, currentTop)
                            , Keyframe.ofInt(tickFraction, currentTop), Keyframe.ofInt(1, endTop))
                    , PropertyValuesHolder.ofKeyframe(PVH_ALPHA, Keyframe.ofInt(0, currentAlpha)
                            , Keyframe.ofInt(tickFraction, currentAlpha), Keyframe.ofInt(1, endAlpha))
                    , PropertyValuesHolder.ofKeyframe(PVH_RADIUS, Keyframe.ofFloat(0, currentRadius)
                            , Keyframe.ofFloat(tickFraction, currentRadius), Keyframe.ofFloat(1, endRadius))
                    , PropertyValuesHolder.ofKeyframe(PVH_TICK, Keyframe.ofFloat(0, startDTick)
                            , Keyframe.ofFloat(tickFraction, stopDTick)
                            , Keyframe.ofFloat(1, stopDTick)));
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
        mValueAnimator.setDuration(500);

        final ValueAnimator.AnimatorUpdateListener animatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Object objLeft = animation.getAnimatedValue(PVH_LEFT);
                Object objTop = animation.getAnimatedValue(PVH_TOP);
                Object objRadius = animation.getAnimatedValue(PVH_RADIUS);
                Object objAlpha = animation.getAnimatedValue(PVH_ALPHA);
                Object objTick = animation.getAnimatedValue(PVH_TICK);
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

                //Log.d(AnimCheckBox.TAG, "mCurrentRadius: " + mCurrentRadius + ", mCurrentAlpha: " + mCurrentAlpha + ", mCurrentLeft: " + mCurrentLeft + ", mCurrentTop: " + mCurrentTop + ", this: " + this);
                Log.d(AnimCheckBox.TAG, "objTick: " + mTickLength);
                mPathTick.reset();
                mPathMeasure.getSegment(0, mTickLength, mPathTick, true);

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
