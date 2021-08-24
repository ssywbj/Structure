package com.suheng.structure.view;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.Xfermode;
import android.graphics.fonts.FontStyle;
import android.provider.Settings;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Scroller用法
 * 1.初始化Scroller
 * 2.调用startScroll()开始滚动
 * 3.执行invalidate()刷新界面
 * 4.重写View的computeScroll()并在其内部实现与滚动相关的业务逻辑
 * 5.再次执行invalidate()刷新界面
 */
public class InfiniteLine2 extends View {
    private int mIndex = 0;
    private Paint mPaint;
    private int mWidth;
    private Scroller mScroller;
    private int mDownX, mLastMoveX;

    public InfiniteLine2(Context context) {
        this(context, null);
    }

    public InfiniteLine2(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InfiniteLine2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setBackgroundColor(Color.GRAY);
        mScroller = new Scroller(getContext());

        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        mPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 22, metrics));

        mBitmapWhite = this.createBitmap(RECT_WIDTH, RECT_HEIGHT, Color.WHITE);
        mBitmapBlack = this.createBitmap(RECT_WIDTH / 2, RECT_HEIGHT / 2, Color.BLACK);
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d("Wbj", "onConfigurationChanged: " + newConfig);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        Log.d("Wbj", "onWindowFocusChanged: " + hasWindowFocus);
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE) {
            boolean checked = this.isBoldTextAdjustment();
            if (checked) {
                Log.d("Wbj", "粗休");
                mPaint.setTypeface(Typeface.DEFAULT_BOLD);
            } else {
                Log.d("Wbj", "非粗体");
                mPaint.setTypeface(Typeface.DEFAULT);
            }
        }
    }

    private boolean isBoldTextAdjustment2() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            int boldTextAdjustment = FontStyle.FONT_WEIGHT_BOLD - FontStyle.FONT_WEIGHT_NORMAL;
            int fontWeightAdjustment = Settings.Secure.getInt(getContext().getContentResolver(),
                    "font_weight_adjustment", 0); //Android S新特性：粗体文字
            return fontWeightAdjustment == boldTextAdjustment;
        } else {
            return false;
        }
    }

    private boolean isBoldTextAdjustment() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            Configuration configuration = getResources().getConfiguration();
            int boldTextAdjustment = FontStyle.FONT_WEIGHT_BOLD - FontStyle.FONT_WEIGHT_NORMAL;
            return configuration.fontWeightAdjustment == boldTextAdjustment; //Android S新特性：粗体文字
        } else {
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = (int) event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = (int) (mDownX - event.getX()) + mLastMoveX;
                smoothScrollTo(dx, 0);
                break;
            case MotionEvent.ACTION_UP:
                int ddx = (int) (event.getX() - mDownX);
                if (ddx < 0 && Math.abs(ddx) > 100) {
                    mIndex++;
                } else if (ddx > 0 && Math.abs(ddx) > 100) {
                    mIndex--;
                }
                smoothScrollTo(mWidth * mIndex, 0);
                mLastMoveX = mIndex * mWidth;
                break;
        }
        return true;
    }

    //调用此方法滚动到目标位置
    public void smoothScrollTo(int fx, int fy) {
        int dx = fx - mScroller.getFinalX();
        int dy = fy - mScroller.getFinalY();
        Log.d("Wbj", "fx: " + fx + ", fy: " + fy + "----dx: " + dx + ", dy: " + dy);
        Log.d("Wbj", "final, x: " + mScroller.getFinalX() + ", y: " + mScroller.getFinalY());
        smoothScrollBy(dx, dy);
    }

    //调用此方法设置滚动的相对偏移
    public void smoothScrollBy(int dx, int dy) {
        //设置mScroller的滚动偏移量
        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx, dy, 500);
        invalidate();//这里必须调用invalidate()才能保证computeScroll()会被调用，否则不一定会刷新界面，看不到滚动效果
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {//判断View的滚动是否在继续
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
    }

    private static final int RECT_WIDTH = 300, RECT_HEIGHT = 100;
    private final Paint mPaintRect = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RectF mRectXfermode;
    private static final Xfermode[] sModes = {
            new PorterDuffXfermode(PorterDuff.Mode.CLEAR),
            new PorterDuffXfermode(PorterDuff.Mode.SRC), //显示源图
            new PorterDuffXfermode(PorterDuff.Mode.DST), //只绘制目标图像
            new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER), //在目标图像的顶部绘制源图像
            new PorterDuffXfermode(PorterDuff.Mode.DST_OVER), //在源图像的上方绘制目标图像
            new PorterDuffXfermode(PorterDuff.Mode.SRC_IN), //只在源图像和目标图像相交的地方绘制源图像
            new PorterDuffXfermode(PorterDuff.Mode.DST_IN), //只在源图像和目标图像相交的地方绘制目标图像
            new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT), //只在源图像和目标图像不相交的地方绘制源图像
            new PorterDuffXfermode(PorterDuff.Mode.DST_OUT), //只在源图像和目标图像不相交的地方绘制目标图像
            new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP), //在源图像和目标图像相交的地方绘制源图像，在不相交的地方绘制目标图像
            new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP), //在源图像和目标图像相交的地方绘制目标图像，在不相交的地方绘制源图像
            new PorterDuffXfermode(PorterDuff.Mode.XOR), //在源图像和目标图像重叠之外的任何地方绘制他们，而在不重叠的地方不绘制任何内容
            new PorterDuffXfermode(PorterDuff.Mode.DARKEN), //变暗
            new PorterDuffXfermode(PorterDuff.Mode.LIGHTEN), //变亮
            new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY), //正片叠底
            new PorterDuffXfermode(PorterDuff.Mode.SCREEN), //滤色
            new PorterDuffXfermode(PorterDuff.Mode.ADD), //饱和相加
            new PorterDuffXfermode(PorterDuff.Mode.OVERLAY), //叠加
    };

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mWidth = getMeasuredWidth();
        drawText(canvas, (mIndex - 1) * mWidth, mIndex - 1);
        drawText(canvas, mIndex * mWidth, mIndex);
        drawText(canvas, (mIndex + 1) * mWidth, mIndex + 1);

        //中间镂空的矩形：start
        //Xfermode: https://www.jianshu.com/p/d11892bbe055
        mPaintRect.setColor(Color.WHITE);
        if (mRectXfermode == null) {
            mRectXfermode = new RectF((getWidth() - RECT_WIDTH) / 2f, (getHeight() - RECT_HEIGHT) / 2f
                    , (getWidth() + RECT_WIDTH) / 2f, (getHeight() + RECT_HEIGHT) / 2f);
        }
        int saveLayer = canvas.saveLayer(mRectXfermode, null);
        canvas.drawRect(mRectXfermode, mPaintRect); //目标图像
        mPaintRect.setXfermode(sModes[0]); //CLEAR
        mPaintRect.setColor(Color.parseColor("#000000"));
        float bottom = (getHeight() + RECT_HEIGHT / 2f) / 2f;
        canvas.drawRect((getWidth() - RECT_WIDTH / 2f) / 2f, (getHeight() - RECT_HEIGHT / 2f) / 2f
                , (getWidth() + RECT_WIDTH / 2f) / 2f, bottom, mPaintRect); //源图像
        mPaintRect.setXfermode(null);
        canvas.restoreToCount(saveLayer);

        bottom += (mBitmapWhite.getHeight() / 2f - 10);
        float left = (getWidth() - mBitmapWhite.getWidth()) / 2f;
        int sc = canvas.saveLayer(left, bottom, left + mBitmapWhite.getWidth(), bottom + mBitmapWhite.getHeight(), null);
        canvas.drawBitmap(mBitmapWhite, left, bottom, mPaintBimap); //先绘制目标图像
        mPaintBimap.setXfermode(sModes[7]); //再设置相交模式，SRC_OUT: 只在源图像和目标图像不相交的地方绘制源图像
        canvas.drawBitmap(mBitmapBlack, (getWidth() - mBitmapBlack.getWidth()) / 2f
                , bottom + mBitmapBlack.getHeight() / 2f, mPaintBimap); //最后绘制源图像
        mPaintBimap.setXfermode(null);
        canvas.restoreToCount(sc);
        //中间镂空的矩形：end
    }

    private Bitmap mBitmapWhite, mBitmapBlack;
    private final Paint mPaintBimap = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Bitmap createBitmap(int width, int height, int color) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(color);
        return bitmap;
    }

    private void drawText(Canvas canvas, int startX, int index) {
        canvas.save();
        canvas.translate(startX, 0);
        String text = "粗体测试：" + index;
        canvas.drawText(getEllipsizeText(text, mPaint).toString(), 0, 100, mPaint);
        canvas.restore();
        Log.d("Wbj", "页数：" + index);
    }

    private TextPaint mTextPaint;
    private final Rect mRect = new Rect();

    private CharSequence getEllipsizeText(String origin, Paint paint) {
        if (mTextPaint == null) {
            mTextPaint = new TextPaint(paint);
        }
        paint.getTextBounds(origin, 0, origin.length(), mRect);
        return TextUtils.ellipsize(origin, mTextPaint
                , mRect.width() * 0.8f, TextUtils.TruncateAt.MIDDLE);
    }

}
