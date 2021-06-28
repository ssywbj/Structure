package com.suheng.damping.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;

import androidx.core.widget.NestedScrollView;

public class DampingView extends NestedScrollView {
    private int mPreviousY, mStartY;

    private View mChildView;
    private final Rect mRect = new Rect();
    private float mMoveHeight;

    public DampingView(Context context) {
        this(context, null);
    }

    public DampingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DampingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() != 1) {
            return;
        }

        mChildView = getChildAt(0);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mChildView == null) {
            return super.dispatchTouchEvent(ev);
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartY = (int) ev.getY();
                mPreviousY = mStartY;

                mRect.set(mChildView.getLeft(), mChildView.getTop(), mChildView.getRight(), mChildView.getBottom());
                mMoveHeight = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                int currentY = (int) ev.getY();
                int deltaY = currentY - mPreviousY;
                mPreviousY = currentY;

                //判定是否在顶部、底部开始操作
                if ((!mChildView.canScrollVertically(-1) && (currentY - mStartY) > 0)
                        || (!mChildView.canScrollVertically(1) && (currentY - mStartY) < 0)) {
                    //计算阻尼
                    float distance = Math.abs(currentY - mStartY);

                    float damping; //阻尼值
                    float height = getHeight();
                    if (distance > height) {
                        damping = 0;
                    } else {
                        damping = (height - distance) / height;
                    }
                    if (currentY - mStartY < 0) {
                        damping = 1 - damping;
                    }

                    damping *= 0.25;
                    damping += 0.25;

                    mMoveHeight = mMoveHeight + (deltaY * damping);

                    mChildView.layout(mRect.left, (int) (mRect.top + mMoveHeight), mRect.right,
                            (int) (mRect.bottom + mMoveHeight));
                }
                break;
            case MotionEvent.ACTION_UP:
                this.restoreAnim(); //回移动画
                mChildView.layout(mRect.left, mRect.top, mRect.right, mRect.bottom); //子控件回到初始位置

                mStartY = 0;
                mRect.setEmpty();
                break;
        }

        return super.dispatchTouchEvent(ev);
    }

    private void restoreAnim() {
        TranslateAnimation animation = new TranslateAnimation(0.0f, 0.0f,
                mChildView.getTop(), mRect.top);
        animation.setDuration(600);
        animation.setFillAfter(true);
        animation.setInterpolator(new DampInterpolator());
        mChildView.setAnimation(animation);
    }

    private static class DampInterpolator implements Interpolator {
        @Override
        public float getInterpolation(float input) {
            return (float) (1 - Math.pow((1 - input), 5));
        }
    }

}
