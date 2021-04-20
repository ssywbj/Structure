package com.suheng.structure.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class Hexagon4ProgressBar extends View {
    public static final String TAG = Hexagon4ProgressBar.class.getSimpleName();
    private Paint mPaint;

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
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(ContextCompat.getColor(getContext(), android.R.color.holo_blue_light));
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
