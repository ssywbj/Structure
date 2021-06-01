package com.wiz.watchface.svendandersen.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.structure.wallpaper.basic.view.FaceAnimView;
import com.wiz.watchface.svendandersen.R;

public class PanelView extends FaceAnimView {
    protected final RectF mRectF = new RectF();
    private Bitmap mBgTexture;
    private float mRotateDegrees;

    public PanelView(Context context) {
        super(context);
    }

    public PanelView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PanelView);
        Drawable drawable = typedArray.getDrawable(R.styleable.PanelView_backgroundTexture);
        typedArray.recycle();

        if (drawable != null) {
            mBgTexture = ((BitmapDrawable) drawable).getBitmap();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.calcDimens(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.calcDimens(w, h);
    }

    @Override
    protected void onAppearAnimFinished() {
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBgTexture != null) {
            canvas.save();
            canvas.rotate(mRotateDegrees, mRectF.centerX(), mRectF.centerY());
            canvas.drawBitmap(mBgTexture, null, mRectF, null);
            canvas.restore();
        }
    }

    private void calcDimens(int width, int height) {
        RectF rectF = new RectF();
        rectF.left = getPaddingStart();
        rectF.top = getPaddingTop();
        rectF.right = width - getPaddingEnd();
        rectF.bottom = height - getPaddingBottom();

        float radius = Math.min(rectF.width(), rectF.height()) / 2;
        mRectF.setEmpty();
        mRectF.left = rectF.centerX() - radius;
        mRectF.top = rectF.centerY() - radius;
        mRectF.right = rectF.centerX() + radius;
        mRectF.bottom = rectF.centerY() + radius;
    }

    public void setBgTexture(Bitmap bgTexture) {
        mBgTexture = bgTexture;
    }

    public void setRotateDegrees(float rotateDegrees) {
        mRotateDegrees = rotateDegrees;
    }
}
