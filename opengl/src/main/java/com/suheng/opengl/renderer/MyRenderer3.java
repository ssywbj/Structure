package com.suheng.opengl.renderer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.suheng.opengl.R;
import com.suheng.opengl.Utils;
import com.suheng.opengl.app.OpenGLApp;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyRenderer3 implements GLSurfaceView.Renderer {

    private static final int POINT_TOTAL = 4;
    private static final int VERTEX_ANCHOR = POINT_TOTAL * 3;

    private static final short[] VERTEX_INDEX = {0, 1, 2, 0, 2, 3};
    private final ShortBuffer mVertexIndexBuffer;

    private ImageRenderer mImageRenderer;

    private int mWidth, mHeight;

    public MyRenderer3() {
        mVertexIndexBuffer = ByteBuffer.allocateDirect(VERTEX_INDEX.length * 2)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer()
                .put(VERTEX_INDEX);
        mVertexIndexBuffer.position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        mImageRenderer = new ImageRenderer();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        mWidth = width;
        mHeight = height;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        mImageRenderer.onDrawFrame(mWidth, mHeight);
    }

    private final class ImageRenderer {
        private static final String VERTEX_SHADER =
                "attribute vec4 vPosition;\n" +
                "uniform mat4 uMVPMatrix;\n" +
                "attribute vec2 aTextureCoord;\n" +
                "varying vec2 vTextureCoord;\n"+
                "void main() {\n" +
                "  gl_Position = uMVPMatrix * vPosition;\n" +
                "  vTextureCoord = aTextureCoord;;\n" +
                "}";
        private static final String FRAGMENT_SHADER =
                "precision mediump float;\n" +
                "varying vec2 vTextureCoord;\n" +
                "uniform sampler2D uTexture;\n" +
                "void main() {\n" +
                "  gl_FragColor = texture2D(uTexture, vTextureCoord);\n" +
                "}";
        private final FloatBuffer mVertexBuffer;
        private final int mProgram;
        private final float[] mMatrixProjection = new float[16];

        private final FloatBuffer mTexVertexBuffer;

        private final int mProgram2;

        public ImageRenderer() {
            mVertexBuffer = ByteBuffer.allocateDirect(VERTEX_ANCHOR * 4)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            final float[] coordinates = {
                    1f, 1f, 0,
                    -1f, 1f, 0,
                    -1f, -1f, 0,
                    1f, -1f, 0
            };
            mVertexBuffer.put(coordinates);
            mVertexBuffer.position(0);

            //截取纹理全部区域
            final float[] texVertex = { // in clockwise order:
                    1, 0,  // bottom right
                    0, 0,  // bottom left
                    0, 1,  // top left
                    1, 1,  // top right
            };
            mTexVertexBuffer = ByteBuffer.allocateDirect(texVertex.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer()
                    .put(texVertex);
            mTexVertexBuffer.position(0);

            final int vertexShader = Utils.loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER);
            final int fragmentShader = Utils.loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER);
            mProgram = GLES20.glCreateProgram();
            GLES20.glAttachShader(mProgram, vertexShader);
            GLES20.glAttachShader(mProgram, fragmentShader);
            GLES20.glLinkProgram(mProgram);

            mProgram2 = GLES20.glCreateProgram();
            GLES20.glAttachShader(mProgram2, vertexShader);
            GLES20.glAttachShader(mProgram2, fragmentShader);
            GLES20.glLinkProgram(mProgram2);
        }

        public void onDrawFrame(int width, int height) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            //options.inScaled = false; //The original picture: Return the original width and height of the image.
            options.inScaled = true; //default is true
            final int resId = R.drawable.girl_gaitubao;
            final Bitmap bitmap = BitmapFactory.decodeResource(OpenGLApp.Companion.getInstance().getResources(), resId, options);
            if (bitmap == null) {
                Log.e("Wbj", "Resource ID " + resId + " could not be decoded.");
                return;
            }

            GLES20.glUseProgram(mProgram);

            final int positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
            GLES20.glEnableVertexAttribArray(positionHandle);
            GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 12, mVertexBuffer);

             final int rectWidth = bitmap.getWidth();
            final int rectHeight = bitmap.getHeight();
            Log.d("Wbj", "onDrawFrame, width: " + width + ", height: " + height
                    + ", rectWidth: " + rectWidth + ", rectHeight: " + rectHeight
                    + ", rect w/h: " + (1f * rectWidth / rectHeight));
            final float ortho = 1f;
            //final float ortho = 0.8f;
            final float scaleX = ((float) rectWidth) / width;
            final float scaleY = ((float) rectHeight) / height;
            final float translateX = ortho - scaleX;
            final float translateY = ortho - scaleY;
            Log.v("Wbj", "onDrawFrame, ortho: " + ortho + ", scaleX: " + scaleX + ", scaleY: " + scaleY
                    + ", translateX: " + translateX + ", translateY: " + translateY);
            final int matrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
            Matrix.setIdentityM(mMatrixProjection, 0);
            Matrix.orthoM(mMatrixProjection, 0, -ortho, ortho, -ortho, ortho, -1f, 1f);
            Matrix.translateM(mMatrixProjection, 0, -translateX, translateY, 0f);
            Matrix.scaleM(mMatrixProjection, 0, scaleX, scaleY, 1f);
            GLES20.glUniformMatrix4fv(matrixHandle, 1, false, mMatrixProjection, 0);

            //final int textureId = Utils.loadTexture(OpenGLApp.Companion.getInstance(), R.drawable.girl_gaitubao);
            final int textureId = Utils.loadTexture(bitmap, !bitmap.isRecycled());
            final int textureCoordHandle = GLES20.glGetAttribLocation(mProgram, "aTextureCoord");
            GLES20.glEnableVertexAttribArray(textureCoordHandle);
            GLES20.glVertexAttribPointer(textureCoordHandle, 2, GLES20.GL_FLOAT, false, 8, mTexVertexBuffer);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);

            final int  textureHandle = GLES20.glGetUniformLocation(mProgram, "uTexture");
            GLES20.glUniform1i(textureHandle, 0);

            Log.v("Wbj", "onDrawFrame, textureId: " + textureId + ", textureCoordHandle: " + textureCoordHandle
                    + ", textureHandle: " + textureHandle);

            GLES20.glDrawElements(GLES20.GL_TRIANGLES, mVertexIndexBuffer.capacity(), GLES20.GL_UNSIGNED_SHORT, mVertexIndexBuffer);

            GLES20.glDisableVertexAttribArray(positionHandle);
            GLES20.glDisableVertexAttribArray(textureCoordHandle);

            this.onDraw(width, height);
            this.onDraw2(width, height);
        }

        public void onDraw(int width, int height) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            //options.inScaled = false; //The original picture: Return the original width and height of the image.
            options.inScaled = true; //default is true
            final int resId = R.drawable.girl_gaitubao;
            final Bitmap bitmap = BitmapFactory.decodeResource(OpenGLApp.Companion.getInstance().getResources(), resId, options);
            if (bitmap == null) {
                Log.e("Wbj", "Resource ID " + resId + " could not be decoded.");
                return;
            }

            GLES20.glUseProgram(mProgram2);

            final int positionHandle = GLES20.glGetAttribLocation(mProgram2, "vPosition");
            GLES20.glEnableVertexAttribArray(positionHandle);
            GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 12, mVertexBuffer);

            final int rectWidth = width;
            final int rectHeight = (int) (bitmap.getHeight() * 1f * width / bitmap.getWidth());
            Log.d("Wbj", "onDraw, width: " + width + ", height: " + height
                    + ", rectWidth: " + rectWidth + ", rectHeight: " + rectHeight
                    + ", rect w/h: " + (1f * rectWidth / rectHeight));
            final float ortho = 1f;
            final float scaleX = ((float) rectWidth) / width;
            final float scaleY = ((float) rectHeight) / height;
            final float translateX = ortho - scaleX;
            final float translateY = ortho - scaleY;
            Log.v("Wbj", "onDraw, ortho: " + ortho + ", scaleX: " + scaleX + ", scaleY: " + scaleY
                    + ", translateX: " + translateX + ", translateY: " + translateY);
            final int matrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
            Matrix.setIdentityM(mMatrixProjection, 0);
            Matrix.orthoM(mMatrixProjection, 0, -ortho, ortho, -ortho, ortho, -1f, 1f);
            Matrix.translateM(mMatrixProjection, 0, 0f, 0.33f, 0f);
            Matrix.scaleM(mMatrixProjection, 0, scaleX, scaleY, 1f);
            GLES20.glUniformMatrix4fv(matrixHandle, 1, false, mMatrixProjection, 0);

            final int textureId = Utils.loadTexture(bitmap, !bitmap.isRecycled());
            final int textureCoordHandle = GLES20.glGetAttribLocation(mProgram2, "aTextureCoord");
            GLES20.glEnableVertexAttribArray(textureCoordHandle);
            GLES20.glVertexAttribPointer(textureCoordHandle, 2, GLES20.GL_FLOAT, false, 8, mTexVertexBuffer);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);

            final int  textureHandle = GLES20.glGetUniformLocation(mProgram2, "uTexture");
            GLES20.glUniform1i(textureHandle, 0);

            Log.v("Wbj", "onDraw, textureId: " + textureId + ", textureCoordHandle: " + textureCoordHandle
                    + ", textureHandle: " + textureHandle);

            GLES20.glDrawElements(GLES20.GL_TRIANGLES, mVertexIndexBuffer.capacity(), GLES20.GL_UNSIGNED_SHORT, mVertexIndexBuffer);

            GLES20.glDisableVertexAttribArray(positionHandle);
            GLES20.glDisableVertexAttribArray(textureCoordHandle);
        }

        public void onDraw2(int width, int height) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            //options.inScaled = false; //The original picture: Return the original width and height of the image.
            options.inScaled = true; //default is true
            final int resId = R.drawable.girl_gaitubao;
            final Bitmap bitmap = BitmapFactory.decodeResource(OpenGLApp.Companion.getInstance().getResources(), resId, options);
            if (bitmap == null) {
                Log.e("Wbj", "Resource ID " + resId + " could not be decoded.");
                return;
            }

            GLES20.glUseProgram(mProgram2);

            final int positionHandle = GLES20.glGetAttribLocation(mProgram2, "vPosition");
            GLES20.glEnableVertexAttribArray(positionHandle);
            GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 12, mVertexBuffer);

            final int rectWidth = width;
            final int rectHeight = (int) (bitmap.getHeight() * 1f * width / bitmap.getWidth());
            Log.d("Wbj", "onDraw, width: " + width + ", height: " + height
                    + ", rectWidth: " + rectWidth + ", rectHeight: " + rectHeight
                    + ", rect w/h: " + (1f * rectWidth / rectHeight));
            final float ortho = 0.8f;
            final float scaleX = ((float) rectWidth) / width;
            final float scaleY = ((float) rectHeight) / height;
            final float translateX = ortho - scaleX;
            final float translateY = ortho - scaleY + (1 - ortho) / 2f;
            Log.v("Wbj", "onDraw, ortho: " + ortho + ", scaleX: " + scaleX + ", scaleY: " + scaleY
                    + ", translateX: " + translateX + ", translateY: " + translateY);
            final int matrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
            Matrix.setIdentityM(mMatrixProjection, 0);
            Matrix.orthoM(mMatrixProjection, 0, -ortho, ortho, -ortho, ortho, -1f, 1f);
            Matrix.translateM(mMatrixProjection, 0, 0f, -translateY, 0f);
            Matrix.scaleM(mMatrixProjection, 0, scaleX, scaleY, 1f);
            GLES20.glUniformMatrix4fv(matrixHandle, 1, false, mMatrixProjection, 0);

            final int textureId = Utils.loadTexture(bitmap, !bitmap.isRecycled());
            final int textureCoordHandle = GLES20.glGetAttribLocation(mProgram2, "aTextureCoord");
            GLES20.glEnableVertexAttribArray(textureCoordHandle);
            GLES20.glVertexAttribPointer(textureCoordHandle, 2, GLES20.GL_FLOAT, false, 8, mTexVertexBuffer);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);

            final int  textureHandle = GLES20.glGetUniformLocation(mProgram2, "uTexture");
            GLES20.glUniform1i(textureHandle, 0);

            Log.v("Wbj", "onDraw, textureId: " + textureId + ", textureCoordHandle: " + textureCoordHandle
                    + ", textureHandle: " + textureHandle);

            GLES20.glDrawElements(GLES20.GL_TRIANGLES, mVertexIndexBuffer.capacity(), GLES20.GL_UNSIGNED_SHORT, mVertexIndexBuffer);

            GLES20.glDisableVertexAttribArray(positionHandle);
            GLES20.glDisableVertexAttribArray(textureCoordHandle);
        }
    }

}
