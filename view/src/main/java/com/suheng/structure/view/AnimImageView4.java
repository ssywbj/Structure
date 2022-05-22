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
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.PathInterpolator;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class AnimImageView4 extends AppCompatImageView {
    private final PathInterpolator mFirstPhaseInterpolator = new PathInterpolator(0.33f, 0, 0.66f, 1);
    private final PathInterpolator mSecondPhaseInterpolator = new PathInterpolator(0.33f, 0, 0, 1);
    public static final int FIRST_PHASE_ANIM_DURATION = 1250;
    public static final int COMPLETE_ANIM_DURATION = 1700;
    public static final float END_SCALE = 1.08f;
    public static final float START_SCALE = 1f;
    private final RectF mRectF = new RectF();
    private final Path mPath = new Path();
    private ValueAnimator mMaskAnimator, mAlphaAnimator;
    private AnimatorSet mPhaseAnimator;
    private Paint mPaint;
    private Bitmap mBitmapSrc;
    private boolean mSelected;
    private int mAlpha;
    private boolean mIsSelectedAnimRunning;

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

        this.initMaskAnimator();
        this.initAlphaAnimator();
        this.initPhaseAnimator();
    }

    private void initMaskAnimator() {
        mMaskAnimator = ValueAnimator.ofFloat(0, 0);
        mMaskAnimator.setDuration(FIRST_PHASE_ANIM_DURATION);
        mMaskAnimator.setInterpolator(mFirstPhaseInterpolator);
        mMaskAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
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

    private void initAlphaAnimator() {
        mAlphaAnimator = ValueAnimator.ofInt(255, 0);
        mAlphaAnimator.setDuration(FIRST_PHASE_ANIM_DURATION);
        mAlphaAnimator.setInterpolator(mFirstPhaseInterpolator);
        mAlphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Object object = animation.getAnimatedValue();
                if ((object instanceof Integer) && !mRectF.isEmpty()) {
                    mAlpha = (int) object;
                    //Log.d("Wbj", "onAnimationUpdate, mAlpha: " + mAlpha);
                    invalidate();
                }
            }
        });
        mAlphaAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
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
        firstPhaseAnim.setInterpolator(mFirstPhaseInterpolator);
        firstPhaseAnim.addUpdateListener(animatorUpdateListener);
        ValueAnimator secondPhaseAnim = ValueAnimator.ofFloat(END_SCALE, START_SCALE);
        secondPhaseAnim.setDuration(COMPLETE_ANIM_DURATION - FIRST_PHASE_ANIM_DURATION);
        secondPhaseAnim.setInterpolator(mSecondPhaseInterpolator);
        secondPhaseAnim.addUpdateListener(animatorUpdateListener);

        mPhaseAnimator = new AnimatorSet();
        mPhaseAnimator.play(secondPhaseAnim).after(firstPhaseAnim);
        mPhaseAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mIsSelectedAnimRunning = true;
                setSelected(false);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mIsSelectedAnimRunning = false;
                setSelected(true);
                mPath.reset();
                invalidate();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                mIsSelectedAnimRunning = false;
                mPath.reset();
                invalidate();
            }
        });
    }

    private void getSourceImage() {
        //https://www.jianshu.com/p/a936b158ed49
        //https://stackoverflow.com/questions/65828719/how-to-use-pixelcopy-api-in-android-java-to-get-bitmap-from-view
        //https://juejin.cn/post/6931267559383629838
        setDrawingCacheEnabled(true);
        mBitmapSrc = getDrawingCache();
        Log.d("Wbj", "src bitmap: " + mBitmapSrc);
        int width = getWidth();
        int height = getHeight();
        if (mBitmapSrc == null || width <= 0 || height <= 0) {
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
        //color = Color.RED;
        mPaint.setColor(selectedColor);

        if (mMaskAnimator == null) {
            this.initMaskAnimator();
        }
        mMaskAnimator.setFloatValues(Math.max(mRectF.width(), mRectF.height()) / 2f);
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
        setDrawingCacheEnabled(false); //会自动回收getDrawingCache()对应的bitmap
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBitmapSrc == null) {
            return;
        }
        Log.i("Wbj", "onDraw: " + canvas + ", " + mSelected + ", " + mAlpha);

        if (mSelected) {
            canvas.save();
            canvas.clipPath(mPath);
            canvas.drawBitmap(mBitmapSrc.extractAlpha(), null, mRectF, mPaint);
            canvas.restore();
        } else {
            int saveLayer = canvas.saveLayerAlpha(mRectF, mAlpha, Canvas.ALL_SAVE_FLAG);
            canvas.drawBitmap(mBitmapSrc.extractAlpha(), null, mRectF, mPaint);
            canvas.restoreToCount(saveLayer);
        }
    }

    public void setSelectedAnim(boolean selected) {
        if (mBitmapSrc == null) {
            this.getSourceImage();
        }
        if (mBitmapSrc == null) {
            return;
        }

        if (mMaskAnimator != null && mMaskAnimator.isRunning()) {
            return;
        }
        if (mAlphaAnimator != null && mAlphaAnimator.isRunning()) {
            return;
        }
        if (mPhaseAnimator != null && mPhaseAnimator.isRunning()) {
            return;
        }

        Log.d("Wbj", "setSelected: " + selected);
        mSelected = selected;

        if (selected) {
            if (mMaskAnimator == null) {
                this.initMaskAnimator();
            }
            mMaskAnimator.start();
            if (mPhaseAnimator == null) {
                this.initPhaseAnimator();
            }
            mPhaseAnimator.start();
        } else {
            if (mAlphaAnimator == null) {
                this.initAlphaAnimator();
            }
            mAlphaAnimator.start();
        }
    }

    public boolean isSelectedAnimRunning() {
        return mIsSelectedAnimRunning;
    }
}
