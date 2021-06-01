package com.wiz.watchface.datastatistics.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.structure.wallpaper.basic.NumberBeatView;
import com.structure.wallpaper.basic.utils.DateUtil;
import com.structure.wallpaper.basic.utils.DimenUtil;
import com.wiz.watchface.datastatistics.R;

public class TimeView extends NumberBeatView {
    private final Paint mPaintAm = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mPaintHm;
    private Paint mPaintSecond;

    private int mTimeWidth;
    private final Rect mRectHm = new Rect();
    private final Rect mRectSecond = new Rect();

    public TimeView(Context context) {
        super(context);
        this.init();
    }

    public TimeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    private void init() {
        //setBackgroundColor(Color.RED);
        setDefaultTime(8, 36, 54);
        mPaintAm.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));

        final Rect rect = new Rect();

        mPaintAm.setColor(ContextCompat.getColor(mContext, R.color.date_color));
        mPaintAm.setTextSize(DimenUtil.dip2px(getContext(), 36));
        String text = "PM";
        mPaintAm.getTextBounds(text, 0, text.length(), rect);
        mTimeWidth = rect.width() + DimenUtil.dip2px(mContext, 4);

        mPaintHm = new Paint(mPaintAm);
        mPaintHm.setColor(Color.WHITE);
        mPaintHm.setTextSize(DimenUtil.dip2px(getContext(), 102));
        text = "88:88";
        mPaintHm.getTextBounds(text, 0, text.length(), rect);
        mRectHm.set(mTimeWidth, 0, mTimeWidth + rect.width(), rect.height());
        mTimeWidth += rect.width() + DimenUtil.dip2px(mContext, 10);

        if (!mIsDimMode) {
            mPaintSecond = new Paint(mPaintAm);
            mPaintSecond.setTextSize(DimenUtil.dip2px(getContext(), 38));
            text = ":88";
            mPaintSecond.getTextBounds(text, 0, text.length(), rect);
            mTimeWidth += rect.width();
            mRectSecond.set(mRectHm.right + DimenUtil.dip2px(mContext, 10),
                    0, rect.width(), rect.height());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate((getWidth() - mTimeWidth) / 2f, (getHeight() + mRectHm.height()) / 2f);

        String text = DateUtil.isAm() ? "AM" : "PM";
        if (!DateUtil.is24HourFormat(mContext)) {
            canvas.drawText(text, 0, 0, mPaintAm);
        }

        text = mHour / 10 + "" + mHour % 10 + ":" + mMinute / 10 + mMinute % 10;
        canvas.drawText(text, mRectHm.left, 0, mPaintHm);

        if (!mIsDimMode) {
            text = ":" + mSecond / 10 + mSecond % 10;
            canvas.drawText(text, mRectSecond.left, 0, mPaintSecond);
        }
    }

    @Override
    protected void onAppearanceAnimFinished() {
        super.onAppearanceAnimFinished();
        setUpdateTimePerSecondImmediately(true);
        notifyMsgUpdateTimePerSecond();
    }

}
