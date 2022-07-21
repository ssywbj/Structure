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

import androidx.annotation.Nullable;

public class ScrollerTrackerView2 extends View {
    private static final String TAG = ScrollerTrackerView2.class.getSimpleName();
    private int mIndex;
    private Paint mPaint;
    private int mWidth;
    private int mTouchCurrentX, mTouchDownX;
    private int mMaxScrollLength, mDataSize = 3;

    public ScrollerTrackerView2(Context context) {
        super(context);
        this.init();
    }

    public ScrollerTrackerView2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public ScrollerTrackerView2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    private void init() {
        setBackgroundColor(Color.GRAY);
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        mPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 22, metrics));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mMaxScrollLength = (mDataSize - 1) * mWidth;
        Log.v(TAG, "onMeasure, MaxFlingLength：" + mMaxScrollLength);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mWidth <= 0) {
            return;
        }

        Log.d(TAG, "onDraw, mDataSize: " + mDataSize);
        canvas.save();
        for (int i = 0; i < mDataSize; i++) {
            String text = "页面：" + i;
            canvas.drawText(text, 10, 100, mPaint);
            canvas.translate(mWidth, 0);
        }
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
                Log.v(TAG, "action_up, getScrollX(): " + getScrollX() + ", dux: " + dux + ", isChangePage: " + isChangePage);
                break;
        }

        return true;
    }

}
