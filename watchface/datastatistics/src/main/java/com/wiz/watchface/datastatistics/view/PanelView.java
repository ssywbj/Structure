package com.wiz.watchface.datastatistics.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.structure.wallpaper.basic.WatchFaceView;
import com.structure.wallpaper.basic.manager.SportDataManager;
import com.structure.wallpaper.basic.utils.BitmapHelper;
import com.structure.wallpaper.basic.utils.DateUtil;
import com.structure.wallpaper.basic.utils.DimenUtil;
import com.wiz.watchface.datastatistics.R;

import java.util.Calendar;
import java.util.Locale;

public class PanelView extends WatchFaceView {
    protected final RectF mRectBg = new RectF();
    private Bitmap mBackground;

    private final Paint mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Rect mRectText = new Rect();

    protected final Path mPathArcRight = new Path();
    private final PathMeasure mPathMeasureRight = new PathMeasure();
    protected final Path mPathArcLeft = new Path();
    private final PathMeasure mPathMeasureLeft = new PathMeasure();
    private final Paint mPaintArc = new Paint(Paint.ANTI_ALIAS_FLAG);
    private LinearGradient mGradientSteps, mGradientBattery, mGradientAlarm, mGradientKcal;
    protected final Path mPathArcProgress = new Path();
    private float mMarginAngle, mSweepAngle;
    private int mArcWidth;

    private SportDataManager mSportDataManager;

    public PanelView(Context context) {
        super(context);
        this.init();
    }

