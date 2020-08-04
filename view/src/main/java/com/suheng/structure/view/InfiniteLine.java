package com.suheng.structure.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

public class InfiniteLine extends View {
    private Paint mPaint, mPaintText;
    private Path mPath;
    private int mLineLen;

    private ValueAnimator mAnimator;
    private float mAnimValue;
    private boolean mNotStartAnim = true;
    private int mPeriod = 0;

    public InfiniteLine(Context context) {
        this(context, null);
    }

    public InfiniteLine(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InfiniteLine(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(4f);

        mPaintText = new Paint();
        mPaintText.setAntiAlias(true);
        mPaintText.setColor(Color.WHITE);
        mPaintText.setStyle(Paint.Style.FILL);
        mPaintText.setTextSize(40f);

        mPath = new Path();

        mAnimator = ValueAnimator.ofFloat(0, 0);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (animation.getAnimatedValue() instanceof Float) {
                    mAnimValue = (Float) animation.getAnimatedValue();
                    long currentPlayTime = animation.getCurrentPlayTime();
                    mPeriod = (int) (currentPlayTime / 1500);
                    Log.d("Wbj", "onAnimationUpdate: " + mAnimValue + ", repeat count: " + currentPlayTime);
                    invalidate();
                }
            }
        });

        mAnimator.setDuration(1500);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mAnimator.isRunning()) {
            mAnimator.cancel();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);

        /*mPath.reset();
        mPath.moveTo(0, getHeight() / 2f);
        mPath.lineTo(getWidth() - mOffsetX, getHeight() / 2f);
        mPath.lineTo(getWidth() - mOffsetX + getWidth(), getHeight() / 2f);
        canvas.drawPath(mPath, mPaint);*/

        //canvas.drawTextOnPath("0", mPath, 0, 0, mPaintText);
        canvas.drawText("" + mPeriod / 10 + mPeriod % 10, getWidth() - mAnimValue, getHeight() / 2f, mPaintText);

        mLineLen = getWidth();
        if (mLineLen != 0) {
            if (mNotStartAnim) {
                mNotStartAnim = false;
                mAnimator.setFloatValues(0, mLineLen);
                //mAnimator.start();
            }
        }
    }

}
