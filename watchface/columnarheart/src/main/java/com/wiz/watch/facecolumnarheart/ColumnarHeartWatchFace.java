package com.wiz.watch.facecolumnarheart;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;

import com.structure.wallpaper.basic.WatchFaceView;
import com.structure.wallpaper.basic.utils.DimenUtil;
import com.structure.wallpaper.basic.utils.IntentHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class ColumnarHeartWatchFace extends WatchFaceView {

    private ColumnarBitmapManager mBitmapManager;

    private int mWeatherIconRes = R.drawable.paint_weather_no_data;
    private int mTemperature = Integer.MAX_VALUE, mTemperatureUnitRes = R.string.tpe_unit_c;
    private Paint mPaintTpe;

    private static final String COLON = ":";
    private Paint mPaintTime = new Paint();
    private Rect mRectTime = new Rect();
    private Rect mRectColon = new Rect();
    private int mTimeVerticalOffset;

    private static final int HEART_TIME_SCALES = 5;
    private Paint mPaintHeartTime = new Paint();
    private Rect mRectHeartTime = new Rect();
    private float mOffsetHeartTime, mMarginHeartTime/*最左侧的时间文案距离屏幕左侧的间隙*/;

    public static final int LINE_SCALES = 25;
    private Paint mPaintLine = new Paint();
    private float mOffsetLine, mMarginLine/*最左侧的网络线条距离屏幕左侧的间隙*/, mTopLine;

    private static final int LINE_START_COLOR = 0xFFF8652E;
    private static final int LINE_END_COLOR = 0xFFF0F644;
    private Paint mPaintColumnar = new Paint();
    private float mWidthColumnar;/*柱状图宽度*/

    private RectF mRectFHeartRate = new RectF();
    private RectF mRectFCamera = new RectF();
    private RectF mRectFContacts = new RectF();
    private boolean mPassActionMove;

    private Paint mPaintHeartRate;
    private Rect mRectHeartRate = new Rect();

    public ColumnarHeartWatchFace(Context context) {
        super(context);
        this.init();
    }

    public ColumnarHeartWatchFace(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    private void init() {
        mBitmapManager = new ColumnarBitmapManager(mContext);

        mPaintTime = new Paint();
        mPaintTime.setAntiAlias(true);
        mPaintTime.setStyle(Paint.Style.FILL);
        mPaintTime.setTypeface(Typeface.DEFAULT_BOLD);
        mPaintTime.setColor(Color.WHITE);
        mPaintTime.setTextSize(DimenUtil.dip2px(mContext, 78));
        String text = "00";
        mPaintTime.getTextBounds(text, 0, text.length(), mRectTime);
        mPaintTime.getTextBounds(COLON, 0, COLON.length(), mRectColon);
        mTimeVerticalOffset = DimenUtil.dip2px(mContext, 54);

        mPaintTpe = new Paint(mPaintTime);
        mPaintTpe.setTextSize(DimenUtil.dip2px(mContext, 30));

        mMarginHeartTime = DimenUtil.dip2px(mContext, 8);
        mPaintHeartTime.setAntiAlias(true);
        mPaintHeartTime.setStyle(Paint.Style.FILL);
        mPaintHeartTime.setColor(Color.WHITE);
        mPaintHeartTime.setTextSize(DimenUtil.dip2px(mContext, 18));
        text = "00AM";
        mPaintHeartTime.getTextBounds(text, 0, text.length(), mRectHeartTime);

        mMarginLine = mMarginHeartTime * 2f;
        mPaintLine.setAntiAlias(true);
        mPaintHeartTime.setStyle(Paint.Style.FILL);
        //mPaintHistogram.setColor(Color.parseColor("#2B2B2B"));
        mPaintLine.setColor(Color.parseColor("#FFFFFF"));
        mPaintLine.setStrokeWidth(0.2f);

        mPaintColumnar.setAntiAlias(true);
        mPaintColumnar.setStyle(Paint.Style.FILL);

        mPaintHeartRate = new Paint(mPaintTpe);
        mPaintHeartRate.setColor(Color.parseColor("#F8AE1F"));

        setUpdateTimePerSecondImmediately(true);
        notifyMsgUpdateTimePerSecond();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float paintWidth = w - 2 * mMarginHeartTime;
        mOffsetHeartTime = (paintWidth - HEART_TIME_SCALES * mRectHeartTime.width()) / (HEART_TIME_SCALES - 1);

        paintWidth = w - 2 * mMarginLine;
        mOffsetLine = paintWidth / (LINE_SCALES - 1);
        mTopLine = mPointScreenCenter.y - DimenUtil.dip2px(mContext, 50);

        mWidthColumnar = mOffsetLine - 2 * DimenUtil.dip2px(mContext, 2.8f);

        float marginBottomIcon = (w - 3 * mContext.getResources().getDimension(R.dimen.bottom_icon)) / 4;
        if (marginBottomIcon <= 0) {
            marginBottomIcon = DimenUtil.dip2px(mContext, 20);
        }

        Bitmap bitmap = mBitmapManager.get(R.drawable.icon_heart_rate);
        int dy = h - bitmap.getHeight() - DimenUtil.dip2px(mContext, 6);
        mRectFHeartRate.top = dy;
        mRectFHeartRate.bottom = mRectFHeartRate.top + bitmap.getHeight();
        mRectFCamera.top = dy;
        mRectFCamera.bottom = mRectFCamera.top + bitmap.getHeight();
        mRectFContacts.top = dy;
        mRectFContacts.bottom = mRectFContacts.top + bitmap.getHeight();

        float left = 1f * marginBottomIcon;
        mRectFHeartRate.left = left;
        left += bitmap.getWidth();
        mRectFHeartRate.right = left;

        left += 1f * marginBottomIcon;
        mRectFCamera.left = left;
        left += bitmap.getWidth();
        mRectFCamera.right = left;

        bitmap = mBitmapManager.get(R.drawable.icon_phone);
        left += 1f * marginBottomIcon;
        mRectFContacts.left = left;
        left += bitmap.getWidth();
        mRectFContacts.right = left;
        Log.d(mTAG, "mRectFHeartRate: " + mRectFHeartRate + ", mRectFTakePhoto: "
                + mRectFCamera + ", mRectFPhone: " + mRectFContacts);
    }

    @Override
    public void onVisibilityChanged(boolean visible) {
        super.onVisibilityChanged(visible);
        if (visible) {
            try {
                this.queryCurrentHeartRate();

                this.queryWeatherInfo();
                mContext.getContentResolver().registerContentObserver(WEATHER_URI, true, mContentObserver);

                this.queryCurrentHeartRate();
                mContext.getContentResolver().registerContentObserver(URI_HEART_RATE, true, mObserverHeart);
            } catch (Exception e) {
                Log.e(mTAG, "register content observer error: " + e.toString(), new Exception());
            }
        } else {
            mContext.getContentResolver().unregisterContentObserver(mContentObserver);

            mContext.getContentResolver().unregisterContentObserver(mObserverHeart);

            this.destroy();
        }

    }

    @Override
    public void destroy() {
        super.destroy();
        mBitmapManager.clear();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        Log.d(mTAG, "x: " + x + ", y: " + y + ", rawX: " + event.getRawX() + ", rawY: " + event.getRawY());
        if (!(mRectFHeartRate.contains(x, y) || mRectFCamera.contains(x, y) || mRectFContacts.contains(x, y))) {
            return false;//如果落点不在点击的区域，那么不拦截touch事件
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Log.d(mTAG, "down, action: " + action);
                mPassActionMove = false;
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(mTAG, "move, action: " + action);
                mPassActionMove = true;
                break;
            case MotionEvent.ACTION_UP:
                Log.d(mTAG, "up, action: " + action);
                if (mPassActionMove) {
                    return true;
                }

                if (mRectFHeartRate.contains(x, y)) {
                    IntentHelper.openHeartRateApp(mContext);
                } else if (mRectFCamera.contains(x, y)) {
                    IntentHelper.openCameraApp(mContext);
                } else if (mRectFContacts.contains(x, y)) {
                    IntentHelper.openContactsApp(mContext);
                }
                break;
            default:
                Log.d(mTAG, "cancel, action: " + action);
                break;
        }

        return true;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(ContextCompat.getColor(mContext, android.R.color.transparent), PorterDuff.Mode.CLEAR);

        this.paintWeather(canvas);
        this.paintTime(canvas);
        this.paintColumnar(canvas);
        this.paintBottomIcons(canvas);
    }

    private void paintColumnar(Canvas canvas) {
        canvas.save();
        canvas.translate(mMarginHeartTime, mPointScreenCenter.y * 1.46f);
        //canvas.drawLine(0, 0, canvas.getWidth() - 2 * mMarginHeartTime, 0, mPaintHeartTime);

        float x = 0;
        String text = "00AM";
        for (int index = 0; index < HEART_TIME_SCALES; index++) {
            if (index == 1) {
                text = "06AM";
            }
            if (index == 2) {
                text = "12AM";
            }
            if (index == 3) {
                text = "06PM";
            }
            if (index == 4) {
                text = "11PM";
            }
            canvas.drawText(text, x, mRectHeartTime.height() / 2f, mPaintHeartTime);
            x += (mRectHeartTime.width() + mOffsetHeartTime);
        }

        canvas.translate(mMarginLine - mMarginHeartTime, -mRectHeartTime.height() / 1.5f);
        x = 0;
        float stopY, startX;
        float heartRate, ratio;
        for (int index = 0; index < LINE_SCALES; index++) {
            canvas.drawLine(x, 0, x, -mTopLine, mPaintLine);

            if (index != LINE_SCALES - 1) {
                startX = x + mOffsetLine / 2;

                    /*if (mMapHourRate.containsKey(index)) {
                        rateTmp = mMapHourRate.get(index);
                        heartRate = (rateTmp == null ? 0 : rateTmp);
                    } else {
                        heartRate = 0;
                    }*/

                float maxHeartRate = 100;
                heartRate = new Random().nextInt((int) (maxHeartRate - 5)) + 5;
                if (heartRate != 0) {
                    ratio = heartRate / maxHeartRate;
                    stopY = -mTopLine * ratio;
                    mPaintColumnar.setShader(new LinearGradient(startX, -mPaintColumnar.getStrokeWidth() / 2
                            , startX, stopY, LINE_START_COLOR
                            , ColorUtils.blendARGB(LINE_START_COLOR, LINE_END_COLOR, ratio)/*获取两点之前的颜色值*/
                            , Shader.TileMode.CLAMP));

                    canvas.drawRoundRect(startX - mWidthColumnar / 2, stopY, startX + mWidthColumnar / 2, 0, mWidthColumnar, mWidthColumnar, mPaintColumnar);
                }
            }

            x += mOffsetLine;
        }

        canvas.restore();
    }

    private void paintWeather(Canvas canvas) {
        canvas.save();
        canvas.translate(0, mTimeVerticalOffset);
        //canvas.drawLine(0, 0, canvas.getWidth(), 0, mPaintTime);

        int color = android.R.color.white;
        Bitmap bitmap = mBitmapManager.get(mWeatherIconRes, ContextCompat.getColor(mContext, color));
        canvas.drawBitmap(bitmap, DimenUtil.dip2px(mContext, 10), -bitmap.getHeight() / 2f, null);

        String text;
        //mTemperature = Integer.MAX_VALUE;
        if (mTemperature == Integer.MAX_VALUE) {
            text = String.format(mContext.getString(mTemperatureUnitRes), "NA");
        } else {
            text = mContext.getString(mTemperatureUnitRes, String.valueOf(mTemperature));
        }
        canvas.drawText(text, DimenUtil.dip2px(mContext, 16)
                , bitmap.getHeight() - DimenUtil.dip2px(mContext, 6), mPaintTpe);

        canvas.restore();
    }

    private void paintTime(Canvas canvas) {
        canvas.save();
        canvas.translate(0, mTimeVerticalOffset);

        float tmpX = 2 * mPointScreenCenter.x - DimenUtil.dip2px(mContext, 24);
        float x = tmpX;
        float margin = mRectColon.width() / 2f;

        int minute = Calendar.getInstance().get(Calendar.MINUTE);
        String textMinute = minute / 10 + "" + minute % 10;
        tmpX -= mRectTime.width();
        canvas.drawText(textMinute, tmpX, -mRectTime.centerY(), mPaintTime);

        tmpX -= (mRectColon.width() + margin);
        canvas.drawText(COLON, tmpX, -mRectTime.centerY(), mPaintTime);

        int hour = getHour();
        tmpX -= (mRectTime.width() + margin);
        canvas.drawText(hour / 10 + "" + hour % 10, tmpX, -mRectTime.centerY(), mPaintTime);

        String text;
        if (mHeartRate == Short.MAX_VALUE) {
            text = "NA bpm";
        } else {
            text = mHeartRate + " bpm";
        }
        mPaintHeartRate.getTextBounds(text, 0, text.length(), mRectHeartRate);
        canvas.drawText(text, x - mRectHeartRate.width()
                , mRectTime.height() + DimenUtil.dip2px(mContext, 4), mPaintHeartRate);

        canvas.restore();
    }

    private void paintBottomIcons(Canvas canvas) {
        canvas.save();
        canvas.translate(0, mRectFHeartRate.top);

        //canvas.drawLine(0, 0, canvas.getWidth(), 0, mPaintHeartTime);
        //canvas.drawLine(0, bitmap.getHeight(), canvas.getWidth(), bitmap.getHeight(), mPaintHeartTime);

        canvas.drawBitmap(mBitmapManager.get(R.drawable.icon_heart_rate), mRectFHeartRate.left, 0, null);
        //canvas.drawLine(mRectFHeartRate.left, 0, mRectFHeartRate.left, bitmap.getHeight(), mPaintHeartTime);
        //canvas.drawLine(mRectFHeartRate.right, 0, mRectFHeartRate.right, bitmap.getHeight(), mPaintHeartTime);

        canvas.drawBitmap(mBitmapManager.get(R.drawable.icon_take_photo), mRectFCamera.left, 0, null);
        //canvas.drawLine(mRectFTakePhoto.left, 0, mRectFTakePhoto.left, bitmap.getHeight(), mPaintHeartTime);
        //canvas.drawLine(mRectFTakePhoto.right, 0, mRectFTakePhoto.right, bitmap.getHeight(), mPaintHeartTime);

        canvas.drawBitmap(mBitmapManager.get(R.drawable.icon_phone), mRectFContacts.left, 0, null);
        //canvas.drawLine(mRectFPhone.left, 0, mRectFPhone.left, bitmap.getHeight(), mPaintHeartTime);
        //canvas.drawLine(mRectFPhone.right, 0, mRectFPhone.right, bitmap.getHeight(), mPaintHeartTime);

        canvas.restore();
    }

    public static final String AUTHORITY = "com.wiz.watch.weather";
    public final Uri WEATHER_URI = Uri.parse("content://" + AUTHORITY + "/weathers");
    public static final String QUERY_WEATHER_INFO = "query_weather_info";

    private ContentObserver mContentObserver = new ContentObserver(null) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            Log.d(mTAG, "onChange, selfChange: " + selfChange + ", uri: " + uri);
            queryWeatherInfo();
            invalidate();
        }
    };

    private void queryWeatherInfo() {
        try {
            Bundle extras = new Bundle();
            extras.putLong("current_time_millis", System.currentTimeMillis());
            Bundle bundle = mContext.getContentResolver().call(WEATHER_URI, QUERY_WEATHER_INFO, null, extras);
            if (bundle == null) {
                Log.e(mTAG, "query weather info error: bundle is null");
                return;
            }
            ArrayList<String> data = bundle.getStringArrayList(QUERY_WEATHER_INFO);
            if (data == null || data.size() == 0) {
                Log.e(mTAG, "query weather info error, data is null or empty");
                return;
            }

            String country = data.get(0);
            String weather = data.get(1);
            String temperatureUnit = data.get(2);
            String temperature = data.get(3);
            Log.d(mTAG, "query weather info result, country: " + country + ", weather: " + weather
                    + ", temperature: " + temperatureUnit + ", temperature: " + temperature);

            if ("CN".equals(country)) {
                mWeatherIconRes = mBitmapManager.getWeatherResId(Integer.parseInt(weather));
            } else {
                mWeatherIconRes = mBitmapManager.getOverseasWeatherResId(Integer.parseInt(weather));
            }
            if ("C".equals(temperatureUnit)) {
                mTemperatureUnitRes = R.string.tpe_unit_c;
            } else {
                mTemperatureUnitRes = R.string.tpe_unit_f;
            }

            mTemperature = Integer.parseInt(temperature);
        } catch (Exception e) {
            Log.e(mTAG, "query weather info error: " + e.toString(), new Exception());
            mTemperature = Integer.MAX_VALUE;
        }
    }

    public static final String AUTHORITY_HEALTH = "com.wiz.watch.health.provider";
    public static final String CALL_HEART_RATE_DATA = "heart_rate/heart_rate_data";//"com.wiz.watch.health.provider"
    public final Uri URI_HEART_RATE = Uri.parse("content://" + AUTHORITY_HEALTH + "/heart_rate");
    private int mHeartRate = Short.MAX_VALUE;

    private ContentObserver mObserverHeart = new ContentObserver(null) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            Log.d(mTAG, "onChange, uri: " + uri);
            if (uri.toString().startsWith(URI_HEART_RATE.toString())) {
                queryCurrentHeartRate();

                invalidate();
            }
        }
    };

    private void queryCurrentHeartRate() {
        try {
            Bundle bundle = mContext.getContentResolver().call(Uri.parse("content://" + AUTHORITY_HEALTH)
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
            mHeartRate = (int) data[0];
            Log.d(mTAG, "heart rate: " + mHeartRate);
            //heartRate = 104;
        } catch (Exception e) {
            mHeartRate = Short.MAX_VALUE;
            Log.e(mTAG, "query heart rate error: " + e.toString(), new Exception());
        }
    }
}
