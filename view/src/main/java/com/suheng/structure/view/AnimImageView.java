package com.suheng.structure.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

public class AnimImageView extends View {
    private Paint mPaint;
    private Bitmap mBitmapSrc;
    private final RectF mRectF = new RectF();
    private final Xfermode mXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
    private final Path mPath = new Path();

    public AnimImageView(Context context) {
        super(context);
        this.init();
    }

    public AnimImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public AnimImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setDither(true);
        mPaint.setFilterBitmap(true);

        mBitmapSrc = BitmapHelper.get(getContext(), R.drawable.vector_delete);
        mRectF.set(0, 0, mBitmapSrc.getWidth(), mBitmapSrc.getHeight());

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, Math.max(mRectF.width(), mRectF.height()) / 2f);
        valueAnimator.setDuration(2000);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Object object = animation.getAnimatedValue();
                if (object instanceof Float) {
                    float ratio = (float) object;
                    mPath.reset();
                    mPath.addCircle(mRectF.centerX(), mRectF.centerY(), ratio, Path.Direction.CCW);

                    invalidate();
                }
            }
        });
        valueAnimator.setStartDelay(100);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mBitmapSrc != null) {
            setMeasuredDimension(mBitmapSrc.getWidth(), mBitmapSrc.getHeight());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mBitmapSrc, null, mRectF, null);
        canvas.save();
        canvas.clipPath(mPath);
        int saveLayer = canvas.saveLayer(mRectF, null);
        canvas.drawColor(Color.RED);
        mPaint.setXfermode(mXfermode);
        canvas.drawBitmap(mBitmapSrc, null, mRectF, mPaint);
        mPaint.setXfermode(null);
        canvas.restoreToCount(saveLayer);
        canvas.restore();
    }

}
