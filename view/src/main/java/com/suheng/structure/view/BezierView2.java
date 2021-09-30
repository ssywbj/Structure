package com.suheng.structure.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class BezierView2 extends View {
    private Paint mPaint;
    private Path mPath;
    private final int mWaveWidth = 600;
    private final int mHalfWaveWidth = mWaveWidth / 2;

    private ValueAnimator mAnimator;
    private int mOffsetX;

    public BezierView2(Context context) {
        this(context, null);
    }

    public BezierView2(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BezierView2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setBackgroundColor(Color.RED);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLUE);
        mPaint.setAlpha(128);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(5);

        mPath = new Path();

        mAnimator = ValueAnimator.ofInt(0, mWaveWidth);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mOffsetX = (int) animation.getAnimatedValue();
                invalidate();
            }
        });

        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.setDuration(1500);
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.start();
    }

    //https://www.jianshu.com/p/12fcc3fedbbc
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPath.reset();
        int waveHeight = 100;

        mPath.moveTo(-mOffsetX, getHeight() / 2f); //左移
        //mPath.moveTo(-mWaveWidth + mOffsetX, getHeight() / 2f); //右移
        for (int i = -mWaveWidth; i < mWaveWidth + getWidth(); i += mWaveWidth) { //控制波浪的数量，至少两个，用于周期显示
            mPath.rQuadTo(mHalfWaveWidth / 2f, -waveHeight, mHalfWaveWidth, 0);
            mPath.rQuadTo(mHalfWaveWidth / 2f, waveHeight, mHalfWaveWidth, 0);
        }

        mPath.lineTo(getWidth(), getHeight());
        mPath.lineTo(0, getHeight());
        mPath.close();

        canvas.drawPath(mPath, mPaint);
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE) {
            if (mAnimator.isPaused()) {
                mAnimator.resume();
            }
        } else {
            if (mAnimator.isRunning()) {
                mAnimator.pause();
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mAnimator.isRunning()) {
            mAnimator.cancel();
        }
    }


}
