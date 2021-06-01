package com.wiz.watch.facemultifunction3;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.content.Context;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
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
import java.util.Locale;

public class MultiFunction3WatchFace extends WatchFaceView {

    private MultiFunction3BitmapManager mBitmapManager;

    private Rect mRectTpe = new Rect();
    private Paint mPaintTpe;

    private RectF mRectFContacts = new RectF();
    private RectF mRectFMusic = new RectF();
    private RectF mRectFHeartRate = new RectF();
    private boolean mPassActionMove;

    public static final int SCALES = 120;
    public static final float SCALE_DEGREE = 360f / SCALES;
    private float mScaleRadius;
    private int mScaleLength;
    private Paint mPaintScale;
    private Paint mPaintPointer;
    private RectF mRectFSecond = new RectF();
    private RectF mRectFMinute = new RectF();
    private RectF mRectFHour = new RectF();
    private Path mPathTextLogo = new Path();

    private static final int arc_distance_degree_start = -72;
    private static final int ARC_STEPS_DEGREE_OFFSET = 100;
    private static final int arc_alarm_degree_offset = 80;
    private static final int ARC_WEATHER_DEGREE_OFFSET = 100;
    private Paint mPaintCornerArc;
    private Path mPathCornerArc = new Path();
    private Paint mPaintArcCircle;

    private float mArcCircleRadius;
    private PointF mRectFArcCircleCentre = new PointF();

    private RectF mRectFDistance = new RectF(), mRectFSteps = new RectF();
    private RectF mRectFAlarm = new RectF(), mRectFWeather = new RectF();

    private Paint mPaintDate;
    private Rect mRectDate = new Rect();

    public MultiFunction3WatchFace(Context context) {
        super(context);
        this.init();
    }

