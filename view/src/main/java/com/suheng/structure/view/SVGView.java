package com.suheng.structure.view;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.Picture;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.animation.LinearInterpolator;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class SVGView extends ClipView {
    private static final String TAG = SVGView.class.getSimpleName();
    private final PointF mPointCenter = new PointF();
    private final Paint mPaint = new Paint();
    private final Rect mRect = new Rect();
    private BitmapManager mBitmapManager;

    private final Path mPath = new Path();
    private float mRadius;

    protected PaintFlagsDrawFilter mPaintFlagsDrawFilter;

    public SVGView(Context context) {
        super(context);
        this.init();
    }

    public SVGView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public SVGView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    private void init() {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        mPointCenter.x = 1.0f * displayMetrics.widthPixels / 2;
        mPointCenter.y = 1.0f * displayMetrics.heightPixels / 2;
        mRadius = Math.min(mPointCenter.x, mPointCenter.y);

        mPaint.setAntiAlias(true);

        mBitmapManager = new BitmapManager(getContext());
        mPaintFlagsDrawFilter = new PaintFlagsDrawFilter(0, Paint.FILTER_BITMAP_FLAG);
        mBitmapManager2 = new BitmapManager2(getContext());

        mPaintRect.setStyle(Paint.Style.FILL);

        this.paintPicture();
        Log.d(TAG, "-----------init-------");

        mBitmapEarth = BitmapFactory.decodeResource(getResources(), R.drawable.earth);

        mRectClip.set(10, 140, 160, 290);
        mRectClip2.set(mRectClip.right + 10, 140, mRectClip.right + mRectClip.width(), 290);

        //setLayerType(View.LAYER_TYPE_SOFTWARE, null);//关闭硬件加速
        this.initSecondPointerAnim();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.d(TAG, "------- onAttachedToWindow --------");
        //this.updateTimeBySecond();

        long delayMillis = UPDATE_RATE_MS - (System.currentTimeMillis() % UPDATE_RATE_MS);
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, "------- mTimer.schedule --------" + Thread.currentThread().getName());
                //invalidate();
                //invalidate(mRectClip);
                postInvalidate(mRectClip.left, mRectClip.top, mRectClip.right, mRectClip.bottom);
            }
        }, delayMillis, 1000);

        /*mTimer2.schedule(new TimerTask() {
            @Override
            public void run() {
                postInvalidate(mRectClip2.left, mRectClip2.top, mRectClip2.right, mRectClip2.bottom);
            }
        }, 0, 1000);*/

        this.startSecondPointerAnim();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mBitmapManager2.clear();
        removeCallbacks(mRunnable);
        Log.d(TAG, "------- onDetachedFromWindow --------");

        mTimer.cancel();
        mTimer2.cancel();

        this.releaseAnim(mSecondAnimator);
    }

    private static final long UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1);

    private void updateTimeBySecond() {
        if (getHandler() == null) { //在onAttachedToWindow()中调用getHandler()以确保其不为空
            return;
        }
        long delayMillis = UPDATE_RATE_MS - (System.currentTimeMillis() % UPDATE_RATE_MS);
        getHandler().removeCallbacks(mRunnable);
        getHandler().postDelayed(mRunnable, delayMillis);
    }

    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "------- updateTimeBySecond --------" + Thread.currentThread().getName());
            invalidate();
            //postInvalidate(mRectClip.left, mRectClip.top, mRectClip.right, mRectClip.bottom);

            updateTimeBySecond();
        }
    };

    private final Timer mTimer = new Timer();
    private final Timer mTimer2 = new Timer();

    private Bitmap mBitmapEarth;
    RectF dst = new RectF();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(ContextCompat.getColor(getContext(), android.R.color.holo_red_dark));

        Bitmap bitmap = BitmapHelper.toGray(mBitmapEarth);
        canvas.drawBitmap(bitmap, 0, 10, null);
        canvas.drawBitmap(mBitmapEarth, bitmap.getWidth() + 20, 10, null);

        //this.paintRect(canvas);
        //this.paintScaleBitmap2(canvas);
        //this.paintScaleBitmap(canvas);
        //this.paintPath(canvas);
        //this.paintScalesText(canvas);
        //this.paintScalesBitmapMethod1(canvas);
        //this.paintScalesBitmapMethod2(canvas);

        this.paintPicture(canvas);

        canvas.save();
        canvas.clipRect(mRectClip);
        canvas.drawColor(Color.WHITE);
        canvas.translate(mRectClip.centerX() - mTimePicture.mRect.centerX()
                , mRectClip.centerY() - mTimePicture.mRect.centerY());
        canvas.drawPicture(mTimePicture.getPicture());
        canvas.restore();

        /*canvas.save();
        canvas.clipRect(mRectClip2);
        canvas.drawColor(Color.WHITE);
        canvas.translate(mRectClip2.centerX() - mTimePicture.mRect.centerX()
                , mRectClip2.centerY() - mTimePicture.mRect.centerY());
        canvas.drawPicture(mTimePicture.getPicture());
        canvas.restore();*/
    }

    private void paintScalesBitmapMethod2(Canvas canvas) {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.RED);
        float radius = mRadius - 8;
        canvas.drawCircle(mPointCenter.x, mPointCenter.y, radius, mPaint);

        final int scales = 26 * 3;
        float scaleDegree = 1.0f * 360 / scales;
        Bitmap bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_a, android.R.color.white);
        float bitmapRadius = radius - 1.0f * bitmap.getHeight() / 2;
        double sinValue, cosValue, radians;
        for (int index = 0; index < scales; index++) {
            radians = Math.toRadians(index * scaleDegree);//弧度值，Math.toRadians：度换算成弧度
            sinValue = Math.sin(radians);
            cosValue = Math.cos(radians);
            canvas.drawLine(mPointCenter.x, mPointCenter.y, (float) (mPointCenter.x + radius * sinValue)
                    , (float) (mPointCenter.y - radius * cosValue), mPaint);
            bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_a + index % 26, android.R.color.white);
            canvas.drawBitmap(bitmap, (float) (mPointCenter.x + bitmapRadius * sinValue) - 1.0f * bitmap.getWidth() / 2
                    , (float) (mPointCenter.y - bitmapRadius * cosValue) - 1.0f * bitmap.getHeight() / 2, null);
        }
    }

    private void paintScalesBitmapMethod1(Canvas canvas) {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.RED);
        float radius = mRadius - 8;
        canvas.drawCircle(mPointCenter.x, mPointCenter.y, radius, mPaint);

        final int scales = 98;
        float scaleDegree = 1.0f * 360 / scales, rotateDegrees;
        float stopY = mPointCenter.y - radius;
        Bitmap bitmap;
        for (int index = 0; index < scales; index++) {
            rotateDegrees = index * scaleDegree;

            canvas.save();
            canvas.rotate(rotateDegrees, mPointCenter.x, mPointCenter.y);//画布旋转后，在低分辨率手机上bitmap会有些锯齿现象，但能接受
            //canvas.drawLine(mPointCenter.x, mPointCenter.y, mPointCenter.x, stopY, mPaint);

            if (rotateDegrees > 90 && rotateDegrees < 270) {
                bitmap = mBitmapManager.get(R.drawable.reverse_alphabet_uppercase_a + index % 26,
                        R.color.colorPrimary);
            } else {
                /*//旋转回相应的角度，目的是摆正图片。但再次旋转后，在低分辨率手机上锯齿现象会严重加剧，不能接受
                bitmap = mBitmapManager.getRotate(R.drawable.alphabet_uppercase_a + index % 26,
                        R.color.alphabet_uppercase, -rotateDegrees);*/
                bitmap = mBitmapManager.get(R.drawable.alphabet_uppercase_a + index % 26,
                        R.color.alphabet_uppercase);
            }

            canvas.drawBitmap(bitmap, mPointCenter.x - 1.0f * bitmap.getWidth() / 2, stopY, null);
            canvas.restore();
        }
    }

    private void paintScalesText(Canvas canvas) {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.RED);
        float radius = mRadius - 26;
        canvas.drawCircle(mPointCenter.x, mPointCenter.y, radius, mPaint);

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(20f);
        final int scales = 26;
        float scaleDegree = 1.0f * 360 / scales;
        float stopY = mPointCenter.y - radius;
        String text;
        for (int index = 0; index < scales; index++) {
            canvas.save();
            canvas.rotate(index * scaleDegree, mPointCenter.x, mPointCenter.y);
            canvas.drawLine(mPointCenter.x, mPointCenter.y, mPointCenter.x, stopY, mPaint);
            text = String.valueOf(index);
            mRect.setEmpty();
            mPaint.getTextBounds(text, 0, text.length(), mRect);
            canvas.drawText(index + "", mPointCenter.x - 1.0f * mRect.width() / 2, stopY + mRect.height(), mPaint);
            canvas.restore();
        }
    }

    private void paintScaleBitmap(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.RED);
        //原图
        float left = 10, top = 35;
        Bitmap bitmap = mBitmapManager.get(R.drawable.number_5_big);
        canvas.drawBitmap(bitmap, left, top, null);
        top += bitmap.getHeight();

        float lineStartY = (top - 1.0f * bitmap.getHeight() / 2);
        canvas.drawLine(0, lineStartY, getWidth(), lineStartY, mPaint);

        left += (bitmap.getWidth() + 16);
        bitmap = mBitmapManager.get(R.drawable.number_5);
        canvas.drawBitmap(bitmap, left, lineStartY - 1.0f * bitmap.getHeight() / 2, null);

        left += (bitmap.getWidth() + 16);
        bitmap = mBitmapManager.get(R.drawable.number_5_small);
        canvas.drawBitmap(bitmap, left, lineStartY - 1.0f * bitmap.getHeight() / 2, null);

        //#####################################缩放测试：Start#####################################
        //小图放大
        left = 4;
        top += 8;
        //bitmap = mBitmapManager.getScale(R.drawable.number_5_small, 4);//放大4倍
        bitmap = get(getContext(), R.drawable.number_5_small, 4);
        canvas.drawBitmap(bitmap, left, top, null);
        top += bitmap.getHeight();

        lineStartY = (top - 1.0f * bitmap.getHeight() / 2);
        canvas.drawLine(0, lineStartY, getWidth(), lineStartY, mPaint);

        left += (bitmap.getWidth() + 16);
        //bitmap = mBitmapManager.getScale(R.drawable.number_5_small, 2);//放大2倍
        bitmap = get(getContext(), R.drawable.number_5_small, 2);
        canvas.drawBitmap(bitmap, left, lineStartY - 1.0f * bitmap.getHeight() / 2, null);

        left += (bitmap.getWidth() + 16);
        //bitmap = mBitmapManager.getScale(R.drawable.number_5_small, 1);//放大1倍（原图）
        bitmap = get(getContext(), R.drawable.number_5_small, 1);//放大1倍（原图）
        canvas.drawBitmap(bitmap, left, lineStartY - 1.0f * bitmap.getHeight() / 2, null);

        //大图缩小
        left = 4;
        top += 8;
        //bitmap = mBitmapManager.getScale(R.drawable.number_5_big, 1);//缩小1倍（原图）
        bitmap = get(getContext(), R.drawable.number_5_big, 1);
        canvas.drawBitmap(bitmap, left, top, null);
        top += bitmap.getHeight();

        lineStartY = (top - 1.0f * bitmap.getHeight() / 2);
        canvas.drawLine(0, lineStartY, getWidth(), lineStartY, mPaint);

        left += (bitmap.getWidth() + 16);
        //bitmap = mBitmapManager.getScale(R.drawable.number_5_big, 1.0f / 2);//缩小2倍
        bitmap = get(getContext(), R.drawable.number_5_big, 1.0f / 2);
        canvas.drawBitmap(bitmap, left, lineStartY - 1.0f * bitmap.getHeight() / 2, null);

        left += (bitmap.getWidth() + 16);
        //bitmap = mBitmapManager.getScale(R.drawable.number_5_big, 1.0f / 4);//缩小4倍
        bitmap = get(getContext(), R.drawable.number_5_big, 1.0f / 4);
        canvas.drawBitmap(bitmap, left, lineStartY - 1.0f * bitmap.getHeight() / 2, null);
        //###########缩放测试：end，结论：单个SVG不管是缩小还是放大，都有锯齿，其中放大的更明显###############
    }

    private void paintPath(Canvas canvas) {
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);

        mPath.reset();
        mPath.addCircle(mPointCenter.x, mPointCenter.y, mRadius / 1.5f, Path.Direction.CW);//顺时针生成
        canvas.drawPath(mPath, mPaint);
        mPaint.setTextSize(30f);
        mPaint.setStyle(Paint.Style.FILL);
        //hOffset，vOffset：文案在水平、垂直方向相对Path的偏移量，0表示不偏移贴着Path，正数表示向上偏移穿过Path，负数表示向下偏移远离Path
        //顺时针生成，文案在Path外部被绘制
        canvas.drawTextOnPath("顺时针生成，文案在Path外部被绘制", mPath, -8, -8, mPaint);

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.GREEN);
        mPath.reset();
        mPath.addCircle(mPointCenter.x, mPointCenter.y, mRadius / 1.7f, Path.Direction.CCW);//逆时针生成
        canvas.drawPath(mPath, mPaint);
        mPaint.setTextSize(30f);
        mPaint.setStyle(Paint.Style.FILL);
        //逆时针生成，文案在Path内部被绘制
        canvas.drawTextOnPath("逆时针生成，文案在Path内部被绘制", mPath, 6, 6, mPaint);

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.BLUE);
        mPath.reset();
        mPath.moveTo(mPointCenter.x + 30, mPointCenter.y + 4);//从右到左画线
        mPath.lineTo(mPointCenter.x - 30, mPointCenter.y + 4);
        canvas.drawPath(mPath, mPaint);
        mPaint.setTextSize(20);
        mPaint.setStyle(Paint.Style.FILL);
        //从右到左画线，文案在Path下方被绘制，绘制方向也是从右到左
        canvas.drawTextOnPath("线下方", mPath, 0, 0, mPaint);

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.CYAN);
        mPath.reset();
        mPath.moveTo(mPointCenter.x - 30, mPointCenter.y - 4);//从左到右画线
        mPath.lineTo(mPointCenter.x + 30, mPointCenter.y - 4);
        canvas.drawPath(mPath, mPaint);
        mPaint.setTextSize(20);
        mPaint.setStyle(Paint.Style.FILL);
        //从左到右画线，文案在Path上方被绘制，绘制方向也是从左到右
        canvas.drawTextOnPath("线上方", mPath, -4, -4, mPaint);
    }

    public static Bitmap get(Context context, @DrawableRes int resId/*, int color*/, float scale) {
        Drawable drawable = ContextCompat.getDrawable(context, resId);
        if (drawable == null) {
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof VectorDrawable || drawable instanceof VectorDrawableCompat) {
            //drawable.setTint(ContextCompat.getColor(context, color));
            int intrinsicWidth = drawable.getIntrinsicWidth();
            int intrinsicHeight = drawable.getIntrinsicHeight();
            Bitmap bitmap = Bitmap.createBitmap((int) (intrinsicWidth * scale), (int) (intrinsicHeight * scale), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            //canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.FILTER_BITMAP_FLAG));
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);

            return bitmap;
        } else {
            throw new IllegalArgumentException("unsupported drawable type");
        }
    }

    private BitmapManager2 mBitmapManager2;

    private void paintScaleBitmap2(Canvas canvas) {
        //canvas.setDrawFilter(mPaintFlagsDrawFilter);

        //---------------------------方法１-------------------------
        //原图
        float left = 10, top = 20;
        Bitmap bitmap = mBitmapManager.get(R.drawable.test_pic);
        //Bitmap bitmap = mBitmapManager2.get(R.drawable.earth);
        canvas.drawBitmap(bitmap, left, top, null);
        left += bitmap.getWidth();

        canvas.save();
        canvas.scale(0.8f, 0.8f);
        canvas.drawBitmap(bitmap, left + 20, top + 10, null);
        canvas.restore();

        canvas.save();
        canvas.scale(2f, 2f);
        canvas.drawBitmap(bitmap, left, top - 16, null);
        canvas.restore();

        //-------------------------方法２---------------------------
        //原图
        left += 3.8f * bitmap.getWidth();
        canvas.drawBitmap(bitmap, left, top, null);
        left += (bitmap.getWidth() + 10);

        canvas.save();
        bitmap = BitmapHelper.scale(bitmap, 0.8f);
        canvas.drawBitmap(bitmap, left, top + 10, null);
        left += (bitmap.getWidth() + 10);
        canvas.restore();

        canvas.save();
        bitmap = BitmapHelper.scale(bitmap, 2.4f);
        canvas.drawBitmap(bitmap, left, top - 16, null);
        canvas.restore();

        //-------------------------方法3---------------------------
        //原图
        left = 10;
        top = 160;
        bitmap = mBitmapManager2.get(R.drawable.test_pic);
        canvas.drawBitmap(bitmap, left, top, null);
        left += (bitmap.getWidth() + 10);

        //原图
        canvas.save();
        bitmap = mBitmapManager2.get(R.drawable.test_pic_small);
        //bitmap = BitmapManager.rotate(bitmap, 30);
        canvas.drawBitmap(bitmap, left, top + 10, null);
        left += (bitmap.getWidth() + 10);
        canvas.restore();

        //原图
        canvas.save();
        bitmap = mBitmapManager2.get(R.drawable.test_pic_big);
        canvas.drawBitmap(bitmap, left, top - 16, null);
        left += (bitmap.getWidth() + 10);
        canvas.restore();

        //-------------------------方法4---------------------------
        //原图
        top = 160;
        bitmap = mBitmapManager2.get(R.drawable.test_pic);
        //bitmap = mBitmapManager2.getRotate(R.drawable.test_pic, 45);
        canvas.drawBitmap(bitmap, left, top, null);
        left += (bitmap.getWidth() + 10);

        canvas.save();
        //bitmap = mBitmapManager2.get(R.drawable.earth, 0.8f);
        bitmap = mBitmapManager2.get(R.drawable.test_pic, 0.8f);
        canvas.drawBitmap(bitmap, left, top + 10, null);
        left += (bitmap.getWidth() + 10);
        canvas.restore();

        canvas.save();
        //bitmap = mBitmapManager2.get(R.drawable.earth, 2.0f);
        bitmap = mBitmapManager2.get(R.drawable.test_pic, 2.0f);
        //bitmap = BitmapHelper.scale(mBitmapManager2.get(R.drawable.test_pic), 2);
        canvas.drawBitmap(bitmap, left, top - 16, null);
        canvas.restore();
    }

    private final RectF mRectF = new RectF();
    private final RectF mRectFDst = new RectF();
    private final Paint mPaintRect = new Paint(Paint.ANTI_ALIAS_FLAG);

    private void paintRect(Canvas canvas) {
        canvas.setDrawFilter(mPaintFlagsDrawFilter);

        mRectF.setEmpty();
        Bitmap bitmap = mBitmapManager2.get(R.drawable.number_5_big);
        int left = 100;
        int top = 10;
        mRectF.set(left, top, bitmap.getWidth() + left, bitmap.getHeight() + top);
        mPaintRect.setColor(Color.BLUE);
        canvas.drawRect(mRectF, mPaintRect);
        canvas.drawBitmap(bitmap, null, mRectF, null);

        Matrix matrix = new Matrix();
        mRectFDst.setEmpty();
        int degrees = 90;
        matrix.setRotate(degrees, mRectF.centerX(), mRectF.centerY());//先旋转
        //matrix.postTranslate(100, 0);//后平移
        matrix.postScale(1.3f, 1.3f, mRectF.centerX(), mRectF.centerY());//后放大
        matrix.mapRect(mRectFDst, mRectF);
        mPaintRect.setColor(Color.BLACK);
        canvas.drawRect(mRectFDst, mPaintRect);
        canvas.drawBitmap(BitmapHelper.rotate(bitmap, degrees), null, mRectFDst, null);

        top += 2 * mRectF.width();
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.test_pic);
        bitmap = BitmapHelper.drawableToBitmap2(drawable, 2.4f, 540);
        canvas.drawBitmap(bitmap, left, top, null);
    }

    private final Picture mPicture = new Picture();

    private void paintPicture() {
        Canvas canvas = mPicture.beginRecording(100, 100);
        Bitmap bitmap = mBitmapManager2.get(R.drawable.number_5_big);
        canvas.drawBitmap(bitmap, 0, 0, null);
        mPicture.endRecording();
    }

    private final TimeView mTimePicture = new TimeView();

    private static class TimeView {
        private final Picture mTmpPicture = new Picture();
        private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Rect mRect = new Rect();

        private TimeView() {
            mPaint.setColor(Color.WHITE);
            mPaint.setTextSize(100);
            mPaint.setTypeface(Typeface.DEFAULT_BOLD);
        }

        public Picture getPicture() {
            Canvas canvas = mTmpPicture.beginRecording(0, 0);
            String text = String.valueOf(Calendar.getInstance().get(Calendar.SECOND));
            mPaint.getTextBounds(text, 0, text.length(), mRect);
            canvas.clipRect(mRect);
            canvas.drawColor(Color.BLUE);
            canvas.drawText(text, 0, 0, mPaint);
            mTmpPicture.endRecording();

            return mTmpPicture;
        }

        public Rect getRect() {
            return mRect;
        }
    }

    private void paintPicture(Canvas canvas) {
        Picture tmpPicture = mTimePicture.getPicture();
        Rect rect = mTimePicture.getRect();
        dst.set(getWidth() / 2f - rect.width() / 2f, 100, getWidth() / 2f + rect.width() / 2f, 0);
        canvas.drawPicture(tmpPicture, dst);
        Log.d(TAG, "TmpPicture, w-h: " + tmpPicture.getWidth() + ", " + tmpPicture.getHeight()
                + ", --: " + rect.width() + ", " + rect.height() + ", --: " + +getWidth() + ", " + getHeight());
    }

    private final Rect mRectClip = new Rect();
    private final Rect mRectClip2 = new Rect();

    protected ValueAnimator mSecondAnimator;
    protected float mSecondAnimatorValue;
    private long mCurrentTimeMillis;

    private void initSecondPointerAnim() {
        mSecondAnimator = ValueAnimator.ofFloat(0, 0);//属性动画
        mSecondAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {//监听动画过程
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (System.currentTimeMillis() - mCurrentTimeMillis > 40) { //控制属性动画的刷新频率
                    mCurrentTimeMillis = System.currentTimeMillis();

                    if (animation.getAnimatedValue() instanceof Float) {
                        mSecondAnimatorValue = (Float) animation.getAnimatedValue();
                        /*Log.d(TAG, "pointer anim: " + mSecondAnimatorValue + ", " + animation.getCurrentPlayTime()
                                + ", " + animation.getAnimatedFraction());*/
                    }
                }
            }
        });

        mSecondAnimator.setDuration(TimeUnit.MINUTES.toMillis(1L));
        mSecondAnimator.setInterpolator(new LinearInterpolator());
        mSecondAnimator.setRepeatCount(ValueAnimator.INFINITE);
        /*mSecondAnimator.setEvaluator(new TypeEvaluator<Float>() {
            @Override
            public Float evaluate(float fraction, Float startValue, Float endValue) {
                //Log.i(TAG, "anim evaluate: " + fraction + ", " + startValue + ", " + endValue);
                return null;
            }
        });*/
    }

    protected void startSecondPointerAnim() {
        if (mSecondAnimator == null) {
            this.initSecondPointerAnim();
        }
        if (mSecondAnimator.isRunning()) {
            mSecondAnimator.cancel();
        }
        Calendar calendar = Calendar.getInstance();
        float offsetValue = (calendar.get(Calendar.SECOND) + calendar.get(Calendar.MILLISECOND) / 1000f) / 60f;
        mSecondAnimator.setFloatValues(offsetValue, 1 + offsetValue);
        //mSecondAnimator.setCurrentPlayTime(2000);
        mSecondAnimator.start();

        PropertyValuesHolder hourVH = PropertyValuesHolder.ofFloat("hour", 1, 10);
        PropertyValuesHolder minuteVH = PropertyValuesHolder.ofFloat("minute", 19, 41);
        PropertyValuesHolder secondVH = PropertyValuesHolder.ofFloat("second", 36, 10);
        ValueAnimator valueAnimator = ValueAnimator.ofPropertyValuesHolder(hourVH, minuteVH, secondVH);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setDuration(TimeUnit.SECONDS.toMillis(10L));
        valueAnimator.start();
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (animation.getAnimatedValue() instanceof PropertyValuesHolder[]) {
                    Log.i(TAG, "animation: " + animation);
                } else {
                    //Log.d(TAG, "animation is PropertyValuesHolder object: " + animation);
                    PropertyValuesHolder[] values = animation.getValues();
                    Log.d(TAG, "animation is PropertyValuesHolder object: " + values.length + ", " + values[0].getPropertyName() +
                            ", " + animation.getAnimatedValue("hour") + ", " + animation.getAnimatedValue("minute")
                            + ", " + animation.getAnimatedValue("second") + ", " + animation.getAnimatedValue("mills_second"));
                }

            }
        });

    }

    protected void releaseAnim(ValueAnimator animator) {
        if (animator == null) {
            return;
        }
        if (animator.isRunning()) {
            animator.cancel();
        }
    }
}
