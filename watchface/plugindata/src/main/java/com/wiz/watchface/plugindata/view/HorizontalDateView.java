package com.wiz.watchface.plugindata.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.structure.wallpaper.basic.utils.DateUtil;
import com.structure.wallpaper.basic.view.FaceAnimView;
import com.wiz.watchface.plugindata.PluginDataBitmapManager;
import com.wiz.watchface.plugindata.R;

import java.util.Calendar;

public class HorizontalDateView extends FaceAnimView {
    private static final int SHADE_NUM = 9;
    private PluginDataBitmapManager mBitmapManager;
    private float mMarginShade, mScale;
    private Bitmap mBitmap8Shade;

    public HorizontalDateView(Context context) {
        super(context);
        this.init();
    }

    public HorizontalDateView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public void init() {
        setDefaultTime(TIME_NONE, TIME_NONE, 59);
        needAppearAnimNumber();
        mBitmapManager = new PluginDataBitmapManager(getContext());
        mScale = Float.parseFloat(mContext.getString(R.string.ratio)) * 0.475f;
        mBitmap8Shade = mBitmapManager.getNumberBitmap(8, ContextCompat.getColor(mContext, R.color.bitmap8), mScale);
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
            unregisterSecondTicker();
        }
    }

    @Override
    protected void onAppearAnimFinished() {
        registerSecondTicker();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float left = 0;
        for (int i = 0; i < SHADE_NUM; i++) {
            canvas.drawBitmap(mBitmap8Shade, left, 0, null);
            left += (mBitmap8Shade.getWidth() + mMarginShade);
        }

        Calendar calendar = Calendar.getInstance();
        Bitmap bitmap;
        int color = Color.WHITE;
        left = 0;
        if (!DateUtil.is24HourFormat(mContext)) {
            if (DateUtil.isAm()) {
                bitmap = mBitmapManager.get(R.drawable.alphabet_a, color, mScale);
            } else {
                bitmap = mBitmapManager.get(R.drawable.alphabet_p, color, mScale);
            }
            canvas.drawBitmap(bitmap, left, 0, null);
            left += (mBitmap8Shade.getWidth() + mMarginShade);
            bitmap = mBitmapManager.get(R.drawable.alphabet_m, color, mScale);
            canvas.drawBitmap(bitmap, left, 0, null);

            left = 3 * (mBitmap8Shade.getWidth() + mMarginShade);
        }
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case 2:
                bitmap = mBitmapManager.get(R.drawable.alphabet_m, color, mScale);
                canvas.drawBitmap(bitmap, left, 0, null);
                left += (mBitmap8Shade.getWidth() + mMarginShade);
                bitmap = mBitmapManager.get(R.drawable.alphabet_o, color, mScale);
                canvas.drawBitmap(bitmap, left, 0, null);
                left += (mBitmap8Shade.getWidth() + mMarginShade);
                bitmap = mBitmapManager.get(R.drawable.alphabet_n, color, mScale);
                canvas.drawBitmap(bitmap, left, 0, null);
                break;
            case 3:
                bitmap = mBitmapManager.get(R.drawable.alphabet_t, color, mScale);
                canvas.drawBitmap(bitmap, left, 0, null);
                left += (mBitmap8Shade.getWidth() + mMarginShade);
                bitmap = mBitmapManager.get(R.drawable.alphabet_u, color, mScale);
                canvas.drawBitmap(bitmap, left, 0, null);
                left += (mBitmap8Shade.getWidth() + mMarginShade);
                bitmap = mBitmapManager.get(R.drawable.alphabet_e, color, mScale);
                canvas.drawBitmap(bitmap, left, 0, null);
                left += (mBitmap8Shade.getWidth() + mMarginShade);
                bitmap = mBitmapManager.get(R.drawable.alphabet_s, color, mScale);
                canvas.drawBitmap(bitmap, left, 0, null);
                break;
            case 4:
                bitmap = mBitmapManager.get(R.drawable.alphabet_w, color, mScale);
                canvas.drawBitmap(bitmap, left, 0, null);
                left += (mBitmap8Shade.getWidth() + mMarginShade);
                bitmap = mBitmapManager.get(R.drawable.alphabet_e, color, mScale);
                canvas.drawBitmap(bitmap, left, 0, null);
                left += (mBitmap8Shade.getWidth() + mMarginShade);
                bitmap = mBitmapManager.get(R.drawable.alphabet_d, color, mScale);
                canvas.drawBitmap(bitmap, left, 0, null);
                break;
            case 5:
                bitmap = mBitmapManager.get(R.drawable.alphabet_t, color, mScale);
                canvas.drawBitmap(bitmap, left, 0, null);
                left += (mBitmap8Shade.getWidth() + mMarginShade);
                bitmap = mBitmapManager.get(R.drawable.alphabet_h, color, mScale);
                canvas.drawBitmap(bitmap, left, 0, null);
                left += (mBitmap8Shade.getWidth() + mMarginShade);
                bitmap = mBitmapManager.get(R.drawable.alphabet_u, color, mScale);
                canvas.drawBitmap(bitmap, left, 0, null);
                left += (mBitmap8Shade.getWidth() + mMarginShade);
                bitmap = mBitmapManager.get(R.drawable.alphabet_r, color, mScale);
                canvas.drawBitmap(bitmap, left, 0, null);
                break;
            case 6:
                bitmap = mBitmapManager.get(R.drawable.alphabet_f, color, mScale);
                canvas.drawBitmap(bitmap, left, 0, null);
                left += (mBitmap8Shade.getWidth() + mMarginShade);
                bitmap = mBitmapManager.get(R.drawable.alphabet_r, color, mScale);
                canvas.drawBitmap(bitmap, left, 0, null);
                left += (mBitmap8Shade.getWidth() + mMarginShade);
                bitmap = mBitmapManager.get(R.drawable.alphabet_i, color, mScale);
                canvas.drawBitmap(bitmap, left, 0, null);
                break;
            case 7:
                bitmap = mBitmapManager.get(R.drawable.alphabet_s, color, mScale);
                canvas.drawBitmap(bitmap, left, 0, null);
                left += (mBitmap8Shade.getWidth() + mMarginShade);
                bitmap = mBitmapManager.get(R.drawable.alphabet_a, color, mScale);
                canvas.drawBitmap(bitmap, left, 0, null);
                left += (mBitmap8Shade.getWidth() + mMarginShade);
                bitmap = mBitmapManager.get(R.drawable.alphabet_t, color, mScale);
                canvas.drawBitmap(bitmap, left, 0, null);
                break;
            default:
                bitmap = mBitmapManager.get(R.drawable.alphabet_s, color, mScale);
                canvas.drawBitmap(bitmap, left, 0, null);
                left += (mBitmap8Shade.getWidth() + mMarginShade);
                bitmap = mBitmapManager.get(R.drawable.alphabet_u, color, mScale);
                canvas.drawBitmap(bitmap, left, 0, null);
                left += (mBitmap8Shade.getWidth() + mMarginShade);
                bitmap = mBitmapManager.get(R.drawable.alphabet_n, color, mScale);
                canvas.drawBitmap(bitmap, left, 0, null);
        }

        if (!mIsDimMode) {
            left = 7 * (mBitmap8Shade.getWidth() + mMarginShade);
            bitmap = mBitmapManager.getNumberBitmap(mSecond / 10, color, mScale);
            canvas.drawBitmap(bitmap, left, 0, null);
            left += (mBitmap8Shade.getWidth() + mMarginShade);
            bitmap = mBitmapManager.getNumberBitmap(mSecond % 10, color, mScale);
            canvas.drawBitmap(bitmap, left, 0, null);
        }
    }

    private void calcDimens(int width, int height) {
        mMarginShade = 1.0f * (width - mBitmap8Shade.getWidth() * SHADE_NUM) / (SHADE_NUM - 1);
    }

}
