package com.suheng.opengl.renderer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.suheng.opengl.R;
import com.suheng.opengl.Utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyRenderer4 implements GLSurfaceView.Renderer {
    public static final float SCALE_RATIO = 1.28f;
    public static final float SCALE_RATIO2 = 0.72f;
    public static final float SCALE_ALPHA2 = 0.4f;

    private final Context mContext;
    private int mWidth, mHeight;
    private ImageRenderer mImageRenderer;

    public MyRenderer4(Context context) {
        mContext = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0f, 0f, 0f, 0f);
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
        mImageRenderer.onDrawFrame();
    }

    public void onDestroy() {
        mImageRenderer.onDestroy();
    }

    private final class ImageRenderer {
        private static final float ORTHO = 1f;
        private static final int POINT_TOTAL = 4;
        private static final int VERTEX_ANCHOR = POINT_TOTAL * 3;
        private final FloatBuffer mVertexBuffer;
        private final int mProgram;
        private final float[] mMatrixProjection = new float[16];

        private final ShortBuffer mVertexIndexBuffer;

        private final FloatBuffer mTexVertexBuffer;

        private int[] mTextures;

        private float mTranslateX;
        private float mTranslateY;
        private float mScaleX;
        private float mScaleY;

        private int mMatrixHandle;
        private int mAlphaHandle;

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

            final short[] vertexIndex = {0, 1, 2, 0, 2, 3};
            mVertexIndexBuffer = ByteBuffer.allocateDirect(vertexIndex.length * 2)
                    .order(ByteOrder.nativeOrder())
                    .asShortBuffer()
                    .put(vertexIndex);
            mVertexIndexBuffer.position(0);

            final float[] texVertex = { // in clockwise order:
                    1f, 0f,  //bottom right
                    0f, 0f,  //bottom left
                    0f, 1f,  //top left
                    1f, 1f,  //top right
            };
            mTexVertexBuffer = ByteBuffer.allocateDirect(texVertex.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer()
                    .put(texVertex);
            mTexVertexBuffer.position(0);

            mProgram = Utils.glCreateProgram(mContext, R.raw.image_renderer_vertex
                    , R.raw.image_renderer_fragment);
        }

        public void onDrawFrame() {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false; //The original picture: Return the original width and height of the image.
            final int resId = R.drawable.iz0rltfp;
            final Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), resId, options);
            if (bitmap == null) {
                Log.e("Wbj", "Resource ID " + resId + " could not be decoded.");
                return;
            }
            mTextures = Utils.glGenTextures(2);
            if (mTextures == null) {
                return;
            }
            Log.d("Wbj", "textures: " + Arrays.toString(mTextures));

            //support alpha blending
            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
            //GLES20.glBlendFuncSeparate(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA, GLES20.GL_ONE, GLES20.GL_ZERO);

            GLES20.glUseProgram(mProgram);

            final int positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
            GLES20.glEnableVertexAttribArray(positionHandle);
            GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 12, mVertexBuffer);

            Log.d("Wbj", "onDrawFrame, width: " + mWidth + ", height: " + mHeight
                    + ", rectWidth: " + bitmap.getWidth() + ", rectHeight: " + bitmap.getWidth());
            mScaleX = bitmap.getWidth() * SCALE_RATIO / mWidth;
            mScaleY = bitmap.getHeight() * SCALE_RATIO / mHeight;
            Log.v("Wbj", "onDrawFrame, scaleX: " + mScaleX + ", scaleY: " + mScaleY
                    + ", translateX: " + mTranslateX + ", translateY: " + mTranslateY);

            mMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
            Matrix.setIdentityM(mMatrixProjection, 0);
            Matrix.orthoM(mMatrixProjection, 0, -ORTHO, ORTHO, -ORTHO, ORTHO, -1f, 1f);
            Matrix.translateM(mMatrixProjection, 0, 0, 0, 0f);
            Matrix.scaleM(mMatrixProjection, 0, mScaleX, mScaleY, 1f);
            GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, mMatrixProjection, 0);
            Utils.texImage2D(bitmap, mTextures[0], false);

            final int textureCoordHandle = GLES20.glGetAttribLocation(mProgram, "aTextureCoord");
            GLES20.glEnableVertexAttribArray(textureCoordHandle);
            GLES20.glVertexAttribPointer(textureCoordHandle, 2, GLES20.GL_FLOAT, false, 8, mTexVertexBuffer);

            mAlphaHandle = GLES20.glGetUniformLocation(mProgram, "uAlpha");
            final float alpha = 1f;
            GLES20.glUniform1f(mAlphaHandle, alpha);

            final int textureHandle = GLES20.glGetUniformLocation(mProgram, "uTexture");
            GLES20.glUniform1i(textureHandle, 0);

            Log.v("Wbj", "onDrawFrame, textureCoordHandle: " + textureCoordHandle + ", textureHandle: " + textureHandle + ", textureHandle: " + ", alphaHandle: " + mAlphaHandle);

            GLES20.glDrawElements(GLES20.GL_TRIANGLES, mVertexIndexBuffer.capacity(), GLES20.GL_UNSIGNED_SHORT, mVertexIndexBuffer);

            //pint the second texture
            this.onDrawFrame2(bitmap);

            GLES20.glDisableVertexAttribArray(positionHandle);
            GLES20.glDisableVertexAttribArray(textureCoordHandle);

            GLES20.glDisable(GLES20.GL_BLEND); //disable alpha blending
        }

        public void onDrawFrame2(Bitmap bitmap) {
            mScaleX = bitmap.getWidth() * SCALE_RATIO2 / mWidth;
            mScaleY = bitmap.getHeight() * SCALE_RATIO2 / mHeight;
            mTranslateX = ORTHO - mScaleX;
            mTranslateY = ORTHO - mScaleY;

            Matrix.setIdentityM(mMatrixProjection, 0);
            Matrix.orthoM(mMatrixProjection, 0, -ORTHO, ORTHO, -ORTHO, ORTHO, -1f, 1f);
            Matrix.translateM(mMatrixProjection, 0, -mTranslateX, -mTranslateY, 0f);
            Matrix.scaleM(mMatrixProjection, 0, mScaleX, mScaleY, 1f);
            GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, mMatrixProjection, 0);
            Utils.texImage2D(bitmap, mTextures[1], true);

            GLES20.glUniform1f(mAlphaHandle, SCALE_ALPHA2);

            final int textureHandle2 = GLES20.glGetUniformLocation(mProgram, "uTexture");
            GLES20.glUniform1i(textureHandle2, 0);

            GLES20.glDrawElements(GLES20.GL_TRIANGLES, mVertexIndexBuffer.capacity(), GLES20.GL_UNSIGNED_SHORT, mVertexIndexBuffer);
        }

        public void onDestroy() {
            Log.i("Wbj", "ImageRenderer onDestroy");
            GLES20.glDeleteProgram(mProgram);
            if (mTextures != null) {
                GLES20.glDeleteTextures(mTextures.length, mTextures, 0);
            }
        }
    }

}
