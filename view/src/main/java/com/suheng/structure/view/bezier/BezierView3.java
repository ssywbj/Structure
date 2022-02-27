package com.suheng.structure.view.bezier;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class BezierView3 extends View {
    private Paint mPaint;
    private Path mPath;
    private int mWaveWidth = 600, mWaveHeight = 50;
    private int mHalfWaveWidth;

    private ValueAnimator mAnimator;
    private int mOffsetX;
    private String mText;

    public BezierView3(Context context) {
        this(context, null);
    }

    public BezierView3(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BezierView3(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //setBackgroundColor(Color.RED);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 60, getResources().getDisplayMetrics()));

        mPath = new Path();

        mHalfWaveWidth = mWaveWidth / 2;

        mAnimator = ValueAnimator.ofInt(0, mWaveWidth);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mOffsetX = (int) animation.getAnimatedValue();
                invalidate();
            }
        });

        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.setDuration(1000);
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.start();

        mText = "贴";
        mTextCentreY = (int) ((mPaint.descent() + mPaint.ascent()) / 2f);
    }

    private int mCentreX, mCentreY, mTextCentreY;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCentreX = w / 2;
        mCentreY = h / 2;
    }

    //https://www.jianshu.com/p/c8e70e045133
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Color.BLUE);
        canvas.drawText(mText, mCentreX, mCentreY - mTextCentreY, mPaint);

        mPath.reset();
        mPath.moveTo(-mOffsetX, mCentreY); //左移
        //mPath.moveTo(-mWaveWidth + mOffsetX, mTextCentreY); //右移
        for (int i = -mWaveWidth; i < mWaveWidth + getWidth(); i += mWaveWidth) { //控制波浪的数量，至少两个，用于周期显示
            mPath.rQuadTo(mHalfWaveWidth / 2f, -mWaveHeight, mHalfWaveWidth, 0);
            mPath.rQuadTo(mHalfWaveWidth / 2f, mWaveHeight, mHalfWaveWidth, 0);
        }

        mPath.lineTo(getWidth(), getHeight());
        mPath.lineTo(0, getHeight());
        mPath.close();

        canvas.clipPath(mPath);
        canvas.drawCircle(mCentreX, mCentreY, mCentreY, mPaint);

        mPaint.setColor(Color.WHITE);
        canvas.drawText(mText, mCentreX, mCentreY - mTextCentreY, mPaint);
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
