package com.suheng.opengl.renderer;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.suheng.opengl.Utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyRenderer implements GLSurfaceView.Renderer {

    private static final String VERTEX_SHADER =
            "attribute vec4 vPosition;\n" +
            "void main() {\n" +
            "  gl_Position = vPosition;\n" +
            "}";
    private static final String FRAGMENT_SHADER =
            "precision mediump float;\n" +
            "void main() {\n" +
            "  gl_FragColor = vec4(0.5, 0, 0, 1);\n" +
            "}";
    private static final float[] VERTEX = { //in counterclockwise order:
            0, 1, 0,  // top
            -0.5f, -1, 0,  // bottom left
            1, -1, 0,  // bottom right
    };

    private final FloatBuffer mVertexBuffer;

    public MyRenderer() {
        mVertexBuffer = ByteBuffer.allocateDirect(VERTEX.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(VERTEX);
        mVertexBuffer.position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        int program = GLES20.glCreateProgram();
        int vertexShader = Utils.loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER);
        int fragmentShader = Utils.loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER);
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);

        GLES20.glUseProgram(program);

        int position = GLES20.glGetAttribLocation(program, "vPosition");
        GLES20.glEnableVertexAttribArray(position);
        GLES20.glVertexAttribPointer(position, 3, GLES20.GL_FLOAT, false,
                12, mVertexBuffer);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
    }

}
