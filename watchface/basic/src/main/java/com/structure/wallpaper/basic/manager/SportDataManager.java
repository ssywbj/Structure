package com.structure.wallpaper.basic.manager;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class SportDataManager {
    private static final String TAG = SportDataManager.class.getSimpleName();
    private static final String AUTHORITY = "com.wiz.watch.health.provider";
    private static final Uri URI_HEART_RATE = Uri.parse("content://" + AUTHORITY + "/heart_rate");
    private static final String CALL_HEART_RATE_DATA = "heart_rate/heart_rate_data";
    private final Context mContext;

    private int mCalories; //卡路里
    private int mSteps; //步数
    private long mSportTime; //运动时长：毫秒
    private int mHeartRate; //心率

    private long mCaloriesTarget; //目标卡路里
    private long mStepsTarget; //目标步数
    private long mSportTimeTarget; //目标运动时长：毫秒

    private HeartRateListener mHeartRateListener;

    private final ContentObserver mContentObserver = new ContentObserver(null) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            if (uri.toString().startsWith(URI_HEART_RATE.toString())) {
                queryHeartRate();
                if (mHeartRateListener != null) {
                    mHeartRateListener.onChange(selfChange, uri);
                }
            }
        }
    };

    public SportDataManager(Context context) {
        mContext = context;
    }

    public void registerContentHeartRate() {
        try {
            mContext.getContentResolver().registerContentObserver(URI_HEART_RATE, true, mContentObserver);
        } catch (Exception e) {
            Log.e(TAG, "register content observer error: " + e.toString(), new Exception());
        }
    }

    public void unregisterContentHeartRate() {
        mContext.getContentResolver().unregisterContentObserver(mContentObserver);
    }

    public void queryStepAndCalories() {
        try {
            Bundle bundle = mContext.getContentResolver().call(Uri.parse("content://" + AUTHORITY),
                    "sport_step/query_current", mContext.getPackageName(), null);
            if (bundle == null) {
                Log.e(TAG, "query sport data error: bundle is null");
                return;
            }

            mCalories = (int) bundle.getFloat("calorie");
            mSteps = bundle.getInt("step");
            mSportTime = bundle.getLong("time");
            mCaloriesTarget = bundle.getLong("tc");
            mStepsTarget = bundle.getLong("ts");
            mSportTimeTarget = bundle.getLong("tt");

            /*mCalories = new Random().nextInt((int) mCaloriesTarget);
            mSteps = new Random().nextInt((int) mStepsTarget);*/

            /*Log.d(TAG, "calories: " + mCalories + ", steps: " + mSteps + ", sport time: " + mSportTime +
                    ", calories target: " + mCaloriesTarget + ", steps target: " + mStepsTarget + ", sport time target: " + mSportTimeTarget);*/
        } catch (Exception e) {
            Log.e(TAG, "query step and calories error: " + e.toString(), new Exception());
        }
    }

    public void queryHeartRate() {
        try {
            Bundle bundle = mContext.getContentResolver().call(Uri.parse("content://" + AUTHORITY)
                    , CALL_HEART_RATE_DATA, null, null);
            if (bundle == null) {
                Log.e(TAG, "query heart rate error: bundle is null");
                return;
            }
            long[] data = bundle.getLongArray(CALL_HEART_RATE_DATA);
            if (data == null || data.length == 0) {
                Log.e(TAG, "query heart rate error, data is null or empty");
                return;
            }
            mHeartRate = (int) data[0];
            Log.d(TAG, "heart rate: " + mHeartRate);
            //heartRate = 104;
        } catch (Exception e) {
            Log.e(TAG, "query heart rate error: " + e.toString(), new Exception());
        }
    }

    public float getCaloriesProgress() {
        if (mCaloriesTarget <= 0) {
            return 0;
        }
        final float progress = 1f * mCalories / mCaloriesTarget;
        return progress > 1 ? 1.0f : progress;
    }

    public float getStepsProgress() {
        if (mStepsTarget <= 0) {
            return 0;
        }
        final float progress = 1f * mSteps / mStepsTarget;
        return progress > 1 ? 1.0f : progress;
    }

    public float getSportTimeProgress() {
        if (mSportTimeTarget <= 0) {
            return 0;
        }
        final float progress = 1f * mSportTime / mSportTimeTarget;
        return progress > 1 ? 1.0f : progress;
    }

    public int getCalories() {
        return mCalories;
    }

    public int getSteps() {
        return mSteps;
    }

    public long getSportTime() {
        return mSportTime;
    }

    public int getHeartRate() {
        return mHeartRate;
    }

    public void setHeartRateListener(HeartRateListener heartRateListener) {
        mHeartRateListener = heartRateListener;
    }

    public interface HeartRateListener {
        void onChange(boolean selfChange, Uri uri);
    }
}
