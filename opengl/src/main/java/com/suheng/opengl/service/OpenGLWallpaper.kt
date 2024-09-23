package com.suheng.opengl.service

import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import com.suheng.opengl.renderer.MySurfaceRenderer

//https://github.com/arthabus/AndroidViewToGLRendering
class OpenGLWallpaper : WallpaperService() {
    override fun onCreateEngine(): Engine = OpenGLEngine()

    inner class OpenGLEngine : Engine() {

        private lateinit var surfaceRenderer: MySurfaceRenderer

        override fun onCreate(surfaceHolder: SurfaceHolder) {
            super.onCreate(surfaceHolder)
        }

        override fun onDestroy() {
            super.onDestroy()
        }

        override fun onSurfaceCreated(holder: SurfaceHolder) {
            super.onSurfaceCreated(holder)
            surfaceRenderer = MySurfaceRenderer(holder.surface)
            surfaceRenderer.onSurfaceCreated()
        }

        override fun onSurfaceChanged(
            holder: SurfaceHolder?,
            format: Int,
            width: Int,
            height: Int,
        ) {
            super.onSurfaceChanged(holder, format, width, height)
            surfaceRenderer.onSurfaceChanged(width, height)
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder?) {
            super.onSurfaceDestroyed(holder)
            surfaceRenderer.onSurfaceDestroyed()
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            if (visible) {
                surfaceRenderer.onDrawFrame()
            }
        }
    }

}