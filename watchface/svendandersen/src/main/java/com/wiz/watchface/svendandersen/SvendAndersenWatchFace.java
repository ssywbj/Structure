package com.wiz.watchface.svendandersen;

import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.structure.wallpaper.basic.view.FaceAnimView;
import com.wiz.watchface.svendandersen.view.ClockPanel;
import com.wiz.watchface.svendandersen.view.PanelView;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class SvendAndersenWatchFace extends FrameLayout {
    public static final String TAG = SvendAndersenWatchFace.class.getSimpleName();
    private static final float SCALE = 360 / 24f;
    private ClockPanel mClockPanel;
    private PanelView mPanelViewCity;
    private boolean mIsEditMode, mIsDimMode;
    private boolean mRegisteredChangedZoneReceiver;

    private final BroadcastReceiver mTimeChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setTimeZoneCity(false);
            mPanelViewCity.invalidate();
            setTimeCity(false);
            mClockPanel.invalidate();
        }
    };

    public SvendAndersenWatchFace(Context context, boolean isEditMode, boolean isDimMode) {
        super(context);
        mIsEditMode = isEditMode;
        mIsDimMode = isDimMode;
        this.init();
    }

    public SvendAndersenWatchFace(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public void init() {
        setBackgroundColor(Color.BLACK);

        View.inflate(getContext(), R.layout.view_svendandersen_face, this);

        PanelView panelView = findViewById(R.id.sa_panel_view);
        mClockPanel = findViewById(R.id.clock_panel);
        mClockPanel.setEditMode(mIsEditMode);
        mClockPanel.setDimMode(mIsDimMode);
        mPanelViewCity = findViewById(R.id.city_panel_view);
        mPanelViewCity.setEditMode(mIsEditMode);
        mPanelViewCity.setDimMode(mIsDimMode);

        boolean isNeedAnim = !(mIsDimMode || mIsEditMode);
        if (mIsDimMode) {
            panelView.setBgTexture(BitmapFactory.decodeResource(getResources(), R.drawable.w_bg_dim));
            mClockPanel.setBgTexture(BitmapFactory.decodeResource(getResources(), R.drawable.w_hour_24_dim));
            mPanelViewCity.setVisibility(GONE);
        } else {
            panelView.setBgTexture(BitmapFactory.decodeResource(getResources(), R.drawable.w_bg));
            mClockPanel.setBgTexture(BitmapFactory.decodeResource(getResources(), R.drawable.w_hour_24));
            mPanelViewCity.setVisibility(VISIBLE);
            mPanelViewCity.setBgTexture(BitmapFactory.decodeResource(getResources(), R.drawable.w_city));
            mPanelViewCity.setRotation(SCALE); //先把图片的零时区设置到中间位置
            setTimeZoneCity(isNeedAnim);
        }

        setTimeCity(isNeedAnim);

        //使用夏令时的国家：美国 欧盟 加拿大 欧盟国家、俄罗斯和瑞士，到夏令时时间拔快1小时
        //使用半时区的国家：伊朗东3.5时区、阿富汗东4.5时区、印度及尼泊尔东5.5时区、缅甸东6.5时区等

        // 城市或地区：格林尼治标准时(夏令时);在背景图上的序号
        // LONDON(伦敦)：GMT+00:00(+01:00);1
        // GENEVE(日内瓦)：GMT+01:00(+02:00);2
        // ISTANBUL(伊斯坦布尔)：GMT+03:00;3
        // MOSKWA(莫斯科)：GMT+03:00;4
        // DUBAI(迪拜)：GMT+04:00;5
        // KARACHI(卡拉奇)：GMT+05:00;6
        // NEW DELHI(新德里)：GMT+05:30;7
        // BANGKOK(曼谷)：GMT+07:00;8
        // HONG KONG(香港)：GMT+08:00;9
        // TOKYO(东京)：GMT+09:00;10
        // SYDNEY(悉尼)：GMT+10:00(+11:00);12
        // NOUMEA(努美阿)：GMT+11:00;11
        // AUCKLAND(奥克兰)：GMT+12:00(+13:00);13
        // MIDWAY(中途岛)：GMT-11:00;14
        // HAWAII(夏威夷)：GMT-10:00;15
        // ALASKA(阿拉斯加州)：GMT-09:00(-08:00);16
        // BEVERLY HILLS(比弗利山庄，在落杉矶)：GMT-08:00(-07:00);17
        // ASPEN(阿斯彭-凤凰城)：GMT-07:00;18
        // HOUSTON(休斯敦)：GMT-06:00;19
        // NEW YORK(纽约)：GMT-05:00(-04:00);20
        // BARBADOS(巴巴多斯)：GMT-04:00;21
        // SAO PAULO(圣保罗)：GMT-03:00;22
        // S.GEORGIA(南.乔治亚岛)：GMT-02:00;23
        // AZORES(亚速尔群岛)：GMT-01:00(+00:00);24
    }

    private void setTimeCity(boolean isNeedAnim) {
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        float degrees = (hourOfDay + minute / 60f) * SCALE;

        if (isNeedAnim) {
            post(() -> {
                ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, -degrees);
                valueAnimator.setDuration(FaceAnimView.ANIM_DURATION);
                valueAnimator.setInterpolator(new LinearInterpolator());
                valueAnimator.addUpdateListener(animation -> {
                    if (animation.getAnimatedValue() instanceof Float) {
                        mClockPanel.setRotateDegrees((Float) animation.getAnimatedValue());
                        mClockPanel.invalidate();
                    }
                });
                valueAnimator.start();
            });
        } else {
            mClockPanel.setRotateDegrees(-degrees);
        }
    }

    private void setTimeZoneCity(boolean isNeedAnim) {
        TimeZone timeZone = TimeZone.getDefault();
        String gmt = timeZone.getDisplayName(false, TimeZone.SHORT); //标准时
        String dnts = timeZone.getDisplayName(true, TimeZone.SHORT); //夏令时
        String zoneID = timeZone.getID();
        Log.d(TAG, "dn: " + timeZone.getDisplayName() + ", id: " + zoneID + ", dnfs: " + gmt + ", dnts: " + dnts);
        Log.d(TAG, "inDaylight: " + timeZone.inDaylightTime(new Date()) + ", useDaylight: " + timeZone.useDaylightTime());
        if (gmt.matches("^(GMT|UTC|gmt|utc)[-+][0-9]{2}:[0-9]{2}$")) {
            String sign = gmt.substring(3, 4);
            float offset = Float.parseFloat(gmt.substring(4, 6));
            offset += Float.parseFloat(gmt.substring(7, 9)) / 60;
            Log.d(TAG, "sign: " + sign + ", offset: " + offset);

            float rotateDegrees;
            if ("+".equals(sign)) {
                if (offset == 2) { //东二区没有城市显示，显示的是东一区的
                    offset--;
                }
                if (offset == 3 && !"Europe/Moscow".equalsIgnoreCase(zoneID)) {
                    offset--; //东三区有两个城市，Moscow正常显示在第四格的位置，其它的被前推到显示在第三格
                }
                rotateDegrees = -offset * SCALE;
            } else {
                rotateDegrees = offset * SCALE;
            }

            if (isNeedAnim) {
                post(() -> {
                    ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, rotateDegrees);
                    valueAnimator.setDuration(FaceAnimView.ANIM_DURATION);
                    valueAnimator.setInterpolator(new LinearInterpolator());
                    valueAnimator.addUpdateListener(animation -> {
                        if (animation.getAnimatedValue() instanceof Float) {
                            mPanelViewCity.setRotateDegrees((Float) animation.getAnimatedValue());
                            mPanelViewCity.invalidate();
                        }
                    });
                    valueAnimator.start();
                });
            } else {
                mPanelViewCity.setRotateDegrees(rotateDegrees);
            }
        } else {
            Log.e(TAG, "time zone selected error: " + gmt);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.registerTimeChangedReceiver();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.unregisterTimeChangedReceiver();
    }

    protected void registerTimeChangedReceiver() {
        if (mRegisteredChangedZoneReceiver) {
            return;
        }
        mRegisteredChangedZoneReceiver = true;

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        getContext().registerReceiver(mTimeChangedReceiver, filter);
    }

    protected void unregisterTimeChangedReceiver() {
        if (mRegisteredChangedZoneReceiver) {
            mRegisteredChangedZoneReceiver = false;
            getContext().unregisterReceiver(mTimeChangedReceiver);
        }
    }
}
