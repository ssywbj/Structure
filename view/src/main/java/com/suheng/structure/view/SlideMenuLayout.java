package com.suheng.structure.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class SlideMenuLayout extends ViewGroup {
    public static final String TAG = SlideMenuLayout.class.getSimpleName();

    public SlideMenuLayout(Context context) {
        super(context);
    }

    public SlideMenuLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d(TAG, "OSSwipeMenuLayout(Context context, AttributeSet attrs)");
    }

    @Override
    protected boolean checkLayoutParams(LayoutParams p) {
        return p instanceof MarginLayoutParams;
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        //返回MarginLayoutParams，再配合后面的measureChildWithMargins方法，让XML中设置的Margin属性生效
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return generateDefaultLayoutParams();
    }

    private int mMenuWidth;

    //https://juejin.cn/post/6844904133489590279
    //https://academy.realm.io/posts/360andev-huyen-tue-dao-measure-layout-draw-repeat-custom-views-and-viewgroups-android/
    //方法执行顺序：onMeasure->onSizeChanged->onLayout，先measure得到尺寸后再layout方位
    //https://github.com/ljphawk/SwipeMenuLayout.git
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //heightMeasureSpec,xxdp：1073741944(但会随dp的多少而已改变),match_parent:1073743338,1073743334, wrap_content:-2147482134, -2147482138
        //heightMode, xxdp、match_parent：MeasureSpec.EXACTLY(1073741824)，wrap_content：MeasureSpec.AT_MOST(-2147483648)
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        Log.i(TAG, "onMeasure: " + widthMeasureSpec + ", " + heightMeasureSpec);
        Log.d(TAG, "onMeasure: " + MeasureSpec.EXACTLY + ", " + MeasureSpec.AT_MOST
                + ", " + MeasureSpec.UNSPECIFIED + "===" + heightMode);
        mMenuWidth = 0;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (i == 0) {
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0); //先对View进行测量
                MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams(); //对View进行测量后，才能拿到它的尺寸
                int widthUsed = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
                int heightUsed = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
                setMeasuredDimension(widthUsed + getPaddingLeft() + getPaddingRight()
                        , heightUsed + getPaddingTop() + getPaddingBottom());
            } else {
                /*if (heightMode == MeasureSpec.AT_MOST) {
                    measureChild(child, widthMeasureSpec, heightMeasureSpec);
                } else {
                    measureChild(child, widthMeasureSpec, heightMeasureSpec);
                }*/
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
                mMenuWidth += child.getMeasuredWidth();
                Log.d(TAG, "mMenuWidth: " + mMenuWidth);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.d(TAG, "onLayout: " + changed + ", " + l + ", " + t + ", " + r + ", " + b);
        int childCount = getChildCount();
        int x = 0, y = 0;
        int b1 = 0;
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (i == 0) {
                MarginLayoutParams layoutParams = (MarginLayoutParams) child.getLayoutParams();
                x = getPaddingLeft() + layoutParams.leftMargin;
                y = getPaddingTop() + layoutParams.topMargin;
                b1 = y + child.getMeasuredHeight();
                child.layout(x, y, x + child.getMeasuredWidth(), b1);
                if (getLayoutDirection() == View.LAYOUT_DIRECTION_LTR) {
                    x += child.getMeasuredWidth() - mMenuWidth;
                }
                //child.bringToFront();
            } else {
                //child.layout(x, y, x + child.getMeasuredWidth(), b1);
                child.layout(x, y+10, x + child.getMeasuredWidth(), y + child.getMeasuredHeight()+10);
                x += child.getMeasuredWidth();
                //child.setVisibility(INVISIBLE);
            }
        }

        /*View child = getChildAt(0);
        MarginLayoutParams layoutParams = (MarginLayoutParams) child.getLayoutParams();
        x = getPaddingLeft() + layoutParams.leftMargin;
        y = getPaddingTop() + layoutParams.topMargin;
        child.layout(x, y, x + child.getMeasuredWidth(), y + child.getMeasuredHeight());*/
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG, "onSizeChanged: " + w + ", " + h);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Log.d(TAG, "draw: " + canvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG, "onDraw: " + canvas);
    }
}
