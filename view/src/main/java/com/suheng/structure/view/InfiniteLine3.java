package com.suheng.structure.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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

public class InfiniteLine3 extends View {
    public static final int PER_SCREEN_SECONDS = 5;
    private Paint mPaint, mPaintText;
    private Rect mRectText = new Rect();
    private float mTextWidth, mTextOffset;
    private int mPageIndex;

    private ValueAnimator mTranslateAnim;
    private float mAnimValue;

    public InfiniteLine3(Context context) {
        this(context, null);
    }

    public InfiniteLine3(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InfiniteLine3(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(2f);

        mPaintText = new Paint();
        mPaintText.setAntiAlias(true);
        mPaintText.setColor(Color.WHITE);
        mPaint.setStrokeWidth(1f);
        mPaintText.setStyle(Paint.Style.FILL);
        mPaintText.setTypeface(Typeface.DEFAULT_BOLD);
        mPaintText.setTextSize(120f);

        String text = "00";
        mPaintText.getTextBounds(text, 0, text.length(), mRectText);
        //mTextWidth = (mRectText.right - mRectText.left);
        mTextWidth = 2 * Math.abs(mRectText.centerX());

        this.initAnim();
        //mTranslateAnim.setRepeatCount(ValueAnimator.INFINITE);
    }

    private void initAnim() {
        mTranslateAnim = ValueAnimator.ofFloat(0, 0);
        mTranslateAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAnimValue = (float) animation.getAnimatedValue();
                Log.d("Wbj", "onAnimationUpdate: " + mAnimValue);
                invalidate();
            }
        });
        mTranslateAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mPageIndex++;
                mTranslateAnim.setFloatValues(mPageIndex * (getWidth() /*+  mTextOffset*/), (mPageIndex + 1) * (getWidth() /*+ mTextOffset*/));
                mTranslateAnim.start();
            }
        });
        mTranslateAnim.setDuration(5000);
        mTranslateAnim.setInterpolator(new LinearInterpolator());
        //mTranslateAnim.setRepeatCount(ValueAnimator.INFINITE);
        //mTranslateAnim.setRepeatCount(ValueAnimator.RESTART);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d("Wbj", "w: " + w + ", h: " + h + ", oldw: " + oldw + ", oldh: " + oldh);
        mTextOffset = (w - PER_SCREEN_SECONDS * mTextWidth) / PER_SCREEN_SECONDS;
        if (!mTranslateAnim.isRunning()) {
            mTranslateAnim.setFloatValues(mPageIndex * getWidth(), (mPageIndex + 1) * (getWidth() /*+ mTextOffset*/));
            mTranslateAnim.start();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mTranslateAnim.cancel();
        Log.d("Wbj", "onDetachedFromWindow, anim running: " + mTranslateAnim.isRunning());
        mTranslateAnim = null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);
        float dx = getWidth() / 2f;
        float dy = getHeight() / 2f;
        canvas.save();
        canvas.translate(dx, dy);
        canvas.drawLine(0, -dy, 0, dy, mPaint);
        canvas.drawLine(-dx, 0, dx, 0, mPaint);
        canvas.restore();
        //canvas.drawText("00", -mTextWidth / 2f, -mRectText.centerY(), mPaintText);
        /*float x = -mTextWidth / 2f;
        for (int index = 0; index < 3; index++) {
            canvas.drawText(index / 10 + "" + index % 10, x, -mRectText.centerY(), mPaintText);
            x += (mTextOffset + mTextWidth);
        }
        x = -mTextWidth / 2f;
        x -= (mTextOffset + mTextWidth);
        canvas.drawText("59", x, -mRectText.centerY(), mPaintText);
        x -= (mTextOffset + mTextWidth);
        canvas.drawText("58", x, -mRectText.centerY(), mPaintText);
        ;*/

        canvas.translate(0, dy);
        this.drawText(canvas, mPageIndex);
        this.drawText(canvas, mPageIndex + 1);
    }

    private void drawText(Canvas canvas, int pageIndex) {
        canvas.save();
        canvas.translate(pageIndex * getWidth(), 0);
        float x = 0;
        int number;
        for (int index = pageIndex * PER_SCREEN_SECONDS; index < (pageIndex + 1) * PER_SCREEN_SECONDS; index++) {
            number = index % 14;
            canvas.drawText(number / 10 + "" + number % 10, x - mAnimValue, -mRectText.centerY(), mPaintText);
            x += (mTextOffset + mTextWidth);
        }
        canvas.restore();
    }


}
