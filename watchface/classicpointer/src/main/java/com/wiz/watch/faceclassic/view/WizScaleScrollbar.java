package com.wiz.watch.faceclassic.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.wiz.watch.faceclassic.R;

import java.lang.ref.WeakReference;

public class WizScaleScrollbar extends View {

    private RecyclerView attachRecyclerView;
    private static final float SCROLLBAR_SCALE_DEFAULT = 0.3F;

    private float mScaleScrollbarTrackWidth = getResources().getDimension(
            R.dimen.wiz_scale_scrollbar_track_width_default);
    private float mScaleScrollbarThumbWidth = getResources().getDimension(
            R.dimen.wiz_scale_scrollbar_thumb_width_default);
    private float mScaleScrollbarDefaultPaddingTop = getResources().getDimension(
            R.dimen.wiz_scale_scrollbar_paddingTop_default);
    private float mScaleScrollbarDefaultPaddingEnd = getResources().getDimension(
            R.dimen.wiz_scale_scrollbar_paddingEnd_default);

    private int mScaleScrollbarTrackColor;
    private int mScaleScrollbarThumbColor;

    private Paint thumbScalePaint, trackScalePaint, thumbArcScalePaint, trackArcScalePaint;

    private boolean isUseScaleScrollbar = true;
    private boolean isNeedRefreshScaleTrack = true;

    private int trackHeight;
    private float trackLeft, trackBarTop, trackRight, trackBottom;

    private boolean isArc = false;
    private int trackStartAngle, trackSweepAngle;
    private float trackArcLeft, trackArcTop, trackArcRight, trackArcBottom;

    private int screenWidth, screenHeight;
    private Handler hiddenHandler = new MyHandler(this);

    public WizScaleScrollbar(@NonNull Context context) {
        this(context, null);
    }

