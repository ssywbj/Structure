package com.tencent.qgame.animplayer

import android.content.res.AssetManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Surface
import com.tencent.qgame.animplayer.file.AssetsFileContainer
import com.tencent.qgame.animplayer.file.FileContainer
import com.tencent.qgame.animplayer.file.IFileContainer
import com.tencent.qgame.animplayer.inter.IAnimListener
import com.tencent.qgame.animplayer.inter.IFetchResource
import com.tencent.qgame.animplayer.inter.OnResourceClickListener
import com.tencent.qgame.animplayer.mask.MaskConfig
import com.tencent.qgame.animplayer.util.IScaleType
import com.tencent.qgame.animplayer.util.ScaleType
import com.tencent.qgame.animplayer.util.ScaleTypeUtil
import java.io.File

class VapSurface : IAnimView{

    companion object {
        private const val TAG = "${Constant.TAG}.VapSurface"
    }

    private var player: AnimPlayer
    private val uiHandler by lazy { Handler(Looper.getMainLooper()) }
    private var surface: Surface? = null
    private var animListener: IAnimListener? = null
    private var lastFile: IFileContainer? = null
    private val scaleTypeUtil = ScaleTypeUtil()

    // 代理监听
    private val animProxyListener by lazy {
        object : IAnimListener {
            override fun onVideoConfigReady(config: AnimConfig): Boolean {
                scaleTypeUtil.setVideoSize(config.width, config.height)
                return animListener?.onVideoConfigReady(config) ?: super.onVideoConfigReady(config)
            }

            override fun onVideoStart() {
                animListener?.onVideoStart()
            }

            override fun onVideoRender(frameIndex: Int, config: AnimConfig?) {
                animListener?.onVideoRender(frameIndex, config)
            }

            override fun onVideoComplete() {
                hide()
                animListener?.onVideoComplete()
            }

            override fun onVideoDestroy() {
                hide()
                animListener?.onVideoDestroy()
            }

            override fun onFailed(errorType: Int, errorMsg: String?) {
                animListener?.onFailed(errorType, errorMsg)
            }
        }
    }

    private var needPrepareTextureView = false

    init {
        hide()
        player = AnimPlayer(this)
        player.animListener = animProxyListener
    }

    override fun prepareTextureView() {
        Log.e(TAG, "onSizeChanged not called")
        needPrepareTextureView = true
    }

    override fun getSurface(): Surface? {
        return surface
    }

    fun onSurfaceSizeChanged(width: Int, height: Int) {
        Log.i(TAG, "onSurfaceSizeChanged $width x $height")
        player.onSurfaceTextureSizeChanged(width, height)
    }
    
    fun onSurfaceDestroyed() {
        Log.i(TAG, "onSurfaceDestroyed")
        this.surface = null
        player.onSurfaceTextureDestroyed()
    }

    fun onSurfaceAvailable(surface: Surface, width: Int, height: Int) {
        Log.i(TAG, "onSurfaceAvailable width=$width height=$height")
        this.surface = surface
        player.onSurfaceTextureAvailable(width, height)
    }

    fun onSizeChanged(w: Int, h: Int) {
        Log.i(TAG, "onSizeChanged w=$w, h=$h")
        scaleTypeUtil.setLayoutSize(w, h)
        if (needPrepareTextureView) {
            needPrepareTextureView = false
            prepareTextureView()
        }
    }

    fun onAttachedToWindow() {
        Log.i(TAG, "onAttachedToWindow")
        player.isDetachedFromWindow = false
        // 自动恢复播放
        if (player.playLoop > 0) {
            lastFile?.apply {
                startPlay(this)
            }
        }
    }

    fun onDetachedFromWindow() {
        Log.i(TAG, "onDetachedFromWindow")
        player.isDetachedFromWindow = true
        player.onSurfaceTextureDestroyed()
    }

    override fun setAnimListener(animListener: IAnimListener?) {
        this.animListener = animListener
    }

    override fun setFetchResource(fetchResource: IFetchResource?) {
        player.pluginManager.getMixAnimPlugin()?.resourceRequest = fetchResource
    }

    override fun setOnResourceClickListener(resourceClickListener: OnResourceClickListener?) {
        player.pluginManager.getMixAnimPlugin()?.resourceClickListener = resourceClickListener
    }

    /**
     * 兼容方案，优先保证表情显示
     */
    open fun enableAutoTxtColorFill(enable: Boolean) {
        player.pluginManager.getMixAnimPlugin()?.autoTxtColorFill = enable
    }

    override fun setLoop(playLoop: Int) {
        player.playLoop = playLoop
    }

    override fun supportMask(isSupport: Boolean, isEdgeBlur: Boolean) {
        player.supportMaskBoolean = isSupport
        player.maskEdgeBlurBoolean = isEdgeBlur
    }

    override fun updateMaskConfig(maskConfig: MaskConfig?) {
        player.updateMaskConfig(maskConfig)
    }

    @Deprecated("Compatible older version mp4, default false")
    fun enableVersion1(enable: Boolean) {
        player.enableVersion1 = enable
    }

    // 兼容老版本视频模式
    @Deprecated("Compatible older version mp4")
    fun setVideoMode(mode: Int) {
        player.videoMode = mode
    }

    override fun setFps(fps: Int) {
        Log.i(TAG, "setFps=$fps")
        player.defaultFps = fps
    }

    override fun setScaleType(type: ScaleType) {
        scaleTypeUtil.currentScaleType = type
    }

    override fun setScaleType(scaleType: IScaleType) {
        scaleTypeUtil.scaleTypeImpl = scaleType
    }

    /**
     * @param isMute true 静音
     */
    override fun setMute(isMute: Boolean) {
        Log.e(TAG, "set mute=$isMute")
        player.isMute = isMute
    }

    override fun startPlay(file: File) {
        try {
            val fileContainer = FileContainer(file)
            startPlay(fileContainer)
        } catch (e: Throwable) {
            animProxyListener.onFailed(Constant.REPORT_ERROR_TYPE_FILE_ERROR, Constant.ERROR_MSG_FILE_ERROR)
            animProxyListener.onVideoComplete()
        }
    }

    override fun startPlay(assetManager: AssetManager, assetsPath: String) {
        try {
            val fileContainer = AssetsFileContainer(assetManager, assetsPath)
            startPlay(fileContainer)
        } catch (e: Throwable) {
            animProxyListener.onFailed(Constant.REPORT_ERROR_TYPE_FILE_ERROR, Constant.ERROR_MSG_FILE_ERROR)
            animProxyListener.onVideoComplete()
        }
    }

    override fun startPlay(fileContainer: IFileContainer) {
        ui {
            if (player.isRunning()) {
                Log.e(TAG, "is running can not start")
            } else {
                lastFile = fileContainer
                player.startPlay(fileContainer)
            }
        }
    }

    override fun stopPlay() {
        player.stopPlay()
    }

    override fun isRunning(): Boolean {
        return player.isRunning()
    }

    override fun getRealSize(): Pair<Int, Int> {
        return scaleTypeUtil.getRealSize()
    }

    private fun hide() {
        lastFile?.close()
    }

    private fun ui(f: () -> Unit) {
        if (Looper.myLooper() == Looper.getMainLooper()) f() else uiHandler.post { f() }
    }

}