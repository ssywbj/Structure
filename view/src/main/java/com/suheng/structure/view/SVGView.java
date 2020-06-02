package com.suheng.structure.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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

        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(26f);

        mBitmapManager = new BitmapManager(getContext());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //canvas.translate(mPointCenter.x, mPointCenter.y);
        canvas.drawColor(ContextCompat.getColor(getContext(), android.R.color.black));


        //原图
        float left = 4, top = 4;
        Bitmap bitmap = mBitmapManager.get(R.drawable.view_number_5_big);
        canvas.drawBitmap(bitmap, left, top, null);
        top += bitmap.getHeight();

        float lineStartY = (top - 1.0f * bitmap.getHeight() / 2);
        canvas.drawLine(0, lineStartY, getWidth(), lineStartY, mPaint);

        left += (bitmap.getWidth() + 16);
        bitmap = mBitmapManager.get(R.drawable.view_number_5);
        canvas.drawBitmap(bitmap, left, lineStartY - 1.0f * bitmap.getHeight() / 2, null);

        left += (bitmap.getWidth() + 16);
        bitmap = mBitmapManager.get(R.drawable.view_number_5_small);
        canvas.drawBitmap(bitmap, left, lineStartY - 1.0f * bitmap.getHeight() / 2, null);

        //#####################################缩放测试：Start#####################################
        //小图放大
        left = 4;
        top += 8;
        bitmap = mBitmapManager.getScale(R.drawable.view_number_5_small, 4);//放大4倍
        canvas.drawBitmap(bitmap, left, top, null);
        top += bitmap.getHeight();

        lineStartY = (top - 1.0f * bitmap.getHeight() / 2);
        canvas.drawLine(0, lineStartY, getWidth(), lineStartY, mPaint);

        left += (bitmap.getWidth() + 16);
        bitmap = mBitmapManager.getScale(R.drawable.view_number_5_small, 2);//放大2倍
        canvas.drawBitmap(bitmap, left, lineStartY - 1.0f * bitmap.getHeight() / 2, null);

        left += (bitmap.getWidth() + 16);
        bitmap = mBitmapManager.getScale(R.drawable.view_number_5_small, 1);//放大1倍（原图）
        canvas.drawBitmap(bitmap, left, lineStartY - 1.0f * bitmap.getHeight() / 2, null);

        //大图缩小
        left = 4;
        top += 8;
        bitmap = mBitmapManager.getScale(R.drawable.view_number_5_big, 1);//缩小1倍（原图）
        canvas.drawBitmap(bitmap, left, top, null);
        top += bitmap.getHeight();

        lineStartY = (top - 1.0f * bitmap.getHeight() / 2);
        canvas.drawLine(0, lineStartY, getWidth(), lineStartY, mPaint);

        left += (bitmap.getWidth() + 16);
        bitmap = mBitmapManager.getScale(R.drawable.view_number_5_big, 1.0f / 2);//缩小2倍
        canvas.drawBitmap(bitmap, left, lineStartY - 1.0f * bitmap.getHeight() / 2, null);

        left += (bitmap.getWidth() + 16);
        bitmap = mBitmapManager.getScale(R.drawable.view_number_5_big, 1.0f / 4);//缩小4倍
        canvas.drawBitmap(bitmap, left, lineStartY - 1.0f * bitmap.getHeight() / 2, null);
        //###########缩放测试：end，结论：单个SVG不管是缩小还是放大，都有锯齿，其中放大的更明显###############

    }

}
