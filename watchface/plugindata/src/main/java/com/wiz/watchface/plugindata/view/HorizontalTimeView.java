package com.wiz.watchface.plugindata.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.structure.wallpaper.basic.view.FaceAnimView;
import com.wiz.watchface.plugindata.PluginDataBitmapManager;
import com.wiz.watchface.plugindata.R;

public class HorizontalTimeView extends FaceAnimView {
    private PluginDataBitmapManager mBitmapManager;
    private final PointF mPointPaintCenter = new PointF();
    private float mScale;

    public HorizontalTimeView(Context context) {
        super(context);
        this.init();
    }

    public HorizontalTimeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public void init() {
        setDefaultTime(8, 36, TIME_NONE);
        needAppearAnimNumber();
        mBitmapManager = new PluginDataBitmapManager(getContext());
        mScale = Float.parseFloat(mContext.getString(R.string.ratio));
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
    public void onVisibilityChanged(boolean visible) {
        super.onVisibilityChanged(visible);
        if (!visible) {
            unregisterTimeTickReceiver();
        }
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
        canvas.save();
        canvas.translate(getWidth() / 2f, getHeight() / 2f);
        Bitmap bitmap = mBitmapManager.get(R.drawable.punctuation_colon, color);
        final float colonHalfWidth = bitmap.getWidth() / 2f;
        float left = -colonHalfWidth;
        canvas.drawBitmap(bitmap, left, -bitmap.getHeight() / 2f, null);
        left += bitmap.getWidth();

        Bitmap bitmap8Shade = mBitmapManager.getNumberBitmap(8, ContextCompat.getColor(mContext, R.color.bitmap8), mScale);

        bitmap = mBitmapManager.getNumberBitmap(mMinute / 10, color, mScale);
        canvas.drawBitmap(bitmap8Shade, left, -bitmap.getHeight() / 2f, null);
        canvas.drawBitmap(bitmap, left, -bitmap.getHeight() / 2f, null);
        left += bitmap.getWidth();
        canvas.drawBitmap(bitmap8Shade, left, -bitmap.getHeight() / 2f, null);
        bitmap = mBitmapManager.getNumberBitmap(mMinute % 10, color, mScale);
        canvas.drawBitmap(bitmap, left, -bitmap.getHeight() / 2f, null);

        left = -colonHalfWidth;
        bitmap = mBitmapManager.getNumberBitmap(mHour % 10, color, mScale);
        left -= bitmap.getWidth();
        canvas.drawBitmap(bitmap8Shade, left, -bitmap.getHeight() / 2f, null);
        canvas.drawBitmap(bitmap, left, -bitmap.getHeight() / 2f, null);
        bitmap = mBitmapManager.getNumberBitmap(mHour / 10, color, mScale);
        left -= bitmap.getWidth();
        canvas.drawBitmap(bitmap8Shade, left, -bitmap.getHeight() / 2f, null);
        canvas.drawBitmap(bitmap, left, -bitmap.getHeight() / 2f, null);

        canvas.restore();
    }

    private void calcDimens(int width, int height) {
        mPointPaintCenter.x = (width - getPaddingStart() - getPaddingEnd()) / 2f;
        mPointPaintCenter.y = (height - getPaddingTop() - getPaddingBottom()) / 2f;
    }
}
