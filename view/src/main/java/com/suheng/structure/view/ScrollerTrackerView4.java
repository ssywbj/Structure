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
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.OverScroller;

import androidx.annotation.Nullable;

public class ScrollerTrackerView4 extends View {
    private static final String TAG = ScrollerTrackerView4.class.getSimpleName();
    private int mIndex;
    private Paint mPaint;
    private int mWidth;
    private OverScroller mScroller;
    private int mTouchCurrentX, mTouchDownX, mOffsetX, mScrollOffsetX;

    private VelocityTracker mVelocityTracker;
    /**
     * 滑动的最大速度
     */
    private int mMaximumVelocity;
    /**
     * 滑动的最小速度
     */
    private int mMinimumVelocity;

    public ScrollerTrackerView4(Context context) {
        super(context);
        this.init();
    }

    public ScrollerTrackerView4(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public ScrollerTrackerView4(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    private void init() {
        setBackgroundColor(Color.GRAY);

        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        mPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 22, metrics));

        mScroller = new OverScroller(getContext());

        //https://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2012/1114/558.html
        //https://juejin.cn/post/6844903791066628110
        mVelocityTracker = VelocityTracker.obtain();

        ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());
        mMaximumVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
        mMinimumVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
        Log.v(TAG, "init, maximumVelocity：" + mMaximumVelocity + ", minimumVelocity: " + mMinimumVelocity
                + ", scaledTouchSlop: " + viewConfiguration.getScaledTouchSlop());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mMaxScrollLength = (mDataSize - 1) * mWidth;
        Log.v(TAG, "onMeasure, MaxFlingLength：" + mMaxScrollLength);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mVelocityTracker.addMovement(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchDownX = (int) event.getX();
                mTouchCurrentX = mTouchDownX;
                break;
            case MotionEvent.ACTION_MOVE:
                final int moveX = (int) event.getX();
                final int dmx = mTouchCurrentX - moveX;
                if (dmx > 0) { //往左滑，滑出右侧界面
                    if (getScrollX() >= mMaxScrollLength) { //已滑到最右侧
                        scrollTo(mMaxScrollLength, 0);
                    } else {
                        scrollBy(dmx, 0);
                    }
                }

                if (dmx < 0) { //往右滑，滑出左侧界面
                    if (getScrollX() <= 0) { //已滑到最左侧
                        scrollTo(0, 0);
                    } else {
                        scrollBy(dmx, 0);
                    }
                }

                /*scrollBy(dmx, 0);
                if (getScrollX() <= 0) { //最左边
                    scrollTo(0, 0);
                }
                if (getScrollX() >= mMaxScrollLength) { //最右边
                    scrollTo(mMaxScrollLength, 0);
                }*/
                mTouchCurrentX = moveX;
                Log.v(TAG, "action_move, getScrollX(): " + getScrollX() + ", dmx: " + dmx);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                final int dux = (int) (mTouchDownX - event.getX());
                boolean isChangePage = (Math.abs(dux) >= mWidth / 4); //判断抬手时可以换页的条件：滑动的距离达到视图1/4的宽度
                if (isChangePage) { //切换页面
                    if (dux > 0) { //往左滑，滑出右侧界面
                        if (getScrollX() < mMaxScrollLength) { //未滑到右侧界面
                            mIndex++;
                        }
                    }
                    if (dux < 0) { //往右滑，滑出左侧界面
                        if (getScrollX() > 0) { //未滑到左侧界面
                            mIndex--;
                        }
                    }
                }
                scrollTo(mIndex * mWidth, 0);

                //计算速度
                //mVelocityTracker.computeCurrentVelocity(1000);
                //float velocityXTmp = mVelocityTracker.getXVelocity();
                mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                velocityX = (int) mVelocityTracker.getXVelocity();
                Log.v(TAG, "fling, velocityX：" + velocityX + ", before scroll getScrollX(): " + scrollX /*+ ", ScrollOffsetX: " + dmx*/);

                /*if (getScrollX() == mMaxScrollLength) {
                    Log.d(TAG, "onTouchEvent: to right");
                    mIndex = 0;
                    scrollTo(0, 0);
                } else {
                    //mIndex++;
                    //scrollTo(mIndex * mWidth, 0);
                    scrollBy(mWidth, 0);
                }
                Log.v(TAG, "fling, velocityX：" + velocityX + ", after scroll getScrollX(): " + getScrollX() + ", ScrollOffsetX: " + dmx);*/

                if (Math.abs(velocityX) > 2000) {
                    if (!mScroller.isFinished()) { //停止上一次的滚动
                        mScroller.abortAnimation();
                    }

                    /*scrollX = getScrollX();
                    //mScroller.fling(getScrollX(), 0, velocityX, 0, 0, Integer.MAX_VALUE, 0, 0);
                    mScroller.fling(scrollX, 0, velocityX, 0, 0, mMaxScrollLength, 0, 0);
                    //scrollBy(-velocityX, 0);
                    if (getHandler() != null) {
                        getHandler().post(mFlingRunnable);
                    }*/
                } /*else {
                    int dmx = (int) (event.getX() - mDownX);
                    boolean isChangePage = Math.abs(dmx) >= mWidth / 4;
                    if (isChangePage) { //当滑动超过1/4的宽度时再去判断是左滑还是右滑
                        if (dmx < 0) { //手势向左滑，带出右侧视图
                            mIndex++;
                        } else if (dmx > 0) { //手势向右滑，带出左侧视图
                            mIndex--;
                        }
                    }
                    mOffsetX = mIndex * mWidth;
                    Log.v(TAG, "action_up, index: " + mIndex + ", mOffsetX: " + mOffsetX + ", dmx:" + dmx);
                    //this.smoothScrollTo(mOffsetX);
                }*/

                mVelocityTracker.clear(); //清除监视器事件
                break;
        }