    public PanelView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PanelView);
        Drawable drawable = typedArray.getDrawable(R.styleable.PanelView_backgroundTexture);
        typedArray.recycle();

        if (drawable != null) {
            mBackground = BitmapHelper.drawableToBitmap(drawable);
        }
        this.init();
    }

    public void init() {
        mPaintText.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
        mPaintText.setStrokeWidth(2);

        mMarginAngle = 26;
        mSweepAngle = (360 - mMarginAngle * 4) / 4f;
        mArcWidth = DimenUtil.dip2px(getContext(), 6);
        mPaintArc.setStrokeWidth(mArcWidth);
        mPaintArc.setStyle(Paint.Style.STROKE);
        mPaintArc.setStrokeCap(Paint.Cap.ROUND);

        mSportDataManager = new SportDataManager(mContext);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.calcDimens(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.calcDimens(w, h);
    }

    @Override
    public void onVisibilityChanged(boolean visible) {
        super.onVisibilityChanged(visible);
        if (visible) {
            registerBatteryChangeReceiver();
            mSportDataManager.queryStepAndCalories();
        } else {
            unregisterBatteryChangeReceiver();
        }
    }

    private void calcDimens(int width, int height) {
        RectF rectF = new RectF();
        rectF.left = getPaddingStart();
        rectF.top = getPaddingTop();
        rectF.right = width - getPaddingEnd();
        rectF.bottom = height - getPaddingBottom();

        final float radius = Math.min(rectF.width(), rectF.height()) / 2;
        mRectBg.setEmpty();
        mRectBg.left = rectF.centerX() - radius;
        mRectBg.top = rectF.centerY() - radius;
        mRectBg.right = rectF.centerX() + radius;
        mRectBg.bottom = rectF.centerY() + radius;

        final float gas = mArcWidth / 2f + DimenUtil.dip2px(getContext(), 5);
        final float radiusArc = radius - gas, offsetAngle = mMarginAngle / 2f;
        mPathArcRight.reset();
        mPathArcRight.addArc(-radiusArc, -radiusArc, radiusArc, radiusArc, -90 + offsetAngle, mSweepAngle);
        mPathMeasureRight.setPath(mPathArcRight, false);
        float sinStart = (float) Math.sin(Math.toRadians(offsetAngle));
        float cosStart = (float) Math.cos(Math.toRadians(offsetAngle));
        float sinEnd = (float) Math.sin(Math.toRadians(offsetAngle + mSweepAngle));
        float cosEnd = (float) Math.cos(Math.toRadians(offsetAngle + mSweepAngle));
        mGradientAlarm = new LinearGradient(radiusArc * sinStart, -radiusArc * cosStart
                , radiusArc * sinEnd, -radiusArc * cosEnd,
                new int[]{0xFF00FFFD, 0xFF2757FF}, null, Shader.TileMode.CLAMP);
        mGradientKcal = new LinearGradient(radiusArc * sinStart, -radiusArc * cosStart
                , radiusArc * sinEnd, -radiusArc * cosEnd,
                new int[]{0xFF00FFFD, 0xFFFF0071}, null, Shader.TileMode.CLAMP);

        mPathArcLeft.reset();
        mPathArcLeft.addArc(-radiusArc, -radiusArc, radiusArc, radiusArc, -offsetAngle, -mSweepAngle);//负数逆时针绘制
        mPathMeasureLeft.setPath(mPathArcLeft, false);
        final float angdeg = 90 - offsetAngle;
        sinStart = (float) Math.sin(Math.toRadians(angdeg));
        cosStart = (float) Math.cos(Math.toRadians(angdeg));
        sinEnd = (float) Math.sin(Math.toRadians(angdeg - mSweepAngle));
        cosEnd = (float) Math.cos(Math.toRadians(angdeg - mSweepAngle));
        mGradientSteps = new LinearGradient(radiusArc * sinStart, -radiusArc * cosStart
                , radiusArc * sinEnd, -radiusArc * cosEnd,
                new int[]{0xFF1495FF, 0xFF16FF75}, null, Shader.TileMode.CLAMP);
        mGradientBattery = new LinearGradient(radiusArc * sinStart, -radiusArc * cosStart
                , radiusArc * sinEnd, -radiusArc * cosEnd,
                new int[]{0xFFFFC602, 0xFF33FF54}, null, Shader.TileMode.CLAMP);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //this.recycleBitmap(mBackground);
    }

    @Override
    protected void onTimeTick() {
        mSportDataManager.queryStepAndCalories();

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBackground != null) {
            canvas.drawBitmap(mBackground, null, mRectBg, null);
        }

        this.paintArc(canvas);
        this.paintDate(canvas);
    }

    private void paintArc(Canvas canvas) {
        canvas.save();
        canvas.translate(mRectBg.centerX(), mRectBg.centerY());
        for (int i = 0; i < 4; i++) {
            canvas.save();
            canvas.rotate(90 * i);
            mPathArcProgress.reset();
            if (i == 0) {
                mPaintArc.setShader(mGradientSteps);
                mPathMeasureLeft.getSegment(0, mPathMeasureLeft.getLength() * mSportDataManager.getStepsProgress()
                        , mPathArcProgress, true);
            } else if (i == 1) {
                mPaintArc.setShader(mGradientBattery);
                mPathMeasureLeft.getSegment(0, mPathMeasureLeft.getLength() * (mBatteryLevel / 100f), mPathArcProgress, true);
            } else if (i == 2) {
                mPaintArc.setShader(mGradientAlarm);
                mPathMeasureRight.getSegment(0, 0, mPathArcProgress, true);
            } else {
                mPaintArc.setShader(mGradientKcal);
                mPathMeasureRight.getSegment(0, mPathMeasureRight.getLength() * mSportDataManager.getCaloriesProgress()
                        , mPathArcProgress, true);
            }
            mPaintArc.setAlpha((int) (255 * 0.6));
            if (i < 2) {
                canvas.drawPath(mPathArcLeft, mPaintArc);
            } else {
                canvas.drawPath(mPathArcRight, mPaintArc);
            }
            mPaintArc.setAlpha(255);
            canvas.drawPath(mPathArcProgress, mPaintArc);

            canvas.restore();
        }

        canvas.restore();
    }

    private void paintDate(Canvas canvas) {
        canvas.save();
        canvas.translate(mRectBg.centerX(), mRectBg.centerY() + getResources().getDimension(R.dimen.date_top_offset));

        final String week = DateUtil.getWeekText(mContext);
        mPaintText.setColor(Color.WHITE);
        Locale locale = Locale.getDefault();
        String language = locale.getLanguage();
        if ("zh".equalsIgnoreCase(language)) {
            mPaintText.setTextSize(getResources().getDimension(R.dimen.text_week));
        } else {
            final int weekIndex = DateUtil.getWeekIndex();
            if (weekIndex == 2) {
                mPaintText.setTextSize(getResources().getDimension(R.dimen.text_week_en_small));
            } else if (weekIndex == 3 || weekIndex == 5) {
                mPaintText.setTextSize(getResources().getDimension(R.dimen.text_week));
            } else {
                mPaintText.setTextSize(getResources().getDimension(R.dimen.text_week_en_big));
            }
        }
        mPaintText.getTextBounds(week, 0, week.length(), mRectText);
        int height = mRectText.height();
        canvas.drawText(week, -mRectText.centerX(), height, mPaintText);

        Calendar calendar = Calendar.getInstance();
        mPaintText.setTextSize(getResources().getDimension(R.dimen.text_month));
        int month = calendar.get(Calendar.MONTH) + 1;
        String date = calendar.get(Calendar.YEAR) + "." + (month / 10) + "" + (month % 10);
        mPaintText.getTextBounds(date, 0, date.length(), mRectText);
        final int marginTop = DimenUtil.dip2px(mContext, 14);
        height += (mRectText.height() + marginTop);
        canvas.drawText(date, -mRectText.centerX(), height, mPaintText);

        mPaintText.setColor(ContextCompat.getColor(mContext, R.color.date_color));
        mPaintText.setTextSize(getResources().getDimension(R.dimen.text_date));
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        date = (dayOfMonth / 10) + "" + (dayOfMonth % 10);
        mPaintText.getTextBounds(date, 0, date.length(), mRectText);
        height += (mRectText.height() + marginTop);
        canvas.drawText(date, -mRectText.centerX(), height, mPaintText);

        canvas.restore();
    }

    protected void recycleBitmap(Bitmap bitmap) {
        if (bitmap == null || bitmap.isRecycled()) {
            return;
        }
        bitmap.recycle();
    }

}
