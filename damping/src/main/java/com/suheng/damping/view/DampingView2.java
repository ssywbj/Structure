package com.suheng.damping.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class DampingView2 extends NestedScrollView {
    private static final String TAG = DampingView2.class.getSimpleName();

    public DampingView2(Context context) {
        super(context);
    }

    public DampingView2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DampingView2(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onNestedPreScroll(@NonNull @NotNull View target, int dx, int dy, @NonNull @NotNull int[] consumed) {
        super.onNestedPreScroll(target, dx, dy, consumed);
        Log.w(TAG, "onNestedPreScroll, dx: " + dx + ", dy: " + dy + ", consumed: " + Arrays.toString(consumed));
    }

    @Override
    public void onNestedScroll(@NonNull @NotNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);
        Log.w(TAG, "onNestedScroll, dxConsumed: " + dxConsumed + ", dxConsumed: " + dxConsumed + ", dxUnconsumed: " + dxUnconsumed);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull @NotNull View child, @NonNull @NotNull View target, int axes, int type) {
        Log.w(TAG, "onStartNestedScroll, axes: " + axes + ", type: " + type + ", target: " + target);
        return super.onStartNestedScroll(child, target, axes, type);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull @NotNull View child, @NonNull @NotNull View target, int nestedScrollAxes) {
        Log.w(TAG, "onStartNestedScroll, nestedScrollAxes: " + nestedScrollAxes + ", target: " + target);
        return super.onStartNestedScroll(child, target, nestedScrollAxes);
    }

    @Override
    public void onNestedScroll(@NonNull @NotNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        Log.w(TAG, "onNestedScroll, dxConsumed: " + dxConsumed + ", dxConsumed: " + dxConsumed);
    }

    @Override
    public void onNestedScrollAccepted(@NonNull @NotNull View child, @NonNull @NotNull View target, int axes, int type) {
        super.onNestedScrollAccepted(child, target, axes, type);
        Log.e(TAG, "onNestedScrollAccepted, axes: " + axes + ", type: " + type);
    }

    @Override
    public void onNestedScrollAccepted(@NonNull @NotNull View child, @NonNull @NotNull View target, int nestedScrollAxes) {
        super.onNestedScrollAccepted(child, target, nestedScrollAxes);
        Log.e(TAG, "onNestedScrollAccepted, nestedScrollAxes: " + nestedScrollAxes);
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        Log.v(TAG, "onOverScrolled, scrollX: " + scrollX + ", scrollY: " + scrollY + ", clampedX: " + clampedX + ", clampedX: " + clampedY);
    }

    //https://www.cnblogs.com/xlqwe/p/6183492.html
    //https://blog.csdn.net/qq_42944793/article/details/88417127
    //https://www.jianshu.com/p/6547ec3202bd
    @Override
    public boolean onNestedFling(@NonNull View target, float velocityX, float velocityY, boolean consumed) {
        Log.w(TAG, "onNestedFling, velocityX: " + velocityX + ", velocityY: " + velocityY + ", consumed: " + consumed);
        return super.onNestedFling(target, velocityX, velocityY, consumed);
    }

    @Override
    public boolean onNestedPreFling(@NonNull View target, float velocityX, float velocityY) {
        Log.w(TAG, "onNestedPreFling, velocityX: " + velocityX + ", velocityY: " + velocityY);
        return super.onNestedPreFling(target, velocityX, velocityY);
    }

    @Override
    public void fling(int velocityY) {
        super.fling(velocityY);
        Log.w(TAG, "fling, velocityY: " + velocityY );
    }

    /*@Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return super.dispatchNestedFling(velocityX, velocityY, consumed);
    }*/

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        Log.d(TAG, "onScrollChanged, l: " + l + ", t: " + t + ", oldl: " + oldl + ", oldt: " + oldt);
    }

}
