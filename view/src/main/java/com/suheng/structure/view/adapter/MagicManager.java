package com.suheng.structure.view.adapter;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MagicManager extends RecyclerView.LayoutManager {
    private final String TAG = "MagicManager";
    private int itemHSize = -1;//每个item的高度
    private int itemWSize = -1;//每个item的宽度
    private int hCount = 3;//横向显示的item个数
    private int vCount = 3;//纵向显示item个数
    private int screenItemCount = 0;
    private int mvOffsetCount = 0;

    private int mFirsItemPosition = 0;
    private int mLastItemPosition = 0;
    private int mCurrentOffset = 0;
    private int totalOffset = 0;
    private int mSetOffset = 0;//小球的位移距离
    private float minScale = 0.28f;//最小的缩放倍数
    private int smallCircleTopToScreenOffset;
    private int bottomSmallCircleTopOffset;//底部小圆的top
    private int withSmallCircleDist;//大圆与小圆的距离
    private int innerPaddingLeft;
    private int innerPaddingTop;
    private int innerHorizontalInterval;

    public MagicManager() {
        this(3, 3);
    }

    public MagicManager(int hCount, int vCount) {
        this.hCount = hCount;
        this.vCount = vCount;
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (state.getItemCount() == 0) {
            removeAndRecycleAllViews(recycler);
            return;
        }
        detachAndScrapAttachedViews(recycler);

        mLastItemPosition = getItemCount() - 1;
        itemWSize = getHorizontalSpace() / hCount;

        if (vCount == 0) {//如果没有设置横向个数,就需要动态计算,以宽度作为item的高度计算出个数
            vCount = (getVerticalSpace() - 2 * mSetOffset) / itemWSize;
            mSetOffset = (int) (getVerticalSpace() / (vCount + 1) / 3);
            itemHSize = (getVerticalSpace() - 2 * mSetOffset) / vCount;
        } else {
            mSetOffset = (int) (getVerticalSpace() / (vCount + 1) / 3);//计算小球的位移,因为小球是缩放了0.3f,将高度划分为vCount + 1份空间,而上下两边的小球一共占用一份空间,所以小球的位移距离就是每份空间高度乘以minScale
            itemHSize = (getVerticalSpace() - 2 * mSetOffset) / vCount;//mSetOffset是位移的距离,同时也是小球的高度,先把上下小球的高度去除,再用剩余的高度计算正常item的高度
        }
        //为了保证是正方形
        innerPaddingTop = 0;
        innerPaddingLeft = 0;
        if (itemHSize > itemWSize){
            itemHSize = itemWSize;
            innerPaddingTop = (int) ((getVerticalSpace() - itemHSize * vCount - 2 * itemHSize * minScale) / 2);
        } else {
            itemWSize = itemHSize;
//            innerPaddingLeft = (getHorizontalSpace() - itemWSize * hCount) / 2;
            innerHorizontalInterval = (getHorizontalSpace() - itemWSize * hCount) / hCount;
            innerPaddingLeft = innerHorizontalInterval / 2;
        }
        smallCircleTopToScreenOffset = (int) -(itemHSize / 2 - itemHSize * minScale / 2);
        bottomSmallCircleTopOffset = getVerticalSpace() - (itemHSize + smallCircleTopToScreenOffset);
        withSmallCircleDist = Math.abs(mSetOffset - smallCircleTopToScreenOffset);
        if (screenItemCount == 0) {
            screenItemCount = hCount * vCount;
        }

        mvOffsetCount = (int) Math.ceil((getItemCount() + 0f) / hCount);
        int screenOffsetCount = (screenItemCount) / hCount;
        if (mvOffsetCount > screenOffsetCount) {
            mvOffsetCount = mvOffsetCount - screenOffsetCount;
        } else {
            mvOffsetCount = 0;
        }
        totalOffset = mvOffsetCount * itemHSize + itemHSize - 1;

        fill(recycler, state, 0);
        recycleChildren(recycler);
    }


    //添加子view
    private void addChild(View child, int l, int t, int r, int b, float scf) {
        addView(child);
        measureChildWithMargins(child, getHorizontalSpace() - (r - l), getVerticalSpace() - (b - t));
        if (scf > 1f) {
            scf = 1f;
        }
        if (scf < minScale) {
            scf = minScale;
        }
        child.setScaleX(scf);
        child.setScaleY(scf);
        layoutDecoratedWithMargins(child, l, t, r, b);
    }

    @Nullable
    @Override
    public View onInterceptFocusSearch(@NonNull View focused, int direction) {
        return super.onInterceptFocusSearch(focused, direction);
    }

    private int fill(RecyclerView.Recycler recycler, RecyclerView.State state, int dy) {
        detachAndScrapAttachedViews(recycler);

        int fs = (int) Math.floor(Math.abs(mCurrentOffset) / itemHSize) * hCount;
        if (fs <= getItemCount() - 1) {
            mFirsItemPosition = fs;
        }else {
            mFirsItemPosition = 0;
        }
        int LastItemPosition = mFirsItemPosition + screenItemCount - 1 + hCount;
        if (LastItemPosition > getItemCount() - 1) {
            LastItemPosition = getItemCount() - 1;
        }
        mLastItemPosition = LastItemPosition;

        int buttomItemCount = (mLastItemPosition + 1) % hCount;
        if (buttomItemCount == 0) {
            buttomItemCount = hCount;
        }
        int leftOffset = getAllPaddingLeft();
        int lastOffset = (vCount - 1) * itemHSize + mSetOffset;
        float itemOffset = (mCurrentOffset + 0f) % itemHSize;
        float frac = itemOffset / itemHSize;
        if (mCurrentOffset >= totalOffset) {
            frac = 1f;
        }
        int scrollY = (int) (itemHSize * frac);
        int viewTopOffset = getAllPaddingTop() + mSetOffset;
        viewTopOffset -= scrollY;//偏移量


        //顶部小item
        if (mFirsItemPosition >= hCount) {

            for (int k = mFirsItemPosition - hCount; k < mFirsItemPosition; k++) {
                View child = recycler.getViewForPosition(k);
                addChild(child, leftOffset, smallCircleTopToScreenOffset, leftOffset + itemWSize, smallCircleTopToScreenOffset + itemHSize, minScale);
                leftOffset += (itemWSize + innerHorizontalInterval);
            }
        }

        //画底部小圆
        leftOffset = getAllPaddingLeft();
        int startButItemPosition = 0;
        int endButItemPosition = 0;
        if (mLastItemPosition + hCount < getItemCount()) {
            startButItemPosition = mLastItemPosition + 1;
            endButItemPosition = mLastItemPosition + hCount;
        } else {
            startButItemPosition = mLastItemPosition + 1;
            endButItemPosition = getItemCount() - 1;
        }
        if (startButItemPosition != 0 && endButItemPosition != 0) {
            for (int i = mLastItemPosition + 1; i < getItemCount(); i++) {
                View child = recycler.getViewForPosition(i);
                addChild(child, leftOffset, bottomSmallCircleTopOffset, leftOffset + itemWSize, bottomSmallCircleTopOffset + itemHSize, minScale);
                leftOffset += (itemWSize + innerHorizontalInterval);
                //把多余的item移到屏幕外
                if (leftOffset >= (getAllPaddingLeft() + itemWSize * hCount - 1)){
                    leftOffset = getWidth();
                }
            }
        }

        //下滑，从底部开始布局
        if (mCurrentOffset < 0 && mLastItemPosition >= (vCount - 1) * hCount) {
            for (int i = mLastItemPosition; i >= 0; i--) {
                View child = recycler.getViewForPosition(i);
                int iwCount = i % hCount + 1;//有几个宽度
                int l = iwCount * itemWSize + getAllPaddingLeft() - itemWSize + (iwCount - 1) * innerHorizontalInterval;
                int r = iwCount * itemWSize + getAllPaddingLeft() + (iwCount - 1) * innerHorizontalInterval;
                int ihCount = i / hCount + 0;//在第几行

                if (i >= vCount * hCount) {
                    addChild(child, l, bottomSmallCircleTopOffset, r, bottomSmallCircleTopOffset + itemHSize, minScale);
                } else if (i >= (vCount - 1) * hCount) {
                    int interceptOffset = lastOffset + viewTopOffset - mSetOffset;
                    if (interceptOffset > bottomSmallCircleTopOffset) {
                        interceptOffset = bottomSmallCircleTopOffset;
                    }
                    float realScale = 1f - (1f - minScale) * (interceptOffset - lastOffset) / withSmallCircleDist;
                    addChild(child, l, interceptOffset, r, interceptOffset + itemHSize, realScale);

                } else {
                    addChild(child, l, ihCount * itemHSize + viewTopOffset, r, (ihCount + 1) * itemHSize + viewTopOffset, 1f);
                }
            }
            return dy;
        }

        leftOffset = getHorizontalSpace() + getAllPaddingLeft();
        for (int i = mFirsItemPosition; i <= mLastItemPosition; i++) {
            View child = recycler.getViewForPosition(i);
            int iwCount = i % hCount + 1;//有几个宽度
            int l = iwCount * itemWSize + getAllPaddingLeft() - itemWSize + (iwCount - 1) * innerHorizontalInterval;
            int r = iwCount * itemWSize + getAllPaddingLeft() + (iwCount - 1) * innerHorizontalInterval;
            if (i - mFirsItemPosition < hCount) {
                //第一行
                int oneLineTopOffset = 0;
                float realScale = 1f - (1f - minScale) * frac;
                if (viewTopOffset < mSetOffset) {
                    realScale = 1f - (1f - minScale) * (mSetOffset - viewTopOffset) / withSmallCircleDist; //计算出滑动到小item的百分比
                }
                oneLineTopOffset = viewTopOffset;
                if (viewTopOffset <= smallCircleTopToScreenOffset) {
                    oneLineTopOffset = smallCircleTopToScreenOffset;//上滑到此停住
                }
                addChild(child, l, oneLineTopOffset, l + itemWSize, oneLineTopOffset + itemHSize, realScale);
            } else if (i > mLastItemPosition - buttomItemCount && i >= screenItemCount) {
                //画底部小圆，下滑时秒变正常大小item的最后一行，要注意
                float realScale = minScale + (1f - minScale) * frac;

                if (viewTopOffset < lastOffset) {
                    //计算出滑动到小item的百分比
                    realScale = minScale + (1f - minScale) * (lastOffset - viewTopOffset + smallCircleTopToScreenOffset) / withSmallCircleDist;
                }
                int myTopoffset = bottomSmallCircleTopOffset;//固定住底部小item
                if (viewTopOffset <= bottomSmallCircleTopOffset - itemHSize) {//上滑时跟随上滑
                    myTopoffset = viewTopOffset + itemHSize;
                }
                if (myTopoffset > bottomSmallCircleTopOffset) {//下滑时固定
                    myTopoffset = bottomSmallCircleTopOffset;
                }
                if (mCurrentOffset > totalOffset - itemHSize - 1) {//最后一行正常大小显示
                    myTopoffset = viewTopOffset + itemHSize;
                    realScale = 1f;

                }
                addChild(child, l, myTopoffset, r, myTopoffset + itemHSize, realScale);

            } else { //正常大小item的第二行到倒数第二行
                if (leftOffset + itemWSize <= getHorizontalSpace() + getAllPaddingLeft()) { //当前行还排列的下
                    addChild(child, leftOffset, viewTopOffset, leftOffset + itemWSize, viewTopOffset + itemHSize, 1f);
                    leftOffset += (itemWSize + innerHorizontalInterval);
                } else {
                    leftOffset = getAllPaddingLeft();
                    viewTopOffset += itemHSize;
                    addChild(child, leftOffset, viewTopOffset, leftOffset + itemWSize, viewTopOffset + itemHSize, 1f);
                    leftOffset += (itemWSize + innerHorizontalInterval);
                }
            }

        }
        return dy;
    }


    /**
     * 回收屏幕外需回收的Item
     */
    private void recycleChildren(RecyclerView.Recycler recycler) {
        List<RecyclerView.ViewHolder> scrapList = recycler.getScrapList();
        for (int i = 0; i < scrapList.size(); i++) {
            RecyclerView.ViewHolder holder = scrapList.get(i);
            removeView(holder.itemView);
            recycler.recycleView(holder.itemView);
        }
    }


    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (dy == 0 || getChildCount() == 0) {
            return 0;
        }
        int realOffset = dy;//实际滑动的距离

        mCurrentOffset += realOffset;//累加实际滑动距离
        if (mCurrentOffset <= -itemHSize) {
            realOffset = 0;
            mCurrentOffset = (int) -itemHSize + 1;
        }
        if (mCurrentOffset >= totalOffset) {
            mCurrentOffset = totalOffset - 1;
            realOffset = 0;
        }
        fill(recycler, state, realOffset);
        return realOffset;
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        return super.scrollHorizontallyBy(dx, recycler, state);
    }

    @Override
    public boolean canScrollHorizontally() {
        return false;
    }

    @Override
    public boolean canScrollVertically() {
        return true;
    }


    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        switch (state) {
            case RecyclerView.SCROLL_STATE_DRAGGING://手指触摸屏幕
                cancelAnmationtor();
                break;
            case RecyclerView.SCROLL_STATE_IDLE://列表停止滚动后
                smoothScrollToPosition(findFirstPosition());
                break;
            default:
                break;
        }
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        smoothScrollToPosition(position);
    }

    @Override
    public void scrollToPosition(int position) {
        mCurrentOffset += getScrollToPositionOffset(position);
        if (mCurrentOffset >= totalOffset) {
            mCurrentOffset = totalOffset - 1;
        }
        requestLayout();
    }

    private int getScrollToPositionOffset(int position) {
        position = (int) Math.ceil((position + 0f) / hCount);
        return position * itemHSize - Math.abs(mCurrentOffset);
    }

    public int findFirstPosition() {
        if (itemHSize == 0) {
            return -1;
        }
        int remainder = -1;
        remainder = Math.abs(mCurrentOffset) % itemHSize;
        if (remainder > itemHSize / 2.0f) {
            //下一项
            if (mFirsItemPosition + hCount <= getItemCount() - 1) {
                return mFirsItemPosition + hCount;
            }
        }

        return mFirsItemPosition;
    }

    public int findLastPosition() {
        if (itemHSize == 0) {
            return -1;
        }
        int remainder = -1;
        remainder = Math.abs(mCurrentOffset) % itemHSize;
        if (remainder > itemHSize / 2.0f) {
            //下一项
            if (mFirsItemPosition + hCount <= getItemCount() - 1) {
                return mFirsItemPosition + hCount + hCount * vCount - 1;
            }
        }

        return mFirsItemPosition + hCount * vCount - 1;
    }



    private void smoothScrollToPosition(int position) {
        if (position >= 0 && position < getItemCount()) {
            startValueAnimation(position);
        }
    }

    /**
     * 自动选中动画
     */
    private ValueAnimator selectAnimator;
    private long autoSelectMinDuration = 100;
    private long autoSelectMaxDuration = 150;
    private float distance;

    private void startValueAnimation(int position) {
        cancelAnmationtor();
        distance = getScrollToPositionOffset(position);
        if (mCurrentOffset < 0) {
            distance = mCurrentOffset;
        } else if (mCurrentOffset > totalOffset - itemHSize) {
            distance = -(mCurrentOffset - (totalOffset - itemHSize));
        }
        long minDuration = autoSelectMinDuration;
        long maxDuration = autoSelectMaxDuration;
        long duration;
        float distanceFraction = Math.abs(distance) / itemHSize;
        if (distance <= itemHSize) {
            duration = (long) (minDuration + (maxDuration - minDuration) * distanceFraction);
        } else {
            duration = (long) (maxDuration * distanceFraction);
        }
        selectAnimator = ValueAnimator.ofFloat(0.0f, distance).setDuration(duration);
        selectAnimator.setInterpolator(new LinearInterpolator());
        final float anstartOffset = mCurrentOffset;
        selectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (mCurrentOffset < 0) {
                    mCurrentOffset = (int) Math.floor(anstartOffset - (float) animation.getAnimatedValue());
                    if (mCurrentOffset >= totalOffset) {
                        mCurrentOffset = totalOffset - 1;
                    }
                } else {
                    mCurrentOffset = (int) Math.ceil(anstartOffset + (float) animation.getAnimatedValue());
                    if (mCurrentOffset >= totalOffset) {
                        mCurrentOffset = totalOffset - 1;
                    }
                }
                requestLayout();
            }
        });
        selectAnimator.start();
    }

    public void cancelAnmationtor() {
        if (selectAnimator != null && (selectAnimator.isStarted() || selectAnimator.isRunning())) {
            selectAnimator.cancel();
        }
    }

    /**
     * 获取内容的高度
     *
     * @return
     */
    public int getVerticalSpace() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }

    private int getAllPaddingTop(){
        return getPaddingTop() + innerPaddingTop;
    }

    /**
     * 获取内容的宽度
     *
     * @return
     */
    public int getHorizontalSpace() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

    private int getAllPaddingLeft(){
        return getPaddingLeft() + innerPaddingLeft;
    }
}
