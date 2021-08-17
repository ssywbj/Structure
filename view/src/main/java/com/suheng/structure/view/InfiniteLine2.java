package com.suheng.structure.view;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.fonts.FontStyle;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Scroller用法
 * 1.初始化Scroller
 * 2.调用startScroll()开始滚动
 * 3.执行invalidate()刷新界面
 * 4.重写View的computeScroll()并在其内部实现与滚动相关的业务逻辑
 * 5.再次执行invalidate()刷新界面
 */
public class InfiniteLine2 extends View {
    private int mIndex = 0;
    private Paint mPaint;
    private int mWidth;
    private Scroller mScroller;
    private int mDownX, mLastMoveX;

    public InfiniteLine2(Context context) {
        this(context, null);
    }

    public InfiniteLine2(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InfiniteLine2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setBackgroundColor(Color.WHITE);
        mScroller = new Scroller(getContext());

        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        mPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 22, metrics));
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d("Wbj", "onConfigurationChanged: " + newConfig);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        Log.d("Wbj", "onWindowFocusChanged: " + hasWindowFocus);
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE) {
            boolean checked = this.isBoldTextAdjustment();
            if (checked) {
                Log.d("Wbj", "粗休");
                mPaint.setTypeface(Typeface.DEFAULT_BOLD);
            } else {
                Log.d("Wbj", "非粗体");
                mPaint.setTypeface(Typeface.DEFAULT);
            }
        }
    }

    private boolean isBoldTextAdjustment2() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            int boldTextAdjustment = FontStyle.FONT_WEIGHT_BOLD - FontStyle.FONT_WEIGHT_NORMAL;
            int fontWeightAdjustment = Settings.Secure.getInt(getContext().getContentResolver(),
                    "font_weight_adjustment", 0); //Android S新特性：粗体文字
            return fontWeightAdjustment == boldTextAdjustment;
        } else {
            return false;
        }
    }

    private boolean isBoldTextAdjustment() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            Configuration configuration = getResources().getConfiguration();
            int boldTextAdjustment = FontStyle.FONT_WEIGHT_BOLD - FontStyle.FONT_WEIGHT_NORMAL;
            return configuration.fontWeightAdjustment == boldTextAdjustment; //Android S新特性：粗体文字
        } else {
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = (int) event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = (int) (mDownX - event.getX()) + mLastMoveX;
                smoothScrollTo(dx, 0);
                break;
            case MotionEvent.ACTION_UP:
                int ddx = (int) (event.getX() - mDownX);
                if (ddx < 0 && Math.abs(ddx) > 100) {
                    mIndex++;
                } else if (ddx > 0 && Math.abs(ddx) > 100) {
                    mIndex--;
                }
                smoothScrollTo(mWidth * mIndex, 0);
                mLastMoveX = mIndex * mWidth;
                break;
        }
        return true;
    }

    //调用此方法滚动到目标位置
    public void smoothScrollTo(int fx, int fy) {
        int dx = fx - mScroller.getFinalX();
        int dy = fy - mScroller.getFinalY();
        Log.d("Wbj", "fx: " + fx + ", fy: " + fy + "----dx: " + dx + ", dy: " + dy);
        Log.d("Wbj", "final, x: " + mScroller.getFinalX() + ", y: " + mScroller.getFinalY());
        smoothScrollBy(dx, dy);
    }

    //调用此方法设置滚动的相对偏移
    public void smoothScrollBy(int dx, int dy) {
        //设置mScroller的滚动偏移量
        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx, dy, 500);
        invalidate();//这里必须调用invalidate()才能保证computeScroll()会被调用，否则不一定会刷新界面，看不到滚动效果
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {//判断View的滚动是否在继续
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mWidth = getMeasuredWidth();
        drawText(canvas, (mIndex - 1) * mWidth, mIndex - 1);
        drawText(canvas, mIndex * mWidth, mIndex);
        drawText(canvas, (mIndex + 1) * mWidth, mIndex + 1);
    }

    private void drawText(Canvas canvas, int startX, int index) {
        canvas.save();
        canvas.translate(startX, 0);
        canvas.drawText("页数：" + index, 0, 100, mPaint);
        canvas.restore();
        Log.d("Wbj", "页数：" + index);
    }

}
