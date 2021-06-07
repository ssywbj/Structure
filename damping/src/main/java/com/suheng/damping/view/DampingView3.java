package com.suheng.damping.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

import androidx.core.widget.NestedScrollView;

import com.suheng.damping.R;

public class DampingView3 extends NestedScrollView {
    private static final String TAG = DampingView3.class.getSimpleName();

    private View mDampingLayout;
    private final Rect mRect = new Rect(); //用于记录childView的初始位置

    private int mMode;

    private View mLayoutLoading;
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
    }

    private void calcHeights() {
        mLoadingViewHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP
                , 70, getResources().getDisplayMetrics());

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getContext().getDisplay().getRealMetrics(displayMetrics);
        mScreenHeight = displayMetrics.heightPixels;
        Log.i(TAG, "screen height: " + mScreenHeight + ", mLoadingViewHeight: " + mLoadingViewHeight);
    }

    private static final float LOADING_START_ALPHA = 0.3f;
    private static final float LOADING_DELTA_ALPHA = 1 - LOADING_START_ALPHA;
    private static final float LOADING_START_SCALE = 0.5f;
    private static final float LOADING_DELTA_SCALE = 1 - LOADING_START_SCALE;

    View childView;
    private View mTextLoading;
    private DampingProgressBar mProgressBar;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Log.i(TAG, "onFinishInflate: " + getChildCount());
        if (getChildCount() == 1) {
            childView = getChildAt(0);
            if (mMode == 2) {
                this.calcHeights();

                final ViewGroup.LayoutParams layoutParams = childView.getLayoutParams();
                removeView(childView);

                mLayoutLoading = inflate(getContext(), R.layout.damping_view_title, null);
                FrameLayout layoutParent = new FrameLayout(getContext());
                layoutParent.addView(childView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                layoutParent.addView(mLayoutLoading, LayoutParams.MATCH_PARENT, mLoadingViewHeight);
                addView(layoutParent, layoutParams);

                mDampingLayout = layoutParent;

                mTextLoading = findViewById(R.id.damping_text_loading);
                mProgressBar = findViewById(R.id.damping_progress_bar);

                mTextLoading.setScaleX(LOADING_START_SCALE);
                mTextLoading.setScaleY(LOADING_START_SCALE);
                mTextLoading.setAlpha(LOADING_START_ALPHA);

                mProgressBar.setScaleX(LOADING_START_SCALE);
                mProgressBar.setScaleY(LOADING_START_SCALE);
                mProgressBar.setAlpha(LOADING_START_ALPHA);
            } else if (mMode == 1) {
                mDampingLayout = childView;
            }
        }
    }

    int mTop;

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (mDampingLayout != null) {
            mTop = mDampingLayout.getTop();
            if (mMode == 2) {
                //mLoadingViewHeight = 200;
                mLayoutLoading.setTranslationY(-mLoadingViewHeight);
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mDampingLayout != null) {
            this.calcDampingArea(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    private float mPreviousY;
    private float mMoveHeight;
    private int mScreenHeight;
    float startY, distanceY;

    private void calcDampingArea(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPreviousY = ev.getY();
                startY = mPreviousY;
                mMoveHeight = 0;
                break;
            case MotionEvent.ACTION_MOVE: //0.8 * pow(1 - x, 4), x=s/h, s是滑动距离、h是屏幕高度
                float currentY = ev.getY();
                //distanceY = Math.abs(currentY - startY);
                float factor = (float) (0.8 * Math.pow(1 - 1.0 * distanceY / mScreenHeight, 4));

                float deltaY = currentY - mPreviousY;

                Log.d(TAG, mPreviousY + "--" + currentY + ", distance y: "
                        + distanceY + ",factor: " + factor);

                //mMoveHeight += deltaY * factor;
                mMoveHeight += deltaY;

                if ((!canScrollVertically(-1)) && (currentY - mPreviousY) > 0) {
                    Log.d(TAG, "scroll to up, top: " + mDampingLayout.getTop()
                            + ", bottom: " + mDampingLayout.getBottom() + ", move height: " + mMoveHeight);
                    if (mLayoutLoading != null) {
                        if (mMoveHeight <= 2.5 * mLoadingViewHeight) {
                            if (mMoveHeight <= mLoadingViewHeight) {
                                float percent = mMoveHeight / mLoadingViewHeight;
                                mTextLoading.setScaleX(LOADING_START_SCALE + LOADING_DELTA_SCALE * percent);
                                mTextLoading.setScaleY(mTextLoading.getScaleX());
                                mTextLoading.setAlpha(LOADING_START_ALPHA + LOADING_DELTA_ALPHA * percent);

                                mProgressBar.setScaleX(mTextLoading.getScaleX());
                                mProgressBar.setScaleY(mTextLoading.getScaleX());
                                mProgressBar.setAlpha(mTextLoading.getAlpha());
                            }
                            mLayoutLoading.setTranslationY(-mLoadingViewHeight + mMoveHeight);

                            childView.setTranslationY(mMoveHeight);
                        }
                    }
                    /*mDampingLayout.layout(mDampingLayout.getLeft(), (int) (mTop + mMoveHeight)
                            , mDampingLayout.getRight(), mDampingLayout.getBottom());*/
                }

                /*if (canScrollVertically(1)) {
                } else {
                    Log.i(TAG, "scroll to bottom");
                }*/

                mPreviousY = currentY;
                break;
            case MotionEvent.ACTION_UP:
                childView.setTranslationY(0);
                if (mLayoutLoading != null) {
                    mLayoutLoading.setTranslationY(-mLoadingViewHeight);
                }
                //this.verticalAnimation();
                //mDampingLayout.layout(mDampingLayout.getLeft(), mTop, mDampingLayout.getRight(), mDampingLayout.getBottom());
                //mTextView.setTranslationY(-mLoadingViewHeight);

                /*mTextView.setAlpha(mLoadingStartAlpha);
                mTextView.setScaleX(mLoadingStartScale);
                mTextView.setScaleY(mLoadingStartScale);*/
                break;
        }
    }

    private void verticalAnimation() {
        TranslateAnimation animation = new TranslateAnimation(0.0f, 0.0f,
                (int) (mTop + mMoveHeight), mTop); //上下回弹的动画效果
        animation.setDuration(600);
        animation.setFillAfter(true);
        animation.setInterpolator(new DampInterpolator());
        mDampingLayout.setAnimation(animation);

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
