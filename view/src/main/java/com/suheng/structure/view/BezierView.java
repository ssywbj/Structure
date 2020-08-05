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

        //连接线
        mPointPath.moveTo(mStartPoint.x, mStartPoint.y);
        mPointPath.lineTo(mControlPoint.x, mControlPoint.y);
        mPointPath.lineTo(mEndPoint.x, mEndPoint.y);
        //绘制起始点、控制点、终点的连线
        mPaint.setColor(Color.BLUE);
        canvas.drawPath(mPointPath, mPaint);

        //贝塞尔
        mBezierPath.moveTo(mStartPoint.x, mStartPoint.y);
        mBezierPath.quadTo(mControlPoint.x, mControlPoint.y, mEndPoint.x, mEndPoint.y);//两点坐标
        //mBezierPath.rQuadTo(200, 300, 400, -200);//两点距离起始点的距离
        //绘制贝塞尔
        mPaint.setColor(Color.RED);
        canvas.drawPath(mBezierPath, mPaint);
    }

}
