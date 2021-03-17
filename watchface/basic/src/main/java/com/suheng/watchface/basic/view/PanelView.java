package com.suheng.watchface.basic.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

public class PanelView extends FaceAnimView {
    private Paint mPaint;
    public PanelView(Context context) {
        super(context);
        this.init();
    }

    public PanelView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    private void init() {
        setAppearAnimPointer(true);
        setAppearAnimNumber(true);
        //setDefaultTime(8, 36, 55);
        //setDefaultTime(8, 36, TIME_NONE);
        setDefaultTime(TIME_NONE, 1, 55);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.BLACK);
        mPaint.setTextSize(55);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(getWidth() / 2f, getHeight() / 2f);
        canvas.drawText(String.valueOf(mSecond), 0, 0, mPaint);
    }

    @Override
    protected void onAppearAnimFinished() {
        super.onAppearAnimFinished();
        updateTimePerSecond();
    }
}
