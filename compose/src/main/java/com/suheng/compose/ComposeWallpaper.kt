package com.suheng.compose

import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder

class ComposeWallpaper : WallpaperService() {

    override fun onCreateEngine(): Engine = ComposeEngin()

    inner class ComposeEngin : Engine() {
        override fun onSurfaceCreated(holder: SurfaceHolder?) {
            super.onSurfaceCreated(holder)
        }

        override fun onSurfaceChanged(
            holder: SurfaceHolder?,
            format: Int,
            width: Int,
            height: Int
        ) {
            super.onSurfaceChanged(holder, format, width, height)
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder?) {
            super.onSurfaceDestroyed(holder)
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
        }
    }

}