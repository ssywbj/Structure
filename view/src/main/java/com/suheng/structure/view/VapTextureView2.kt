package com.suheng.structure.view;

import android.content.Context
import android.content.Intent
import android.graphics.SurfaceTexture
import android.util.AttributeSet
import android.util.Log
import android.view.Surface
import android.view.TextureView

class VapTextureView2 @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TextureView(context, attrs, defStyleAttr), TextureView.SurfaceTextureListener {

    companion object {
        const val TAG = "VapTextureView"
    }

    init {
        surfaceTextureListener = this
        isOpaque = false
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        Log.v(TAG, "onSurfaceTextureAvailable, width: $width, width: $height")
        val intent = Intent().apply {
            val pkgHome = "com.suheng.wallpaper.myhealth"
            `package` = pkgHome
            setClassName(pkgHome, "${pkgHome}.VapService")
            putExtra("vap_surface", Surface(surface))
        }
        context.startService(intent)
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
        Log.v(TAG, "onSurfaceTextureSizeChanged")
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        Log.v(TAG, "onSurfaceTextureDestroyed")
        return false
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
        Log.v(TAG, "onSurfaceTextureUpdated")
    }

}
