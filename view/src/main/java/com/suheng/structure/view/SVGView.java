package com.suheng.structure.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class SVGView extends View {
    private PointF mPointCenter = new PointF();
    private Paint mPaint = new Paint();
    private Rect mRect = new Rect();
    private BitmapManager mBitmapManager;

    private Path mPath = new Path();
    private float mRadius;

    public SVGView(Context context) {
        super(context);
        this.init();
    }

    public SVGView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public SVGView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    private void init() {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        mPointCenter.x = 1.0f * displayMetrics.widthPixels / 2;
        mPointCenter.y = 1.0f * displayMetrics.heightPixels / 2;
        mRadius = Math.min(mPointCenter.x, mPointCenter.y);

        mPaint.setAntiAlias(true);

        mBitmapManager = new BitmapManager(getContext());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mRect.set(0, 0, 400, 400);
        //canvas.clipRect(mRect);
        canvas.save();
        canvas.translate(0, 100);
        canvas.drawColor(ContextCompat.getColor(getContext(), android.R.color.holo_red_dark));
        canvas.restore();

        //this.paintScaleBitmap(canvas);
        //this.paintPath(canvas);
        //this.paintScalesText(canvas);
        //this.paintScalesBitmapMethod1(canvas);
        //this.paintScalesBitmapMethod2(canvas);
    }

    private void paintScalesBitmapMethod2(Canvas canvas) {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.RED);
        float radius = mRadius - 8;
        canvas.drawCircle(mPointCenter.x, mPointCenter.y, radius, mPaint);

        final int scales = 26 * 3;
        float scaleDegree = 1.0f * 360 / scales;
        Bitmap bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_a, android.R.color.white);
        float bitmapRadius = radius - 1.0f * bitmap.getHeight() / 2;
        double sinValue, cosValue, radians;
        for (int index = 0; index < scales; index++) {
            radians = Math.toRadians(index * scaleDegree);//弧度值，Math.toRadians：度换算成弧度
            sinValue = Math.sin(radians);
            cosValue = Math.cos(radians);
            canvas.drawLine(mPointCenter.x, mPointCenter.y, (float) (mPointCenter.x + radius * sinValue)
                    , (float) (mPointCenter.y - radius * cosValue), mPaint);
            bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_a + index % 26, android.R.color.white);
            canvas.drawBitmap(bitmap, (float) (mPointCenter.x + bitmapRadius * sinValue) - 1.0f * bitmap.getWidth() / 2
                    , (float) (mPointCenter.y - bitmapRadius * cosValue) - 1.0f * bitmap.getHeight() / 2, null);
        }
    }

    private void paintScalesBitmapMethod1(Canvas canvas) {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.RED);
        float radius = mRadius - 8;
        canvas.drawCircle(mPointCenter.x, mPointCenter.y, radius, mPaint);

        final int scales = 98;
        float scaleDegree = 1.0f * 360 / scales, rotateDegrees;
        float stopY = mPointCenter.y - radius;
        Bitmap bitmap;
        for (int index = 0; index < scales; index++) {
            rotateDegrees = index * scaleDegree;

            canvas.save();
            canvas.rotate(rotateDegrees, mPointCenter.x, mPointCenter.y);//画布旋转后，在低分辨率手机上bitmap会有些锯齿现象，但能接受
            //canvas.drawLine(mPointCenter.x, mPointCenter.y, mPointCenter.x, stopY, mPaint);

            if (rotateDegrees > 90 && rotateDegrees < 270) {
                bitmap = mBitmapManager.get(R.drawable.reverse_alphabet_uppercase_a + index % 26,
                        R.color.colorPrimary);
            } else {
                /*//旋转回相应的角度，目的是摆正图片。但再次旋转后，在低分辨率手机上锯齿现象会严重加剧，不能接受
                bitmap = mBitmapManager.getRotate(R.drawable.alphabet_uppercase_a + index % 26,
                        R.color.alphabet_uppercase, -rotateDegrees);*/
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_a + index % 26,
                        R.color.alphabet_uppercase);
            }

            canvas.drawBitmap(bitmap, mPointCenter.x - 1.0f * bitmap.getWidth() / 2, stopY, null);
            canvas.restore();
        }
    }

    private void paintScalesText(Canvas canvas) {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.RED);
        float radius = mRadius - 26;
        canvas.drawCircle(mPointCenter.x, mPointCenter.y, radius, mPaint);

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(20f);
        final int scales = 26;
        float scaleDegree = 1.0f * 360 / scales;
        float stopY = mPointCenter.y - radius;
        String text;
        for (int index = 0; index < scales; index++) {
            canvas.save();
            canvas.rotate(index * scaleDegree, mPointCenter.x, mPointCenter.y);
            canvas.drawLine(mPointCenter.x, mPointCenter.y, mPointCenter.x, stopY, mPaint);
            text = String.valueOf(index);
            mRect.setEmpty();
            mPaint.getTextBounds(text, 0, text.length(), mRect);
            canvas.drawText(index + "", mPointCenter.x - 1.0f * mRect.width() / 2, stopY + mRect.height(), mPaint);
            canvas.restore();
        }
    }

    private void paintScaleBitmap(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.RED);
        //原图
        float left = 4, top = 4;
        Bitmap bitmap = mBitmapManager.get(R.drawable.number_5_big);
        canvas.drawBitmap(bitmap, left, top, null);
        top += bitmap.getHeight();

        float lineStartY = (top - 1.0f * bitmap.getHeight() / 2);
        canvas.drawLine(0, lineStartY, getWidth(), lineStartY, mPaint);

        left += (bitmap.getWidth() + 16);
        bitmap = mBitmapManager.get(R.drawable.number_5);
        canvas.drawBitmap(bitmap, left, lineStartY - 1.0f * bitmap.getHeight() / 2, null);

        left += (bitmap.getWidth() + 16);
        bitmap = mBitmapManager.get(R.drawable.number_5_small);
        canvas.drawBitmap(bitmap, left, lineStartY - 1.0f * bitmap.getHeight() / 2, null);

        //#####################################缩放测试：Start#####################################
        //小图放大
        left = 4;
        top += 8;
        bitmap = mBitmapManager.getScale(R.drawable.number_5_small, 4);//放大4倍
        canvas.drawBitmap(bitmap, left, top, null);
        top += bitmap.getHeight();

        lineStartY = (top - 1.0f * bitmap.getHeight() / 2);
        canvas.drawLine(0, lineStartY, getWidth(), lineStartY, mPaint);

        left += (bitmap.getWidth() + 16);
        bitmap = mBitmapManager.getScale(R.drawable.number_5_small, 2);//放大2倍
        canvas.drawBitmap(bitmap, left, lineStartY - 1.0f * bitmap.getHeight() / 2, null);

        left += (bitmap.getWidth() + 16);
        bitmap = mBitmapManager.getScale(R.drawable.number_5_small, 1);//放大1倍（原图）
        canvas.drawBitmap(bitmap, left, lineStartY - 1.0f * bitmap.getHeight() / 2, null);

        //大图缩小
        left = 4;
        top += 8;
        bitmap = mBitmapManager.getScale(R.drawable.number_5_big, 1);//缩小1倍（原图）
        canvas.drawBitmap(bitmap, left, top, null);
        top += bitmap.getHeight();

        lineStartY = (top - 1.0f * bitmap.getHeight() / 2);
        canvas.drawLine(0, lineStartY, getWidth(), lineStartY, mPaint);

        left += (bitmap.getWidth() + 16);
        bitmap = mBitmapManager.getScale(R.drawable.number_5_big, 1.0f / 2);//缩小2倍
        canvas.drawBitmap(bitmap, left, lineStartY - 1.0f * bitmap.getHeight() / 2, null);

        left += (bitmap.getWidth() + 16);
        bitmap = mBitmapManager.getScale(R.drawable.number_5_big, 1.0f / 4);//缩小4倍
        canvas.drawBitmap(bitmap, left, lineStartY - 1.0f * bitmap.getHeight() / 2, null);
        //###########缩放测试：end，结论：单个SVG不管是缩小还是放大，都有锯齿，其中放大的更明显###############
    }

    private void paintPath(Canvas canvas) {
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);

        mPath.reset();
        mPath.addCircle(mPointCenter.x, mPointCenter.y, mRadius / 1.5f, Path.Direction.CW);//顺时针生成
        canvas.drawPath(mPath, mPaint);
        mPaint.setTextSize(30f);
        mPaint.setStyle(Paint.Style.FILL);
        //hOffset，vOffset：文案在水平、垂直方向相对Path的偏移量，0表示不偏移贴着Path，正数表示向上偏移穿过Path，负数表示向下偏移远离Path
        //顺时针生成，文案在Path外部被绘制
        canvas.drawTextOnPath("顺时针生成，文案在Path外部被绘制", mPath, -8, -8, mPaint);

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.GREEN);
        mPath.reset();
        mPath.addCircle(mPointCenter.x, mPointCenter.y, mRadius / 1.7f, Path.Direction.CCW);//逆时针生成
        canvas.drawPath(mPath, mPaint);
        mPaint.setTextSize(30f);
        mPaint.setStyle(Paint.Style.FILL);
        //逆时针生成，文案在Path内部被绘制
        canvas.drawTextOnPath("逆时针生成，文案在Path内部被绘制", mPath, 6, 6, mPaint);

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.BLUE);
        mPath.reset();
        mPath.moveTo(mPointCenter.x + 30, mPointCenter.y + 4);//从右到左画线
        mPath.lineTo(mPointCenter.x - 30, mPointCenter.y + 4);
        canvas.drawPath(mPath, mPaint);
        mPaint.setTextSize(20);
        mPaint.setStyle(Paint.Style.FILL);
        //从右到左画线，文案在Path下方被绘制，绘制方向也是从右到左
        canvas.drawTextOnPath("线下方", mPath, 0, 0, mPaint);

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.CYAN);
        mPath.reset();
        mPath.moveTo(mPointCenter.x - 30, mPointCenter.y - 4);//从左到右画线
        mPath.lineTo(mPointCenter.x + 30, mPointCenter.y - 4);
        canvas.drawPath(mPath, mPaint);
        mPaint.setTextSize(20);
        mPaint.setStyle(Paint.Style.FILL);
        //从左到右画线，文案在Path上方被绘制，绘制方向也是从左到右
        canvas.drawTextOnPath("线上方", mPath, -4, -4, mPaint);
    }

}
