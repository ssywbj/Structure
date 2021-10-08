package com.suheng.structure.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;

public class FontView extends View {
    private Paint mPaint;

    private String mText;

    private final Rect mRect = new Rect();

    public FontView(Context context) {
        this(context, null);
    }

    public FontView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FontView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setBackgroundColor(Color.BLACK);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(2);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.WHITE);
        mPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20, getResources().getDisplayMetrics()));

        mText = "bmp1097";

        mPaint.getTextBounds(mText, 0, mText.length(), mRect);

        //https://blog.csdn.net/chen_yuyunfox/article/details/44559057
        //https://blog.51cto.com/mikewang/871765
        mFontMetrics = mPaint.getFontMetrics();
        Log.d("Wbj", "init: " + mFontMetrics.top + ", " + mFontMetrics.bottom + ", " + mFontMetrics.ascent
                + ", " + mFontMetrics.descent + ", " + mFontMetrics.leading);
        Log.d("Wbj", "init: " + ((mFontMetrics.bottom + mFontMetrics.top) / 2f) + ", " + mRect.centerY()
                + ", " + ((mFontMetrics.descent + mFontMetrics.ascent) / 2f));
        //获取文字高度的正确方法：Paint.descent()封装了FontMetrics.descent，Paint.ascent()封装了FontMetrics.ascent
        float textHeight = mPaint.descent() - mPaint.ascent();
        Log.d("Wbj", "init: " + (mFontMetrics.bottom - mFontMetrics.top) + ", " + mRect.height()
                + ", " + (mFontMetrics.descent - mFontMetrics.ascent) + ", " + textHeight);
    }

    private int mCentreY;
    private Paint.FontMetrics mFontMetrics;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), (int) (mRect.height() * 2.5));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCentreY = h / 2;
    }

    //https://www.jianshu.com/p/c8e70e045133
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(0, getHeight() / 2f);
        canvas.drawLine(0, 0, getWidth(), 0, mPaint);
        canvas.drawLine(0, -mRect.height() * 0.6f, getWidth(), -mRect.height() * 0.6f, mPaint);
        canvas.drawLine(0, mRect.height() * 0.6f, getWidth(), mRect.height() * 0.6f, mPaint);
        canvas.restore();

        int offsetX = 6;

        int x = offsetX;
        float tcy = (mFontMetrics.bottom + mFontMetrics.top) / 2f;
        canvas.drawText(mText, x, mCentreY - tcy, mPaint);

        x += (offsetX * 2 + mRect.width());
        tcy = mRect.centerY();
        canvas.drawText(mText, x, mCentreY - tcy, mPaint);

        x += (offsetX * 2 + mRect.width());
        tcy = (mPaint.descent() + mPaint.ascent()) / 2f;
        canvas.drawText(mText, x, mCentreY - tcy, mPaint); //文案居中显示的正确画法
    }
}
