package com.wiz.watchface.auspiciousdragon;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.wiz.watchface.auspiciousdragon.view.ClockPanel;

public class AuspiciousDragonWatchFace extends FrameLayout {
    private boolean mIsEditMode, mIsDimMode;
    private ClockPanel mClockPanel;

    public AuspiciousDragonWatchFace(Context context, boolean isEditMode, boolean isDimMode) {
        super(context);
        mIsEditMode = isEditMode;
        mIsDimMode = isDimMode;
        this.init();
    }

    public AuspiciousDragonWatchFace(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public void init() {
        setBackgroundColor(Color.BLACK);

        View.inflate(getContext(), R.layout.view_auspiciousdragon_face, this);

        mClockPanel = findViewById(R.id.clock_panel);
        mClockPanel.setEditMode(mIsEditMode);
        mClockPanel.setDimMode(mIsDimMode);
        if (mIsDimMode) {
            mClockPanel.setBgTexture(BitmapFactory.decodeResource(getResources(), R.drawable.w_bg_dim));
            mClockPanel.setSecondPointer(null);
        } else {
            mClockPanel.setBgTexture(BitmapFactory.decodeResource(getResources(), R.drawable.w_bg));
            mClockPanel.setSecondPointer(BitmapFactory.decodeResource(getResources(), R.drawable.second_spot));
        }
        mClockPanel.init();
    }

}
