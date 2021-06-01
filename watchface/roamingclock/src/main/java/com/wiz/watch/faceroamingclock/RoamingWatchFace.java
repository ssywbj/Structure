package com.wiz.watch.faceroamingclock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.icu.text.TimeZoneNames;
import android.os.AsyncTask;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.structure.wallpaper.basic.WatchFaceView;
import com.structure.wallpaper.basic.utils.DimenUtil;
import com.wiz.watch.faceroamingclock.time.SsCity;
import com.wiz.watch.faceroamingclock.time.SsWorldTimeData;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class RoamingWatchFace extends WatchFaceView {
    private float mMarginHorizontal;

    private Paint mPaint;
    private Paint mPaintCity;
    private final Rect mRect = new Rect();
    private float mMarginTime;
    private int mTextSizeBig, mTextSizeSmall, mTextSizeSmaller;

    private RoamingBitmapManager mBitmapManager;
    private SharedPreferences mPrefs;

    private String mLanguage = "en";
    private float mMarginDateWeek;
    private LoadAsyncTask mLoadAsyncTask;
    private final Map<String, String> mMapCity = new HashMap<>();

    private int mCurrentHour;
    private int mCurrentMinute;
    private int mCurrentHourCity1;
    private int mCurrentMinuteCity1;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (RoamingWatchFaceConfigFragment.ACTION_UPDATE_FACE.equals(intent.getAction())) {
                invalidate();
            } else if (Intent.ACTION_LOCALE_CHANGED.equals(intent.getAction())) {
                mLoadAsyncTask.cancel(true);
                mLoadAsyncTask = null;
                mLoadAsyncTask = new LoadAsyncTask(RoamingWatchFace.this);
                mLoadAsyncTask.execute();
            }
        }
    };

    public RoamingWatchFace(Context context) {
        super(context);
        this.init();
    }

    public RoamingWatchFace(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public void init() {
        mBitmapManager = new RoamingBitmapManager(mContext);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.parseColor("#EB483F"));
        mPaint.setStyle(Paint.Style.FILL);

        mPaintCity = new Paint();
        mPaintCity.setAntiAlias(true);
        mPaintCity.setTypeface(Typeface.DEFAULT_BOLD);
        mTextSizeSmaller = DimenUtil.dip2px(mContext, 19);
        mTextSizeSmall = DimenUtil.dip2px(mContext, 22);
        mTextSizeBig = DimenUtil.dip2px(mContext, 25);
        mPaintCity.setTextSize(mTextSizeBig);
        mPaintCity.setColor(ContextCompat.getColor(mContext, R.color.text_city));

        mPrefs = mContext.getSharedPreferences(RoamingWatchFaceConfigFragment.PREFS_FILE, Context.MODE_PRIVATE);

        mCurrentMinute = Calendar.getInstance().get(Calendar.MINUTE);
        mCurrentHour = getHour();
        Calendar instance = this.getCalendarCity1();
        mCurrentHourCity1 = instance.get(Calendar.HOUR_OF_DAY);
        mCurrentMinuteCity1 = instance.get(Calendar.MINUTE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mMarginHorizontal = DimenUtil.dip2px(mContext, 26);
        mMarginTime = DimenUtil.dip2px(mContext, 12);
        mMarginDateWeek = DimenUtil.dip2px(mContext, 12);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RoamingWatchFaceConfigFragment.ACTION_UPDATE_FACE);
        intentFilter.addAction(Intent.ACTION_LOCALE_CHANGED);
        mContext.registerReceiver(mReceiver, intentFilter);

        if (mLoadAsyncTask != null) {
            mLoadAsyncTask.cancel(true);
        }
        mLoadAsyncTask = null;
        mLoadAsyncTask = new LoadAsyncTask(this);
        mLoadAsyncTask.execute();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mContext.unregisterReceiver(mReceiver);

        mBitmapManager.clear();
        SsWorldTimeData.clearSearchList();
        mMapCity.clear();
        if (mLoadAsyncTask != null) {
            mLoadAsyncTask.cancel(true);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        getHandler().removeMessages(MSG_UPDATE_TIME);
    }

    @Override
    protected void onTimeTick() {
        super.onTimeTick();
        if (mIsPlayingAnim) {
            return;
        }
        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(ContextCompat.getColor(mContext, android.R.color.transparent), PorterDuff.Mode.CLEAR);

        final float lineWidth = DimenUtil.dip2px(mContext, 3.5f);
        canvas.save();
        canvas.rotate(44f, mPointScreenCenter.x, mPointScreenCenter.y);
        canvas.drawRoundRect(mPointScreenCenter.x - lineWidth / 2, 0, mPointScreenCenter.x + lineWidth / 2
                , 2 * mPointScreenCenter.y, lineWidth, lineWidth, mPaint);
        canvas.restore();

        Locale locale = Locale.getDefault();
        mLanguage = locale.getLanguage();

        if (mIsPlayingAnim && !mIsEditMode) {
            this.paintCity1(canvas, mHourCity1, mMinuteCity1);
            this.paintCity2(canvas, mHour, mMinute);

            if (mIsFirstPlayAnim) {
                //目的是为了让第一帧停留的时间久一些，也就是让用户看到初始值的显示
                getHandler().sendEmptyMessageDelayed(MSG_UPDATE_TIME, 160);
            } else {
                getHandler().removeMessages(MSG_UPDATE_TIME);
                getHandler().sendEmptyMessageDelayed(MSG_UPDATE_TIME, 25);
            }
        } else {
            this.paintCity2(canvas, getHour(), Calendar.getInstance().get(Calendar.MINUTE));

            Calendar instance = this.getCalendarCity1();
            this.paintCity1(canvas, instance.get(Calendar.HOUR_OF_DAY), instance.get(Calendar.MINUTE));
        }
    }

    private Calendar getCalendarCity1() {
        String timeZone = mPrefs.getString(RoamingWatchFaceConfigFragment.PREFS_KEY_GMT, DEFAULT_CITY1_TIME_ZONE);
        return Calendar.getInstance(TimeZone.getTimeZone(timeZone));
    }

    private void loadMapCity(List<SsCity> ssCities) {
        if (ssCities == null) {
            return;
        }

        String displayName;
        for (SsCity ssCity : ssCities) {
            displayName = ssCity.getName();
            mMapCity.put(ssCity.getId(), displayName.substring(0, displayName.indexOf(",")).trim());
        }
    }

    private void paintCity1(Canvas canvas, int hour, int minute) {
        //city: London , UnitedKingdom; Id: Europe/London; gmt: GMT-4:00，英文状态下默认显示伦敦时间
        String cityId = mPrefs.getString(RoamingWatchFaceConfigFragment.PREFS_KEY_CITY_ID, "Europe/London");
        String city = mMapCity.get(cityId);
        if (TextUtils.isEmpty(city)) {
            city = "London";
        }
        this.setPaintTextSize(city);
        mPaintCity.getTextBounds(city, 0, city.length(), mRect);
        canvas.drawText(city, 2 * mPointScreenCenter.x - mRect.width() - mMarginHorizontal,
                mPointScreenCenter.y + mRect.height(), mPaintCity);
        String timeZone = mPrefs.getString(RoamingWatchFaceConfigFragment.PREFS_KEY_GMT, DEFAULT_CITY1_TIME_ZONE);
        Calendar instance = Calendar.getInstance(TimeZone.getTimeZone(timeZone));

        //分
        int color = ContextCompat.getColor(mContext, android.R.color.white);
        int units = minute % 10;//个位
        int tens = minute / 10;//十位
        Bitmap bitmap = mBitmapManager.get(mBitmapManager.getBigNumberResId(units), color);
        float left = 2 * mPointScreenCenter.x - 1.0f * bitmap.getWidth() - mMarginHorizontal;
        float top = mPointScreenCenter.y + mRect.height() + mMarginTime;
        canvas.drawBitmap(bitmap, left, top, null);
        bitmap = mBitmapManager.get(mBitmapManager.getBigNumberResId(tens), color);
        left -= bitmap.getWidth();
        canvas.drawBitmap(bitmap, left, top, null);

        //冒号
        bitmap = mBitmapManager.get(R.drawable.paint_sign_colon, color);
        left -= bitmap.getWidth();
        canvas.drawBitmap(bitmap, left, top, null);

        //时
        units = hour % 10;//个位
        tens = hour / 10;//十位
        bitmap = mBitmapManager.get(mBitmapManager.getBigNumberResId(units), color);
        left -= bitmap.getWidth();
        canvas.drawBitmap(bitmap, left, top, null);
        bitmap = mBitmapManager.get(mBitmapManager.getBigNumberResId(tens), color);
        left -= bitmap.getWidth();
        canvas.drawBitmap(bitmap, left, top, null);

        //星期
        top += (bitmap.getHeight() + mMarginTime);
        left = 2 * mPointScreenCenter.x - mMarginHorizontal - DimenUtil.dip2px(mContext, 3);
        color = ContextCompat.getColor(mContext, R.color.text_city);
        if ("zh".equals(mLanguage)) {
            bitmap = mBitmapManager.get(mBitmapManager.getWeekResId(timeZone), color);
            left -= bitmap.getWidth();
            canvas.drawBitmap(bitmap, left, top, null);
            bitmap = mBitmapManager.get(R.drawable.paint_text_week_middle, color);
            left -= bitmap.getWidth();
            canvas.drawBitmap(bitmap, left, top, null);
        } else {
            switch (instance.get(Calendar.DAY_OF_WEEK)) {
                case 2:
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_n_middle, color);
                    left -= bitmap.getWidth();
                    canvas.drawBitmap(bitmap, left, top, null);
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_o, color);
                    left -= bitmap.getWidth();
                    canvas.drawBitmap(bitmap, left, top, null);
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_m, color);
                    left -= bitmap.getWidth();
                    canvas.drawBitmap(bitmap, left, top, null);
                    break;
                case 3:
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_s, color);
                    left -= bitmap.getWidth();
                    canvas.drawBitmap(bitmap, left, top, null);
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_e, color);
                    left -= bitmap.getWidth();
                    canvas.drawBitmap(bitmap, left, top, null);
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_u, color);
                    left -= bitmap.getWidth();
                    canvas.drawBitmap(bitmap, left, top, null);
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_t, color);
                    left -= bitmap.getWidth();
                    canvas.drawBitmap(bitmap, left, top, null);
                    break;
                case 4:
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_d, color);
                    left -= bitmap.getWidth();
                    canvas.drawBitmap(bitmap, left, top, null);
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_e, color);
                    left -= bitmap.getWidth();
                    canvas.drawBitmap(bitmap, left, top, null);
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_w, color);
                    left -= bitmap.getWidth();
                    canvas.drawBitmap(bitmap, left, top, null);
                    break;
                case 5:
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_r, color);
                    left -= bitmap.getWidth();
                    canvas.drawBitmap(bitmap, left, top, null);
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_u, color);
                    left -= bitmap.getWidth();
                    canvas.drawBitmap(bitmap, left, top, null);
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_h, color);
                    left -= bitmap.getWidth();
                    canvas.drawBitmap(bitmap, left, top, null);
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_t, color);
                    left -= bitmap.getWidth();
                    canvas.drawBitmap(bitmap, left, top, null);
                    break;
                case 6:
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_i, color);
                    left -= bitmap.getWidth();
                    canvas.drawBitmap(bitmap, left, top, null);
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_r, color);
                    left -= bitmap.getWidth();
                    canvas.drawBitmap(bitmap, left, top, null);
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_f, color);
                    left -= bitmap.getWidth();
                    canvas.drawBitmap(bitmap, left, top, null);
                    break;
                case 7:
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_t, color);
                    left -= bitmap.getWidth();
                    canvas.drawBitmap(bitmap, left, top, null);
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_a_middle, color);
                    left -= bitmap.getWidth();
                    canvas.drawBitmap(bitmap, left, top, null);
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_s, color);
                    left -= bitmap.getWidth();
                    canvas.drawBitmap(bitmap, left, top, null);
                    break;
                default:
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_n_middle, color);
                    left -= bitmap.getWidth();
                    canvas.drawBitmap(bitmap, left, top, null);
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_u, color);
                    left -= bitmap.getWidth();
                    canvas.drawBitmap(bitmap, left, top, null);
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_s, color);
                    left -= bitmap.getWidth();
                    canvas.drawBitmap(bitmap, left, top, null);
            }
        }

        //日
        left -= mMarginDateWeek;
        if ("zh".equals(mLanguage)) {
            bitmap = mBitmapManager.get(R.drawable.paint_text_day_middle, color);
            left -= bitmap.getWidth();
            canvas.drawBitmap(bitmap, left, top, null);
        }
        int day = instance.get(Calendar.DAY_OF_MONTH);
        units = day % 10;//个位
        tens = day / 10;//十位
        bitmap = mBitmapManager.get(mBitmapManager.getSmallNumberResId(units), color);
        left -= bitmap.getWidth();
        canvas.drawBitmap(bitmap, left, top, null);
        bitmap = mBitmapManager.get(mBitmapManager.getSmallNumberResId(tens), color);
        left -= bitmap.getWidth();
        canvas.drawBitmap(bitmap, left, top, null);

        //月
        float offset = 0;
        if ("zh".equals(mLanguage)) {
            bitmap = mBitmapManager.get(R.drawable.paint_text_month_middle, color);
        } else {
            bitmap = mBitmapManager.get(R.drawable.paint_number_ic_point, color);
            offset = 1.0f * bitmap.getWidth() / 5;
            left += offset;
        }
        left -= bitmap.getWidth();
        canvas.drawBitmap(bitmap, left, top, null);
        left += offset;
        int month = instance.get(Calendar.MONTH) + 1;
        units = month % 10;//个位
        tens = month / 10;//十位
        bitmap = mBitmapManager.get(mBitmapManager.getSmallNumberResId(units), color);
        left -= bitmap.getWidth();
        canvas.drawBitmap(bitmap, left, top, null);
        bitmap = mBitmapManager.get(mBitmapManager.getSmallNumberResId(tens), color);
        left -= bitmap.getWidth();
        canvas.drawBitmap(bitmap, left, top, null);
    }

    private void setPaintTextSize(String city) {
        int length = city.length();
        if ("zh".equals(mLanguage)) {
            if (length > 6) {
                mPaintCity.setTextSize(mTextSizeSmaller);
            } else if (length == 6) {
                mPaintCity.setTextSize(mTextSizeSmall);
            } else {
                mPaintCity.setTextSize(mTextSizeBig);
            }
        } else {
            if (length > 11) {
                mPaintCity.setTextSize(mTextSizeSmaller);
            } else if (length == 11) {
                mPaintCity.setTextSize(mTextSizeSmall);
            } else {
                mPaintCity.setTextSize(mTextSizeBig);
            }
        }
    }

    private static final int DEFAULT_HOUR_CITY1 = 22, DEFAULT_MINUTE_CITY1 = 36;
    private int mHourCity1 = DEFAULT_HOUR_CITY1, mMinuteCity1 = DEFAULT_MINUTE_CITY1;//22:36
    private static final String DEFAULT_CITY1_TIME_ZONE = "GMT+01:00";

    private static final int DEFAULT_HOUR = 8, DEFAULT_MINUTE = 36;
    private static final int MSG_UPDATE_TIME = 12;
    private int mHour = DEFAULT_HOUR, mMinute = DEFAULT_MINUTE;//08:36

    private boolean mIsPlayingAnim = true, mIsFirstPlayAnim = true;

    @Override
    protected void dispatchMsg(Message msg) {
        if (mIsFirstPlayAnim) {
            mIsFirstPlayAnim = false;
        }

        if (mHour != mCurrentHour) {
            if (DEFAULT_HOUR < mCurrentHour) {
                mHour++;
            } else if (DEFAULT_HOUR > mCurrentHour) {
                mHour--;
            }
        }

        if (mMinute != mCurrentMinute) {
            if (DEFAULT_MINUTE < mCurrentMinute) {
                mMinute++;
            } else if (DEFAULT_MINUTE > mCurrentMinute) {
                mMinute--;
            }
        }

        if (mHourCity1 != mCurrentHourCity1) {
            if (DEFAULT_HOUR_CITY1 < mCurrentHourCity1) {
                mHourCity1++;
            } else if (DEFAULT_HOUR_CITY1 > mCurrentHourCity1) {
                mHourCity1--;
            }
        }

        if (mMinuteCity1 != mCurrentMinuteCity1) {
            if (DEFAULT_MINUTE < mCurrentMinuteCity1) {
                mMinuteCity1++;
            } else if (DEFAULT_MINUTE_CITY1 > mCurrentMinuteCity1) {
                mMinuteCity1--;
            }
        }

        if ((mHour == mCurrentHour) && (mMinute == mCurrentMinute) && (mHourCity1 == mCurrentHourCity1) && (mMinuteCity1 == mCurrentMinuteCity1)) {
            mIsPlayingAnim = false;
        }

        invalidate();
    }

    private void paintCity2(Canvas canvas, int hour, int minute) {
        //取系统设置时间与区域信息
        float top = mPointScreenCenter.y;

        String city = TimeZoneNames.getInstance(Locale.getDefault()).getExemplarLocationName(TimeZone.getDefault().getID());
        if (TextUtils.isEmpty(city)) {
            top -= DimenUtil.dip2px(mContext, 18);
        } else {
            this.setPaintTextSize(city);
            mPaintCity.getTextBounds(city, 0, city.length(), mRect);
            canvas.drawText(city, mMarginHorizontal, top - DimenUtil.dip2px(mContext, 4), mPaintCity);
            top -= mRect.height();
        }

        Calendar instance = Calendar.getInstance(TimeZone.getDefault());
        int color = ContextCompat.getColor(mContext, android.R.color.white);

        //时
        int units = hour % 10;//个位
        int tens = hour / 10;//十位
        Bitmap bitmap = mBitmapManager.get(mBitmapManager.getBigNumberResId(tens), color);
        float left = mMarginHorizontal - DimenUtil.dip2px(mContext, 6);
        top -= (bitmap.getHeight() + mMarginTime);
        canvas.drawBitmap(bitmap, left, top, null);
        left += bitmap.getWidth();
        bitmap = mBitmapManager.get(mBitmapManager.getBigNumberResId(units), color);
        canvas.drawBitmap(bitmap, left, top, null);
        left += bitmap.getWidth();

        //冒号
        bitmap = mBitmapManager.get(R.drawable.paint_sign_colon, color);
        canvas.drawBitmap(bitmap, left, top, null);
        left += bitmap.getWidth();

        //分
        units = minute % 10;//个位
        tens = minute / 10;//十位
        bitmap = mBitmapManager.get(mBitmapManager.getBigNumberResId(tens), color);
        canvas.drawBitmap(bitmap, left, top, null);
        bitmap = mBitmapManager.get(mBitmapManager.getBigNumberResId(units), color);
        left += bitmap.getWidth();
        canvas.drawBitmap(bitmap, left, top, null);

        color = ContextCompat.getColor(mContext, R.color.text_city);
        int month = instance.get(Calendar.MONTH) + 1;
        //月
        units = month % 10;//个位
        tens = month / 10;//十位
        bitmap = mBitmapManager.get(mBitmapManager.getSmallNumberResId(tens), color);
        top -= (bitmap.getHeight() + mMarginTime);
        left = mMarginHorizontal;
        canvas.drawBitmap(bitmap, left, top, null);
        left += bitmap.getWidth();
        bitmap = mBitmapManager.get(mBitmapManager.getSmallNumberResId(units), color);
        canvas.drawBitmap(bitmap, left, top, null);
        left += bitmap.getWidth();
        float offset = 0;
        if ("zh".equals(mLanguage)) {
            bitmap = mBitmapManager.get(R.drawable.paint_text_month_middle, color);
        } else {
            bitmap = mBitmapManager.get(R.drawable.paint_number_ic_point, color);
            offset = 1.0f * bitmap.getWidth() / 5;
            left -= offset;
        }
        canvas.drawBitmap(bitmap, left, top, null);
        left += (bitmap.getWidth() - offset);

        int day = instance.get(Calendar.DAY_OF_MONTH);
        //日
        units = day % 10;//个位
        tens = day / 10;//十位
        bitmap = mBitmapManager.get(mBitmapManager.getSmallNumberResId(tens), color);
        canvas.drawBitmap(bitmap, left, top, null);
        left += bitmap.getWidth();
        bitmap = mBitmapManager.get(mBitmapManager.getSmallNumberResId(units), color);
        canvas.drawBitmap(bitmap, left, top, null);
        left += bitmap.getWidth();
        if ("zh".equals(mLanguage)) {
            bitmap = mBitmapManager.get(R.drawable.paint_text_day_middle, color);
            canvas.drawBitmap(bitmap, left, top, null);
            left += bitmap.getWidth();
        }

        left += mMarginDateWeek;
        //周
        if ("zh".equals(mLanguage)) {
            bitmap = mBitmapManager.get(R.drawable.paint_text_week_middle, color);
            canvas.drawBitmap(bitmap, left, top, null);
            left += bitmap.getWidth();
            bitmap = mBitmapManager.get(mBitmapManager.getWeekResId(null), color);
            canvas.drawBitmap(bitmap, left, top, null);
        } else {
            switch (instance.get(Calendar.DAY_OF_WEEK)) {
                case 2:
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_m, color);
                    canvas.drawBitmap(bitmap, left, top, null);
                    left += bitmap.getWidth();
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_o, color);
                    canvas.drawBitmap(bitmap, left, top, null);
                    left += bitmap.getWidth();
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_n_middle, color);
                    canvas.drawBitmap(bitmap, left, top, null);
                    break;
                case 3:
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_t, color);
                    canvas.drawBitmap(bitmap, left, top, null);
                    left += bitmap.getWidth();
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_u, color);
                    canvas.drawBitmap(bitmap, left, top, null);
                    left += bitmap.getWidth();
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_e, color);
                    canvas.drawBitmap(bitmap, left, top, null);
                    left += bitmap.getWidth();
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_s, color);
                    canvas.drawBitmap(bitmap, left, top, null);
                    break;
                case 4:
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_w, color);
                    canvas.drawBitmap(bitmap, left, top, null);
                    left += bitmap.getWidth();
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_e, color);
                    canvas.drawBitmap(bitmap, left, top, null);
                    left += bitmap.getWidth();
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_d, color);
                    canvas.drawBitmap(bitmap, left, top, null);
                    break;
                case 5:
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_t, color);
                    canvas.drawBitmap(bitmap, left, top, null);
                    left += bitmap.getWidth();
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_h, color);
                    canvas.drawBitmap(bitmap, left, top, null);
                    left += bitmap.getWidth();
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_u, color);
                    canvas.drawBitmap(bitmap, left, top, null);
                    left += bitmap.getWidth();
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_r, color);
                    canvas.drawBitmap(bitmap, left, top, null);
                    break;
                case 6:
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_f, color);
                    canvas.drawBitmap(bitmap, left, top, null);
                    left += bitmap.getWidth();
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_r, color);
                    canvas.drawBitmap(bitmap, left, top, null);
                    left += bitmap.getWidth();
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_i, color);
                    canvas.drawBitmap(bitmap, left, top, null);
                    break;
                case 7:
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_s, color);
                    canvas.drawBitmap(bitmap, left, top, null);
                    left += bitmap.getWidth();
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_a_middle, color);
                    canvas.drawBitmap(bitmap, left, top, null);
                    left += bitmap.getWidth();
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_t, color);
                    canvas.drawBitmap(bitmap, left, top, null);
                    break;
                default:
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_s, color);
                    canvas.drawBitmap(bitmap, left, top, null);
                    left += bitmap.getWidth();
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_u, color);
                    canvas.drawBitmap(bitmap, left, top, null);
                    left += bitmap.getWidth();
                    bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_n_middle, color);
                    canvas.drawBitmap(bitmap, left, top, null);
            }
        }
    }

    private static class LoadAsyncTask extends AsyncTask<Void, Void, Void> {

        private final WeakReference<RoamingWatchFace> mWeakReference;

        public LoadAsyncTask(RoamingWatchFace instance) {
            mWeakReference = new WeakReference<>(instance);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (mWeakReference.get() == null) {
                return null;
            }
            List<SsCity> worldTimeZones = SsWorldTimeData.getWorldTimeZones(mWeakReference.get().mContext);
            mWeakReference.get().loadMapCity(worldTimeZones);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (mWeakReference.get() == null) {
                return;
            }
            mWeakReference.get().invalidate();
        }
    }

}
