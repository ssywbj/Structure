package com.suheng.wallpaper.myhealth.aty

import android.os.Bundle
import android.widget.RadioGroup
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.suheng.wallpaper.myhealth.R
import com.suheng.wallpaper.myhealth.file.PrefsUtils

class VapWallpaperConfig : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView()
        setContent {
            Text(
                modifier = Modifier
                    .height(15.dp)
                    .background(Color.Green)
                    .padding(horizontal = 6.dp),
                text = "Hello Compose",
                fontFamily = FontFamily.Cursive
            )
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
