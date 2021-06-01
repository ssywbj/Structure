package com.wiz.watch.facefacingchallenges;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.structure.wallpaper.basic.utils.BitmapManager2;
import com.structure.wallpaper.basic.utils.DateUtil;
import com.structure.wallpaper.basic.view.FaceAnimView;

import java.util.Calendar;

public class VerticalTimeView extends FaceAnimView {
    private BitmapManager2 mBitmapManager;
    private final PointF mPointPaintCenter = new PointF();
    private final RectF mRectFTopLeft = new RectF();
    private final RectF mRectFTopRight = new RectF();
    private final RectF mRectFBottomLeft = new RectF();
    private final RectF mRectFBottomRight = new RectF();
    private TimeUpdateListener mTimeUpdateListener;
    private float mHorizontalSpacing = 16;
    private float mScale;

    public VerticalTimeView(Context context) {
        super(context);
        this.init();
    }

    public VerticalTimeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.VerticalTimeView);
        mHorizontalSpacing = typedArray.getDimension(R.styleable.VerticalTimeView_horizontalSpacing, 16);
        typedArray.recycle();

        this.init();
    }

    public void init() {
        setDefaultTime(8, 36, TIME_NONE);
        needAppearAnimNumber();
        mBitmapManager = new BitmapManager2(getContext());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.calcDimens(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.calcDimens(w, h);
    }

    @Override
    protected void onTimeTick() {
        super.onTimeTick();
        if (mTimeUpdateListener != null) {
            mTimeUpdateListener.onTimeTick();
        }
    }

    @Override
    public void onVisibilityChanged(boolean visible) {
        super.onVisibilityChanged(visible);
        if (!visible) {
            unregisterTimeTickReceiver();
        }
    }

    @Override
    public void updateTime() {
        mHour = DateUtil.getHour(mContext);
        mMinute = Calendar.getInstance().get(Calendar.MINUTE);
    }

    @Override
    protected void onAppearAnimFinished() {
        updateTime();
        invalidate();
        registerTimeTickReceiver();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final int color = ContextCompat.getColor(getContext(), android.R.color.white);
        canvas.drawBitmap(this.getNumberBitmap(mHour / 10, color), mRectFTopLeft.left, mRectFTopLeft.top, null);
        canvas.drawBitmap(this.getNumberBitmap(mHour % 10, color), mRectFTopRight.left, mRectFTopRight.top, null);
        canvas.drawBitmap(this.getNumberBitmap(mMinute / 10, color), mRectFBottomLeft.left, mRectFBottomLeft.top, null);
        canvas.drawBitmap(this.getNumberBitmap(mMinute % 10, color), mRectFBottomRight.left, mRectFBottomRight.top, null);
    }

    private void calcDimens(int width, int height) {
        mPointPaintCenter.x = (width - getPaddingStart() - getPaddingEnd()) / 2f;
        mPointPaintCenter.y = (height - getPaddingTop() - getPaddingBottom()) / 2f;

        this.calcRectF(mRectFTopLeft, getPaddingStart(), getPaddingTop(), true);
        this.calcRectF(mRectFTopRight, mPointPaintCenter.x + getPaddingStart(), getPaddingTop(), true);
        this.calcRectF(mRectFBottomLeft, getPaddingStart(), mPointPaintCenter.y + getPaddingTop(), false);
        this.calcRectF(mRectFBottomRight, mPointPaintCenter.x + getPaddingStart(), mPointPaintCenter.y + getPaddingTop(), false);

        mScale = mRectFTopLeft.width() / mBitmapManager.get(R.drawable.number_0).getWidth() * 1.05f;
    }

    private void calcRectF(RectF rectF, float left, float top, boolean isTopRow) {
        rectF.setEmpty();
        rectF.left = left;
        rectF.top = (isTopRow ? top : top + mHorizontalSpacing / 2);
        rectF.right = rectF.left + mPointPaintCenter.x;
        rectF.bottom = rectF.top + mPointPaintCenter.y - mHorizontalSpacing / 2;
    }

    private Bitmap getNumberBitmap(int number, int color) {
        switch (number) {
            case 1:
                return mBitmapManager.get(R.drawable.number_1, color, mScale);
            case 2:
                return mBitmapManager.get(R.drawable.number_2, color, mScale);
            case 3:
                return mBitmapManager.get(R.drawable.number_3, color, mScale);
            case 4:
                return mBitmapManager.get(R.drawable.number_4, color, mScale);
            case 5:
                return mBitmapManager.get(R.drawable.number_5, color, mScale);
            case 6:
                return mBitmapManager.get(R.drawable.number_6, color, mScale);
            case 7:
                return mBitmapManager.get(R.drawable.number_7, color, mScale);
            case 8:
                return mBitmapManager.get(R.drawable.number_8, color, mScale);
            case 9:
                return mBitmapManager.get(R.drawable.number_9, color, mScale);
            default:
                return mBitmapManager.get(R.drawable.number_0, color, mScale);
        }
    }

    public void setTimeUpdateListener(TimeUpdateListener timeUpdateListener) {
        mTimeUpdateListener = timeUpdateListener;
    }

    public interface TimeUpdateListener {
        void onTimeTick();
    }

}
