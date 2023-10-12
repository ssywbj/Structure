package com.suheng.structure.view;

import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView

class VapTextureView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SurfaceView(context, attrs, defStyleAttr), SurfaceHolder.Callback {

    companion object {
        const val TAG = "VapTextureView"
    }

    init {
        setZOrderOnTop(true)
        holder.setFormat(PixelFormat.TRANSLUCENT)

        holder.addCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        Log.v(TAG, "surfaceCreated")
        val intent = Intent().apply {
            val pkgHome = "com.suheng.wallpaper.myhealth"
            `package` = pkgHome
            setClassName(pkgHome, "${pkgHome}.VapService")

            putExtra("onSurfaceTextureAvailable", holder.surface)
            putExtra("vap_width", width)
            putExtra("vap_height", height)
        }
        context.startService(intent)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        Log.v(TAG, "surfaceChanged: width = $width, height = $height")
        val intent = Intent().apply {
            val pkgHome = "com.suheng.wallpaper.myhealth"
            `package` = pkgHome
            setClassName(pkgHome, "${pkgHome}.VapService")

            putExtra("onSurfaceTextureSizeChanged", holder.surface)
            putExtra("vap_width", width)
            putExtra("vap_height", height)
        }
        context.startService(intent)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        Log.v(TAG, "surfaceDestroyed")
        val flagDestroyed = false
        val intent = Intent().apply {
            val pkgHome = "com.suheng.wallpaper.myhealth"
            `package` = pkgHome
            setClassName(pkgHome, "${pkgHome}.VapService")

            putExtra("onSurfaceTextureDestroyed", flagDestroyed)
        }
        context.startService(intent)
    }

    override fun onVisibilityAggregated(isVisible: Boolean) {
        super.onVisibilityAggregated(isVisible)
        Log.v(VapTextureView2.TAG, "onVisibilityAggregated, isVisible = $isVisible")
        val intent = Intent().apply {
            val pkgHome = "com.suheng.wallpaper.myhealth"
            `package` = pkgHome
            setClassName(pkgHome, "${pkgHome}.VapService")

            putExtra("onVisibilityAggregated", isVisible)
        }
        context.startService(intent)
    }

    override fun onScreenStateChanged(screenState: Int) {
        super.onScreenStateChanged(screenState)
        Log.v(VapTextureView2.TAG, "onScreenStateChanged, screenState = $screenState")
        val intent = Intent().apply {
            val pkgHome = "com.suheng.wallpaper.myhealth"
            `package` = pkgHome
            setClassName(pkgHome, "${pkgHome}.VapService")

            putExtra("onScreenStateChanged", screenState)
        }
        context.startService(intent)
    }
}
