package com.suheng.structure.view.bezier;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class BezierView4 extends View {
    private Paint mPaint;
    private Path mPath;
    private int mWaveWidth = 600, mWaveHeight = 50;
    private int mHalfWaveWidth;

    private ValueAnimator mAnimator;
    private int mOffsetX;
    private String mText;

    public BezierView4(Context context) {
        this(context, null);
    }

    public BezierView4(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BezierView4(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setDither(true);
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

        if (mBitmapCircle != null) {
            if (!mBitmapCircle.isRecycled()) {
                mBitmapCircle.recycle();
            }

            mBitmapCircle = null;
        }

        mPaint.setColor(Color.BLUE);
        mBitmapCircle = this.createCircleBitmap(mCentreY);
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

        int radius = mCentreY;
        int saveLayer = canvas.saveLayer(mCentreX - radius, mCentreY - radius
                , mCentreX + radius, mCentreY + radius, null, Canvas.ALL_SAVE_FLAG);

        canvas.drawPath(mPath, mPaint);

        mPaint.setXfermode(mXfermodeCircle);
        canvas.drawBitmap(mBitmapCircle, mCentreX - radius, mCentreY - radius, mPaint);
        //canvas.drawCircle(mCentreX, mCentreY, radius, mPaint);

        mPaint.setColor(Color.WHITE);
        mPaint.setXfermode(mXfermodeText);
        canvas.drawText(mText, mCentreX, mCentreY - mTextCentreY, mPaint);

        mPaint.setXfermode(null);
        canvas.restoreToCount(saveLayer);
    }

    private final PorterDuffXfermode mXfermodeCircle = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
    private final PorterDuffXfermode mXfermodeText = new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP);
    private Bitmap mBitmapCircle;

    private Bitmap createCircleBitmap(int radius) {
        Bitmap bitmap = Bitmap.createBitmap(radius * 2, radius * 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawCircle(radius, radius, radius, mPaint);
        return bitmap;
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
