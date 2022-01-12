package com.suheng.structure.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class LetterSelectorLayout extends FrameLayout {
    public static final String TAG = LetterSelectorLayout.class.getSimpleName();
    public static final String[] LETTERS = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N"
            , "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13"
            , "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27"/**/};

    private float mMarginTop, mMarginRight, mPadding, mBubbleOffsetX;

    private final ArrayMap<RectF, String> mArrayMap = new ArrayMap<>();
    private final RectF mRectFTotal = new RectF();
    private final RectF mRectFBubble = new RectF();
    private final List<RectF> mRectFList = new ArrayList<>();

    private Paint mPaint, mPaintSelected;
    private String mSelectedLetter;
    private int mSelectedLetterPosition = -1;

    private float mLetterCentreY, mSelectedLetterCentreY;
    private Bitmap mBitmapBubble;
    private boolean mIsOnTouch;
    private boolean mIsVerticalCentre;
    private List<String> mLetters;
    private OnTouchLetterListener mOnTouchLetterListener;
    private boolean mIsLastVisibleItemPosition;
    private boolean mIsOverUnits;
    private Runnable mRunnableScrollToBottom, mRunnableHideBubble;

    public LetterSelectorLayout(Context context) {
        super(context);
        this.init();
    }

    public LetterSelectorLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public LetterSelectorLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    private void init() {
        setWillNotDraw(false);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        mPaint = new Paint();
        mPaint.setColor(Color.BLUE);
        mPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, metrics));
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setStrokeWidth(4);
        mLetterCentreY = (mPaint.descent() + mPaint.ascent()) / 2f;

        mPaintSelected = new Paint(mPaint);
        mPaintSelected.setTypeface(Typeface.DEFAULT_BOLD);
        mPaintSelected.setTextSize(mPaint.getTextSize() * 2);
        mPaintSelected.setTextAlign(Paint.Align.CENTER);
        mSelectedLetterCentreY = (mPaintSelected.descent() + mPaintSelected.ascent()) / 2f;

        mMarginTop = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, metrics);
        mMarginRight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 2, metrics);
        mPadding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, metrics);
        mBubbleOffsetX = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, metrics);

        Drawable bubble = ContextCompat.getDrawable(getContext(), R.drawable.bubble);
        if (bubble != null) {
            final int intrinsicWidth = bubble.getIntrinsicWidth();
            final int intrinsicHeight = bubble.getIntrinsicHeight();
            mBitmapBubble = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(mBitmapBubble);
            bubble.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            bubble.draw(canvas);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG, "onSizeChanged: " + w + ", " + h + ", " + getWidth() + ", " + getHeight());
        this.calcLetterLayout();
    }

    private int mWidth, mHeight;

    private void calcLetterLayout() {
        int width = getWidth();
        int height = getHeight();
        if (mLetters == null || mLetters.size() == 0 || width <= 0 || height <= 0) {
            return;
        }

        if (mWidth != width) {
            mWidth = width;
        }

        if (mHeight != height) {
            mHeight = height;
        }

        mArrayMap.clear();
        mRectFList.clear();

        final String text = mLetters.get(0);
        Rect rect = new Rect();
        mPaint.getTextBounds(text, 0, text.length(), rect);

        RectF rectF = new RectF();
        rectF.top = mMarginTop;
        rectF.bottom = rectF.top + rect.height() + mPadding;
        /*rectF.left = mMarginRight;
        rectF.right = rectF.left + rect.width() + mPadding * 2.2f;*/
        rectF.right = mWidth - mMarginRight;
        rectF.left = rectF.right - rect.width() - mPadding * 2.2f;

        float unitHeight = rectF.height();
        int length = mLetters.size();
        if (mIsVerticalCentre) {
            float letterPanelHeight = unitHeight * length;
            mMarginTop = (mHeight - letterPanelHeight) / 2;
            if (mMarginTop < unitHeight) {
                mMarginTop = unitHeight;
            }
            rectF.top = mMarginTop;
            rectF.bottom = rectF.top + unitHeight;
        }

        float remainHeight = mHeight - mMarginTop;
        int units = (int) (remainHeight / unitHeight) - 1;
        //Log.d(TAG, "calcLetterLayout, units: " + units + ", remainHeight: " + remainHeight + ", h: " + mHeight);
        if (units <= 0) {
            return;
        }
        mIsOverUnits = length > units;

        /*Log.d(TAG, "draw: " + rect.toShortString() + ", " + rect.width() + "---" + rect.height() + "\n"
                + rectF.toShortString() + ", " + rectF.width() + "---" + unitHeight + ", " + rectF.centerX() + "---" + rectF.centerY());*/

        mRectFTotal.set(rectF);

        int paintLen = mIsOverUnits ? units : length;
        for (int i = 0; i < paintLen; i++) {
            RectF f = new RectF(rectF.left, rectF.top, rectF.right, rectF.bottom);
            mRectFList.add(f);
            rectF.top = rectF.bottom;
            rectF.bottom += unitHeight;

            mArrayMap.put(f, mLetters.get(i));
        }

        if (mRectFList.size() == 0) {
            return;
        }

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int topOffset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70, metrics);
        int rightOffset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, metrics);
        RectF src = mRectFList.get(0);
        float top = src.top + topOffset;
        float right = src.left - rightOffset;
        mRectFBubble.set(right - mBitmapBubble.getWidth(), top, right, top + mBitmapBubble.getHeight());

        mRectFTotal.bottom = rectF.top /*+ mPadding * 1.5f*/;
        //mRectFTotal.top = mRectFTotal.top - mPadding * 1.5f;


        invalidate();
    }

    /*private int valueToDimen(int unit, int value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }*/

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (mLetters == null || mLetters.size() == 0 || mRectFList.size() == 0) {
            return;
        }

        /*canvas.save();
        if (mIsOnTouch) {
            Log.i(TAG, "draw, mSelectedLetter: " + mSelectedLetter);
            canvas.scale(mScale, mScale, 10, 10);
            canvas.drawBitmap(mBitmapBubble, left, top, null);
            canvas.drawPoint(10,10, mPaint);
            canvas.drawText(mSelectedLetter, left + width / 2.3f, rectF.centerY() - mSelectedLetterCentreY, mPaintSelected);
        }
        canvas.restore();*/

        //mPaint.setColor(Color.RED);
        //canvas.drawRect(mRectFTotal, mPaint);

        final int cor = 255 / mRectFList.size();
        Log.d(TAG, "draw, cor: " + cor + ", " + mRectFList.size());
        for (int i = 0; i < mRectFList.size(); i++) {
            RectF rectF = mRectFList.get(i);
            mPaint.setColor(Color.rgb(cor * i, cor * i, cor * i));
            canvas.drawRect(rectF, mPaint);

            String letter = mArrayMap.get(rectF);
            //if (letter != null && letter.equals(mSelectedLetter)) {
            if (letter != null && letter.equals(mSelectedLetter)) {
                mPaint.setColor(Color.BLUE);
                canvas.drawText(mSelectedLetter, rectF.centerX(), rectF.centerY() - mLetterCentreY, mPaint);

                int width = mBitmapBubble.getWidth();
                float left = rectF.left - width - mBubbleOffsetX;
                float top = rectF.centerY() - mBitmapBubble.getHeight() / 2f;

                if (mIsOnTouch) {
                    Log.i(TAG, "draw, mSelectedLetter: " + mSelectedLetter);
                    canvas.scale(mScale, mScale, left + width / 2f, rectF.centerY());
                    canvas.drawBitmap(mBitmapBubble, left, top, null);
                    canvas.drawPoint(left + width / 2f, rectF.centerY(), mPaint);
                    canvas.drawText(mSelectedLetter, left + width / 2.3f, rectF.centerY() - mSelectedLetterCentreY, mPaintSelected);
                }
            } else {
                mPaint.setColor(Color.WHITE);
                canvas.drawText(letter, rectF.centerX(), rectF.centerY() - mLetterCentreY, mPaint);
            }
        }

        Log.d(TAG, "draw, mSelectedLetter: " + getVisibility());
        //if (mIsOnTouch && !TextUtils.isEmpty(mSelectedLetter)) {
        if (mSelectedLetter != null) {
            //canvas.drawRect(mRectFBubble, mPaint);
            canvas.drawText(mSelectedLetter, mRectFBubble.centerX(), mRectFBubble.centerY() - mSelectedLetterCentreY, mPaintSelected);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (mRectFTotal.contains(ev.getX(), ev.getY())) {
                requestDisallowInterceptTouchEvent(true);
                return true;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mLetters == null || mLetters.size() == 0) {
            return super.onTouchEvent(event);
        }

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //Log.d(TAG, "ACTION_DOWN, ACTION_DOWN, ACTION_DOWN");
                if (!mRectFTotal.contains(x, y)) {
                    return false;
                }

                mIsOnTouch = true;
                for (RectF rectF : mRectFList) {
                    if (rectF.contains(x, y)) {
                        String selectedLetter = mArrayMap.get(rectF);
                        int selectedLetterPosition = mLetters.indexOf(selectedLetter);
                        Log.d(TAG, "down: selected rectF: " + selectedLetterPosition + ", letter: " + selectedLetter);

                        this.handleTouchedLetter(selectedLetter, selectedLetterPosition);
                        break;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (!mRectFTotal.contains(x, y)) {
                    return true;
                }
                //Log.i(TAG, "ACTION_MOVE, ACTION_MOVE, ACTION_MOVE");

                for (RectF rectF : mRectFList) {
                    if (rectF.contains(x, y)) {
                        String selectedLetter = mArrayMap.get(rectF);
                        int selectedLetterPosition = mLetters.indexOf(selectedLetter);

                        if (mIsOverUnits) {
                            int rectPst = mRectFList.indexOf(rectF);
                            Log.d(TAG, "move: selected rectF: " + rectPst + ", letter: " + selectedLetter);

                            if ((rectPst == 0) && (selectedLetterPosition > 0)) {
                                int tmpPst = 0, topOffset = selectedLetterPosition - 1;
                                for (int i = topOffset; i < topOffset + mRectFList.size(); i++) {
                                    mArrayMap.put(mRectFList.get(tmpPst), mLetters.get(i));
                                    tmpPst++;
                                }
                            }

                            if ((rectPst == mRectFList.size() - 1) && (selectedLetterPosition < mLetters.size() - 1)) {
                                int topLetterIndex = mLetters.indexOf(mArrayMap.get(mRectFList.get(0)));
                                int tmpPst = 0, topOffset = topLetterIndex + 1;
                                for (int i = topOffset; i < mRectFList.size() + topOffset; i++) {
                                    mArrayMap.put(mRectFList.get(tmpPst), mLetters.get(i));
                                    tmpPst++;
                                }
                            }
                        }

                        this.handleTouchedLetter(selectedLetter, selectedLetterPosition);

                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                //Log.v(TAG, "ACTION_UP, ACTION_UP, ACTION_UP");
                if (mIsOnTouch && getHandler() != null) {
                    if (mRunnableHideBubble == null) {
                        mRunnableHideBubble = () -> {
                            mIsOnTouch = false;
                            invalidate();
                        };
                    }

                    Handler handler = getHandler();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        if (handler.hasCallbacks(mRunnableHideBubble)) {
                            handler.removeCallbacks(mRunnableHideBubble);
                        }
                    } else {
                        handler.removeCallbacks(mRunnableHideBubble);
                    }
                    handler.postDelayed(mRunnableHideBubble, 1000);
                }
                break;
        }

        return true;
    }

    private void handleTouchedLetter(String selectedLetter, int selectedLetterPosition) {
        if (mIsLastVisibleItemPosition) {
            if (selectedLetterPosition < mSelectedLetterPosition) {
                this.setSelectedLetter(selectedLetter);

                if (mOnTouchLetterListener != null) {
                    mOnTouchLetterListener.onTouchLetter(selectedLetter, selectedLetterPosition);
                }
            }
        } else {
            this.setSelectedLetter(selectedLetter);

            if (mOnTouchLetterListener != null) {
                mOnTouchLetterListener.onTouchLetter(selectedLetter, selectedLetterPosition);
            }
        }
    }

    private float mScale = 0.5f;

    @Override
    public void onVisibilityAggregated(boolean isVisible) {
        super.onVisibilityAggregated(isVisible);
        Log.v(TAG, "onVisibilityAggregated: " + isVisible);
        /*Handler handler = getHandler();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (handler.hasCallbacks(mRunnableHideBubble)) {
                handler.removeCallbacks(mRunnableHideBubble);
            }
        } else {
            handler.removeCallbacks(mRunnableHideBubble);
        }*/

        //mSelectedLetter = "";
        /*if (!isVisible) {
            //mIsOnTouch = true;
            mSelectedLetter = "";
            invalidate();
        }*/

        mSelectedLetter = "";
        invalidate();
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        //Log.d(TAG, "onVisibilityChanged: " + visibility);
        /*if (visibility != VISIBLE) {
            mIsOnTouch = true;
            mScale = 0;
            invalidate();
        }*/
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        //Log.i(TAG, "onWindowVisibilityChanged: " + visibility);
        /*if (visibility != VISIBLE) {
            mIsOnTouch = true;
            mScale = 0;
            invalidate();
        }*/
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        //Log.d(TAG, "onWindowFocusChanged: " + hasWindowFocus);
        /*if (!hasWindowFocus) {
            mIsOnTouch = true;
            mScale = 0;
            invalidate();
        }*/
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mArrayMap.clear();
        if (mBitmapBubble != null && !mBitmapBubble.isRecycled()) {
            mBitmapBubble.recycle();
        }

        if (mRunnableScrollToBottom != null) {
            getHandler().removeCallbacks(mRunnableScrollToBottom);
        }
    }

    public void setLetters(List<String> letters) {
        mLetters = letters;
    }

    public void setSelectedLetter(String selectedLetter) {
        Log.d(TAG, "setSelectedLetter, selected equal letter: " + selectedLetter + ", pst: " + mSelectedLetterPosition + ", to bottom:" + mIsLastVisibleItemPosition);
        if (mLetters == null || selectedLetter == null || selectedLetter.equals(mSelectedLetter)) {
            return;
        }

        int selectedLetterPosition = mLetters.indexOf(selectedLetter);

        if (mIsOverUnits) {
            this.handleOverUnitsPst(selectedLetter, selectedLetterPosition);
        }

        mSelectedLetter = selectedLetter;
        mSelectedLetterPosition = selectedLetterPosition;
        Log.d(TAG, "setSelectedLetter, selectedLetter: " + selectedLetter + ", selectedLetterPosition: " + mSelectedLetterPosition);
        mScale = 1;
        invalidate();
    }

    private void handleOverUnitsPst(String selectedLetter, int selectedLetterPosition) {
        boolean isShowOnPanel = false;
        for (RectF rectF : mRectFList) {
            String letter = mArrayMap.get(rectF);
            if (selectedLetter.equals(letter)) {
                isShowOnPanel = true;
                break;
            }
        }

        if (isShowOnPanel) {
            return;
        }

        int index = mLetters.indexOf(selectedLetter);
        if (mSelectedLetterPosition < selectedLetterPosition) {
            Log.v(TAG, "setSelectedLetter, down scroll: " + selectedLetter + ", " + selectedLetterPosition + "--" + mSelectedLetterPosition);
            int pst = mRectFList.size() - 1;
            while (index >= 0 && pst >= 0) {
                mArrayMap.put(mRectFList.get(pst), mLetters.get(index));
                index--;
                pst--;
            }

            /*if (mRunnableScrollToBottom == null) {
                mRunnableScrollToBottom = () -> {
                    String letter = mArrayMap.get(mRectFList.get(mRectFList.size() - 1));
                    if (letter != null && !letter.equals(mLetters.get(mLetters.size() - 1))) {
                        int topLetterIndex = mLetters.indexOf(mArrayMap.get(mRectFList.get(0)));
                        int tmpPst = 0, topOffset = topLetterIndex + 1;
                        for (int i = topOffset; i < mRectFList.size() + topOffset; i++) {
                            mArrayMap.put(mRectFList.get(tmpPst), mLetters.get(i));
                            tmpPst++;
                        }

                        getHandler().postDelayed(mRunnableScrollToBottom, 40);
                    }

                    postInvalidate();
                };
            }
            getHandler().post(mRunnableScrollToBottom);*/
        } else {
            Log.i(TAG, "setSelectedLetter, up scroll: " + selectedLetter + ", " + selectedLetterPosition + "--" + mSelectedLetterPosition);
            int pst = 0;
            while (index < mLetters.size() && pst < mRectFList.size()) {
                mArrayMap.put(mRectFList.get(pst), mLetters.get(index));
                index++;
                pst++;
            }
        }
    }

    public void setLastVisibleItemPosition(int lastVisibleItemPosition) {
        mIsLastVisibleItemPosition = (mLetters != null && lastVisibleItemPosition == (mLetters.size() - 1));
    }

    public void setOnTouchLetterListener(OnTouchLetterListener onTouchLetterListener) {
        mOnTouchLetterListener = onTouchLetterListener;
    }

    public void setVerticalCentre(boolean verticalCentre) {
        mIsVerticalCentre = verticalCentre;
    }

    public interface OnTouchLetterListener {
        void onTouchLetter(String letter, int pst);
    }
}
