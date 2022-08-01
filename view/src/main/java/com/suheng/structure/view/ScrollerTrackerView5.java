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

public class ScrollerTrackerView5 extends View {
    private static final String TAG = ScrollerTrackerView5.class.getSimpleName();
    private int mIndex, mOffsetX;
    private Paint mPaint;
    private int mWidth;
    private int mTouchCurrentX, mTouchDownX;

    private OverScroller mScroller;

    private boolean mIsCycle = true;
    private int mCycleNum = 3;

    public ScrollerTrackerView5(Context context) {
        super(context);
        this.init();
    }

    public ScrollerTrackerView5(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public ScrollerTrackerView5(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    private void init() {
        setBackgroundColor(Color.GRAY);
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        mPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 22, metrics));

        this.initScroller();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mWidth <= 0) {
            return;
        }

        int index = mIsCycle ? Math.abs(mIndex) % mCycleNum : mIndex;
        int pageIndexPrevious = index - 1;
        if (pageIndexPrevious < 0) {
            pageIndexPrevious = mCycleNum - 1;
        }
        drawText(canvas, (mIndex - 1) * mWidth, pageIndexPrevious);
        drawText(canvas, mIndex * mWidth, index);
        int pageIndexNext = index + 1;
        if (pageIndexNext > mCycleNum - 1) {
            pageIndexNext = 0;
        }
        drawText(canvas, (mIndex + 1) * mWidth, pageIndexNext);
    }

    private void drawText(Canvas canvas, int dx, int pageIndex) {
        canvas.save();
        canvas.translate(dx, 0);
        String text = "页面：" + pageIndex;
        canvas.drawText(text, 800, 100, mPaint);
        //canvas.drawText(text, 10, 100, mPaint);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchDownX = (int) event.getX();
                mTouchCurrentX = mTouchDownX;
                break;
            case MotionEvent.ACTION_MOVE:
                final int moveX = (int) event.getX();

                this.smoothScrollTo(mTouchDownX - moveX + mOffsetX);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                final int dux = (int) (mTouchDownX - event.getX());
                boolean isChangePage = (Math.abs(dux) >= mWidth / 4); //判断抬手时可以换页的条件：滑动的距离达到视图1/4的宽度
                if (isChangePage) { //切换页面
                    if (dux > 0) { //往左滑，滑出右侧界面
                        mIndex++;
                    }
                    if (dux < 0) { //往右滑，滑出左侧界面
                        mIndex--;
                    }
                }

                mOffsetX = mIndex * mWidth;
                this.smoothScrollTo(mIndex * mWidth);
                Log.v(TAG, "action_up, mIndex: " + mIndex + ", dux: " + dux + ", isChangePage: " + isChangePage);
                break;
        }

        return true;
    }

    private void initScroller() {
        mScroller = new OverScroller(getContext());
    }

    private void smoothScrollTo(int offsetX) {
        //scrollTo(offsetX, 0);

        int currX = mScroller.getCurrX();
        int startX = mScroller.getFinalX();
        int dx = offsetX - startX;
        Log.v(TAG, "smoothScrollTo, offsetX: " + offsetX + ", startX: " + startX + ", dx: " + dx + ", currX: " + currX);

        //startX：开始点的x坐标；startY：开始点的y坐标；dx：水平方向的偏移量，正数会将内容向左滚动；dy：垂直方向的偏移量，正数会将内容向上滚动。
        mScroller.startScroll(startX, 0, dx, 0, 500);

        invalidate();
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        boolean finished = mScroller.isFinished();
        //计算滚动中的新坐标，会配合着getCurrX()和getCurrY()。如果返回true，说明动画未完成；若返回false，说明动画已经完成或是被终止。
        boolean computeScrollOffset = mScroller.computeScrollOffset();
        if (computeScrollOffset) {
            int currX = mScroller.getCurrX(); //滚动中的水平方向相对于原点的偏移量，即当前的X坐标。
            int finalX = mScroller.getFinalX(); //最终滚动到的X坐标，最终是Scroller.getCurrX()=Scroller.getFinalX()
            scrollTo(currX, 0);
            invalidate();
            Log.d(TAG, "computeScroll, finished: " + finished + ", currX: " + currX + ", finalX: " + finalX);
        } else {
            Log.d(TAG, "computeScroll, finished: " + finished);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (!mScroller.isFinished()) {
            mScroller.abortAnimation();
        }
    }

    public void setCycle(boolean cycle) {
        mIsCycle = cycle;
    }

    public void setCycleNum(int cycleNum) {
        mCycleNum = cycleNum;
    }
}
