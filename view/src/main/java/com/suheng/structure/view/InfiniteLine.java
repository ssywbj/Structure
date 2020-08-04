package com.suheng.structure.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

public class InfiniteLine extends View {
    private Paint mPaint, mPaintText;
    private int mLineLen;

    private ValueAnimator mAnimator;
    private int mAnimValue;
    private ValueAnimator mAnimator2;
    private int mAnimValue2;
    private boolean mNotStartAnim = true;

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
        mPaintText.setTypeface(Typeface.DEFAULT_BOLD);
        mPaintText.setTextSize(60f);
        mPaintText.getTextBounds(text, 0, text.length(), mRectText);
        Log.d("Wbj", "text len: " + mRectText.right);

        mAnimator = ValueAnimator.ofInt(0, 0);
        final int duration = 5000;
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAnimValue = (int) animation.getAnimatedValue();
                Log.d("Wbj", "onAnimationUpdate: " + mAnimValue);
                if (mAnimValue >= mRectText.right) {
                    if (!mAnimator2.isRunning()) {
                        mAnimator2.start();
                        mAnimValue2 = 0;
                    }
                }
                invalidate();
            }
        });
        mAnimator.setDuration(duration);
        mAnimator.setInterpolator(new LinearInterpolator());

        mAnimator2 = ValueAnimator.ofInt(0, 0);
        mAnimator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAnimValue2 = (int) animation.getAnimatedValue();
                Log.d("Wbj", "mAnimValue2: " + mAnimValue2);
                if (!mAnimator.isRunning()) {
                    invalidate();
                }
                if (mAnimValue2 >= mRectText.right) {
                    if (!mAnimator.isRunning()) {
                        mAnimator.start();
                        mAnimValue = 0;
                    }
                }
            }
        });
        mAnimator2.setDuration(duration);
        mAnimator2.setInterpolator(new LinearInterpolator());
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mAnimator.isRunning()) {
            mAnimator.cancel();
        }
    }

    private Rect mRectText = new Rect();
    String text = " 00 01 02 03 04 05 06 07 08 09 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29";

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);

        canvas.save();
        canvas.translate(getWidth(), getHeight() / 2f);
        if (mAnimator.isRunning()) {
            canvas.drawText(text, -mAnimValue, 0, mPaintText);
        }
        if (mAnimator2.isRunning()) {
            canvas.drawText(text, -mAnimValue2, 0, mPaintText);
        }
        canvas.restore();

        mLineLen = getWidth();
        if (mLineLen != 0) {
            if (mNotStartAnim) {
                mNotStartAnim = false;
                mAnimator.setIntValues(0, mRectText.right + getWidth());
                mAnimator2.setIntValues(0, mRectText.right + getWidth());
                mAnimator.start();
            }
        }

    }

}
