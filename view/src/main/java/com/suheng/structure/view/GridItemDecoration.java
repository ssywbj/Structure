package com.suheng.structure.view;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class GridItemDecoration extends RecyclerView.ItemDecoration {

    private final int mSpace;
    private final GridLayoutManager mLayoutManager;
    private int mColor;

    public GridItemDecoration(GridLayoutManager layoutManager, int space) {
        mLayoutManager = layoutManager;
        mSpace = space;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(mSpace, mSpace, mSpace, mSpace);
    }

    /*@Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int right = 0, left = 0, top = 0;
        int spanCount = mLayoutManager.getSpanCount();
        int position = parent.getChildAdapterPosition(view);
        //Log.d("Wbj", "position: " + position + ", spanCount: " + spanCount);
        if (position / spanCount > 0) {
            top = mSpace;
        }

        int perOffset = mSpace / 3;
        if (position % spanCount == 0) { //第一列
            //Log.i("Wbj", "position, 第一列");
            right = perOffset * 2;
        } else if ((position + 1) % spanCount == 0) { //最后一列
            //Log.v("Wbj", "position, 最后一列");
            left = perOffset * 2;
        } else { //中间各列
            //Log.d("Wbj", "position, 中间各列");
            left = perOffset;
            right = left;
        }
        //https://juejin.cn/post/6844904116859174926
        *//*int column = position % spanCount;
        left = column * mSpace / spanCount; //column * (列间距 * (1f / 列数))
        right = mSpace - (column + 1) * mSpace / spanCount; //列间距 - (column + 1) * (列间距 * (1f /列数))*//*
        outRect.set(left, top, right, 0);

        //Log.i("Wbj", "outRect: " + outRect.left + ", " + outRect.right);
    }*/

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);
        //c.drawColor(mColor);
        //mDivider.draw(c);

        /*drawVertical(c, parent);
        drawHorizontal(c, parent);*/
    }

    private Drawable mDivider;
    private final Rect mBounds = new Rect();

    public void setColor(int color) {
        mColor = color;
        mDivider = new ColorDrawable(mColor);
        //mDivider.setBounds(0, 0, mSpace, mSpace);
    }

    private void drawVertical(Canvas canvas, RecyclerView parent) {
        canvas.save();
        final int left;
        final int right;
        if (parent.getClipToPadding()) {
            left = parent.getPaddingLeft();
            right = parent.getWidth() - parent.getPaddingRight();
            canvas.clipRect(left, parent.getPaddingTop(), right, parent.getHeight() - parent.getPaddingBottom());
        } else {
            left = 0;
            right = parent.getWidth();
        }

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            parent.getDecoratedBoundsWithMargins(child, mBounds);
            final int bottom = mBounds.bottom + Math.round(child.getTranslationY());
            final int top = bottom - mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(canvas);
        }
        canvas.restore();
    }

    private void drawHorizontal(Canvas canvas, RecyclerView parent) {
        canvas.save();
        final int top;
        final int bottom;
        if (parent.getClipToPadding()) {
            top = parent.getPaddingTop();
            bottom = parent.getHeight() - parent.getPaddingBottom();
            canvas.clipRect(parent.getPaddingLeft(), top, parent.getWidth() - parent.getPaddingRight(), bottom);
        } else {
            top = 0;
            bottom = parent.getHeight();
        }

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            parent.getDecoratedBoundsWithMargins(child, mBounds);
            final int right = mBounds.right + Math.round(child.getTranslationX());
            final int left = right - mDivider.getIntrinsicWidth();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(canvas);
        }
        canvas.restore();
    }

}
