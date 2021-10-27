package com.suheng.structure.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

public class CustomLayout extends ViewGroup {
    public static final String TAG = CustomLayout.class.getSimpleName();
    private TextView mTextLeft, mTextRight;

    public CustomLayout(Context context) {
        super(context);
    }

    public CustomLayout(Context context, AttributeSet attrs) {
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

    //https://juejin.cn/post/6844904133489590279
    //https://academy.realm.io/posts/360andev-huyen-tue-dao-measure-layout-draw-repeat-custom-views-and-viewgroups-android/
    //方法执行顺序：onMeasure->onSizeChanged->onLayout，先measure得到尺寸后再layout方位
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mTextLeft == null) {
            mTextLeft = findViewById(R.id.text_left);
            mTextRight = findViewById(R.id.text_right);
        }
        Log.d(TAG, "onMeasure: " + widthMeasureSpec + ", " + heightMeasureSpec);
        //measureChild(mTextLeft, widthMeasureSpec, heightMeasureSpec); //XML中设置的Margin属性不算在测量范围内
        //measureChildWithMargins：把在XML中设置的Margin属性算在测量范围内，让其生效
        measureChildWithMargins(mTextLeft, widthMeasureSpec, 0, heightMeasureSpec, 0); //先对View进行测量
        MarginLayoutParams lp = (MarginLayoutParams) mTextLeft.getLayoutParams(); //对View进行测量后，才能拿到它的尺寸
        int widthUsed = mTextLeft.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;

        //measureChild(mTextRight, widthMeasureSpec, heightMeasureSpec);
        measureChildWithMargins(mTextRight, widthMeasureSpec, widthUsed, heightMeasureSpec, 0);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.d(TAG, "onLayout: " + changed + ", " + l + ", " + t + ", " + r + ", " + b);
        MarginLayoutParams layoutParams = (MarginLayoutParams) mTextLeft.getLayoutParams();
        int x = getPaddingLeft() + layoutParams.leftMargin;
        int y = getPaddingTop() + layoutParams.topMargin;
        mTextLeft.layout(x, y, x += mTextLeft.getMeasuredWidth(), y + mTextLeft.getMeasuredHeight());
        x += layoutParams.rightMargin;

        layoutParams = (MarginLayoutParams) mTextRight.getLayoutParams();
        x += getPaddingLeft() + layoutParams.leftMargin;
        y = getPaddingTop() + layoutParams.topMargin;
        mTextRight.layout(x, y, x + mTextRight.getMeasuredWidth(), y + mTextRight.getMeasuredHeight());
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
