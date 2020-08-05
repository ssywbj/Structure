package com.suheng.structure.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import androidx.annotation.Nullable;

public class InfiniteLine2 extends View {

    private int index = 0;
    private Paint paint;
    private int width;
    private Scroller mScroller;
    private int downX, lastMoveX;

    public InfiniteLine2(Context context) {
        this(context, null);
    }

    public InfiniteLine2(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InfiniteLine2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mScroller = new Scroller(getContext());

        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setTextSize(50);

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = (int) (downX - event.getX()) + lastMoveX;
                smoothScrollTo(dx, 0);
                break;
            case MotionEvent.ACTION_UP:
                int ddx = (int) (event.getX() - downX);
                if (ddx < 0 && Math.abs(ddx) > 100) {
                    index++;
                } else if (ddx > 0 && Math.abs(ddx) > 100) {
                    index--;
                }
                smoothScrollTo(width * index, 0);
                lastMoveX = index * width;
                break;
        }
        return true;

    }

    //调用此方法滚动到目标位置
    public void smoothScrollTo(int fx, int fy) {
        Log.d("dsw", "dsw:" + fx + "--" + fy);
        Log.d("dsw", "dsw---FinalX:" + mScroller.getFinalX() + "--FinalY:" + mScroller.getFinalY());
        int dx = fx - mScroller.getFinalX();
        int dy = fy - mScroller.getFinalY();
        smoothScrollBy(dx, dy);
    }

    //调用此方法设置滚动的相对偏移
    public void smoothScrollBy(int dx, int dy) {
        //设置mScroller的滚动偏移量
        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx, dy, 500);
        invalidate();//这里必须调用invalidate()才能保证computeScroll()会被调用，否则不一定会刷新界面，看不到滚动效果
    }


    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        width = getMeasuredWidth();
        drawText(canvas, (index - 1) * width, 0, index - 1);
        drawText(canvas, index * width, 0, index);
        drawText(canvas, (index + 1) * width, 0, index + 1);
    }

    private void drawText(Canvas canvas, int startX, int startY, int index) {
        canvas.save();
        canvas.translate(startX, 0);
        canvas.drawText("页数：" + index, 0, 100, paint);
        canvas.restore();
    }


}
