package com.suheng.opengl.renderer;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.suheng.opengl.Utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyRenderer2 implements GLSurfaceView.Renderer {

    private static final int POINT_TOTAL = 4;
    private static final int VERTEX_ANCHOR = POINT_TOTAL * 3;

    private static final short[] VERTEX_INDEX = {0, 1, 2, 0, 2, 3};

    private RectRenderer mRectRenderer;
    private RectRenderer2 mRectRenderer2;
    private RectRenderer3 mRectRenderer3;

    private int mWidth, mHeight;

    public MyRenderer2() {
        //mRectRenderer = new RectRenderer();
        //mRectRenderer2 = new RectRenderer2();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        mRectRenderer = new RectRenderer();
        mRectRenderer2 = new RectRenderer2();
        mRectRenderer3 = new RectRenderer3();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        mWidth = width;
        mHeight = height;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        Log.v("Wbj", "onDrawFrame, gl: " + gl);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        mRectRenderer.onSurfaceCreated();
        mRectRenderer.onSurfaceChanged(mWidth, mHeight);
        mRectRenderer.onDrawFrame();

        mRectRenderer2.onSurfaceCreated();
        mRectRenderer2.onSurfaceChanged(mWidth, mHeight);
        mRectRenderer2.onDrawFrame();

        mRectRenderer3.onDrawFrame(mWidth, mHeight);
    }

    private static class RectRenderer  {
        private static final String VERTEX_SHADER =
                "attribute vec4 vPosition;\n" +
                "void main() {\n" +
                "  gl_Position = vPosition;\n" +
                "}";
        private static final String FRAGMENT_SHADER =
                "precision mediump float;\n" +
                "void main() {\n" +
                "  gl_FragColor = vec4(1, 0, 0, 1);\n" +
                "}";
        private final FloatBuffer mVertexBuffer;
        private final ShortBuffer mVertexIndexBuffer;
        private int mPositionHandle;
        private final int mProgram;

        public RectRenderer() {
            mVertexBuffer = ByteBuffer.allocateDirect(VERTEX_ANCHOR * 4)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();

            mVertexIndexBuffer = ByteBuffer.allocateDirect(VERTEX_INDEX.length * 2)
                    .order(ByteOrder.nativeOrder()).asShortBuffer();
            mVertexIndexBuffer.put(VERTEX_INDEX);
            mVertexIndexBuffer.position(0);

            mProgram = GLES20.glCreateProgram();
            final int vertexShader = Utils.loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER);
            final int fragmentShader = Utils.loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER);
            GLES20.glAttachShader(mProgram, vertexShader);
            GLES20.glAttachShader(mProgram, fragmentShader);
            GLES20.glLinkProgram(mProgram);
            Log.i("Wbj", "RectRenderer(), program: " + mProgram);
        }

        public void onSurfaceCreated() {
            GLES20.glUseProgram(mProgram);

            mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
            GLES20.glEnableVertexAttribArray(mPositionHandle);
            GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false,
                    12, mVertexBuffer);
            Log.d("Wbj", "onSurfaceCreated, positionHandle: " + mPositionHandle);
        }

        public void onSurfaceChanged(int width, int height) {
            final int rectWidth = width / 2;
            //final int rectWidth = width; //full view width
            final int rectHeight = rectWidth * 2 / 3;
            //final int rectHeight = rectWidth; //square
            final float xAnchor = 1f * rectWidth / width; //x coordinate: width-based, width is px unit
            final float yAnchorRatio = xAnchor * rectHeight / rectWidth; //opengl coordinate height/width ratio
            final float screenWHRatio = 1f * width / height; //view width/height ratio
            final float yAnchor = yAnchorRatio * screenWHRatio; //y coordinate
            Log.d("Wbj", "onSurfaceChanged, width: " + width + ", height: " + height
                    + ", rectWidth: " + rectWidth + ", rectHeight: " + rectHeight + ", xAnchor: " + xAnchor
                    + ", yAnchor: " + yAnchor + ", screenWHRatio: " + screenWHRatio
                    + ", rect w/h: " + (xAnchor / yAnchorRatio));
            final float[] coordinates = {
                    xAnchor, yAnchor, 0,
                    -xAnchor, yAnchor, 0,
                    -xAnchor, -yAnchor, 0,
                    xAnchor, -yAnchor, 0
            };
            mVertexBuffer.put(coordinates);
            mVertexBuffer.position(0);
        }

        public void onDrawFrame() {
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, mVertexIndexBuffer.capacity(), GLES20.GL_UNSIGNED_SHORT, mVertexIndexBuffer);
            GLES20.glDisableVertexAttribArray(mPositionHandle);
        }
    }

    private static class RectRenderer2  {
        private static final String VERTEX_SHADER_MAT =
                "attribute vec4 vPosition;\n" +
                "uniform mat4 uMVPMatrix;\n" +
                "void main() {\n" +
                "  gl_Position = uMVPMatrix * vPosition;\n" +
                "}";
        private static final String FRAGMENT_SHADER =
                "precision mediump float;\n" +
                "void main() {\n" +
                "  gl_FragColor = vec4(0, 1, 0, 1);\n" +
                "}";
        private final FloatBuffer mVertexBuffer;
        private final ShortBuffer mVertexIndexBuffer;
        private final int mProgram;
        private int mPositionHandle;
        private int mMatrixHandle;
        private final float[] mMatrixProjection = new float[16];

        public RectRenderer2() {
            mVertexBuffer = ByteBuffer.allocateDirect(VERTEX_ANCHOR * 4)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();

            mVertexIndexBuffer = ByteBuffer.allocateDirect(VERTEX_INDEX.length * 2)
                    .order(ByteOrder.nativeOrder()).asShortBuffer();
            mVertexIndexBuffer.put(VERTEX_INDEX);
            mVertexIndexBuffer.position(0);

            final int vertexShader = Utils.loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER_MAT);
            final int fragmentShader = Utils.loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER);
            mProgram = GLES20.glCreateProgram();
            GLES20.glAttachShader(mProgram, vertexShader);
            GLES20.glAttachShader(mProgram, fragmentShader);
            GLES20.glLinkProgram(mProgram);
            Log.i("Wbj", "RectRenderer2(), program: " + mProgram);
        }

        public void onSurfaceCreated() {
            GLES20.glUseProgram(mProgram);

            mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
            GLES20.glEnableVertexAttribArray(mPositionHandle);
            GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false,12, mVertexBuffer);

            mMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
            Log.d("Wbj", "onSurfaceCreated, uMVPMatrix: " + mMatrixHandle  + ", positionHandle: " + mPositionHandle);
        }

        public void onSurfaceChanged(int width, int height) {
            final int rectWidth = width / 2;
            //final int rectWidth = width; //full view width
            final int rectHeight = rectWidth * 2 / 3;
            //final int rectHeight = rectWidth; //square
            final float xAnchor = 1f * rectWidth / width; //x coordinate: width-based, width is px unit
            final float yAnchor = xAnchor * rectHeight / rectWidth; //y coordinate
            final float screenWHRatio = 1f * width / height; //view width/height ratio
            Log.d("Wbj", "onSurfaceChanged, width: " + width + ", height: " + height
                    + ", rectWidth: " + rectWidth + ", rectHeight: " + rectHeight + ", xAnchor: " + xAnchor
                    + ", yAnchor: " + yAnchor + ", screenWHRatio: " + screenWHRatio
                    + ", rect w/h: " + (xAnchor / yAnchor));
            final float[] coordinates = {
                    xAnchor, yAnchor, 0,
                    -xAnchor, yAnchor, 0,
                    -xAnchor, -yAnchor, 0,
                    xAnchor, -yAnchor, 0
            };
            mVertexBuffer.put(coordinates);
            mVertexBuffer.position(0);

            Matrix.perspectiveM(mMatrixProjection, 0, 45, screenWHRatio, 0.1f, 100f);
            Matrix.translateM(mMatrixProjection, 0, 0f, 0.8f, -5f); //z可控制大小
            GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, mMatrixProjection, 0);
        }

        public void onDrawFrame() {
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, mVertexIndexBuffer.capacity(), GLES20.GL_UNSIGNED_SHORT, mVertexIndexBuffer);
            GLES20.glDisableVertexAttribArray(mPositionHandle);
        }
    }

    private static class RectRenderer3  {
        private static final String VERTEX_SHADER_MAT =
                "attribute vec4 vPosition;\n" +
                "uniform mat4 uMVPMatrix;\n" +
                "void main() {\n" +
                "  gl_Position = uMVPMatrix * vPosition;\n" +
                "}";
        private static final String FRAGMENT_SHADER =
                "precision mediump float;\n" +
                "void main() {\n" +
                "  gl_FragColor = vec4(0, 0, 1, 1);\n" +
                "}";
        private static final String FRAGMENT_SHADER2 =
                "precision mediump float;\n" +
                        "void main() {\n" +
                        "  gl_FragColor = vec4(0, 1, 1, 1);\n" +
                        "}";
        private final FloatBuffer mVertexBuffer;
        private final ShortBuffer mVertexIndexBuffer;
        private final int mProgram;
        private final int mProgram2;
        private final float[] mMatrixProjection = new float[16];

        public RectRenderer3() {
            mVertexBuffer = ByteBuffer.allocateDirect(VERTEX_ANCHOR * 4)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            final float[] coordinates = {
                    0.5f, 0.5f, 0,
                    -0.5f, 0.5f, 0,
                    -0.5f, -0.5f, 0,
                    0.5f, -0.5f, 0
            };
            mVertexBuffer.put(coordinates);
            mVertexBuffer.position(0);

            mVertexIndexBuffer = ByteBuffer.allocateDirect(VERTEX_INDEX.length * 2)
                    .order(ByteOrder.nativeOrder()).asShortBuffer();
            mVertexIndexBuffer.put(VERTEX_INDEX);
            mVertexIndexBuffer.position(0);

            final int vertexShader = Utils.loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER_MAT);
            final int fragmentShader = Utils.loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER);
            mProgram = GLES20.glCreateProgram();
            GLES20.glAttachShader(mProgram, vertexShader);
            GLES20.glAttachShader(mProgram, fragmentShader);
            GLES20.glLinkProgram(mProgram);

            final int fragmentShader2 = Utils.loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER2);
            mProgram2 = GLES20.glCreateProgram();
            GLES20.glAttachShader(mProgram2, vertexShader);
            GLES20.glAttachShader(mProgram2, fragmentShader2);
            GLES20.glLinkProgram(mProgram2);
            Log.i("Wbj", "RectRenderer3(), program: " + mProgram + " , mProgram2: " + mProgram2);
        }

        /*public void onSurfaceCreated() {
        }

        public void onSurfaceChanged() {
        }*/

        public void onDrawFrame(int width, int height) {
            GLES20.glUseProgram(mProgram);

            final int positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
            GLES20.glEnableVertexAttribArray(positionHandle);
            GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false,12, mVertexBuffer);

            final int rectWidth = width / 2;
            //final int rectWidth = width; //full view width
            final int rectHeight = rectWidth * 2 / 3;
            //final int rectHeight = rectWidth; //square
            final float screenWHRatio = 1f * width / height; //view width/height ratio
            Log.d("Wbj", "onDrawFrame, width: " + width + ", height: " + height
                    + ", rectWidth: " + rectWidth + ", rectHeight: " + rectHeight + ", screenWHRatio: " + screenWHRatio
                    + ", rect w/h: " + (1f * rectWidth / rectHeight));

            final int matrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
            Matrix.setIdentityM(mMatrixProjection, 0);
            Log.v("Wbj", "onDrawFrame, setIdentityM: " + Arrays.toString(mMatrixProjection));
            Matrix.orthoM(mMatrixProjection, 0, -screenWHRatio, screenWHRatio, -1f, 1f, -1f, 1f);
            Log.v("Wbj", "onDrawFrame, orthoM: " + Arrays.toString(mMatrixProjection));
            Matrix.translateM(mMatrixProjection, 0, 0f, -0.38f, 0f);
            Log.v("Wbj", "onDrawFrame, translateM: " + Arrays.toString(mMatrixProjection));
            Matrix.scaleM(mMatrixProjection, 0, 1f * rectWidth / width, 2f * rectHeight / height, 1f);
            Log.v("Wbj", "onDrawFrame, scaleM: " + Arrays.toString(mMatrixProjection));
            /*Matrix.orthoM(mMatrixProjection, 0, -screenWHRatio, screenWHRatio, -screenWHRatio, screenWHRatio, -1f, 1f);
            Matrix.translateM(mMatrixProjection, 0, 0f, -0.19f, 0f);
            Matrix.scaleM(mMatrixProjection, 0, 1f * rectWidth / width, 1f * rectHeight / height, 1f);*/
            GLES20.glUniformMatrix4fv(matrixHandle, 1, false, mMatrixProjection, 0);

            GLES20.glDrawElements(GLES20.GL_TRIANGLES, mVertexIndexBuffer.capacity(), GLES20.GL_UNSIGNED_SHORT, mVertexIndexBuffer);

            GLES20.glDisableVertexAttribArray(positionHandle);

            this.onDraw(width, height);
        }

        private void onDraw(int width, int height) {
            GLES20.glUseProgram(mProgram2);

            final int positionHandle = GLES20.glGetAttribLocation(mProgram2, "vPosition");
            GLES20.glEnableVertexAttribArray(positionHandle);
            GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false,12, mVertexBuffer);

            final int rectWidth = (int) (width / 1.5f);
            final int rectHeight = rectWidth / 3;
            final float screenWHRatio = 1f * width / height;
            final float scaleX = 1f * rectWidth / width;
            final float scaleY = 1f * rectHeight / height;
            Log.d("Wbj", "onDraw, width: " + width + ", height: " + height + ", rectWidth: " + rectWidth
                    + ", rectHeight: " + rectHeight + ", screenWHRatio: " + screenWHRatio);

            final int matrixHandle = GLES20.glGetUniformLocation(mProgram2, "uMVPMatrix");
            Matrix.setIdentityM(mMatrixProjection, 0);
            Log.v("Wbj", "onDraw, setIdentityM: " + Arrays.toString(mMatrixProjection));
            Matrix.orthoM(mMatrixProjection, 0, -screenWHRatio, screenWHRatio, -screenWHRatio, screenWHRatio, -1f, 1f);
            //center:(0f, 0f)
            //bottom center:(0f, -translateY)
            //bottom right:(translateX, -translateY)
            //bottom left:(-translateX, -translateY)
            //top center:(0f, translateY)
            //top right:(translateX, translateY)
            //top left:(-translateX, translateY)
            //left center:(-translateX, 0f)
            //right center:(translateX, 0f)
            final float translateX = screenWHRatio - scaleX / 2;
            final float translateY = screenWHRatio - scaleY / 2;

            Log.v("Wbj", "onDraw, translateX: " + translateX + ", translateY: " + translateY
                    + ", scaleX: " + scaleX + ", scaleY: " + scaleY);
            Matrix.translateM(mMatrixProjection, 0, translateX, -translateY, 0f);
            Matrix.scaleM(mMatrixProjection, 0, scaleX, scaleY, 1f);
            Log.v("Wbj", "onDraw, orthoM, translateM, scaleM: " + Arrays.toString(mMatrixProjection));
            GLES20.glUniformMatrix4fv(matrixHandle, 1, false, mMatrixProjection, 0);

            GLES20.glDrawElements(GLES20.GL_TRIANGLES, mVertexIndexBuffer.capacity(), GLES20.GL_UNSIGNED_SHORT, mVertexIndexBuffer);

            GLES20.glDisableVertexAttribArray(positionHandle);
        }
    }

}
