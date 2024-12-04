package com.suheng.opengl.renderer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.suheng.opengl.R;
import com.suheng.opengl.Utils;

import java.io.File;
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
    public static final float ALPHA_RATIO2 = 0.4f;

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
        private static final int VERTEX_COMPONENTS = 2;
        private static final int POINT_TOTAL = 4;
        private static final int VERTEX_ANCHOR = POINT_TOTAL * VERTEX_COMPONENTS;

        private static final int TEXTURE_POINTS = 4;
        private static final int TEXTURE_COMPONENTS = 2;
        private static final int TEXTURE_ANCHORS = TEXTURE_POINTS * TEXTURE_COMPONENTS;

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
        private int mRendererTypeHandle;

        private final Handler mMainThread = new Handler(Looper.getMainLooper());
        private final Handler mWorkThread;
        private boolean mIsDestroyed;

        public ImageRenderer() {
            mVertexBuffer = ByteBuffer.allocateDirect(VERTEX_ANCHOR * 4)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            final float[] coordinates = {
                    1f, 1f,
                    -1f, 1f,
                    -1f, -1f,
                    1f, -1f
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
                    1f, 0f, //bottom right
                    0f, 0f, //bottom left
                    0f, 1f, //top left
                    1f, 1f, //top right
            };
            mTexVertexBuffer = ByteBuffer.allocateDirect(TEXTURE_ANCHORS * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer()
                    .put(texVertex);
            mTexVertexBuffer.position(0);

            mProgram = Utils.glCreateProgram(mContext, R.raw.image_renderer_vertex
                    , R.raw.image_renderer_fragment);

            HandlerThread workerThread = new HandlerThread("WorkerThread");
            workerThread.start();
            mWorkThread = new Handler(workerThread.getLooper());
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
            mTextures = Utils.glGenTextures(1);
            if (mTextures == null) {
                return;
            }
            Log.d("Wbj", "textures: " + Arrays.toString(mTextures));

            //support alpha blending
            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
            //GLES20.glBlendFuncSeparate(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA, GLES20.GL_ONE, GLES20.GL_ZERO);

            GLES20.glUseProgram(mProgram);

            final int positionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
            GLES20.glEnableVertexAttribArray(positionHandle);
            GLES20.glVertexAttribPointer(positionHandle, VERTEX_COMPONENTS, GLES20.GL_FLOAT, false
                    , VERTEX_ANCHOR, mVertexBuffer);

            Log.d("Wbj", "onDrawFrame, width: " + mWidth + ", height: " + mHeight
                    + ", rectWidth: " + bitmap.getWidth() + ", rectHeight: " + bitmap.getWidth());
            mScaleX = bitmap.getWidth() * SCALE_RATIO / mWidth;
            mScaleY = bitmap.getHeight() * SCALE_RATIO / mHeight;
            Log.v("Wbj", "onDrawFrame, scaleX: " + mScaleX + ", scaleY: " + mScaleY
                    + ", translateX: " + mTranslateX + ", translateY: " + mTranslateY);

            Utils.texImage2D(bitmap, mTextures[0], true); //texture only loaded once, not every frame

            mMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
            Matrix.setIdentityM(mMatrixProjection, 0);
            Matrix.orthoM(mMatrixProjection, 0, -ORTHO, ORTHO, -ORTHO, ORTHO, -1f, 1f);
            Matrix.translateM(mMatrixProjection, 0, 0, 0, 0f);
            Matrix.scaleM(mMatrixProjection, 0, mScaleX, mScaleY, 1f);
            GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, mMatrixProjection, 0);

            final int coordinateHandle = GLES20.glGetAttribLocation(mProgram, "aCoordinate");
            GLES20.glEnableVertexAttribArray(coordinateHandle);
            GLES20.glVertexAttribPointer(coordinateHandle, TEXTURE_COMPONENTS, GLES20.GL_FLOAT, false
                    , TEXTURE_ANCHORS, mTexVertexBuffer);

            mAlphaHandle = GLES20.glGetUniformLocation(mProgram, "uAlpha");
            final float alphaRatio = 1f;
            GLES20.glUniform1f(mAlphaHandle, alphaRatio);

            mRendererTypeHandle = GLES20.glGetUniformLocation(mProgram, "uRendererType");
            GLES20.glUniform1i(mRendererTypeHandle, 0);

            final int textureHandle = GLES20.glGetUniformLocation(mProgram, "uTexture");
            GLES20.glUniform1i(textureHandle, 0);

            Log.d("Wbj", "onDrawFrame, positionHandle: " + positionHandle + ", coordinateHandle: " + coordinateHandle);
            Log.v("Wbj", "onDrawFrame, textureHandle: " + textureHandle + ", alphaHandle: " + mAlphaHandle
                    + ", rendererTypeHandle: " + mRendererTypeHandle + ", matrixHandle: " + mMatrixHandle);

            GLES20.glDrawElements(GLES20.GL_TRIANGLES, mVertexIndexBuffer.capacity(), GLES20.GL_UNSIGNED_SHORT, mVertexIndexBuffer);

            final long startTime = System.currentTimeMillis();
            final ByteBuffer byteBuffer = Utils.glCreateReadPixels(mWidth, mHeight);
            Log.i("Wbj", "onDrawFrame, glCreateReadPixels take time: " + (System.currentTimeMillis() - startTime) / 1000f + "s");
            mWorkThread.post(() -> {
                final long start = System.currentTimeMillis();
                String fileName = System.currentTimeMillis() + "_" + mWidth + "_" + mHeight + ".png";
                String path = mContext.getCacheDir() + File.separator + fileName;
                Utils.bufferToFile(byteBuffer, path, mWidth, mHeight);
                Log.i("Wbj", "onDrawFrame, bufferToFile take time: " + (System.currentTimeMillis() - start) / 1000f + "s"
                        + ", path: " + path + ", thread: " + Thread.currentThread().getName());

                if (!mIsDestroyed) {
                    mMainThread.post(() -> Toast.makeText(mContext, "save success thread: "
                            + Thread.currentThread().getName(), Toast.LENGTH_SHORT).show());
                }
            });

            //pint the second texture
            this.onDrawFrame2(bitmap);
            this.onDrawFrame3(bitmap);
            this.onDrawFrame4(bitmap);

            GLES20.glDisableVertexAttribArray(positionHandle);
            GLES20.glDisableVertexAttribArray(coordinateHandle);

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

            GLES20.glUniform1i(mRendererTypeHandle, 0);

            GLES20.glUniform1f(mAlphaHandle, ALPHA_RATIO2);

            GLES20.glDrawElements(GLES20.GL_TRIANGLES, mVertexIndexBuffer.capacity(), GLES20.GL_UNSIGNED_SHORT, mVertexIndexBuffer);

            final ByteBuffer byteBuffer = Utils.glCreateReadPixels(mWidth, mHeight);
            mWorkThread.post(() -> {
                String fileName = System.currentTimeMillis() + "_" + mWidth + "_" + mHeight + ".png";
                String path = mContext.getCacheDir() + File.separator + fileName;
                Utils.bufferToFile(byteBuffer, path, mWidth, mHeight);
            });
        }

        public void onDrawFrame3(Bitmap bitmap) {
            Matrix.setIdentityM(mMatrixProjection, 0);
            Matrix.orthoM(mMatrixProjection, 0, -ORTHO, ORTHO, -ORTHO, ORTHO, -1f, 1f);
            Matrix.translateM(mMatrixProjection, 0, -mTranslateX, mTranslateY, 0f);
            Matrix.scaleM(mMatrixProjection, 0, mScaleX, mScaleY, 1f);
            GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, mMatrixProjection, 0);

            GLES20.glUniform1i(mRendererTypeHandle, 1);

            //blur parameters
            final int radiusHandle = GLES20.glGetUniformLocation(mProgram, "uRadius");
            final int stepsHandle = GLES20.glGetUniformLocation(mProgram, "uSteps");
            final int texWidthHandle = GLES20.glGetUniformLocation(mProgram, "uTexWidth");
            final int texHeightHandle = GLES20.glGetUniformLocation(mProgram, "uTexHeight");
            GLES20.glUniform1f(stepsHandle, 2.5f);
            GLES20.glUniform1i(radiusHandle, 10);
            GLES20.glUniform1f(texWidthHandle, bitmap.getWidth());
            GLES20.glUniform1f(texHeightHandle, bitmap.getHeight());
            Log.v("Wbj", "onDrawFrame3, radiusHandle: " + radiusHandle + ", stepsHandle: " + stepsHandle
                    + ", texWidthHandle: " + texWidthHandle + ", texHeightHandle: " + texHeightHandle);

            GLES20.glDrawElements(GLES20.GL_TRIANGLES, mVertexIndexBuffer.capacity(), GLES20.GL_UNSIGNED_SHORT, mVertexIndexBuffer);
        }

        public void onDrawFrame4(Bitmap bitmap) {
            Matrix.setIdentityM(mMatrixProjection, 0);
            Matrix.orthoM(mMatrixProjection, 0, -ORTHO, ORTHO, -ORTHO, ORTHO, -1f, 1f);
            Matrix.translateM(mMatrixProjection, 0, mTranslateX, mTranslateY, 0f);
            Matrix.scaleM(mMatrixProjection, 0, mScaleX, mScaleY, 1f);
            GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, mMatrixProjection, 0);

            GLES20.glUniform1i(mRendererTypeHandle, 2);

            final int blurRadiusLocation = GLES20.glGetUniformLocation(mProgram, "uBlurRadius");
            final int blurOffsetLocation = GLES20.glGetUniformLocation(mProgram, "uBlurOffset");
            final int sumWeightLocation = GLES20.glGetUniformLocation(mProgram, "uSumWeight");
            this.setBlurOffset(1, 0);
            this.calculateSumWeight();

            GLES20.glUniform1i(blurRadiusLocation, blurRadius);
            int width = bitmap.getWidth();
            float var1 = blurOffsetW / width;
            int height = bitmap.getHeight();
            float var2 = blurOffsetH / height;
            GLES20.glUniform2f(blurOffsetLocation, var1, var2);
            GLES20.glUniform1f(sumWeightLocation, sumWeight);

            //blurRadius: 30, (0.0018518518, 0.0), sumWeight: 0.99683464, width: 540, height: 1113
            //blurRadius: 30, (0.0, 8.984726E-4), sumWeight: 0.99683464, width: 540, height: 1113

            //blurRadius: 30, (0.0014641288, 0.0), sumWeight: 0.99683464, width: 683, height: 993
            //blurRadius: 30, (0.0014641288, 0.0), sumWeight: 0.99683464, width: 683, height: 993
            Log.d("Wbj", "blurRadius: " + blurRadius + ", (" + var1 + ", " + var2 + ")" + ", sumWeight: " + sumWeight
                    + ", width: " + width + ", height: " + height);
            /*GLES20.glUniform1i(blurRadiusLocation, 30);
            GLES20.glUniform2f(blurOffsetLocation, 0.0018518518f, 0.0f);
            GLES20.glUniform1f(sumWeightLocation, 0.99683464f);*/

            GLES20.glDrawElements(GLES20.GL_TRIANGLES, mVertexIndexBuffer.capacity(), GLES20.GL_UNSIGNED_SHORT, mVertexIndexBuffer);
        }

        private final int blurRadius = 100;
        private float sumWeight;
        private float blurOffsetW;
        private float blurOffsetH;

        /**
         * 计算总权重
         */
        private void calculateSumWeight() {
            if (blurRadius < 1) {
                Log.d("Wbj", "calculateSumWeight: blurRadius:" + blurRadius + " w:" + blurOffsetW + " h:" + blurOffsetH);
                setSumWeight(0);
                return;
            }

            float sumWeight = 0;
            float sigma = blurRadius / 3f;
            for (int i = 0; i < blurRadius; i++) {
                float weight = (float) ((1 / Math.sqrt(2 * Math.PI * sigma * sigma)) * Math.exp(-(i * i) / (2 * sigma * sigma)));
                sumWeight += weight;
                if (i != 0) {
                    sumWeight += weight;
                }
            }

            setSumWeight(sumWeight);
        }

        private void setBlurOffset(float width, float height) {
            this.blurOffsetW = width;
            this.blurOffsetH = height;
        }

        private void setSumWeight(float sumWeight) {
            Log.d("Wbj", "setSumWeight: " + sumWeight);
            this.sumWeight = sumWeight;
        }

        public void onDestroy() {
            Log.i("Wbj", "ImageRenderer onDestroy");
            mIsDestroyed = true;
            GLES20.glDeleteProgram(mProgram);
            if (mTextures != null) {
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0); //Unbind texture
                GLES20.glDeleteTextures(mTextures.length, mTextures, 0);
            }
            mWorkThread.getLooper().quitSafely();
        }
    }

}
