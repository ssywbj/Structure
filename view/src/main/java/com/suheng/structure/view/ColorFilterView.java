package com.suheng.structure.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
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

    //https://www.cnblogs.com/tianzhijiexian/p/4297104.html
    //https://blog.csdn.net/aigestudio/article/details/41316141
    private final ColorFilter mColorFilter = new ColorMatrixColorFilter(new float[]{
            1f, 0f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f, 0f,
            0f, 0f, 1f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f}); //原图

    public static final ColorFilter mColorFilter1 = new ColorMatrixColorFilter(new float[]{
            1.6f, 0f, 0f, 0f, 0f,
            0f, 1.6f, 0f, 0f, 0f,
            0f, 0f, 1.6f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f}); //缩放运算---乘法 -- 颜色增强

    public static final ColorFilter mColorFilter7 = new ColorMatrixColorFilter(new float[]{
            0.4f, 0f, 0f, 0f, 0f,
            0f, 0.4f, 0f, 0f, 0f,
            0f, 0f, 0.4f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f}); //缩放运算---乘法 -- 颜色变暗

    private final ColorFilter mColorFilter2 = new ColorMatrixColorFilter(new float[]{
            1f, 0f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f, 100f,
            0f, 0f, 1f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f}); //平移运算---加法

    private final ColorFilter mColorFilter3 = new ColorMatrixColorFilter(new float[]{
            1f, 0f, 0f, 0f, 0f,
            0f, 0f, 1f, 0f, 0f,
            0f, 1f, 0f, 0f, 0f,
            0f, 0f, 0f, 0.5F, 0f}); //发色效果---(比如红色和绿色交换)

    private final ColorFilter mColorFilter4 = new ColorMatrixColorFilter(new float[]{
            1 / 2f, 1 / 2f, 1 / 2f, 0f, 0f,
            1 / 3f, 1 / 3f, 1 / 3f, 0f, 0f,
            1 / 4f, 1 / 4f, 1 / 4f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f}); //复古

    private final ColorFilter mColorFilter5 = new ColorMatrixColorFilter(new float[]{
            0.213f, 0.715f, 0.072f, 0f, 0f,
            0.213f, 0.715f, 0.072f, 0f, 0f,
            0.213f, 0.715f, 0.072f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f}); //黑白

    private final ColorFilter mColorFilter6 = new ColorMatrixColorFilter(new float[]{
            -1f, 0f, 0f, 0f, 255f,
            0f, -1f, 0f, 0f, 255f,
            0f, 0f, -1f, 0f, 255f,
            0f, 0f, 0f, 1f, 0f}); //底片

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

        mPaint.setColorFilter(mColorFilter);
        //mPaint.setColorFilter(null);
        canvas.drawBitmap(mBitmap, null, mRectF, mPaint);

        canvas.save();
        mPaint.setColorFilter(mColorFilter1);
        canvas.translate(mRectF.width(), 0);
        canvas.drawBitmap(mBitmap, null, mRectF, mPaint);
        canvas.restore();

        canvas.save();
        mPaint.setColorFilter(mColorFilter2);
        canvas.translate(0, mRectF.height());
        canvas.drawBitmap(mBitmap, null, mRectF, mPaint);
        canvas.restore();

        canvas.save();
        mPaint.setColorFilter(mColorFilter3);
        canvas.translate(mRectF.width(), mRectF.height());
        canvas.drawBitmap(mBitmap, null, mRectF, mPaint);
        canvas.restore();

        canvas.save();
        mPaint.setColorFilter(mColorFilter4);
        canvas.translate(0, 2 * mRectF.height());
        canvas.drawBitmap(mBitmap, null, mRectF, mPaint);
        canvas.restore();

        canvas.save();
        mPaint.setColorFilter(mColorFilter5);
        canvas.translate(mRectF.width(), 2 * mRectF.height());
        canvas.drawBitmap(mBitmap, null, mRectF, mPaint);
        canvas.restore();

        canvas.save();
        mPaint.setColorFilter(mColorFilter6);
        canvas.translate(0, 3 * mRectF.height());
        canvas.drawBitmap(mBitmap, null, mRectF, mPaint);
        canvas.restore();

        canvas.save();
        mPaint.setColorFilter(mColorFilter7);
        canvas.translate(mRectF.width(), 3 * mRectF.height());
        canvas.drawBitmap(mBitmap, null, mRectF, mPaint);
        canvas.restore();
    }

}