    public WizScaleScrollbar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WizScaleScrollbar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mScaleScrollbarTrackColor = ContextCompat.getColor(context, R.color.wiz_scale_scrollbar_track_color);
        mScaleScrollbarThumbColor = ContextCompat.getColor(context, R.color.wiz_scale_scrollbar_thumb_color);

        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels;
        if (isUseScaleScrollbar) {
            initScaleScrollbarPaint();
            startShowAnimator();
        }
    }

    /**
     * 定义画笔样式
     **/
    private void initScaleScrollbarPaint() {
        trackScalePaint = new Paint();
        trackScalePaint.setAntiAlias(true);
        trackScalePaint.setColor(mScaleScrollbarTrackColor);

        trackArcScalePaint = new Paint();
        trackArcScalePaint.setAntiAlias(true);
        trackArcScalePaint.setColor(mScaleScrollbarTrackColor);
        trackArcScalePaint.setStyle(Paint.Style.STROKE);
        trackArcScalePaint.setStrokeWidth(mScaleScrollbarTrackWidth);
        trackArcScalePaint.setStrokeCap(Paint.Cap.ROUND);

        thumbScalePaint = new Paint();
        thumbScalePaint.setColor(mScaleScrollbarThumbColor);
        thumbScalePaint.setAntiAlias(true);

        thumbArcScalePaint = new Paint();
        thumbArcScalePaint.setAntiAlias(true);
        thumbArcScalePaint.setColor(mScaleScrollbarThumbColor);
        thumbArcScalePaint.setStyle(Paint.Style.STROKE);
        thumbArcScalePaint.setStrokeWidth(mScaleScrollbarThumbWidth);
        thumbArcScalePaint.setStrokeCap(Paint.Cap.ROUND);

    }

    public void attachToRecyclerView(RecyclerView attachView) {
        this.attachRecyclerView = attachView;
        attachRecyclerView.setOnScrollChangeListener(new OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                startShowAnimator();
            }
        });
        attachRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    startShowAnimator();
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                startShowAnimator();
            }
        });
        attachRecyclerView.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {

                    @Override
                    public boolean onPreDraw() {
                        attachRecyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
                        if (!isArc && trackHeight == 0) {
                            initScaleTrack();
                        }
                        return true;
                    }
                });
        initScaleTrack();
        invalidate();
    }

    private void initScaleTrack() {
        float scrollbarScale = SCROLLBAR_SCALE_DEFAULT;
        if (isArc) {
            float trackArcRadius = Math.min(screenHeight, screenWidth) / 2f;
            trackStartAngle = this.isLayoutRtl(getContext()) ? ((int) (180 * (1 - scrollbarScale) / 2) + 90) : ((int) (180 * (1 - scrollbarScale) / 2) - 90);
            trackSweepAngle = (int) (180 * scrollbarScale);

            trackArcLeft = this.isLayoutRtl(getContext()) ? (0f + mScaleScrollbarDefaultPaddingEnd * 2) : (0f - mScaleScrollbarDefaultPaddingEnd * 2);
            trackArcTop = 0f + mScaleScrollbarDefaultPaddingTop;
            trackArcRight = trackArcLeft + trackArcRadius * 2f;
            trackArcBottom = trackArcTop + trackArcRadius * 2f;
        } else {
            View attachView = attachRecyclerView;
            trackHeight = (int) (attachView.getHeight() * scrollbarScale);
            trackBarTop = (1.0f * attachView.getHeight() - trackHeight) / 2 + mScaleScrollbarDefaultPaddingTop;
            trackBottom = trackBarTop + trackHeight;

            if (this.isLayoutRtl(getContext())) {
                trackLeft = attachView.getPaddingStart() + mScaleScrollbarDefaultPaddingEnd;
            } else {
                trackLeft = (screenWidth - attachView.getPaddingEnd() - mScaleScrollbarTrackWidth - mScaleScrollbarDefaultPaddingEnd);
            }
            trackRight = trackLeft + mScaleScrollbarTrackWidth;
        }
        isNeedRefreshScaleTrack = false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(screenWidth, screenHeight);
    }


    @Override
    public void layout(int l, int t, int r, int b) {
        super.layout(0, 0, screenWidth, screenHeight);
    }

    @Override
    public void draw(Canvas c) {
        super.draw(c);
        if (null != attachRecyclerView && attachRecyclerView.computeVerticalScrollExtent() >= attachRecyclerView.computeVerticalScrollRange()) {
            return;
        }
        if (isUseScaleScrollbar) {
            if (isNeedRefreshScaleTrack) {
                initScaleTrack();
            }
            if (isArc) {
                drawArcTrack(c);
                drawArcThumb(c);
            } else {
                drawTrack(c);
                drawThumb(c);
            }
        }
    }

    private void drawThumb(Canvas c) {
        int range = attachRecyclerView.computeVerticalScrollRange();
        int offset = attachRecyclerView.computeVerticalScrollOffset();
        int extent = attachRecyclerView.computeVerticalScrollExtent();

        int thumbHeight = (int) ((extent * 1f / range) * trackHeight);
        if (thumbHeight < 20) {
            thumbHeight = 20;
        }
        float thumbTop = trackBarTop + (trackHeight - thumbHeight) * 1f * (offset * 1f / (range - extent));
        float thumbBottom = thumbTop + thumbHeight;
        float thumbLeft = trackLeft + ((mScaleScrollbarTrackWidth - mScaleScrollbarThumbWidth) / 2);
        float thumbRight = thumbLeft + mScaleScrollbarThumbWidth;

        c.drawRoundRect(thumbLeft, thumbTop, thumbRight, thumbBottom, mScaleScrollbarThumbWidth / 2,
                mScaleScrollbarThumbWidth / 2, thumbScalePaint);
    }

    private void drawTrack(Canvas c) {
        c.drawRoundRect(trackLeft, trackBarTop, trackRight, trackBottom,
                mScaleScrollbarTrackWidth / 2, mScaleScrollbarTrackWidth / 2,
                trackScalePaint);
    }

    private void drawArcThumb(Canvas c) {
        int range = attachRecyclerView.computeVerticalScrollRange();
        int offset = attachRecyclerView.computeVerticalScrollOffset();
        int extent = attachRecyclerView.computeVerticalScrollExtent();

        float thumbArcLeft = trackArcLeft;
        float thumbArcTop = trackArcTop;
        float thumbArcRight = trackArcRight;
        float thumbArcBottom = trackArcBottom;

        int thumbSweepAngle = (int) ((extent * 1f / range) * trackSweepAngle);
        int thumbStartAngle = isLayoutRtl(getContext()) ? ((int) (trackStartAngle + (trackSweepAngle - thumbSweepAngle)
                * 1f * (1f - (offset * 1f / (range - extent)))))
                : ((int) (trackStartAngle + (trackSweepAngle - thumbSweepAngle) * 1f * (offset * 1f / (range - extent))));
        c.drawArc(thumbArcLeft, thumbArcTop, thumbArcRight, thumbArcBottom, thumbStartAngle,
                thumbSweepAngle, false, thumbArcScalePaint);
    }

    private void drawArcTrack(Canvas c) {
        c.drawArc(trackArcLeft, trackArcTop, trackArcRight, trackArcBottom, trackStartAngle,
                trackSweepAngle, false, trackArcScalePaint);
    }

    private void startShowAnimator() {
        hiddenHandler.removeMessages(0);
        setAlpha(1f);
        invalidate();
        hiddenHandler.sendEmptyMessageDelayed(0, 2500);
    }

    private void startHiddenAnimator() {
        setAlpha(0f);
        invalidate();
    }

    private boolean isLayoutRtl(Context context) {
        return View.LAYOUT_DIRECTION_RTL == context.getResources().getConfiguration().getLayoutDirection();
    }

    private static class MyHandler extends Handler {
        private WeakReference<WizScaleScrollbar> mWeakReference;

        public MyHandler(WizScaleScrollbar instance) {
            mWeakReference = new WeakReference<>(instance);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            WizScaleScrollbar scaleScrollbar = mWeakReference.get();
            if (scaleScrollbar == null) {
                return;
            }
            scaleScrollbar.startHiddenAnimator();
        }
    }
}
