package com.suheng.structure.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.Arrays;

public class Hexagon4ProgressBar extends View {
    public static final String TAG = Hexagon4ProgressBar.class.getSimpleName();
    private Paint mPaint;
    private final Rect mRect = new Rect(-300, -300, 300, 300);

    private final Path mPath = new Path();
    private Paint mPaintPath;
    private final int mLineLen = 240;

    public Hexagon4ProgressBar(Context context) {
        super(context);
        this.initView();
    }

    public Hexagon4ProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.initView();
    }

    private void initView() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);//空心，默认实心。
        mPaint.setStrokeWidth(8f);//画笔宽度
        mPaint.setColor(Color.BLACK);//画笔颜色

        mPaintPath = new Paint(mPaint);
        mPaintPath.setStrokeWidth(24f);
        mPaintPath.setPathEffect(new CornerPathEffect(4));
        //mPaintPath.setStrokeJoin(Paint.Join.ROUND);

        this.makePathByDegrees();
        mPath.set(this.makePathByMatrix());
    }

    private Path makePathByMatrix() {
        Path path = new Path();
        float[] src = {0, 0}; //原点
        float[] dst = new float[2]; //line1终点坐标
        Matrix matrix = new Matrix();
        matrix.postTranslate(mLineLen, 0);
        matrix.mapPoints(dst, src);
        float[] firstLine = {src[0], src[1], dst[0], dst[1]}; //line1坐标
        Log.d(TAG, "line1: " + Arrays.toString(firstLine));

        //path.moveTo(firstLine[0],firstLine[1]);
        path.moveTo(firstLine[2], firstLine[3]);

        matrix.reset();
        for (int i = 0; i < 5; i++) {
            dst = new float[4]; //用于保存线段坐标：起点及终点的X、Y轴坐标
            matrix.postRotate(60);
            matrix.mapPoints(dst, firstLine);
            Log.d(TAG, "line" + (i + 2) + ": " + Arrays.toString(dst));

            path.lineTo(dst[2], dst[3]);
        }
        path.close();

        return path;
    }

    private Path makePathByDegrees() {
        final double radians = Math.toRadians(60);
        float lenCos60 = (float) (mLineLen * Math.cos(radians));
        float lenSin60 = (float) (mLineLen * Math.sin(radians));
        Path path = new Path();
        path.moveTo(mLineLen, 0); //第一个顶点
        Log.i(TAG, "point1: " + mLineLen + ", " + 0);
        path.lineTo(lenCos60, lenSin60); //第二个顶点
        Log.i(TAG, "point2: " + lenCos60 + ", " + lenSin60);
        path.lineTo(-lenCos60, lenSin60); //第三个顶点
        Log.i(TAG, "point3: " + (-lenCos60) + ", " + lenSin60);
        path.lineTo(-mLineLen, 0); //第四个顶点
        Log.i(TAG, "point4: " + (-mLineLen) + ", " + 0);
        path.lineTo(lenCos60, -lenSin60); //第五个顶点
        Log.i(TAG, "point5: " + lenCos60 + ", " + (-lenSin60));
        path.lineTo(lenCos60, -lenSin60); //第六个顶点
        Log.i(TAG, "point6: " + lenCos60 + ", " + (-lenSin60));
        path.close();

        return path;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(ContextCompat.getColor(getContext(), android.R.color.holo_blue_light));
        this.drawRects(canvas);
        this.drawHexagon(canvas);
    }

    private void drawHexagon(Canvas canvas) {
        canvas.save();
        canvas.translate(getWidth() / 2f, (float) (getHeight() / 2f - 1.5 * mLineLen));
        canvas.drawPath(mPath, mPaintPath);
        canvas.restore();
    }

    private void drawRects(Canvas canvas) {
        canvas.save();
        canvas.translate(getWidth() / 2f, getHeight() / 2f + mLineLen);
        for (int i = 0; i < 25; i++) {
            canvas.scale(0.9f, 0.9f);
            canvas.drawRect(mRect, mPaint);
        }
        canvas.restore();
    }

}
