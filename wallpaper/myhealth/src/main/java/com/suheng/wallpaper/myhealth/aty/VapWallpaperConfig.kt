package com.suheng.wallpaper.myhealth.aty

import android.os.Bundle
import android.widget.RadioGroup
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
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
import com.suheng.wallpaper.myhealth.R
import com.suheng.wallpaper.myhealth.bean.AdtItem
import com.suheng.wallpaper.myhealth.bean.asAdtItem
import com.suheng.wallpaper.myhealth.file.PrefsUtils
import com.suheng.wallpaper.myhealth.file.VideoLoader
import com.suheng.wallpaper.myhealth.repository.VideoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VapWallpaperConfig : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView()

        val videoList = VideoLoader.getVideoList()
        val itemList = mutableListOf<AdtItem>()
        if (videoList.isEmpty()) {
            lifecycleScope.launch(Dispatchers.IO) {
                VideoRepository.parseVideoConfig().collect {
                    VideoLoader.setVideoList(it)
                    itemList.addAll(it.map { video -> video.asAdtItem() }
                        .onEach { item -> println(item) })
                }
            }
        } else {
            itemList.addAll(videoList.map { it.asAdtItem() }.onEach { println(it) })
        }

        setContent {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                itemsIndexed(itemList) { _, item ->
                    Box(modifier = Modifier.fillMaxSize()) {
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

    private fun setContentView() {
        setContentView(R.layout.vap_wallpaper_config)

        findViewById<RadioGroup>(R.id.rg_render_way).apply {
            check(
                when (PrefsUtils.loadRenderWay(this@VapWallpaperConfig)) {
                    PrefsUtils.RENDER_WAY_VALUE_1 -> R.id.rg_render_ogl
                    else -> R.id.rg_render_vd
                }
            )
            setOnCheckedChangeListener { _, checkedId ->
                PrefsUtils.saveRenderWay(
                    this@VapWallpaperConfig, when (checkedId) {
                        R.id.rg_render_ogl -> PrefsUtils.RENDER_WAY_VALUE_1
                        else -> PrefsUtils.RENDER_WAY_VALUE_DEF
                    }
                )
            }
        }
    }

}
