package com.suheng.structure.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BezierView1 extends View {
    public static final String TAG = BezierView1.class.getSimpleName();
    private Paint mPaint;

    private Path mBezierPath;
    private Path mPointPath;

    private final PointF mX0 = new PointF();
    private final PointF mX1 = new PointF();
    private final PointF mBezier = new PointF();

    private final List<PointF> mPointList = new ArrayList<>();
    private final List<PointF> mPointBeziers = new ArrayList<>();

    private final List<PointF> mPointList2 = new ArrayList<>();

    public BezierView1(Context context) {
        super(context);
        this.initView();
    }

    public BezierView1(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.initView();
    }

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

    private void initView() {
        setBackgroundColor(Color.GRAY);
        mPaint = this.getPaint();

        mBezierPath = new Path();
        mPointPath = new Path();

        mPointBeziers.add(BezierView.sStartPoint);
        mPointBeziers.add(BezierView.sControlPoint);
        mPointBeziers.add(BezierView.sEndPoint);

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(2000);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Object object = animation.getAnimatedValue();
                if (object instanceof Float) {
                    float u = (float) object;
                    float[] floats = bezierPoint(u, BezierView.sStartPoint.x, BezierView.sStartPoint.y, BezierView.sControlPoint.x, BezierView.sControlPoint.y);
                    mX0.x = floats[0];
                    mX0.y = floats[1];

                    floats = bezierPoint(u, BezierView.sControlPoint.x, BezierView.sControlPoint.y, BezierView.sEndPoint.x, BezierView.sEndPoint.y);
                    mX1.x = floats[0];
                    mX1.y = floats[1];
                    PointF pointF = bezierPoint(u, BezierView.sControlPoint, BezierView.sEndPoint);
                    mX1.x = pointF.x;
                    mX1.y = pointF.y;

                    floats = bezierPoint(u, mX0.x, mX0.y, mX1.x, mX1.y);
                    mBezier.x = floats[0];
                    mBezier.y = floats[1];
                    pointF = bezierPoint(u, mPointBeziers);
                    mBezier.x = pointF.x;
                    mBezier.y = pointF.y;

                    mPointList.add(new PointF(mBezier.x, mBezier.y));

                    pointF = bezierPoint(u, BezierView.sPoint1, BezierView.sPoint2, BezierView.sPoint3, BezierView.sPoint4);
                    mPointList2.add(pointF);

                    invalidate();
                }
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationRepeat(Animator animation) {
                super.onAnimationRepeat(animation);
                mPointList.clear();
                mPointList2.clear();
            }
        });
        valueAnimator.setStartDelay(100);
        //valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.start();
    }

    //https://juejin.cn/post/6844903760007790600
    //https://github.com/zincPower/UI2018/tree/dev_zinc
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Color.BLUE);
        mPaint.setStrokeWidth(6);
        canvas.drawPoint(BezierView.sStartPoint.x, BezierView.sStartPoint.y, mPaint);
        canvas.drawPoint(BezierView.sControlPoint.x, BezierView.sControlPoint.y, mPaint);
        canvas.drawPoint(BezierView.sEndPoint.x, BezierView.sEndPoint.y, mPaint);

        canvas.drawPoint(mX0.x, mX0.y, mPaint);
        canvas.drawPoint(mX1.x, mX1.y, mPaint);
        canvas.drawPoint(mBezier.x, mBezier.y, mPaint);

        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(4);
        for (PointF point : mPointList) {
            canvas.drawPoint(point.x, point.y, mPaint);
        }
        for (PointF point : mPointList2) {
            canvas.drawPoint(point.x, point.y, mPaint);
        }

    }

    public static float[] bezierPoint(float ratio, float x1, float y1, float x2, float y2) {
        float x0 = (1 - ratio) * x1 + ratio * x2;
        float y0 = (1 - ratio) * y1 + ratio * y2;
        return new float[]{x0, y0};
    }

    public static PointF bezierPoint(float ratio, PointF p1, PointF p2) {
        float[] point = bezierPoint(ratio, p1.x, p1.y, p2.x, p2.y);
        return new PointF(point[0], point[1]);
    }

    public static PointF bezierPoint(float ratio, List<PointF> points) {
        if (points == null || points.size() == 0) {
            return new PointF();
        }

        if (points.size() == 1) {
            return points.get(0);
        }

        List<PointF> temp = new ArrayList<>();
        int offset = 0, len = points.size();
        do {
            temp.add(bezierPoint(ratio, points.get(offset), points.get(offset + 1)));
            offset++;
        } while (offset + 1 < len);

        return bezierPoint(ratio, temp);
    }

    public static PointF bezierPoint(float ratio, PointF... point) {
        return bezierPoint(ratio, Arrays.asList(point));
    }

}
