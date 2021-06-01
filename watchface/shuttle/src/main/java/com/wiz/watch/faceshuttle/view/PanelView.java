package com.wiz.watch.faceshuttle.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.Nullable;

import com.structure.wallpaper.basic.NumberBeatView;
import com.structure.wallpaper.basic.utils.DateUtil;
import com.structure.wallpaper.basic.utils.DimenUtil;
import com.wiz.watch.faceshuttle.R;

import java.util.Calendar;

public class PanelView extends NumberBeatView {
    protected final RectF mRectF = new RectF();
    private Bitmap mBackground;
    private boolean mIsShowDate;
    private final Rect mRect = new Rect();
    private final Paint mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);

    public PanelView(Context context) {
        super(context);
        this.init();
    }

    public PanelView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PanelView);
        Drawable drawable = typedArray.getDrawable(R.styleable.PanelView_backgroundTexture);
        mIsShowDate = typedArray.getBoolean(R.styleable.PanelView_isShowDate, false);
        typedArray.recycle();

        if (drawable != null) {
            mBackground = ((BitmapDrawable) drawable).getBitmap();
            Log.d(mTAG, "drawable: " + drawable + ", background: " + mBackground);
        }
        this.init();
    }

    public void init() {
        mPaintText.setColor(Color.WHITE);
        mPaintText.setTextSize(DimenUtil.dip2px(getContext(), 22));
        mPaintText.setTypeface(Typeface.DEFAULT_BOLD);
        mPaintText.setAlpha(217);//255*0.85
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
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBackground != null) {
            canvas.drawBitmap(mBackground, null, mRectF, null);
        }

        if (mIsShowDate) {
            canvas.save();
            canvas.translate(mRectF.centerX(), mRectF.centerY());
            mRect.setEmpty();
            final String week = DateUtil.getWeekText(mContext).toUpperCase();
            mPaintText.getTextBounds(week, 0, week.length(), mRect);
            canvas.drawText(week, -mRect.width() / 2f, -mRectF.height() / 3.9f, mPaintText);
            final String date = this.getDateText();
            mRect.setEmpty();
            mPaintText.getTextBounds(date, 0, date.length(), mRect);
            canvas.drawText(date, -mRect.width() / 2f, mRectF.height() / 3.2f, mPaintText);
            canvas.restore();
        }
    }

    private void calcDimens(int width, int height) {
        Log.d(mTAG, "width: " + width + ", height: " + height + ", ps: " + getPaddingStart()
                + ", pe: " + getPaddingEnd() + ", pt: " + getPaddingTop() + ", pb: " + getPaddingBottom());
        RectF rectF = new RectF();
        rectF.left = getPaddingStart();
        rectF.top = getPaddingTop();
        rectF.right = width - getPaddingEnd();
        rectF.bottom = height - getPaddingBottom();

        float radius = Math.min(rectF.width(), rectF.height()) / 2;
        mRectF.setEmpty();
        mRectF.left = rectF.centerX() - radius;
        mRectF.top = rectF.centerY() - radius;
        mRectF.right = rectF.centerX() + radius;
        mRectF.bottom = rectF.centerY() + radius;
    }

    protected void recycleBitmap(Bitmap bitmap) {
        if (bitmap == null || bitmap.isRecycled()) {
            return;
        }
        bitmap.recycle();
    }

    private String getDateText() {
        StringBuilder stringBuilder = new StringBuilder();
        Calendar calendar = Calendar.getInstance();
        stringBuilder.append(calendar.get(Calendar.YEAR)).append(".");
        int month = calendar.get(Calendar.MONTH) + 1;
        stringBuilder.append(month / 10).append(month % 10).append(".");
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        stringBuilder.append(day / 10).append(day % 10);
        return stringBuilder.toString();
    }

    public void setBackground(Bitmap background) {
        mBackground = background;
    }

    public void setShowDate(boolean showDate) {
        mIsShowDate = showDate;
    }
}
