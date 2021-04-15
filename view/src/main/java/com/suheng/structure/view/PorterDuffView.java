package com.suheng.structure.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class PorterDuffView extends View {
    private static final String TAG = PorterDuffView.class.getSimpleName();
    private Paint mDstPaint, mSrcPaint;
    private PorterDuffXfermode mXfermode;
    private Rect mRect;


    public PorterDuffView(Context context) {
        super(context);
        this.init();
    }

    public PorterDuffView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public PorterDuffView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    private void init() {
        //View从API11才加入setLayerType方法
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        //setBackgroundColor(Color.RED);

        mDstPaint = new Paint();
        mDstPaint.setColor(Color.GRAY);
        mSrcPaint = new Paint();
        mSrcPaint.setColor(Color.BLUE);

        mRect = new Rect(100, 100, 300, 300);

        //CLEAR、SRC、DST、SRC_OVER、DST_OVER、SRC_IN、DST_IN、SRC_OUT、DST_OUT、SRC_ATOP、DST_ATOP、XOR
        //DARKEN、LIGHTEN、MULTIPLY、SCREEN、ADD、OVERLAY
        mXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //this.paintXfermode(canvas);
        this.paintSaveLayer(canvas);
    }

    private void paintXfermode(Canvas canvas) {
        int sc = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);//创建一个新的layer
        //canvas.drawARGB(255, 0xFF, 0, 0);//画布颜色

        //下层图像
        canvas.drawCircle(100, 100, 100, mDstPaint);

        //上层图像
        mSrcPaint.setXfermode(mXfermode);
        //canvas.drawRect(new Rect(0,0,200,200),mSrcPaint);//圆和矩形位置重叠
        canvas.drawRect(mRect, mSrcPaint);//圆和矩形位置稍有偏移

        mSrcPaint.setXfermode(null); // 还原混合模式
        canvas.restoreToCount(sc);  // 将这个layer绘制到canvas默认的layer
    }

    private void paintSaveLayer(Canvas canvas) {
        //canvas.drawCircle(100, 100, 100, mDstPaint);

        /*int i = canvas.saveLayerAlpha(mRect.left, mRect.top, mRect.right, mRect.bottom, Canvas.ALL_SAVE_FLAG);
        int saveCount = canvas.getSaveCount();
        Log.d(TAG, "saveCount: " + saveCount + ", i = " + i);
        i = canvas.saveLayerAlpha(mRect.left + 10, mRect.top + 10, mRect.right + 10, mRect.bottom + 10, Canvas.ALL_SAVE_FLAG);
        saveCount = canvas.getSaveCount();
        Log.d(TAG, "saveCount: " + saveCount + ", i = " + i);

        canvas.restoreToCount(2);
        saveCount = canvas.getSaveCount();
        Log.d(TAG, "saveCount: " + saveCount);*/


        canvas.drawCircle(100, 100, 100, mDstPaint);
        //int i = canvas.saveLayerAlpha(mRect.left, mRect.top, mRect.right, mRect.bottom, Canvas.ALL_SAVE_FLAG);
        //mSrcPaint.setAlpha(110);
        canvas.saveLayer(mRect.left, mRect.top, mRect.right, mRect.bottom, mSrcPaint, Canvas.ALL_SAVE_FLAG);
        canvas.drawRect(mRect, mSrcPaint);
    }
}
