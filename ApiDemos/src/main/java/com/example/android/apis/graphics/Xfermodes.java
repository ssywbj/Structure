/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.apis.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Xfermode;
import android.os.Bundle;
import android.view.View;

public class Xfermodes extends GraphicsActivity {

    // create a bitmap with a circle, used for the "dst" image
    static Bitmap makeDst(int w, int h) {
        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);

        p.setColor(0xFFFF0000);
        c.drawOval(new RectF(0, 0, 1.0f * w * 3 / 4, 1.0f * h * 3 / 4), p);
        return bm;
    }

    // create a bitmap with a rect, used for the "src" image
    static Bitmap makeSrc(int w, int h) {
        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);

        p.setColor(0xFF0000FF);
        c.drawRect(1.0f * w / 3, 1.0f * h / 3, 1.0f * w * 19 / 20, 1.0f * h * 19 / 20, p);
        return bm;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new SampleView(this));
    }

    private static class SampleView extends View {
        private static final int W = 64;
        private static final int H = 64;
        private static final int ROW_MAX = 4;   // number of samples per row

        private final Bitmap mSrcB;
        private final Bitmap mDstB;
        private final Shader mBG;     // background checker-board pattern

        private static final Xfermode[] sModes = {
                new PorterDuffXfermode(PorterDuff.Mode.CLEAR),
                new PorterDuffXfermode(PorterDuff.Mode.SRC), //显示源图
                new PorterDuffXfermode(PorterDuff.Mode.DST), //只绘制目标图像
                new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER), //在目标图像的顶部绘制源图像
                new PorterDuffXfermode(PorterDuff.Mode.DST_OVER), //在源图像的上方绘制目标图像
                new PorterDuffXfermode(PorterDuff.Mode.SRC_IN), //只在源图像和目标图像相交的地方绘制源图像
                new PorterDuffXfermode(PorterDuff.Mode.DST_IN), //只在源图像和目标图像相交的地方绘制目标图像
                new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT), //只在源图像和目标图像不相交的地方绘制源图像
                new PorterDuffXfermode(PorterDuff.Mode.DST_OUT), //只在源图像和目标图像不相交的地方绘制目标图像
                new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP), //在源图像和目标图像相交的地方绘制源图像，在不相交的地方绘制目标图像
                new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP), //在源图像和目标图像相交的地方绘制目标图像，在不相交的地方绘制源图像
                new PorterDuffXfermode(PorterDuff.Mode.XOR), //在源图像和目标图像重叠之外的任何地方绘制他们，而在不重叠的地方不绘制任何内容
                new PorterDuffXfermode(PorterDuff.Mode.DARKEN), //变暗
                new PorterDuffXfermode(PorterDuff.Mode.LIGHTEN), //变亮
                new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY), //正片叠底
                new PorterDuffXfermode(PorterDuff.Mode.SCREEN), //滤色
                new PorterDuffXfermode(PorterDuff.Mode.ADD), //饱和相加
                new PorterDuffXfermode(PorterDuff.Mode.OVERLAY), //叠加
        };

        private static final String[] sLabels = {
                "Clear", "Src", "Dst", "SrcOver",
                "DstOver", "SrcIn", "DstIn", "SrcOut",
                "DstOut", "SrcATop", "DstATop", "Xor",
                "Darken", "Lighten", "Multiply", "Screen",
                "Add", "Overlay"
        };

        public SampleView(Context context) {
            super(context);

            mSrcB = makeSrc(W, H);
            mDstB = makeDst(W, H);

            // make a ckeckerboard pattern
            Bitmap bm = Bitmap.createBitmap(new int[]{0xFFFFFFFF, 0xFFCCCCCC,
                            0xFFCCCCCC, 0xFFFFFFFF}, 2, 2,
                    Bitmap.Config.RGB_565);
            mBG = new BitmapShader(bm,
                    Shader.TileMode.REPEAT,
                    Shader.TileMode.REPEAT);
            Matrix m = new Matrix();
            m.setScale(6, 6);
            mBG.setLocalMatrix(m);

            labelP.setTextAlign(Paint.Align.CENTER);
            paint.setFilterBitmap(false);

            //Custom paint

            // make a ckeckerboard pattern
            Bitmap bmcp = Bitmap.createBitmap(new int[]{0xFFFFFFFF, 0xFF0000FF,
                    0xFF0000FF, 0xFFFFFFFF}, 2, 2, Bitmap.Config.RGB_565);
            mBGCP = new BitmapShader(bmcp, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
            Matrix matrix = new Matrix();
            matrix.setScale(6, 6);
            mBGCP.setLocalMatrix(matrix);

            //mPaintLabel.setTextAlign(Paint.Align.CENTER);

            mPaint.setFilterBitmap(false);
        }

        Paint labelP = new Paint(Paint.ANTI_ALIAS_FLAG);
        Paint paint = new Paint();

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawColor(Color.WHITE);

            canvas.translate(15, 35);

            int x = 0;
            int y = 0;
            for (int i = 0; i < sModes.length; i++) {
                // draw the border
                paint.setStyle(Paint.Style.STROKE);
                paint.setShader(null);
                canvas.drawRect(x - 0.5f, y - 0.5f,
                        x + W + 0.5f, y + H + 0.5f, paint);

                // draw the checker-board pattern
                paint.setStyle(Paint.Style.FILL);
                paint.setShader(mBG);
                canvas.drawRect(x, y, x + W, y + H, paint);

                // draw the src/dst example into our offscreen bitmap
                int sc = canvas.saveLayer(x, y, x + W, y + H, null);
                canvas.translate(x, y);
                canvas.drawBitmap(mDstB, 0, 0, paint); //先画dst，红色部分
                paint.setXfermode(sModes[i]); //设置图层混合模式
                canvas.drawBitmap(mSrcB, 0, 0, paint); //再画src，蓝色部分
                paint.setXfermode(null); //还原图层混合模式
                canvas.restoreToCount(sc);

                // draw the label
                canvas.drawText(sLabels[i],
                        x + 1.0f * W / 2, y - labelP.getTextSize() / 2, labelP);

                x += W + 10;

                // wrap around when we've drawn enough for one row
                if ((i % ROW_MAX) == ROW_MAX - 1) {
                    x = 0;
                    y += H + 30;
                }
            }

            paintXfermodeRects(canvas, y);
        }

        private final Paint mPaint = new Paint();
        private final Shader mBGCP;     // background checker-board pattern
        private final Paint mPaintLabel = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint mPaintXfermode = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Rect mRectText = new Rect();

        private void paintXfermodeRects(Canvas canvas, int y) {
            final float checkerBoardPadding = 0.5f;
            int x = 0;
            y += H + 60;
            for (int i = 0; i < sModes.length; i++) {
                canvas.save();
                canvas.translate(x, y);

                // draw the border
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setShader(null);
                canvas.drawRect(-checkerBoardPadding, -checkerBoardPadding, W + checkerBoardPadding, H + checkerBoardPadding, mPaint);

                // draw the checker-board pattern
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setShader(mBGCP);
                canvas.drawRect(0, 0, W, H, mPaint);

                // draw the label，方法2:不设置setTextAlign(Paint.Align.CENTER);
                mPaintLabel.getTextBounds(sLabels[i], 0, sLabels[i].length(), mRectText);
                canvas.drawText(sLabels[i], 1.0f * W / 2 - mRectText.width() / 2f, -mPaintLabel.getTextSize() / 2, mPaintLabel);

                // draw the src/dst example into our offscreen bitmap
                int sc = canvas.saveLayer(0, 0, W, H, null); //如果不saveLayer，下层图片没有时，会有一片黑色的背景
                mPaintXfermode.setColor(0xFFFF0000);
                canvas.drawOval(new RectF(0, 0, 1.0f * W * 3 / 4, 1.0f * H * 3 / 4), mPaintXfermode);
                mPaintXfermode.setXfermode(sModes[i]); //定义下层图片的过度方式
                mPaintXfermode.setColor(0xFF0000FF);
                canvas.drawRect(1.0f * W / 3, 1.0f * H / 3, 1.0f * W * 19 / 20, 1.0f * H * 19 / 20, mPaintXfermode);
                mPaintXfermode.setXfermode(null);
                canvas.restoreToCount(sc);

                canvas.restore();

                x += W + 10;

                // wrap around when we've drawn enough for one row
                if ((i % ROW_MAX) == ROW_MAX - 1) {
                    x = 0;
                    y += H + 30;
                }
            }
        }
    }

}

