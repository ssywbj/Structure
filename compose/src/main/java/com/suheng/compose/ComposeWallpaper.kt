package com.suheng.compose

import android.graphics.Color
import android.graphics.Matrix
import android.service.wallpaper.WallpaperService
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import androidx.compose.ui.platform.ComposeView
import com.suheng.compose.ui.PaperFlameLayout
import java.lang.reflect.InvocationTargetException

class ComposeWallpaper : WallpaperService() {

    override fun onCreateEngine(): Engine = ComposeEngin()

    inner class ComposeEngin : Engine() {

        override fun onApplyWindowInsets(insets: WindowInsets?) {
            super.onApplyWindowInsets(insets)
            Log.d("Wbj", "onApplyWindowInsets: $insets")
            //insets?.getInsets()
        }

        private lateinit var composeView: ComposeView
        private lateinit var layout: PaperFlameLayout

        override fun onSurfaceCreated(holder: SurfaceHolder?) {
            super.onSurfaceCreated(holder)
            layout = View.inflate(
                this@ComposeWallpaper,
                R.layout.compose_wallpaper,
                null
            ) as PaperFlameLayout

            //addGhost(layout.findViewById(R.id.textView), layout)
        }

        private fun addGhost(view: View, viewGroup: ViewGroup) {
            try {
                val ghostViewClass = Class.forName("android.view.GhostView")
                val addGhostMethod = ghostViewClass.getMethod(
                    "addGhost",
                    View::class.java,
                    ViewGroup::class.java,
                    Matrix::class.java
                )
                val ghostView = addGhostMethod.invoke(null, view, viewGroup, null) as View
                //val ghostView = addGhostMethod.invoke(null, viewGroup, null) as View
                ghostView.setBackgroundColor(Color.YELLOW)
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
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