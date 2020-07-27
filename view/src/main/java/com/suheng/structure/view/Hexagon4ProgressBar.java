package com.suheng.structure.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class Hexagon4ProgressBar extends View {
    public static final String TAG = Hexagon4ProgressBar.class.getSimpleName();
    private Paint mPaint;
    private ValueAnimator mValueAnimator, mScaleAnimator;
    private float mAnimatorValue = 0.0f, mScaleValue = 1.2f;
    private boolean mIsNotStartAnim = true;

    public Hexagon4ProgressBar(Context context) {
        super(context);
        this.initView();
    }

    public Hexagon4ProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.initView();
    }

    private void initView() {
        mPaint = this.getPaint();

        Log.d(TAG, "x: " + 1.0f * getWidth() / 2 + ", y: " + 1.0f * getHeight() / 2);

        mValueAnimator = ValueAnimator.ofFloat(mAnimatorValue, 1f);//属性动画
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {//监听动画过程
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (animation.getAnimatedValue() instanceof Float) {
                    mAnimatorValue = (Float) animation.getAnimatedValue();
                    Log.d(TAG, "animator value: " + mAnimatorValue);
                    //invalidate();//UI刷新
                }
            }
        });
        mValueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                Log.d(TAG, "----anim finish-----");
            }
        });
        int duration = 800;
        mValueAnimator.setDuration(duration);
        mValueAnimator.setInterpolator(new LinearInterpolator());//线性（匀速）

        mScaleAnimator = ValueAnimator.ofFloat(mScaleValue, 1f);//属性动画
        mScaleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {//监听动画过程
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (animation.getAnimatedValue() instanceof Float) {
                    mScaleValue = (Float) animation.getAnimatedValue();
                    Log.d(TAG, "scale value: " + mScaleValue);
                    invalidate();//UI刷新
                }
            }
        });
        mScaleAnimator.setDuration(duration);
        mScaleAnimator.setInterpolator(new BounceInterpolator());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(ContextCompat.getColor(getContext(), android.R.color.holo_blue_light));

        canvas.translate(1.0f * getWidth() / 2, 1.0f * getHeight() / 2);
        canvas.scale(mScaleValue, mScaleValue);

        canvas.save();
        canvas.rotate(360 * mAnimatorValue);
        float radius = 170f;
        canvas.drawCircle(0, 0, radius, mPaint);
        canvas.drawLine(0, 0, 0, -radius, mPaint);
        canvas.restore();

        if (mIsNotStartAnim) {//先保证用初始值画完一次界面再启动动画
            mIsNotStartAnim = false;
            int startDelay = 100;
            mValueAnimator.setStartDelay(startDelay);
            mValueAnimator.start();

            mScaleAnimator.setStartDelay(startDelay);
            mScaleAnimator.start();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mValueAnimator != null) {
            mValueAnimator.cancel();
            mValueAnimator = null;
        }
    }

    /**
     * 获取特定颜色的画笔
     *
     * @param color The new color (including alpha) to set in the paint.
     * @return Paint
     */
    private Paint getPaint(int color) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);//抗(不显示)锯齿，让绘出来的物体更清晰
        paint.setStyle(Paint.Style.STROKE);//空心，默认实心。
        paint.setStrokeWidth(4f);//画笔宽度
        paint.setColor(color);//画笔颜色
        return paint;
    }

    private Paint getPaint() {
        return this.getPaint(Color.BLUE);
    }
}
