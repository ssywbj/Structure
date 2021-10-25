package com.suheng.structure.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
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

public class GuaguakaView1 extends View {
    private Bitmap mBitmapResult, mBitmapOver, mBitmapDst;

    private Canvas mCanvasDst; //透明画布
    private final Path mPathDst = new Path();
    private final Paint mPaintDst = new Paint(Paint.ANTI_ALIAS_FLAG), mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final Xfermode mXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT);

    private final RectF mRectF = new RectF();

    public GuaguakaView1(Context context) {
        this(context, null);
    }

    public GuaguakaView1(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GuaguakaView1(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    private void init() {
        //mBitmapResult = BitmapFactory.decodeResource(getResources(), R.mipmap.guaguaka_result);
        mBitmapOver = BitmapFactory.decodeResource(getResources(), R.mipmap.guaguaka_over);

        mPaintDst.setDither(true);
        mPaintDst.setStyle(Paint.Style.STROKE);
        mPaintDst.setStrokeWidth(40);
        mPaintDst.setStrokeCap(Paint.Cap.ROUND);

        mPaint.setDither(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), mBitmapOver.getHeight());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRectF.set(0, 0, w, mBitmapOver.getHeight());
        mBitmapDst = Bitmap.createBitmap(w, mBitmapOver.getHeight(), Bitmap.Config.ARGB_8888);
        mCanvasDst = new Canvas(mBitmapDst);

        mBitmapResult = Bitmap.createBitmap(w, mBitmapOver.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mBitmapResult);
        canvas.drawColor(Color.WHITE);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setDither(true);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.BLUE);
        paint.setTextSize(40);
        canvas.drawText("中奖结果", w / 2f, mBitmapOver.getHeight() / 2f, paint);
    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    //http://www.voidcn.com/article/p-wvoxlzgs-ev.html
    //https://www.jianshu.com/p/0e8d39d36fa0
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final float x = event.getX(), y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPathDst.reset();
                mPathDst.moveTo(x, y);

                mX = x;
                mY = y;
                return true;
            case MotionEvent.ACTION_MOVE:
                //mPathDst.lineTo(x, y);
                //invalidate();

                float dx = Math.abs(x - mX);
                float dy = Math.abs(y - mY);
                if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                    mPathDst.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2); //用贝塞尔曲线连接，解决线段的连接处不顺滑问题
                    mX = x;
                    mY = y;
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                mPathDst.lineTo(mX, mY);
                mPathDst.reset();

                invalidate();
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
