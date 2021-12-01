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
    private static final String TAG = LetterSelectorLayout.class.getSimpleName();
    private static final String[] LETTERS = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N"
            , "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"/**/};
    private float mMarginTop, mMarginRight, mPadding, mBubbleOffsetX;
    private final ArrayMap<String, RectF> mArrayMap = new ArrayMap<>();
    private final ArrayMap<RectF, String> mArrayMap2 = new ArrayMap<>();
    private final RectF mRectFTotal = new RectF();
    private Paint mPaint, mPaintSelected;
    private String mSelectedLetter;
    public float mLetterCentreY, mSelectedLetterCentreY;
    private Bitmap mBitmapBubble;
    private boolean mIsOnTouch;
    private boolean mIsVerticalCentre;

    public LetterSelectorLayout(Context context) {
        this(context, null);
    }

    public LetterSelectorLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LetterSelectorLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    private void init() {
        setBackgroundColor(Color.GRAY);
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
        mArrayMap.clear();

        final String text = LETTERS[0];
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
        int length = LETTERS.length;
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

        /*mArrayMap.clear();
        for (String letter : LETTERS) {
            mArrayMap.put(letter, new RectF(rectF.left, rectF.top, rectF.right, rectF.bottom));
            rectF.top = rectF.bottom;
            rectF.bottom += unitHeight;
        }*/

        mRectFList.clear();
        mArrayMap2.clear();
        int paintLen = mIsOverUnits ? units : length;
        for (int i = 0; i < paintLen; i++) {
            RectF f = new RectF(rectF.left, rectF.top, rectF.right, rectF.bottom);
            mRectFList.add(f);
            rectF.top = rectF.bottom;
            rectF.bottom += unitHeight;

            mArrayMap2.put(f, LETTERS[i]);
        }

        mRectFTotal.bottom = rectF.top;
    }

    private final List<RectF> mRectFList = new ArrayList<>();
    private boolean mIsOverUnits;

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        final int cor = 255 / LETTERS.length;
        /*for (int i = 0; i < LETTERS.length; i++) {
            String letter = LETTERS[i];
            RectF rectF = mArrayMap.get(letter);
            mPaint.setColor(Color.rgb(cor * i, cor * i, cor * i));
            canvas.drawRect(rectF, mPaint);

            if (letter.equals(mSelectedLetter)) {
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

            mPaint.setColor(Color.RED);
            canvas.drawPoint(rectF.centerX(), rectF.centerY(), mPaint);
        }*/

        for (int i = 0; i < mRectFList.size(); i++) {
            RectF rectF = mRectFList.get(i);
            mPaint.setColor(Color.rgb(cor * i, cor * i, cor * i));
            canvas.drawRect(rectF, mPaint);

            String letter = mArrayMap2.get(rectF);
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

    private int mOverUnitsOffset;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "ACTION_DOWN, ACTION_DOWN, ACTION_DOWN");
                mIsOnTouch = true;
                /*if (mRectFTotal.contains(x, y)) {
                    for (Map.Entry<String, RectF> entry : mArrayMap.entrySet()) {
                        RectF value = entry.getValue();
                        if (value.contains(x, y)) {
                            mSelectedLetter = entry.getKey();
                            Log.d(TAG, "move: selectedLetter-" + mSelectedLetter);
                            Log.v(TAG, "click: selectedLetter-" + entry.getValue());
                            break;
                        }
                    }

                    invalidate();
                } else {
                    return false;
                }*/

                if (mRectFTotal.contains(x, y)) {
                    for (RectF rectF : mRectFList) {
                        if (rectF.contains(x, y)) {
                            mSelectedLetter = mArrayMap2.get(rectF);
                            int index = mRectFList.indexOf(rectF);
                            Log.i(TAG, "down: selectedLetter-" + index);
                            if (index == 0) {
                                Log.d(TAG, "down top: " + index);
                            }

                            if (index == mRectFList.size() - 1) {
                                Log.d(TAG, "down bottom: " + index);

                                /*if (mIsOverUnits && (mRectFList.size() + mOverUnitsOffset) < LETTERS.length) {
                                    mOverUnitsOffset++;
                                    int len = mRectFList.size() + mOverUnitsOffset;
                                    for (int i = mOverUnitsOffset; i < len; i++) {
                                        mArrayMap2.put(mRectFList.get(i - mOverUnitsOffset), LETTERS[i]);
                                    }

                                    mSelectedLetter = LETTERS[len - 1];
                                }*/
                            }

                            break;
                        }
                    }

                    invalidate();
                } else {
                    return false;
                }

                break;
            case MotionEvent.ACTION_MOVE:
                if (!mRectFTotal.contains(x, y)) {
                    return true;
                }
                Log.i(TAG, "ACTION_MOVE, ACTION_MOVE, ACTION_MOVE");

                /*for (Map.Entry<String, RectF> entry : mArrayMap.entrySet()) {
                    if (entry.getValue().contains(x, y)) {
                        String key = entry.getKey();
                        if (key.equals(mSelectedLetter)) {
                            Log.v(TAG, "move, move, move, the same letter");
                            //return true;
                        } else {
                            mSelectedLetter = key;
                            Log.d(TAG, "move: selectedLetter-" + mSelectedLetter);

                            invalidate();
                        }
                    }
                }*/

                for (RectF rectF : mRectFList) {
                    if (rectF.contains(x, y)) {
                        String electedLetter = mArrayMap2.get(rectF);
                        if (electedLetter != null && electedLetter.equals(mSelectedLetter)) {
                            Log.v(TAG, "move, move, move, the same letter");
                            //return true;
                        } else {
                            mSelectedLetter = electedLetter;
                            Log.d(TAG, "move: selectedLetter-" + mSelectedLetter);

                            invalidate();
                        }
                        int index = mRectFList.indexOf(rectF);
                        Log.d(TAG, "move: selectedLetter-" + index);
                        if (mIsOverUnits && index == 0) {
                            Log.d(TAG, "move to top: " + index);
                        }

                        if (mIsOverUnits && index == mRectFList.size() - 1) {
                            Log.d(TAG, "move to bottom: " + index);

                            if ((mRectFList.size() + mOverUnitsOffset) < LETTERS.length) {
                                mOverUnitsOffset++;
                                int len = mRectFList.size() + mOverUnitsOffset;
                                for (int i = mOverUnitsOffset; i < len; i++) {
                                    mArrayMap2.put(mRectFList.get(i - mOverUnitsOffset), LETTERS[i]);
                                }

                                mSelectedLetter = LETTERS[len - 1];
                            }
                        }

                    }
                }

                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                //Log.v(TAG, "ACTION_UP, ACTION_UP, ACTION_UP");
                invalidate();
                mIsOnTouch = false;
                break;
        }

        return true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mArrayMap.clear();
        mArrayMap2.clear();
        if (mBitmapBubble != null && !mBitmapBubble.isRecycled()) {
            mBitmapBubble.recycle();
        }
    }

}
