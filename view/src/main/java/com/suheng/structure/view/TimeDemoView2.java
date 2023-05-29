package com.suheng.structure.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.lifecycle.ViewTreeViewModelStoreOwner;

import com.suheng.structure.view.utils.CountViewModel;

import java.util.Calendar;

public class TimeDemoView2 extends View implements ViewModelStoreOwner, LifecycleOwner {
    private CountViewModel mViewModel = null;
    private static final String TAG = TimeDemoView2.class.getSimpleName();
    private final Paint mPaint = new Paint();
    private final Rect mRectSmall = new Rect();

    public TimeDemoView2(Context context) {
        super(context);
        this.init();
    }

    public TimeDemoView2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public TimeDemoView2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    private void init() {
        mPaint.setAntiAlias(true);
        mPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mPaint.setColor(Color.BLACK);

        //Log.d(TAG, "init, owner: "+owner);
        mViewModel = new ViewModelProvider(this).get(CountViewModel.class);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mViewModel.getMCountLive().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                invalidate();
            }
        });
        mViewModel.startObserver();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPaint.setTextSize(40);
        String text = "88";
        Rect rect = new Rect();
        mPaint.getTextBounds(text, 0, text.length(), rect);
        int left = (int) ((w - rect.width()) / 2f);
        int top = 15;
        mRectSmall.set(left, top, left + rect.width(), top += rect.height());

        mPaint.setTextSize(60);
        mPaint.getTextBounds(text, 0, text.length(), rect);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //int second = Calendar.getInstance().get(Calendar.SECOND);
        int second = mViewModel.getMCountLive().getValue();
        String text = second / 10 + "" + second % 10;
        mPaint.setTextSize(40);
        canvas.drawText(text, mRectSmall.left, mRectSmall.bottom, mPaint);
    }

    @NonNull
    @Override
    public ViewModelStore getViewModelStore() {
        return new ViewModelStore();
        //return ViewTreeViewModelStoreOwner.get(this).getViewModelStore();
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return new LifecycleRegistry(this);
    }
}
