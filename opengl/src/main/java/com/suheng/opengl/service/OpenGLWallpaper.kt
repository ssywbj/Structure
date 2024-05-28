package com.suheng.opengl.service

import android.opengl.EGL14
import android.opengl.EGL14.EGL_CONTEXT_CLIENT_VERSION
import android.opengl.GLES10
import android.opengl.GLES20
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import com.suheng.opengl.renderer.MyRendererTmp
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.egl.EGLContext
import javax.microedition.khronos.egl.EGLDisplay
import javax.microedition.khronos.opengles.GL10


class OpenGLWallpaper : WallpaperService() {
    override fun onCreateEngine(): Engine = OpenGLEngine()

    inner class OpenGLEngine : Engine() {
        //private lateinit var glThread: GLThread2
        //private lateinit var glThread: GLThread
        private lateinit var myRendererTmp: MyRendererTmp

        override fun onCreate(surfaceHolder: SurfaceHolder) {
            super.onCreate(surfaceHolder)
        }

        override fun onDestroy() {
            super.onDestroy()
        }

        override fun onSurfaceCreated(holder: SurfaceHolder) {
            super.onSurfaceCreated(holder)
            //glThread = GLThread2(holder)
            //glThread = GLThread(holder)
            //glThread.start()
            myRendererTmp = MyRendererTmp(holder.surface)
            myRendererTmp.onSurfaceCreated()
        }

        override fun onSurfaceChanged(
            holder: SurfaceHolder?,
            format: Int,
            width: Int,
            height: Int
        ) {
            super.onSurfaceChanged(holder, format, width, height)
            myRendererTmp.onSurfaceChanged(width, height)
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder?) {
            super.onSurfaceDestroyed(holder)
            //glThread.requestExitAndWait()
            myRendererTmp.onSurfaceDestroyed()
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            //glThread.setPaused(!visible)
            if (visible) {
                myRendererTmp.onDrawFrame()
            }
        }
    }

    internal class GLThread(private val surfaceHolder: SurfaceHolder) : Thread() {
        private var paused = false
        private var shouldExit = false

        override fun run() {
            val egl = EGLContext.getEGL() as EGL10
            val display: EGLDisplay = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY)
            val version = IntArray(2)
            //egl.eglInitialize(display, null)
            egl.eglInitialize(display, version)

            val configs = arrayOfNulls<EGLConfig>(1)
            val numConfigs = IntArray(1)
            val configSpec = intArrayOf(
                EGL10.EGL_RED_SIZE, 5,
                EGL10.EGL_GREEN_SIZE, 6,
                EGL10.EGL_BLUE_SIZE, 5,
                EGL10.EGL_DEPTH_SIZE, 16,
                EGL10.EGL_SURFACE_TYPE, EGL10.EGL_WINDOW_BIT,
                EGL10.EGL_NONE
            )
            egl.eglChooseConfig(display, configSpec, configs, 1, numConfigs)
            val config = configs[0]

            val glContext = egl.eglCreateContext(display, config, EGL10.EGL_NO_CONTEXT, null)
            val surface = egl.eglCreateWindowSurface(display, config, surfaceHolder, null)
            egl.eglMakeCurrent(display, surface, surface, glContext)

            // Here we start the actual OpenGL ES rendering
            GLES10.glClearColor(0f, 0f, 0f, 1f)

            while (!shouldExit) {
                if (!paused) {
                    GLES10.glClear(GLES10.GL_COLOR_BUFFER_BIT)

                    // Drawing code here
                    drawTriangle()

                    egl.eglSwapBuffers(display, surface)
                }

                synchronized(this) {
                    if (paused) {
                        try {
                            (this as Object).wait()
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }
                    }
                }
            }

            egl.eglDestroySurface(display, surface)
            egl.eglDestroyContext(display, glContext)
            egl.eglTerminate(display)
        }

        fun setPaused(paused: Boolean) {
            synchronized(this) {
                this.paused = paused
                if (!paused) {
                    (this as Object).notify()
                }
            }
        }

