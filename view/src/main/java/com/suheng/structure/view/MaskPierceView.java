package com.suheng.structure.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class MaskPierceView extends View {
    private static final String TAG = MaskPierceView.class.getSimpleName();
    private Bitmap mSrcRect;
    private Bitmap mDstCircle;

    private int mPiercedX, mPiercedY;
    private int mPiercedRadius;
    private Paint mPaint;
    private PorterDuffXfermode mXfermode;

    public MaskPierceView(Context context) {
        super(context);
        this.init();
    }

    public MaskPierceView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public MaskPierceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    private void init() {
        //View从API11才加入setLayerType方法
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        setBackgroundResource(R.drawable.beauty2);

        this.setPiercePosition(200, 200, 100);

        mPaint = new Paint();
        mXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mSrcRect = makeSrcRect(w, h);
        mDstCircle = makeDstCircle(w, h);

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setFilterBitmap(false);
        int sl = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);

        canvas.drawBitmap(mDstCircle, 0, 0, mPaint);
        mPaint.setXfermode(mXfermode);

        mPaint.setAlpha(160);
        canvas.drawBitmap(mSrcRect, 0, 0, mPaint);
        mPaint.setXfermode(null);

        /*canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
        mPaint.setAlpha(255);*/

        //canvas.restoreToCount(sl);
    }

    /**
     * 创建镂空层圆形形状
     */
    private Bitmap makeDstCircle(int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);

        canvas.drawCircle(mPiercedX, mPiercedY, mPiercedRadius, paint);
        return bitmap;
    }

    /**
     * @param piercedX 镂空的圆心坐标
     * @param piercedY 镂空的圆心坐标
     * @param radius   镂空的圆半径
     */
    public void setPiercePosition(int piercedX, int piercedY, int radius) {
        this.mPiercedX = piercedX;
        this.mPiercedY = piercedY;
        this.mPiercedRadius = radius;
    }

    /**
     * 创建遮罩层形状
     */
    private Bitmap makeSrcRect(int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        canvas.drawRect(new RectF(0, 0, width, height), paint);
        return bitmap;
    }

}
