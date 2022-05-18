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

public class GuaguakaView2 extends View {
    private Bitmap mBitmapResult, mBitmapOver, mBitmapDst;

    private Canvas mCanvasDst; //透明画布
    private final Path mPathDst = new Path();
    private final Paint mPaintDst = new Paint(Paint.ANTI_ALIAS_FLAG), mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final Xfermode mXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT);

    private final RectF mRectF = new RectF();

    public GuaguakaView2(Context context) {
        this(context, null);
    }

    public GuaguakaView2(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GuaguakaView2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    private void init() {
        //mBitmapResult = BitmapFactory.decodeResource(getResources(), R.mipmap.guaguaka_result);
        mBitmapOver = BitmapFactory.decodeResource(getResources(), R.mipmap.guaguaka_over);

        mPaintDst.setDither(true);
        mPaintDst.setStrokeCap(Paint.Cap.ROUND);
        mPaintDst.setStrokeJoin(Paint.Join.ROUND);
        mPaintDst.setStyle(Paint.Style.STROKE);
        mPaintDst.setStrokeWidth(40);

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
                float dx = Math.abs(x - mX);
                float dy = Math.abs(y - mY);
                if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                    mPathDst.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2); //用贝塞尔曲线连接，让各线段平滑连接
                    mX = x;
                    mY = y;

                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mPathDst.lineTo(mX, mY);
                mCanvasDst.drawPath(mPathDst, mPaintDst); //把刮卡过程保存在另一块透明的画布上
                mPathDst.reset();

                invalidate();
                break;
        }
        //performClick();
        return super.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //此画布从下到上有三层：中奖结果、刮卡过程、彩票封面，其中刮卡过程和彩票封面设置图像相交模式为PorterDuff.Mode.SRC_OUT：只在源图像和目标图像不相交的地方绘制源图像
        canvas.drawBitmap(mBitmapResult, null, mRectF, null);

        int saveLayer = canvas.saveLayer(mRectF, null);
        canvas.drawPath(mPathDst, mPaintDst); //显示刮卡过程，为目标图像
        canvas.drawBitmap(mBitmapDst, null, mRectF, null); //因为ACTION_UP的时候Path被reset，会导致刮卡过程被重置，因此需要把刮卡过程不断地定格到画布上
        //onTouchEvent为ACTION_UP的时候先把刮卡过程保存在另一块透明的画布上，onDraw的时候再把透明的画布所依托的Bitmap绘制上，这样会大大减少图像锯齿问题（可和GuaguakaView1对比）

        mPaint.setXfermode(mXfermode);
        canvas.drawBitmap(mBitmapOver, null, mRectF, mPaint); //彩票封面为源图像
        mPaint.setXfermode(null);
        canvas.restoreToCount(saveLayer);
    }

}
