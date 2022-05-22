package com.suheng.structure.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.OverScroller;

import androidx.annotation.Nullable;

/**
 * Scroller用法
 * 1.初始化Scroller
 * 2.调用startScroll()开始滚动
 * 3.执行invalidate()刷新界面
 * 4.重写View的computeScroll()并在其内部实现与滚动相关的业务逻辑
 * 5.再次执行invalidate()刷新界面
 */
public class ScrollerTrackerView extends View {
    private static final String TAG = ScrollerTrackerView.class.getSimpleName();
    private int mIndex = 0;
    private Paint mPaint;
    private int mWidth;
    private OverScroller mScroller;
    private int mDownX;
    private int mOffsetX;

    public ScrollerTrackerView(Context context) {
        this(context, null);
    }

    public ScrollerTrackerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollerTrackerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setBackgroundColor(Color.GRAY);

        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        mPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 22, metrics));

        mScroller = new OverScroller(getContext());

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = (int) event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                int scrollOffsetX = (int) (mDownX - event.getX()) + mOffsetX;
                this.smoothScrollTo(scrollOffsetX);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                int ddx = (int) (event.getX() - mDownX);
                boolean isScrollToOtherIndex = Math.abs(ddx) >= mWidth / 4;
                if (isScrollToOtherIndex) { //当滑动超过1/4的宽度时再去判断是左滑还是右滑
                    if (ddx < 0) { //手势向左滑，带出右侧视图
                        mIndex++;
                    } else if (ddx > 0) { //手势向右滑，带出左侧视图
                        mIndex--;
                    }
                }
                mOffsetX = mIndex * mWidth;
                Log.v(TAG, "ACTION_UP, index: " + mIndex + ", mOffsetX: " + mOffsetX + ", ddx:" + ddx);
                this.smoothScrollTo(mOffsetX);
                break;
        }
        return true;
    }

    private void smoothScrollTo(int offsetX) {
        int startX = mScroller.getFinalX(); //getFinal: 获取Scroller最终停止位置的位置
        int dx = offsetX - startX;
        Log.d(TAG, "offsetX: " + offsetX + ", startX: " + startX + ", dx: " + dx);

        //startScroll：Scroller开始滚动，start：开始滚动的位置，d：滚动的距离，duration：滚动时长
        mScroller.startScroll(startX, 0, dx, 0, 500);

        invalidate(); //调用invalidate()才能保证computeScroll()会被调用
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        boolean isComputeScroll = mScroller.computeScrollOffset(); //Scroller计算当前时间点对应的滚动位置并返回动画是否还在进行
        if (isComputeScroll) {
            int currX = mScroller.getCurrX(); //Scroller获取computeScrollOffset执行时的滚动x值
            Log.i(TAG, "computeScroll, currX: " + currX);
            scrollTo(currX, 0);
            invalidate();
        } else {
            //Scroller.isFinished()：Scroller根据当前的时间点判断动画是否已结束
            Log.d(TAG, "computeScroll is over, isFinished: " + mScroller.isFinished());
        }
    }

    /*@Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        Log.d(TAG, "onScrollChanged, l: " + l + ", t: " + t + ", oldl: " + oldl + ", oldt: " + oldt);
    }*/

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mWidth <= 0) {
            return;
        }

        drawText(canvas, (mIndex - 1) * mWidth, mIndex - 1);
        drawText(canvas, mIndex * mWidth, mIndex);
        drawText(canvas, (mIndex + 1) * mWidth, mIndex + 1);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (!mScroller.isFinished()) {
            mScroller.abortAnimation(); //Scroller停止滚动
        }
    }

    private void drawText(Canvas canvas, int startX, int index) {
        canvas.save();
        canvas.translate(startX, 0);
        String text = "页面：" + index;
        canvas.drawText(text, 10, 100, mPaint);
        canvas.restore();
    }

}