        return true;
    }

    int velocityX;
    int scrollX;

    private final Runnable mFlingRunnable = new Runnable() {
        @Override
        public void run() {
            boolean finished = mScroller.isFinished();
            boolean computeScrollOffset = mScroller.computeScrollOffset();
            int currX = mScroller.getCurrX(); //Scroller获取computeScrollOffset执行时的滚动x值
            int diffX = scrollX - currX;
            Log.i(TAG, "run, currX: " + currX + ", scrollX: " + scrollX + ", diffX: " + diffX
                    + ", finished: " + finished + ", computeScrollOffset: " + computeScrollOffset);
            //scrollBy(1, 0);
            //scrollTo(velocityX, 0);

            /*if (getHandler() != null) {
                getHandler().post(this);
            }*/
        }
    };

    private void smoothScrollTo(int offsetX) {
        int startX = mScroller.getFinalX(); //getFinal: 获取Scroller最终停止位置的位置
        int dx = offsetX - startX;
        //Log.d(TAG, "offsetX: " + offsetX + ", startX: " + startX + ", dx: " + dx);

        //startScroll：Scroller开始滚动，start：开始滚动的位置，d：滚动的距离，duration：滚动时长
        mScroller.startScroll(startX, 0, dx, 0, 500);

        invalidate(); //调用invalidate()才能保证computeScroll()会被调用
    }

    /*@Override
    public void computeScroll() {
        super.computeScroll();
        boolean isComputeScroll = mScroller.computeScrollOffset(); //Scroller计算当前时间点对应的滚动位置并返回动画是否还在进行
        if (isComputeScroll) {
            int currX = mScroller.getCurrX(); //Scroller获取computeScrollOffset执行时的滚动x值
            Log.i(TAG, "computeScroll, currX: " + currX);
            scrollBy(currX, 0);
        } else {
            //Scroller.isFinished()：Scroller根据当前的时间点判断动画是否已结束
            Log.d(TAG, "computeScroll is over, isFinished: " + mScroller.isFinished());
        }
    }*/

    private int mMaxScrollLength, mDataSize = 10;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mWidth <= 0) {
            return;
        }

        /*drawText(canvas, (mIndex - 1) * mWidth, mIndex - 1);
        drawText(canvas, mIndex * mWidth, mIndex);
        drawText(canvas, (mIndex + 1) * mWidth, mIndex + 1);*/

        canvas.save();
        for (int i = 0; i < mDataSize; i++) {
            String text = "页面：" + i;
            canvas.drawText(text, 10, 100, mPaint);
            canvas.translate(mWidth, 0);
        }
        canvas.restore();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (!mScroller.isFinished()) {
            mScroller.abortAnimation(); //Scroller停止滚动
        }
        mVelocityTracker.recycle(); //回收速度监控器
    }

    private void drawText(Canvas canvas, int startX, int index) {
        canvas.save();
        canvas.translate(startX, 0);
        String text = "页面：" + index;
        canvas.drawText(text, 10, 100, mPaint);
        canvas.restore();
    }

}
