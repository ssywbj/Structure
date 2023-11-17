package com.suheng.structure.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.Calendar;

public class HourMinuteView extends View {
    private final PointF mPointPaintCenter = new PointF();
    private ResourcesObtain mResourcesObtain;
    private int[] mTimePictureRes;
    private int mTimeDivideResId, mTimeShadeBgId;
    private MyBitmapManager mBitmapManager;
    private float mRatio;

    public HourMinuteView(Context context) {
        super(context);
        this.init();
    }

    public HourMinuteView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.HourMinuteView);
        //mTimeDivideResId = typedArray.getResourceId(R.styleable.HourMinuteView_divider_sign, R.drawable.w40h80_colon);
        mTimeShadeBgId = typedArray.getResourceId(R.styleable.HourMinuteView_time_shade_bg, 0);
        typedArray.recycle();

        this.init();
    }

    private void init() {
        mBitmapManager = new MyBitmapManager(getContext());
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mResourcesObtain == null) {
            mTimePictureRes = new int[10];
            for (int i = 0; i < mTimePictureRes.length; i++) {
                //mTimePictureRes[i] = R.drawable.w34h60_number_0 + i;
            }
        } else {
            mTimePictureRes = mResourcesObtain.getTimePictureRes();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mBitmapManager.clear();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);

        Bitmap bitmap = mBitmapManager.get(mTimePictureRes[0]);
        final int widgetWidth = bitmap.getWidth() * 4 + mBitmapManager.get(mTimeDivideResId).getWidth();
        mRatio = 1.0f * measuredWidth / widgetWidth;

        final int measuredHeight = mBitmapManager.getScale(mTimePictureRes[0], mRatio).getHeight();
        setMeasuredDimension(measuredWidth, measuredHeight);

        this.calcDimens(measuredWidth, measuredHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.calcDimens(w, h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int color = ContextCompat.getColor(getContext(), android.R.color.white);
        canvas.translate(getWidth() / 2f, getHeight() / 2f);
        Bitmap bitmap = mBitmapManager.getScale(mTimeDivideResId, color, mRatio);
        final float colonHalfWidth = bitmap.getWidth() / 2f;
        float left = -colonHalfWidth;
        canvas.drawBitmap(bitmap, left, -bitmap.getHeight() / 2f, null);
        left += bitmap.getWidth();

        Calendar calendar = Calendar.getInstance();
        int mMinute = calendar.get(Calendar.MINUTE);
        bitmap = mBitmapManager.getNumberBitmap(mMinute / 10, color, mRatio);
        this.drawTimeShadeBg(canvas, left);
        canvas.drawBitmap(bitmap, left, -bitmap.getHeight() / 2f, null);
        left += bitmap.getWidth();
        bitmap = mBitmapManager.getNumberBitmap(mMinute % 10, color, mRatio);
        this.drawTimeShadeBg(canvas, left);
        canvas.drawBitmap(bitmap, left, -bitmap.getHeight() / 2f, null);

        int mHour = calendar.get(Calendar.HOUR_OF_DAY);
        left = -colonHalfWidth;
        bitmap = mBitmapManager.getNumberBitmap(mHour % 10, color, mRatio);
        left -= bitmap.getWidth();
        this.drawTimeShadeBg(canvas, left);
        canvas.drawBitmap(bitmap, left, -bitmap.getHeight() / 2f, null);
        bitmap = mBitmapManager.getNumberBitmap(mHour / 10, color, mRatio);
        left -= bitmap.getWidth();
        this.drawTimeShadeBg(canvas, left);
        canvas.drawBitmap(bitmap, left, -bitmap.getHeight() / 2f, null);
    }

    private void calcDimens(int width, int height) {
        mPointPaintCenter.x = (width - getPaddingStart() - getPaddingEnd()) / 2f;
        mPointPaintCenter.y = (height - getPaddingTop() - getPaddingBottom()) / 2f;
    }

    public void drawTimeShadeBg(Canvas canvas, float left) {
        if (mTimeShadeBgId != 0) {
            Bitmap bitmapShade = mBitmapManager.getScale(mTimeShadeBgId, Color.parseColor("#33FFFFFF"), mRatio);
            canvas.drawBitmap(bitmapShade, left, -bitmapShade.getHeight() / 2f, null);
        }
    }

    public void setResourcesObtain(ResourcesObtain resourcesObtain) {
        mResourcesObtain = resourcesObtain;
    }

    private class MyBitmapManager extends BitmapManager {
        public MyBitmapManager(Context context) {
            super(context);
        }

        Bitmap getNumberBitmap(int number, int color, float scale) {
            return getScale(mTimePictureRes[number], color, scale);
        }
    }

    public interface ResourcesObtain {
        int[] getTimePictureRes();
    }

}
