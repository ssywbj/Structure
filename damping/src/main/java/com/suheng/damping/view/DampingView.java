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
    private int mPreviousY = 0;
    private int mStartY = 0;

    private View mChildView;
    private final Rect mRect = new Rect(); //用于记录childView的初始位置
    private float mMoveHeight; //水平移动搞定距离

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
        if (getChildCount() > 0) {
            mChildView = getChildAt(0);
        }
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

                //判定是否在顶部或者滑到了底部
                if ((!mChildView.canScrollVertically(-1) && (currentY - mStartY) > 0)
                        || (!mChildView.canScrollVertically(1) && (currentY - mStartY) < 0)) {
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

                    mChildView.layout(mRect.left, (int) (mRect.top + mMoveHeight), mRect.right,
                            (int) (mRect.bottom + mMoveHeight));
                }
                break;
            case MotionEvent.ACTION_UP:
                this.verticalAnimation(); //开始回移动画
                mChildView.layout(mRect.left, mRect.top, mRect.right, mRect.bottom); //子控件回到初始位置

                mStartY = 0;
                mRect.setEmpty();
                break;
        }

        return super.dispatchTouchEvent(ev);
    }

    private void verticalAnimation() {
        TranslateAnimation animation = new TranslateAnimation(0.0f, 0.0f,
                mChildView.getTop(), mRect.top); //上下回弹的动画效果
        animation.setDuration(600);
        animation.setFillAfter(true);
        //设置阻尼动画效果
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
