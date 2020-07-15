package com.suheng.structure.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class Hexagon4ProgressBar extends View {
    public static final String TAG = Hexagon4ProgressBar.class.getSimpleName();
    private Paint mPaint;
    private PathMeasure mPathMeasure;
    private Path mPath;
    private ValueAnimator mValueAnimator;
    private float mAnimatorValue = 0.1f;
    private float mCircleLength;
    private float[] mTan = new float[2];
    private PointF mCircleCenter = new PointF();
    private float mRadius = 120f;
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

        mPath = new Path();
        mCircleCenter.x = 1.0f * getWidth() / 2;
        mCircleCenter.y = 1.0f * getHeight() / 2;
        //mPath.addCircle(mCircleCenter.x, mCircleCenter.y, radius, Path.Direction.CW);
        mPath.addCircle(0, 0, mRadius, Path.Direction.CW);
        mPathMeasure = new PathMeasure(mPath, false);
        mCircleLength = mPathMeasure.getLength();
        Log.d(TAG, "circle length: " + mCircleLength + ", x: " + mCircleCenter.x + ", y: " + mCircleCenter.y);

        //mAnimatorValue = Math.abs(1.0f - mAnimatorValue);
        mAnimatorValue = 0.1f;
        mValueAnimator = ValueAnimator.ofFloat(0, 0.5f);//属性动画
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
        mValueAnimator.setDuration(600);
        //mValueAnimator.setRepeatCount(ValueAnimator.INFINITE);//无限循环
        mValueAnimator.setInterpolator(new LinearInterpolator());//线性（匀速）
        //mValueAnimator.setStartDelay(200);
        //mValueAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(ContextCompat.getColor(getContext(), android.R.color.holo_blue_light));

        canvas.save();
        mPathMeasure.getPosTan(mAnimatorValue * mCircleLength, null, mTan);
        canvas.translate(1.0f * getWidth() / 2, 1.0f * getHeight() / 2);
        canvas.drawPath(mPath, mPaint);
        //float degrees = (float) (Math.atan2(mTan[1], mTan[0]) * 180 / Math.PI);
        float degrees = 360 * mAnimatorValue;
        canvas.rotate(degrees);
        canvas.drawLine(0, 0, 0, -mRadius, mPaint);
        canvas.restore();
        Log.d(TAG, "animator value: " + mAnimatorValue + ", tan(" + mTan[0] + ", " + mTan[1] + ")"
                + ", degrees: " + degrees);

        if (mIsNotStartAnim) {//先保证用初始值画完一次界面再启动动画
            mIsNotStartAnim = false;
            mValueAnimator.start();
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
        paint.setStrokeWidth(8f);//画笔宽度
        paint.setColor(color);//画笔颜色
        return paint;
    }

    private Paint getPaint() {
        return this.getPaint(Color.BLUE);
    }
}
