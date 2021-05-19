package com.suheng.structure.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class ColorFilterView extends View {
    private Bitmap mBitmap;
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final RectF mRectF = new RectF();

    public ColorFilterView(Context context) {
        this(context, null);
    }

    public ColorFilterView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorFilterView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    private void init() {
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.iz0rltfp);
        mPaint.setDither(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRectF.set(0, 0, w / 2f, h / 4f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setColorFilter(null);//原图
        canvas.drawBitmap(mBitmap, null, mRectF, mPaint);

        canvas.save();
        mPaint.setColorFilter(new ColorMatrixColorFilter(new float[]{
                1.2f, 0f, 0f, 0f, 0f,
                0f, 1.2f, 0f, 0f, 0f,
                0f, 0f, 1.2f, 0f, 0f,
                0f, 0f, 0f, 1.2f, 0f})); //缩放运算---乘法 -- 颜色增强
        canvas.translate(mRectF.width(), 0);
        canvas.drawBitmap(mBitmap, null, mRectF, mPaint);
        canvas.restore();

        canvas.save();
        mPaint.setColorFilter(new ColorMatrixColorFilter(new float[]{
                1f, 0f, 0f, 0f, 0f,
                0f, 1f, 0f, 0f, 100f,
                0f, 0f, 1f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f})); //平移运算---加法
        canvas.translate(0, mRectF.height());
        canvas.drawBitmap(mBitmap, null, mRectF, mPaint);
        canvas.restore();

        canvas.save();
        mPaint.setColorFilter(new ColorMatrixColorFilter(new float[]{
                1f, 0f, 0f, 0f, 0f,
                0f, 0f, 1f, 0f, 0f,
                0f, 1f, 0f, 0f, 0f,
                0f, 0f, 0f, 0.5F, 0f})); //发色效果---(比如红色和绿色交换)
        canvas.translate(mRectF.width(), mRectF.height());
        canvas.drawBitmap(mBitmap, null, mRectF, mPaint);
        canvas.restore();

        canvas.save();
        mPaint.setColorFilter(new ColorMatrixColorFilter(new float[]{
                1 / 2f, 1 / 2f, 1 / 2f, 0f, 0f,
                1 / 3f, 1 / 3f, 1 / 3f, 0f, 0f,
                1 / 4f, 1 / 4f, 1 / 4f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f})); //复古
        canvas.translate(0, 2 * mRectF.height());
        canvas.drawBitmap(mBitmap, null, mRectF, mPaint);
        canvas.restore();

        canvas.save();
        mPaint.setColorFilter(new ColorMatrixColorFilter(new float[]{
                0.213f, 0.715f, 0.072f, 0f, 0f,
                0.213f, 0.715f, 0.072f, 0f, 0f,
                0.213f, 0.715f, 0.072f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f})); //黑白
        canvas.translate(mRectF.width(), 2 * mRectF.height());
        canvas.drawBitmap(mBitmap, null, mRectF, mPaint);
        canvas.restore();

        canvas.save();
        mPaint.setColorFilter(new ColorMatrixColorFilter(new float[]{
                -1f, 0f, 0f, 0f, 255f,
                0f, -1f, 0f, 0f, 255f,
                0f, 0f, -1f, 0f, 255f,
                0f, 0f, 0f, 1f, 0f})); //底片
        canvas.translate(0, 3 * mRectF.height());
        canvas.drawBitmap(mBitmap, null, mRectF, mPaint);
        canvas.restore();
    }

}
