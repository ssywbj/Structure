package com.wiz.watch.facefacingchallenges;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.structure.wallpaper.basic.manager.SportDataManager;
import com.structure.wallpaper.basic.utils.IntentHelper;

public class FacingChallengesWatchFace extends LinearLayout{
    private TextView mTextSteps, mTextSportTime, mTextCalories;
    private boolean mIsEditMode, mIsDimMode;
    private VerticalTimeView mVerticalTimeView;
    private SportDataManager mSportDataManager;
    private final int[] mSportTime = new int[4];

    public FacingChallengesWatchFace(Context context, boolean isEditMode, boolean isDimMode) {
        super(context);
        mIsEditMode = isEditMode;
        mIsDimMode = isDimMode;
        this.init();
    }

    public FacingChallengesWatchFace(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public void init() {
        View.inflate(getContext(), R.layout.view_facing_challenges_watch_face, this);

        mVerticalTimeView = findViewById(R.id.vertical_time_view);
        mVerticalTimeView.setEditMode(mIsEditMode);
        mVerticalTimeView.setDimMode(mIsDimMode);
        mTextSteps = findViewById(R.id.text_steps);
        mTextSportTime = findViewById(R.id.text_sport_time);
        mTextCalories = findViewById(R.id.text_calories);

        if (!mIsEditMode) {
            findViewById(R.id.layout_bottom).setOnClickListener((View v) ->
                    IntentHelper.openApp(getContext(), "com.wiz.watch.health.action.SPORT_MAIN_LAUNCHER")
            );
        }

        mSportDataManager = new SportDataManager(getContext());

        this.queryStepAndCalories();
        this.setStepAndCalories();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        //Log.d("FacingChallengesWatchFace", "onAttachedToWindow");
        if (!mIsEditMode || !mIsDimMode) {
            mVerticalTimeView.setTimeUpdateListener(() -> {
                //Log.i("FacingChallengesWatchFace", "setTimeUpdateListener: " + FacingChallengesWatchFace.this, new Exception());
                queryStepAndCalories();
                setStepAndCalories();
            });

            postDelayed(() -> {
                this.queryStepAndCalories();
                this.setStepAndCalories();
            }, 300);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mVerticalTimeView.setTimeUpdateListener(null);
    }

    @Override
    public void onScreenStateChanged(int screenState) {
        super.onScreenStateChanged(screenState);
        //Log.d("FacingChallengesWatchFace", "onScreenStateChanged(int): " + screenState);
        boolean visible = (screenState == SCREEN_STATE_ON);
        if (visible) {
            this.queryStepAndCalories();
            this.setStepAndCalories();
        }
    }

    private void queryStepAndCalories() {
        mSportDataManager.queryStepAndCalories();

        long sportTime = mSportDataManager.getSportTime() / 1000;
        int hour = (int) (sportTime / (60 * 60));
        int minute = (int) ((sportTime / 60) % 60);
        mSportTime[0] = hour / 10;
        mSportTime[1] = hour % 10;
        mSportTime[2] = minute / 10;
        mSportTime[3] = minute % 10;
    }

    @SuppressLint("SetTextI18n")
    private void setStepAndCalories() {
        mTextSportTime.setText(mSportTime[0] + "" + mSportTime[1] + ":" + mSportTime[2] + "" + mSportTime[3]);

        mTextSteps.setText(mSportDataManager.getSteps() + "");
        mTextCalories.setText(mSportDataManager.getCalories() + "");
    }

}
