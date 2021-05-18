package com.suheng.structure.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class ShaderView extends View {
    private Bitmap mBg;
    private Paint mPaint;

    public ShaderView(Context context) {
        super(context);
        this.init();
    }

    public ShaderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public ShaderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    private void init() {
        //mBg = BitmapFactory.decodeResource(getResources(), R.drawable.girl_gaitubao);
        mBg = BitmapFactory.decodeResource(getResources(), R.mipmap.girl_gaitubao);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintText.setColor(Color.BLACK);
        mPaintText.setTextSize(43);
        mPaintText.getTextBounds(mText, 0, mText.length(), mRectText);

        //float[] positions：颜色占的比例区间，如new float[]{0f, 0.2f, 1f}表示两个比例长度用于颜色A到颜色B的变换，
        //剩下的八个比例长度用于B到C的变换。如果传的是null，那么各个颜色均分变换长度。
        mLinearGradient = new LinearGradient(0, 0, mRectText.width(), 0, new int[]{0xffff0000, 0xff000000, 0xff00ff00}
                , new float[]{0f, 0.2f, 1f}, Shader.TileMode.CLAMP);
        /*mLinearGradient2 = new LinearGradient(-mRectText.width(), 0, 0, 0, new int[]{0xff000000, 0xffff0000, 0xff00ff00, 0xff000000}
                , null, Shader.TileMode.CLAMP);*/
        mLinearGradient2 = new LinearGradient(-mRectText.width() / 2f - mRectText.width(), 0, -mRectText.width() / 2f, 0, new int[]{0xff000000, 0xffff0000, 0xff00ff00, 0xff000000}
                , null, Shader.TileMode.CLAMP);
    }

    private final String mText = "欢迎来到文字渐变的世界";
    private LinearGradient mLinearGradient, mLinearGradient2;
    private Paint mPaintText;
    private final Rect mRectText = new Rect();
    private final Matrix mMatrixText = new Matrix();
    private int mTrans;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int rectWidth = mBg.getWidth() * 2;
        int rectHeight = mBg.getHeight() * 2;
        int margin = 2;
        //CLAMP：拉伸最后一个像素去铺满剩下的地方；MIRROR：通过镜像翻转铺满剩下的地方；REPEAT：重复图片平铺整个画面
        mPaint.setShader(new BitmapShader(mBg, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        canvas.drawRect(margin, margin, rectWidth + margin, rectHeight + margin, mPaint);
        mPaint.setShader(new BitmapShader(mBg, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT));
        canvas.drawRect(margin, rectHeight + 2 * margin, rectWidth + margin, 2 * (rectHeight + margin), mPaint);
        mPaint.setShader(new BitmapShader(mBg, Shader.TileMode.MIRROR, Shader.TileMode.MIRROR));
        canvas.drawRect(margin, 2 * rectHeight + 3 * margin, rectWidth + margin, 3 * (rectHeight + margin), mPaint);

        canvas.save();
        Bitmap heart = BitmapFactory.decodeResource(getResources(), R.mipmap.heart);
        Rect rect = new Rect();
        rect.left = margin;
        rect.top = 3 * (rectHeight + margin) + margin;
        rect.right = rect.left + 4 * margin + heart.getWidth();
        rect.bottom = rect.top + 4 * margin + heart.getHeight();
        canvas.clipRect(rect); //裁剪一个矩形区域做为下面复合渲染的对比
        canvas.drawColor(Color.BLACK);
        canvas.drawBitmap(heart, 3 * margin, 3 * (rectHeight + margin) + 3 * margin, null);
        canvas.restore();

        canvas.save();
        int left = rect.right + margin;
        int top = rect.top;
        final int bottom = top + heart.getHeight();
        rect = new Rect(left, top, left + heart.getWidth(), bottom); //为什么这个位置不能直接显示出复合图像，显示单个的就可以
        //但通过平移到这个位置就可以显示出复合图像，应该是和图片的默认位置有有关系：图片原点(左上顶点)和屏幕坐标系原点(左上顶点)重合；如果不平移，那么是点(left, top)和图片原点重合
        canvas.translate(rect.left, rect.top);
        rect = new Rect(0, 0, heart.getWidth(), heart.getHeight()); //图片区域：平移后，点(left, top)变成的屏幕坐标系原点(0, 0)，此时和图片原点重合
        BitmapShader bitmapShader = new BitmapShader(heart, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        LinearGradient linearGradient = new LinearGradient(rect.left, rect.top, rect.right, rect.bottom
                , 0xFFFF0000, 0xFF00FF00, Shader.TileMode.CLAMP); //从左上角到右下角的颜色渐变效果
        ComposeShader composeShader = new ComposeShader(linearGradient, bitmapShader, PorterDuff.Mode.MULTIPLY);
        Paint paint = new Paint();
        //paint.setShader(linearGradient);
        paint.setShader(composeShader);
        canvas.drawRect(rect, paint);
        canvas.restore();

        canvas.save();
        mPaintText.setTextAlign(Paint.Align.LEFT);
        canvas.translate(0, bottom + mRectText.height() + 4 * margin);
        mPaintText.setShader(mLinearGradient);
        canvas.drawText(mText, 0, 0, mPaintText);
        canvas.restore();

        canvas.save();

        mPaintText.setTextAlign(Paint.Align.CENTER);
        canvas.translate(getWidth() / 2f, bottom + 2 * mRectText.height() + 5 * margin);
        mMatrixText.setTranslate(mTrans, 0);

        /*canvas.translate(0, bottom + 2 * mRectText.height() + 5 * margin);
        mMatrixText.setTranslate(mTrans, 0);*/

        mLinearGradient2.setLocalMatrix(mMatrixText);
        mPaintText.setShader(mLinearGradient2);
        canvas.drawText(mText, 0, 0, mPaintText);
        mTrans += 10;
        if (mTrans >= 2 * mRectText.width()) {
            mTrans = 0;
        }
        canvas.restore();
        postInvalidateDelayed(20);
    }

}
