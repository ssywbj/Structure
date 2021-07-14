package com.suheng.structure.view.wheel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.text.Layout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.FloatRange;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RawRes;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import com.suheng.structure.view.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class WheelView<T> extends View {
    private static final float DEFAULT_LINE_SPACING = dp2px(2f);
    private static final float DEFAULT_TEXT_SIZE = sp2px(15f);
    private static final float DEFAULT_TEXT_BOUNDARY_MARGIN = 0;
    private static final float DEFAULT_DIVIDER_HEIGHT = dp2px(1);
    private static final int DEFAULT_VISIBLE_ITEM = 5;
    private static final int DEFAULT_SCROLL_DURATION = 250;
    private static final int MODIFY_SCROLL_DURATION = 350;
    private static final long DEFAULT_CLICK_CONFIRM = 120;
    //    private static final String DEFAULT_INTEGER_FORMAT = "%02d";
    private static final String DEFAULT_INTEGER_FORMAT = "%2d";
    //默认折射比值，通过字体大小来实现折射视觉差
    private static final float DEFAULT_REFRACT_RATIO = 1f;

    //文字对齐方式
    public static final int TEXT_ALIGN_LEFT = 0;
    public static final int TEXT_ALIGN_CENTER = 1;
    public static final int TEXT_ALIGN_RIGHT = 2;

    //滚动状态
    public static final int SCROLL_STATE_IDLE = 0;
    public static final int SCROLL_STATE_DRAGGING = 1;
    public static final int SCROLL_STATE_SCROLLING = 2;

    //弯曲效果对齐方式
    public static final int CURVED_ARC_DIRECTION_LEFT = 0;
    public static final int CURVED_ARC_DIRECTION_CENTER = 1;
    public static final int CURVED_ARC_DIRECTION_RIGHT = 2;

    public static final float DEFAULT_CURVED_FACTOR = 0.75f;

    //分割线填充类型
    public static final int DIVIDER_TYPE_FILL = 0;
    public static final int DIVIDER_TYPE_WRAP = 1;

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mSecondPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mSelectedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    //是否自动调整字体大小以显示完全
    private boolean isAutoFitTextSize;
    private FontMetrics mFontMetrics;
    //文字中心距离baseline的距离
    private int mCenterToBaselineY;
    //可见的item条数
    private int mVisibleItems;
    //每个item之间的空间，行间距
    private float mLineSpacing;
    //是否循环滚动
    private boolean isCyclic;
    //文字对齐方式
    @TextAlign
    private int mTextAlign;
    //是否显示分割线
    private boolean isShowDivider;
    //分割线的颜色
    private int mDividerColor;
    //分割线高度
    private float mDividerSize;
    //分割线填充类型
    @DividerType
    private int mDividerType;
    //分割线类型为DIVIDER_TYPE_WRAP时 分割线左右两端距离文字的间距
    private float mDividerPaddingForWrap;
    //分割线两端形状，默认圆头
    private Paint.Cap mDividerCap = Paint.Cap.ROUND;
    //分割线和选中区域偏移，实现扩大选中区域
    private float mDividerOffset;

    //是否绘制选中区域
    private boolean isDrawSelectedRect;
    //选中区域颜色
    private int mSelectedRectColor;

    //文字起始X
    private int mStartX;
    //X轴中心点
    private int mCenterX;
    //Y轴中心点
    private int mCenterY;
    //选中边界的上下限制
    private int mSelectedItemTopLimit;
    private int mSelectedItemBottomLimit;
    //裁剪边界
    private int mClipLeft;
    private int mClipTop;
    private int mClipRight;
    private int mClipBottom;
    //绘制区域
    private Rect mDrawRect;
    //字体外边距，目的是留有边距
    private float mTextBoundaryMargin;

    //3D效果
    private Camera mCamera;
    private Matrix mMatrix;
    //是否是弯曲（3D）效果
    private boolean isCurved;
    //弯曲（3D）效果左右圆弧偏移效果方向 center 不偏移
    @CurvedArcDirection
    private int mCurvedArcDirection;
    //弯曲（3D）效果左右圆弧偏移效果系数 0-1之间 越大越明显
    private float mCurvedArcDirectionFactor;
    //选中后折射的偏移 与字体大小的比值，1为不偏移 越小偏移越明显
    //(普通效果和3d效果都适用)
    private float mRefractRatio;

    //数据列表
    @NonNull
    private List<T> mDataList = new ArrayList<>(1);
    //数据变化时，是否重置选中下标到第一个位置
    private boolean isResetSelectedPosition = false;

    private VelocityTracker mVelocityTracker;
    private int mMaxFlingVelocity;
    private int mMinFlingVelocity;
    private FixedScroller mScroller;

    //最小滚动距离，上边界
    private int mMinScrollY;
    //最大滚动距离，下边界
    private int mMaxScrollY;

    //Y轴滚动偏移
    private float mScrollOffsetY;
    //Y轴已滚动偏移，控制重绘次数
    private float mScrolledY = 0;
    //手指最后触摸的位置
//    private float mLastTouchY;
    private float mDownTouchY;
    private float mDownTouchScrollOffsetY;
    //手指按下时间，根据按下抬起时间差处理点击滚动
    private long mDownStartTime;
    //是否强制停止滚动
//    private boolean isForceFinishScroll = false;
    //是否是快速滚动，快速滚动结束后跳转位置
//    private boolean isFlingScroll;
    //当前选中的下标
    private int mSelectedItemPosition;
    //当前滚动经过的下标
    private int mCurrentScrollPosition;

    //字体
    private boolean mIsBoldForSelectedItem = false;
    //如果 mIsBoldForSelectedItem==true 则这个字体为未选中条目的字体
    private Typeface mNormalTypeface = null;
    //如果 mIsBoldForSelectedItem==true 则这个字体为选中条目的字体
    private Typeface mBoldTypeface = null;

    //监听器
    private OnItemSelectedListener<T> mOnItemSelectedListener;
    private OnWheelChangedListener mOnWheelChangedListener;

    //音频
    private SoundHelper mSoundHelper;
    //是否开启音频效果
    private boolean isSoundEffect = false;
    private int mTextSizeFirst, mTextSizeSecond, mTextSizeSelect;
    private int mTextSelectColor, mTextFirstColor, mTextSecondColor, mTextThirdColor;
    private int mItemNormalHeight, mItemSelectHeight;
    private int mWheelWidth, mWheelHeight;

    private boolean mYearDays, mHourWheel, mMinuteWheel;
    private boolean mIs24Format;
    private Calendar mCalendar;
    private Calendar mTempCalendar;
    private List<String> mMonthList = new ArrayList<>();
    private boolean mIsRtl;
    private int mBgColor;
//    private static final String EG_AR = "EG_ar";
//    private static final String IR_FA = "IR_fa";
//    private boolean isNumberNeedTranslate;

    Matrix mSelectedMatrix = new Matrix();

    private TextPaint mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private float mDensity;

    float mBaseWidth = 720;
    float mMinScrollGap = 2;
    private int mTotalHeight;
    private boolean mNeedFling;

    public WheelView(Context context) {
        this(context, null);
    }

    public WheelView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WheelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrsAndDefault(context, attrs);
        initValue(context);
    }

    public static boolean isRtl() {
        Locale locale = Locale.getDefault();
        return TextUtils.getLayoutDirectionFromLocale(locale) == View.LAYOUT_DIRECTION_RTL;
    }

    /**
     * 初始化自定义属性及默认值
     *
     * @param context 上下文
     * @param attrs   attrs
     */
    private void initAttrsAndDefault(Context context, AttributeSet attrs) {
        isAutoFitTextSize = false;
        mIsRtl = isRtl();
        mTextAlign = TEXT_ALIGN_CENTER;
        mTextBoundaryMargin = DEFAULT_TEXT_BOUNDARY_MARGIN;
        Locale locale = Locale.getDefault();
//        isNumberNeedTranslate = (locale.getCountry() + "_" + locale.getLanguage()).equals(EG_AR) || (locale.getCountry() + "_" + locale.getLanguage()).equals(IR_FA);

        Resources res = context.getResources();

        mTextSelectColor = ContextCompat.getColor(context, R.color.os_text_primary_color);

        mTextFirstColor = ContextCompat.getColor(context, R.color.os_text_quaternary_color);
        mTextSecondColor = ContextCompat.getColor(context, R.color.os_text_tertiary_color);
        mTextThirdColor = ContextCompat.getColor(context, R.color.os_text_tertiary_color);
        mBgColor = ContextCompat.getColor(context, R.color.os_altitude_tertiary_color);


        TypedArray taWidth = context.obtainStyledAttributes(attrs, new int[]{R.attr.wheelWidth});
        mWheelWidth = taWidth.getDimensionPixelSize(0, res.getDimensionPixelSize(R.dimen.picker_wheel_width_hour));
        mWheelHeight = res.getDimensionPixelSize(R.dimen.picker_wheel_item_height);
        taWidth.recycle();


        mTextSizeFirst = res.getDimensionPixelSize(R.dimen.picker_wheel_text_first);
//        mTextSizeSecond = res.getDimensionPixelSize(R.dimen.picker_wheel_text_second);
        mTextSizeSecond = res.getDimensionPixelSize(R.dimen.picker_wheel_text_first);
        mTextSizeSelect = res.getDimensionPixelSize(R.dimen.picker_wheel_text_select);
        mItemNormalHeight = res.getDimensionPixelSize(R.dimen.picker_wheel_item_height);
        mItemSelectHeight = res.getDimensionPixelSize(R.dimen.picker_wheel_item_height_select);

        mSecondPaint.setTextSize(mTextSizeSecond);
        mSecondPaint.setColor(mTextSecondColor);

        mSelectedPaint.setTextSize(mTextSizeSecond);

//        mPaint.setTypeface(mBoldTypeface);
//        mPaint.setColor(textColor);
//        mPaint.setTextSize(scaleTextSize);


        mLineSpacing = DEFAULT_LINE_SPACING;
        mVisibleItems = DEFAULT_VISIBLE_ITEM;
        //跳转可见item为奇数
        mVisibleItems = adjustVisibleItems(mVisibleItems);
        mSelectedItemPosition = 0;
        //初始化滚动下标
        mCurrentScrollPosition = mSelectedItemPosition;
        isCyclic = false;

        isShowDivider = false;
        mDividerType = DIVIDER_TYPE_FILL;
        mDividerSize = DEFAULT_DIVIDER_HEIGHT;
        mDividerColor = mTextSelectColor;
        mDividerPaddingForWrap = DEFAULT_TEXT_BOUNDARY_MARGIN;

        mDividerOffset = 0;
        isDrawSelectedRect = false;
        mSelectedRectColor = Color.TRANSPARENT;

        isCurved = false;
        mCurvedArcDirection = CURVED_ARC_DIRECTION_CENTER;
        mCurvedArcDirectionFactor = DEFAULT_CURVED_FACTOR;
        //折射偏移默认值
        //Deprecated 将在新版中移除
        float curvedRefractRatio = 0.9f;
        mRefractRatio = DEFAULT_REFRACT_RATIO;
        mRefractRatio = isCurved ? Math.min(curvedRefractRatio, mRefractRatio) : mRefractRatio;
        if (mRefractRatio > 1f) {
            mRefractRatio = 1.0f;
        } else if (mRefractRatio < 0f) {
            mRefractRatio = DEFAULT_REFRACT_RATIO;
        }
        float density = context.getResources().getDisplayMetrics().density;
        mBaseWidth = density * 360;
        mMinScrollGap = 2 * density;
    }

    /**
     * 初始化并设置默认值
     *
     * @param context 上下文
     */
    private void initValue(Context context) {
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        mMaxFlingVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
        mMinFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
//        EaseCubicInterpolator interpolator = new EaseCubicInterpolator(0.4f, 0f, 0.2f, 1f);
        Interpolator interpolator = new OvershootInterpolator(2);
        mScroller = new FixedScroller(context, interpolator);
        mDrawRect = new Rect();
        mCamera = new Camera();
        mMatrix = new Matrix();
        mDensity = context.getResources().getDisplayMetrics().density;
        if (isSoundEffect && !isInEditMode()) {
            mSoundHelper = SoundHelper.obtain();
            initDefaultVolume(context);
        }
        calculateTextSize();
        updateTextAlign();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mSoundHelper != null) {
            mSoundHelper.release();
        }
    }

    /**
     * 初始化默认音量
     *
     * @param context 上下文
     */
    private void initDefaultVolume(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            //获取系统媒体当前音量
            int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            //获取系统媒体最大音量
            int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            //设置播放音量
            mSoundHelper.setPlayVolume(currentVolume * 1.0f / maxVolume);
        } else {
            mSoundHelper.setPlayVolume(0.3f);
        }
    }

    /**
     * 测量文字最大所占空间
     */
    private void calculateTextSize() {
        mPaint.setTextSize(mTextSizeSelect);
        mFontMetrics = mPaint.getFontMetrics();
    }

    /**
     * 更新textAlign
     */
    private void updateTextAlign() {
        switch (mTextAlign) {
            case TEXT_ALIGN_LEFT:
                mPaint.setTextAlign(Paint.Align.LEFT);
                break;
            case TEXT_ALIGN_RIGHT:
                mPaint.setTextAlign(Paint.Align.RIGHT);
                break;
            case TEXT_ALIGN_CENTER:
            default:
                mPaint.setTextAlign(Paint.Align.CENTER);
                break;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //Line Space算在了mItemHeight中
        int height;
        if (isCurved) {
            height = (int) ((mWheelHeight * mVisibleItems * 2 / Math.PI) + getPaddingTop() + getPaddingBottom());
        } else {
            height = mWheelHeight * mVisibleItems + getPaddingTop() + getPaddingBottom();
        }
        int width = (int) (mWheelWidth + getPaddingLeft() + getPaddingRight() + mTextBoundaryMargin * 2);
        if (isCurved) {
            int towardRange = (int) (Math.sin(Math.PI / 48) * height);
            width += towardRange;
        }
        setMeasuredDimension(resolveSizeAndState(width, widthMeasureSpec, 0),
                resolveSizeAndState(height, heightMeasureSpec, 0));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //设置内容可绘制区域
        mDrawRect.set(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
        mCenterX = mDrawRect.centerX();
        mCenterY = mDrawRect.centerY();
        mSelectedItemTopLimit = (int) (mCenterY - mWheelHeight / 2 - mDividerOffset);
        mSelectedItemBottomLimit = (int) (mCenterY + mWheelHeight / 2 + mDividerOffset);
        mClipLeft = getPaddingLeft();
        mClipTop = getPaddingTop();
        mClipRight = getWidth() - getPaddingRight();
        mClipBottom = getHeight() - getPaddingBottom();

        calculateDrawStart();
        //计算滚动限制
        calculateLimitY();

        //如果初始化时有选中的下标，则计算选中位置的距离
        int itemDistance = calculateItemDistance(mSelectedItemPosition);
        if (itemDistance > 0) {
            doScroll(itemDistance);
        }
    }

    /**
     * 起算起始位置
     */
    private void calculateDrawStart() {
        switch (mTextAlign) {
            case TEXT_ALIGN_LEFT:
                mStartX = (int) (getPaddingLeft() + mTextBoundaryMargin);
                break;
            case TEXT_ALIGN_RIGHT:
                mStartX = (int) (getWidth() - getPaddingRight() - mTextBoundaryMargin);
                break;
            case TEXT_ALIGN_CENTER:
            default:
                mStartX = getWidth() / 2;
                break;
        }

        //文字中心距离baseline的距离
        mCenterToBaselineY = (int) (mFontMetrics.ascent + (mFontMetrics.descent - mFontMetrics.ascent) / 2);
    }

    /**
     * 计算滚动限制
     */
    private void calculateLimitY() {
        mMinScrollY = isCyclic ? Integer.MIN_VALUE : 0;
        //下边界 (dataSize - 1 - mInitPosition) * mWheelHeight
        mMaxScrollY = isCyclic ? Integer.MAX_VALUE : (mDataList.size() - 1) * mWheelHeight;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBgColor != 0) {
            canvas.drawColor(mBgColor);
        }
        int itemHeight = dividedItemHeight();

        int scrolledItem = (int) (mScrollOffsetY / itemHeight);
        final int OFFSET_ITEMS = 4;
        float height = getHeight();
        float halfHeight = height * 0.5f;
        int SCREEN_ITEMS = (int) (height / itemHeight);
        int ITEMS = OFFSET_ITEMS + SCREEN_ITEMS;
        int index = scrolledItem - (ITEMS >> 1);
        float endIndex = index + ITEMS;

        float halfItemHeight = itemHeight * 0.5f;
        float top = halfHeight - halfItemHeight - mScrollOffsetY;

        float cy = halfHeight;

        for (; index < endIndex; index += 1) {
            float oy = top + index * itemHeight;
            float dy = Math.abs(cy - oy - halfItemHeight);

            if (dy < halfItemHeight) {
                float scale = 1 - dy / halfItemHeight;
                float textSizeScale = 1.f + 1.f * (mTextSizeSelect - mTextSizeSecond) / mTextSizeSecond * scale;
                mSelectedPaint.setTextSize(textSizeScale * mTextSizeSecond);
                int textColor = textColorScale(scale);
                mSelectedPaint.setColor(textColor);
                drawItem2(canvas, mSelectedPaint, index, oy, itemHeight);
                continue;
            }
            drawItem2(canvas, mSecondPaint, index, oy, itemHeight);
        }
    }

    int textColorScale(float scale) {
        if (mTextSecondColor == mTextSelectColor) {
            return mTextSelectColor;
        }
        //
        int srcColorA = (mTextSecondColor & 0xFF000000) >>> 24;
        int srcColorR = (mTextSecondColor & 0xFF0000) >>> 16;
        int srcColorG = (mTextSecondColor & 0xFF00) >>> 8;
        int srcColorB = mTextSecondColor & 0xFF;

        int targetColorA = (mTextSelectColor & 0xFF000000) >>> 24;
        int targetColorR = (mTextSelectColor & 0xFF0000) >>> 16;
        int targetColorG = (mTextSelectColor & 0xFF00) >>> 8;
        int targetColorB = mTextSelectColor & 0xFF;

        int colorA = (int) (srcColorA + (targetColorA - srcColorA) * scale);
        int colorR = (int) (srcColorR + (targetColorR - srcColorR) * scale);
        int colorG = (int) (srcColorG + (targetColorG - srcColorG) * scale);
        int colorB = (int) (srcColorB + (targetColorB - srcColorB) * scale);

        return (colorA << 24) | (colorR << 16) | (colorG << 8) | colorB;
    }

    private void drawItem2(Canvas canvas, Paint paint, int index, float offsetY, float itemHeight) {
        String text = getDataByIndex(index);
        if (text == null) {
            return;
        }

        float textSize = paint.getTextSize();
        float textWidth = paint.measureText(text);

        float dy = (itemHeight - textSize) * 0.5f;
        float reset = getWidth() - textWidth;
        //os: add by guisheng.wang5 fix bug OSBRREL-1123  OSBRDEV-7109 日期外文显示不全显示省略号 (核对，控件尺寸正确) 20210626 start
        if(reset < 0) {
            text = TextUtils.ellipsize(text, new TextPaint(paint), getWidth(), TextUtils.TruncateAt.END).toString();
            reset = getWidth() - textWidth;
        }
        //os: add by guisheng.wang5 fix bug OSBRREL-1123  OSBRDEV-7109 日期外文显示不全显示省略号 (核对，控件尺寸正确) 20210626 start
        float left = reset > 0 ? reset * 0.5f : 0;
        float top = offsetY + dy + textSize;
        canvas.drawText(text, left, top, paint);
    }

    /**
     * 重新测量字体大小
     *
     * @param contentText 被测量文字内容
     * @return 文字中心距离baseline的距离
     */
    private int remeasureTextSize(String contentText) {
        float textWidth = mPaint.measureText(contentText);
        float drawWidth = getWidth();
        float textMargin = mTextBoundaryMargin * 2;
        //稍微增加了一点文字边距 最大为宽度的1/10
        if (textMargin > (drawWidth / 10f)) {
            drawWidth = drawWidth * 9f / 10f;
            textMargin = drawWidth / 10f;
        } else {
            drawWidth = drawWidth - textMargin;
        }
        if (drawWidth <= 0) {
            return mCenterToBaselineY;
        }
        float textSize = mTextSizeSecond;
        while (textWidth > drawWidth) {
            textSize--;
            if (textSize <= 0) {
                break;
            }
            mPaint.setTextSize(textSize);
            textWidth = mPaint.measureText(contentText);
        }
        //重新计算文字起始X
        recalculateStartX(textMargin / 2.0f);
        //高度起点也变了
        return recalculateCenterToBaselineY();
    }

    /**
     * 重新计算字体起始X
     *
     * @param textMargin 文字外边距
     */
    private void recalculateStartX(float textMargin) {
        switch (mTextAlign) {
            case TEXT_ALIGN_LEFT:
                mStartX = (int) textMargin;
                break;
            case TEXT_ALIGN_RIGHT:
                mStartX = (int) (getWidth() - textMargin);
                break;
            case TEXT_ALIGN_CENTER:
            default:
                mStartX = getWidth() / 2;
                break;
        }
    }

    /**
     * 字体大小变化后重新计算距离基线的距离
     *
     * @return 文字中心距离baseline的距离
     */
    private int recalculateCenterToBaselineY() {
        FontMetrics fontMetrics = mPaint.getFontMetrics();
        //高度起点也变了
        return (int) (fontMetrics.ascent + (fontMetrics.descent - fontMetrics.ascent) / 2);
    }

    /**
     * 根据下标获取到内容
     *
     * @param index 下标
     * @return 绘制的文字内容
     */
    private String getDataByIndex(int index) {
        int dataSize = mDataList.size();
        if (dataSize == 0) {
            return null;
        }

        String itemText = null;
        if (isCyclic) {
            int i = index % dataSize;
            if (i < 0) {
                i += dataSize;
            }
            itemText = getDataText(mDataList.get(i));
        } else {
            if (index >= 0 && index < dataSize) {
                itemText = getDataText(mDataList.get(index));
            }
        }

        if (mYearDays && !TextUtils.isEmpty(itemText)) {
            itemText = dayOfYearToMonthDay(itemText);
        }
        return itemText;
    }

    /**
     * 获取item text
     *
     * @param item item数据
     * @return 文本内容
     */
    protected String getDataText(T item) {
        if (item == null) {
            return "";
        } else if (item instanceof Integer) {
            int value = ((Integer) item).intValue();
            String format;
            if (mHourWheel) {
                format = mIs24Format ? "%02d" : "%d";
            } else if (mMinuteWheel) {
                format = "%02d";
            } else {
                format = "%d";
            }
            return String.format(Locale.getDefault(), format, value) + "";
        } else if (item instanceof String) {
            return (String) item;
        }
        return item.toString();
    }

    protected void forceStopScroller() {
        if (!mScroller.isFinished()) {
            //强制滚动完成
            mScroller.forceFinished(true);
        }
    }

    protected void runScroller() {
        if (mScroller.isFinished()) {
            return;
        }
        ViewCompat.postOnAnimation(this, mScrollerRun);
    }

    protected void updateScroller() {
        boolean computeUpdate = mScroller.computeScrollOffset();
        if (!computeUpdate) {
            return;
        }
        mScrollOffsetY = mScroller.getCurrY() + mScroller.getFixedFlingValue();
        invalidateIfYChanged();
    }

    float modifyScrollerPosition(boolean scrollAnimation, float clickToCenterDistance) {
        //
        float scrollRange = clickToCenterDistance + calculateDistanceToEndPoint((clickToCenterDistance + mScrollOffsetY) % dividedItemHeight());
        //大于最小值滚动值
        boolean isInMinRange = scrollRange < 0 && mScrollOffsetY + scrollRange >= mMinScrollY;
        //小于最大滚动值
        boolean isInMaxRange = scrollRange > 0 && mScrollOffsetY + scrollRange <= mMaxScrollY;

        if (isInMinRange || isInMaxRange) {
            //在滚动范围之内再修正位置
            //平稳滑动
            if (scrollAnimation) {
                mScroller.startScroll(0, (int) mScrollOffsetY, 0, (int) scrollRange, MODIFY_SCROLL_DURATION);
            }
            return scrollRange;
        }
        return 0;
    }

    protected Runnable mScrollerRun = new Runnable() {
        @Override
        public void run() {
            updateScroller();
            runScroller();
        }
    };

    public static float measureTextHeight(Paint paint) {
        if (null == paint) {
            return 0;
        }
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        return (fontMetrics.descent - fontMetrics.ascent) * 0.5f;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //屏蔽如果未设置数据时，触摸导致运算数据不正确的崩溃 issue #20
        if (!isEnabled() || mDataList.isEmpty()) {
            return super.onTouchEvent(event);
        }
        initVelocityTracker();
        mVelocityTracker.addMovement(event);

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                //手指按下
                //处理滑动事件嵌套 拦截事件序列
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                forceStopScroller();
                mDownTouchY = event.getY();
                mDownTouchScrollOffsetY = mScrollOffsetY;
                //按下时间
                mDownStartTime = System.currentTimeMillis();
                mTotalHeight = (int) (measureTextHeight(mSecondPaint) * mDataList.size());
                mNeedFling = false;
                break;
            case MotionEvent.ACTION_MOVE:
                //手指移动
                float moveY = event.getY();
                float deltaY = mDownTouchY - moveY;
                if (Math.abs(deltaY) >= mTotalHeight) {
                    mNeedFling = true;
                }
                mScrollOffsetY = mDownTouchScrollOffsetY + deltaY;

                if (mOnWheelChangedListener != null) {
                    mOnWheelChangedListener.onWheelScrollStateChanged(SCROLL_STATE_DRAGGING);
                }
                onWheelScrollStateChanged(SCROLL_STATE_DRAGGING);
                invalidateIfYChanged();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                //手指抬起
//                isForceFinishScroll = false;
                mVelocityTracker.computeCurrentVelocity(1000, mMaxFlingVelocity);
                float velocityY = mVelocityTracker.getYVelocity();
                if (Math.abs(velocityY) > mMinFlingVelocity || mNeedFling) {
                    //快速滑动
                    forceStopScroller();
//                    isFlingScroll = true;
                    mScroller.fling(0, (int) mScrollOffsetY, 0, (int) -velocityY, 0, 0,
                            mMinScrollY, mMaxScrollY);


                    float finalY = mScroller.getFinalY();
                    float scrollOffsetY = mScrollOffsetY;
                    mScrollOffsetY = finalY;
                    float modifyY = modifyScrollerPosition(false, 0);
                    mScrollOffsetY = scrollOffsetY;
                    mScroller.setFixedFlingValue(modifyY);
                } else {
                    int clickToCenterDistance = 0;
                    if (System.currentTimeMillis() - mDownStartTime <= DEFAULT_CLICK_CONFIRM) {
                        //处理点击滚动
                        //手指抬起的位置到中心的距离为滚动差值
                        clickToCenterDistance = (int) (event.getY() - mCenterY);
                    }
                    modifyScrollerPosition(true, clickToCenterDistance);
                }
                runScroller();
                invalidateIfYChanged();
                recycleVelocityTracker();
                break;
        }

        return true;
    }

    /**
     * 初始化 VelocityTracker
     */
    private void initVelocityTracker() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
    }

    /**
     * 回收 VelocityTracker
     */
    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    /**
     * 计算滚动偏移
     *
     * @param distance 滚动距离
     */
    private void doScroll(float distance) {
        mScrollOffsetY += distance;
        if (!isCyclic) {
            //修正边界
            if (mScrollOffsetY < mMinScrollY) {
                mScrollOffsetY = mMinScrollY;
            } else if (mScrollOffsetY > mMaxScrollY) {
                mScrollOffsetY = mMaxScrollY;
            }
        }
    }

    /**
     * 当Y轴的偏移值改变时再重绘，减少重回次数
     */
    private void invalidateIfYChanged() {
        if (mScrollOffsetY != mScrolledY) {
            mScrolledY = mScrollOffsetY;
            //滚动偏移发生变化
            if (mOnWheelChangedListener != null) {
                mOnWheelChangedListener.onWheelScroll((int) mScrollOffsetY);
            }
            onWheelScroll(mScrollOffsetY);
            //观察item变化
            observeItemChanged();
            invalidate();
        }
    }

    /**
     * 观察item改变
     */
    private void observeItemChanged() {
        //item改变回调
        int oldPosition = mCurrentScrollPosition;
        int newPosition = getCurrentPosition();
        if (oldPosition != newPosition) {
            //改变了
            if (mOnWheelChangedListener != null && mScrolledY >= mMinScrollY && mScrolledY <= mMaxScrollY) {
                mOnWheelChangedListener.onWheelItemChanged(oldPosition, newPosition);
            }
            onWheelItemChanged(oldPosition, newPosition);
            //播放音频
            playSoundEffect();
            //更新下标
            mCurrentScrollPosition = newPosition;
            //os: add by guisheng.wang5 fix up bug:OSBRREL-916 wheelview滚动没重置 mSelectedItemPosition 手动重置，不然滚动年份或者月份会回到初始化位置 20210629 start
            mSelectedItemPosition = newPosition;
            //os: add by guisheng.wang5 fix up bug:OSBRREL-916 wheelview滚动没重置 mSelectedItemPosition 手动重置，不然滚动年份或者月份会回到初始化位置 20210629 end
        }
    }

    /**
     * 播放滚动音效
     */
    public void playSoundEffect() {
        if (mSoundHelper != null && isSoundEffect) {
            mSoundHelper.playSoundEffect();
        }
    }

    /**
     * 强制滚动完成，直接停止
     */
    public void forceFinishScroll() {
        if (!mScroller.isFinished()) {
            mScroller.forceFinished(true);
        }
    }

    /**
     * 强制滚动完成，并且直接滚动到最终位置
     */
    public void abortFinishScroll() {
        if (!mScroller.isFinished()) {
            mScroller.abortAnimation();
        }
    }

    /**
     * 计算距离终点的偏移，修正选中条目
     *
     * @param remainder 余数
     * @return 偏移量
     */
    private float calculateDistanceToEndPoint(float remainder) {
        if (Math.abs(remainder) > mWheelHeight / 2) {
            if (mScrollOffsetY < 0) {
                return -mWheelHeight - remainder;
            } else {
                return mWheelHeight - remainder;
            }
        } else {
            return -remainder;
        }
    }

    /**
     * 根据偏移计算当前位置下标
     *
     * @return 偏移量对应的当前下标 if dataList is empty return -1
     */
    private int getCurrentPosition() {
        if (mDataList.isEmpty()) {
            return -1;
        }
        int itemPosition;
        if (mScrollOffsetY < 0) {
            itemPosition = (int) ((mScrollOffsetY - mWheelHeight / 2) / dividedItemHeight());
        } else {
            itemPosition = (int) ((mScrollOffsetY + mWheelHeight / 2) / dividedItemHeight());
        }
        int currentPosition = itemPosition % mDataList.size();
        if (currentPosition < 0) {
            currentPosition += mDataList.size();
        }
        return currentPosition;
    }

