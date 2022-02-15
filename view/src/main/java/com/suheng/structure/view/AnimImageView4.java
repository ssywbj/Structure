package com.suheng.structure.view;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class AnimImageView4 extends AppCompatImageView {
    private final EaseCubicInterpolator mFirstPhaseInterpolator = new EaseCubicInterpolator(0.33f, 0, 0.66f, 1);
    private final EaseCubicInterpolator mSecondPhaseInterpolator = new EaseCubicInterpolator(0.33f, 0, 0, 1);
    public static final int FIRST_PHASE_ANIM_DURATION = 1000;
    public static final int COMPLETE_ANIM_DURATION = 2500;
    public static final float END_SCALE = 1.58f;
    public static final float START_SCALE = 1f;
    private final RectF mRectF = new RectF();
    private final Path mPath = new Path();
    private ValueAnimator mValueAnimator;
    private Paint mPaint;
    private int mSelectedColor;
    private Bitmap mBitmapSrc;
    private final Xfermode mXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
    private boolean mSelected;
    private int mAlpha;

    public AnimImageView4(Context context) {
        super(context);
        this.init();
    }

    public AnimImageView4(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public AnimImageView4(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setDither(true);
        mPaint.setFilterBitmap(true);

        Drawable drawable = getDrawable();
        Log.d("Wbj", "init: " + drawable);
        if (drawable == null) {
            return;
        }
        mBitmapSrc = BitmapHelper.drawableToBitmap(drawable);
        //Bitmap bitmapSrc = null;
        Log.d("Wbj", "init: " + mBitmapSrc);
        if (mBitmapSrc == null) {
            return;
        }
        mRectF.set(0, 0, mBitmapSrc.getWidth(), mBitmapSrc.getHeight());

        /*if (drawable instanceof BitmapDrawable) {
            ColorMatrix colorMatrix = new ColorMatrix();
            colorMatrix.setSaturation(0.2f); //0~1，0为全灰，1为原色
            ColorMatrixColorFilter matrixColorFilter = new ColorMatrixColorFilter(colorMatrix);
            paint.setColorFilter(matrixColorFilter);
            //paint.setColorFilter(ColorFilterView.mColorFilter1);
            canvas.drawBitmap(bitmapSrc, 0, 0, paint);
        } else {*/
        ColorStateList imageTintList = getImageTintList();
        if (imageTintList == null) {
            mSelectedColor = Color.BLACK;
        } else {
            mSelectedColor = imageTintList.getColorForState(new int[]{android.R.attr.state_empty}, Color.BLACK);
        }
        //int color = Color.RED;
        Log.d("Wbj", "init: " + mSelectedColor);
        //}

        if (mValueAnimator == null) {
            mValueAnimator = ValueAnimator.ofFloat(0, Math.max(mRectF.width(), mRectF.height()) / 2f);
            //mValueAnimator.setDuration(2000);
            //mValueAnimator.setInterpolator(new LinearInterpolator());
            mValueAnimator.setDuration(FIRST_PHASE_ANIM_DURATION);
            mValueAnimator.setInterpolator(mFirstPhaseInterpolator);
            mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    Object object = animation.getAnimatedValue();
                    if ((object instanceof Float)) {
                        float radius = (float) object;
                        //Log.d("Wbj", "onAnimationUpdate: " + radius);
                        mPath.reset();
                        mPath.addCircle(mRectF.centerX(), mRectF.centerY(), radius, Path.Direction.CCW);

                        invalidate();
                    }
                }
            });
        }
    }

    /*@Override
    public void onVisibilityAggregated(boolean isVisible) {
        super.onVisibilityAggregated(isVisible);
        if (mValueAnimator != null) {
            if (isVisible) {
                if (mValueAnimator.isPaused()) {
                    mValueAnimator.resume();
                }
            } else {
                if (mValueAnimator.isRunning()) {
                    mValueAnimator.pause();
                }
            }
        }
    }*/

    /*@Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mValueAnimator != null) {
            mValueAnimator.cancel();
        }
    }*/

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBitmapSrc == null) {
            return;
        }
        Log.i("Wbj", "onDraw: " + canvas + ", " + mSelected);

        int saveLayer;
        if (mSelected) {
            canvas.clipPath(mPath);
            saveLayer = canvas.saveLayer(mRectF, null, Canvas.ALL_SAVE_FLAG);
        } else {
            saveLayer = canvas.saveLayerAlpha(mRectF, mAlpha, Canvas.ALL_SAVE_FLAG);
        }
        canvas.drawColor(mSelectedColor);
        mPaint.setXfermode(mXfermode);
        canvas.drawBitmap(mBitmapSrc, null, mRectF, mPaint);
        mPaint.setXfermode(null);
        canvas.restoreToCount(saveLayer);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        Log.d("Wbj", "setSelected: " + selected);
        mSelected = selected;
        if (selected) {
            if (mBitmapSrc == null) {
                this.init();
            }

            if (mBitmapSrc == null) {
                return;
            }

            if (mValueAnimator != null) {
                if (mValueAnimator.isRunning()) {
                    mValueAnimator.cancel();
                }
                mValueAnimator.start();
            }

            /*ValueAnimator maskAnim = ValueAnimator.ofFloat(0, Math.max(mRectF.width(), mRectF.height()) / 2f);
            maskAnim.setDuration(FIRST_PHASE_ANIM_DURATION);
            maskAnim.setInterpolator(mFirstPhaseInterpolator);
            maskAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    Object object = animation.getAnimatedValue();
                    if ((object instanceof Float)) {
                        float radius = (float) object;
                        //Log.d("Wbj", "onAnimationUpdate: " + radius);
                        mPath.reset();
                        mPath.addCircle(mRectF.centerX(), mRectF.centerY(), radius, Path.Direction.CCW);

                        invalidate();
                    }
                }
            });
            maskAnim.start();*/

            ValueAnimator.AnimatorUpdateListener animatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    Object value = animation.getAnimatedValue();
                    if (value instanceof Float) {
                        float scale = (float) value;
                        setScaleX(scale);
                        setScaleY(scale);
                    }
                }
            };

            ValueAnimator firstPhaseAnim = ValueAnimator.ofFloat(START_SCALE, END_SCALE);
            firstPhaseAnim.setDuration(FIRST_PHASE_ANIM_DURATION);
            firstPhaseAnim.setInterpolator(mFirstPhaseInterpolator);
            firstPhaseAnim.addUpdateListener(animatorUpdateListener);

            ValueAnimator secondPhaseAnim = ValueAnimator.ofFloat(END_SCALE, START_SCALE);
            secondPhaseAnim.setDuration(COMPLETE_ANIM_DURATION - FIRST_PHASE_ANIM_DURATION);
            secondPhaseAnim.setInterpolator(mSecondPhaseInterpolator);
            secondPhaseAnim.addUpdateListener(animatorUpdateListener);

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(secondPhaseAnim).after(firstPhaseAnim);
            animatorSet.start();
        } else {
            ValueAnimator a = ValueAnimator.ofInt(255, 0);
            a.setDuration(FIRST_PHASE_ANIM_DURATION);
            a.setInterpolator(mFirstPhaseInterpolator);
            a.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    Object object = animation.getAnimatedValue();
                    if ((object instanceof Integer) && !mRectF.isEmpty()) {
                        mAlpha = (int) object;
                        Log.d("Wbj", "onAnimationUpdate, mAlpha: " + mAlpha);
                        invalidate();
                    }
                }
            });
            a.start();
        }

    }

}
