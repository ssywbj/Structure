package com.suheng.structure.view.adapter;

import android.animation.PointFEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Point;
import android.graphics.PointF;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HoneycombLayoutManager extends RecyclerView.LayoutManager {
    private static final String TAG = "HoneycombLayoutManager";

    private int itemSizeW, itemSizeH;

    private float ITEM_IN_WINDOW_PROPORTION = 0.3F;

    private float FIRST_SCALE = 1.0f;
    private float SECOND_SCALE = 0.9f;
    private float THIRD_SCALE = 0.8f;
    private float FOURTh_SCALE = 0.7f;
    private float FIFTH_SCALE = 0.3f;

    private int centerX, centerY;
    private int scrollX = Integer.MAX_VALUE;//记录每次滑动的位移量
    private int scrollY = Integer.MAX_VALUE;//记录每次滑动的位移量

    private ValueAnimator selectAnimator;
    private long autoSelectMinDuration = 100;
    private long autoSelectMaxDuration = 250;
    private float distanceX;
    private float distanceY;

    private int originalOffsetAngle = 0;//默认偏移的角度

    private int offsetX = Integer.MAX_VALUE;//默认偏移量
    private int offsetY = Integer.MAX_VALUE;//默认偏移量


    private List<ChildRect> childRecfRecord = new ArrayList<>();//记录每个item的位置和scale

    public HoneycombLayoutManager() {
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (state.getItemCount() == 0) {
            removeAndRecycleAllViews(recycler);
            return;
        }
        detachAndScrapAttachedViews(recycler);
        centerX = getHorizontalSpace() / 2;
        centerY = getVerticalSpace() / 2;
        if (offsetX == Integer.MAX_VALUE || offsetY == Integer.MAX_VALUE) {
            offsetX = centerX;
            offsetY = centerY;
        }
        itemSizeW = (int) (getHorizontalSpace() * ITEM_IN_WINDOW_PROPORTION);
        itemSizeH = (int) (getVerticalSpace() * ITEM_IN_WINDOW_PROPORTION);
        if (itemSizeW > itemSizeH) {
            itemSizeW = itemSizeH;
        } else {
            itemSizeH = itemSizeW;
        }
        if (getVerticalSpace() > getHorizontalSpace()) {
            originalOffsetAngle = 0;
        } else {
            originalOffsetAngle = 0;
        }

        calculateChild(recycler, state, 0, 0);
        int count = childRecfRecord.size();
        for (int i = 0; i < count; i++) {
            View child = recycler.getViewForPosition(i);
            ChildRect childRect = childRecfRecord.get(i);
            addChild(child, childRect);
        }
        recycleChildren(recycler);
    }

    private void calculateChild(RecyclerView.Recycler recycler, RecyclerView.State state, int dx, int dy) {
        childRecfRecord.clear();
        int totalCount = getItemCount();

        int currentRound = 0;
        int radius = 0;
        while (totalCount > 0) {
            int count = currentRound * 6;//这一圈的个数
            if (count == 0)
                count = 1;

            double angle = 360f / count;

            if (count > totalCount) {
                count = totalCount;
            }

            for (int i = 0; i < count; i++) {
                ChildRect childRect = new ChildRect();
                Point point = calculatePoint(angle * i + originalOffsetAngle, offsetX, offsetY, radius);
                childRect.x = point.x;
                childRect.y = point.y;
                childRect.angle = angle * i + originalOffsetAngle;
                involutePointAndScale(childRect);
                childRecfRecord.add(childRect);
            }

            radius += itemSizeH;
            currentRound++;
            totalCount -= count;
        }
    }

    private void addChild(View child, ChildRect childRect) {
        addView(child);
        measureChildWithMargins(child, getHorizontalSpace() - itemSizeH, getVerticalSpace() - itemSizeH);
        child.setScaleX(childRect.scale);
        child.setScaleY(childRect.scale);
        int radius = itemSizeH / 2;
        int l = childRect.x - radius;
        int t = childRect.y - radius;
        int r = childRect.x + radius;
        int b = childRect.y + radius;
        layoutDecoratedWithMargins(child, l, t, r, b);

    }

    private Point calculatePoint(double angle, int x, int y, int radius) {
        Point point = new Point();
        if (angle < originalOffsetAngle) {
            return point;
        }
        if (angle > originalOffsetAngle && angle < 60 + originalOffsetAngle) {
            point = calculateHexagonEdgePoint(angle, x, y, radius, originalOffsetAngle, 60 + originalOffsetAngle);
        } else if (angle > 60 + originalOffsetAngle && angle < 120 + originalOffsetAngle) {
            point = calculateHexagonEdgePoint(angle, x, y, radius, 60 + originalOffsetAngle, 120 + originalOffsetAngle);
        } else if (angle > 120 + originalOffsetAngle && angle < 180 + originalOffsetAngle) {
            point = calculateHexagonEdgePoint(angle, x, y, radius, 120 + originalOffsetAngle, 180 + originalOffsetAngle);
        } else if (angle > 180 + originalOffsetAngle && angle < 240 + originalOffsetAngle) {
            point = calculateHexagonEdgePoint(angle, x, y, radius, 180 + originalOffsetAngle, 240 + originalOffsetAngle);
        } else if (angle > 240 + originalOffsetAngle && angle < 300 + originalOffsetAngle) {
            point = calculateHexagonEdgePoint(angle, x, y, radius, 240 + originalOffsetAngle, 300 + originalOffsetAngle);
        } else if (angle > 300 + originalOffsetAngle && angle < 360 + originalOffsetAngle) {
            point = calculateHexagonEdgePoint(angle, x, y, radius, 300 + originalOffsetAngle, 360 + originalOffsetAngle);
        } else {
            int absX = (int) Math.round(radius * Math.cos(Math.toRadians(angle)));
            int absY = (int) Math.round(radius * Math.sin(Math.toRadians(angle)));
//            Log.d(TAG, "calculatePoint Math.sin(Math.toRadians(angle)) = " + Math.sin(Math.toRadians(angle))
//                    + ",Math.cos(Math.toRadians(angle)) = " + Math.cos(Math.toRadians(angle))
//                    + ",angle = " + angle + ",absY = " + absY + ",absX = " + absX);
            point.x = x - absX;
            point.y = y - absY;
        }
        return point;
    }


    /**
     * 计算六边形边上的点
     *
     * @param angle      六边形边上的点对应的角度
     * @param x          六边形的中心
     * @param y          六边形的中心
     * @param radius     六边形的半径
     * @param startAngle 六边形边的起始点
     * @param endAngle   六边形边的结束点
     * @return
     */
    private Point calculateHexagonEdgePoint(double angle, int x, int y, int radius, double startAngle, double endAngle) {
        Point point = new Point();
        int startX = x - (int) Math.round(radius * Math.cos(Math.toRadians(startAngle)));
        int startY = y - (int) Math.round(radius * Math.sin(Math.toRadians(startAngle)));

        int endX = x - (int) Math.round(radius * Math.cos(Math.toRadians(endAngle)));
        int endY = y - (int) Math.round(radius * Math.sin(Math.toRadians(endAngle)));

        point.x = (int) Math.round((endX - startX) * ((angle - startAngle) / 60) + startX);
        point.y = (int) Math.round((endY - startY) * ((angle - startAngle) / 60) + startY);
        return point;
    }

    private float calculateScale2(Point point) {
        float scale;
        double maxDistance = pointToPointDistance(0, 0, centerX, centerY);
        double currentPointDistance = pointToPointDistance(point.x, point.y, centerX, centerY);
        scale = (float) (1f - 0.45f * (currentPointDistance / maxDistance));
        if (scale < 0)
            scale = 0;
        return scale;
    }

    /**
     * @param childRect
     */
    private void involutePointAndScale(ChildRect childRect) {
        reworkChildPoint(childRect);
        childRect.scale = calculateScale2(new Point(childRect.x, childRect.y));
    }

    private void reworkChildPoint(ChildRect childRect) {
        if (childRect.x < centerX) {
            float distance = centerX - childRect.x;
            double iolunvteScale = Math.pow(0.97, Math.pow(distance / itemSizeW, 1.8));//离边越近，内卷越大
            childRect.x = centerX - (int) (distance * iolunvteScale);
        } else if (childRect.x > centerX) {
            float distance = childRect.x - centerX;
            double iolunvteScale = Math.pow(0.97, Math.pow(distance / itemSizeW, 1.8));
            childRect.x = (int) (distance * iolunvteScale + centerX);
        }
        if (childRect.y < centerY) {
            float distance = centerY - childRect.y;
            double iolunvteScale = Math.pow(0.97, Math.pow(distance / itemSizeW, 1.8));
            childRect.y = centerY - (int) (distance * iolunvteScale);
        } else if (childRect.y > centerY) {
            float distance = childRect.y - centerY;
            double iolunvteScale = Math.pow(0.97, Math.pow(distance / itemSizeW, 1.8));
            childRect.y = (int) (distance * iolunvteScale + centerY);
        }
    }

    private void reworkChildPoint2(ChildRect childRect) {
        if (childRect.x < centerX) {

//            float distance = centerX - childRect.x;
//            double iolunvteScale = Math.pow(0.955, Math.pow(distance / itemSizeW, 1.8));//离边越近，内卷越大
//
//            childRect.x = centerX - (int) (distance * iolunvteScale);

            childRect.x = (int) (childRect.x + itemSizeW / 2 * (1 - childRect.scale));
        } else if (childRect.x > centerX) {

//            float distance = childRect.x - centerX;
//            double iolunvteScale = Math.pow(0.955, Math.pow(distance / itemSizeW, 1.8));
//            childRect.x = (int) (distance * iolunvteScale + centerX);

            childRect.x = (int) (childRect.x - itemSizeW / 2 * (1 - childRect.scale));
        }
        if (childRect.y < centerY) {

//            float distance = centerY - childRect.y;
//            double iolunvteScale = Math.pow(0.955, Math.pow(distance / itemSizeW, 1.8));
//            childRect.y = centerY - (int) (distance * iolunvteScale);

            childRect.y = (int) (childRect.y + itemSizeW / 2 * (1 - childRect.scale));
        } else if (childRect.y > centerY) {

//            float distance = childRect.y - centerY;
//            double iolunvteScale = Math.pow(0.955, Math.pow(distance / itemSizeW, 1.8));
//            childRect.y = (int) (distance * iolunvteScale + centerY);

            childRect.y = (int) (childRect.y - itemSizeW / 2 * (1 - childRect.scale));
        }
    }

    private float calculateScale(Point point) {
//        Log.d(TAG, "calculateScale pointToPointDistance(point.x, point.y, centerX, centerY) = " + pointToPointDistance(point.x, point.y, centerX, centerY)
//                + ",itemSizeW = " + itemSizeW + ",getHorizontalSpace() = " + getHorizontalSpace() + ",getVerticalSpace() = " + getVerticalSpace()
//                + ",point = " + point);
        if (point.x <= getHorizontalSpace() && point.x >= 0 && point.y <= getVerticalSpace() && point.y >= 0) {
            float minFixedEdge = itemSizeW * FIFTH_SCALE;//距离边距的最小值
            float minDistance = Float.MAX_VALUE;//距离四边最小的距离
            if (point.x < minDistance) {
                minDistance = point.x;
            }
            if (point.y < minDistance) {
                minDistance = point.y;
            }
            if (getHorizontalSpace() - point.x < minDistance) {
                minDistance = getHorizontalSpace() - point.x;
            }
            if (getVerticalSpace() - point.y < minDistance) {
                minDistance = getVerticalSpace() - point.y;
            }
            if (point.x <= minFixedEdge || point.x >= getHorizontalSpace() - minFixedEdge || point.y <= minFixedEdge || point.y >= getVerticalSpace() - minFixedEdge) {
                return minDistance / (itemSizeW * FIFTH_SCALE) * FIFTH_SCALE;
            }

            float scale = Float.MAX_VALUE;
            if (point.x < centerX) {
                float temp = (point.x - minFixedEdge) / (centerX - minFixedEdge);
                if (scale > temp)
                    scale = temp;
            } else {
                float temp = (getHorizontalSpace() - point.x - minFixedEdge) / (centerX - minFixedEdge);
                if (scale > temp)
                    scale = temp;
            }

            if (point.y < centerY) {
                float temp = (point.y - minFixedEdge) / (centerY - minFixedEdge);
                if (scale > temp)
                    scale = temp;
            } else {
                float temp = (getVerticalSpace() - point.y - minFixedEdge) / (centerY - minFixedEdge);
                if (scale > temp)
                    scale = temp;
            }
            scale = (float) Math.pow(scale, 0.25);
            scale = FIFTH_SCALE + (1 - FIFTH_SCALE) * scale;
            return scale;
        }
        return 0;
    }

    private double pointToPointDistance(int startX, int startY, int endX, int endY) {
        return Math.pow(Math.pow(startX - endX, 2) + Math.pow(startY - endY, 2), 0.5f);
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
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        switch (state) {
            case RecyclerView.SCROLL_STATE_DRAGGING://手指触摸屏幕
                cancelAnmationtor();
                break;
            case RecyclerView.SCROLL_STATE_IDLE://列表停止滚动后
                smoothScrollToPosition(findTargetPosition());
                break;
            default:
                break;
        }
    }

    private void smoothScrollToPosition(int position) {
        if (position >= 0 && position < getItemCount()) {
            startAnimation(position);
        }
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
//        smoothScrollToPosition(position);
    }

    @Override
    public void scrollToPosition(int position) {
//        requestLayout();
    }


    private void startAnimation(int position) {
        cancelAnmationtor();
        final ChildRect childRect = childRecfRecord.get(position);
        final PointF startPointF = new PointF(offsetX, offsetY);
        final PointF endPointF = new PointF(offsetX + centerX - childRect.x, offsetY + centerY - childRect.y);
        selectAnimator = ValueAnimator.ofObject(new PointFEvaluator(), startPointF, endPointF);
        selectAnimator.setInterpolator(new DecelerateInterpolator());
        selectAnimator.setDuration(autoSelectMaxDuration);
        selectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                PointF result = (PointF) animation.getAnimatedValue();
                offsetX = Math.round(result.x);
                offsetY = Math.round(result.y);
                requestLayout();
            }
        });
        selectAnimator.start();
    }

    private void cancelAnmationtor() {
        if (selectAnimator != null && (selectAnimator.isStarted() || selectAnimator.isRunning())) {
            selectAnimator.cancel();
        }
    }

    private int findTargetPosition() {
        int position = 0;
        int count = childRecfRecord.size();
        double minDistance = Double.MAX_VALUE;
        for (int i = 0; i < count; i++) {
            ChildRect childRect = childRecfRecord.get(i);
            double temp = pointToPointDistance(centerX, centerY, childRect.x, childRect.y);
            if (temp < minDistance) {
                minDistance = temp;
                position = i;
            }
        }
        return position;
    }


    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        return scroll(recycler, state, dx, Integer.MAX_VALUE).x;
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        return scroll(recycler, state, Integer.MAX_VALUE, dy).y;
    }

    private Point scroll(RecyclerView.Recycler recycler, RecyclerView.State state, int dx, int dy) {
        Point point = new Point();
        if (dx != Integer.MAX_VALUE && scrollX == Integer.MAX_VALUE) {
            scrollX = dx;
            if (scrollY == Integer.MAX_VALUE)
                return point;
        } else if (scrollX != Integer.MAX_VALUE && dx != Integer.MAX_VALUE && scrollY == Integer.MAX_VALUE) {
            scrollX += dx;
            scrollY = 0;
        }
        if (dy != Integer.MAX_VALUE && scrollY == Integer.MAX_VALUE) {
            scrollY = dy;
            if (scrollX == Integer.MAX_VALUE)
                return point;
        } else if (scrollY != Integer.MAX_VALUE && dy != Integer.MAX_VALUE && scrollX == Integer.MAX_VALUE) {
            scrollY += dy;
            scrollX = 0;
        }

        if (getChildCount() == 0) {
            scrollX = scrollY = Integer.MAX_VALUE;
            return point;
        }

//        if (scrollX > 50) {
//            scrollX = 50;
//        } else if (scrollX < -50) {
//            scrollX = -50;
//        }
//        if (scrollY > 50) {
//            scrollY = 50;
//        } else if (scrollY < -50) {
//            scrollY = -50;
//        }

        int count = childRecfRecord.size();
        boolean haveShow = false;
        for (int i = 0; i < count; i++) {
            ChildRect childRect = childRecfRecord.get(i);
            //如果还有item可以看见，则可以继续滑动
            if (childRect.y - scrollY < getHorizontalSpace() - itemSizeW * FIFTH_SCALE && childRect.y - scrollY > 0 + itemSizeW * FIFTH_SCALE
                    && childRect.x - scrollX < getVerticalSpace() - itemSizeW * FIFTH_SCALE && childRect.x > 0 + itemSizeW * FIFTH_SCALE) {
                haveShow = true;
                break;
            }
        }
        if (haveShow) {
            point.set(scrollX, scrollY);
        } else {
            point.set(0, 0);
        }
        fill(recycler, state, point.x, point.y);

        scrollX = scrollY = Integer.MAX_VALUE;
        return point;
    }

    private Point fill(RecyclerView.Recycler recycler, RecyclerView.State state, int dx, int dy) {
        Point point = new Point();
        detachAndScrapAttachedViews(recycler);
        offsetX -= dx;
        offsetY -= dy;
        calculateChild(recycler, state, 0, 0);
        int count = childRecfRecord.size();
        for (int i = 0; i < count; i++) {
            View child = recycler.getViewForPosition(i);
            ChildRect childRect = childRecfRecord.get(i);
            addChild(child, childRect);
        }
        return point;
    }

    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

    @Override
    public boolean canScrollVertically() {
        return true;
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    /**
     * 获取内容的高度
     *
     * @return
     */
    public int getVerticalSpace() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }


    /**
     * 获取内容的宽度
     *
     * @return
     */
    public int getHorizontalSpace() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

    private static class ChildRect {
        public int x, y;
        public float scale;
        public double angle;

        @Override
        public String toString() {
            return "ChildRect{" +
                    "x=" + x +
                    ", y=" + y +
                    ", scale=" + scale +
                    ", angle=" + angle +
                    '}';
        }
    }
}
