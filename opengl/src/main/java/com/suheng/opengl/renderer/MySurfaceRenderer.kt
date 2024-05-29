package com.suheng.opengl.renderer

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.Log
import android.view.Surface
import com.suheng.opengl.Utils
import com.suheng.opengl.manager.GLES20Helper
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MySurfaceRenderer(surface: Surface? = null) : GLSurfaceView.Renderer {
    companion object {
        private const val TAG = "Wbj"

        private const val VERTEX_SHADER = (
                "attribute vec4 vPosition;\n" +
                "void main() {\n" +
                "  gl_Position = vPosition;\n" +
                "}")
        private const val FRAGMENT_SHADER = (
                "precision mediump float;\n" +
                "void main() {\n" +
                "  gl_FragColor = vec4(0.5, 0, 0, 1);\n" +
                "}")
        private val VERTEX = floatArrayOf(
            //in counterclockwise order:
            0f, 1f, 0f,  // top
            -0.5f, -1f, 0f,  // bottom left
            1f, -1f, 0f,  // bottom right
        )
    }

    private val mVertexBuffer: FloatBuffer =
        ByteBuffer.allocateDirect(VERTEX.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
            .put(VERTEX)

    private var gles20Helper: GLES20Helper? = null

    init {
        mVertexBuffer.position(0)
        surface?.let {
            gles20Helper = GLES20Helper(it)
        }
    }

    fun onSurfaceCreated() {
        gles20Helper?.let {
            this.onSurfaceCreated(null, null)
        }
    }

    fun onSurfaceChanged(width: Int, height: Int) {
        gles20Helper?.let {
            this.onSurfaceChanged(null, width, height)
        }
    }

    fun onDrawFrame() {
        gles20Helper?.let {
            onDrawFrame(null)
            it.swapBuffers()
        }
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        Log.i(TAG, "onSurfaceCreated, gl10: $gl, egl config: $config")
        //GLES20.glClearColor(0f, 0f, 0f, 0f);
        GLES20.glClearColor(0f, 255f, 0f, 0f);

        val program = GLES20.glCreateProgram()
        val vertexShader = Utils.loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER)
        val fragmentShader = Utils.loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER)
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        GLES20.glLinkProgram(program)

        GLES20.glUseProgram(program)

        val position = GLES20.glGetAttribLocation(program, "vPosition")
        GLES20.glEnableVertexAttribArray(position)
        GLES20.glVertexAttribPointer(
            position, 3, GLES20.GL_FLOAT, false, 12, mVertexBuffer
        )
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        Log.d(TAG, "onSurfaceChanged, gl10: $gl, width: $width, height: $height")
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        Log.v(TAG, "onDrawFrame, gl10: $gl")
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3)
    }

    fun onSurfaceDestroyed() {
        gles20Helper?.destroyed()
    }

}
