package com.wiz.watch.faceclassic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.structure.wallpaper.basic.utils.DimenUtil;
import com.structure.wallpaper.basic.view.FaceAnimView;

public class ClassicWatchFace extends FaceAnimView {
    private static final int SCALES = 60;//60个刻度
    private static final float SCALE_DEGREE = 1.0f * 360 / SCALES;//刻度角
    private static final int SCALES_HOUR = 12;
    private static final float SCALE_DEGREE_HOUR = 1.0f * 360 / SCALES_HOUR;

    private float mRadiusOuter;//刻度外半径长度

    private ClassicBitmapManager mBitmapManager;

    private Paint mPaintScale;
    private float mScaleHourLen, mScaleMinuteLen;
    private Paint mPaintPointer;
    private float mPointerHourWidth;

    private Paint mPaintCenterCircle;
    private float mCenterCircleRadius;

    public ClassicWatchFace(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public void init() {
        mBitmapManager = new ClassicBitmapManager(mContext);

        mScaleHourLen = DimenUtil.dip2px(mContext, 12);
        mScaleMinuteLen = mScaleHourLen - DimenUtil.dip2px(mContext, 2);
        mPointerHourWidth = DimenUtil.dip2px(mContext, 10f);
        mCenterCircleRadius = mPointerHourWidth / 2 + DimenUtil.dip2px(mContext, 2.5f);

        mPaintScale = new Paint();
        mPaintScale.setAntiAlias(true);
        mPaintScale.setStyle(Paint.Style.FILL);
        mPaintScale.setStrokeCap(Paint.Cap.ROUND);
        mPaintPointer = new Paint(mPaintScale);

        mPaintCenterCircle = new Paint();
        mPaintCenterCircle.setAntiAlias(true);
        mPaintCenterCircle.setStyle(Paint.Style.FILL);

        needAppearAnimPointer();
        setDefaultTime(10, 10, 30);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float screenRadius = Math.min(w / 2f, h / 2f);//屏幕半径
        mRadiusOuter = screenRadius - DimenUtil.dip2px(mContext, 3);
    }

    @Override
    public void onVisibilityChanged(boolean visible) {
        super.onVisibilityChanged(visible);
        if (!visible) {
            stopSecondPointerAnim();
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);

        canvas.translate(getWidth() / 2f, getHeight() / 2f);
        this.paintScale(canvas);
        this.paintPointer(canvas, mHourRatio, mMinuteRatio, mSecondRatio);
    }

    private void paintScale(Canvas canvas) {
        float rotateDegrees;
        for (int index = 0; index < SCALES; index++) {
            rotateDegrees = SCALE_DEGREE * index;

            canvas.save();
            canvas.rotate(rotateDegrees);
            if (index % 5 == 0) {
                mPaintScale.setStrokeWidth(mPointerHourWidth / 3);
                mPaintScale.setColor(Color.parseColor("#CCCCCC"));
                canvas.drawLine(0, mRadiusOuter - mScaleHourLen, 0, mRadiusOuter, mPaintScale);
            } else {
                mPaintScale.setStrokeWidth(mPointerHourWidth / 4.5f);
                mPaintScale.setColor(Color.parseColor("#808080"));
                canvas.drawLine(0, mRadiusOuter - mScaleMinuteLen, 0, mRadiusOuter, mPaintScale);
            }

            canvas.restore();
        }

        float bitmapRadius = mRadiusOuter - mContext.getResources().getDimension(R.dimen.number_big_height) - 0.8f * mScaleHourLen;
        double sinValue, cosValue, radians;
        for (int index = 0; index < SCALES_HOUR; index++) {
            radians = Math.toRadians(index * SCALE_DEGREE_HOUR);//弧度值，Math.toRadians：度换算成弧度
            sinValue = Math.sin(radians);
            cosValue = Math.cos(radians);

            Bitmap bitmap = mBitmapManager.getNumberBitmap((index == 0 ? 12 : index)
                    , ContextCompat.getColor(mContext, android.R.color.white));
            canvas.drawBitmap(bitmap, (float) (bitmapRadius * sinValue) - bitmap.getWidth() / 2f
                    , -((float) (bitmapRadius * cosValue) + bitmap.getHeight() / 2f), null);
        }
    }

    private void paintPointer(Canvas canvas, float rateHour, float rateMinute, float rateSecond) {
        canvas.save();
        canvas.rotate(rateHour * 360);
        int color = Color.parseColor("#FFFFFF");
        mPaintPointer.setColor(color);
        mPaintPointer.setStrokeWidth(mPointerHourWidth);
        canvas.drawLine(0, 1.3f * mScaleHourLen, 0, -(mRadiusOuter - 5.5f * mScaleHourLen), mPaintPointer);
        canvas.restore();

        canvas.save();
        canvas.rotate(rateMinute * 360);
        color = Color.parseColor("#E7E7E7");
        mPaintPointer.setColor(color);
        mPaintPointer.setStrokeWidth(mPointerHourWidth / 1.5f);
        canvas.drawLine(0, 2 * mScaleHourLen, 0, -(mRadiusOuter - 4.5f * mScaleHourLen), mPaintPointer);
        canvas.restore();

        mPaintCenterCircle.setColor(color);
        canvas.drawCircle(0, 0, mCenterCircleRadius, mPaintCenterCircle);
        color = Color.parseColor("#FFA200");
        mPaintCenterCircle.setColor(color);
        canvas.drawCircle(0, 0, mCenterCircleRadius / 1.5f, mPaintCenterCircle);

        if (!mIsDimMode) {
            canvas.save();
            canvas.rotate(rateSecond * 360);
            mPaintPointer.setColor(color);
            mPaintPointer.setStrokeWidth(mPointerHourWidth / 3f);
            canvas.drawLine(0, 2.4f * mScaleHourLen, 0, -(mRadiusOuter - 1.75f * mScaleHourLen), mPaintPointer);
            canvas.restore();
        }

        mPaintCenterCircle.setColor(Color.parseColor("#B3A100"));
        canvas.drawCircle(0, 0, mCenterCircleRadius / 4f, mPaintCenterCircle);
    }

    @Override
    protected void onAppearAnimFinished() {
        startSecondPointerAnim();
    }

}
