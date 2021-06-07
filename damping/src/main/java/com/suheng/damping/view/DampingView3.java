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
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.core.widget.NestedScrollView;

import com.suheng.damping.R;

public class DampingView3 extends NestedScrollView {
    private static final String TAG = DampingView3.class.getSimpleName();

    private View mDampingLayout;
    private final Rect mRect = new Rect(); //用于记录childView的初始位置

    private int mMode;

    private FrameLayout mLinearLayout;
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
            mLinearLayout = new FrameLayout(getContext());
            //mLinearLayout.setOrientation(LinearLayout.VERTICAL);

            mTextView = new TextView(getContext());
            mTextView.setGravity(Gravity.CENTER);
            mTextView.setTextColor(Color.RED);
            mTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            mTextView.setBackgroundColor(Color.BLUE);
            mTextView.setText("Loading");

            mLoadingViewHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics());

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getContext().getDisplay().getRealMetrics(displayMetrics);
            mScreenHeight = displayMetrics.heightPixels;
            Log.i(TAG, "screen height: " + mScreenHeight + ", mLoadingViewHeight: " + mLoadingViewHeight);
        }
    }

    View childView;
    private float mLoadingStartAlpha = 0.2f;
    private float mLoadingEndAlpha = 1f, mLoadingDeltaAlpha = mLoadingEndAlpha - mLoadingStartAlpha;
    private float mLoadingStartScale = 0.6f;
    private float mLoadingEndScale = 1f, mLoadingDeltaScale = mLoadingEndScale - mLoadingStartScale;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Log.i(TAG, "onFinishInflate: " + getChildCount());
        if (getChildCount() == 1) {
            childView = getChildAt(0);
            if (mMode == 2) {
                final ViewGroup.LayoutParams layoutParams = childView.getLayoutParams();
                removeView(childView);

                //FrameLayout.LayoutParams params = new FrameLayout.LayoutParams();
                mLinearLayout.addView(childView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                mLinearLayout.addView(mTextView, LayoutParams.MATCH_PARENT, mLoadingViewHeight);
                //mTextView.setVisibility(GONE);
                /*mTextView.setAlpha(mLoadingStartAlpha);
                mTextView.setScaleX(mLoadingStartScale);
                mTextView.setScaleY(mLoadingStartScale);*/
                addView(mLinearLayout, layoutParams);

                mDampingLayout = mLinearLayout;
            } else if (mMode == 1) {
                mDampingLayout = childView;
            } else {
                mDampingLayout = childView;
            }
        }
    }

    int mTop;

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.i(TAG, "onAttachedToWindow: " + getChildCount());
    }

    private int top;

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (mMode == 2) {
            //scrollTo(0, mLoadingViewHeight);
            //top = mDampingLayout.getTop();
            mTop = top;
            //mDampingLayout.layout(mDampingLayout.getLeft(), this.mTop - mLoadingViewHeight, mDampingLayout.getRight(), mDampingLayout.getBottom());
            this.mTop = mDampingLayout.getTop();
            Log.v(TAG, "onLayout: " + getChildCount() + ", mTop:" + mTop + ", top:" + top);
            mTextView.setTranslationY(-mLoadingViewHeight);
        }
    }

    /*@Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        Log.i(TAG, "onScrollChanged, l: " + l + ", t: " + t + ", oldl: " + oldl + ", oldt: " + oldt);
        if (t < mLoadingViewHeight) {
            scrollTo(0, mLoadingViewHeight);
        }
    }*/

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        Log.d(TAG, "onOverScrolled, scrollXv: " + scrollX + ", scrollY: " + scrollY + ", clampedX: " + clampedX + ", clampedY: " + clampedY);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //if (mMode == 1 || mMode == 2) {
        this.calcDampingArea(ev);
        //}
        return super.dispatchTouchEvent(ev);
    }

    private float mPreviousY;
    private float mMoveHeight;
    private int mScreenHeight;
    float startY,distanceY;

    private void calcDampingArea(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPreviousY = ev.getY();
                startY = mPreviousY;
                mMoveHeight = 0;
                //this.mTop = top;
                break;
            case MotionEvent.ACTION_MOVE: //0.8 * pow(1 - x, 4), x=s/h, s是滑动距离、h是屏幕高度
                float currentY = ev.getY();
                 distanceY = Math.abs(currentY - startY);
                float factor = (float) (0.8 * Math.pow(1 - 1.0 * distanceY / mScreenHeight, 4));

                float deltaY = Math.abs(currentY - mPreviousY);

                if (distanceY > mLoadingViewHeight) {
                    Log.i(TAG, "滑了一个Loading的高度");
                } else {
                    mTextView.setTranslationY(-mLoadingViewHeight+distanceY);
                    /*mTextView.setAlpha(mLoadingStartAlpha + mLoadingDeltaAlpha * distanceY / mLoadingViewHeight);
                    mTextView.setScaleX(mLoadingStartScale + mLoadingDeltaScale * distanceY / mLoadingViewHeight);
                    mTextView.setScaleY(mLoadingStartScale + mLoadingDeltaScale * distanceY / mLoadingViewHeight);*/
                }

                mMoveHeight += deltaY * factor;

                if ((!canScrollVertically(-1)) && (currentY - mPreviousY) > 0) {
                    Log.d(TAG, "scroll to up, " + mPreviousY + "--" + currentY + ", distance y: "
                            + distanceY + ",factor: " + factor + ", top: " + mDampingLayout.getTop()
                            + ", bottom: " + mDampingLayout.getBottom() + ", move height: " + mMoveHeight);
                    /*childView.layout(childView.getLeft(), (int) (mTop + mMoveHeight)
                            , childView.getRight(), childView.getBottom());*/
                    childView.setTranslationY(mMoveHeight);
                }


                /*if (canScrollVertically(1)) {
                } else {
                    Log.i(TAG, "scroll to bottom");
                }*/

                mPreviousY = currentY;
                break;
            case MotionEvent.ACTION_UP:
                /*this.verticalAnimation();
                childView.layout(childView.getLeft(), mTop, childView.getRight(), childView.getBottom());*/
                //mTextView.setTranslationY(-mLoadingViewHeight);

                /*mTextView.setAlpha(mLoadingStartAlpha);
                mTextView.setScaleX(mLoadingStartScale);
                mTextView.setScaleY(mLoadingStartScale);*/
                break;
        }
    }

    private void verticalAnimation() {
        TranslateAnimation animation = new TranslateAnimation(0.0f, 0.0f,
                childView.getTop(), mTop); //上下回弹的动画效果
        animation.setDuration(600);
        animation.setFillAfter(true);
        animation.setInterpolator(new DampInterpolator());
        childView.setAnimation(animation);

        /*TranslateAnimation animation1 = new TranslateAnimation(0.0f, 0.0f,
                -mLoadingViewHeight+distanceY, -mLoadingViewHeight); //上下回弹的动画效果
        animation1.setDuration(600);
        animation1.setFillAfter(true);
        mTextView.setAnimation(animation);*/
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
