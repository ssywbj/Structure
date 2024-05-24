package com.suheng.opengl.renderer

import android.opengl.GLSurfaceView

interface BaseRenderer : GLSurfaceView.Renderer {
    fun destroy()
}