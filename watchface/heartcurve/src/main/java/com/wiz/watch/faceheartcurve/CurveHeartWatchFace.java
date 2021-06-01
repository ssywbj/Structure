package com.wiz.watch.faceheartcurve;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
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

import com.structure.wallpaper.basic.WatchFaceView;
import com.structure.wallpaper.basic.utils.DimenUtil;
import com.structure.wallpaper.basic.utils.IntentHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class CurveHeartWatchFace extends WatchFaceView {
    private CurveBitmapManager mBitmapManager;

    private int mWeatherIconRes = R.drawable.paint_weather_no_data;
    private int mTemperature = Integer.MAX_VALUE, mTemperatureUnitRes = R.string.tpe_unit_c;
    private Rect mRectTpt = new Rect();
    private Paint mPaintTpe;

    private static final String COLON = ":";
    private Paint mPaintTime = new Paint();
    private Rect mRectTime = new Rect();
    private Rect mRectColon = new Rect();
    private int mTimeVerticalOffset;

    public static final int HEART_TIME_SCALES = 3;
    private Paint mPaintHeartTime = new Paint();
    private Rect mRectHeartTime = new Rect();
    private float mOffsetHeartTime, mMarginHeartTime;
    private static final int LINE_SCALES = 24;
    private static final int TIME_SCALES = 12;
    private float mTimeScaleHeight, mTimeScaleOffset;

    private float mOffsetLine, mTopLine;

    private Paint mPaintCurve = new Paint();
    private Path mPathCurve = new Path();
    private Path mPathCurveShader = new Path();
    private Paint mPaintCurveShader = new Paint();
    private int mCurveShaderColor = 0xE6FF0000;

    private Paint mPaintDash = new Paint();
    private Rect mRectDashText = new Rect();
    private float mDashTextMargin;

    private Paint mPaintRectF = new Paint();
    private RectF mRectFContacts = new RectF();
    private RectF mRectFMusic = new RectF();
    private RectF mRectFCamera = new RectF();
    private boolean mPassActionMove;

    private Paint mPaintHeartRate;

    public CurveHeartWatchFace(Context context) {
        super(context);
        this.init();

    }

    public CurveHeartWatchFace(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    private void init() {
        mBitmapManager = new CurveBitmapManager(mContext);

        mPaintTime = new Paint();
        mPaintTime.setAntiAlias(true);
        mPaintTime.setStyle(Paint.Style.FILL);
        mPaintTime.setTypeface(Typeface.DEFAULT_BOLD);
        mPaintTime.setColor(Color.WHITE);
        mPaintTime.setTextSize(DimenUtil.dip2px(mContext, 58));
        String text = "00";
        mPaintTime.getTextBounds(text, 0, text.length(), mRectTime);
        mPaintTime.getTextBounds(COLON, 0, COLON.length(), mRectColon);
        mTimeVerticalOffset = DimenUtil.dip2px(mContext, 54);

        mPaintTpe = new Paint(mPaintTime);
        mPaintTpe.setTextSize(DimenUtil.dip2px(mContext, 27));

        mTimeScaleHeight = DimenUtil.dip2px(mContext, 8);
        mMarginHeartTime = DimenUtil.dip2px(mContext, 4);
        mPaintHeartTime.setAntiAlias(true);
        mPaintHeartTime.setStyle(Paint.Style.FILL);
        mPaintHeartTime.setColor(Color.WHITE);
        mPaintHeartTime.setTextSize(DimenUtil.dip2px(mContext, 18));
        mPaintHeartTime.setStrokeWidth(1f);
        text = "00AM";
        mPaintHeartTime.getTextBounds(text, 0, text.length(), mRectHeartTime);

        int curveColor = 0xFFFF0000;
        mPaintCurve.setAntiAlias(true);
        mPaintCurve.setStyle(Paint.Style.STROKE);
        mPaintCurve.setColor(curveColor);
        mPaintCurve.setStrokeWidth(2f);
        CornerPathEffect pathEffect = new CornerPathEffect(8);
        mPaintCurve.setPathEffect(pathEffect);
        mPaintCurve.setStrokeJoin(Paint.Join.ROUND);

        mPaintCurveShader = new Paint(mPaintCurve);
        mPaintCurveShader.setStyle(Paint.Style.FILL);
        mPaintCurveShader.setColor(mCurveShaderColor);

        mDashTextMargin = DimenUtil.dip2px(mContext, 4);
        mPaintDash.setAntiAlias(true);
        mPaintDash.setStyle(Paint.Style.FILL);
        mPaintDash.setColor(0x99FFFFFF);
        mPaintDash.setStrokeWidth(1.4f);
        mPaintDash.setTextSize(DimenUtil.dip2px(mContext, 18));
        mPaintDash.setPathEffect(new DashPathEffect(new float[]{8, 8}, 0));

        mPaintHeartRate = new Paint(mPaintTpe);
        mPaintHeartRate.setColor(curveColor);

        mPaintRectF = new Paint();
        mPaintRectF.setAntiAlias(true);
        mPaintRectF.setStyle(Paint.Style.FILL);

        setUpdateTimePerSecondImmediately(true);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        notifyMsgUpdateTimePerSecond();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float paintWidth = w - 2 * mMarginHeartTime;
        mOffsetHeartTime = (paintWidth - HEART_TIME_SCALES * mRectHeartTime.width()) / (HEART_TIME_SCALES - 1);

        paintWidth = w;
        mOffsetLine = paintWidth / (LINE_SCALES - 1);
        mTopLine = mPointScreenCenter.y - DimenUtil.dip2px(mContext, 55);
        mPaintCurveShader.setShader(new LinearGradient(0, 0, 0, -mTopLine, 0xFF000000,
                mCurveShaderColor, Shader.TileMode.CLAMP));

        mTimeScaleOffset = 1.0f * w / TIME_SCALES;

        float rectWidth = w / 3f, rectHeight = DimenUtil.dip2px(mContext, 94);
        float dy = h - rectHeight;
        mRectFMusic.top = dy;
        mRectFMusic.bottom = mRectFMusic.top + rectHeight;
        mRectFCamera.top = dy;
        mRectFCamera.bottom = mRectFCamera.top + rectHeight;
        mRectFContacts.top = dy;
        mRectFContacts.bottom = mRectFContacts.top + rectHeight;

        if (rectWidth == 0) {
            rectWidth = mContext.getResources().getDisplayMetrics().widthPixels / 3f;
        }
        float left = 0;
        mRectFContacts.left = left;
        left += rectWidth;
        mRectFContacts.right = left;

        mRectFMusic.left = left;
        left += rectWidth;
        mRectFMusic.right = left;

        mRectFCamera.left = left;
        left += rectWidth;
        mRectFCamera.right = left;
    }

    @Override
    public void onVisibilityChanged(boolean visible) {
        super.onVisibilityChanged(visible);
        if (visible) {
            try {
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
        if (!(mRectFMusic.contains(x, y) || mRectFCamera.contains(x, y) || mRectFContacts.contains(x, y))) {
            return false;
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

                if (mRectFMusic.contains(x, y)) {
                    IntentHelper.openMusicApp(mContext);
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
        this.paintCurve(canvas);
        this.paintBottomIcons(canvas);
    }

    private void paintCurve(Canvas canvas) {
        canvas.save();
        canvas.translate(mMarginHeartTime, mPointScreenCenter.y * 1.40f);

        float x = 0;
        String text = "09:00";
        for (int index = 0; index < HEART_TIME_SCALES; index++) {
            if (index == 1) {
                text = "16:00";
            }
            if (index == 2) {
                text = "21:00";
            }
            canvas.drawText(text, x, mRectHeartTime.height() / 2f, mPaintHeartTime);
            x += (mRectHeartTime.width() + mOffsetHeartTime);
        }

        canvas.translate(-mMarginHeartTime, -mRectHeartTime.height() / 1.5f - mTimeScaleHeight);

        x = 0;
        canvas.drawLine(0, 0, canvas.getWidth(), 0, mPaintHeartTime);
        for (int index = 0; index < TIME_SCALES; index++) {
            if (index == 0) {
                x += mTimeScaleOffset;
                continue;
            }
            canvas.drawLine(x, 0, x, mTimeScaleHeight, mPaintHeartTime);
            x += mTimeScaleOffset;
        }

        x = 0;
        float stopY;
        float heartRate, ratio;
        mPathCurve.reset();
        mPathCurveShader.reset();
        float maxHeartRate = 140;
        for (int index = 0; index < LINE_SCALES; index++) {

            heartRate = new Random().nextInt((int) (maxHeartRate - 70)) + 70;
            ratio = heartRate / maxHeartRate;
            stopY = -mTopLine * ratio;
            if (index == 0) {
                mPathCurve.moveTo(x, stopY);
                mPathCurveShader.moveTo(x, stopY);
            } else {
                mPathCurve.lineTo(x, stopY);
                mPathCurveShader.lineTo(x, stopY);

                if (index == (LINE_SCALES - 1)) {
                    mPathCurveShader.lineTo(x, -mPaintHeartTime.getStrokeWidth() * 1.3f);
                }
            }
            x += mOffsetLine;
        }
        canvas.drawPath(mPathCurve, mPaintCurve);
        mPathCurveShader.lineTo(0, -mPaintHeartTime.getStrokeWidth() * 1.3f);
        mPathCurveShader.close();
        canvas.drawPath(mPathCurveShader, mPaintCurveShader);

        ratio = 1.3f / 3;
        mRectDashText.setEmpty();
        String number = String.valueOf((int) (maxHeartRate * ratio));
        mPaintDash.getTextBounds(number, 0, number.length(), mRectDashText);
        float offset = mRectDashText.width() + mDashTextMargin;
        canvas.drawText(number, canvas.getWidth() - offset, -mTopLine * ratio + mRectDashText.height() / 2f, mPaintDash);
        canvas.drawLine(0, -mTopLine * ratio, canvas.getWidth() - offset - mDashTextMargin
                , -mTopLine * ratio, mPaintDash);

        ratio = 2.3f / 3;
        mRectDashText.setEmpty();
        number = String.valueOf((int) (maxHeartRate * ratio));
        mPaintDash.getTextBounds(number, 0, number.length(), mRectDashText);
        offset = mRectDashText.width() + mDashTextMargin;
        canvas.drawText(number, canvas.getWidth() - offset, -mTopLine * ratio + mRectDashText.height() / 2f, mPaintDash);
        canvas.drawLine(0, -mTopLine * ratio, canvas.getWidth() - offset - mDashTextMargin
                , -mTopLine * ratio, mPaintDash);

        canvas.translate(DimenUtil.dip2px(mContext, 12), -mTopLine - DimenUtil.dip2px(mContext, 11));
        if (mHeartRate == Short.MAX_VALUE) {
            canvas.drawText("NA bpm", 0, 0, mPaintHeartRate);
        } else {
            canvas.drawText(mHeartRate + " bpm", 0, 0, mPaintHeartRate);
        }

        canvas.restore();
    }

    private void paintWeather(Canvas canvas) {
        canvas.save();
        canvas.translate(canvas.getWidth() - DimenUtil.dip2px(mContext, 25)
                , mTimeVerticalOffset - DimenUtil.dip2px(mContext, 8));

        int color = android.R.color.white;
        Bitmap bitmap = mBitmapManager.get(mWeatherIconRes, ContextCompat.getColor(mContext, color));
        float x = -bitmap.getWidth();
        canvas.drawBitmap(bitmap, x, -bitmap.getHeight() / 2f, null);

        mRectTpt.setEmpty();
        String text;
        //mTemperature = Integer.MAX_VALUE;
        if (mTemperature == Integer.MAX_VALUE) {
            text = String.format(mContext.getString(mTemperatureUnitRes), "NA");
        } else {
            text = mContext.getString(mTemperatureUnitRes, String.valueOf(mTemperature));
        }
        mPaintTpe.getTextBounds(text, 0, text.length(), mRectTpt);
        x = -mRectTpt.width();
        canvas.drawText(text, x, bitmap.getHeight() - DimenUtil.dip2px(mContext, 6), mPaintTpe);

        canvas.restore();
    }

    private void paintTime(Canvas canvas) {
        canvas.save();
        canvas.translate(0, mTimeVerticalOffset);

        float x = DimenUtil.dip2px(mContext, 12);
        float margin = mRectColon.width() / 2f;

        int hour = getHour();
        canvas.drawText(hour / 10 + "" + hour % 10, x, -mRectTime.centerY(), mPaintTime);
        x += (mRectTime.width() + margin);

        canvas.drawText(COLON, x, -mRectTime.centerY(), mPaintTime);
        x += (mRectColon.width() + margin);

        int minute = Calendar.getInstance().get(Calendar.MINUTE);
        String textMinute = minute / 10 + "" + minute % 10;
        canvas.drawText(textMinute, x, -mRectTime.centerY(), mPaintTime);

        canvas.restore();
    }

    private void paintBottomIcons(Canvas canvas) {
        mPaintRectF.setColor(0xFF332E00);
        canvas.drawRect(mRectFContacts, mPaintRectF);
        mPaintRectF.setColor(0xFF35004C);
        canvas.drawRect(mRectFMusic, mPaintRectF);
        mPaintRectF.setColor(0xFF004C42);
        canvas.drawRect(mRectFCamera, mPaintRectF);

        canvas.save();
        canvas.translate(0, mRectFContacts.top);
        Bitmap bitmap = mBitmapManager.get(R.drawable.icon_phone);
        float left = mRectFContacts.left + (mRectFContacts.width() - bitmap.getWidth()) / 2;
        canvas.drawBitmap(bitmap, left, (mRectFContacts.height() - bitmap.getHeight()) / 2, null);

        bitmap = mBitmapManager.get(R.drawable.icon_music);
        left = mRectFMusic.left + (mRectFMusic.width() - bitmap.getWidth()) / 2f;
        canvas.drawBitmap(bitmap, left, (mRectFMusic.height() - bitmap.getHeight()) / 2, null);

        bitmap = mBitmapManager.get(R.drawable.icon_camrea);
        left = mRectFCamera.left + (mRectFCamera.width() - bitmap.getWidth()) / 2;
        canvas.drawBitmap(bitmap, left, (mRectFCamera.height() - bitmap.getHeight()) / 2, null);

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
