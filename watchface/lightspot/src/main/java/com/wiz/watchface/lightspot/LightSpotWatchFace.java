package com.wiz.watchface.lightspot;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.wiz.watchface.lightspot.view.MaskBallView;
import com.wiz.watchface.lightspot.view.TimeDataView;

public class LightSpotWatchFace extends FrameLayout {
    private boolean mIsEditMode;
    private int mStyle;
    private TimeDataView mTimeDataView;
    private MaskBallView mMaskBallView;

    public LightSpotWatchFace(Context context, boolean isEditMode, int style) {
        super(context);
        mIsEditMode = isEditMode;
        mStyle = style;
        this.init();
    }

    public LightSpotWatchFace(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public void init() {
        setBackgroundColor(Color.BLACK);

        View.inflate(getContext(), R.layout.view_lightspot_face, this);

        mTimeDataView = findViewById(R.id.time_date_time);
        mMaskBallView = findViewById(R.id.mask_ball_view);

        mTimeDataView.setEditMode(mIsEditMode);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.setViewStyle();
    }


    private void setViewStyle() {
        mMaskBallView.setStyle(mStyle);
        if (mStyle == 1) {
            mTimeDataView.setMinuteColor(ContextCompat.getColor(getContext(), R.color.minute_1));
        } else if (mStyle == 2) {
            mTimeDataView.setMinuteColor(ContextCompat.getColor(getContext(), R.color.minute_2));
        } else {
            mTimeDataView.setMinuteColor(ContextCompat.getColor(getContext(), R.color.minute));
        }
    }
}
