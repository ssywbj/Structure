package com.suheng.opengl.renderer;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.suheng.opengl.Utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyRenderer2 implements GLSurfaceView.Renderer {

    /*private static final String VERTEX_SHADER =
            "attribute vec4 vPosition;\n" +
            "uniform mat4 uMVPMatrix;\n" +
            "void main() {\n" +
            "  gl_Position = uMVPMatrix * vPosition;\n" +
            "}";*/
    private static final String VERTEX_SHADER =
            "attribute vec4 vPosition;\n" +
            "void main() {\n" +
            "  gl_Position = vPosition;\n" +
            "}";
    private static final String FRAGMENT_SHADER =
            "precision mediump float;\n" +
            "void main() {\n" +
            "  gl_FragColor = vec4(0, 1, 0, 1);\n" +
            "}";

    private static final int POINT_TOTAL = 4;
    private static final int VERTEX_ANCHOR = POINT_TOTAL * 3;
    private final FloatBuffer mVertexBuffer;

    private static final short[] VERTEX_INDEX = {0, 1, 2, 0, 2, 3};
    private final ShortBuffer mVertexIndexBuffer;

    private int mMatrixHandle;
    private final float[] mMVPMatrix = new float[16];

    public MyRenderer2() {
        mVertexBuffer = ByteBuffer.allocateDirect(VERTEX_ANCHOR * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();

        mVertexIndexBuffer = ByteBuffer.allocateDirect(VERTEX_INDEX.length * 2)
                .order(ByteOrder.nativeOrder()).asShortBuffer();
        mVertexIndexBuffer.put(VERTEX_INDEX);
        mVertexIndexBuffer.position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.d("Wbj", "onSurfaceCreated");
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        int program = GLES20.glCreateProgram();
        int vertexShader = Utils.loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER);
        int fragmentShader = Utils.loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER);
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);

        GLES20.glUseProgram(program);

        final int positionHandle = GLES20.glGetAttribLocation(program, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false,
                12, mVertexBuffer);

        mMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        final int rectWidth = width / 2;
        final int rectHeight = rectWidth * 2 / 3;
        //final int rectHeight = rectWidth; //square
        final float xAnchor = 1f * rectWidth / width; //width-based, width is px unit
        //final float xAnchor = 1f; //full screen width
        final float yAnchorRatio = xAnchor * rectHeight / rectWidth; //opengl coordinate height/width ratio
        final float screenWHRatio = 1f * width / height; //screen width/height ratio
        final float yAnchor = yAnchorRatio * screenWHRatio; //height-to-width ratio
        Log.d("Wbj", "onSurfaceChanged, width: " + width + ", height: " + height
                + ", rectWidth: " + rectWidth + ", rectHeight: " + rectHeight + ", xAnchor: " + xAnchor
                + ", yAnchor: " + yAnchor + ", screenWHRatio: " + screenWHRatio
                + ", rect w/h: " + (xAnchor / yAnchorRatio));
        final float[] coordinates = {xAnchor, yAnchor, 0, -xAnchor, yAnchor, 0,
                -xAnchor, -yAnchor, 0, xAnchor, -yAnchor, 0};
        mVertexBuffer.put(coordinates);
        mVertexBuffer.position(0);

        //Matrix.perspectiveM(mMVPMatrix, 0, 45, (float) width / height, 0.1f, 100f);
        //Matrix.translateM(mMVPMatrix, 0, 0f, 0f, -3.0f); //z可控制大小
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        //GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, mMVPMatrix, 0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, mVertexIndexBuffer.capacity(), GLES20.GL_UNSIGNED_SHORT, mVertexIndexBuffer);
    }

}
