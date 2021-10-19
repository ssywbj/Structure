package com.suheng.structure.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

public class BezierView11 extends View {
    public static final String TAG = BezierView11.class.getSimpleName();
    private Paint mPaint;
    private final Path mPath = new Path();
    private final Matrix mMatrix = new Matrix();
    private float mRatio, mRadius;

    public BezierView11(Context context) {
        super(context);
        this.initView();
    }

    public BezierView11(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.initView();
    }

    private void initView() {
        setBackgroundColor(Color.GRAY);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(4f);

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1, 0);
        valueAnimator.setDuration(2000);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Object object = animation.getAnimatedValue();
                if (object instanceof Float) {
                    mRatio = (float) object;

                    invalidate();
                }
            }
        });
        valueAnimator.setStartDelay(100);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.start();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRadius = (w > h ? h / 2f : w / 2f) - mPaint.getStrokeWidth() / 2f;
    }

    //https://juejin.cn/post/6844903760007790600
    //https://github.com/zincPower/UI2018/tree/dev_zinc
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(getWidth() / 2f, getHeight() / 2f);

        //对比二阶和三阶贝塞尔曲线的对比：start
        mMatrix.reset();
        mPath.reset();
        mPath.moveTo(0, -mRadius);
        float[] src = {mRadius, -mRadius, mRadius, 0};
        float[] dst = {0, 0, 0, 0};
        mPath.quadTo(src[0], src[1], src[2], src[3]);
        for (int i = 0; i < 3; i++) {
            mMatrix.preRotate(90);
            mMatrix.mapPoints(dst, src);
            //二阶传一个控件点(dst[0], dst[1])
            mPath.quadTo(dst[0], dst[1], dst[2], dst[3]);
        }
        mPaint.setColor(Color.GREEN);
        canvas.drawPath(mPath, mPaint);

        mMatrix.reset();
        mPath.reset();
        mPath.moveTo(0, -mRadius);
        mPath.cubicTo(src[0], src[1], src[0], src[1], src[2], src[3]);
        for (int i = 0; i < 3; i++) {
            mMatrix.preRotate(90);
            mMatrix.mapPoints(dst, src);
            //三阶传两个控件点(dst[0], dst[1])和(dst[0], dst[1])
            mPath.cubicTo(dst[0], dst[1], dst[0], dst[1], dst[2], dst[3]);
        }
        mPaint.setColor(Color.RED);
        canvas.drawPath(mPath, mPaint);
        //对比：从绘制出的曲线来看，虽然三阶贝塞尔传的两个控制点都是一样的，都为(dst[0], dst[1])，但它并没有转为二阶贝塞尔曲线。
        //结论：虽然控制点都一样，但不同阶的贝塞尔曲线还是不一样的（后面持续验证）
        //对比二阶和三阶贝塞尔曲线的对比：end

        mMatrix.reset();
        mPath.reset();
        mPath.moveTo(0, -mRadius);
        float[] src2 = {mRadius * mRatio, -mRadius, mRadius, -mRadius * mRatio, mRadius, 0};
        float[] dst2 = {0, 0, 0, 0, 0, 0};
        mPath.cubicTo(src2[0], src2[1], src2[2], src2[3], src2[4], src2[5]);
        for (int i = 0; i < 3; i++) {
            mMatrix.preRotate(90);
            mMatrix.mapPoints(dst2, src2);
            mPath.cubicTo(dst2[0], dst2[1], dst2[2], dst2[3], dst2[4], dst2[5]);
        }
        mPaint.setColor(Color.BLUE);
        canvas.drawPath(mPath, mPaint);

        mPaint.setColor(Color.WHITE);
        canvas.drawCircle(0, 0, mRadius, mPaint);
        mPaint.setColor(Color.BLACK);
        canvas.drawRect(-mRadius, -mRadius, mRadius, mRadius, mPaint);

        canvas.restore();
    }

}
