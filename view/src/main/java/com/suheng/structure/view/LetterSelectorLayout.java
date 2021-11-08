package com.suheng.structure.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import java.util.Map;

public class LetterSelectorLayout extends FrameLayout {
    private static final String TAG = LetterSelectorLayout.class.getSimpleName();
    private Paint mPaint;
    private final String[] mLetters = {"A", "B", "C", "D", "E", "F", "G"};
    private float mMargin;

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
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        mPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, metrics));
        mMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 28, metrics);
        mPaint.setTextAlign(Paint.Align.CENTER);
    }

    private final ArrayMap<RectF, String> mArrayMap = new ArrayMap<>();
    private final RectF mRectFTotal = new RectF();

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        mArrayMap.clear();

        final String text = mLetters[0];
        Rect rect = new Rect();
        mPaint.getTextBounds(text, 0, text.length(), rect);

        RectF rectF = new RectF();
        rectF.left = mMargin;
        rectF.top = mMargin;
        rectF.right = rectF.left + rect.width() + mMargin;
        rectF.bottom = rectF.top + rect.height() + mMargin;
        float height = rectF.height();
        Log.d(TAG, "draw: " + rect.toShortString() + ", " + rect.width() + "---" + rect.height() + "\n"
                + rectF.toShortString() + ", " + rectF.width() + "---" + height + ", " + rectF.centerX() + "---" + rectF.centerY());

        mRectFTotal.set(rectF);

        final int cor = 255 / mLetters.length;
        for (int i = 0; i < mLetters.length; i++) {
            mPaint.setColor(Color.rgb(cor * i, cor * i, cor * i));
            canvas.drawRect(rectF, mPaint);

            mPaint.setColor(Color.WHITE);
            canvas.drawText(mLetters[i], rectF.centerX(), rectF.centerY() + rect.height() / 2f, mPaint);
            //canvas.drawText("A", rectF.centerX(), rectF.bottom, mPaint);

            mArrayMap.put(new RectF(rectF.left, rectF.top, rectF.right, rectF.bottom), mLetters[i]);
            Log.d(TAG, "draw+++++: " + rectF.bottom);

            rectF.top = rectF.bottom;
            rectF.bottom += height;
        }

        mRectFTotal.bottom = rectF.top;
        Log.d(TAG, "draw------: " + mRectFTotal.bottom + ", " + mArrayMap.size());
    }

    private String mSelectedLetter;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "ACTION_DOWN, ACTION_DOWN, ACTION_DOWN");
                if (!mRectFTotal.contains(x, y)) {
                    return false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (!mRectFTotal.contains(x, y)) {
                    return true;
                }
                Log.i(TAG, "ACTION_MOVE, ACTION_MOVE, ACTION_MOVE");

                for (Map.Entry<RectF, String> entry : mArrayMap.entrySet()) {
                    if (entry.getKey().contains(x, y)) {
                        String value = entry.getValue();
                        if (value.equals(mSelectedLetter)) {
                            return true;
                        } else {
                            mSelectedLetter = value;
                            Log.d(TAG, "move: selectedLetter-" + mSelectedLetter);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.v(TAG, "ACTION_UP, ACTION_UP, ACTION_UP");
                for (Map.Entry<RectF, String> entry : mArrayMap.entrySet()) {
                    if (entry.getKey().contains(x, y)) {
                        Log.v(TAG, "click: selectedLetter-" + entry.getValue());
                        break;
                    }
                }
                break;
        }

        return true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mArrayMap.clear();
    }

    class Letter {
        private final RectF mRectF = new RectF();
    }
}
