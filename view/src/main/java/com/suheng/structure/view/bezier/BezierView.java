package com.suheng.structure.view.bezier;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class BezierView extends View {
    public static final String TAG = BezierView.class.getSimpleName();
    private Paint mPaint;

    private Path mBezierPath;
    private Path mPointPath;

    public static PointF sStartPoint = new PointF(10, 200);
    public static PointF sControlPoint = new PointF(100, 50);
    public static PointF sEndPoint = new PointF(200, 300);

    public static PointF sPoint1 = new PointF(400, 200);
    public static PointF sPoint2 = new PointF(500, 50);
    public static PointF sPoint3 = new PointF(600, 50);
    public static PointF sPoint4 = new PointF(700, 350);

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
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(ContextCompat.getColor(getContext(), android.R.color.holo_blue_light));

        //两条相交直线
        mPointPath.moveTo(sStartPoint.x, sStartPoint.y); //起始点
        mPointPath.lineTo(sControlPoint.x, sControlPoint.y); //控制点
        mPointPath.lineTo(sEndPoint.x, sEndPoint.y); //终点
        mPaint.setColor(Color.BLUE);
        canvas.drawPath(mPointPath, mPaint);

        //二阶贝塞尔曲线
        mBezierPath.moveTo(sStartPoint.x, sStartPoint.y); //起始点
        mBezierPath.quadTo(sControlPoint.x, sControlPoint.y, sEndPoint.x, sEndPoint.y);//传入控制点和终点
        //加一根二阶贝塞尔曲线：rQuadTo, 控制点(dx1, dy1)和终点(dx2, dy2)距离上一个终点的距离，以下等价于quadTo(200, 400, 400, 200)，
        //即(200+0, 300+100, 200+200, 300-100)。
        mBezierPath.rQuadTo(0, 100, 200, -100);
        mPaint.setColor(Color.RED);
        canvas.drawPath(mBezierPath, mPaint);

        //三条相交直线
        mPointPath.moveTo(sPoint1.x, sPoint1.y); //起始点
        mPointPath.lineTo(sPoint2.x, sPoint2.y); //控制点
        mPointPath.lineTo(sPoint3.x, sPoint3.y); //控制点
        mPointPath.lineTo(sPoint4.x, sPoint4.y); //终点
        mPaint.setColor(Color.BLUE);
        canvas.drawPath(mPointPath, mPaint);

        //三阶贝塞尔曲线
        mBezierPath.moveTo(sPoint1.x, sPoint1.y); //起始点
        //mBezierPath.cubicTo(500, 50, 600, 50, 700, 350);//传入两控制点和终点，等价于rCubicTo(100, -150, 200, -150, 300, 150)
        mBezierPath.rCubicTo(sPoint2.x - sPoint1.x, sPoint2.y - sPoint1.y, sPoint3.x - sPoint1.x
                , sPoint3.y - sPoint1.y, sPoint4.x - sPoint1.x, sPoint4.y - sPoint1.y);
        mPaint.setColor(Color.RED);
        canvas.drawPath(mBezierPath, mPaint);
    }

}
