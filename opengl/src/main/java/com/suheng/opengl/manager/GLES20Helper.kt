package com.suheng.opengl.manager

import android.opengl.EGL14
import android.util.Log
import android.view.Surface
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.egl.EGLContext
import javax.microedition.khronos.egl.EGLDisplay
import javax.microedition.khronos.egl.EGLSurface

class GLES20Helper(surface: Surface) {

    companion object {
        private const val TAG = "Wbj"
    }

    private lateinit var eglDisplay: EGLDisplay
    private lateinit var egl10: EGL10
    private lateinit var eglSurface: EGLSurface
    private lateinit var eglContext: EGLContext

    init {
        this.createEnv(surface)
    }

    private fun createEnv(surface: Surface) {
        egl10 = EGLContext.getEGL() as EGL10
        eglDisplay = egl10.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY)
        val version = IntArray(2)
        egl10.eglInitialize(eglDisplay, version).also { Log.d(TAG, "egl initialize: $it") }

        val configs = arrayOfNulls<EGLConfig>(1)
        val numConfigs = IntArray(1)
        val attribChoices = intArrayOf(
            EGL10.EGL_RED_SIZE, 8,
            EGL10.EGL_GREEN_SIZE, 8,
            EGL10.EGL_BLUE_SIZE, 8,
            EGL10.EGL_ALPHA_SIZE, 8,
            EGL10.EGL_DEPTH_SIZE, 16,
            EGL10.EGL_RENDERABLE_TYPE,
            EGL14.EGL_OPENGL_ES2_BIT, //EGL_OPENGL_ES2_BIT表示支持OpenGL ES 2.0
            EGL10.EGL_NONE
        )
        egl10.eglChooseConfig(eglDisplay, attribChoices, configs, 1, numConfigs)

        val eglConfig = configs[0]
        val attribContexts = intArrayOf(EGL14.EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE)
        eglContext =
            egl10.eglCreateContext(eglDisplay, eglConfig, EGL10.EGL_NO_CONTEXT, attribContexts)
        eglSurface = egl10.eglCreateWindowSurface(eglDisplay, eglConfig, surface, null)
        egl10.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)
            .also { Log.d(TAG, "egl make current: $it") }
    }

    fun destroyed() {
        egl10.eglMakeCurrent(
            eglDisplay,
            EGL10.EGL_NO_SURFACE,
            EGL10.EGL_NO_SURFACE,
            EGL10.EGL_NO_CONTEXT
        )
        egl10.eglDestroySurface(eglDisplay, eglSurface)
        egl10.eglDestroyContext(eglDisplay, eglContext)
        egl10.eglTerminate(eglDisplay)
    }

    fun swapBuffers() {
        egl10.eglSwapBuffers(eglDisplay, eglSurface)
    }

}
