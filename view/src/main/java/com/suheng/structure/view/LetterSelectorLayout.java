package com.suheng.structure.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.SystemClock;
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
    private String[] mLetters = {"A", "B", "C", "D", "E", "F", "G"};
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
        mMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18, metrics);
        mPaint.setTextAlign(Paint.Align.CENTER);

        mOffsetX = 20;
        mOffsetY = 50;
    }

    private final ArrayMap<RectF, String> mArrayMap = new ArrayMap<>();
    private int mOffsetX, mOffsetY;

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

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


        canvas.save();
        canvas.translate(mOffsetX, mOffsetY);
        final int cor = 255 / mLetters.length;
        for (int i = 0; i < mLetters.length; i++) {
            mPaint.setColor(Color.rgb(cor * i, cor * i, cor * i));
            canvas.drawRect(rectF, mPaint);

            mPaint.setColor(Color.WHITE);
            canvas.drawText(mLetters[i], rectF.centerX(), rectF.centerY() + rect.height() / 2f, mPaint);
            //canvas.drawText("A", rectF.centerX(), rectF.bottom, mPaint);

            mArrayMap.put(new RectF(rectF.left, rectF.top, rectF.right, rectF.bottom), mLetters[i]);

            rectF.top = rectF.bottom;
            rectF.bottom += height;
        }
        canvas.restore();

    }

    private String mSelectedLetter;
    private boolean mIsMove;
    private long mMoveTime;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float eventX = event.getX();
        float eventY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mIsMove = false;
                break;
            case MotionEvent.ACTION_MOVE:
                mMoveTime = SystemClock.uptimeMillis();

                mIsMove = true;
                for (Map.Entry<RectF, String> entry : mArrayMap.entrySet()) {
                    if (entry.getKey().contains(eventX - mOffsetX, eventY - mOffsetY)) {
                        String value = entry.getValue();
                        if (value.equals(mSelectedLetter)) {

                        } else {
                            mSelectedLetter = value;
                            Log.d(TAG, "move: selectedLetter-" + mSelectedLetter);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.v(TAG, "ACTION_UP, ACTION_UP, ACTION_UP");
                if (mIsMove) {
                    mIsMove = false;
                    break;
                }

                for (Map.Entry<RectF, String> entry : mArrayMap.entrySet()) {
                    if (entry.getKey().contains(eventX - mOffsetX, eventY - mOffsetY)) {
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
