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

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class InfiniteLine3 extends View {
    public static final int SECONDS_SCALE = 60;
    public static final int PER_SCREEN_SECONDS = 5;
    private Paint mPaint, mPaintText;
    private final Rect mRectText = new Rect();
    private float mTextWidth, mTextOffset;
    private int mPageIndex;
    private ValueAnimator mTranslateAnim;
    private int mAnimValue;
    private int mStartSecond;

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
        mPaintText.setTextSize(60f);

        String text = "00";
        mPaintText.getTextBounds(text, 0, text.length(), mRectText);
        //mTextWidth = (mRectText.right - mRectText.left);
        mTextWidth = 2 * Math.abs(mRectText.centerX());
        mStartSecond = Calendar.getInstance().get(Calendar.SECOND);

        this.initAnim();
    }

    private void initAnim() {
        mTranslateAnim = ValueAnimator.ofInt(0, 0);
        mTranslateAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAnimValue = (int) animation.getAnimatedValue();
                Log.d("Wbj", "onAnimationUpdate: " + mAnimValue);
                if (mTranslateAnim != null) {
                    invalidate();
                }
            }
        });
        mTranslateAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mPageIndex++;
                if (mTranslateAnim != null) {
                    mTranslateAnim.setIntValues(mPageIndex * getWidth(), (mPageIndex + 1) * getWidth());
                    mTranslateAnim.start();
                }
            }
        });
        mTranslateAnim.setDuration(TimeUnit.SECONDS.toMillis(PER_SCREEN_SECONDS));
        mTranslateAnim.setInterpolator(new LinearInterpolator());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d("Wbj", "w: " + w + ", h: " + h + ", oldw: " + oldw + ", oldh: " + oldh);
        mTextOffset = (w - PER_SCREEN_SECONDS * mTextWidth) / PER_SCREEN_SECONDS;
        if (!mTranslateAnim.isRunning()) {
            mTranslateAnim.setIntValues(mPageIndex * getWidth(), (mPageIndex + 1) * getWidth());
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

        canvas.translate(0, dy);
        this.drawText(canvas, mPageIndex);
        this.drawText(canvas, mPageIndex + 1);
    }

    private void drawText(Canvas canvas, int pageIndex) {
        canvas.save();
        canvas.translate(getWidth() / 2f + pageIndex * getWidth(), 0);
        float x = -getWidth() / 2f;
        int number;
        int start = pageIndex * PER_SCREEN_SECONDS + mStartSecond, end = PER_SCREEN_SECONDS * (pageIndex + 1) + mStartSecond;
        for (int index = start - 2; index < end; index++) {
            number = (index + SECONDS_SCALE) % SECONDS_SCALE;
            canvas.drawText(number / 10 + "" + number % 10, x - mAnimValue, -mRectText.centerY(), mPaintText);
            x += (mTextOffset + mTextWidth);
        }
        canvas.restore();
    }

}
