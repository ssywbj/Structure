package com.suheng.structure.view.sideslide;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SideSlideLayout extends FrameLayout {
    private static final String TAG = SideSlideLayout.class.getSimpleName();

    public SideSlideLayout(@NonNull Context context) {
        super(context);
    }

    public SideSlideLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SideSlideLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private LinearLayout mLayoutSideSlide;
    private View mLayoutItem;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() != 1) {
            return;
        }

        mLayoutItem = getChildAt(0);
        ViewGroup.LayoutParams layoutParams = mLayoutItem.getLayoutParams();
        Log.d(TAG, "onFinishInflate: " + layoutParams);

        mLayoutSideSlide = new LinearLayout(getContext());
        mLayoutSideSlide.setOrientation(LinearLayout.HORIZONTAL);
        mLayoutSideSlide.setGravity(Gravity.CENTER_VERTICAL);

        this.setSideSlideLayout(mLayoutSideSlide, "删除", Color.RED);
        this.setSideSlideLayout(mLayoutSideSlide, "分享", Color.BLUE);
        this.setSideSlideLayout(mLayoutSideSlide, "收藏", Color.GREEN);

        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.END;
        addView(mLayoutSideSlide, 0, params);

        //removeView(child);
        //addView(child, 1, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        //bringChildToFront(child);

        mLayoutSideSlide.setVisibility(GONE);
    }

    private void setSideSlideLayout(LinearLayout linearLayout, String text, int backgroundColor) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int paddingStart = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, metrics);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                , LinearLayout.LayoutParams.MATCH_PARENT);
        TextView textView = new TextView(getContext());
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        textView.setTextColor(Color.BLACK);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(paddingStart, 0, paddingStart, 0);
        textView.setBackgroundColor(backgroundColor);
        textView.setText(text);

        linearLayout.addView(textView, layoutParams);
    }

    int top, bottom;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        post(() -> {
            top = mLayoutSideSlide.getTop();
            bottom = mLayoutSideSlide.getBottom();
            Log.d(TAG, "onSizeChanged: " + mLayoutSideSlide.getLeft() + ", " + mLayoutSideSlide.getRight()
                    + ", " + top + ", " + bottom);
            int childCount = mLayoutSideSlide.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = mLayoutSideSlide.getChildAt(i);
                Log.i(TAG, "child: " + child.getLeft() + ", " + child.getRight()
                        + ", " + child.getTop() + ", " + child.getBottom());
                child.layout(0, child.getTop(), 0, child.getBottom());
            }
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        post(() -> {
            Log.d(TAG, "onSizeChanged: " + mLayoutSideSlide.getLeft() + ", " + mLayoutSideSlide.getRight()
                    + ", " + mLayoutSideSlide.getTop() + ", " + mLayoutSideSlide.getBottom());
            int childCount = mLayoutSideSlide.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = mLayoutSideSlide.getChildAt(i);
                Log.i(TAG, "child: " + child.getLeft() + ", " + child.getRight()
                        + ", " + child.getTop() + ", " + child.getBottom());
                child.layout(0, child.getTop(), 0, child.getBottom());
            }
        });
    }

    private float mOffsetX, mCurrentX;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mCurrentX = x;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mOffsetX = 0;
                mLayoutItem.setTranslationX(mOffsetX);
                break;
            case MotionEvent.ACTION_MOVE:
                float offset = x - mCurrentX;
                mOffsetX += offset;
                mLayoutItem.setTranslationX(mOffsetX);
                mCurrentX = x;
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

}
