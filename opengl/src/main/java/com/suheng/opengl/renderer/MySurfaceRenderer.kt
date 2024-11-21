package com.suheng.opengl.renderer

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.Log
import android.view.Surface
import com.suheng.opengl.R
import com.suheng.opengl.Utils
import com.suheng.opengl.app.OpenGLApp
import com.suheng.opengl.manager.GLES20Helper
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MySurfaceRenderer(surface: Surface? = null) : GLSurfaceView.Renderer {
    companion object {
        private const val TAG = "Wbj"
    }

    private var gles20Helper: GLES20Helper? = null

    private var textureId: Int = 0
    private lateinit var backgroundRenderer: BackgroundRenderer
    private lateinit var rectangleRenderer: RectangleRenderer

    init {
        //mVertexBuffer.position(0)
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
        GLES20.glClearColor(0f, 0f, 0f, 0f);

        backgroundRenderer = BackgroundRenderer()
        textureId = Utils.loadTexture(OpenGLApp.getInstance(), R.drawable.air_hockey_surface)
        rectangleRenderer = RectangleRenderer()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        Log.d(TAG, "onSurfaceChanged, gl10: $gl, width: $width, height: $height")
        screenWidth = width
        screenHeight = height
        GLES20.glViewport(0, 0, width, height)
    }

    private var screenWidth: Int = 0
    private var screenHeight: Int = 0

    override fun onDrawFrame(gl: GL10?) {
        Log.v(TAG, "onDrawFrame, gl10: $gl")
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        //绘制背景
        backgroundRenderer.draw(textureId)

        val color = floatArrayOf(1f, 0f, 0f, 1f)
        rectangleRenderer.draw(color, 200, 300, screenWidth, screenHeight)
    }

    fun onSurfaceDestroyed() {
        gles20Helper?.destroyed()
    }

}
