package com.suheng.opengl.renderer;

import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import androidx.annotation.ColorInt;

import com.suheng.opengl.Utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

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
            "  gl_FragColor = vec4(1, 0, 0, 1);\n" +
            "}";

    //Coordinate(x, y, z): x,y,z∈[-1,1], x=-1 is the farthest left，x=1 is the farthest right，
    //y=-1 is the lowest，y=1 is the highest
    private static final float[] VERTEX_TRIANGLE = { //draw a triangle, in counterclockwise order:
            0, 1, 0,  //top: the center of the top of the screen
            -0.5f, -1, 0,  //bottom left: a quarter from the bottom of the screen
            1, -1, 0,  //bottom right: the right side of the bottom of the screen
    };

    private static final float[] VERTEX_TRIANGLE_CENTER = { //draw a triangle, in counterclockwise order:
            0, 0.5f, 0,  //top
            -0.5f, -0.5f, 0,  //bottom left
            0.5f, -0.5f, 0,  //bottom right
    };

    private final FloatBuffer mVertexBuffer;

    private static final String FRAGMENT_SHADER_COL =
            "precision mediump float;\n" +
            "uniform vec4 vColor;\n" +
            "void main() {\n" +
            "  gl_FragColor = vColor;\n" +
            "}";
    private static final Float FULL_COLOR_COMPONENT = 255f;
    private final @ColorInt int mColor;

    private static final float RECTANGLE_SIDE_LEN = 0.7f;
    private static final float[] VERTEX_RECTANGLE_STANDARD_POINTS = { //draw a rectangle, in counterclockwise order:
            RECTANGLE_SIDE_LEN, RECTANGLE_SIDE_LEN, 0,
            -RECTANGLE_SIDE_LEN, RECTANGLE_SIDE_LEN, 0,
            -RECTANGLE_SIDE_LEN, -RECTANGLE_SIDE_LEN, 0,
            RECTANGLE_SIDE_LEN, RECTANGLE_SIDE_LEN, 0,
            -RECTANGLE_SIDE_LEN, -RECTANGLE_SIDE_LEN, 0,
            RECTANGLE_SIDE_LEN, -RECTANGLE_SIDE_LEN, 0,
    };
    private final boolean mIsRect;

    private static final float[] VERTEX_RECTANGLE_LESS_POINTS = { //draw a rectangle, in counterclockwise order:
            RECTANGLE_SIDE_LEN, RECTANGLE_SIDE_LEN, 0,
            -RECTANGLE_SIDE_LEN, RECTANGLE_SIDE_LEN, 0,
            -RECTANGLE_SIDE_LEN, -RECTANGLE_SIDE_LEN, 0,
            RECTANGLE_SIDE_LEN, -RECTANGLE_SIDE_LEN, 0,
    };
    private static final short[] VERTEX_RECTANGLE_LESS_POINTS_INDEX = {0, 1, 2, 0, 2, 3};
    private ShortBuffer mBufferRectangleIndex;
    //int array applies to the IntBuffer, short array applies to the ShortBuffer
    //private static final int[] VERTEX_RECTANGLE_LESS_POINTS_INDEX = {0, 1, 2, 0, 2, 3};
    //private IntBuffer mBufferRectangleIndex;
    private final boolean mIsPlotRectByIndex = true;

    public MyRenderer() {
        this(false, false, Color.RED);
    }

    public MyRenderer(boolean isRect, boolean isCenter, @ColorInt int color) {
        mIsRect = isRect;
        mColor = color;
        ByteBuffer byteBuffer;
        if (isRect) {
            if (mIsPlotRectByIndex) {
                byteBuffer = ByteBuffer.allocateDirect(VERTEX_RECTANGLE_LESS_POINTS.length * 4);
            } else {
                byteBuffer = ByteBuffer.allocateDirect(VERTEX_RECTANGLE_STANDARD_POINTS.length * 4);
            }
        } else {
            byteBuffer = ByteBuffer.allocateDirect(VERTEX_TRIANGLE.length * 4);
        }
        mVertexBuffer = byteBuffer.order(ByteOrder.nativeOrder()).asFloatBuffer();

        isCenter = isRect || isCenter;
        if (isRect) {
            if (mIsPlotRectByIndex) {
                mVertexBuffer.put(VERTEX_RECTANGLE_LESS_POINTS);

                mBufferRectangleIndex = ByteBuffer.allocateDirect(VERTEX_RECTANGLE_LESS_POINTS_INDEX.length * 2).order(ByteOrder.nativeOrder()).asShortBuffer(); //ShortBuffer multiply 2 times
                //mBufferRectangleIndex = ByteBuffer.allocateDirect(VERTEX_RECTANGLE_LESS_POINTS_INDEX.length * 4).order(ByteOrder.nativeOrder()).asIntBuffer(); //IntBuffer multiply 4 times
                mBufferRectangleIndex.put(VERTEX_RECTANGLE_LESS_POINTS_INDEX);
                mBufferRectangleIndex.position(0);
            } else {
                mVertexBuffer.put(VERTEX_RECTANGLE_STANDARD_POINTS);
            }
        } else {
            if (isCenter) {
                mVertexBuffer.put(VERTEX_TRIANGLE_CENTER);
            } else {
                mVertexBuffer.put(VERTEX_TRIANGLE);
            }
        }
        mVertexBuffer.position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        final int program = GLES20.glCreateProgram();
        final int vertexShader = Utils.loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER);
        final int fragmentShader = (mColor == Color.RED) ? Utils.loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER)
                : Utils.loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER_COL);
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);

        GLES20.glUseProgram(program);

        final int positionHandle = GLES20.glGetAttribLocation(program, "vPosition");
        Log.i("Wbj", "positionHandle: " + positionHandle);
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false,
                12, mVertexBuffer);

        final int colorHandle = GLES20.glGetUniformLocation(program, "vColor");
        Log.i("Wbj", "colorHandle: " + colorHandle + ", test: " + GLES20.glGetUniformLocation(program, "vColorTest"));
        if (colorHandle != -1) {
            //color with red, green, blue and alpha (opacity) values, all values are between 0 and 1, 1.0f is 100% of 255
            final float redRatio = Color.red(mColor) / FULL_COLOR_COMPONENT;
            final float greenRatio = Color.green(mColor) / FULL_COLOR_COMPONENT;
            final float blueRatio = Color.blue(mColor) / FULL_COLOR_COMPONENT;
            final float alphaRatio = Color.alpha(mColor) / FULL_COLOR_COMPONENT;
            //float[] rgbaV = {1f, 1f, 1f, 1.0f}; //White
            final float[] rgbaV = {redRatio, greenRatio, blueRatio, alphaRatio};
            Log.i("Wbj", "colorHandle, redRatio: " + redRatio + ", greenRatio: " + greenRatio
                    + ", blueRatio: " + blueRatio + ", alphaRatio: " + alphaRatio);
            GLES20.glUniform4fv(colorHandle, 1, rgbaV, 0);
        } else {
            Log.w("Wbj", "colorHandle, invalid color handle");
        }

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.d("Wbj", "onSurfaceChanged, width: " + width + ", height: " + height);
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        Log.d("Wbj", "onDrawFrame, called: " + gl);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        //GLES20.glDrawArrays(GLES20.GL_LINES, 0, 3); //draw a line, the second point connects to the first point
        //GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, 3);//draw lines, the last point doesn't connects to the first point
        //GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 0, 3);//draw lines, the last point connects to the first point
        //GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);//draw lines, the last point connects to the first point, and then fill area with color

        if (mIsRect) {
            if (mIsPlotRectByIndex) {
                Log.d("Wbj", "onDrawFrame, capacity: " + mBufferRectangleIndex.capacity());
                GLES20.glDrawElements(GLES20.GL_TRIANGLES, VERTEX_RECTANGLE_LESS_POINTS_INDEX.length, GLES20.GL_UNSIGNED_SHORT, mBufferRectangleIndex);
                //GLES20.glDrawElements(GLES20.GL_TRIANGLES, VERTEX_RECTANGLE_LESS_POINTS_INDEX.length, GLES20.GL_UNSIGNED_INT, mBufferRectangleIndex);
            } else {
                //first: The index of the first plotted coordinate point
                //count: The number of plotted coordinate points, divided by 3 because one point consists of three values: (x, y, z).
                GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, VERTEX_RECTANGLE_STANDARD_POINTS.length / 3); //Plot all the coordinate points (or optionally, plot only a part of them)
            }
        } else {
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, VERTEX_TRIANGLE.length / 3);
        }
    }

}