    public MultiFunction3WatchFace(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    private void init() {
        mBitmapManager = new MultiFunction3BitmapManager(mContext);

        mPaintTpe = new Paint();
        mPaintTpe.setAntiAlias(true);
        mPaintTpe.setStyle(Paint.Style.FILL);
        mPaintTpe.setTypeface(Typeface.DEFAULT_BOLD);
        mPaintTpe.setColor(Color.WHITE);

        mScaleLength = DimenUtil.dip2px(mContext, 10);
        mPaintScale = new Paint();
        mPaintScale.setAntiAlias(true);
        mPaintScale.setStyle(Paint.Style.FILL);
        mPaintScale.setTypeface(Typeface.DEFAULT_BOLD);
        mPaintScale.setTextSize(DimenUtil.dip2px(mContext, 18));

        mPaintPointer = new Paint();
        mPaintPointer.setAntiAlias(true);
        mPaintPointer.setFilterBitmap(true);//Bitmap防锯齿

        mPaintCornerArc = new Paint();
        mPaintCornerArc.setAntiAlias(true);
        mPaintCornerArc.setStyle(Paint.Style.STROKE);
        mPaintCornerArc.setColor(0xFFFF0000);
        mPaintCornerArc.setStrokeCap(Paint.Cap.ROUND);
        int strokeWidth = DimenUtil.dip2px(mContext, 11);
        mPaintCornerArc.setStrokeWidth(strokeWidth);
        mArcCircleRadius = strokeWidth / 2.2f;
        mPaintArcCircle = new Paint();
        mPaintArcCircle.setAntiAlias(true);
        mPaintArcCircle.setStyle(Paint.Style.STROKE);
        mPaintArcCircle.setColor(0xFF000000);
        mPaintArcCircle.setStrokeWidth(DimenUtil.dip2px(mContext, 2f));

        mPaintDate = new Paint();
        mPaintDate.setAntiAlias(true);
        mPaintDate.setStyle(Paint.Style.FILL);
        mPaintDate.setTypeface(Typeface.DEFAULT_BOLD);
        mPaintDate.setColor(0xFFFFFFFF);

        setUpdateTimePerSecondImmediately(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mScaleRadius = Math.min(mPointScreenCenter.x, mPointScreenCenter.y) - DimenUtil.dip2px(mContext, 6);
        mPathTextLogo.addArc(-mScaleRadius, -mScaleRadius, mScaleRadius, mScaleRadius, -114, 48);

        final float cornerArcRadius = 1.3f * mScaleLength + mScaleRadius;
        final int startAngle = arc_distance_degree_start, sweepAngle = 42;
        mPathCornerArc.addArc(-cornerArcRadius, -cornerArcRadius, cornerArcRadius, cornerArcRadius, startAngle, sweepAngle);
        //圆弧从圆的顶部顶点开始绘制，那里的弧度是－90度，而LinearGradient着色器在那里的角度是0度，所以要想拿到弧度两端的坐标位置得往回加90度
        int startDegrees = 90 + startAngle, endDegrees = startDegrees + sweepAngle;//开始18度，结束60度
        float centreDegrees = startDegrees + (endDegrees - startDegrees) / 2f;//18度和60度中间的角度
        float sinStart = (float) Math.sin(Math.toRadians(startDegrees));
        float cosStart = (float) Math.cos(Math.toRadians(startDegrees));
        float sinEnd = (float) Math.sin(Math.toRadians(endDegrees));
        float cosEnd = (float) Math.cos(Math.toRadians(endDegrees));
        mPaintCornerArc.setShader(new LinearGradient(cornerArcRadius * sinStart, -cornerArcRadius * cosStart
                , cornerArcRadius * sinEnd, -cornerArcRadius * cosEnd,
                new int[]{0xFF0FF416, 0xFFFFE600, 0xFFFF0000}, null, Shader.TileMode.CLAMP));
        float sinCenter = (float) Math.sin(Math.toRadians(centreDegrees));
        float cosCenter = (float) Math.cos(Math.toRadians(centreDegrees));
        mRectFArcCircleCentre.x = cornerArcRadius * sinCenter;
        mRectFArcCircleCentre.y = -cornerArcRadius * cosCenter;

        PointF pointF = new PointF();
        float infoIconRadius = 2.1f * mScaleLength + cornerArcRadius;
        pointF.x = infoIconRadius * sinCenter;
        pointF.y = -infoIconRadius * cosCenter;
        Bitmap bitmapIcon = mBitmapManager.get(R.drawable.icon_distance);
        mRectFDistance.left = pointF.x - bitmapIcon.getWidth() / 2f;
        mRectFDistance.top = pointF.y - bitmapIcon.getHeight() / 2f;
        mRectFDistance.right = pointF.x + bitmapIcon.getWidth() / 2f;
        mRectFDistance.bottom = pointF.y + bitmapIcon.getHeight() / 2f;
        startDegrees += ARC_STEPS_DEGREE_OFFSET;
        endDegrees = startDegrees + sweepAngle;//开始18度，结束60度
        centreDegrees = startDegrees + (endDegrees - startDegrees) / 2f;
        sinCenter = (float) Math.sin(Math.toRadians(centreDegrees));
        cosCenter = (float) Math.cos(Math.toRadians(centreDegrees));
        pointF.x = infoIconRadius * sinCenter;
        pointF.y = -infoIconRadius * cosCenter;
        bitmapIcon = mBitmapManager.get(R.drawable.icon_steps);
        mRectFSteps.left = pointF.x - bitmapIcon.getWidth() / 2f;
        mRectFSteps.top = pointF.y - bitmapIcon.getHeight() / 2f;
        mRectFSteps.right = pointF.x + bitmapIcon.getWidth() / 2f;
        mRectFSteps.bottom = pointF.y + bitmapIcon.getHeight() / 2f;
        startDegrees += arc_alarm_degree_offset;
        endDegrees = startDegrees + sweepAngle;
        centreDegrees = startDegrees + (endDegrees - startDegrees) / 2f;
        sinCenter = (float) Math.sin(Math.toRadians(centreDegrees));
        cosCenter = (float) Math.cos(Math.toRadians(centreDegrees));
        pointF.x = infoIconRadius * sinCenter - DimenUtil.dip2px(mContext, 8);
        pointF.y = -infoIconRadius * cosCenter + DimenUtil.dip2px(mContext, 1);
        bitmapIcon = mBitmapManager.get(R.drawable.icon_alarm);
        mRectFAlarm.left = pointF.x - bitmapIcon.getWidth() / 2f;
        mRectFAlarm.top = pointF.y - bitmapIcon.getHeight() / 2f;
        mRectFAlarm.right = pointF.x + bitmapIcon.getWidth() / 2f;
        mRectFAlarm.bottom = pointF.y + bitmapIcon.getHeight() / 2f;
        startDegrees += ARC_WEATHER_DEGREE_OFFSET;
        endDegrees = startDegrees + sweepAngle;
        centreDegrees = startDegrees + (endDegrees - startDegrees) / 2f;
        sinCenter = (float) Math.sin(Math.toRadians(centreDegrees));
        cosCenter = (float) Math.cos(Math.toRadians(centreDegrees));
        pointF.x = infoIconRadius * sinCenter - DimenUtil.dip2px(mContext, 8);
        pointF.y = -infoIconRadius * cosCenter - DimenUtil.dip2px(mContext, 1);
        bitmapIcon = mBitmapManager.get(R.drawable.paint_weather_no_data);
        mRectFWeather.left = pointF.x - bitmapIcon.getWidth() / 2f;
        mRectFWeather.top = pointF.y - bitmapIcon.getHeight() / 2f;
        mRectFWeather.right = pointF.x + bitmapIcon.getWidth() / 2f;
        mRectFWeather.bottom = pointF.y + bitmapIcon.getHeight() / 2f;

        Bitmap bitmap = mBitmapManager.get(R.drawable.icon_heart_rate);
        mRectFHeartRate.left = mPointScreenCenter.x - (mScaleRadius + bitmap.getWidth()) / 2;
        mRectFHeartRate.right = mRectFHeartRate.left + bitmap.getWidth();
        mRectFHeartRate.top = mPointScreenCenter.y - bitmap.getHeight() / 2f;
        mRectFHeartRate.bottom = mRectFHeartRate.top + bitmap.getHeight();
        bitmap = mBitmapManager.get(R.drawable.icon_contacts);
        mRectFContacts.left = mPointScreenCenter.x + (mScaleRadius - bitmap.getWidth()) / 2f;
        mRectFContacts.right = mRectFContacts.left + bitmap.getWidth();
        mRectFContacts.top = mPointScreenCenter.y - bitmap.getHeight() / 2f;
        mRectFContacts.bottom = mRectFContacts.top + bitmap.getHeight();
        bitmap = mBitmapManager.get(R.drawable.icon_music);
        mRectFMusic.left = mPointScreenCenter.x - bitmap.getWidth() / 2f;
        mRectFMusic.right = mRectFMusic.left + bitmap.getWidth();
        mRectFMusic.top = mPointScreenCenter.y - (mScaleRadius + bitmap.getHeight()) / 2;
        mRectFMusic.bottom = mRectFMusic.top + bitmap.getHeight();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        notifyMsgUpdateTimePerSecond();
    }

    @Override
    public void onVisibilityChanged(boolean visible) {
        super.onVisibilityChanged(visible);
        if (visible) {
            this.getAlarmClockInfo();

            try {
                this.queryWeatherInfo();
                mContext.getContentResolver().registerContentObserver(WEATHER_URI, true, mContentObserver);

                this.queryStepAndCalories();
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

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(ContextCompat.getColor(mContext, android.R.color.transparent), PorterDuff.Mode.CLEAR);

        this.paintClickIcons(canvas);
        this.paintDate(canvas);
        this.paintScale(canvas);
        this.paintInfoIcon(canvas);
    }

    private void paintScale(Canvas canvas) {
        canvas.save();
        canvas.translate(mPointScreenCenter.x, mPointScreenCenter.y);

        mPaintScale.setColor(0xFF808080);
        mPaintScale.setStyle(Paint.Style.FILL);
        float degrees;
        for (int index = 0; index < SCALES; index++) {
            degrees = index * SCALE_DEGREE;

            if ((index % 10 == 0) || index < 10 || index > (SCALES - 10)) {
                continue;
            }

            canvas.save();

            canvas.rotate(degrees);
            canvas.drawLine(0, -(mScaleRadius - mScaleLength), 0, -mScaleRadius, mPaintScale);

            canvas.restore();
        }

        mPaintScale.setColor(0xFFFFFFFF);
        canvas.drawTextOnPath("SMART  WATCH", mPathTextLogo, 0, DimenUtil.dip2px(mContext, 17), mPaintScale);

        canvas.save();
        canvas.drawPath(mPathCornerArc, mPaintCornerArc);
        canvas.drawCircle(mRectFArcCircleCentre.x, mRectFArcCircleCentre.y, mArcCircleRadius, mPaintArcCircle);
        canvas.rotate(ARC_STEPS_DEGREE_OFFSET);
        canvas.drawPath(mPathCornerArc, mPaintCornerArc);
        canvas.drawCircle(mRectFArcCircleCentre.x, mRectFArcCircleCentre.y, mArcCircleRadius, mPaintArcCircle);
        canvas.rotate(arc_alarm_degree_offset);
        canvas.drawPath(mPathCornerArc, mPaintCornerArc);
        canvas.drawCircle(mRectFArcCircleCentre.x, mRectFArcCircleCentre.y, mArcCircleRadius, mPaintArcCircle);
        canvas.rotate(ARC_WEATHER_DEGREE_OFFSET);
        canvas.drawPath(mPathCornerArc, mPaintCornerArc);
        canvas.drawCircle(mRectFArcCircleCentre.x, mRectFArcCircleCentre.y, mArcCircleRadius, mPaintArcCircle);
        canvas.restore();

        this.paintPointer(canvas);

        canvas.restore();
    }

    private void paintPointer(Canvas canvas) {
        Calendar calendar = Calendar.getInstance();
        int hour = getHour();
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        canvas.save();
        Bitmap bitmap = mBitmapManager.get(R.drawable.pointer_minute);
        canvas.rotate((minute + second / 60f) / 60 * 360);
        mRectFMinute.set(-bitmap.getWidth() / 2f, -mScaleRadius + 1.2f * mScaleLength
                , bitmap.getWidth() / 2f, mScaleRadius - 1.2f * mScaleLength);
        canvas.drawBitmap(bitmap, null, mRectFMinute, mPaintPointer);
        //canvas.drawBitmap(bitmap, -bitmap.getWidth() / 2f, -mScaleRadius, null);
        //canvas.drawBitmap(bitmap.extractAlpha(), -bitmap.getWidth() / 2f, -mScaleRadius, mPaintScale);
        canvas.restore();

        canvas.save();
        bitmap = mBitmapManager.get(R.drawable.pointer_hour);
        canvas.rotate((hour + minute / 60f) / 12 * 360);
        mRectFHour.set(-bitmap.getWidth() / 2f, -mScaleRadius, bitmap.getWidth() / 2f, mScaleRadius);
        canvas.drawBitmap(bitmap, null, mRectFHour, mPaintPointer);
        //canvas.drawBitmap(bitmap.extractAlpha(), -bitmap.getWidth() / 2f, -mScaleRadius, mPaintScale);
        //canvas.drawBitmap(bitmap, -bitmap.getWidth() / 2f, -mScaleRadius, null);
        canvas.restore();

        canvas.save();
        bitmap = mBitmapManager.get(R.drawable.pointer_second);
        canvas.rotate(second / 60f * 360);
        mRectFSecond.set(-bitmap.getWidth() / 2f + 0.5f, -mScaleRadius, bitmap.getWidth() / 2f + 0.5f, mScaleRadius);
        canvas.drawBitmap(bitmap, null, mRectFSecond, mPaintPointer);
        //canvas.drawBitmap(bitmap, -bitmap.getWidth() / 2f, -mScaleRadius, null);
        canvas.restore();
    }

    private void paintDate(Canvas canvas) {
        canvas.save();
        canvas.translate(mPointScreenCenter.x, mPointScreenCenter.y + mScaleRadius / 1.7f);
        Calendar calendar = Calendar.getInstance();

        mRectDate.setEmpty();
        String week;
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case 2:
                week = "Mon";
                break;
            case 3:
                week = "Tues";
                break;
            case 4:
                week = "Wed";
                break;
            case 5:
                week = "Thur";
                break;
            case 6:
                week = "Fri";
                break;
            case 7:
                week = "Sat";
                break;
            default:
                week = "Sun";
        }
        mPaintDate.setTextSize(DimenUtil.dip2px(mContext, 30));
        mPaintDate.getTextBounds(week, 0, week.length(), mRectDate);
        canvas.drawText(week, -mRectDate.centerX(), mRectDate.height() * 1.4f, mPaintDate);

        mRectDate.setEmpty();
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String date = month / 10 + "" + month % 10 + "-" + day / 10 + "" + day % 10;
        mPaintDate.setTextSize(DimenUtil.dip2px(mContext, 45));
        mPaintDate.getTextBounds(date, 0, date.length(), mRectDate);
        canvas.drawText(date, -mRectDate.centerX(), 0, mPaintDate);

        canvas.restore();
    }

    private void paintInfoIcon(Canvas canvas) {
        int hundred = mHeartRate / 100;
        int tens = (mHeartRate % 100) / 10;
        int units = mHeartRate % 10;

        mPaintTpe.setTextSize(DimenUtil.dip2px(mContext, 26));
        String text = hundred + "" + tens + "" + units;
        mPaintTpe.getTextBounds(text, 0, text.length(), mRectTpe);
        canvas.drawText(text, mRectFHeartRate.centerX() - mRectTpe.centerX()
                , mRectFHeartRate.bottom - mRectTpe.height(), mPaintTpe);

        canvas.save();
        canvas.translate(mPointScreenCenter.x, mPointScreenCenter.y);
        mPaintTpe.setTextSize(DimenUtil.dip2px(mContext, 21));

        //运动距离
        canvas.drawBitmap(mBitmapManager.get(R.drawable.icon_distance), mRectFDistance.left, mRectFDistance.top, null);
        mRectTpe.setEmpty();
        mPaintTpe.getTextBounds(mDistance, 0, mDistance.length(), mRectTpe);
        canvas.drawText(mDistance, mRectFDistance.right - mRectTpe.width() - DimenUtil.dip2px(mContext, 4)
                , mRectFDistance.top - DimenUtil.dip2px(mContext, 3), mPaintTpe);

        //步数
        canvas.drawBitmap(mBitmapManager.get(R.drawable.icon_steps), mRectFSteps.left, mRectFSteps.top, null);
        mRectTpe.setEmpty();
        text = String.valueOf(mSteps);
        mPaintTpe.getTextBounds(text, 0, text.length(), mRectTpe);
        canvas.drawText(text, mRectFSteps.left - mRectTpe.width() + DimenUtil.dip2px(mContext, 10)
                , mRectFSteps.bottom + mRectTpe.height() + DimenUtil.dip2px(mContext, 3), mPaintTpe);

        //闹钟
        canvas.drawBitmap(mBitmapManager.get(R.drawable.icon_alarm), mRectFAlarm.left
                , mRectFAlarm.top - DimenUtil.dip2px(mContext, 2), null);
        mRectTpe.setEmpty();
        mPaintTpe.getTextBounds(mAlarmInfo, 0, mAlarmInfo.length(), mRectTpe);
        canvas.drawText(mAlarmInfo, mRectFAlarm.right - DimenUtil.dip2px(mContext, 10)
                , mRectFAlarm.bottom + mRectTpe.height() + DimenUtil.dip2px(mContext, 1), mPaintTpe);

        //天气
        canvas.drawBitmap(mBitmapManager.get(mWeatherIconRes, 0xFFFAAE45), mRectFWeather.left, mRectFWeather.top, null);
        String weather;
        if (mTemperature == Integer.MAX_VALUE) {
            weather = String.format(mContext.getString(mTemperatureUnitRes), "NA");
        } else {
            weather = mContext.getString(mTemperatureUnitRes, String.valueOf(mTemperature));
        }
        canvas.drawText(weather, mRectFWeather.right - DimenUtil.dip2px(mContext, 8)
                , mRectFWeather.top - DimenUtil.dip2px(mContext, 1), mPaintTpe);

        canvas.restore();
    }

    private void paintClickIcons(Canvas canvas) {
            /*canvas.drawBitmap(mBitmapManager.get(R.drawable.icon_heart_rate), mRectFHeartRate.left, mRectFHeartRate.top, null);
            canvas.drawBitmap(mBitmapManager.get(R.drawable.icon_music), mRectFMusic.left, mRectFMusic.top, null);
            canvas.drawBitmap(mBitmapManager.get(R.drawable.icon_contacts), mRectFContacts.left, mRectFContacts.top, null);*/
        canvas.drawBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon_heart_rate), mRectFHeartRate.left, mRectFHeartRate.top, null);
        canvas.drawBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon_music), mRectFMusic.left, mRectFMusic.top, null);
        canvas.drawBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon_contacts), mRectFContacts.left, mRectFContacts.top, null);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        Log.d(mTAG, "x: " + x + ", y: " + y + ", rawX: " + event.getRawX() + ", rawY: " + event.getRawY());
        if (!(mRectFMusic.contains(x, y) || mRectFHeartRate.contains(x, y) || mRectFContacts.contains(x, y))) {
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
                } else if (mRectFHeartRate.contains(x, y)) {
                    IntentHelper.openHeartRateApp(mContext);
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

    public static final String AUTHORITY = "com.wiz.watch.weather";
    public final Uri WEATHER_URI = Uri.parse("content://" + AUTHORITY + "/weathers");
    public static final String QUERY_WEATHER_INFO = "query_weather_info";
    private int mWeatherIconRes = R.drawable.paint_weather_no_data;
    private int mTemperature = Integer.MAX_VALUE, mTemperatureUnitRes = R.string.tpe_unit_c;

    private final ContentObserver mContentObserver = new ContentObserver(null) {
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
    private int mHeartRate;
    private int mSteps;
    private String mDistance = "0 M";

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
            mHeartRate = 0;
            Log.e(mTAG, "query heart rate error: " + e.toString(), new Exception());
        }
    }

    private void queryStepAndCalories() {
        try {
            Bundle bundle = mContext.getContentResolver().call(Uri.parse("content://" + AUTHORITY_HEALTH),
                    "sport_step/query_current", mContext.getPackageName(), null);
            if (bundle == null) {
                Log.e(mTAG, "query sport data error: bundle is null");
                return;
            }

            mSteps = bundle.getInt("step");
            //mSteps = 65487;
            long distance = bundle.getLong("distance");//米
            //distance = 1390;
            Log.d(mTAG, "steps: " + mSteps + ", distance: " + distance);
            if (distance < 1000) {
                mDistance = distance + "M";
            } else {
                mDistance = String.format(Locale.getDefault(), "%.1f", (distance / 1000.0)) + "KM";
            }
        } catch (Exception e) {
            Log.e(mTAG, "query step and calories error: " + e.toString(), new Exception());
            mSteps = 0;
            mDistance = "0 M";
        }
    }

    private String mAlarmInfo = "";

    private void getAlarmClockInfo() {
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        AlarmManager.AlarmClockInfo alarmClockInfo = alarmManager.getNextAlarmClock();
        if (alarmClockInfo == null) {
            Log.w(mTAG, "alarm clock info is null");
            mAlarmInfo = "";
            return;
        }

        long triggerTime = alarmClockInfo.getTriggerTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(triggerTime);
        int minute = calendar.get(Calendar.MINUTE);
        int hour = calendar.get(Calendar.HOUR);
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int ampm = calendar.get(Calendar.AM_PM);//0：上午，1：下午
        Log.d(mTAG, "alarm clock info, triggerTime: " + triggerTime + ", ampm: " + ampm
                + ", hour: " + hour + ", minute: " + minute + ", hourOfDay: " + hourOfDay);
        if (DateFormat.is24HourFormat(mContext)) {
            mAlarmInfo = hourOfDay / 10 + "" + hourOfDay % 10 + ":" + minute / 10 + "" + minute % 10;
        } else {
            mAlarmInfo = hour / 10 + "" + hour % 10 + ":" + minute / 10
                    + "" + minute % 10 + (ampm == 0 ? "AM" : "PM");
        }
    }

}
