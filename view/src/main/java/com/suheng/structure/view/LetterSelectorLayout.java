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
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class LetterSelectorLayout extends FrameLayout {
    public static final String TAG = LetterSelectorLayout.class.getSimpleName();
    public static final String[] LETTERS = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N"
            , "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13"/**/};
    private float mMarginTop, mMarginRight, mPadding, mBubbleOffsetX;
    private final ArrayMap<RectF, String> mArrayMap = new ArrayMap<>();
    private final RectF mRectFTotal = new RectF();
    private Paint mPaint, mPaintSelected;
    private String mSelectedLetter;
    private int mSelectedPosition;
    private float mLetterCentreY, mSelectedLetterCentreY;
    private Bitmap mBitmapBubble;
    private boolean mIsOnTouch;
    private boolean mIsVerticalCentre;
    private List<String> mLetters;
    private OnTouchLetterListener mOnTouchLetterListener;
    private boolean mIsLastVisibleItemPosition;
    private final List<RectF> mRectFList = new ArrayList<>();
    private boolean mIsOverUnits;
    private Runnable mRunnableScrollToBottom;

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
        Log.d("LetterSelectActivity", "onSizeChanged: ");
        if (mLetters == null || mLetters.size() == 0) {
            return;
        }

        mArrayMap.clear();

        final String text = mLetters.get(0);
        Rect rect = new Rect();
        mPaint.getTextBounds(text, 0, text.length(), rect);

        RectF rectF = new RectF();
        rectF.top = mMarginTop;
        rectF.bottom = rectF.top + rect.height() + mPadding;
        /*rectF.left = mMarginRight;
        rectF.right = rectF.left + rect.width() + mPadding * 2.2f;*/
        rectF.right = w - mMarginRight;
        rectF.left = rectF.right - rect.width() - mPadding * 2.2f;

        float unitHeight = rectF.height();
        int length = mLetters.size();
        if (mIsVerticalCentre) {
            float letterPanelHeight = unitHeight * length;
            mMarginTop = (h - letterPanelHeight) / 2;
            if (mMarginTop < unitHeight) {
                mMarginTop = unitHeight;
            }
            rectF.top = mMarginTop;
            rectF.bottom = rectF.top + unitHeight;
        }

        float remainHeight = h - mMarginTop;
        int units = (int) (remainHeight / unitHeight);
        Log.d(TAG, "draw, units: " + units + ", remainHeight: " + remainHeight + ", h: " + h);
        mIsOverUnits = length > units;

        /*Log.d(TAG, "draw: " + rect.toShortString() + ", " + rect.width() + "---" + rect.height() + "\n"
                + rectF.toShortString() + ", " + rectF.width() + "---" + unitHeight + ", " + rectF.centerX() + "---" + rectF.centerY());*/

        mRectFTotal.set(rectF);

        mRectFList.clear();
        mArrayMap.clear();
        int paintLen = mIsOverUnits ? units : length;
        for (int i = 0; i < paintLen; i++) {
            RectF f = new RectF(rectF.left, rectF.top, rectF.right, rectF.bottom);
            mRectFList.add(f);
            rectF.top = rectF.bottom;
            rectF.bottom += unitHeight;

            mArrayMap.put(f, mLetters.get(i));
        }

        mRectFTotal.bottom = rectF.top;

        invalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (mLetters == null || mLetters.size() == 0) {
            return;
        }

        final int cor = 255 / mLetters.size();
        for (int i = 0; i < mRectFList.size(); i++) {
            RectF rectF = mRectFList.get(i);
            mPaint.setColor(Color.rgb(cor * i, cor * i, cor * i));
            canvas.drawRect(rectF, mPaint);

            String letter = mArrayMap.get(rectF);
            if (letter != null && letter.equals(mSelectedLetter)) {
                mPaint.setColor(Color.BLUE);
                canvas.drawText(mSelectedLetter, rectF.centerX(), rectF.centerY() - mLetterCentreY, mPaint);

                int width = mBitmapBubble.getWidth();
                float left = rectF.left - width - mBubbleOffsetX;
                float top = rectF.centerY() - mBitmapBubble.getHeight() / 2f;

                if (mIsOnTouch) {
                    canvas.drawBitmap(mBitmapBubble, left, top, null);
                    canvas.drawPoint(left + width / 2f, rectF.centerY(), mPaint);
                    canvas.drawText(mSelectedLetter, left + width / 2.3f, rectF.centerY() - mSelectedLetterCentreY, mPaintSelected);
                }
            } else {
                mPaint.setColor(Color.WHITE);
                canvas.drawText(letter, rectF.centerX(), rectF.centerY() - mLetterCentreY, mPaint);
            }
        }
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
                Log.d(TAG, "ACTION_DOWN, ACTION_DOWN, ACTION_DOWN");
                mIsOnTouch = true;

                if (mRectFTotal.contains(x, y)) {
                    for (RectF rectF : mRectFList) {
                        if (rectF.contains(x, y)) {
                            int pst = mRectFList.indexOf(rectF);
                            String selectedLetter = mArrayMap.get(rectF);
                            Log.i(TAG, "down: selectedLetter-" + pst + ", " + selectedLetter);
                            if (mIsLastVisibleItemPosition) {
                                Log.i(TAG, "down: selectedLetter-" + pst + ", " + mSelectedPosition);
                                if (pst > mSelectedPosition) {
                                    return false;
                                }
                            }

                            this.setSelectedLetter(selectedLetter);

                            if (mOnTouchLetterListener != null) {
                                mOnTouchLetterListener.onTouchLetter(selectedLetter, pst);
                            }
                            break;
                        }
                    }
                } else {
                    return false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (!mRectFTotal.contains(x, y)) {
                    return true;
                }
                Log.i(TAG, "ACTION_MOVE, ACTION_MOVE, ACTION_MOVE");

                for (RectF rectF : mRectFList) {
                    if (rectF.contains(x, y)) {
                        String selectedLetter = mArrayMap.get(rectF);
                        int pst = mRectFList.indexOf(rectF);
                        Log.d(TAG, "move: selectedLetter-" + pst + ", " + selectedLetter);

                        if (mIsOverUnits) {
                            int letterIndex = mLetters.indexOf(selectedLetter);

                            if (pst == 0) {
                                Log.d(TAG, "move to top, rect pst: " + pst + ", letter pst: " + letterIndex);
                                if (letterIndex > 0) {
                                    int tmpPst = 0, topOffset = letterIndex - 1;
                                    for (int i = topOffset; i < topOffset + mRectFList.size(); i++) {
                                        mArrayMap.put(mRectFList.get(tmpPst), mLetters.get(i));
                                        tmpPst++;
                                    }
                                }
                            }

                            if (pst == mRectFList.size() - 1) {
                                Log.d(TAG, "down bottom: " + pst + ", letter pst: " + letterIndex);
                                if (letterIndex < mLetters.size() - 1) {
                                    int topLetterIndex = mLetters.indexOf(mArrayMap.get(mRectFList.get(0)));
                                    int tmpPst = 0, topOffset = topLetterIndex + 1;
                                    for (int i = topOffset; i < mRectFList.size() + topOffset; i++) {
                                        mArrayMap.put(mRectFList.get(tmpPst), mLetters.get(i));
                                        tmpPst++;
                                    }
                                }
                            }
                        }

                        this.setSelectedLetter(mArrayMap.get(rectF));
                        invalidate();

                        if (mOnTouchLetterListener != null) {
                            mOnTouchLetterListener.onTouchLetter(mSelectedLetter, mSelectedPosition);
                        }

                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.v(TAG, "ACTION_UP, ACTION_UP, ACTION_UP");
                if (mIsOnTouch) {
                    postDelayed(() -> {
                        mIsOnTouch = false;
                        invalidate();
                    }, 300);
                }
                break;
        }

        return true;
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

    boolean scrollToTop;

    public void setSelectedLetter(String selectedLetter) {
        if (mLetters == null || selectedLetter == null || selectedLetter.equals(mSelectedLetter)) {
            return;
        }

        mSelectedLetter = selectedLetter;
        int selectedPosition = mLetters.indexOf(selectedLetter);
        Log.v(TAG, "setSelectedLetter: " + selectedLetter + ", " + mSelectedPosition);

        /*String topLetter = mArrayMap.get(mRectFList.get(0));
        if (scrollToTop) {
            if (selectedPosition < mSelectedPosition) {
                Log.i(TAG, "setSelectedLetter ,to top up");
                int topOffset = mLetters.indexOf(selectedLetter);
                int tmpPst = 0;
                for (int i = topOffset; i < topOffset + mRectFList.size(); i++) {
                    mArrayMap.put(mRectFList.get(tmpPst), mLetters.get(i));
                    tmpPst++;
                }

                invalidate();

                return;
            } else if (selectedPosition > mSelectedPosition) {
                Log.i(TAG, "setSelectedLetter ,to top down");
                scrollToTop = false;
            }
        } else {
            scrollToTop = mSelectedLetter.equals(topLetter);
        }*/

        mSelectedPosition = selectedPosition;

        if (mIsLastVisibleItemPosition) {
            Log.v(TAG, "setSelectedLetter ,to bottom");
            if (mIsOverUnits) {
                if (mRunnableScrollToBottom == null) {
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

                getHandler().post(mRunnableScrollToBottom);
            } else {
                invalidate();
            }
        } else {
            invalidate();
        }
    }

    public void setSelectedPosition(int position) {
        if (mLetters == null || position > mLetters.size() - 1) {
            return;
        }
        this.setSelectedLetter(mLetters.get(position));
    }

    public void setLastVisibleItemPosition(int lastVisibleItemPosition) {
        mIsLastVisibleItemPosition = (mLetters != null && lastVisibleItemPosition == (mLetters.size() - 1));
    }

    public void setOnTouchLetterListener(OnTouchLetterListener onTouchLetterListener) {
        mOnTouchLetterListener = onTouchLetterListener;
    }

    public interface OnTouchLetterListener {
        void onTouchLetter(String letter, int pst);
    }
}
