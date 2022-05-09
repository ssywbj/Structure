package com.suheng.structure.view.temp2.manager;

import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.suheng.structure.view.temp2.listener.OverScrollListener;

/**
 * Created by shichaohui on 2015/8/3 0003.
 * <br/>
 */
public class AnimRFGridLayoutManager extends GridLayoutManager {

    private OverScrollListener mListener;

    public AnimRFGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public AnimRFGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public AnimRFGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int scrollRange = super.scrollVerticallyBy(dy, recycler, state);

        mListener.overScrollBy(dy - scrollRange);

        return scrollRange;
    }

    /**
     * 设置滑动过度监听
     *
     * @param listener
     */
    public void setOverScrollListener(OverScrollListener listener) {
        mListener = listener;
    }

}
