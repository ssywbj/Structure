package com.suheng.damping;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.OverScroller;
import android.widget.Scroller;

import androidx.appcompat.app.AppCompatActivity;

public class ScrollGestureActivity extends AppCompatActivity {
    public static final String TAG = ScrollGestureActivity.class.getSimpleName();

    private GestureDetector mGestureDetector;
    private OverScroller mOverScroller;
    private Scroller mScroller;

    private View mLayoutRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll_gesture);
        mLayoutRoot = findViewById(R.id.aty_root);

        //this.initGestureDetector();

        mOverScroller = new OverScroller(this);
        mScroller = new Scroller(this);
    }

    private void initGestureDetector() {
        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                boolean onDown = super.onDown(e);
                Log.d(TAG, "onDown, e:" + e.getAction() + ", onDown: " + onDown);
                return true;
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                boolean onSingleTapUp = super.onSingleTapUp(e);
                Log.v(TAG, "onSingleTapUp, e:" + e.getAction() + ", onSingleTapUp: " + onSingleTapUp);
                return onSingleTapUp;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                boolean onSingleTapConfirmed = super.onSingleTapConfirmed(e);
                Log.v(TAG, "onSingleTapConfirmed, e:" + e.getAction() + ", onSingleTapConfirmed: " + onSingleTapConfirmed);
                return onSingleTapConfirmed;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                boolean onFling = super.onFling(e1, e2, velocityX, velocityY);
                Log.w(TAG, "onFling, e1:" + e1.getAction() + ", e2: " + e2.getAction() + ", velocityX: " + velocityX
                        + ", velocityY: " + velocityY + ", onFling: " + onFling);
                mOverScroller.fling(0, 0, 0, (int) velocityY, 0, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                boolean onScroll = super.onScroll(e1, e2, distanceX, distanceY);
                Log.w(TAG, "onScroll, e1:" + e1.getAction() + ", e2: " + e2.getAction() + ", distanceX: " + distanceX
                        + ", distanceY: " + distanceY + ", onScroll: " + onScroll);
                return true;
            }
        });
        mLayoutRoot.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mGestureDetector.onTouchEvent(event);
            }
        });
    }

    private int mDownY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int currentY = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownY = currentY;
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = mDownY - currentY;
                mDownY = currentY;
                this.smoothScrollTo(dx, 0);
                break;
            case MotionEvent.ACTION_UP:
                //int ddx = (int) (event.getX() - mDownX);
                //smoothScrollTo(mWidth * mIndex, 0);
                break;
        }
        return true;
    }

    //调用此方法滚动到目标位置
    public void smoothScrollTo(int fx, int fy) {
        int dx = fx - mScroller.getFinalX();
        int dy = fy - mScroller.getFinalY();
        Log.d("Wbj", "fx: " + fx + ", fy: " + fy + "----dx: " + dx + ", dy: " + dy);
        Log.d("Wbj", "final, x: " + mScroller.getFinalX() + ", y: " + mScroller.getFinalY());
        this.smoothScrollBy(dx, dy);
    }

    //调用此方法设置滚动的相对偏移
    public void smoothScrollBy(int dx, int dy) {
        //设置mScroller的滚动偏移量
        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx, dy, 500);
        mLayoutRoot.invalidate();//这里必须调用invalidate()才能保证computeScroll()会被调用，否则不一定会刷新界面，看不到滚动效果
    }

}