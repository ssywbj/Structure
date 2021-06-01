package com.wiz.watchface.datastatistics.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.structure.wallpaper.basic.WatchFaceView;
import com.structure.wallpaper.basic.manager.SportDataManager;
import com.structure.wallpaper.basic.utils.BitmapHelper;
import com.structure.wallpaper.basic.utils.DimenUtil;
import com.wiz.watchface.datastatistics.R;

public class StepsView extends WatchFaceView {
    private Bitmap mBitmapIcon;
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Rect mRect = new Rect();
    private SportDataManager mSportDataManager;

    public StepsView(Context context) {
        super(context);
        this.init();
    }

    public StepsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    private void init() {
        mBitmapIcon = BitmapHelper.get(getContext(), R.drawable.icon_steps, Float.parseFloat(mContext.getString(R.string.ratio)));

        mPaint.setTextSize(getResources().getDimension(R.dimen.text_data));
        mPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
        mPaint.setColor(Color.WHITE);

        mSportDataManager = new SportDataManager(mContext);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final String text = String.valueOf(mSportDataManager.getSteps());
        mPaint.getTextBounds(text, 0, text.length(), mRect);
        final int measuredWidth = Math.max(mBitmapIcon.getWidth(), mRect.width()) + DimenUtil.dip2px(getContext(), 4);
        final int measuredHeight = mBitmapIcon.getHeight() + mRect.height() + DimenUtil.dip2px(getContext(), 6);
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    public void onVisibilityChanged(boolean visible) {
        super.onVisibilityChanged(visible);
        if (visible) {
            mSportDataManager.queryStepAndCalories();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        /*if (mBitmapIcon != null && !mBitmapIcon.isRecycled()) {
            mBitmapIcon.recycle();
        }*/
    }

    @Override
    protected void onTimeTick() {
        mSportDataManager.queryStepAndCalories();

        requestLayout();

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(getWidth() / 2f, DimenUtil.dip2px(getContext(), 1));
        String text = String.valueOf(mSportDataManager.getSteps());
        mPaint.getTextBounds(text, 0, text.length(), mRect);
        canvas.drawText(text, -mRect.width() / 2f, mRect.height(), mPaint);

        canvas.drawBitmap(mBitmapIcon, -mBitmapIcon.getWidth() / 2f, mRect.height() + DimenUtil.dip2px(getContext(), 5), null);
    }

}
