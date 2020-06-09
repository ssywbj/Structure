package com.suheng.structure.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class VectorView extends View {
    private PointF mPointCenter = new PointF();
    private Bitmap mBitmap;
    private Paint mPaint = new Paint();
    private Matrix mMatrix = new Matrix();

    public VectorView(Context context) {
        super(context);
        this.init();
    }

    public VectorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public VectorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    private void init() {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        mPointCenter.x = 1.0f * displayMetrics.widthPixels / 2;
        mPointCenter.y = 1.0f * displayMetrics.heightPixels / 2;

        mBitmap = BitmapManager.get(getContext(), R.drawable.number_5, android.R.color.white);

        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(2f);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //canvas.translate(mPointCenter.x, mPointCenter.y);
        canvas.drawColor(ContextCompat.getColor(getContext(), android.R.color.black));

        float centerX = getWidth() * 1.0f / 2, centerY = getHeight() * 1.0f / 2;

        //canvas.drawLine(centerX, 0, centerX, getHeight(), mPaint);
        //canvas.drawLine(0, centerY, getWidth(), centerY, mPaint);

        //set：直接设置Matrix的值，每次set一次，整个Matrix的数组都会变掉
        mMatrix.reset();
        //单组变换，以画布左上角顶点为原点(0, 0)
        mMatrix.setTranslate(centerX - 1.0f * mBitmap.getWidth() / 2, 0);//平移到画布顶部中线
        canvas.drawBitmap(mBitmap, mMatrix, null);
        mMatrix.setTranslate(0, centerY - 1.0f * mBitmap.getHeight() / 2);//平移到画布左侧中线
        canvas.drawBitmap(mBitmap, mMatrix, null);
        mMatrix.setScale(0.3f, 0.3f);
        canvas.drawBitmap(mBitmap, mMatrix, null);

        //复合变换，先平移到点(pivotX, pivotY)，后缩小到原来的0.6倍
        float pivotX = 180, pivotY = 290, ratio = 0.6f;
        //以下绘制是为做对比，效果是图片的几何中心位于点(pivotX, pivotY)
        //canvas.drawBitmap(mBitmap, pivotX - 1.0f * mBitmap.getWidth() / 2, pivotY - 1.0f * mBitmap.getHeight() / 2, null);
        canvas.drawCircle(pivotX, pivotY, 6, mPaint);//点(pivotX, pivotY)

        mMatrix.reset();
        float dx = 1.0f * mBitmap.getWidth() / 2, dy = 1.0f * mBitmap.getHeight() / 2;
        mMatrix.preTranslate(pivotX - dx, pivotY - dy);//缩小到以前的0.5倍
        mMatrix.preScale(ratio, ratio);//缩小到以前的0.5倍
        mMatrix.preTranslate(-dx, -dy);
        mMatrix.postTranslate(dx, dy);
        canvas.drawBitmap(mBitmap, mMatrix, null);

        canvas.drawBitmap(mBitmap, centerX - 1.0f * mBitmap.getWidth() / 2
                , centerY - 1.0f * mBitmap.getHeight() / 2, mPaint);

        /*mMatrix.reset();
        mMatrix.setTranslate(centerX - 1.0f * mBitmap.getWidth() / 2, 2 * centerY - mBitmap.getHeight());//平移到画布底部水平中心
        mMatrix.preScale(0.5f, 0.5f);//缩小到以前的0.5倍
        canvas.drawBitmap(mBitmap, mMatrix, mPaint);*/
    }

}
