package com.suheng.structure.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class Hexagon1ProgressBar extends View {
    public static final String TAG = "Wbj";
    private Paint mPaint;
    private PathMeasure mPathMeasure;
    private Path mPath;
    private ValueAnimator mValueAnimator;
    private float mAnimatorValue;
    private float mCircleLength;

    private final Matrix mMatrix = new Matrix();
    private Bitmap mBitmap;

    public Hexagon1ProgressBar(Context context) {
        super(context);
        this.initView();
    }

    public Hexagon1ProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.initView();
    }

    private void initView() {
        mPaint = this.getPaint();

        mPath = new Path();
        float radius = 150f;
        mPath.addCircle(0, 0, radius, Path.Direction.CW);
        mPathMeasure = new PathMeasure(mPath, false);
        mCircleLength = mPathMeasure.getLength();
        Log.d(TAG, "circle length = " + mCircleLength);

        mBitmap = BitmapFactory.decodeResource(getResources(), android.R.drawable.arrow_down_float);

        mValueAnimator = ValueAnimator.ofFloat(0, 1);//属性动画
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {//监听动画过程
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (animation.getAnimatedValue() instanceof Float) {
                    mAnimatorValue = (Float) animation.getAnimatedValue();
                    invalidate();//UI刷新
                }
            }
        });
        mValueAnimator.setDuration(1800);
        mValueAnimator.setRepeatCount(ValueAnimator.INFINITE);//无限循环
        mValueAnimator.setInterpolator(new LinearInterpolator());//线性（匀速）
        mValueAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(ContextCompat.getColor(getContext(), android.R.color.holo_blue_light));

        canvas.save();
        //得到矩阵
        mPathMeasure.getMatrix(mCircleLength * mAnimatorValue, mMatrix
                , PathMeasure.POSITION_MATRIX_FLAG | PathMeasure.TANGENT_MATRIX_FLAG);
        canvas.translate(1.0f * getWidth() / 2, 1.0f * getHeight() / 2);//将坐标系移动到控件的中心位置
        canvas.drawPath(mPath, mPaint);
        mMatrix.preRotate(270);//调整箭头的朝向为正切线的方向
        mMatrix.preTranslate(-1.0f * mBitmap.getWidth() / 2, -1.0f * mBitmap.getHeight() / 2);//箭头的中轴线移动到圆的轨道上
        canvas.drawBitmap(mBitmap, mMatrix, null);
        canvas.restore();
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
        paint.setStrokeWidth(6f);//画笔宽度
        paint.setColor(color);//画笔颜色
        return paint;
    }

    private Paint getPaint() {
        return this.getPaint(Color.BLUE);
    }
}
