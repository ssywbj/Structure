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
        context.startService(Intent().apply {
            val pkgHome = "com.suheng.wallpaper.myhealth"
            `package` = pkgHome
            setClassName(pkgHome, "${pkgHome}.VapService")
            putExtra("vap_surface", holder.surface)
        })
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        Log.v(TAG, "surfaceChanged: width = $width, height = $height")
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        Log.v(TAG, "surfaceDestroyed")
    }

}
