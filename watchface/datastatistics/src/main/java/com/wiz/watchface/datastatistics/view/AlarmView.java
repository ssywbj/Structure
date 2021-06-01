package com.wiz.watchface.datastatistics.view;

import android.app.AlarmManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.Nullable;

import com.structure.wallpaper.basic.WatchFaceView;
import com.structure.wallpaper.basic.utils.BitmapHelper;
import com.structure.wallpaper.basic.utils.DimenUtil;
import com.wiz.watchface.datastatistics.R;

import java.util.Calendar;

public class AlarmView extends WatchFaceView {
    private Bitmap mBitmapIcon;
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Rect mRect = new Rect();
    private String mAlarmInfo = "";
    private AlarmManager mAlarmManager;

    public AlarmView(Context context) {
        super(context);
        this.init();
    }

    public AlarmView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    private void init() {
        mBitmapIcon = BitmapHelper.get(getContext(), R.drawable.icon_alarm, Float.parseFloat(mContext.getString(R.string.ratio)));

        mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        mPaint.setTextSize(getResources().getDimension(R.dimen.text_data));
        mPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
        mPaint.setColor(Color.WHITE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final String text = "20:58";
        mPaint.getTextBounds(text, 0, text.length(), mRect);
        final int measuredWidth = Math.max(mBitmapIcon.getWidth(), mRect.width())+ DimenUtil.dip2px(getContext(), 4);
        final int measuredHeight = mBitmapIcon.getHeight() + mRect.height() + DimenUtil.dip2px(getContext(), 6);
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        /*if (mBitmapIcon != null && !mBitmapIcon.isRecycled()) {
            mBitmapIcon.recycle();
        }*/
    }

    @Override
    public void onVisibilityChanged(boolean visible) {
        if (visible) {
            this.getAlarmClockInfo();

            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(getWidth() / 2f, 0);
        canvas.drawBitmap(mBitmapIcon, -mBitmapIcon.getWidth() / 2f, 0, null);
        mPaint.getTextBounds(mAlarmInfo, 0, mAlarmInfo.length(), mRect);
        canvas.drawText(mAlarmInfo, -mRect.width() / 2f, getHeight() - DimenUtil.dip2px(getContext(), 1), mPaint);
    }

    private void getAlarmClockInfo() {
        AlarmManager.AlarmClockInfo alarmClock = mAlarmManager.getNextAlarmClock();
        if (alarmClock == null) {
            mAlarmInfo = "";
            return;
        }
        long triggerTime = alarmClock.getTriggerTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(triggerTime);
        int minute = calendar.get(Calendar.MINUTE);
        int hour = calendar.get(Calendar.HOUR);
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int ampm = calendar.get(Calendar.AM_PM);//0：上午，1：下午
        Log.d(mTAG, "alarm clock info, triggerTime: " + triggerTime + ", ampm: " + ampm
                + ", hour: " + hour + ", minute: " + minute + ", hourOfDay: " + hourOfDay);
        mAlarmInfo = hourOfDay / 10 + "" + hourOfDay % 10 + ":" + minute / 10 + "" + minute % 10;
        /*if (DateFormat.is24HourFormat(mContext)) {
            mAlarmInfo = hourOfDay / 10 + "" + hourOfDay % 10 + ":" + minute / 10 + "" + minute % 10;
        } else {
            mAlarmInfo = hour / 10 + "" + hour % 10 + ":" + minute / 10
                    + "" + minute % 10 + (ampm == 0 ? "AM" : "PM");
        }*/
    }

}
