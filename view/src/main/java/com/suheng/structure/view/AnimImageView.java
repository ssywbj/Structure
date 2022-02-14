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
    private Bitmap mBitmapSrc, mBitmapDst;
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
        float left = 1, top = left;
        mRectF.set(left, top, left + mBitmapSrc.getWidth(), top + mBitmapSrc.getHeight());

        mBitmapDst = Bitmap.createBitmap(mBitmapSrc.getWidth(), mBitmapSrc.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mBitmapDst);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setDither(true);
        paint.setFilterBitmap(true);
        paint.setColor(Color.RED);
        canvas.drawRect(mRectF, paint);

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, mRectF.height());
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
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mBitmapSrc, null, mRectF, null);
        /*int saveLayer = canvas.saveLayer(mRectFDst, mPaint);
        canvas.drawBitmap(mBitmapDst, null, mRectFDst, mPaint);
        mPaint.setXfermode(mXfermode);
        canvas.drawBitmap(mBitmapSrc, null, mRectF, mPaint);
        mPaint.setXfermode(null);
        canvas.restoreToCount(saveLayer);*/

        canvas.save();
        canvas.clipPath(mPath);
        int saveLayer = canvas.saveLayer(mRectF, null);
        canvas.drawBitmap(mBitmapDst, null, mRectF, mPaint);
        mPaint.setXfermode(mXfermode);
        canvas.drawBitmap(mBitmapSrc, null, mRectF, mPaint);
        mPaint.setXfermode(null);
        canvas.restoreToCount(saveLayer);
        canvas.restore();
    }

}
