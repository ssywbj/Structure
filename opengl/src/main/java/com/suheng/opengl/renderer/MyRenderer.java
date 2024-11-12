package com.suheng.opengl.renderer;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

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

    //Coordinate(x, y, z): x,y,z∈[-1,1], x=-1 is the farthest left，x=1 is the farthest right，
    //y=-1 is the lowest，y=1 is the highest
    private static final float[] VERTEX = { //draw a triangle, in counterclockwise order:
            0, 1, 0,  //top: the center of the top of the screen
            -0.5f, -1, 0,  //bottom left: a quarter from the bottom of the screen
            1, -1, 0,  //bottom right: the right side of the bottom of the screen
    };

    private static final float[] VERTEX_CENTER = { //draw a triangle, in counterclockwise order:
            0, 0.5f, 0,  //top
            -0.5f, -0.5f, 0,  //bottom left
            0.5f, -0.5f, 0,  //bottom right
    };

    private final FloatBuffer mVertexBuffer;

    private final boolean isCenter;

    public MyRenderer() {
        this(false);
    }

    public MyRenderer(boolean isCenter) {
        this.isCenter = isCenter;
        mVertexBuffer = ByteBuffer.allocateDirect(VERTEX.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
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
        if (isCenter) {
            mVertexBuffer.put(VERTEX_CENTER);
        } else {
            mVertexBuffer.put(VERTEX);
        }
        mVertexBuffer.position(0);
        GLES20.glVertexAttribPointer(position, 3, GLES20.GL_FLOAT, false,
                12, mVertexBuffer);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        Log.d("Wbj", "onDrawFrame, called: " + gl);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        //GLES20.glDrawArrays(GLES20.GL_LINES, 0, 3); //draw a line, the second point connects to the first point
        //GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, 3);//draw lines, the last point doesn't connects to the first point
        //GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 0, 3);//draw lines, the last point connects to the first point
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);//draw lines, the last point connects to the first point, and then fill area with color
    }

}
