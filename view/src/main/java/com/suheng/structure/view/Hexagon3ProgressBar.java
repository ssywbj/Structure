package com.suheng.structure.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

public class Hexagon3ProgressBar extends View {
    public static final String TAG = "Wbj";
    private Paint mPaint;
    private PathMeasure mPathMeasure;
    private Path mPathDst;//用于存储PathMeasure截取片断
    private ValueAnimator mValueAnimator;
    private float mStartDst, mStopDst;

    public Hexagon3ProgressBar(Context context) {
        super(context);
        this.initView();
    }

    public Hexagon3ProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.initView();
    }

    private void initView() {
        mPaint = this.getPaint();

        Path path = new Path();
        path.addCircle(200f, 200f, 100f, Path.Direction.CW);
        mPathMeasure = new PathMeasure(path, false);
        final float circleLength = mPathMeasure.getLength();
        Log.d(TAG, "circle length = " + circleLength);
        mPathDst = new Path();

        mValueAnimator = ValueAnimator.ofFloat(0, 1);//属性动画
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {//监听动画过程
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (animation.getAnimatedValue() instanceof Float) {
                    float animatedValue = (Float) animation.getAnimatedValue();
                    mStopDst = circleLength * animatedValue;
                    double proportion = 0.5 - Math.abs(animatedValue - 0.5);
                    mStartDst = (float) (mStopDst - proportion * circleLength);
                    Log.d(TAG, "animated value = " + animatedValue + ", proportion = " + proportion
                            + ", start dst = " + mStartDst + ", stop dst = " + mStopDst);
                    invalidate();//UI刷新
                }
            }
        });
        mValueAnimator.setDuration(1500);
        mValueAnimator.setRepeatCount(ValueAnimator.INFINITE);//无限循环
        mValueAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.GREEN);

        canvas.save();
        mPathDst.reset();
        mPathDst.lineTo(0, 0);//避免使用硬件加速产生的bug
        //mPathMeasure.getSegment(0, mStopDst, mPathDst, true);
        mPathMeasure.getSegment(mStartDst, mStopDst, mPathDst, true);
        canvas.drawPath(mPathDst, mPaint);//绘制截取的片段
        canvas.restore();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mValueAnimator.cancel();
        mValueAnimator = null;
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
