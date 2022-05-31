package com.suheng.structure.view.damping;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.PathInterpolator;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.widget.NestedScrollView;

import com.suheng.structure.view.R;

public class DampingLayout extends NestedScrollView {
    private static final String TAG = DampingLayout.class.getSimpleName();
    private static final float REFRESHING_START_ALPHA = 0;
    private static final float REFRESHING_DELTA_ALPHA = 1 - REFRESHING_START_ALPHA;
    private static final float REFRESHING_START_SCALE = 0.5f;
    private static final float REFRESHING_DELTA_SCALE = 1 - REFRESHING_START_SCALE;

    private View mLayoutRefresh;
    private int mHeightRefreshLayout;
    private ProgressBar mProgressBar;
    private TextView mTextRefreshing;

    private boolean mRefreshing;
    private OnRefreshListener mOnRefreshListener;

    private View mLayoutContent;

    private int mScreenHeight, mMoveHeight;

    public DampingLayout(Context context) {
        super(context);
        this.init();
    }

    public DampingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public DampingLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.init();
    }

    private void init() {
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() != 1) {
            return;
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        //getContext().getDisplay().getRealMetrics(displayMetrics);
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
        mScreenHeight = displayMetrics.heightPixels;
        mLayoutContent = getChildAt(0);

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

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (mLayoutRefresh != null) {
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
                fromTopDownPull = (!mLayoutContent.canScrollVertically(-1) && (currentY - mStartY) > 0);
                fromBottomUpPull = (!mLayoutContent.canScrollVertically(1) && (currentY - mStartY) < 0);
                Log.d(TAG, "fromTopDownPull: " + fromTopDownPull + ", fromBottomUpPull: " + fromBottomUpPull);
                Log.i(TAG, "canScrollVertically-1: " + (!mLayoutContent.canScrollVertically(-1))
                        + ", canScrollVertically1: " + (!mLayoutContent.canScrollVertically(1)));

                if (fromTopDownPull || fromBottomUpPull) {
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
                    /*Log.d(TAG, "distance y: " + distance + ", factor: " + factor + ", damping: " + damping
                            + ", moveHeight: " + (deltaY * factor) + "--" + (deltaY * damping) + ", move height: "
                            + mMoveHeight);*/

                    if (fromTopDownPull) {
                        if (mMoveHeight < mHeightRefreshLayout) {
                            float percent = 1.0f * mMoveHeight / mHeightRefreshLayout;
                            mTextRefreshing.setScaleX(REFRESHING_START_SCALE + REFRESHING_DELTA_SCALE * percent);
                            mTextRefreshing.setScaleY(mTextRefreshing.getScaleX());
                            mTextRefreshing.setAlpha(REFRESHING_START_ALPHA + REFRESHING_DELTA_ALPHA * percent);

                            mProgressBar.setScaleX(mTextRefreshing.getScaleX());
                            mProgressBar.setScaleY(mTextRefreshing.getScaleX());
                            mProgressBar.setAlpha(mTextRefreshing.getAlpha());

                            mLayoutRefresh.setTranslationY((float) (mLayoutRefresh.getTranslationY() + moveHeight));
                        } else {
                            mLayoutRefresh.setTranslationY((float) (mLayoutRefresh.getTranslationY() + moveHeight * 0.5));
                            //mTextRefreshing.setText("松手更新");
                        }
                    }

                    mLayoutContent.setTranslationY((float) (mLayoutContent.getTranslationY() + moveHeight));
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mMoveHeight < mHeightRefreshLayout) {
                    this.setRefreshing(false);
                } else {
                    this.dampingAnim(mLayoutContent, mLayoutContent.getTranslationY(), mHeightRefreshLayout);

                    this.dampingAnim(mLayoutRefresh, mLayoutRefresh.getTranslationY(), -mHeightRefreshLayout + mHeightRefreshLayout);
                    if (mOnRefreshListener != null) {
                        //mProgressBar.start();
                        //mTextRefreshing.setText("正在更新");

                        mRefreshing = true;
                        mOnRefreshListener.onRefresh();
                    }
                }

                break;
        }
    }

    private final Rect mRect = new Rect();

    private void dampingAnim(View view, float start, float end) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(start, end);
        valueAnimator.setDuration(300);
        valueAnimator.setInterpolator(new PathInterpolator(0.25f, 0, 0, 1));
        valueAnimator.addUpdateListener(animation -> {
            Object object = animation.getAnimatedValue();
            if (object instanceof Float) {
                float value = (float) object;
                view.setTranslationY(value);
            }
        });
        valueAnimator.start();
    }

    public void setRefreshing(boolean refreshing) {
        mRefreshing = refreshing;

        if (!mRefreshing) {
            this.dampingAnim(mLayoutContent, mLayoutContent.getTranslationY(), 0);

            final float alpha = mTextRefreshing.getAlpha();
            final float deltaScale = mTextRefreshing.getScaleX() - REFRESHING_START_SCALE;
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(mLayoutRefresh.getTranslationY(), -mHeightRefreshLayout);
            valueAnimator.setDuration(300);
            valueAnimator.setInterpolator(new PathInterpolator(0.25f, 0, 0, 1));
            valueAnimator.addUpdateListener(animation -> {
                Object object = animation.getAnimatedValue();
                if (object instanceof Float) {
                    float value = (float) object;
                    mLayoutRefresh.setTranslationY(value);
                    float ratio = Math.abs(value) / mHeightRefreshLayout;
                    mTextRefreshing.setScaleX(REFRESHING_START_SCALE + deltaScale * (1 - ratio));
                    mTextRefreshing.setScaleY(mTextRefreshing.getScaleX());
                    mTextRefreshing.setAlpha(alpha - alpha * ratio);

                    mProgressBar.setScaleX(mTextRefreshing.getScaleX());
                    mProgressBar.setScaleY(mTextRefreshing.getScaleX());
                    mProgressBar.setAlpha(mTextRefreshing.getAlpha());
                }
            });
            valueAnimator.start();

            //mProgressBar.stop();
            //mTextRefreshing.setText("下拉刷新");
        }
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        mOnRefreshListener = onRefreshListener;
    }

    public void setProgressColor(int color) {
        //mProgressBar.setProgressColor(color);
    }

    public void setTextColor(int color) {
        mTextRefreshing.setTextColor(color);
    }

    public interface OnRefreshListener {

        void onRefresh();
    }

}
