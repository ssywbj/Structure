package com.suheng.photo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.suheng.photo.R;

public class DividerDecoration extends RecyclerView.ItemDecoration {
    private static final String TAG = DividerDecoration.class.getSimpleName();
    public static final int LIST_DIVIDER_DIMEN = 2;//分隔线尺寸
    private Drawable mListDivider;
    private boolean mHideLastDividerLine;

    public DividerDecoration(Context context) {
        mListDivider = context.getResources().getDrawable(R.drawable.recycler_divider_line);
    }

    public DividerDecoration(Context context, boolean hideLastDividerLine) {
        this(context);
        mHideLastDividerLine = hideLastDividerLine;
    }

    /*@Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        Log.i(TAG, "onDraw, parent: " + parent + ", state: " + state);
        this.drawHorizontal(c, parent);
        this.drawVertical(c, parent);
    }

    private void drawHorizontal(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount(), left, right, top, bottom;
        //int intrinsicWidth = mListDivider.getIntrinsicWidth();
        int intrinsicWidth = LIST_DIVIDER_DIMEN;
        //int intrinsicHeight = mListDivider.getIntrinsicHeight();
        int intrinsicHeight = LIST_DIVIDER_DIMEN;
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            left = child.getLeft() - params.leftMargin;
            right = child.getRight() + params.rightMargin + intrinsicWidth;
            top = child.getBottom() + params.bottomMargin;
            bottom = top + intrinsicHeight;
            mListDivider.setBounds(left, top, right, bottom);
            mListDivider.draw(c);
        }
    }

    private void drawVertical(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount(), top, bottom, left, right;
        //int intrinsicWidth = mListDivider.getIntrinsicWidth();
        int intrinsicWidth = LIST_DIVIDER_DIMEN;
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            top = child.getTop() - params.topMargin;
            bottom = child.getBottom() + params.bottomMargin;
            left = child.getRight() + params.rightMargin;
            right = left + intrinsicWidth;

            mListDivider.setBounds(left, top, right, bottom);
            mListDivider.draw(c);
        }
    }

    private boolean isLastColumn(RecyclerView parent, int pos, int spanCount, int childCount) {
        *//*RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            if ((pos + 1) % spanCount == 0) {//如果是最后一列，则不需要绘制右边
                return true;
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager).getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                if ((pos + 1) % spanCount == 0) {// 如果是最后一列，则不需要绘制右边
                    return true;
                }
            } else {
                childCount = childCount - childCount % spanCount;
                if (pos >= childCount) {//如果是最后一列，则不需要绘制右边
                    return true;
                }
            }
        }
        return false;*//*
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        int orientation = OrientationHelper.VERTICAL;
        if (layoutManager instanceof GridLayoutManager) {
            orientation = ((GridLayoutManager) layoutManager).getOrientation();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            // 由于瀑布流布局不是按顺序往多列间排序的，故这里的计算经常会不正确
            orientation = ((StaggeredGridLayoutManager) layoutManager).getOrientation();
        }

        if (orientation == StaggeredGridLayoutManager.VERTICAL) {
            if ((pos + 1) % spanCount == 0)// 如果是最后一列，则不需要绘制右边
                return true;
        } else {
            childCount = childCount - childCount % spanCount;
            if (pos >= childCount)// 如果是最后一列，则不需要绘制右边
                return true;
        }
        return false;
    }

    @Override
    public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
        int spanCount = this.getSpanCount(parent);
        int childCount = parent.getAdapter().getItemCount();
        //int intrinsicWidth = mListDivider.getIntrinsicWidth();
        int intrinsicWidth = LIST_DIVIDER_DIMEN;
        if (this.isLastRaw(parent, itemPosition, spanCount, childCount)) {//如果是最后一行，则不需要绘制底部
            outRect.set(0, 0, intrinsicWidth, 0);
        } else {
            //int intrinsicHeight = mListDivider.getIntrinsicHeight();
            int intrinsicHeight = LIST_DIVIDER_DIMEN;
            if (this.isLastColumn(parent, itemPosition, spanCount, childCount)) {//如果是最后一列，则不需要绘制右边
                outRect.set(0, 0, 0, intrinsicHeight);
            } else {
                outRect.set(0, 0, intrinsicWidth, intrinsicHeight);
            }
        }

        Log.i(TAG, "getItemOffsets, getIntrinsicWidth(): " + mListDivider.getIntrinsicWidth() +
                ", getIntrinsicHeight(): " + mListDivider.getIntrinsicHeight());
    }

    private int getSpanCount(RecyclerView parent) {
        int spanCount = -1;//列数
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            spanCount = ((StaggeredGridLayoutManager) layoutManager).getSpanCount();
        }
        return spanCount;
    }

    private boolean isLastRaw(RecyclerView parent, int pos, int spanCount, int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        int orientation = OrientationHelper.VERTICAL;
        if (layoutManager instanceof GridLayoutManager) {
            orientation = ((GridLayoutManager) layoutManager).getOrientation();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            orientation = ((StaggeredGridLayoutManager) layoutManager).getOrientation();
        }

        if (orientation == OrientationHelper.VERTICAL) {
            childCount = childCount - childCount % spanCount;
            if (pos >= childCount) {//如果是最后一行，则不需要绘制底部
                return true;
            }
        } else {
            if ((pos + 1) % spanCount == 0) {//如果是最后一行，则不需要绘制底部
                return true;
            }
        }
        return false;
    }*/

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        drawHorizontal(c, parent);
        drawVertical(c, parent);
    }

    private int getSpanCount(RecyclerView parent) {
        int spanCount = -1;// 列数
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            spanCount = ((StaggeredGridLayoutManager) layoutManager).getSpanCount();
        }
        return spanCount;
    }

    private void drawHorizontal(Canvas c, RecyclerView parent) {
        View child;
        RecyclerView.LayoutParams params;
        int childCount = parent.getChildCount(), left, right, top, bottom;
        if (mHideLastDividerLine) {
            childCount -= 1;//如果有FooterView，FooterView也会被加上分隔线（测试方法：加粗分隔线尺寸，设置分隔线颜色为深色），所以此处要限制它的绘制
        }
        for (int i = 0; i < childCount; i++) {
            child = parent.getChildAt(i);
            params = (RecyclerView.LayoutParams) child.getLayoutParams();
            left = child.getLeft() - params.leftMargin;
            //int right = child.getRight() + params.rightMargin + mDivider.getIntrinsicWidth();
            right = child.getRight() + params.rightMargin + LIST_DIVIDER_DIMEN;
            top = child.getBottom() + params.bottomMargin;
            //int bottom = top + mDivider.getIntrinsicHeight();
            bottom = top + LIST_DIVIDER_DIMEN;
            mListDivider.setBounds(left, top, right, bottom);
            mListDivider.draw(c);
        }
    }

    private void drawVertical(Canvas c, RecyclerView parent) {
        View child;
        RecyclerView.LayoutParams params;
        int childCount = parent.getChildCount(), top, bottom, left, right;
        if (mHideLastDividerLine) {
            childCount -= 1;//如果有FooterView，FooterView也会被加上分隔线（测试方法：加粗分隔线尺寸，设置分隔线颜色为深色），所以此处要限制它的绘制
        }
        for (int i = 0; i < childCount; i++) {
            child = parent.getChildAt(i);
            params = (RecyclerView.LayoutParams) child.getLayoutParams();
            top = child.getTop() - params.topMargin;
            bottom = child.getBottom() + params.bottomMargin;
            left = child.getRight() + params.rightMargin;
            //right = left + mListDivider.getIntrinsicWidth();
            right = left + LIST_DIVIDER_DIMEN;
            mListDivider.setBounds(left, top, right, bottom);
            mListDivider.draw(c);
        }
    }

    private boolean isLastColumn(RecyclerView parent, int pos, int spanCount, int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        int orientation = OrientationHelper.VERTICAL;
        if (layoutManager instanceof GridLayoutManager) {
            orientation = ((GridLayoutManager) layoutManager).getOrientation();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            // 由于瀑布流布局不是按顺序往多列间排序的，故这里的计算经常会不正确
            orientation = ((StaggeredGridLayoutManager) layoutManager).getOrientation();
        }

        if (orientation == StaggeredGridLayoutManager.VERTICAL) {
            if ((pos + 1) % spanCount == 0) {//如果是最后一列，则不需要绘制右边
                return true;
            }
        } else {
            childCount = childCount - childCount % spanCount;
            if (pos >= childCount) {//如果是最后一列，则不需要绘制右边
                return true;
            }
        }
        return false;
    }

    private boolean isLastRaw(RecyclerView parent, int pos, int spanCount, int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        int orientation = OrientationHelper.VERTICAL;
        if (layoutManager instanceof GridLayoutManager) {
            orientation = ((GridLayoutManager) layoutManager).getOrientation();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            orientation = ((StaggeredGridLayoutManager) layoutManager).getOrientation();
        }

        if (orientation == OrientationHelper.VERTICAL) {
            childCount = childCount - childCount % spanCount;
            if (pos >= childCount) {//如果是最后一行，则不需要绘制底部
                return true;
            }
        } else {
            if ((pos + 1) % spanCount == 0) {//如果是最后一行，则不需要绘制底部
                return true;
            }
        }
        return false;
    }

    @Override
    public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
        int spanCount = getSpanCount(parent);
        int childCount = parent.getAdapter().getItemCount();
        if (this.isLastRaw(parent, itemPosition, spanCount, childCount)) {//如果是最后一行，则不需要绘制底部
            //outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
            outRect.set(0, 0, LIST_DIVIDER_DIMEN, 0);
        } else if (this.isLastColumn(parent, itemPosition, spanCount, childCount)) {//如果是最后一列，则不需要绘制右边
            //outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
            outRect.set(0, 0, 0, LIST_DIVIDER_DIMEN);
        } else {
            //outRect.set(0, 0, mDivider.getIntrinsicWidth(), mDivider.getIntrinsicHeight());
            outRect.set(0, 0, LIST_DIVIDER_DIMEN, LIST_DIVIDER_DIMEN);
        }
    }

}
