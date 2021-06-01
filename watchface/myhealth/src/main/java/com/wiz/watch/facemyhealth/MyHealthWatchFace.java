package com.wiz.watch.facemyhealth;

import android.content.Context;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.structure.wallpaper.basic.manager.SportDataManager;
import com.structure.wallpaper.basic.utils.DateUtil;
import com.structure.wallpaper.basic.utils.DimenUtil;
import com.structure.wallpaper.basic.view.FaceAnimView;

import java.util.Calendar;

public class MyHealthWatchFace extends FaceAnimView {
    private final String AUTHORITY = "com.wiz.watch.health.provider";
    public final Uri URI_HEART_RATE = Uri.parse("content://" + AUTHORITY + "/heart_rate");
    public final String CALL_HEART_RATE_DATA = "heart_rate/heart_rate_data";

    private static final int SCALES = 6;
    private static final float SCALE_DEGREES = 1.0f * 360 / SCALES;
    private float mRadiusOuter, mRadiusInner;

    private Paint mPaint;
    private final RectF mRectF = new RectF();
    protected PointF mPointScreenCenter = new PointF();//屏幕中心点
    protected int mBatteryLevel;

    private int mLittleTriangleHeight;

    private MyHealthBitmapManager mBitmapManager;
    private final int[] mCalories = new int[5];
    private final int[] mHeartRate = new int[3];
    private final int[] mSportTime = new int[4];
    private final int[] mSteps = new int[6];

    private float mScale;

    private SportDataManager mSportDataManager;

