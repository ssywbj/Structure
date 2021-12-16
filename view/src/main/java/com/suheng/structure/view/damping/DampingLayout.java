package com.suheng.structure.view.damping;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

import com.suheng.structure.view.R;

public class DampingLayout extends NestedScrollView {
    private static final String TAG = DampingLayout.class.getSimpleName();

    public DampingLayout(@NonNull Context context) {
        super(context);
        this.init();
    }

    public DampingLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public DampingLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    private void init() {
        /*FrameLayout frameLayout = new FrameLayout(getContext());
        addView(frameLayout);*/
        //View.inflate(getContext(), R.layout.damping_layout_refresh_parent,this);
        //View inflate = LayoutInflater.from(getContext()).inflate(R.layout.damping_layout_refresh_parent, this);
        //inflate.setBackgroundColor(Color.RED);
    }

    private ViewGroup mLayoutContent;
    private View mLayoutRefreshTitle;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() != 1) {
            return;
        }

        View child = getChildAt(0);
        ViewGroup.LayoutParams layoutParams = child.getLayoutParams();
        Log.d(TAG, "onFinishInflate: " + layoutParams);
        removeView(child);
        mLayoutContent = new FrameLayout(getContext());
        mLayoutRefreshTitle = View.inflate(getContext(), R.layout.damping_layout_refresh_title, null);
        mLayoutContent.addView(mLayoutRefreshTitle, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT
                , FrameLayout.LayoutParams.WRAP_CONTENT));
        mLayoutContent.addView(child, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(mLayoutContent, layoutParams);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG, "onSizeChanged: w =" + w + ", h = " + h);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        post(() -> {
            int height = mLayoutRefreshTitle.getHeight();
            //mLayoutRefreshTitle.setTranslationY(-height);
            Log.d(TAG, "onAttachedToWindow: " + height);
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

}
