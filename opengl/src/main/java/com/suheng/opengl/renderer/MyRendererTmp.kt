package com.suheng.opengl.renderer

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class MyRendererTmp {
    private val mVertexBuffer: FloatBuffer = ByteBuffer.allocateDirect(VERTEX.size * 4)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()
        .put(VERTEX)

    init {
        mVertexBuffer.position(0)
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)
        return shader
    }

    fun onSurfaceCreated() {
        //GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClearColor(0f, 255f, 0f, 0f)

        val program = GLES20.glCreateProgram()
        val vertexShader = this.loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER)
        val fragmentShader = this.loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER)
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        GLES20.glLinkProgram(program)

        GLES20.glUseProgram(program)

        val position = GLES20.glGetAttribLocation(program, "vPosition")
        GLES20.glEnableVertexAttribArray(position)
        GLES20.glVertexAttribPointer(
            position, 3, GLES20.GL_FLOAT, false,
            12, mVertexBuffer
        )
    }

    fun onSurfaceChanged(width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    fun onDrawFrame() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3)
    }

    companion object {
        private const val VERTEX_SHADER = ("attribute vec4 vPosition;\n"
                + "void main() {\n"
                + "  gl_Position = vPosition;\n"
                + "}")
        private const val FRAGMENT_SHADER = ("precision mediump float;\n"
                + "void main() {\n"
                + "  gl_FragColor = vec4(0.5, 0, 0, 1);\n"
                + "}")
        private val VERTEX = floatArrayOf(
            //in counterclockwise order:
            0f, 1f, 0f,  // top
            -0.5f, -1f, 0f,  // bottom left
            1f, -1f, 0f,  // bottom right
        )
    }

}
