package com.suheng.wallpaper.myhealth.aty

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.suheng.wallpaper.myhealth.bean.AdtItem
import com.suheng.wallpaper.myhealth.bean.asAdtItem
import com.suheng.wallpaper.myhealth.file.PrefsUtils
import com.suheng.wallpaper.myhealth.file.VideoLoader
import com.suheng.wallpaper.myhealth.repository.VideoRepository
import kotlinx.coroutines.Dispatchers
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
                    //VideoLoader.setVideoList(it)
                    itemList.addAll(it.map { video ->
                        video.asAdtItem().apply {
                            selected = selectedId == video.id
                            previewSelected = selected
                        }
                    }.onEach { item -> println(item) })
                    Log.v(TAG, "onCreate size: ${itemList.size}")
                }
            }
        } else {
            itemList.addAll(videoList.map {
                it.asAdtItem().apply {
                    selected = selectedId == it.id
                    previewSelected = selected
                }
            }.onEach { println(it) })
        }

        setContent {
            Log.d(TAG, "onCreate setContent")
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                itemsIndexed(itemList) { _, item ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable {
                                PrefsUtils.saveSelectedVideoId(context, item.id)
                            }) {
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
                    }

                }
            }
        }
    }

}
