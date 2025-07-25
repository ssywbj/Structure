package com.suheng.structure.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class ComposeShaderView extends View{

    private static final String TAG = "ComposeShaderView";
    private int mWidth = 180;
    private int mHeight = 180;
    private Paint mHaloPaint;
    private Paint mBubblePaint;

    Shader mLinearGradient;
    Shader mRadialGradient;
    ComposeShader mComposeShader;
    Shader mBubbleRadialGradient;
    ComposeShader mBubbleComposeShader;

    private final ArrayList<Bubble> mBubbles = new ArrayList<>();
    private final LinkedList<Bubble> mReuseBubbles = new LinkedList<>();
    private final Random random = new Random();
    private Thread mBubbleThread;

    private final int mBubbleRefreshTime = 30;
    private final int mBubbleMaxRadius = 8;
    private final int mBubbleMinRadius = 2;
    private final int mBubbleMaxSize = 100;
    private final int mBubbleMinSize = 60;
    private final int mBubbleMaxSpeed = 4;
    private final int mBubbleMaxAngle = 360;
    private final float mBorderSize = 15f;

    public ComposeShaderView(Context context) {
        this(context,null);
    }

    public ComposeShaderView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ComposeShaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mHaloPaint = new Paint();
        mHaloPaint.setAntiAlias(true);
        mHaloPaint.setDither(true);
        mHaloPaint.setStrokeWidth(mBorderSize);
        mBubblePaint = new Paint();
        mBubblePaint.setAntiAlias(true);
        mBubblePaint.setDither(true);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startBubbleSync();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopBubbleSync();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        Log.d(TAG,"mWidth:"+ mWidth+",mHeight:"+mHeight);
        mLinearGradient =new LinearGradient(0,
                0,
                0,
                mHeight,
                Color.GREEN,
                Color.BLUE,
                Shader.TileMode.CLAMP);

        mRadialGradient = new RadialGradient(mWidth >> 1,
                mHeight >> 1,
                mWidth >> 1,
                new int[]{0x000000,0xffffffff},
                new float[]{0.6f,1.0f},
                Shader.TileMode.CLAMP);
        mComposeShader = new ComposeShader(mLinearGradient,mRadialGradient,PorterDuff.Mode.MULTIPLY);
        mHaloPaint.setShader(mComposeShader);

        mBubbleRadialGradient = new RadialGradient(mWidth >> 1,
                mHeight >> 1,
                mWidth >> 1,
                new int[]{0x00ffffff,0xffffffff},
                new float[]{0.4f,1.0f},
                Shader.TileMode.CLAMP);
        mBubbleComposeShader = new ComposeShader(mLinearGradient, mBubbleRadialGradient,PorterDuff.Mode.MULTIPLY);
        mBubblePaint.setShader(mBubbleComposeShader);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mComposeShader != null) {
            mHaloPaint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(mWidth >> 1, mHeight >> 1, (mWidth >> 1) - mBorderSize, mHaloPaint);
            mHaloPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            canvas.drawCircle(mWidth >> 1, mHeight >> 1, (mWidth >> 1) - mBorderSize, mHaloPaint);
            drawBubble(canvas);
        }
    }

    private void drawBubble(Canvas canvas) {
        List<Bubble> list = new ArrayList<>(mBubbles);
        for (Bubble bubble : list) {
            if (bubble != null) {
                canvas.drawCircle(bubble.x, bubble.y, bubble.radius, mBubblePaint);
            }
        }
    }

    private void runAnimation() {
        while (true) {
            try {
                Thread.sleep(mBubbleRefreshTime);
                tryCreateBubble();
                refreshBubbles();
                postInvalidate();
            } catch (InterruptedException e) {
                System.out.println("Bubble thread interrupted");
                break;
            }
        }
    }

    private static class Bubble {
        int radius;
        float speed;
        float distance;
        float angle;
        float x;
        float y;
    }

    private void startBubbleSync() {
        stopBubbleSync();
        mBubbleThread = new Thread(this::runAnimation);
        mBubbleThread.start();
    }

    private void stopBubbleSync() {
        if (mBubbleThread != null) {
            mBubbleThread.interrupt();
            mBubbleThread = null;
        }
    }

    private void tryCreateBubble() {
        do {
            if (mBubbles.size() >= mBubbleMaxSize) {
                return;
            }
            Bubble bubble = mReuseBubbles.poll();
            if (bubble == null) {
                bubble = new Bubble();
            }
            int radius = random.nextInt(mBubbleMaxRadius - mBubbleMinRadius);
            radius += mBubbleMinRadius;
            float speed = random.nextFloat() * mBubbleMaxSpeed;
            while (speed < 1) {
                speed = random.nextFloat() * mBubbleMaxSpeed;
            }
            bubble.speed = speed;
            bubble.radius = radius;
            bubble.angle = random.nextFloat() * mBubbleMaxAngle;
            bubble.distance = (mWidth >> 1) - radius - mBorderSize;
            mBubbles.add(bubble);
        } while (mBubbles.size() < mBubbleMinSize);
    }

    private void refreshBubbles() {
        List<Bubble> list = new ArrayList<>(mBubbles);
        for (Bubble bubble : list) {
            if (bubble.distance - bubble.speed <= ((mWidth >> 1) - bubble.radius - mBorderSize)/ 2f) {
                mBubbles.remove(bubble);
                //reuse bubble
                mReuseBubbles.offer(bubble);
            } else {
                bubble.distance -= bubble.speed;
                bubble.x = (float) (mWidth/2 + bubble.distance * Math.cos(bubble.angle * Math.PI / 180f));
                bubble.y = (float) (mHeight/2 + bubble.distance * Math.sin(bubble.angle * Math.PI / 180f));
            }
        }
    }
}