    private final ContentObserver mContentObserver = new ContentObserver(null) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            //Log.d(mTAG, "onChange, uri: " + uri);
            if (uri.toString().startsWith(URI_HEART_RATE.toString())) {
                queryHeartRate();
            }
        }
    };

    public MyHealthWatchFace(Context context, boolean isEditMode, boolean isDimMode) {
        super(context, isEditMode, false, isDimMode);
        this.init();
    }

    public MyHealthWatchFace(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    private void queryStepAndCalories() {
        mSportDataManager.queryStepAndCalories();

        int calories = mSportDataManager.getCalories();
        mCalories[0] = calories / 10000;
        mCalories[1] = (calories % 10000) / 1000;
        mCalories[2] = (calories % 1000) / 100;
        mCalories[3] = (calories % 100) / 10;
        mCalories[4] = calories % 10;

        int steps = mSportDataManager.getSteps();
        mSteps[0] = steps / 100000;
        mSteps[1] = (steps % 100000) / 10000;
        mSteps[2] = (steps % 10000) / 1000;
        mSteps[3] = (steps % 1000) / 100;
        mSteps[4] = (steps % 100) / 10;
        mSteps[5] = steps % 10;

        long sportTime = mSportDataManager.getSportTime() / 1000;
        int hour = (int) (sportTime / (60 * 60));
        int minute = (int) ((sportTime / 60) % 60);
        mSportTime[0] = hour / 10;
        mSportTime[1] = hour % 10;
        mSportTime[2] = minute / 10;
        mSportTime[3] = minute % 10;
    }

    private void queryHeartRate() {
        try {
            Bundle bundle = mContext.getContentResolver().call(Uri.parse("content://" + AUTHORITY)
                    , CALL_HEART_RATE_DATA, null, null);
            if (bundle == null) {
                Log.e(mTAG, "query heart rate error: bundle is null");
                return;
            }
            long[] data = bundle.getLongArray(CALL_HEART_RATE_DATA);
            if (data == null || data.length == 0) {
                Log.e(mTAG, "query heart rate error, data is null or empty");
                return;
            }
            int heartRate = (int) data[0];
            Log.d(mTAG, "heart rate: " + heartRate);
            //heartRate = 104;
            mHeartRate[0] = heartRate / 100;
            mHeartRate[1] = (heartRate % 100) / 10;
            mHeartRate[2] = heartRate % 10;
        } catch (Exception e) {
            Log.e(mTAG, "query heart rate error: " + e.toString(), new Exception());
        }
    }

    public void init() {
        needAppearAnimNumber();
        setDefaultTime(8, 36, 55);
        mBitmapManager = new MyHealthBitmapManager(mContext);
        mSportDataManager = new SportDataManager(mContext);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        mScale = Float.parseFloat(mContext.getString(R.string.ratio));
        mBatteryLevel = getBatteryLevel();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPointScreenCenter.x = w / 2f;
        mPointScreenCenter.y = h / 2f;
        mRadiusOuter = Math.min(mPointScreenCenter.x, mPointScreenCenter.y) - DimenUtil.dip2px(mContext, 1);
        mRadiusInner = mRadiusOuter - getResources().getDimension(R.dimen.inner_radius_offset);
    }

    @Override
    public void onVisibilityChanged(boolean visible) {
        super.onVisibilityChanged(visible);
        if (visible) {
            try {
                this.queryHeartRate();
                mContext.getContentResolver().registerContentObserver(URI_HEART_RATE, true, mContentObserver);
            } catch (Exception e) {
                Log.e(mTAG, "register content observer error: " + e.toString(), new Exception());
            }
        } else {
            unregisterBatteryChangeReceiver();
            unregisterTimeTickReceiver();
            unregisterSecondTicker();

            mContext.getContentResolver().unregisterContentObserver(mContentObserver);
        }
    }

    @Override
    protected void onAppearAnimFinished() {
        this.queryStepAndCalories();
        registerBatteryChangeReceiver();
        registerTimeTickReceiver();
        registerSecondTicker();
    }

    public void destroy() {
        mBitmapManager.clear();
    }

    @Override
    protected void onTimeTick() {
        this.queryStepAndCalories();
    }

    @Override
    public void updateTime() {
        Calendar calendar = Calendar.getInstance();
        mHour = DateUtil.getHour(mContext);
        mMinute = calendar.get(Calendar.MINUTE);
        mSecond = calendar.get(Calendar.SECOND);
    }

    @Override
    protected void onBatteryChange(int level) {
        mBatteryLevel = level;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);

        mPaint.setStyle(Paint.Style.STROKE);

        float radius = mRadiusInner - DimenUtil.dip2px(mContext, 8);
        mRectF.set(mPointScreenCenter.x - radius, mPointScreenCenter.y - radius
                , mPointScreenCenter.x + radius, mPointScreenCenter.y + radius);
        canvas.drawBitmap(mBitmapManager.get(R.drawable.my_health_stripe_bg, 0x33FFFFFF), null, mRectF, null);

        //-90：从矩形区域顶边中点开始(-110在它的左侧)；0：从矩形区域右边中点开始；90：从矩形区域底边中点开始；180：从矩形区域左边中点开始
        float startAngle = -112, sweepAngle = (Math.abs(startAngle) - Math.abs(-90)) * 2;
        mPaint.setStrokeWidth(2f);
        mPaint.setColor(ContextCompat.getColor(mContext, R.color.alphabet_uppercase));
        mRectF.set(mPointScreenCenter.x - mRadiusInner, mPointScreenCenter.y - mRadiusInner
                , mPointScreenCenter.x + mRadiusInner, mPointScreenCenter.y + mRadiusInner);
        for (int index = 0; index < SCALES; index++) {
            if (index == 5) {
                continue;
            }
            canvas.save();
            canvas.rotate(index * SCALE_DEGREES, mPointScreenCenter.x, mPointScreenCenter.y);
            canvas.drawArc(mRectF, startAngle, sweepAngle, false, mPaint);
            canvas.restore();
        }

        Bitmap bitmap = mBitmapManager.get(R.drawable.my_health_little_triangle
                , ContextCompat.getColor(mContext, R.color.my_health_little_triangle));
        mLittleTriangleHeight = bitmap.getHeight();
        float degrees;
        for (int index = 0; index < SCALE_DEGREES * 2; index++) {
            canvas.save();
            degrees = index * SCALE_DEGREES / 2;
            canvas.rotate(degrees, mPointScreenCenter.x, mPointScreenCenter.y);
            if (degrees % 60 != 0) {
                canvas.drawBitmap(bitmap, mPointScreenCenter.x - 1.0f * bitmap.getWidth() / 2
                        , mPointScreenCenter.y - mRadiusInner, null);
            }
            canvas.restore();
        }

        this.paintTime(canvas, mHour, mMinute, mSecond);
        this.paintIconInfo(canvas);
    }

    private void paintTime(Canvas canvas, int hour, int minute, int second) {
        Calendar instance = Calendar.getInstance();
        //分钟
        int color = ContextCompat.getColor(mContext, android.R.color.white);
        int units = minute % 10;//个位
        int tens = minute / 10;//十位
        Bitmap bitmap = mBitmapManager.getNumberBitmap(tens, color, mScale);
        float left = mPointScreenCenter.x - bitmap.getWidth();
        float hourBitmapHeight = 1.0f * bitmap.getHeight() / 2;
        float top = mPointScreenCenter.y - hourBitmapHeight;
        canvas.drawBitmap(bitmap, left, top, null);
        left += bitmap.getWidth();
        bitmap = mBitmapManager.getNumberBitmap(units, color, mScale);
        canvas.drawBitmap(bitmap, left, top, null);
        left += bitmap.getWidth();
        bitmap = mBitmapManager.get(R.drawable.my_health_minute_flag, color);
        canvas.drawBitmap(bitmap, left, top, null);

        if (!mIsDimMode) {
            //秒钟
            color = ContextCompat.getColor(mContext, R.color.second_number);
            units = second % 10;//个位
            tens = second / 10;//十位
            left = mPointScreenCenter.x + mRadiusInner / 2 + DimenUtil.dip2px(mContext, 4);
            top = mPointScreenCenter.y + hourBitmapHeight;
            bitmap = mBitmapManager.getNumberBitmap(tens, color, mScale / 3.3f);
            top -= (bitmap.getHeight() + 2);
            canvas.drawBitmap(bitmap, left, top, null);

            left += (bitmap.getWidth());
            bitmap = mBitmapManager.getNumberBitmap(units, color, mScale / 3.3f);
            canvas.drawBitmap(bitmap, left, top, null);

            left += (bitmap.getWidth() + DimenUtil.dip2px(mContext, 2));
            bitmap = mBitmapManager.get(R.drawable.my_health_second_flag, color);
            canvas.drawBitmap(bitmap, left, top, null);

            left += (bitmap.getWidth() + DimenUtil.dip2px(mContext, 2));
            bitmap = mBitmapManager.get(R.drawable.my_health_second_flag, color);
            canvas.drawBitmap(bitmap, left, top, null);
        }

        //时钟
        color = ContextCompat.getColor(mContext, R.color.my_health_little_triangle);
        units = hour % 10;//个位
        tens = hour / 10;//十位
        bitmap = mBitmapManager.getNumberBitmap(tens, color, mScale / 2);
        left = mPointScreenCenter.x - mRadiusInner - DimenUtil.dip2px(mContext, 12);
        top = mPointScreenCenter.y - bitmap.getHeight() - mLittleTriangleHeight - getResources().getDimension(R.dimen.hour_margin_top);
        canvas.drawBitmap(bitmap, left, top, null);
        left += bitmap.getWidth();
        bitmap = mBitmapManager.getNumberBitmap(units, color, mScale / 2);
        canvas.drawBitmap(bitmap, left, top, null);

        int marginBottom = DimenUtil.dip2px(mContext, 2);
        //float lineLeft = mPointScreenCenter.x - mRadiusInner - DimenUtil.dip2px(mContext, 2);
        float lineLeft = mPointScreenCenter.x - mRadiusInner + bitmap.getWidth() / 3f;
        int lineHeight = DimenUtil.dip2px(mContext, 4);
        float lineTop = top - (marginBottom + 1.8f * lineHeight);
        float lineLen = 1.0f * bitmap.getWidth() / 4;

        //黄线上方的日期
        float ratio = mScale / 5f;
        color = ContextCompat.getColor(mContext, android.R.color.white);
        int month = instance.get(Calendar.MONTH) + 1;
        units = month % 10;//个位
        tens = month / 10;//十位
        left = (lineLeft + DimenUtil.dip2px(mContext, 10));
        bitmap = mBitmapManager.getNumberBitmap(tens, color, ratio);
        top -= (bitmap.getHeight() + marginBottom + 3.3f * lineHeight);
        canvas.drawBitmap(bitmap, left, top, null);
        lineLen += bitmap.getWidth();
        left += bitmap.getWidth();
        bitmap = mBitmapManager.getNumberBitmap(units, color, ratio);
        canvas.drawBitmap(bitmap, left, top, null);
        lineLen += bitmap.getWidth();

        left += bitmap.getWidth();
        bitmap = mBitmapManager.get(R.drawable.my_health_diagonal, color, ratio);
        canvas.drawBitmap(bitmap, left, top, null);
        lineLen += bitmap.getWidth();

        int day = instance.get(Calendar.DAY_OF_MONTH);
        units = day % 10;//个位
        tens = day / 10;//十位
        left += bitmap.getWidth();
        bitmap = mBitmapManager.getNumberBitmap(tens, color, ratio);
        canvas.drawBitmap(bitmap, left, top, null);
        lineLen += bitmap.getWidth();
        left += bitmap.getWidth();
        bitmap = mBitmapManager.getNumberBitmap(units, color, ratio);
        canvas.drawBitmap(bitmap, left, top, null);
        lineLen += bitmap.getWidth();

        //时钟与时间之前的黄线
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(ContextCompat.getColor(mContext, R.color.my_health_little_triangle));
        mRectF.set(lineLeft, lineTop - 1.0f * lineHeight / 2
                , lineLeft + lineLen + DimenUtil.dip2px(mContext, 4), lineTop + 1.0f * lineHeight / 2);
        canvas.drawRoundRect(mRectF, 1.0f * lineHeight / 2, 1.0f * lineHeight / 2, mPaint);
    }

    private void paintIconInfo(Canvas canvas) {
        final float ratio = mScale / 6.9f;
        final float ratioIcon = mScale / 4.8f;

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.RED);
        float radius = mRadiusInner + (mRadiusOuter - mRadiusInner) / 2;

        final int scales = 100;
        final float scaleDegree = 360f / scales;
        float stopY = mPointScreenCenter.y - radius, rotateDegrees;
        Bitmap bitmap;
        int color = ContextCompat.getColor(mContext, R.color.alphabet_uppercase);
        for (int index = 0; index < scales; index++) {
            rotateDegrees = index * scaleDegree;
            bitmap = null;

            if (index > 73 && index < 94) {
                continue;
            }

            canvas.save();
            canvas.rotate(rotateDegrees, mPointScreenCenter.x, mPointScreenCenter.y);

            if (index == 1) {//卡路里后半部分
                bitmap = mBitmapManager.getNumberBitmap(mCalories[4], color, ratio);
            } else if (index == 3) {
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_k, color, ratio);
            } else if (index == 4) {
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_c, color, ratio);
            } else if (index == 5) {
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_a, color, ratio);
            } else if (index == 6) {
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_l, color, ratio);
            } else if (index == 13) {//心率
                bitmap = mBitmapManager.get(R.drawable.paint_heart_rate, color, ratioIcon);
            } else if (index == 15) {
                bitmap = mBitmapManager.getNumberBitmap(mHeartRate[0], color, ratio);
            } else if (index == 16) {
                bitmap = mBitmapManager.getNumberBitmap(mHeartRate[1], color, ratio);
            } else if (index == 17) {
                bitmap = mBitmapManager.getNumberBitmap(mHeartRate[2], color, ratio);
            } else if (index == 19) {
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_b, color, ratio);
            } else if (index == 20) {
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_p, color, ratio);
            } else if (index == 21) {
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_m, color, ratio);
            } else if (index == 27) {//运动
                bitmap = mBitmapManager.getReverseNumber(mSportTime[3], color, ratio);
            } else if (index == 28) {
                bitmap = mBitmapManager.getReverseNumber(mSportTime[2], color, ratio);
            } else if (index == 29) {
                bitmap = mBitmapManager.get(R.drawable.paint_sign_colon, color);
            } else if (index == 30) {
                bitmap = mBitmapManager.getReverseNumber(mSportTime[1], color, ratio);
            } else if (index == 31) {
                bitmap = mBitmapManager.getReverseNumber(mSportTime[0], color, ratio);
            } else if (index == 33) {
                bitmap = mBitmapManager.get(R.drawable.reverse_paint_workout, color, ratioIcon * 1.1f);
            } else if (index == 35) {
                bitmap = mBitmapManager.get(R.drawable.reverse_alphabet_uppercase_t, color, ratio);
            } else if (index == 36) {
                bitmap = mBitmapManager.get(R.drawable.reverse_alphabet_uppercase_r, color, ratio);
            } else if (index == 37) {
                bitmap = mBitmapManager.get(R.drawable.reverse_alphabet_uppercase_o, color, ratio);
            } else if (index == 38) {
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_p, color, ratio, 180);
            } else if (index == 39) {
                bitmap = mBitmapManager.get(R.drawable.reverse_alphabet_uppercase_s, color, ratio);
            } else if (index == 44) {//步数
                bitmap = mBitmapManager.getReverseNumber(mSteps[5], color, ratio);
            } else if (index == 45) {
                bitmap = mBitmapManager.getReverseNumber(mSteps[4], color, ratio);
            } else if (index == 46) {
                bitmap = mBitmapManager.getReverseNumber(mSteps[3], color, ratio);
            } else if (index == 47) {
                bitmap = mBitmapManager.getReverseNumber(mSteps[2], color, ratio);
            } else if (index == 48) {
                bitmap = mBitmapManager.getReverseNumber(mSteps[1], color, ratio);
            } else if (index == 49) {
                bitmap = mBitmapManager.getReverseNumber(mSteps[0], color, ratio);
            } else if (index == 51) {
                bitmap = mBitmapManager.get(R.drawable.reverse_paint_steps, color, ratioIcon * 1.1f);
            } else if (index == 53) {
                bitmap = mBitmapManager.get(R.drawable.reverse_alphabet_uppercase_s, color, ratio);
            } else if (index == 54) {
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_p, color, ratio, 180);
            } else if (index == 55) {
                bitmap = mBitmapManager.get(R.drawable.reverse_alphabet_uppercase_e, color, ratio);
            } else if (index == 56) {
                bitmap = mBitmapManager.get(R.drawable.reverse_alphabet_uppercase_t, color, ratio);
            } else if (index == 57) {
                bitmap = mBitmapManager.get(R.drawable.reverse_alphabet_uppercase_s, color, ratio);
            } else if (index >= 61 && index <= 73) {//电量
                if (mBatteryLevel < 10) {
                    if (index == 62) {
                        bitmap = mBitmapManager.get(R.drawable.reverse_paint_sign_percentage, color, ratio);
                    } else if (index == 63) {
                        bitmap = mBitmapManager.getReverseNumber(mBatteryLevel, color, ratio);
                    } else if (index == 65) {
                        bitmap = mBitmapManager.get(R.drawable.reverse_paint_battary, color, ratioIcon * 1.1f);
                    } else if (index == 67) {
                        bitmap = mBitmapManager.get(R.drawable.reverse_alphabet_uppercase_y, color, ratio);
                    } else if (index == 68) {
                        bitmap = mBitmapManager.get(R.drawable.reverse_alphabet_uppercase_g, color, ratio);
                    } else if (index == 69) {
                        bitmap = mBitmapManager.get(R.drawable.reverse_alphabet_uppercase_r, color, ratio);
                    } else if (index == 70) {
                        bitmap = mBitmapManager.get(R.drawable.reverse_alphabet_uppercase_e, color, ratio);
                    } else if (index == 71) {
                        bitmap = mBitmapManager.get(R.drawable.reverse_alphabet_uppercase_n, color, ratio);
                    } else if (index == 72) {
                        bitmap = mBitmapManager.get(R.drawable.reverse_alphabet_uppercase_e, color, ratio);
                    }
                } else if (mBatteryLevel == 100) {
                    if (index == 61) {
                        bitmap = mBitmapManager.get(R.drawable.reverse_paint_sign_percentage, color, ratio);
                    } else if (index == 62) {
                        bitmap = mBitmapManager.getNumberBitmap(0, color, ratio);
                    } else if (index == 63) {
                        bitmap = mBitmapManager.getNumberBitmap(0, color, ratio);
                    } else if (index == 64) {
                        bitmap = mBitmapManager.getReverseNumber(1, color, ratio);
                    } else if (index == 66) {
                        bitmap = mBitmapManager.get(R.drawable.reverse_paint_battary, color, ratioIcon * 1.1f);
                    } else if (index == 68) {
                        bitmap = mBitmapManager.get(R.drawable.reverse_alphabet_uppercase_y, color, ratio);
                    } else if (index == 69) {
                        bitmap = mBitmapManager.get(R.drawable.reverse_alphabet_uppercase_g, color, ratio);
                    } else if (index == 70) {
                        bitmap = mBitmapManager.get(R.drawable.reverse_alphabet_uppercase_r, color, ratio);
                    } else if (index == 71) {
                        bitmap = mBitmapManager.get(R.drawable.reverse_alphabet_uppercase_e, color, ratio);
                    } else if (index == 72) {
                        bitmap = mBitmapManager.get(R.drawable.reverse_alphabet_uppercase_n, color, ratio);
                    } else if (index == 73) {
                        bitmap = mBitmapManager.get(R.drawable.reverse_alphabet_uppercase_e, color, ratio);
                    }
                } else {
                    if (index == 61) {
                        bitmap = mBitmapManager.get(R.drawable.reverse_paint_sign_percentage, color, ratio);
                    } else if (index == 62) {
                        bitmap = mBitmapManager.getReverseNumber(mBatteryLevel % 10, color, ratio);
                    } else if (index == 63) {
                        bitmap = mBitmapManager.getReverseNumber(mBatteryLevel / 10, color, ratio);
                    } else if (index == 65) {
                        bitmap = mBitmapManager.get(R.drawable.reverse_paint_battary, color, ratioIcon * 1.1f);
                    } else if (index == 67) {
                        bitmap = mBitmapManager.get(R.drawable.reverse_alphabet_uppercase_y, color, ratio);
                    } else if (index == 68) {
                        bitmap = mBitmapManager.get(R.drawable.reverse_alphabet_uppercase_g, color, ratio);
                    } else if (index == 69) {
                        bitmap = mBitmapManager.get(R.drawable.reverse_alphabet_uppercase_r, color, ratio);
                    } else if (index == 70) {
                        bitmap = mBitmapManager.get(R.drawable.reverse_alphabet_uppercase_e, color, ratio);
                    } else if (index == 71) {
                        bitmap = mBitmapManager.get(R.drawable.reverse_alphabet_uppercase_n, color, ratio);
                    } else if (index == 72) {
                        bitmap = mBitmapManager.get(R.drawable.reverse_alphabet_uppercase_e, color, ratio);
                    }
                }
            } else if (index == 95) {//卡路里前半部分
                bitmap = mBitmapManager.get(R.drawable.paint_calorie, color, ratioIcon);
            } else if (index == 97) {
                bitmap = mBitmapManager.getNumberBitmap(mCalories[0], color, ratio);
            } else if (index == 98) {
                bitmap = mBitmapManager.getNumberBitmap(mCalories[1], color, ratio);
            } else if (index == 99) {
                bitmap = mBitmapManager.getNumberBitmap(mCalories[2], color, ratio);
            } else if (index == 0) {
                bitmap = mBitmapManager.getNumberBitmap(mCalories[3], color, ratio);
            }

            if (bitmap != null) {
                canvas.drawBitmap(bitmap, mPointScreenCenter.x - 1.0f * bitmap.getWidth() / 2
                        , stopY - 1.0f * bitmap.getHeight() / 2, null);
            }

            canvas.restore();
        }
    }

}
