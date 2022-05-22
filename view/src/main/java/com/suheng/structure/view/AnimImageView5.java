package com.suheng.structure.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.animation.PathInterpolator;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

/**
 * 带有遮罩层效果的ImageView
 */
public class AnimImageView5 extends AppCompatImageView {
    /*private static final int FIRST_PHASE_ANIM_DURATION = 1300;
    private static final int COMPLETE_ANIM_DURATION = 2700;
    private static final float END_SCALE = 1.35f;*/
    private static final int FIRST_PHASE_ANIM_DURATION = 130;
    private static final int COMPLETE_ANIM_DURATION = 700;
    private static final float END_SCALE = 1.15f;
    private static final float START_SCALE = 1f;
    private final RectF mRectF = new RectF();
    private final Path mPath = new Path();
    private ValueAnimator mMaskAnimator, mAlphaAnimator;
    private AnimatorSet mPhaseAnimator;
    private Paint mPaint;
    private Bitmap mBitmapSrc, mBitmapDst;
    private boolean mIsSelected, mIsCancelPhaseAnimator;
    private int mAlpha;
    private AnimatorListenerAdapter mAnimatorListenerAdapter;

    public AnimImageView5(Context context) {
        super(context);
        this.init();
    }

    public AnimImageView5(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public AnimImageView5(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setDither(true);
        mPaint.setFilterBitmap(true);
    }

    private void initMaskAnimator() {
        mMaskAnimator = ValueAnimator.ofFloat(0, 0);
        mMaskAnimator.setDuration(FIRST_PHASE_ANIM_DURATION);
        mMaskAnimator.setInterpolator(new PathInterpolator(0.01f, 0, 0.1f, 1));
        mMaskAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Object object = animation.getAnimatedValue();
                if ((object instanceof Float)) {
                    float radius = (float) object;
                    mPath.reset();
                    mPath.addCircle(mRectF.centerX(), mRectF.centerY(), radius, Path.Direction.CCW);
                    //Log.i("Wbj", "mMaskAnimator, onAnimationUpdate: " + radius + ", " + this);

                    invalidate();
                }
            }
        });
    }

    private void initAlphaAnimator() {
        mAlphaAnimator = ValueAnimator.ofInt(255, 0);
        mAlphaAnimator.setDuration(FIRST_PHASE_ANIM_DURATION);
        mAlphaAnimator.setInterpolator(new PathInterpolator(0.01f, 0, 0.1f, 1));
        mAlphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Object object = animation.getAnimatedValue();
                if ((object instanceof Integer) && !mRectF.isEmpty()) {
                    mAlpha = (int) object;
                    invalidate();
                    //Log.d("Wbj", "mAlphaAnimator, mAlpha: " + mAlpha + ", " + this);
                }
            }
        });
        mAlphaAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mIsSelected = false;
                setSelected(false);
            }
        });
    }

    private void initPhaseAnimator() {
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
        firstPhaseAnim.setInterpolator(new PathInterpolator(0.01f, 0, 0.1f, 1));
        firstPhaseAnim.addUpdateListener(animatorUpdateListener);
        ValueAnimator secondPhaseAnim = ValueAnimator.ofFloat(END_SCALE, START_SCALE);
        secondPhaseAnim.setDuration(COMPLETE_ANIM_DURATION - FIRST_PHASE_ANIM_DURATION);
        secondPhaseAnim.setInterpolator(new PathInterpolator(0.33f, 0, 0, 1));
        secondPhaseAnim.addUpdateListener(animatorUpdateListener);

        mPhaseAnimator = new AnimatorSet();
        mPhaseAnimator.play(secondPhaseAnim).after(firstPhaseAnim);
        mPhaseAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                setSelected(false);
                mIsCancelPhaseAnimator = false;
                mIsSelected = true;

                if (mAnimatorListenerAdapter != null) {
                    mAnimatorListenerAdapter.onAnimationStart(animation);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                //Log.d("Wbj", "onAnimationEnd: " + (!mIsCancelPhaseAnimator) + ", " + this);
                setSelected(!mIsCancelPhaseAnimator);
                setScaleX(START_SCALE);
                setScaleY(START_SCALE);

                if (mAnimatorListenerAdapter != null) {
                    mAnimatorListenerAdapter.onAnimationEnd(animation);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                //Log.d("Wbj", "onAnimationCancel: " + this);
                mIsCancelPhaseAnimator = true;
                mIsSelected = false;
                mPath.reset();
                invalidate();

                if (mAnimatorListenerAdapter != null) {
                    mAnimatorListenerAdapter.onAnimationCancel(animation);
                }
            }
        });
    }

    private void getSourceImage() {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }
        mBitmapSrc = BitmapHelper.drawableToBitmap(drawable);
        int width = getWidth();
        int height = getHeight();
        if (mBitmapSrc == null || width <= 0 || height <= 0 || mBitmapSrc.getWidth() <= 0 || mBitmapSrc.getHeight() <= 0) {
            return;
        }
        mRectF.set(0, 0, width, height);

        ColorStateList imageTintList = getImageTintList();
        int selectedColor;
        if (imageTintList == null) {
            selectedColor = Color.BLUE;
        } else {
            selectedColor = imageTintList.getColorForState(new int[]{android.R.attr.state_selected}, Color.GREEN);
        }
        //selectedColor = Color.RED;
        mBitmapDst = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mBitmapDst);
        float sx = 1.0f * width / mBitmapSrc.getWidth();
        canvas.scale(sx, sx);
        Paint paint = new Paint(mPaint);
        /*if (drawable instanceof BitmapDrawable) {
            ColorMatrix colorMatrix = new ColorMatrix();
            colorMatrix.setSaturation(0.2f); //0~1，0为全灰，1为原色
            ColorMatrixColorFilter matrixColorFilter = new ColorMatrixColorFilter(colorMatrix);
            paint.setColorFilter(matrixColorFilter);
            //paint.setColorFilter(ColorFilterView.mColorFilter1);
            canvas.drawBitmap(bitmapSrc, 0, 0, paint);
        } else {*/
        paint.setColor(selectedColor);
        canvas.drawBitmap(mBitmapSrc.extractAlpha(), 0, 0, paint);
        //}

        if (mMaskAnimator == null) {
            this.initMaskAnimator();
        }
        mMaskAnimator.setFloatValues(0, Math.max(mRectF.width(), mRectF.height()) / 2f);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mMaskAnimator != null) {
            mMaskAnimator.cancel();
        }
        if (mPhaseAnimator != null) {
            mPhaseAnimator.cancel();
        }
        if (mAlphaAnimator != null) {
            mAlphaAnimator.cancel();
        }
        if (mBitmapSrc != null && !mBitmapSrc.isRecycled()) {
            mBitmapSrc.recycle();
        }
        if (mBitmapDst != null && !mBitmapDst.isRecycled()) {
            mBitmapDst.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBitmapSrc == null) {
            return;
        }

        if (mIsSelected) {
            canvas.save();
            canvas.clipPath(mPath);
            canvas.drawBitmap(mBitmapDst, null, mRectF, mPaint);
            canvas.restore();
        } else {
            int saveLayer = canvas.saveLayerAlpha(mRectF, mAlpha, Canvas.ALL_SAVE_FLAG);
            canvas.drawBitmap(mBitmapDst, null, mRectF, mPaint);
            canvas.restoreToCount(saveLayer);
        }
    }

    public void setSelectedAnim(boolean selected, AnimatorListenerAdapter animatorListenerAdapter) {
        if (mBitmapSrc == null) {
            this.getSourceImage();
        }
        if (mBitmapSrc == null) {
            return;
        }

        mAnimatorListenerAdapter = animatorListenerAdapter;

        if (selected) {
            if (mMaskAnimator != null && mMaskAnimator.isRunning()) {
                return;
            }

            if (mPhaseAnimator != null && mPhaseAnimator.isRunning()) {
                return;
            }

            if (mMaskAnimator == null) {
                this.initMaskAnimator();
            }
            mMaskAnimator.end();
            mMaskAnimator.start();

            if (mPhaseAnimator == null) {
                this.initPhaseAnimator();
            }
            for (Animator childAnimation : mPhaseAnimator.getChildAnimations()) {
                childAnimation.end();
            }
            mPhaseAnimator.start();
        } else {
            if (mAlphaAnimator != null && mAlphaAnimator.isRunning()) {
                return;
            }

            if (mAlphaAnimator == null) {
                this.initAlphaAnimator();
            }
            mAlphaAnimator.end();
            mAlphaAnimator.start();
        }
    }

    public void setSelectedAnim(boolean selected) {
        this.setSelectedAnim(selected, null);
    }

    public boolean isSelectedAnimRunning() {
        return (mPhaseAnimator != null && mPhaseAnimator.isRunning());
    }

    public void cancelSelectedAnimRunning() {
        if (mPhaseAnimator != null) {
            mPhaseAnimator.cancel();
        }
    }
}
