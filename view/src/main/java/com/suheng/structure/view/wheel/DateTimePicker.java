package com.suheng.structure.view.wheel;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;

import com.suheng.structure.view.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DateTimePicker extends LinearLayout {

    private Calendar mCalendar = Calendar.getInstance();
    private WheelView mYearWheel, mMonthWheel, mDayWheel, mMonthDayWheel;
    private WheelView mFormatWheel, mHourWheel, mMinuteWheel;
    private final ArrayList<String> mMonthList = new ArrayList<>();
    private final ArrayList<String> mAmPmList = new ArrayList<>();
    private static int START_YEAR = 1900, END_YEAR = 2100;
    private OnDateTimeChangeListener mOnDateTimeChangeListener;
    public static final String FORMAT_Y_M_D_H_M = "yyyy-MM-dd HH:mm", FORMAT_Y_M_D = "yyyy-MM-dd", FORMAT_M_D = "MM-dd", FORMAT_M_D_H_M = "MM-dd HH:mm", FORMAT_H_M = "HH:mm";
    private WheelView.OnWheelChangedListener mOnYearChangedListener, mOnMonthChangedListener, mOnDayChangedListener, mOnMonthDayChangedLister,
            mOnHourChangedListener, mOnMinuteChangedListener, mOnFormatChangedListener;
    private boolean mIs24Format;
    private String mFormat;
    private final Context mContext;
    private final int DEFAULT_VISIBLE_ITEMS = 5;
    private Vibrator mVibrator;

    public DateTimePicker(Context context) {
        super(context);
        mContext = context;
    }

    public DateTimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public void init(Calendar calendar, String format) {
        mCalendar = calendar;
        init(format);
    }

    public void init(String format) {
        mFormat = format;
        if (WheelView.isRtl()) {
            LayoutInflater.from(mContext).inflate(R.layout.os_picker_date_layout_rtl, this, true);
        } else {
            LayoutInflater.from(mContext).inflate(R.layout.os_picker_date_layout, this, true);
        }

        initAndSetMargins();
        mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
    }

    private void initAndSetMargins() {
        switch (mFormat) {
            case DateTimePicker.FORMAT_Y_M_D_H_M:
                //doNothing
                initYearWheel();
                initMonthWheel();
                initDayWheel();
                initHourWheel();
                initMinuteWheel();
                break;
            case DateTimePicker.FORMAT_Y_M_D:
                initYearWheel();
                initMonthWheel();
                initDayWheel();
                break;
            case DateTimePicker.FORMAT_M_D_H_M:
                initMonthDayWheel();
                initHourWheel();
                initMinuteWheel();
                break;
            case DateTimePicker.FORMAT_M_D:
                initMonthWheel();
                initDayWheel();
                break;
            case DateTimePicker.FORMAT_H_M:
                initHourWheel();
                initMinuteWheel();
                break;
            default:
                break;
        }

        updateMargins();
    }

    private void initYearWheel() {
        int year = mCalendar.get(Calendar.YEAR);
        mYearWheel = findViewById(R.id.yearWheel);
        mYearWheel.setVisibility(VISIBLE);
        List<Integer> yearList = new ArrayList<>();

        for (int i = START_YEAR; i < (Math.max(year, END_YEAR) + 1); i++) {
            yearList.add(i);
        }
        mYearWheel.setData(yearList);
        mYearWheel.setVisibleItems(DEFAULT_VISIBLE_ITEMS);
        mYearWheel.setCyclic(true);
        mYearWheel.setSelectedItemPosition(year > START_YEAR ? year - START_YEAR : 0);
        mOnYearChangedListener = new WheelView.OnWheelChangedListener() {
            @Override
            public void onWheelScroll(int scrollOffsetY) {
            }

            @Override
            public void onWheelItemChanged(int oldValue, int newValue) {
                //Log.i(TAG, "yearWheel changed oldValue = " + oldValue + ",newvalue = " + newValue);
                onYearChange(START_YEAR + newValue);
                onVibrate();
            }

            @Override
            public void onWheelSelected(int position) {
            }

            @Override
            public void onWheelScrollStateChanged(int state) {
            }
        };
        mYearWheel.setOnWheelChangedListener(mOnYearChangedListener);
    }

    private void releaseYearWheel() {
        if (mYearWheel != null) {
            mYearWheel.setOnWheelChangedListener(mOnYearChangedListener);
            mOnYearChangedListener = null;
            mYearWheel = null;
        }
    }

    private void initMonthWheel() {
        int month = mCalendar.get(Calendar.MONTH);
        mMonthWheel = findViewById(R.id.monthWheel);
        mMonthWheel.setVisibility(VISIBLE);
        initMonthList();

        mMonthWheel.setData(mMonthList);
        mMonthWheel.setVisibleItems(DEFAULT_VISIBLE_ITEMS);
        mMonthWheel.setCyclic(true);
        mMonthWheel.setSelectedItemPosition(month);
        mOnMonthChangedListener = new WheelView.OnWheelChangedListener() {
            @Override
            public void onWheelScroll(int scrollOffsetY) {
            }

            @Override
            public void onWheelItemChanged(int oldValue, int newValue) {
                //Log.i(TAG, "monthWheel changed oldValue = " + oldValue + ",newvalue = " + newValue);
                if (newValue > oldValue) {
                    if (oldValue == mCalendar.getActualMinimum(Calendar.MONTH) && newValue - oldValue > 1) {//year -1
                        int year = mCalendar.get(Calendar.YEAR);
                        if (mYearWheel != null && mYearWheel.getData() != null) {
                            int total = mYearWheel.getData().size();
                            mYearWheel.setSelectedItemPosition((mYearWheel.getSelectedItemPosition() + total - 1) % total);
                        } else {
                            mCalendar.set(Calendar.YEAR, year - 1);
                        }
                        onMonthChange(mCalendar.getActualMaximum(Calendar.MONTH));
                    } else {
                        onMonthChange(newValue);
                    }
                } else if (newValue < oldValue) {
                    if (oldValue == mCalendar.getActualMaximum(Calendar.MONTH) && oldValue - newValue > 1) {//year + 1
                        int year = mCalendar.get(Calendar.YEAR);
                        if (mYearWheel != null && mYearWheel.getData() != null) {
                            int total = mYearWheel.getData().size();
                            mYearWheel.setSelectedItemPosition((mYearWheel.getSelectedItemPosition() + 1) % total);
                        } else {
                            mCalendar.set(Calendar.YEAR, year + 1);
                        }
                        onMonthChange(mCalendar.getActualMinimum(Calendar.MONTH));
                    } else {
                        onMonthChange(newValue);
                    }
                }
                onVibrate();
            }

            @Override
            public void onWheelSelected(int position) {
            }

            @Override
            public void onWheelScrollStateChanged(int state) {
            }
        };
        mMonthWheel.setOnWheelChangedListener(mOnMonthChangedListener);
    }

    private void releaseMonthWheel() {
        if (mMonthWheel != null) {
            mMonthWheel.setOnWheelChangedListener(mOnMonthChangedListener);
            mOnMonthChangedListener = null;
            mMonthList.clear();
            mMonthWheel = null;
        }
    }

    private void initDayWheel() {
        int day = mCalendar.get(Calendar.DAY_OF_MONTH);
        mDayWheel = findViewById(R.id.dayWheel);
        mDayWheel.setVisibility(VISIBLE);

        List<Integer> dayList = new ArrayList<>();
        for (int i = 1; i < mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH) + 1; i++) {
            dayList.add(i);
        }
        mDayWheel.setData(dayList);
        mDayWheel.setVisibleItems(DEFAULT_VISIBLE_ITEMS);
        mDayWheel.setCyclic(true);
        mDayWheel.setSelectedItemPosition(day - 1);
        mOnDayChangedListener = new WheelView.OnWheelChangedListener() {
            @Override
            public void onWheelScroll(int scrollOffsetY) {
            }

            @Override
            public void onWheelItemChanged(int oldValue, int newValue) {
                //os: add by guisheng.wang5 fix up bug:OSBRREL-916 wheelview滚动没重置 mSelectedItemPosition 手动重置，不然滚动年份或者月份会回到初始化位置 20210626 start
//                mDayWheel.setSelectedItemPosition(newValue);
                //os: add by guisheng.wang5 fix up bug:OSBRREL-916 wheelview滚动没重置 mSelectedItemPosition 手动重置，不然滚动年份或者月份会回到初始化位置 20210626 end
                onDaysChange(newValue + 1);
//                int min = mCalendar.getActualMinimum(Calendar.DAY_OF_MONTH);
//                int max = mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
//                Log.i(TAG, "dayWheel changed min = " + min + ",max = " + max);
//                if (newValue > oldValue) {
//                    if (oldValue == mCalendar.getActualMinimum(Calendar.DAY_OF_MONTH)-1 && newValue - oldValue > 1) {//month -1
//                        int minDay = mCalendar.getActualMinimum(Calendar.DAY_OF_MONTH);
//                        mCalendar.set(Calendar.DAY_OF_MONTH, minDay);                               //to avoid month auto +1
//                        int month = mCalendar.get(Calendar.MONTH);
//                        onMonthChange(month - 1);
//                        int maxDay = mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
//                        onDaysChange(maxDay);
//                    }else{
//
//                    }
//                } else if (newValue < oldValue) {
//                    if (oldValue == mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH) - 1 && oldValue - newValue > 1) {//month +1
//                        int minDay = mCalendar.getActualMinimum(Calendar.DAY_OF_MONTH);
//                        mCalendar.set(Calendar.DAY_OF_MONTH, minDay);
//                        int month = mCalendar.get(Calendar.MONTH);
//                        onMonthChange(month + 1);
//                    }else{
//                        onDaysChange(newValue + 1);
//                    }
//                }
                onVibrate();
            }

            @Override
            public void onWheelSelected(int position) {
            }

            @Override
            public void onWheelScrollStateChanged(int state) {
            }
        };
        mDayWheel.setOnWheelChangedListener(mOnDayChangedListener);
    }

    private void releaseDayWheel() {
        if (mDayWheel != null) {
            mDayWheel.setOnWheelChangedListener(mOnDayChangedListener);
            mOnDayChangedListener = null;
            mDayWheel = null;
        }
    }

    private void onDaysChange(int days) {
        mCalendar.set(Calendar.DAY_OF_MONTH, days);
        change();
    }

    private void onMonthChange(int month) {
        Calendar tempCalendar = Calendar.getInstance();
        tempCalendar.set(Calendar.DAY_OF_MONTH, 1);
        tempCalendar.set(Calendar.YEAR, mCalendar.get(Calendar.YEAR));
        tempCalendar.set(Calendar.MONTH, month);

        int nextMaxDays = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int currentDays = mCalendar.get(Calendar.DAY_OF_MONTH);

        if (nextMaxDays < currentDays) {
            mCalendar.set(Calendar.DAY_OF_MONTH, nextMaxDays);
        }

        mCalendar.set(Calendar.MONTH, month);
        updateDaysWheel();
        change();
    }

    private void onYearChange(int year) {
        Calendar tempCalendar = Calendar.getInstance();
        tempCalendar.set(Calendar.YEAR, year);
        tempCalendar.set(Calendar.MONTH, mCalendar.get(Calendar.MONTH));
        tempCalendar.set(Calendar.DAY_OF_MONTH, 1);
        int tempMaxDay = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int currentDay = mDayWheel.getSelectedItemPosition() + 1;
        if (tempMaxDay < currentDay) {
            mCalendar.set(Calendar.DAY_OF_MONTH, tempMaxDay);
        }
        mCalendar.set(Calendar.YEAR, year);
        updateDaysWheel();
        change();
    }

    private void initMonthDayWheel() {
        initMonthList();
        int day = mCalendar.get(Calendar.DAY_OF_YEAR);
        mMonthDayWheel = findViewById(R.id.monthDayWheel);
        mMonthDayWheel.setVisibility(VISIBLE);
        mMonthDayWheel.setYearDays(mCalendar, true);
        mMonthDayWheel.setMonthList(mMonthList);

        List<Integer> monthDayList = new ArrayList<>();
        for (int i = 1; i < mCalendar.getActualMaximum(Calendar.DAY_OF_YEAR) + 1; i++) {
            monthDayList.add(i);
        }
        mMonthDayWheel.setData(monthDayList);
        mMonthDayWheel.setVisibleItems(DEFAULT_VISIBLE_ITEMS);
        mMonthDayWheel.setCyclic(true);
        mMonthDayWheel.setSelectedItemPosition(day - 1);
        mOnMonthDayChangedLister = new WheelView.OnWheelChangedListener() {
            @Override
            public void onWheelScroll(int scrollOffsetY) {
            }

            @Override
            public void onWheelItemChanged(int oldValue, int newValue) {
                if (newValue > oldValue) {
                    if (oldValue == mCalendar.getActualMinimum(Calendar.DAY_OF_YEAR) - 1 && newValue - oldValue > 1) {//year -1
                        updateMonthDayData(mCalendar.get(Calendar.YEAR) - 1, true);
                    } else {
                        mCalendar.set(Calendar.DAY_OF_YEAR, newValue + 1);
                    }
                } else if (newValue < oldValue) {
                    if (oldValue == mCalendar.getActualMaximum(Calendar.DAY_OF_YEAR) - 1 && oldValue - newValue > 1) {//year +1
                        updateMonthDayData(mCalendar.get(Calendar.YEAR) + 1, false);
                    } else {
                        mCalendar.set(Calendar.DAY_OF_YEAR, newValue + 1);
                    }
                }
                change();
                onVibrate();
            }

            @Override
            public void onWheelSelected(int position) {
            }

            @Override
            public void onWheelScrollStateChanged(int state) {
            }
        };
        mMonthDayWheel.setOnWheelChangedListener(mOnMonthDayChangedLister);
    }

    private void releaseMonthDayWheel() {
        if (mMonthDayWheel != null) {
            mMonthDayWheel.setOnWheelChangedListener(mOnMonthDayChangedLister);
            mOnMonthDayChangedLister = null;
            mMonthDayWheel = null;
        }
    }

    private void updateMonthDayData(int yearValue, boolean maxValue) {
        mCalendar.set(Calendar.DAY_OF_YEAR, mCalendar.getMinimum(Calendar.DAY_OF_YEAR));            // avoid year auto +1
        mCalendar.set(Calendar.YEAR, yearValue);
        mCalendar.set(Calendar.DAY_OF_YEAR, maxValue ? mCalendar.getActualMaximum(Calendar.DAY_OF_YEAR)
                : mCalendar.getMinimum(Calendar.DAY_OF_YEAR));

        List<Integer> monthDayList = new ArrayList<>();
        for (int i = 1; i < mCalendar.getActualMaximum(Calendar.DAY_OF_YEAR) + 1; i++) {
            monthDayList.add(i);
        }
        mMonthDayWheel.setData(monthDayList);
        int day = mCalendar.get(Calendar.DAY_OF_YEAR);
        mMonthDayWheel.setOnWheelChangedListener(null);
        mMonthDayWheel.setSelectedItemPosition(day - 1);
        mMonthDayWheel.setOnWheelChangedListener(mOnMonthDayChangedLister);
    }

    public static boolean is24HourFormat(Context context) {
        return DateFormat.is24HourFormat(context);
    }

    private void initHourWheel() {
        mHourWheel = findViewById(R.id.hourWheel);
        mHourWheel.setVisibility(VISIBLE);
        mFormatWheel = findViewById(R.id.formatWheel);
        mIs24Format = is24HourFormat(mContext);
        mHourWheel.setHourWheel(true);
        mHourWheel.set24HoursFormat(mIs24Format);
        final int hour = mIs24Format ? mCalendar.get(Calendar.HOUR_OF_DAY) : mCalendar.get(Calendar.HOUR);
        if (mIs24Format) {
            List<Integer> hourList = new ArrayList<>();
            for (int i = 0; i < 24; i++) {
                hourList.add(i);
            }
            mHourWheel.setData(hourList);
            mHourWheel.setSelectedItemPosition(hour);
        } else {
            mAmPmList.add(mContext.getString(R.string.am));
            mAmPmList.add(mContext.getString(R.string.pm));

            List<Integer> hourList = new ArrayList<>();
            for (int i = 1; i < 13; i++) {
                hourList.add(i);
            }
            mHourWheel.setData(hourList);
            mHourWheel.setSelectedItemPosition(((hour + 12) - 1) % 12);

            int amPm = mCalendar.get(Calendar.AM_PM);
            mFormatWheel.setData(mAmPmList);
            mFormatWheel.setVisibleItems(3);
            mFormatWheel.setCyclic(false);
            mFormatWheel.setSelectedItemPosition(amPm);
            mOnFormatChangedListener = new WheelView.OnWheelChangedListener() {
                @Override
                public void onWheelScroll(int scrollOffsetY) {
                }

                @Override
                public void onWheelItemChanged(int oldValue, int newValue) {
                    mCalendar.set(Calendar.AM_PM, newValue);
                    change();
                    onVibrate();
                }

                @Override
                public void onWheelSelected(int position) {
                }

                @Override
                public void onWheelScrollStateChanged(int state) {
                }
            };
            mFormatWheel.setOnWheelChangedListener(mOnFormatChangedListener);
        }
        mHourWheel.setVisibleItems(DEFAULT_VISIBLE_ITEMS);
        mHourWheel.setCyclic(true);
        mOnHourChangedListener = new WheelView.OnWheelChangedListener() {
            @Override
            public void onWheelScroll(int scrollOffsetY) {
            }

            @Override
            public void onWheelItemChanged(int oldValue, int newValue) {
                int realHour = mIs24Format ? newValue : (newValue + 1) % 12;
                mCalendar.set(mIs24Format ? Calendar.HOUR_OF_DAY : Calendar.HOUR, realHour);
                //os: modify by guisheng.wang5 OSBRREL-2886 20210713 start
//                if (!mIs24Format && mFormatWheel != null) {
//                    int nowAmPm = mCalendar.get(Calendar.AM_PM);
//                    mFormatWheel.setSelectedItemPosition(nowAmPm);
//                }
                //os: modify by guisheng.wang5 OSBRREL-2886 20210713 end
                change();
                onVibrate();
            }

            @Override
            public void onWheelSelected(int position) {
            }

            @Override
            public void onWheelScrollStateChanged(int state) {
            }
        };
        mHourWheel.setOnWheelChangedListener(mOnHourChangedListener);
    }

    private void releaseHourWheel() {
        if (mHourWheel != null) {
            mHourWheel.setOnWheelChangedListener(mOnHourChangedListener);
            mOnHourChangedListener = null;
            mHourWheel = null;
        }
    }

    private void initMinuteWheel() {
        int minute = mCalendar.get(Calendar.MINUTE);

        mMinuteWheel = findViewById(R.id.minWheel);
        mMinuteWheel.setVisibility(VISIBLE);
        List<Integer> minuteList = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            minuteList.add(i);
        }
        mMinuteWheel.setMinuteWheel(true);
        mMinuteWheel.setData(minuteList);
        mMinuteWheel.setSelectedItemPosition(minute);
        mMinuteWheel.setVisibleItems(DEFAULT_VISIBLE_ITEMS);
        mMinuteWheel.setCyclic(true);
        mOnMinuteChangedListener = new WheelView.OnWheelChangedListener() {
            @Override
            public void onWheelScroll(int scrollOffsetY) {
            }

            @Override
            public void onWheelItemChanged(int oldValue, int newValue) {
                mCalendar.set(Calendar.MINUTE, newValue);
                change();
                onVibrate();
            }

            @Override
            public void onWheelSelected(int position) {
            }

            @Override
            public void onWheelScrollStateChanged(int state) {
            }
        };
        mMinuteWheel.setOnWheelChangedListener(mOnMinuteChangedListener);
    }

    private void releaseMinuteWheel() {
        if (mMinuteWheel != null) {
            mMinuteWheel.setOnWheelChangedListener(mOnMinuteChangedListener);
            mOnMinuteChangedListener = null;
            mMinuteWheel = null;
        }
    }

    private void releaseFormatWheel() {
        if (mFormatWheel != null) {
            mAmPmList.clear();
            mFormatWheel.setOnWheelChangedListener(mOnFormatChangedListener);
            mOnFormatChangedListener = null;
            mFormatWheel = null;
        }
    }


    private void initMonthList() {
        Resources resources = mContext.getResources();
        mMonthList.add(resources.getString(R.string.jan));
        mMonthList.add(resources.getString(R.string.feb));
        mMonthList.add(resources.getString(R.string.mar));
        mMonthList.add(resources.getString(R.string.apr));
        mMonthList.add(resources.getString(R.string.may));
        mMonthList.add(resources.getString(R.string.jun));
        mMonthList.add(resources.getString(R.string.jul));
        mMonthList.add(resources.getString(R.string.aug));
        mMonthList.add(resources.getString(R.string.sep));
        mMonthList.add(resources.getString(R.string.oct));
        mMonthList.add(resources.getString(R.string.nov));
        mMonthList.add(resources.getString(R.string.dec));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void updateDaysWheel() {
        if (mDayWheel != null) {
            int currentDay = mDayWheel.getSelectedItemPosition() + 1;
            int maxValue = mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            List<Integer> dayList = new ArrayList<>();
            for (int i = 1; i < maxValue + 1; i++) {
                dayList.add(i);
            }
            mDayWheel.setData(dayList);
            if (currentDay < maxValue) {
                mDayWheel.setSelectedItemPosition(currentDay - 1);
            } else {
                mDayWheel.setSelectedItemPosition(maxValue - 1);
            }
        }
    }

    public interface OnDateTimeChangeListener {
        void onChange(DateTimePicker picker, Calendar calendar);
    }

    private void change() {
        if (mOnDateTimeChangeListener != null) {
            mOnDateTimeChangeListener.onChange(this, mCalendar);
        }
    }

    public void setOnDateChangeListener(OnDateTimeChangeListener onChangeListener) {
        this.mOnDateTimeChangeListener = onChangeListener;
        change();
    }

    public boolean is24Format() {
        return mIs24Format;
    }

    public void updateMargins() {
        int yearMarginStart = 0, monthMarginStart = 0, dayMarginStart = 0, monthDayMarginStart = 0, hourMarginStart = 0, minuteMarginStart = 0, formatStart = 0;
        switch (mFormat) {
            case FORMAT_Y_M_D_H_M:
                //doNothing
                break;
            case FORMAT_Y_M_D:
                if (WheelView.isRtl()) {
                    yearMarginStart = dp2px(28);
                    monthMarginStart = dp2px(8);
                    dayMarginStart = dp2px(30);
                } else {
                    yearMarginStart = dp2px(36);
                    monthMarginStart = dp2px(16);
                    dayMarginStart = dp2px(10);
                }
                break;
            case FORMAT_M_D_H_M:
                if (WheelView.isRtl()) {
                    if (mIs24Format) {
                        monthDayMarginStart = dp2px(16);
                        hourMarginStart = dp2px(36);
                        minuteMarginStart = dp2px(46);
                        mFormatWheel.setVisibility(GONE);
                    } else {
                        monthDayMarginStart = dp2px(0);
                        hourMarginStart = dp2px(12);
                        minuteMarginStart = dp2px(12);
                        formatStart = dp2px(0);
                        mFormatWheel.setVisibility(VISIBLE);
                    }
                } else {
                    if (mIs24Format) {
                        monthDayMarginStart = dp2px(20);
                        hourMarginStart = dp2px(26);
                        minuteMarginStart = dp2px(36);
                        mFormatWheel.setVisibility(GONE);
                    } else {
                        monthDayMarginStart = dp2px(6);
                        hourMarginStart = dp2px(8);
                        minuteMarginStart = dp2px(17);
                        formatStart = dp2px(0);
                        mFormatWheel.setVisibility(VISIBLE);
                    }
                }
                break;
            case FORMAT_M_D:

                break;
            case FORMAT_H_M:
                if (WheelView.isRtl()) {
                    if (mIs24Format) {
                        hourMarginStart = dp2px(72);
                        minuteMarginStart = dp2px(88);
                        mFormatWheel.setVisibility(GONE);
                    } else {
                        hourMarginStart = dp2px(56);
                        minuteMarginStart = dp2px(40);
                        formatStart = dp2px(40);
                        mFormatWheel.setVisibility(VISIBLE);
                    }
                } else {
                    if (mIs24Format) {
                        hourMarginStart = dp2px(88);
                        minuteMarginStart = dp2px(72);
                        mFormatWheel.setVisibility(GONE);
                    } else {
                        hourMarginStart = dp2px(40);
                        minuteMarginStart = dp2px(56);
                        formatStart = dp2px(40);
                        mFormatWheel.setVisibility(VISIBLE);
                    }
                }
                break;
            default:
                break;
        }

        setMarginStart(yearMarginStart, monthMarginStart, dayMarginStart, monthDayMarginStart, hourMarginStart, minuteMarginStart, formatStart);
    }

    private void setMarginStart(int yearMarginStart, int monthMarginStart, int dayMarginStart, int monthDayMarginStart, int hourMarginStart, int minuteMarginStart, int formatStart) {
        if (mMonthWheel != null && monthMarginStart != 0) {
            LayoutParams lpMonth = (LayoutParams) mMonthWheel.getLayoutParams();
            lpMonth.setMarginStart(monthMarginStart);
        }

        if (mDayWheel != null && dayMarginStart != 0) {
            LayoutParams lpDay = (LayoutParams) mDayWheel.getLayoutParams();
            lpDay.setMarginStart(dayMarginStart);
        }

        if (mMonthDayWheel != null && monthDayMarginStart != 0) {
            LayoutParams lpMonthDay = (LayoutParams) mMonthDayWheel.getLayoutParams();
            lpMonthDay.setMarginStart(monthDayMarginStart);
        }

        if (mYearWheel != null && yearMarginStart != 0) {
            LayoutParams lpYear = (LayoutParams) mYearWheel.getLayoutParams();
            lpYear.setMarginStart(yearMarginStart);
        }

        if (mHourWheel != null && hourMarginStart != 0) {
            LayoutParams lpHour = (LayoutParams) mHourWheel.getLayoutParams();
            lpHour.setMarginStart(hourMarginStart);
        }

        if (mMinuteWheel != null && minuteMarginStart != 0) {
            LayoutParams lpMinute = (LayoutParams) mMinuteWheel.getLayoutParams();
            lpMinute.setMarginStart(minuteMarginStart);
        }

        if (mFormatWheel != null && formatStart != 0) {
            LayoutParams lpFormat = (LayoutParams) mFormatWheel.getLayoutParams();
            lpFormat.setMarginStart(formatStart);
        }
    }

    private int dp2px(int dpValues) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpValues, mContext.getResources().getDisplayMetrics());
    }

    public void setWheelBackgroundColorRes(int colorResId) {
        if (mContext != null) {
            setWheelBackgroundColor(ContextCompat.getColor(mContext, colorResId));
        }
    }

    public void setWheelBackgroundColor(int color) {
        setWheelBackground(mYearWheel, color);
        setWheelBackground(mMonthWheel, color);
        setWheelBackground(mDayWheel, color);
        setWheelBackground(mMonthDayWheel, color);
        setWheelBackground(mFormatWheel, color);
        setWheelBackground(mHourWheel, color);
        setWheelBackground(mMinuteWheel, color);
    }

    private void setWheelBackground(WheelView wheelView, int color) {
        if (wheelView != null) {
            wheelView.setWheelBackgroundColor(color);
        }
    }

    public void updateTime(int hour, int minute) {
        if (mCalendar != null) {
            mCalendar.set(Calendar.HOUR_OF_DAY, hour);
            mCalendar.set(Calendar.MINUTE, minute);

            release();
            refreshUI();
        }
    }

    public void updateDate(int year, int month, int day) {
        if (mCalendar != null) {
            mCalendar.set(Calendar.YEAR, year);
            mCalendar.set(Calendar.MONTH, month);
            mCalendar.set(Calendar.DAY_OF_MONTH, day);

            release();
            refreshUI();
        }
    }

    public void updateDateTime(int year, int month, int day, int hour, int minute) {
        if (mCalendar != null) {
            mCalendar.set(Calendar.YEAR, year);
            mCalendar.set(Calendar.MONTH, month);
            mCalendar.set(Calendar.DAY_OF_MONTH, day);
            mCalendar.set(Calendar.HOUR_OF_DAY, hour);
            mCalendar.set(Calendar.MINUTE, minute);

            release();
            refreshUI();
        }
    }

    public void release() {
        releaseYearWheel();
        releaseMonthWheel();
        releaseDayWheel();
        releaseMonthDayWheel();
        releaseHourWheel();
        releaseMinuteWheel();
        releaseFormatWheel();
    }

    private void refreshUI() {
        initAndSetMargins();
        if (mOnDateTimeChangeListener != null) {
            change();
        }
    }

    public void setYearDuration(int startYear, int endYear) {
        if (endYear < startYear) {
            return;
        } else {
            START_YEAR = startYear;
            END_YEAR = endYear;
        }
    }

    //private static final boolean IS_4D_VIBRATE_SUPPORT = "1".equals(getSystemProperties("ro.tran_vibrate_ontouch.support"));
    private static final boolean IS_4D_VIBRATE_SUPPORT = true;

    private void onVibrate() {
        Handler viewHandler = getHandler();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (IS_4D_VIBRATE_SUPPORT && viewHandler != null && !viewHandler.hasCallbacks(mVibrateRunnable)) {
                viewHandler.postDelayed(mVibrateRunnable, 50);
            }
        }
    }

    private final Runnable mVibrateRunnable = new Runnable() {
        @Override
        public void run() {
            maybeVibrate();
        }
    };

    private void maybeVibrate() {
        final boolean hapticsDisable = Settings.System.getInt(getContext().getContentResolver(), Settings.System.HAPTIC_FEEDBACK_ENABLED, 0) == 0;
        if (!hapticsDisable && mVibrator != null && mVibrator.hasVibrator()) {
            mVibrator.cancel();
            mVibrator.vibrate(new long[]{0, 40}, -1);
        }
    }
}
