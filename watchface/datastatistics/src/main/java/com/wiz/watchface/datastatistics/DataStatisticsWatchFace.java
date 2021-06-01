package com.wiz.watchface.datastatistics;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wiz.watchface.datastatistics.view.KcalView;
import com.wiz.watchface.datastatistics.view.StepsView;
import com.wiz.watchface.datastatistics.view.TimeView;
import com.wiz.watchface.datastatistics.view.WeatherView;

public class DataStatisticsWatchFace extends FrameLayout {
    private KcalView mKcalView;
    private StepsView mStepsView;
    private WeatherView mWeatherView;
    private TimeView mTimeView;
    private boolean mIsEditMode, mIsDimMode;

    public DataStatisticsWatchFace(Context context) {
        super(context);
        this.init();
    }

    public DataStatisticsWatchFace(Context context, boolean isEditMode) {
        super(context);
        mIsEditMode = isEditMode;
        this.init();
    }

    public DataStatisticsWatchFace(Context context, boolean isEditMode, boolean isDimMode) {
        super(context);
        mIsEditMode = isEditMode;
        mIsDimMode = isDimMode;
        this.init();
    }

    public DataStatisticsWatchFace(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public void init() {
        setBackgroundColor(Color.BLACK);
        View.inflate(getContext(), R.layout.view_data_statistics_face, this);

        mKcalView = findViewById(R.id.kcal_view);
        mStepsView = findViewById(R.id.steps_view);
        mWeatherView = findViewById(R.id.weather_view);
        mTimeView = findViewById(R.id.time_view);

        mKcalView.setEditMode(mIsEditMode);
        mStepsView.setEditMode(mIsEditMode);
        mWeatherView.setEditMode(mIsEditMode);
        mTimeView.setEditMode(mIsEditMode);

        mKcalView.setDimMode(mIsDimMode);
        mStepsView.setDimMode(mIsDimMode);
        mWeatherView.setDimMode(mIsDimMode);
        mTimeView.setDimMode(mIsDimMode);
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
    }


    public void updateTime() {
        mTimeView.updateTime();
        mTimeView.invalidate();
    }

}