        fun requestExitAndWait() {
            shouldExit = true
            setPaused(false)
            try {
                join()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }

        private fun drawTriangle() {
            // Define vertices of the triangle
            val vertices = floatArrayOf(
                0.0f, 0.62200844f, 0.0f,
                -0.5f, -0.31100425f, 0.0f,
                0.5f, -0.31100425f, 0.0f
            )

            val vertexBuffer = ByteBuffer.allocateDirect(vertices.size * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
            vertexBuffer.put(vertices)
            vertexBuffer.position(0)

            GLES10.glEnableClientState(GL10.GL_VERTEX_ARRAY)
            GLES10.glVertexPointer(3, GLES10.GL_FLOAT, 0, vertexBuffer)
            GLES10.glDrawArrays(GLES10.GL_TRIANGLES, 0, vertices.size / 3)
            GLES10.glDisableClientState(GL10.GL_VERTEX_ARRAY)
        }
    }

    internal class GLThread2(private val surfaceHolder: SurfaceHolder) : Thread() {

        private val vertexShaderCode =
            "attribute vec4 a_Position;" +
            "void main() {" +
            "  gl_Position = a_Position;" +
            "}"

        private val fragmentShaderCode =
            "precision mediump float;" +
            "uniform vec4 u_Color;" +
            "void main() {" +
            "  gl_FragColor = u_Color;" +
            "}"

        // 添加用于保存着色器程序ID的变量
        private var program = 0

        private var paused = false
        private var shouldExit = false

        override fun run() {
            val egl = EGLContext.getEGL() as EGL10
            val display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY)
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
                EGL10.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT, //EGL_OPENGL_ES2_BIT表示支持OpenGL ES 2.0
                EGL10.EGL_NONE
            )

            egl.eglChooseConfig(display, configSpec, configs, 1, numConfigs)
            val config = configs[0]

            val attribList = intArrayOf(EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE)
            val glContext = egl.eglCreateContext(display, config, EGL10.EGL_NO_CONTEXT, attribList)
            val surface = egl.eglCreateWindowSurface(display, config, surfaceHolder, null)
            egl.eglMakeCurrent(display, surface, surface, glContext)

            program = createAndUseProgram(vertexShaderCode, fragmentShaderCode);

            // Here we start the actual OpenGL ES rendering
            //GLES10.glClearColor(0f, 0f, 0f, 1f)
            GLES20.glClearColor(0f, 0f, 0f, 1f);

            while (!shouldExit) {
                if (!paused) {
                    //GLES10.glClear(GLES10.GL_COLOR_BUFFER_BIT)
                    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

                    // Drawing code here
                    drawTriangle()

                    egl.eglSwapBuffers(display, surface)
                }

                synchronized(this) {
                    if (paused) {
                        try {
                            (this as Object).wait()
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }
                    }
                }
            }

            egl.eglDestroySurface(display, surface)
            egl.eglDestroyContext(display, glContext)
            egl.eglTerminate(display)
        }

        fun setPaused(paused: Boolean) {
            synchronized(this) {
                this.paused = paused
                if (!paused) {
                    (this as Object).notify()
                }
            }
        }

        fun requestExitAndWait() {
            shouldExit = true
            setPaused(false)
            try {
                join()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }

        // 绘制三角形的方法
        private fun drawTriangle() {
            val vertices = floatArrayOf(
                0.0f, 0.5f, 0.0f,
                -0.5f, -0.5f, 0.0f,
                0.5f, -0.5f, 0.0f
            )
            val vertexBuffer = ByteBuffer.allocateDirect(vertices.size * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
            vertexBuffer.put(vertices)
            vertexBuffer.position(0)

            val positionHandle = GLES20.glGetAttribLocation(program, "a_Position")
            GLES20.glEnableVertexAttribArray(positionHandle)
            GLES20.glVertexAttribPointer(
                positionHandle,
                3,
                GLES20.GL_FLOAT,
                false,
                12,
                vertexBuffer
            )

            val colorHandle = GLES20.glGetUniformLocation(program, "u_Color")
            GLES20.glUniform4f(colorHandle, 1.0f, 0.0f, 0.0f, 1.0f) // 红色

            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3)

            GLES20.glDisableVertexAttribArray(positionHandle)
        }

        private fun createAndUseProgram(vertexShaderCode: String, fragmentShaderCode: String): Int {
            val vertexShader: Int = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
            val fragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

            val program = GLES20.glCreateProgram()
            GLES20.glAttachShader(program, vertexShader)
            GLES20.glAttachShader(program, fragmentShader)
            GLES20.glLinkProgram(program)

            GLES20.glUseProgram(program)
            return program
        }

        private fun loadShader(type: Int, shaderCode: String): Int {
            val shader = GLES20.glCreateShader(type)
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
            return shader
        }
    }

}