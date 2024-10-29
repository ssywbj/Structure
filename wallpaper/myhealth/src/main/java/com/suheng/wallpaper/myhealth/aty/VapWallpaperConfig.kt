package com.suheng.wallpaper.myhealth.aty

import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.lifecycleScope
import com.suheng.wallpaper.myhealth.bean.AdtItem
import com.suheng.wallpaper.myhealth.bean.asAdtItem
import com.suheng.wallpaper.myhealth.file.PrefsUtils
import com.suheng.wallpaper.myhealth.file.VideoLoader
import com.suheng.wallpaper.myhealth.repository.VideoRepository
import com.tencent.qgame.animplayer.VapSurface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class VapWallpaperConfig : AppCompatActivity() {

    companion object {
        //private val TAG = VapWallpaperConfig::class.java.simpleName
        private const val TAG = "SimpleVapWallpaper"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val videoList = VideoLoader.getVideoList()
        val itemList = mutableListOf<AdtItem>()
        val context = this@VapWallpaperConfig
        val selectedId = PrefsUtils.loadSelectedVideoId(context)
        if (videoList.isEmpty()) {
            lifecycleScope.launch(Dispatchers.IO) {
                VideoRepository.parseVideoConfig().collect {
                    VideoLoader.setVideoList(it)
                    itemList.addAll(it.map { video ->
                        video.asAdtItem().apply {
                            selected = selectedId == video.id
                            previewSelected.value = selected
                        }
                    }/*.onEach { item -> println(item) }*/)
                    Log.v(TAG, "onCreate size: ${itemList.size}")
                }
            }
        } else {
            itemList.addAll(videoList.map {
                it.asAdtItem().apply {
                    selected = selectedId == it.id
                    previewSelected.value = selected
                }
            }/*.onEach { println(it) }*/)
        }

        var vapSurface: VapSurface? = null
        var videoPath: String? = null
        VideoLoader.getSelectedFlow().onEach {
            videoPath = it.url + it.path + "/demo.mp4"
            Log.e(TAG, "selectedFlow onEach: $it, assets path: $videoPath")
            vapSurface?.let { vap ->
                if (vap.isRunning()) {
                    vap.stopPlay()
                    delay(200)
                }
                vap.startPlay(context.assets, it.url + it.path + "/demo2.mp4")
            }
        }.launchIn(lifecycleScope)

        setContent {
            Log.d(TAG, "onCreate setContent")
            Column {
                AndroidView(
                    factory = { context ->
                        SurfaceView(context).apply {
                            holder.addCallback(object : SurfaceHolder.Callback {
                                override fun surfaceCreated(holder: SurfaceHolder) {
                                    Log.d(TAG, "surfaceCreated")
                                }

                                override fun surfaceChanged(
                                    holder: SurfaceHolder, format: Int, width: Int, height: Int,
                                ) {
                                    Log.d(TAG, "surfaceChanged")
                                    vapSurface?.onSurfaceSizeChanged(width, height)
                                    if (vapSurface == null) {
                                        vapSurface = holder.surface?.let {
                                            VapSurface().apply {
                                                setLoop(Int.MAX_VALUE)
                                                onSurfaceAvailable(it, width, height)
                                                videoPath?.let {
                                                    startPlay(context.assets, it)
                                                }
                                            }
                                        }
                                    }
                                }

                                override fun surfaceDestroyed(holder: SurfaceHolder) {
                                    Log.d(TAG, "surfaceDestroyed")
                                    vapSurface?.onSurfaceDestroyed()
                                }
                            })
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(2.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    itemsIndexed(itemList) { _, item ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable {
                                    itemList.forEach {
                                        it.previewSelected.value = false
                                    }
                                    item.previewSelected.value = true
                                    VideoLoader
                                        .getVideoList()
                                        .find { it.id == item.id }
                                        ?.let {
                                            VideoLoader.setSelected(context, it)
                                            //Log.d(TAG, "SelectedVideo: $it")
                                        }
                                }
                                .run {
                                    if (item.previewSelected.value) {
                                        border(2.dp, Color.Blue, RectangleShape).padding(2.dp)
                                    } else this
                                },
                        ) {
                            Image(
                                painter = painterResource(item.preview),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize()
                            )
                            Text(
                                item.name, modifier = Modifier
                                    .wrapContentSize()
                                    .align(Alignment.BottomCenter),
                                Color.White, 18.sp, textAlign = TextAlign.Center
                            )

                            if (item.selected) {
                                Checkbox(
                                    true, null,
                                    Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(6.dp)
                                )
                            }
                        }

                    }
                }
            }
        }
    }

}
