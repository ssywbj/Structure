package com.suheng.structure.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class SawToothClipView extends View {

    private Path mClipPath;
    private int mSawToothSize = 40; // 锯齿大小
    private int mSawToothCount = 50; // 每边锯齿数量

    public SawToothClipView(Context context) {
        this(context, null);
    }

    public SawToothClipView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SawToothClipView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mClipPath = new Path();
    }

    /**
     * 设置锯齿大小
     * @param size 锯齿大小（像素）
     */
    public void setSawToothSize(int size) {
        this.mSawToothSize = size;
        invalidate();
    }

    /**
     * 设置每边锯齿数量
     * @param count 锯齿数量
     */
    public void setSawToothCount(int count) {
        this.mSawToothCount = count;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        createSawToothPath(w, h);
    }

    private void createSawToothPath(int width, int height) {
        mClipPath.reset();
        
        // 计算每个锯齿的宽度
        float toothWidth = (float) width / mSawToothCount;
        float toothHeight = (float) height / mSawToothCount;
        
        // 绘制上边锯齿
        mClipPath.moveTo(0, mSawToothSize);
        for (int i = 0; i < mSawToothCount; i++) {
            mClipPath.lineTo((i + 0.5f) * toothWidth, 0);
            mClipPath.lineTo((i + 1) * toothWidth, mSawToothSize);
        }
        
        // 绘制右边锯齿
        for (int i = 0; i < mSawToothCount; i++) {
            mClipPath.lineTo(width, (i + 0.5f) * toothHeight);
            mClipPath.lineTo(width - mSawToothSize, (i + 1) * toothHeight);
        }
        
        // 绘制下边锯齿
        for (int i = 0; i < mSawToothCount; i++) {
            mClipPath.lineTo(width - (i + 0.5f) * toothWidth, height);
            mClipPath.lineTo(width - (i + 1) * toothWidth, height - mSawToothSize);
        }
        
        // 绘制左边锯齿
        for (int i = 0; i < mSawToothCount; i++) {
            mClipPath.lineTo(0, height - (i + 0.5f) * toothHeight);
            mClipPath.lineTo(mSawToothSize, height - (i + 1) * toothHeight);
        }
        
        // 闭合路径
        mClipPath.close();
    }

    @Override
    public void draw(Canvas canvas) {
        // 保存当前画布状态
        canvas.save();
        
        // 应用裁剪路径
        canvas.clipPath(mClipPath);
        
        // 绘制内容
        super.draw(canvas);
        
        // 恢复画布状态
        canvas.restore();
    }
}