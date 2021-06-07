package com.suheng.damping.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.widget.NestedScrollView;

import com.suheng.damping.R;

public class DampingView3 extends NestedScrollView {
    private static final String TAG = DampingView3.class.getSimpleName();

    private View mDampingLayout;
    private final Rect mRect = new Rect(); //用于记录childView的初始位置

    private int mMode;

    private LinearLayout mLinearLayout;
    private TextView mTextView;
    private int mLoadingViewHeight;

    public DampingView3(Context context) {
        this(context, null);
    }

    public DampingView3(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DampingView3(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray typedArray = null;
        try {
            typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DampingView, 0, 0);

            mMode = typedArray.getInt(R.styleable.DampingView_mode, 0);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (typedArray != null) {
                typedArray.recycle();
            }
        }

        this.init();
    }

    private void init() {
        if (mMode == 2) {
            mLinearLayout = new LinearLayout(getContext());
            mLinearLayout.setOrientation(LinearLayout.VERTICAL);

            mTextView = new TextView(getContext());
            mTextView.setGravity(Gravity.CENTER);
            mTextView.setTextColor(Color.RED);
            mTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
            mTextView.setBackgroundColor(Color.BLUE);
            mTextView.setText("Loading");

            mLoadingViewHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getContext().getDisplay().getRealMetrics(displayMetrics);
        mScreenHeight = displayMetrics.heightPixels;
        Log.i(TAG, "screen height: " + mScreenHeight);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Log.i(TAG, "onFinishInflate: " + getChildCount());
        if (getChildCount() == 1) {
            View childView = getChildAt(0);
            if (mMode == 2) {
                final ViewGroup.LayoutParams layoutParams = childView.getLayoutParams();
                removeView(childView);

                mLinearLayout.addView(mTextView, LayoutParams.MATCH_PARENT, mLoadingViewHeight);
                mLinearLayout.addView(childView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                addView(mLinearLayout, layoutParams);

                mDampingLayout = mLinearLayout;
            } else if (mMode == 1) {
                mDampingLayout = childView;
            }
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.i(TAG, "onAttachedToWindow: " + getChildCount());
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        Log.v(TAG, "onLayout: " + getChildCount());
        if (mMode == 2) {
            smoothScrollTo(0, mLoadingViewHeight);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //if (mMode == 1 || mMode == 2) {
        this.calcDampingArea(ev);
        //}
        return super.dispatchTouchEvent(ev);
    }

    private float mPreviousY, mDistanceY;
    private int mScreenHeight;

    private void calcDampingArea(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPreviousY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE: //0.8 * pow(1 - x, 4), x=s/h, s是滑动距离、h是屏幕高度
                float currentY = ev.getY();
                mDistanceY = Math.abs(currentY - mPreviousY);
                float factor = (float) (0.8 * Math.pow(1 - 1.0 * mDistanceY / mScreenHeight, 4));
                if (canScrollVertically(-1)) {
                } else {
                    if ((currentY - mPreviousY) > 0) {
                        Log.i(TAG, "scroll to up");
                        Log.d(TAG, mPreviousY + "--" + currentY + ", distance y: " + mDistanceY + ",factor: " + factor);
                    }
                }
                /*if (canScrollVertically(1)) {
                } else {
                    Log.i(TAG, "scroll to bottom");
                }*/
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
    }

    private void verticalAnimation() {
        TranslateAnimation animation = new TranslateAnimation(0.0f, 0.0f,
                mDampingLayout.getTop() - mRect.top, 0); //上下回弹的动画效果
        animation.setDuration(600);
        //animation.setFillAfter(true);
        //设置阻尼动画效果
        //animation.setInterpolator(new DampInterpolator());
        mDampingLayout.setAnimation(animation);
    }

    private static class DampInterpolator implements Interpolator {
        @Override
        public float getInterpolation(float input) {
            return (float) (1 - Math.pow((1 - input), 5));
        }
    }

    protected float getFriction(float overScrollLength, float offset, boolean isEasing) {
        int viewPortLength = getResources().getDisplayMetrics().heightPixels;
        if (isEasing) {
            return frictionFactor((Math.abs(overScrollLength) - Math.abs(offset)) / viewPortLength);
        } else {
            return 0.8f;
        }
    }

    protected float frictionFactor(float overscrollFraction) {
        return (float) (0.8 * Math.pow(1 - overscrollFraction, 4));
    }

}
