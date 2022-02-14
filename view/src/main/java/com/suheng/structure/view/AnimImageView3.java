package com.suheng.structure.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class AnimImageView3 extends AppCompatImageView {
    private Bitmap mBitmapDst;
    private final RectF mRectF = new RectF();
    private final Path mPath = new Path();
    private ValueAnimator mValueAnimator;

    public AnimImageView3(Context context) {
        super(context);
        this.init();
    }

    public AnimImageView3(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public AnimImageView3(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    private void init() {
        Drawable drawable = getDrawable();
        //Log.d("Wbj", "init: " + drawable);
        if (drawable == null) {
            return;
        }
        Bitmap bitmapSrc = BitmapHelper.drawableToBitmap(drawable);
        //Log.d("Wbj", "init: " + mBitmapSrc);
        if (bitmapSrc == null) {
            return;
        }
        mRectF.set(0, 0, bitmapSrc.getWidth(), bitmapSrc.getHeight());

        mBitmapDst = Bitmap.createBitmap(bitmapSrc);
        Canvas canvas = new Canvas(mBitmapDst);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setDither(true);
        paint.setFilterBitmap(true);
        if (drawable instanceof BitmapDrawable) {
            ColorMatrix colorMatrix = new ColorMatrix();
            colorMatrix.setSaturation(0.2f); //0~1，0为全灰，1为原色
            ColorMatrixColorFilter matrixColorFilter = new ColorMatrixColorFilter(colorMatrix);
            paint.setColorFilter(matrixColorFilter);
            //paint.setColorFilter(ColorFilterView.mColorFilter1);
            canvas.drawBitmap(bitmapSrc, 0, 0, paint);
        } else {
            paint.setColor(Color.RED);
            canvas.drawBitmap(bitmapSrc.extractAlpha(), 0, 0, paint);
        }

        mValueAnimator = ValueAnimator.ofFloat(0, Math.max(mRectF.width(), mRectF.height()) / 2f);
        //mValueAnimator = ValueAnimator.ofFloat(0, mRectF.width());
        mValueAnimator.setDuration(2000);
        mValueAnimator.setInterpolator(new LinearInterpolator());
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Object object = animation.getAnimatedValue();
                if ((object instanceof Float) && mBitmapDst != null) {
                    float radius = (float) object;
                    //Log.d("Wbj", "onAnimationUpdate: " + radius);
                    mPath.reset();
                    mPath.addCircle(mRectF.centerX(), mRectF.centerY(), radius, Path.Direction.CCW);

                    invalidate();
                }
            }
        });
        mValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mValueAnimator.start();
    }

    @Override
    public void onVisibilityAggregated(boolean isVisible) {
        super.onVisibilityAggregated(isVisible);
        if (mValueAnimator != null) {
            if (isVisible) {
                if (mValueAnimator.isPaused()) {
                    mValueAnimator.resume();
                }
            } else {
                if (mValueAnimator.isRunning()) {
                    mValueAnimator.pause();
                }
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mValueAnimator != null) {
            mValueAnimator.cancel();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBitmapDst == null) {
            return;
        }
        //Log.i("Wbj", "onDraw: " + canvas);

        canvas.save();
        canvas.clipPath(mPath);
        canvas.drawBitmap(mBitmapDst, null, mRectF, null);
        canvas.restore();
    }

}
