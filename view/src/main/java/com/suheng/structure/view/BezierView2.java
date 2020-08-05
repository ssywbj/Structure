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

import androidx.annotation.Nullable;

public class BezierView2 extends View {
    private Paint paint;
    private Path mPath;
    private int mItemWidth = 600;

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
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLUE);
        paint.setAlpha(128);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);

        mPath = new Path();

        mAnimator = ValueAnimator.ofInt(0, mItemWidth);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mOffsetX = (int) animation.getAnimatedValue();
                invalidate();
            }
        });

        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.setDuration(1400);
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPath.reset();
        int halfItem = mItemWidth / 2;
        mPath.moveTo(-mItemWidth + mOffsetX, halfItem);
        for (int i = -mItemWidth; i < mItemWidth + getWidth(); i += mItemWidth) {
            mPath.rQuadTo(halfItem / 2f, -100, halfItem, 0);
            mPath.rQuadTo(halfItem / 2f, 100, halfItem, 0);
        }

        //闭合路径波浪以下区域
        /*mPath.lineTo(getWidth(), getHeight());
        mPath.lineTo(0, getHeight());
        mPath.close();*/

        canvas.drawPath(mPath, paint);
    }

}
