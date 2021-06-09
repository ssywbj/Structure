package com.suheng.damping.view;

import android.animation.ValueAnimator;
import android.app.Activity;
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

public class DampingLayout extends NestedScrollView {
    private static final String TAG = DampingLayout.class.getSimpleName();
    private static final float REFRESHING_START_ALPHA = 0;
    private static final float REFRESHING_DELTA_ALPHA = 1 - REFRESHING_START_ALPHA;
    private static final float REFRESHING_START_SCALE = 0.5f;
    private static final float REFRESHING_DELTA_SCALE = 1 - REFRESHING_START_SCALE;

    private int mMode;

    private View mLayoutRefresh;
    private int mHeightRefreshLayout;
    private DampingProgressBar mProgressBar;
    private View mTextRefreshing;

    private boolean mRefreshing;
    private OnRefreshListener mOnRefreshListener;

    private View mLayoutContent;

    private int mScreenHeight, mMoveHeight;

    public DampingLayout(Context context) {
        this(context, null);
    }

    public DampingLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DampingLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray typedArray = null;
        try {
            typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DampingLayout, 0, 0);

            mMode = typedArray.getInt(R.styleable.DampingLayout_mode, 0);
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

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() != 1 || mMode == 0) {
            return;
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        //getContext().getDisplay().getRealMetrics(displayMetrics);
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
        mScreenHeight = displayMetrics.heightPixels;
        //Log.d(TAG, "screen height: " + mScreenHeight);

        mLayoutContent = getChildAt(0);

        if (mMode == 2) {
            mHeightRefreshLayout = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP
                    , 76, getResources().getDisplayMetrics());
            //Log.d(TAG, "refreshing layout Height: " + mHeightRefreshLayout);

            final ViewGroup.LayoutParams layoutParams = mLayoutContent.getLayoutParams();
            removeView(mLayoutContent);

            mLayoutRefresh = inflate(getContext(), R.layout.damping_view_title, null);
            FrameLayout layoutParent = new FrameLayout(getContext());
            layoutParent.addView(mLayoutContent, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            layoutParent.addView(mLayoutRefresh, LayoutParams.MATCH_PARENT, mHeightRefreshLayout);
            addView(layoutParent, layoutParams);

            mTextRefreshing = findViewById(R.id.damping_text_loading);
            mProgressBar = findViewById(R.id.damping_progress_bar);

            mTextRefreshing.setScaleX(REFRESHING_START_SCALE);
            mTextRefreshing.setScaleY(REFRESHING_START_SCALE);
            mTextRefreshing.setAlpha(REFRESHING_START_ALPHA);

            mProgressBar.setScaleX(REFRESHING_START_SCALE);
            mProgressBar.setScaleY(REFRESHING_START_SCALE);
            mProgressBar.setAlpha(REFRESHING_START_ALPHA);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (mMode == 2) {
            //mLoadingViewHeight = 200;
            mLayoutRefresh.setTranslationY(-mHeightRefreshLayout);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mLayoutContent != null && !mRefreshing) {
            this.calcDampingArea(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    private float mPreviousY;
    private float mStartY;

    private void calcDampingArea(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPreviousY = ev.getY();
                mStartY = mPreviousY;
                mMoveHeight = 0;

                mRect.set(mLayoutContent.getLeft(), mLayoutContent.getTop(), mLayoutContent.getRight(), mLayoutContent.getBottom());
                break;
            case MotionEvent.ACTION_MOVE: //0.8 * pow(1 - x, 4), x=s/h, s是滑动距离、h是屏幕高度
                float currentY = ev.getY();
                float deltaY = currentY - mPreviousY;
                mPreviousY = currentY;

                boolean fromTopDownPull = false, fromBottomUpPull = false;
                if (mMode == 2) {
                    fromTopDownPull = (!canScrollVertically(-1) && (currentY - mStartY) > 0);
                    fromBottomUpPull = (!canScrollVertically(1) && (currentY - mStartY) < 0);
                } else if (mMode == 1) {
                    fromTopDownPull = (!mLayoutContent.canScrollVertically(-1) && (currentY - mStartY) > 0);
                    fromBottomUpPull = (!mLayoutContent.canScrollVertically(1) && (currentY - mStartY) < 0);
                }

                if (fromTopDownPull || fromBottomUpPull) {
                    /*if (fromTopDownPull) {
                        Log.i(TAG, "from top down pull");
                    }
                    if (fromBottomUpPull) {
                        Log.i(TAG, "from bottom up pull");
                    }*/

                    float distance = Math.abs(currentY - mStartY);

                    float factor = (float) (0.8 * Math.pow(1 - distance / mScreenHeight, 4));
                    /*factor *= 0.25;
                    factor += 0.25;
                    double moveHeight = deltaY * factor;*/

                    float damping = (mScreenHeight - distance) / mScreenHeight;
                    if (currentY - mStartY < 0) {
                        damping = 1 - damping;
                    }
                    damping *= 0.25;
                    damping += 0.25;
                    double moveHeight = deltaY * damping;

                    mMoveHeight += moveHeight;
                    Log.d(TAG, "distance y: " + distance + ", factor: " + factor + ", damping: " + damping
                            + ", moveHeight: " + (deltaY * factor) + "--" + (deltaY * damping) + ", move height: "
                            + mMoveHeight);

                    if (mMode == 2) {
                        if (fromTopDownPull) {
                            if (mMoveHeight < mHeightRefreshLayout) {
                                float percent = 1.0f * mMoveHeight / mHeightRefreshLayout;
                                mTextRefreshing.setScaleX(REFRESHING_START_SCALE + REFRESHING_DELTA_SCALE * percent);
                                mTextRefreshing.setScaleY(mTextRefreshing.getScaleX());
                                mTextRefreshing.setAlpha(REFRESHING_START_ALPHA + REFRESHING_DELTA_ALPHA * percent);

                                mProgressBar.setScaleX(mTextRefreshing.getScaleX());
                                mProgressBar.setScaleY(mTextRefreshing.getScaleX());
                                mProgressBar.setAlpha(mTextRefreshing.getAlpha());

                                mLayoutRefresh.setTranslationY(-mHeightRefreshLayout + mMoveHeight);
                            } else {
                                mLayoutRefresh.setTranslationY((float) (mLayoutRefresh.getTranslationY() + moveHeight * 0.25));
                            }
                        }
                    }

                    if (mMode == 2) {
                        mLayoutContent.setTranslationY(mMoveHeight);
                    } else if (mMode == 1) {
                        mLayoutContent.layout(mRect.left, mRect.top + mMoveHeight, mRect.right,
                                mRect.bottom + mMoveHeight);
                    }

                }
                break;
            case MotionEvent.ACTION_UP:
                if (mMode == 2) {
                    if (mMoveHeight < mHeightRefreshLayout) {
                        this.setRefreshing(false);
                    } else {
                        //mLayoutContent.setTranslationY(mHeightRefreshLayout);
                        this.dampingAnim(mLayoutContent, mLayoutContent.getTranslationY(), mHeightRefreshLayout);

                        //mLayoutRefresh.setTranslationY(-mHeightRefreshLayout + mHeightRefreshLayout);
                        this.dampingAnim(mLayoutRefresh, mLayoutRefresh.getTranslationY(), -mHeightRefreshLayout + mHeightRefreshLayout);
                        if (mOnRefreshListener != null) {
                            mRefreshing = true;
                            mOnRefreshListener.onRefresh();
                        }
                    }
                } else if (mMode == 1) {
                    this.dampAnimation(mLayoutContent);
                    mLayoutContent.layout(mRect.left, mRect.top, mRect.right, mRect.bottom);
                }
                break;
        }
    }

    private final Rect mRect = new Rect();

    private void dampAnimation(View view) {
        TranslateAnimation animation = new TranslateAnimation(0.0f, 0.0f, view.getTop(), mRect.top);
        animation.setDuration(800);
        animation.setFillAfter(true);
        animation.setInterpolator(new DampInterpolator());
        view.setAnimation(animation);
    }

    private void dampingAnim(View view, float start, float end) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(start, end);
        valueAnimator.setDuration(800);
        valueAnimator.setInterpolator(new DampInterpolator());
        valueAnimator.addUpdateListener(animation -> {
            Object object = animation.getAnimatedValue();
            if (object instanceof Float) {
                float value = (float) object;
                //Log.d(TAG, "damp interpolator, value: " + value);
                view.setTranslationY(value);
            }
        });
        valueAnimator.start();
    }

    public void setRefreshing(boolean refreshing) {
        mRefreshing = refreshing;

        if (mRefreshing) {
        } else {
            //mLayoutContent.setTranslationY(0);
            this.dampingAnim(mLayoutContent, mLayoutContent.getTranslationY(), 0);
            //mLayoutRefresh.setTranslationY(-mHeightRefreshLayout);
            this.dampingAnim(mLayoutRefresh, mLayoutRefresh.getTranslationY(), -mHeightRefreshLayout);
        }
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        mOnRefreshListener = onRefreshListener;
    }

    private static class DampInterpolator implements Interpolator {
        @Override
        public float getInterpolation(float input) {
            float ratio = (float) (1 - Math.pow((1 - input), 5));
            float value = 1 - (float) (0.8 * Math.pow(1 - input, 4));
            //Log.v(TAG, "damp interpolator, input: " + input + ", ratio: " + ratio + ", value: " + value);
            return value;
        }
    }

    public interface OnRefreshListener {

        void onRefresh();
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
