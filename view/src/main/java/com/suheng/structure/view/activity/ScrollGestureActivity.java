package com.suheng.structure.view.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.OverScroller;

import androidx.appcompat.app.AppCompatActivity;

import com.suheng.structure.view.R;

public class ScrollGestureActivity extends AppCompatActivity {
    public static final String TAG = ScrollGestureActivity.class.getSimpleName();

    private GestureDetector mGestureDetector;
    private OverScroller mOverScroller;

    private View mLayoutRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll_gesture);
        mLayoutRoot = findViewById(R.id.aty_root);

        this.initGestureDetector();

        mOverScroller = new OverScroller(this);
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

}