//    int

    /**
     * mWheelHeight 为被除数时避免为0
     *
     * @return 被除数不为0
     */
    private int dividedItemHeight() {
        return mWheelHeight > 0 ? mWheelHeight : 1;
    }

    /**
     * 获取音效开关状态
     *
     * @return 是否开启滚动音效
     */
    public boolean isSoundEffect() {
        return isSoundEffect;
    }

    /**
     * 设置音效开关
     *
     * @param isSoundEffect 是否开启滚动音效
     */
    public void setSoundEffect(boolean isSoundEffect) {
        this.isSoundEffect = isSoundEffect;
    }

    /**
     * 设置声音效果资源
     *
     * @param rawResId 声音效果资源 越小效果越好 {@link RawRes}
     */
    public void setSoundEffectResource(@RawRes int rawResId) {
        if (mSoundHelper != null) {
            mSoundHelper.load(getContext(), rawResId);
        }
    }

    /**
     * 获取播放音量
     *
     * @return 播放音量 range 0.0-1.0
     */
    public float getPlayVolume() {
        return mSoundHelper == null ? 0 : mSoundHelper.getPlayVolume();
    }

    /**
     * 设置播放音量
     *
     * @param playVolume 播放音量 range 0.0-1.0
     */
    public void setPlayVolume(@FloatRange(from = 0.0, to = 1.0) float playVolume) {
        if (mSoundHelper != null) {
            mSoundHelper.setPlayVolume(playVolume);
        }
    }

    /**
     * 获取指定 position 的数据
     *
     * @param position 下标
     * @return position 对应的数据 {@link Nullable}
     */
    @Nullable
    public T getItemData(int position) {
        if (isPositionInRange(position)) {
            return mDataList.get(position);
        } else if (mDataList.size() > 0 && position >= mDataList.size()) {
            return mDataList.get(mDataList.size() - 1);
        } else if (mDataList.size() > 0 && position < 0) {
            return mDataList.get(0);
        }
        return null;
    }

    /**
     * 获取当前选中的item数据
     *
     * @return 当前选中的item数据
     */
    public T getSelectedItemData() {
        return getItemData(mSelectedItemPosition);
    }

    /**
     * 获取数据列表
     *
     * @return 数据列表
     */
    public List<T> getData() {
        return mDataList;
    }

    /**
     * 设置数据
     *
     * @param dataList 数据列表
     */
    public void setData(List<T> dataList) {
        if (dataList == null) {
            return;
        }
        mDataList = dataList;
        if (!isResetSelectedPosition && mDataList.size() > 0) {
            //不重置选中下标
            if (mSelectedItemPosition >= mDataList.size()) {
                mSelectedItemPosition = mDataList.size() - 1;
                //重置滚动下标
                mCurrentScrollPosition = mSelectedItemPosition;
            }
        } else {
            //重置选中下标和滚动下标
            mCurrentScrollPosition = mSelectedItemPosition = 0;
        }
        //强制滚动完成
        forceFinishScroll();
        calculateTextSize();
        calculateLimitY();
        //重置滚动偏移
        mScrollOffsetY = mSelectedItemPosition * mWheelHeight;
        requestLayout();
        invalidate();
    }

    /**
     * 当数据变化时，是否重置选中下标到第一个
     *
     * @return 是否重置选中下标到第一个
     */
    public boolean isResetSelectedPosition() {
        return isResetSelectedPosition;
    }

    /**
     * 设置当数据变化时，是否重置选中下标到第一个
     *
     * @param isResetSelectedPosition 当数据变化时,是否重置选中下标到第一个
     */
    public void setResetSelectedPosition(boolean isResetSelectedPosition) {
        this.isResetSelectedPosition = isResetSelectedPosition;
    }

    /**
     * 获取是否自动调整字体大小，以显示完全
     *
     * @return 是否自动调整字体大小
     */
    public boolean isAutoFitTextSize() {
        return isAutoFitTextSize;
    }

    /**
     * 设置是否自动调整字体大小，以显示完全
     *
     * @param isAutoFitTextSize 是否自动调整字体大小
     */
    public void setAutoFitTextSize(boolean isAutoFitTextSize) {
        this.isAutoFitTextSize = isAutoFitTextSize;
        invalidate();
    }

    /**
     * 获取当前字体
     *
     * @return 字体
     */
    public Typeface getTypeface() {
        return mPaint.getTypeface();
    }

    /**
     * 设置当前字体
     *
     * @param typeface 字体
     */
    public void setTypeface(Typeface typeface) {
        setTypeface(typeface, false);
    }

    /**
     * 设置当前字体
     *
     * @param typeface              字体
     * @param isBoldForSelectedItem 是否设置选中条目字体加粗，其他条目不会加粗
     */
    public void setTypeface(Typeface typeface, boolean isBoldForSelectedItem) {
        if (typeface == null || mPaint.getTypeface() == typeface) {
            return;
        }
        //强制滚动完成
        forceFinishScroll();
        mIsBoldForSelectedItem = isBoldForSelectedItem;
        if (mIsBoldForSelectedItem) {
            //如果设置了选中条目字体加粗，其他条目不会加粗，则拆分两份字体
            if (typeface.isBold()) {
                mNormalTypeface = Typeface.create(typeface, Typeface.NORMAL);
                mBoldTypeface = typeface;
            } else {
                mNormalTypeface = typeface;
                mBoldTypeface = Typeface.create(typeface, Typeface.BOLD);
            }
            //测量时 使用加粗字体测量，因为加粗字体比普通字体宽，以大的为准进行测量
            mPaint.setTypeface(mBoldTypeface);
        } else {
            mPaint.setTypeface(typeface);
        }
        calculateTextSize();
        calculateDrawStart();
        //字体大小变化，偏移距离也变化了
        mScrollOffsetY = mSelectedItemPosition * mWheelHeight;
        calculateLimitY();
        requestLayout();
        invalidate();
    }

    /**
     * 获取文字对齐方式
     *
     * @return 文字对齐
     * {@link #TEXT_ALIGN_LEFT}
     * {@link #TEXT_ALIGN_CENTER}
     * {@link #TEXT_ALIGN_RIGHT}
     */
    public int getTextAlign() {
        return mTextAlign;
    }

    /**
     * 设置文字对齐方式
     *
     * @param textAlign 文字对齐方式
     *                  {@link #TEXT_ALIGN_LEFT}
     *                  {@link #TEXT_ALIGN_CENTER}
     *                  {@link #TEXT_ALIGN_RIGHT}
     */
    public void setTextAlign(@TextAlign int textAlign) {
        if (mTextAlign == textAlign) {
            return;
        }
        mTextAlign = textAlign;
        updateTextAlign();
        calculateDrawStart();
        invalidate();
    }

    /**
     * 获取文字距离边界的外边距
     *
     * @return 外边距值
     */
    public float getTextBoundaryMargin() {
        return mTextBoundaryMargin;
    }

    /**
     * 设置文字距离边界的外边距
     *
     * @param textBoundaryMargin 外边距值
     */
    public void setTextBoundaryMargin(float textBoundaryMargin) {
        setTextBoundaryMargin(textBoundaryMargin, false);
    }

    /**
     * 设置文字距离边界的外边距
     *
     * @param textBoundaryMargin 外边距值
     * @param isDp               单位是否为 dp
     */
    public void setTextBoundaryMargin(float textBoundaryMargin, boolean isDp) {
        float tempTextBoundaryMargin = mTextBoundaryMargin;
        mTextBoundaryMargin = isDp ? dp2px(textBoundaryMargin) : textBoundaryMargin;
        if (tempTextBoundaryMargin == mTextBoundaryMargin) {
            return;
        }
        requestLayout();
        invalidate();
    }

    /**
     * 获取item间距
     *
     * @return 行间距值
     */
    public float getLineSpacing() {
        return mLineSpacing;
    }

    /**
     * 设置item间距
     *
     * @param lineSpacing 行间距值
     */
    public void setLineSpacing(float lineSpacing) {
        setLineSpacing(lineSpacing, false);
    }

    /**
     * 设置item间距
     *
     * @param lineSpacing 行间距值
     * @param isDp        lineSpacing 单位是否为 dp
     */
    public void setLineSpacing(float lineSpacing, boolean isDp) {
        float tempLineSpace = mLineSpacing;
        mLineSpacing = isDp ? dp2px(lineSpacing) : lineSpacing;
        if (tempLineSpace == mLineSpacing) {
            return;
        }
        mScrollOffsetY = 0;
        calculateTextSize();
        requestLayout();
        invalidate();
    }

    /**
     * 获取可见条目数
     *
     * @return 可见条目数
     */
    public int getVisibleItems() {
        return mVisibleItems;
    }

    /**
     * 设置可见的条目数
     *
     * @param visibleItems 可见条目数
     */
    public void setVisibleItems(int visibleItems) {
        if (mVisibleItems == visibleItems) {
            return;
        }
        mVisibleItems = adjustVisibleItems(visibleItems);
        mScrollOffsetY = 0;
        requestLayout();
        invalidate();
    }

    /**
     * 跳转可见条目数为奇数
     *
     * @param visibleItems 可见条目数
     * @return 调整后的可见条目数
     */
    private int adjustVisibleItems(int visibleItems) {
        return Math.abs(visibleItems / 2 * 2 + 1); // 当传入的值为偶数时,换算成奇数;
    }

    /**
     * 是否是循环滚动
     *
     * @return 是否是循环滚动
     */
    public boolean isCyclic() {
        return isCyclic;
    }

    /**
     * 设置是否循环滚动
     *
     * @param isCyclic 是否是循环滚动
     */
    public void setCyclic(boolean isCyclic) {
        if (this.isCyclic == isCyclic) {
            return;
        }
        this.isCyclic = isCyclic;

        forceFinishScroll();
        calculateLimitY();
        //设置当前选中的偏移值
        mScrollOffsetY = mSelectedItemPosition * mWheelHeight;
        invalidate();
    }

    /**
     * 获取当前选中下标
     *
     * @return 当前选中的下标
     */
    public int getSelectedItemPosition() {
        return mSelectedItemPosition;
    }

    /**
     * 设置当前选中下标
     *
     * @param position 下标
     */
    public void setSelectedItemPosition(int position) {
        setSelectedItemPosition(position, false);
    }

    /**
     * 设置当前选中下标
     *
     * @param position       下标
     * @param isSmoothScroll 是否平滑滚动
     */
    public void setSelectedItemPosition(int position, boolean isSmoothScroll) {
        setSelectedItemPosition(position, isSmoothScroll, 0);
    }

    /**
     * 设置当前选中下标
     * <p>
     * bug 修复记录：调用这个方法时大多数情况在初始化时，如果没有执行 onSizeChanged() 方法时，调用这个方法会导致失效
     * 因为 onSizeChanged() 方法执行结束才确定边界等信息，
     * 所以在 onSizeChanged() 方法增加了兼容，如果 mSelectedItemPosition >0 的情况重新计算一下滚动值。
     *
     * @param position       下标
     * @param isSmoothScroll 是否平滑滚动
     * @param smoothDuration 平滑滚动时间
     */
    public void setSelectedItemPosition(int position, boolean isSmoothScroll, int smoothDuration) {
        if (!isPositionInRange(position)) {
            return;
        }

        //item之间差值
        int itemDistance = calculateItemDistance(position);
        if (itemDistance == 0) {
            return;
        }
        //如果Scroller滑动未停止，强制结束动画
        abortFinishScroll();

        if (isSmoothScroll) {
            //如果是平滑滚动并且之前的Scroll滚动完成
            mScroller.startScroll(0, (int) mScrollOffsetY, 0, itemDistance,
                    smoothDuration > 0 ? smoothDuration : DEFAULT_SCROLL_DURATION);
            invalidateIfYChanged();
//            ViewCompat.postOnAnimation(this, this);

        } else {
            doScroll(itemDistance);
            mSelectedItemPosition = position;
            //选中条目回调
            if (mOnItemSelectedListener != null) {
                mOnItemSelectedListener.onItemSelected(this, mDataList.get(mSelectedItemPosition), mSelectedItemPosition);
            }
            onItemSelected(mDataList.get(mSelectedItemPosition), mSelectedItemPosition);
            if (mOnWheelChangedListener != null) {
                mOnWheelChangedListener.onWheelSelected(mSelectedItemPosition);
            }
            onWheelSelected(mSelectedItemPosition);
            invalidateIfYChanged();
        }

    }

    private int calculateItemDistance(int position) {
        return (int) (position * mWheelHeight - mScrollOffsetY);
    }

    /**
     * 判断下标是否在数据列表范围内
     *
     * @param position 下标
     * @return 是否在数据列表范围内
     */
    public boolean isPositionInRange(int position) {
        return position >= 0 && position < mDataList.size();
    }

    /**
     * 获取是否显示分割线
     *
     * @return 是否显示分割线
     */
    public boolean isShowDivider() {
        return isShowDivider;
    }

    /**
     * 设置是否显示分割线
     *
     * @param isShowDivider 是否显示分割线
     */
    public void setShowDivider(boolean isShowDivider) {
        if (this.isShowDivider == isShowDivider) {
            return;
        }
        this.isShowDivider = isShowDivider;
        invalidate();
    }

    /**
     * 获取分割线颜色
     *
     * @return 分割线颜色 ColorInt
     */
    public int getDividerColor() {
        return mDividerColor;
    }

    /**
     * 设置分割线颜色
     *
     * @param dividerColorRes 分割线颜色 {@link ColorRes}
     */
    public void setDividerColorRes(@ColorRes int dividerColorRes) {
        setDividerColor(ContextCompat.getColor(getContext(), dividerColorRes));
    }

    /**
     * 设置分割线颜色
     *
     * @param dividerColor 分割线颜色 {@link ColorInt}
     */
    public void setDividerColor(@ColorInt int dividerColor) {
        if (mDividerColor == dividerColor) {
            return;
        }
        mDividerColor = dividerColor;
        invalidate();
    }

    /**
     * 获取分割线高度
     *
     * @return 分割线高度
     */
    public float getDividerHeight() {
        return mDividerSize;
    }

    /**
     * 设置分割线高度
     *
     * @param dividerHeight 分割线高度
     */
    public void setDividerHeight(float dividerHeight) {
        setDividerHeight(dividerHeight, false);
    }

    /**
     * 设置分割线高度
     *
     * @param dividerHeight 分割线高度
     * @param isDp          单位是否是 dp
     */
    public void setDividerHeight(float dividerHeight, boolean isDp) {
        float tempDividerHeight = mDividerSize;
        mDividerSize = isDp ? dp2px(dividerHeight) : dividerHeight;
        if (tempDividerHeight == mDividerSize) {
            return;
        }
        invalidate();
    }

    /**
     * 获取分割线填充类型
     *
     * @return 分割线填充类型
     * {@link #DIVIDER_TYPE_FILL}
     * {@link #DIVIDER_TYPE_WRAP}
     */
    public int getDividerType() {
        return mDividerType;
    }

    /**
     * 设置分割线填充类型
     *
     * @param dividerType 分割线填充类型
     *                    {@link #DIVIDER_TYPE_FILL}
     *                    {@link #DIVIDER_TYPE_WRAP}
     */
    public void setDividerType(@DividerType int dividerType) {
        if (mDividerType == dividerType) {
            return;
        }
        mDividerType = dividerType;
        invalidate();
    }

    /**
     * 获取自适应分割线类型时的分割线内边距
     *
     * @return 分割线内边距
     */
    public float getDividerPaddingForWrap() {
        return mDividerPaddingForWrap;
    }

    /**
     * 设置自适应分割线类型时的分割线内边距
     *
     * @param dividerPaddingForWrap 分割线内边距
     */
    public void setDividerPaddingForWrap(float dividerPaddingForWrap) {
        setDividerPaddingForWrap(dividerPaddingForWrap, false);
    }

    /**
     * 设置自适应分割线类型时的分割线内边距
     *
     * @param dividerPaddingForWrap 分割线内边距
     * @param isDp                  单位是否是 dp
     */
    public void setDividerPaddingForWrap(float dividerPaddingForWrap, boolean isDp) {
        float tempDividerPadding = mDividerPaddingForWrap;
        mDividerPaddingForWrap = isDp ? dp2px(dividerPaddingForWrap) : dividerPaddingForWrap;
        if (tempDividerPadding == mDividerPaddingForWrap) {
            return;
        }
        invalidate();
    }

    /**
     * 获取分割线两端形状
     *
     * @return 分割线两端形状
     * {@link Paint.Cap#BUTT}
     * {@link Paint.Cap#ROUND}
     * {@link Paint.Cap#SQUARE}
     */
    public Paint.Cap getDividerCap() {
        return mDividerCap;
    }

    /**
     * 设置分割线两端形状
     *
     * @param dividerCap 分割线两端形状
     *                   {@link Paint.Cap#BUTT}
     *                   {@link Paint.Cap#ROUND}
     *                   {@link Paint.Cap#SQUARE}
     */
    public void setDividerCap(Paint.Cap dividerCap) {
        if (mDividerCap == dividerCap) {
            return;
        }
        mDividerCap = dividerCap;
        invalidate();
    }

    /**
     * 获取是否绘制选中区域
     *
     * @return 是否绘制选中区域
     */
    public boolean isDrawSelectedRect() {
        return isDrawSelectedRect;
    }

    /**
     * 设置是否绘制选中区域
     *
     * @param isDrawSelectedRect 是否绘制选中区域
     */
    public void setDrawSelectedRect(boolean isDrawSelectedRect) {
        this.isDrawSelectedRect = isDrawSelectedRect;
        invalidate();
    }

    /**
     * 获取选中区域颜色
     *
     * @return 选中区域颜色 ColorInt
     */
    public int getSelectedRectColor() {
        return mSelectedRectColor;
    }

    /**
     * 设置选中区域颜色
     *
     * @param selectedRectColorRes 选中区域颜色 {@link ColorRes}
     */
    public void setSelectedRectColorRes(@ColorRes int selectedRectColorRes) {
        setSelectedRectColor(ContextCompat.getColor(getContext(), selectedRectColorRes));
    }

    /**
     * 设置选中区域颜色
     *
     * @param selectedRectColor 选中区域颜色 {@link ColorInt}
     */
    public void setSelectedRectColor(@ColorInt int selectedRectColor) {
        mSelectedRectColor = selectedRectColor;
        invalidate();
    }

    /**
     * 获取是否是弯曲（3D）效果
     *
     * @return 是否是弯曲（3D）效果
     */
    public boolean isCurved() {
        return isCurved;
    }

    /**
     * 设置是否是弯曲（3D）效果
     *
     * @param isCurved 是否是弯曲（3D）效果
     */
    public void setCurved(boolean isCurved) {
        if (this.isCurved == isCurved) {
            return;
        }
        this.isCurved = isCurved;
        calculateTextSize();
        requestLayout();
        invalidate();
    }

    /**
     * 获取弯曲（3D）效果左右圆弧效果方向
     *
     * @return 左右圆弧效果方向
     * {@link #CURVED_ARC_DIRECTION_LEFT}
     * {@link #CURVED_ARC_DIRECTION_CENTER}
     * {@link #CURVED_ARC_DIRECTION_RIGHT}
     */
    public int getCurvedArcDirection() {
        return mCurvedArcDirection;
    }

    /**
     * 设置弯曲（3D）效果左右圆弧效果方向
     *
     * @param curvedArcDirection 左右圆弧效果方向
     *                           {@link #CURVED_ARC_DIRECTION_LEFT}
     *                           {@link #CURVED_ARC_DIRECTION_CENTER}
     *                           {@link #CURVED_ARC_DIRECTION_RIGHT}
     */
    public void setCurvedArcDirection(@CurvedArcDirection int curvedArcDirection) {
        if (mCurvedArcDirection == curvedArcDirection) {
            return;
        }
        mCurvedArcDirection = curvedArcDirection;
        invalidate();
    }

    /**
     * 获取弯曲（3D）效果左右圆弧偏移效果方向系数
     *
     * @return 左右圆弧偏移效果方向系数
     */
    public float getCurvedArcDirectionFactor() {
        return mCurvedArcDirectionFactor;
    }

    /**
     * 设置弯曲（3D）效果左右圆弧偏移效果方向系数
     *
     * @param curvedArcDirectionFactor 左右圆弧偏移效果方向系数
     *                                 range 0.0-1.0 越大越明显
     */
    public void setCurvedArcDirectionFactor(@FloatRange(from = 0.0f, to = 1.0f) float curvedArcDirectionFactor) {
        if (mCurvedArcDirectionFactor == curvedArcDirectionFactor) {
            return;
        }
        if (curvedArcDirectionFactor < 0) {
            curvedArcDirectionFactor = 0f;
        } else if (curvedArcDirectionFactor > 1) {
            curvedArcDirectionFactor = 1f;
        }
        mCurvedArcDirectionFactor = curvedArcDirectionFactor;
        invalidate();
    }

    /**
     * 获取折射偏移比例
     *
     * @return 折射偏移比例
     */
    public float getRefractRatio() {
        return mRefractRatio;
    }

    /**
     * 设置选中条目折射偏移比例
     *
     * @param refractRatio 折射偏移比例 range 0.0-1.0
     */
    public void setRefractRatio(@FloatRange(from = 0.0f, to = 1.0f) float refractRatio) {
        float tempRefractRatio = mRefractRatio;
        mRefractRatio = refractRatio;
        if (mRefractRatio > 1f) {
            mRefractRatio = 1.0f;
        } else if (mRefractRatio < 0f) {
            mRefractRatio = DEFAULT_REFRACT_RATIO;
        }
        if (tempRefractRatio == mRefractRatio) {
            return;
        }
        invalidate();
    }

    @Deprecated
    public float getCurvedRefractRatio() {
        return mRefractRatio;
    }

    @Deprecated
    public void setCurvedRefractRatio(@FloatRange(from = 0.0f, to = 1.0f) float refractRatio) {
        setRefractRatio(refractRatio);
    }

    /**
     * 获取选中监听
     *
     * @return 选中监听器
     */
    public OnItemSelectedListener<T> getOnItemSelectedListener() {
        return mOnItemSelectedListener;
    }

    /**
     * 设置选中监听
     *
     * @param onItemSelectedListener 选中监听器
     */
    public void setOnItemSelectedListener(OnItemSelectedListener<T> onItemSelectedListener) {
        mOnItemSelectedListener = onItemSelectedListener;
    }

    /**
     * 获取滚动变化监听
     *
     * @return 滚动变化监听器
     */
    public OnWheelChangedListener getOnWheelChangedListener() {
        return mOnWheelChangedListener;
    }

    /**
     * 设置滚动变化监听
     *
     * @param onWheelChangedListener 滚动变化监听器
     */
    public void setOnWheelChangedListener(OnWheelChangedListener onWheelChangedListener) {
        mOnWheelChangedListener = onWheelChangedListener;
    }

    /*
      --------- 滚动变化方法同监听器方法（适用于子类） ------
     */

    /**
     * WheelView 滚动
     *
     * @param scrollOffsetY 滚动偏移
     */
    protected void onWheelScroll(float scrollOffsetY) {

    }

    /**
     * WheelView 条目变化
     *
     * @param oldPosition 旧的下标
     * @param newPosition 新下标
     */
    protected void onWheelItemChanged(int oldPosition, int newPosition) {

    }

    /**
     * WheelView 选中
     *
     * @param position 选中的下标
     */
    protected void onWheelSelected(int position) {

    }

    /**
     * WheelView 滚动状态
     *
     * @param state 滚动状态
     *              {@link WheelView#SCROLL_STATE_IDLE}
     *              {@link WheelView#SCROLL_STATE_DRAGGING}
     *              {@link WheelView#SCROLL_STATE_SCROLLING}
     */
    protected void onWheelScrollStateChanged(int state) {

    }

    /**
     * 条目选中回调
     *
     * @param data     选中的数据
     * @param position 选中的下标
     */
    protected void onItemSelected(T data, int position) {

    }

    /*
      --------- 滚动变化方法同监听器方法（适用于子类） ------
     */

    /**
     * dp转换px
     *
     * @param dp dp值
     * @return 转换后的px值
     */
    protected static float dp2px(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }

    /**
     * sp转换px
     *
     * @param sp sp值
     * @return 转换后的px值
     */
    protected static float sp2px(float sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, Resources.getSystem().getDisplayMetrics());
    }

    /**
     * 自定义文字对齐方式注解
     * <p>
     * {@link #mTextAlign}
     * {@link #setTextAlign(int)}
     */
    @IntDef({TEXT_ALIGN_LEFT, TEXT_ALIGN_CENTER, TEXT_ALIGN_RIGHT})
    @Retention(value = RetentionPolicy.SOURCE)
    public @interface TextAlign {
    }

    /**
     * 自定义左右圆弧效果方向注解
     * <p>
     * {@link #mCurvedArcDirection}
     * {@link #setCurvedArcDirection(int)}
     */
    @IntDef({CURVED_ARC_DIRECTION_LEFT, CURVED_ARC_DIRECTION_CENTER, CURVED_ARC_DIRECTION_RIGHT})
    @Retention(value = RetentionPolicy.SOURCE)
    public @interface CurvedArcDirection {
    }

    /**
     * 自定义分割线类型注解
     * <p>
     * {@link #mDividerType}
     * {@link #setDividerType(int)}
     */
    @IntDef({DIVIDER_TYPE_FILL, DIVIDER_TYPE_WRAP})
    @Retention(value = RetentionPolicy.SOURCE)
    public @interface DividerType {
    }

    /**
     * 条目选中监听器
     *
     * @param <T>
     */
    public interface OnItemSelectedListener<T> {

        /**
         * 条目选中回调
         *
         * @param wheelView wheelView
         * @param data      选中的数据
         * @param position  选中的下标
         */
        void onItemSelected(WheelView<T> wheelView, T data, int position);
    }

    /**
     * WheelView滚动状态改变监听器
     */
    public interface OnWheelChangedListener {

        /**
         * WheelView 滚动
         *
         * @param scrollOffsetY 滚动偏移
         */
        void onWheelScroll(int scrollOffsetY);

        /**
         * WheelView 条目变化
         *
         * @param oldPosition 旧的下标
         * @param newPosition 新下标
         */
        void onWheelItemChanged(int oldPosition, int newPosition);

        /**
         * WheelView 选中
         *
         * @param position 选中的下标
         */
        void onWheelSelected(int position);

        /**
         * WheelView 滚动状态
         *
         * @param state 滚动状态
         *              {@link WheelView#SCROLL_STATE_IDLE}
         *              {@link WheelView#SCROLL_STATE_DRAGGING}
         *              {@link WheelView#SCROLL_STATE_SCROLLING}
         */
        void onWheelScrollStateChanged(int state);
    }

    /**
     * SoundPool 辅助类
     */
    private static class SoundHelper {

        private SoundPool mSoundPool;
        private int mSoundId;
        private float mPlayVolume;

        @SuppressWarnings("deprecation")
        private SoundHelper() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mSoundPool = new SoundPool.Builder().build();
            } else {
                mSoundPool = new SoundPool(1, AudioManager.STREAM_SYSTEM, 1);
            }
        }

        /**
         * 初始化 SoundHelper
         *
         * @return SoundHelper 对象
         */
        static SoundHelper obtain() {
            return new SoundHelper();
        }

        /**
         * 加载音频资源
         *
         * @param context 上下文
         * @param resId   音频资源 {@link RawRes}
         */
        void load(Context context, @RawRes int resId) {
            if (mSoundPool != null) {
                mSoundId = mSoundPool.load(context, resId, 1);
            }
        }

        /**
         * 设置音量
         *
         * @param playVolume 音频播放音量 range 0.0-1.0
         */
        void setPlayVolume(@FloatRange(from = 0.0, to = 1.0) float playVolume) {
            this.mPlayVolume = playVolume;
        }

        /**
         * 获取音量
         *
         * @return 音频播放音量 range 0.0-1.0
         */
        float getPlayVolume() {
            return mPlayVolume;
        }

        /**
         * 播放声音效果
         */
        void playSoundEffect() {
            if (mSoundPool != null && mSoundId != 0) {
                mSoundPool.play(mSoundId, mPlayVolume, mPlayVolume, 1, 0, 1);
            }
        }

        /**
         * 释放SoundPool
         */
        void release() {
            if (mSoundPool != null) {
                mSoundPool.release();
                mSoundPool = null;
            }
        }
    }

    public boolean isYearDays() {
        return mYearDays;
    }

    public void setYearDays(Calendar calendar, boolean yearDays) {
        this.mCalendar = calendar;
        this.mYearDays = yearDays;
    }

    public void setHourWheel(boolean hourWheel) {
        mHourWheel = hourWheel;
    }

    public void setMinuteWheel(boolean minuteWheel) {
        mMinuteWheel = minuteWheel;
    }

    public void set24HoursFormat(boolean hoursFormat) {
        mIs24Format = hoursFormat;
    }

    public void setMonthList(List<String> monthList) {
        mMonthList = monthList;
    }

    private String dayOfYearToMonthDay(String dayOfYearStr) {
        if (mCalendar == null) {
            return null;
        }
        int dayOfYear = Integer.parseInt(dayOfYearStr);
        if (mTempCalendar == null) {
            mTempCalendar = Calendar.getInstance();
        }
        mTempCalendar.set(Calendar.YEAR, mCalendar.get(Calendar.YEAR));
        mTempCalendar.set(Calendar.DAY_OF_YEAR, dayOfYear);
        int month = mTempCalendar.get(Calendar.MONTH);
        String result = "";
        if (mMonthList != null && mMonthList.size() > month) {
            int day = mTempCalendar.get(Calendar.DAY_OF_MONTH);
            if (mIsRtl) {
                result = String.format("%d", day) + " " + mMonthList.get(month);
            } else {
                result = mMonthList.get(month) + " " + String.format("%d", day);
            }
        }
        return result;
    }

    public void setWheelBackgroundColor(int color) {
        mBgColor = color;
    }

    private void resizeTextSize(Paint paint, String text) {
        if (!TextUtils.isEmpty(text)) {
            int width = getWidth();
            if (width == 0) {
                return;
            }
            mTextPaint.setTextSize(paint.getTextSize());
            int itemWidth = (int) Math.ceil(Layout.getDesiredWidth(text, mTextPaint));
            if (itemWidth > width) {
                mPaint.setTextSize(mPaint.getTextSize() - mDensity);
                resizeTextSize(paint, text);
            }
        }
    }
}