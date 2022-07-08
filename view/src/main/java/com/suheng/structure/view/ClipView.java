package com.suheng.structure.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class ClipView extends View {

    public ClipView(Context context) {
        super(context);
    }

    public ClipView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ClipView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void draw(Canvas canvas) {
        float radius = getWidth() / 2f;
        Matrix matrix = new Matrix();
        Path path = new Path();
        path.moveTo(getWidth() / 2f, -radius + getHeight() / 2f);

        float ratio = 0.91f;
        float[] src = {radius * ratio, -radius, radius, -radius * ratio, radius, 0};
        float[] dst = {0, 0, 0, 0, 0, 0};
        matrix.postTranslate(getWidth() / 2f, getHeight() / 2f);
        matrix.mapPoints(dst, src);
        path.cubicTo(dst[0], dst[1], dst[2], dst[3], dst[4], dst[5]);

        for (int i = 0; i < 3; i++) {
            matrix.preRotate(90);
            matrix.mapPoints(dst, src);
            path.cubicTo(dst[0], dst[1], dst[2], dst[3], dst[4], dst[5]);
        }

        canvas.clipPath(path);

        super.draw(canvas);
    }

    /*@Override
    public void draw(Canvas canvas) {
        Path path = new Path();
        RectF rectF = new RectF(0, 0, getWidth(), getHeight());
        //path.addRoundRect(rectF, 20, 20, Path.Direction.CCW);
        //path.addRoundRect(rectF, getWidth() / 2f, getWidth() / 2f, Path.Direction.CCW);

        //path.addCircle(getWidth() / 2f, getHeight() / 2f, getWidth() / 2f, Path.Direction.CCW);

        //path = this.hexagonPath(getWidth() / 2f);
        path = this.clothoidPath(getWidth() / 2f);

        canvas.clipPath(path);

        super.draw(canvas);
    }*/

    //正六边形
    private Path hexagonPath(float radius) {
        float[] point1 = new float[2];
        Matrix matrix = new Matrix();
        matrix.preTranslate(radius, 0);
        matrix.mapPoints(point1); //以原点为中心进行变换

        Path path = new Path();

        final int lines = 6, len = lines - 1;
        final float degrees = 360f / lines;

        //加“getWidth()/2f”和“getHeight()/2f”是为了得到以点(getWidth()/2f, getHeight()/2)为中心的六个点坐标
        path.moveTo(point1[0] + getWidth() / 2f, point1[1] + getHeight() / 2f); //第一个顶点的坐标

        float[] point = new float[2]; //用于保存其它顶点坐标
        matrix.reset();
        for (int i = 0; i < len; i++) {
            matrix.postRotate(degrees);
            matrix.mapPoints(point, point1); //以原点为中心点，变换后得到各个顶点的坐标

            path.lineTo(point[0] + getWidth() / 2f, point[1] + getHeight() / 2f);
        }
        path.close();
        return path;
    }

    //类羊角螺线圆角矩形
    private Path clothoidPath(float radius) {
        Matrix matrix = new Matrix();
        Path path = new Path();
        path.moveTo(getWidth() / 2f, -radius + getHeight() / 2f);

        float ratio = 0.91f; //控制四个角的弧度比例
        float[] src = {radius * ratio, -radius, radius, -radius * ratio, radius, 0};
        float[] dst = {0, 0, 0, 0, 0, 0};
        matrix.postTranslate(getWidth() / 2f, getHeight() / 2f);
        matrix.mapPoints(dst, src);
        path.cubicTo(dst[0], dst[1], dst[2], dst[3], dst[4], dst[5]);

        for (int i = 0; i < 3; i++) {
            matrix.preRotate(90);
            matrix.mapPoints(dst, src);
            path.cubicTo(dst[0], dst[1], dst[2], dst[3], dst[4], dst[5]);
        }

        return path;
    }

    //类羊角螺线圆角矩形
    private Path clothoidPath2(float radius) {
        Matrix matrix = new Matrix();
        Path path = new Path();
        path.moveTo(getWidth() / 2f, -radius + getHeight() / 2f);

        float ratio = 0.91f;
        float[] src = {radius * ratio, -radius, radius, -radius * ratio, radius, 0};
        float[] dst = {0, 0, 0, 0, 0, 0};
        matrix.postTranslate(getWidth() / 2f, getHeight() / 2f);
        matrix.mapPoints(dst, src);
        path.cubicTo(dst[0], dst[1], dst[2], dst[3], dst[4], dst[5]);

        for (int i = 1; i < 4; i++) {
            matrix.reset();
            matrix.preRotate(i * 90);
            matrix.postTranslate(getWidth() / 2f, getHeight() / 2f);
            matrix.mapPoints(dst, src);

            path.cubicTo(dst[0], dst[1], dst[2], dst[3], dst[4], dst[5]);
        }

        return path;
    }

}
