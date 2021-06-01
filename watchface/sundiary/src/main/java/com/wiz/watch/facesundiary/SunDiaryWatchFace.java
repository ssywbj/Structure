package com.wiz.watch.facesundiary;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.structure.wallpaper.basic.utils.DimenUtil;
import com.structure.wallpaper.basic.view.FaceAnimView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class SunDiaryWatchFace extends FaceAnimView {
    private SunDiaryBitmapManager mBitmapManager;

    private final Paint mPaintTrack = new Paint();
    private float mEarthTrackRadius, mMoonTrackRadius;

    private static final int TIME_PRODUCT_METEOR = 3000;
    private static final int METEOR_TRACK_DEGREE = 40;
    private static final int METEOR_LENGTH = 60;
    private static final int METEOR_ALPHA_OFFSET = 0x44;
    private final Paint mPaintMeteor = new Paint();
    private ValueAnimator mMeteorAnimator;
    private int mMeteorAnimatorValue;
    private final Random mRandom = new Random();

    private final Rect mRect = new Rect();
    private Bitmap mBackground, mBitmapSun, mBitmapEarth, mBitmapMoon;

    private final Picture mPicture = new Picture();

    private int mTranslateDistance;
    private final List<int[]> mListMeteor = new ArrayList<>();

    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (getHandler() == null || getWidth() / 2f <= 0) {
                return;
            }

            int offsetX;
            int[] meteorParams;
            int meteors = mRandom.nextInt(3);
            if (meteors == 0) {
                meteors = 1;
            }
            mListMeteor.clear();
            for (int i = 0; i < meteors; i++) {
                meteorParams = new int[2];

                offsetX = mRandom.nextInt((int) (getWidth() / 2f));
                meteorParams[0] = ((mRandom.nextInt(2) == 0) ? offsetX : -offsetX);
                meteorParams[1] = METEOR_ALPHA_OFFSET + mRandom.nextInt(0xFF - METEOR_ALPHA_OFFSET);
                mListMeteor.add(meteorParams);
            }

            startMeteorAnim();

            getHandler().postDelayed(mRunnable, TIME_PRODUCT_METEOR);
        }
    };

    public SunDiaryWatchFace(Context context, boolean isEditMode, boolean isDimMode) {
        super(context, isEditMode, false, isDimMode);
        this.init();
    }

    public SunDiaryWatchFace(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    private void init() {
        mBitmapManager = new SunDiaryBitmapManager(mContext);
        mBackground = BitmapFactory.decodeResource(getResources(), R.drawable.bg);
        mBitmapSun = BitmapFactory.decodeResource(getResources(), R.drawable.sun);
        mBitmapEarth = BitmapFactory.decodeResource(getResources(), R.drawable.earth);
        mBitmapMoon = BitmapFactory.decodeResource(getResources(), R.drawable.moon);

        mEarthTrackRadius = DimenUtil.dip2px(mContext, 108);
        mMoonTrackRadius = DimenUtil.dip2px(mContext, 42);
        mPaintTrack.setAntiAlias(true);
        mPaintTrack.setStyle(Paint.Style.STROKE);
        mPaintTrack.setColor(0x4CFFFFFF);

        mPaintMeteor.setAntiAlias(true);
        mPaintMeteor.setStyle(Paint.Style.FILL);
        mPaintMeteor.setStrokeCap(Paint.Cap.ROUND);
        mPaintMeteor.setStrokeWidth(DimenUtil.dip2px(mContext, 3));

        setDefaultTime(8, 36, 55);
        needAppearAnimNumber();
        needAppearAnimPointer();
    }

    private void initMeteorAnim() {
        if (mMeteorAnimator == null) {
            mMeteorAnimator = ValueAnimator.ofInt(0, 0);//属性动画
            mMeteorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {//监听动画过程
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if (animation.getAnimatedValue() instanceof Integer) {
                        mMeteorAnimatorValue = (int) animation.getAnimatedValue();
                        invalidate();
                    }
                }
            });
            mMeteorAnimator.setDuration(1200);
            mMeteorAnimator.setInterpolator(new LinearInterpolator());
        }
    }

    private void startMeteorAnim() {
        mMeteorAnimator.setIntValues(0, mTranslateDistance);
        mMeteorAnimator.start();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRect.setEmpty();
        mRect.set(0, 0, w, h);

        mTranslateDistance = (int) Math.sqrt(w * w + h * h) + METEOR_LENGTH;
    }

    @Override
    public void onVisibilityChanged(boolean visible) {
        super.onVisibilityChanged(visible);
        if (!visible) {
            stopSecondPointerAnim();
            releaseAnim(mMeteorAnimator);
            if (getHandler() != null) {
                getHandler().removeCallbacks(mRunnable);
            }
        }
    }

    @Override
    protected void onAppearAnimFinished() {
        startSecondPointerAnim();
        if (getHandler() != null) {
            this.initMeteorAnim();
            getHandler().removeCallbacks(mRunnable);
            getHandler().postDelayed(mRunnable, 1000);
        }
    }

    public void destroy() {
        mBitmapManager.clear();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(ContextCompat.getColor(mContext, android.R.color.transparent), PorterDuff.Mode.CLEAR);
        canvas.drawBitmap(mBackground, null, mRect, null);
        //canvas.drawBitmap(mBitmapManager.get(R.drawable.bg), null, mRect, null);

        this.paintTracks(canvas, mMinuteRatio * 360, mSecondRatio * 360);
        if (!mIsDimMode) {
            this.paintMeteors(canvas);
        }

        this.paintTime(canvas);
        this.paintDate(canvas);
    }

    private void paintMeteors(Canvas canvas) {
        canvas.save();
        canvas.rotate(-METEOR_TRACK_DEGREE);

        for (int[] meteor : mListMeteor) {
            canvas.save();
            canvas.translate(meteor[0], -METEOR_LENGTH + mMeteorAnimatorValue);
            mPaintMeteor.setShader(new LinearGradient(0, 0, 0, METEOR_LENGTH, 0x00FFFFFF
                    , Color.argb(meteor[1], 0xFF, 0xFF, 0xFF), Shader.TileMode.CLAMP));
            canvas.drawLine(0, 0, 0, METEOR_LENGTH, mPaintMeteor);
            canvas.restore();
        }

        canvas.restore();
    }

    private void paintTracks(Canvas canvas, float minuteDegree, float secondDegree) {
        canvas.save();
        canvas.translate(getWidth() / 2f, getHeight() / 2f);
        //Bitmap bitmap = mBitmapManager.get(R.drawable.sun);
        Bitmap bitmap = mBitmapSun;
        canvas.drawBitmap(bitmap, -bitmap.getWidth() / 2f, -bitmap.getHeight() / 2f, null);

        canvas.save();
        canvas.rotate(minuteDegree);
        int startAngle = -68;
        canvas.drawArc(-mEarthTrackRadius, -mEarthTrackRadius, mEarthTrackRadius, mEarthTrackRadius
                , startAngle, 360 - (90 + startAngle) * 2, false, mPaintTrack);
        //bitmap = mBitmapManager.get(R.drawable.earth);
        bitmap = mBitmapEarth;
        canvas.drawBitmap(bitmap, -bitmap.getWidth() / 2f, -(mEarthTrackRadius + bitmap.getHeight() / 2f), null);
        canvas.drawCircle(0, -mEarthTrackRadius, mMoonTrackRadius, mPaintTrack);

        if (!mIsDimMode) {
            canvas.save();
            canvas.translate(0, -mEarthTrackRadius);
            canvas.rotate(-minuteDegree);//月球跟随地球旋转一定的角度后会变斜，需要反转回来
            canvas.rotate(secondDegree);
            //bitmap = mBitmapManager.get(R.drawable.moon);
            bitmap = mBitmapMoon;
            canvas.drawBitmap(bitmap, -bitmap.getWidth() / 2f, -(mMoonTrackRadius + bitmap.getHeight() / 2f), null);
            canvas.restore();
        }

        canvas.restore();

        canvas.restore();
    }

    private void paintTime(Canvas canvas) {
        canvas.save();
        canvas.translate(getWidth() / 2f, DimenUtil.dip2px(mContext, 16));
        int color = 0xFFFFFFFF;

        Bitmap bitmap = mBitmapManager.get(R.drawable.sign_colon, color);
        float leftOffset = bitmap.getWidth() / 2f, left = -leftOffset;
        canvas.drawBitmap(bitmap, left, -DimenUtil.dip2px(mContext, 6), null);

        bitmap = mBitmapManager.getTimeNumber(mHour % 10, color);
        left -= bitmap.getWidth();
        canvas.drawBitmap(bitmap, left, 0, null);
        bitmap = mBitmapManager.getTimeNumber(mHour / 10, color);
        left -= bitmap.getWidth();
        canvas.drawBitmap(bitmap, left, 0, null);

        left = leftOffset;
        bitmap = mBitmapManager.getTimeNumber(mMinute / 10, color);
        canvas.drawBitmap(bitmap, left, 0, null);
        left += bitmap.getWidth();
        bitmap = mBitmapManager.getTimeNumber(mMinute % 10, color);
        canvas.drawBitmap(bitmap, left, 0, null);
        canvas.restore();
    }

    private void paintDate(Canvas canvas) {
        float left = 0;
        int color = 0xFFFFFFFF;

        Calendar calendar = Calendar.getInstance();
        Canvas canvasTmp = mPicture.beginRecording(0, 0);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        Bitmap bitmap = mBitmapManager.getDateNumber(dayOfMonth / 10, color);
        canvasTmp.drawBitmap(bitmap, left, 0, null);
        left += bitmap.getWidth();
        bitmap = mBitmapManager.getDateNumber(dayOfMonth % 10, color);
        canvasTmp.drawBitmap(bitmap, left, 0, null);
        left += bitmap.getWidth();

        left += (DimenUtil.dip2px(mContext, 4));

        switch (calendar.get(Calendar.MONTH)) {
            case 0://1月：January，Jan
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_j, color);
                canvasTmp.drawBitmap(bitmap, left, 0, null);
                left += bitmap.getWidth();
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_a, color);
                canvasTmp.drawBitmap(bitmap, left, 0, null);
                left += bitmap.getWidth();
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_n, color);
                canvasTmp.drawBitmap(bitmap, left, 0, null);
                break;
            case 1://2月：February
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_f, color);
                canvasTmp.drawBitmap(bitmap, left, 0, null);
                left += bitmap.getWidth();
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_e, color);
                canvasTmp.drawBitmap(bitmap, left, 0, null);
                left += bitmap.getWidth();
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_b, color);
                canvasTmp.drawBitmap(bitmap, left, 0, null);
                break;
            case 2://3月：March
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_m, color);
                canvasTmp.drawBitmap(bitmap, left, 0, null);
                left += bitmap.getWidth();
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_a, color);
                canvasTmp.drawBitmap(bitmap, left, 0, null);
                left += bitmap.getWidth();
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_r, color);
                canvasTmp.drawBitmap(bitmap, left, 0, null);
                break;
            case 3://4月：April
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_a, color);
                canvasTmp.drawBitmap(bitmap, left, 0, null);
                left += bitmap.getWidth();
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_p, color);
                canvasTmp.drawBitmap(bitmap, left, 0, null);
                left += bitmap.getWidth();
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_r, color);
                canvasTmp.drawBitmap(bitmap, left, 0, null);
                break;
            case 4://5月：May
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_m, color);
                canvasTmp.drawBitmap(bitmap, left, 0, null);
                left += bitmap.getWidth();
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_a, color);
                canvasTmp.drawBitmap(bitmap, left, 0, null);
                left += bitmap.getWidth();
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_y, color);
                canvasTmp.drawBitmap(bitmap, left, 0, null);
                break;
            case 5://6月：June
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_j, color);
                canvasTmp.drawBitmap(bitmap, left, 0, null);
                left += bitmap.getWidth();
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_u, color);
                canvasTmp.drawBitmap(bitmap, left, 0, null);
                left += bitmap.getWidth();
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_n, color);
                canvasTmp.drawBitmap(bitmap, left, 0, null);
                break;
            case 6://7月：July
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_j, color);
                canvasTmp.drawBitmap(bitmap, left, 0, null);
                left += bitmap.getWidth();
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_u, color);
                canvasTmp.drawBitmap(bitmap, left, 0, null);
                left += bitmap.getWidth();
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_l, color);
                canvasTmp.drawBitmap(bitmap, left, 0, null);
                break;
            case 7://8月：August
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_a, color);
                canvasTmp.drawBitmap(bitmap, left, 0, null);
                left += bitmap.getWidth();
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_u, color);
                canvasTmp.drawBitmap(bitmap, left, 0, null);
                left += bitmap.getWidth();
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_g, color);
                canvasTmp.drawBitmap(bitmap, left, 0, null);
                break;
            case 8://9月：September
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_s, color);
                canvasTmp.drawBitmap(bitmap, left, 0, null);
                left += bitmap.getWidth();
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_e, color);
                canvasTmp.drawBitmap(bitmap, left, 0, null);
                left += bitmap.getWidth();
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_p, color);
                canvasTmp.drawBitmap(bitmap, left, 0, null);
                left += bitmap.getWidth();
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_t, color);
                canvasTmp.drawBitmap(bitmap, left, 0, null);
                break;
            case 9://10月：October
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_o, color);
                canvasTmp.drawBitmap(bitmap, left, 0, null);
                left += bitmap.getWidth();
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_c, color);
                canvasTmp.drawBitmap(bitmap, left, 0, null);
                left += bitmap.getWidth();
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_t, color);
                canvasTmp.drawBitmap(bitmap, left, 0, null);
                break;
            case 10://11月：November
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_n, color);
                canvasTmp.drawBitmap(bitmap, left, 0, null);
                left += bitmap.getWidth();
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_o, color);
                canvasTmp.drawBitmap(bitmap, left, 0, null);
                left += bitmap.getWidth();
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_v, color);
                canvasTmp.drawBitmap(bitmap, left, 0, null);
                break;
            default://12月：December
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_d, color);
                canvasTmp.drawBitmap(bitmap, left, 0, null);
                left += bitmap.getWidth();
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_e, color);
                canvasTmp.drawBitmap(bitmap, left, 0, null);
                left += bitmap.getWidth();
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_c, color);
                canvasTmp.drawBitmap(bitmap, left, 0, null);
                break;
        }
        left += bitmap.getWidth();

        bitmap = mBitmapManager.get(R.drawable.sign_point, color);
        canvasTmp.drawBitmap(bitmap, left, 0, null);
        left += (bitmap.getWidth() + DimenUtil.dip2px(mContext, 5));

        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case 2://MON
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_m, color);
                canvasTmp.drawBitmap(bitmap, left, 0, null);
                left += bitmap.getWidth();
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_o, color);
                canvasTmp.drawBitmap(bitmap, left, 0, null);
                left += bitmap.getWidth();
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_n, color);
                canvasTmp.drawBitmap(bitmap, left, 0, null);
                break;
            case 3://TUES
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_t, color);
                canvasTmp.drawBitmap(bitmap, left, 0, null);
                left += bitmap.getWidth();
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_u, color);
                canvasTmp.drawBitmap(bitmap, left, 0, null);
                left += bitmap.getWidth();
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_e, color);
                canvasTmp.drawBitmap(bitmap, left, 0, null);
                left += bitmap.getWidth();
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_s, color);
                canvasTmp.drawBitmap(bitmap, left, 0, null);
                break;
            case 4://WED
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_w, color);
                canvasTmp.drawBitmap(bitmap, left, 0, null);
                left += bitmap.getWidth();
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_e, color);
                canvasTmp.drawBitmap(bitmap, left, 0, null);
                left += bitmap.getWidth();
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_d, color);
                canvasTmp.drawBitmap(bitmap, left, 0, null);
                break;
            case 5://THUR
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_t, color);
                canvasTmp.drawBitmap(bitmap, left, 0, null);
                left += bitmap.getWidth();
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_h, color);
                canvasTmp.drawBitmap(bitmap, left, 0, null);
                left += bitmap.getWidth();
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_u, color);
                canvasTmp.drawBitmap(bitmap, left, 0, null);
                left += bitmap.getWidth();
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_r, color);
                canvasTmp.drawBitmap(bitmap, left, 0, null);
                break;
            case 6://FRI
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_f, color);
                canvasTmp.drawBitmap(bitmap, left, 0, null);
                left += bitmap.getWidth();
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_r, color);
                canvasTmp.drawBitmap(bitmap, left, 0, null);
                left += bitmap.getWidth();
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_i, color);
                canvasTmp.drawBitmap(bitmap, left, 0, null);
                break;
            case 7://SAT
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_s, color);
                canvasTmp.drawBitmap(bitmap, left, 0, null);
                left += bitmap.getWidth();
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_a, color);
                canvasTmp.drawBitmap(bitmap, left, 0, null);
                left += bitmap.getWidth();
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_t, color);
                canvasTmp.drawBitmap(bitmap, left, 0, null);
                break;
            default://SUN
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_s, color);
                canvasTmp.drawBitmap(bitmap, left, 0, null);
                left += bitmap.getWidth();
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_u, color);
                canvasTmp.drawBitmap(bitmap, left, 0, null);
                left += bitmap.getWidth();
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_n, color);
                canvasTmp.drawBitmap(bitmap, left, 0, null);
        }
        left += bitmap.getWidth();
        mPicture.endRecording();

        canvas.save();
        canvas.translate((getWidth() - left) / 2, getHeight()
                - mContext.getResources().getDimension(R.dimen.number_height_small) - DimenUtil.dip2px(mContext, 15));
        canvas.drawPicture(mPicture);
        canvas.restore();
    }

}
