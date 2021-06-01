package com.wiz.watch.facedynamic;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.wiz.watch.facedynamic.view.ClockPanel;
import com.wiz.watch.facedynamic.view.WeekPanel;

public class DynamicWatchFace extends FrameLayout {
    private int mStyle;
    private boolean mIsEditMode;
    private ClockPanel mClockPanel, mSecondPanel;
    private WeekPanel mWeekPanel;

    public DynamicWatchFace(Context context, int style, boolean isEditMode) {
        super(context);
        mStyle = style;
        mIsEditMode = isEditMode;
        this.initView();
    }

    public DynamicWatchFace(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.initView();
    }

    public void initView() {
        View.inflate(getContext(), R.layout.view_dynamic_face, this);

        mClockPanel = findViewById(R.id.clock_panel);
        mSecondPanel = findViewById(R.id.second_clock_panel);
        mClockPanel.setEditMode(mIsEditMode);
        mSecondPanel.setEditMode(mIsEditMode);

        mWeekPanel = findViewById(R.id.week_panel);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.setViewStyle();
    }

    private void setViewStyle() {
        if (mStyle == 1) {
            setBackground(ContextCompat.getDrawable(getContext(), R.drawable.face_panel_2));
            mClockPanel.setHourPointer(BitmapFactory.decodeResource(getResources(), R.drawable.hour_pointer_2));
            mClockPanel.setMinutePointer(BitmapFactory.decodeResource(getResources(), R.drawable.minute_pointer_2));

            mSecondPanel.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.second_panel_2));
            mSecondPanel.setSecondPointer(BitmapFactory.decodeResource(getResources(), R.drawable.second_pointer_2));

            mWeekPanel.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.week_panel_2));
            mWeekPanel.setPointer(BitmapFactory.decodeResource(getResources(), R.drawable.week_pointer_2));
        } else if (mStyle == 2) {
            setBackground(ContextCompat.getDrawable(getContext(), R.drawable.face_panel));
            mClockPanel.setHourPointer(BitmapFactory.decodeResource(getResources(), R.drawable.hour_pointer));
            mClockPanel.setMinutePointer(BitmapFactory.decodeResource(getResources(), R.drawable.minute_pointer));

            mSecondPanel.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.second_panel));
            mSecondPanel.setSecondPointer(BitmapFactory.decodeResource(getResources(), R.drawable.second_pointer));

            mWeekPanel.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.week_panel));
            mWeekPanel.setPointer(BitmapFactory.decodeResource(getResources(), R.drawable.week_pointer));
        } else {
            setBackground(ContextCompat.getDrawable(getContext(), R.drawable.face_panel_3));
            mClockPanel.setHourPointer(BitmapFactory.decodeResource(getResources(), R.drawable.hour_pointer_3));
            mClockPanel.setMinutePointer(BitmapFactory.decodeResource(getResources(), R.drawable.minute_pointer_3));

            mSecondPanel.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.second_panel_3));
            mSecondPanel.setSecondPointer(BitmapFactory.decodeResource(getResources(), R.drawable.second_pointer_3));

            mWeekPanel.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.week_panel_3));
            mWeekPanel.setPointer(BitmapFactory.decodeResource(getResources(), R.drawable.week_pointer_3));
        }
    }
}
