package com.suheng.structure.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class GuaguakaView extends View {
    private Bitmap mBitmapResult, mBitmapOver, mBitmapDst;

    private Canvas mCanvasDst; //透明画布
    private final Path mPathDst = new Path();
    private final Paint mPaintDst = new Paint(Paint.ANTI_ALIAS_FLAG), mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final Xfermode mXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT);

    private final RectF mRectF = new RectF();

    public GuaguakaView(Context context) {
        this(context, null);
    }

    public GuaguakaView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GuaguakaView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    private void init() {
        mBitmapResult = BitmapFactory.decodeResource(getResources(), R.mipmap.guaguaka_result);
        mBitmapOver = BitmapFactory.decodeResource(getResources(), R.mipmap.guaguaka_over);

        mBitmapDst = Bitmap.createBitmap(mBitmapOver.getWidth(), mBitmapOver.getHeight(), Bitmap.Config.ARGB_8888);
        mCanvasDst = new Canvas(mBitmapDst);
        mPaintDst.setDither(true);
        mPaintDst.setStyle(Paint.Style.STROKE);
        mPaintDst.setStrokeWidth(40);
        mPaintDst.setStrokeCap(Paint.Cap.ROUND);

        mPaint.setDither(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRectF.set(0, 0, w, mBitmapOver.getHeight());
    }

    //http://www.voidcn.com/article/p-wvoxlzgs-ev.html
    //https://www.jianshu.com/p/0e8d39d36fa0
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPathDst.moveTo(event.getX(), event.getY());
                return true;
            case MotionEvent.ACTION_MOVE:
                mPathDst.lineTo(event.getX(), event.getY());
                postInvalidate();
                break;
        }
        //performClick();
        return super.onTouchEvent(event);
    }

    /*@Override
    public boolean performClick() {
        return super.performClick();
    }*/

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.clipRect(mRectF);

        canvas.drawBitmap(mBitmapResult, null, mRectF, null);

        int saveLayer = canvas.saveLayer(mRectF, null);
        mCanvasDst.drawPath(mPathDst, mPaintDst);
        canvas.drawBitmap(mBitmapDst, null, mRectF, null);

        mPaint.setXfermode(mXfermode);
        canvas.drawBitmap(mBitmapOver, null, mRectF, mPaint);
        mPaint.setXfermode(null);
        canvas.restoreToCount(saveLayer);
    }

}
