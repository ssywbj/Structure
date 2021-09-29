package com.suheng.structure.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class BezierView extends View {
    public static final String TAG = BezierView.class.getSimpleName();
    private Paint mPaint;

    private Path mBezierPath;
    private Path mPointPath;

    private Point mStartPoint;
    private Point mControlPoint;
    private Point mEndPoint;

    public BezierView(Context context) {
        super(context);
        this.initView();
    }

    public BezierView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.initView();
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

    private void initView() {
        mPaint = this.getPaint();

        mBezierPath = new Path();
        mPointPath = new Path();

        mStartPoint = new Point();
        mStartPoint.set(10, 200);
        mControlPoint = new Point();
        mControlPoint.set(100, 50);
        mEndPoint = new Point();
        mEndPoint.set(200, 300);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(ContextCompat.getColor(getContext(), android.R.color.holo_blue_light));

        //两条相交直线
        mPointPath.moveTo(mStartPoint.x, mStartPoint.y); //起始点
        mPointPath.lineTo(mControlPoint.x, mControlPoint.y); //控制点
        mPointPath.lineTo(mEndPoint.x, mEndPoint.y); //终点
        mPaint.setColor(Color.BLUE);
        canvas.drawPath(mPointPath, mPaint);

        //二阶贝塞尔曲线
        mBezierPath.moveTo(mStartPoint.x, mStartPoint.y); //起始点
        mBezierPath.quadTo(mControlPoint.x, mControlPoint.y, mEndPoint.x, mEndPoint.y);//传入控制点和终点
        //加一根二阶贝塞尔曲线：rQuadTo, 控制点(dx1, dy1)和终点(dx2, dy2)距离上一个终点的距离，以下等价于quadTo(200, 400, 400, 200)，
        //即(200+0, 300+100, 200+200, 300-100)。
        mBezierPath.rQuadTo(0, 100, 200, -100);
        mPaint.setColor(Color.RED);
        canvas.drawPath(mBezierPath, mPaint);

        //三条相交直线
        mPointPath.moveTo(400, 200); //起始点
        mPointPath.lineTo(500, 50); //控制点
        mPointPath.lineTo(600, 50); //控制点
        mPointPath.lineTo(700, 350); //终点
        mPaint.setColor(Color.BLUE);
        canvas.drawPath(mPointPath, mPaint);

        //三阶贝塞尔曲线
        mBezierPath.moveTo(400, 200); //起始点
        //mBezierPath.cubicTo(500, 50, 600, 50, 700, 350);//传入两控制点和终点，等价于rCubicTo(100, -150, 200, -150, 300, 150)
        mBezierPath.rCubicTo(500 - 400, 50 - 200, 600 - 400, 50 - 200, 700 - 400, 350 - 200);
        mPaint.setColor(Color.RED);
        canvas.drawPath(mBezierPath, mPaint);
    }

}
