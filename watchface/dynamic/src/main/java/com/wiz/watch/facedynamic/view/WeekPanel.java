package com.wiz.watch.facedynamic.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.Nullable;

import com.structure.wallpaper.basic.utils.DateUtil;
import com.wiz.watch.facedynamic.R;

public class WeekPanel extends PanelView {
    private static final float SCALE = 360 / 8f;
    private static final float START_SCALE = -SCALE * 3;
    private Bitmap mPointer;

    public WeekPanel(Context context) {
        super(context);
        this.init();
    }

    public WeekPanel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WeekPanel);
        Drawable drawable = typedArray.getDrawable(R.styleable.WeekPanel_batteryPointer);
        if (drawable != null) {
            mPointer = ((BitmapDrawable) drawable).getBitmap();
        }
        typedArray.recycle();
        Log.d(mTAG, "battery pointer: " + mPointer);
    }

    @Override
    protected void onTimeTick() {
        super.onTimeTick();
        if (mPointer == null) {
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        canvas.translate(mRectF.centerX(), mRectF.centerY());
        if (mPointer != null) {
            canvas.save();
            canvas.rotate(START_SCALE + SCALE * DateUtil.getWeekIndex());
            canvas.drawBitmap(mPointer, -mPointer.getWidth() / 2f, -mPointer.getHeight() / 2f, null);
            canvas.restore();
        }
        canvas.restore();
    }

    public void setPointer(Bitmap pointer) {
        mPointer = pointer;

        invalidate();
    }

}
