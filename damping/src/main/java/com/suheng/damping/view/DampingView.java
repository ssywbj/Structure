package com.suheng.damping.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
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

public class DampingView extends NestedScrollView {
    private static final String TAG = DampingView.class.getSimpleName();
    private int mPreviousY = 0;
    private int mStartY = 0;

    private View mDampingLayout;
    private final Rect mRect = new Rect(); //用于记录childView的初始位置
    private float mMoveHeight; //水平移动搞定距离

    private OnRefreshListener mOnRefreshListener;

    private int mMode;

    private LinearLayout mLinearLayout;
    private TextView mTextView;
    private int mLoadingViewHeight;

    public DampingView(Context context) {
        this(context, null);
    }

    public DampingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DampingView(Context context, AttributeSet attrs, int defStyle) {
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
        if (mMode == 1 || mMode == 2) {
            this.calcDampingArea(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    private void calcDampingArea(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartY = (int) ev.getY();
                mPreviousY = mStartY;

                mRect.set(mDampingLayout.getLeft(), mDampingLayout.getTop(), mDampingLayout.getRight(), mDampingLayout.getBottom());
                mMoveHeight = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                int currentY = (int) ev.getY();
                int deltaY = currentY - mPreviousY;
                mPreviousY = currentY;

                //判定是否在顶部或者滑到了底部
                if ((!mDampingLayout.canScrollVertically(-1) && (currentY - mStartY) > 0)
                        || (!mDampingLayout.canScrollVertically(1) && (currentY - mStartY) < 0)) {
                    //计算阻尼
                    float distance = currentY - mStartY;
                    if (distance < 0) {
                        distance *= -1;
                    }

                    float damping = 0.5f; //阻尼值
                    float height = getHeight();
                    if (height != 0) {
                        if (distance > height) {
                            damping = 0;
                        } else {
                            damping = (height - distance) / height;
                        }
                    }
                    if (currentY - mStartY < 0) {
                        damping = 1 - damping;
                    }

                    damping *= 0.25; //阻力值限制再0.3-0.5之间，平滑过度
                    damping += 0.25;

                    mMoveHeight = mMoveHeight + (deltaY * damping);

                    mDampingLayout.layout(mRect.left, (int) (mRect.top + mMoveHeight), mRect.right,
                            (int) (mRect.bottom + mMoveHeight));
                }
                break;
            case MotionEvent.ACTION_UP:
                this.verticalAnimation(); //开始回移动画
                mDampingLayout.layout(mRect.left, mRect.top, mRect.right, mRect.bottom); //子控件回到初始位置

                mStartY = 0;
                mRect.setEmpty();
                break;
        }
    }

    private void verticalAnimation() {
        TranslateAnimation animation = new TranslateAnimation(0.0f, 0.0f,
                mDampingLayout.getTop(), mRect.top); //上下回弹的动画效果
        animation.setDuration(600);
        animation.setFillAfter(true);
        //设置阻尼动画效果
        animation.setInterpolator(new DampInterpolator());
        mDampingLayout.setAnimation(animation);
    }

    private static class DampInterpolator implements Interpolator {
        @Override
        public float getInterpolation(float input) {
            return (float) (1 - Math.pow((1 - input), 5));
        }
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        mOnRefreshListener = onRefreshListener;
    }

    public interface OnRefreshListener {
        void onRefresh();
    }

}
