package com.suheng.structure.view;

import android.content.Context
import android.content.Intent
import android.graphics.SurfaceTexture
import android.os.Bundle
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
        const val pkgHome = "com.suheng.wallpaper.myhealth"

        const val EXTRA_ON_CLICK = "onClick"
        const val EVENT_ON_CLICK = "com.suheng.wallpaper.event.ON_CLICK"
        const val EXTRA_ON_LONG_CLICK = "onLongClick"
        const val EVENT_ON_LONG_CLICK = "com.suheng.wallpaper.event.ON_LONG_CLICK"

        const val EXTRA_PROTOCOL = "protocol"
    }

    init {
        surfaceTextureListener = this
        isOpaque = false
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        Log.v(TAG, "onSurfaceTextureAvailable, width: $width, width: $height")
        val intent = Intent().apply {
            `package` = pkgHome
            setClassName(pkgHome, "${pkgHome}.VapService")

            putExtra("onSurfaceTextureAvailable", Surface(surface))
            putExtra("vap_width", width)
            putExtra("vap_height", height)
        }
        context.startService(intent)
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
        Log.v(TAG, "onSurfaceTextureSizeChanged")
        val intent = Intent().apply {
            `package` = pkgHome
            setClassName(pkgHome, "${pkgHome}.VapService")

            putExtra("onSurfaceTextureSizeChanged", Surface(surface))
            putExtra("vap_width", width)
            putExtra("vap_height", height)
        }
        context.startService(intent)
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        Log.v(TAG, "onSurfaceTextureDestroyed")
        //surface.release()

        val flagDestroyed = false
        val intent = Intent().apply {
            `package` = pkgHome
            setClassName(pkgHome, "${pkgHome}.VapService")

            putExtra("onSurfaceTextureDestroyed", flagDestroyed)
        }
        context.startService(intent)
        return flagDestroyed
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
        //Log.v(TAG, "onSurfaceTextureUpdated")
    }

    override fun onVisibilityAggregated(isVisible: Boolean) {
        super.onVisibilityAggregated(isVisible)
        Log.v(TAG, "onVisibilityAggregated, isVisible = $isVisible")
        val intent = Intent().apply {
            `package` = pkgHome
            setClassName(pkgHome, "${pkgHome}.VapService")

            putExtra("onVisibilityAggregated", isVisible)
        }
        context.startService(intent)
    }

    override fun onScreenStateChanged(screenState: Int) {
        super.onScreenStateChanged(screenState)
        Log.v(TAG, "onScreenStateChanged, screenState = $screenState")
        val intent = Intent().apply {
            `package` = pkgHome
            setClassName(pkgHome, "${pkgHome}.VapService")

            putExtra("onScreenStateChanged", screenState)
        }
        context.startService(intent)
    }

    fun postEvent(bundle: Bundle) {
        val intent = Intent().apply {
            `package` = pkgHome
            setClassName(pkgHome, "${pkgHome}.VapService")

            putExtras(bundle)
        }
        context.startService(intent)
    }

    fun postEvent(protocol: String) {
        postEvent(Bundle().apply {
            putString(EXTRA_PROTOCOL, protocol)
        })
    }

    override fun performClick(): Boolean = super.performClick().also {
        //Log.v(TAG, "performClick: $it")
        postEvent(Bundle().apply {
            putString(EXTRA_ON_CLICK, EVENT_ON_CLICK)
        })
    }

    override fun performLongClick(): Boolean = super.performLongClick().also {
        //Log.v(TAG, "performLongClick: $it")
        postEvent(Bundle().apply {
            putString(EXTRA_ON_LONG_CLICK, EVENT_ON_LONG_CLICK)
        })
    }

}
