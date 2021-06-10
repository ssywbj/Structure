package com.suheng.damping.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class DampingProgressBar extends View {
    public static final String TAG = DampingProgressBar.class.getSimpleName();
    private Paint mPaintCircle;
    private final Path mPathCircle = new Path();

    private Paint mPaintProgress;

    private final PathMeasure mPathMeasure = new PathMeasure();
    private float mCircleLength;
    private final Path mPathDst = new Path();
    private ValueAnimator mValueAnimator;
    private float mStartDst, mStopDst = 10;

    public DampingProgressBar(Context context) {
        super(context);
        this.init();
    }

    public DampingProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    private void init() {
        mPaintProgress = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintProgress.setAntiAlias(true);
        mPaintProgress.setStyle(Paint.Style.STROKE);
        mPaintProgress.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2.5f, getResources().getDisplayMetrics()));
        mPaintProgress.setColor(ContextCompat.getColor(getContext(), android.R.color.darker_gray));
        mPaintProgress.setStrokeCap(Paint.Cap.ROUND);

        mPaintCircle = new Paint(mPaintProgress);
        mPaintCircle.setAlpha(50);

        mValueAnimator = ValueAnimator.ofFloat(0, 1);
        mValueAnimator.addUpdateListener(animation -> {
            if (animation.getAnimatedValue() instanceof Float) {
                float animatedValue = (Float) animation.getAnimatedValue();
                mStopDst = mCircleLength * animatedValue;
                double proportion = 0.5 - Math.abs(animatedValue - 0.5);
                mStartDst = (float) (mStopDst - proportion * mCircleLength);
                /*Log.d(TAG, "animated value = " + animatedValue + ", proportion = " + proportion
                        + ", start dst = " + mStartDst + ", stop dst = " + mStopDst);*/
                invalidate();
            }
        });
        mValueAnimator.setDuration(1200);
        mValueAnimator.setRepeatCount(ValueAnimator.INFINITE);

        //post(() -> mValueAnimator.start());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float diameter = Math.min(w, h) - mPaintCircle.getStrokeWidth();
        mPathCircle.reset();
        mPathCircle.addCircle(0, 0, diameter / 2f, Path.Direction.CW);

        mPathMeasure.setPath(mPathCircle, false);
        mCircleLength = mPathMeasure.getLength();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(getWidth() / 2f, getHeight() / 2f);
        canvas.rotate(-90);

        canvas.drawPath(mPathCircle, mPaintCircle);
        mPathDst.reset();
        mPathMeasure.getSegment(mStartDst, mStopDst, mPathDst, true);
        canvas.drawPath(mPathDst, mPaintProgress);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mValueAnimator.cancel();
        this.stop();
    }

    public void setProgressColor(int color) {
        mPaintProgress.setColor(color);
        mPaintCircle.set(mPaintProgress);
        mPaintCircle.setAlpha(50);
    }

    public void start() {
        if (mValueAnimator == null || mValueAnimator.isRunning()) {
            return;
        }

        mValueAnimator.start();
    }

    public void stop() {
        if (mValueAnimator != null && mValueAnimator.isRunning()) {
            mValueAnimator.cancel();
        }

        mStartDst = 0;
        mStopDst = 10;
        invalidate();
    }

}
