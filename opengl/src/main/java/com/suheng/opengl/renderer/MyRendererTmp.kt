package com.suheng.opengl.renderer

import android.opengl.EGL14
import android.opengl.GLES20
import android.view.Surface
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.egl.EGLContext
import javax.microedition.khronos.egl.EGLDisplay
import javax.microedition.khronos.egl.EGLSurface

class MyRendererTmp(private val surface: Surface) {
    private val mVertexBuffer: FloatBuffer =
        ByteBuffer.allocateDirect(VERTEX.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
            .put(VERTEX)

    private var display: EGLDisplay
    private var egl: EGL10
    private var eglSurface: EGLSurface
    private var glContext: EGLContext

    init {
        mVertexBuffer.position(0)

        egl = EGLContext.getEGL() as EGL10
        display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY)
        val version = IntArray(2)
        egl.eglInitialize(display, version)

        val configs = arrayOfNulls<EGLConfig>(1)
        val numConfigs = IntArray(1)
        val configSpec = intArrayOf(
            EGL10.EGL_RED_SIZE, 8,
            EGL10.EGL_GREEN_SIZE, 8,
            EGL10.EGL_BLUE_SIZE, 8,
            EGL10.EGL_ALPHA_SIZE, 8,
            EGL10.EGL_DEPTH_SIZE, 16,
            EGL10.EGL_RENDERABLE_TYPE,
            EGL14.EGL_OPENGL_ES2_BIT, //EGL_OPENGL_ES2_BIT表示支持OpenGL ES 2.0
            EGL10.EGL_NONE
        )
        egl.eglChooseConfig(display, configSpec, configs, 1, numConfigs)
        val config = configs[0]

        val attribList = intArrayOf(EGL14.EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE)
        glContext = egl.eglCreateContext(display, config, EGL10.EGL_NO_CONTEXT, attribList)
        eglSurface = egl.eglCreateWindowSurface(display, config, surface, null)
        egl.eglMakeCurrent(display, eglSurface, eglSurface, glContext)
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
            position, 3, GLES20.GL_FLOAT, false, 12, mVertexBuffer
        )
    }

    fun onSurfaceChanged(width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    fun onSurfaceDestroyed() {
        egl.eglDestroySurface(display, eglSurface)
        egl.eglDestroyContext(display, glContext)
        egl.eglTerminate(display)
    }

    fun onDrawFrame() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3)

        egl.eglSwapBuffers(display, eglSurface)
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
