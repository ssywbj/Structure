package com.suheng.wallpaper.myhealth.bean

import androidx.annotation.DrawableRes
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.suheng.wallpaper.myhealth.R

data class Video(val id: Int, val url: String, val path: String, val name: String) {
    val assetsDir = url + path
}

data class AdtItem(
    val id: Int,
    val name: String,
    @DrawableRes val preview: Int,
    var selected: Boolean = false,
    var previewSelected: MutableState<Boolean> = mutableStateOf(false),
)

//inline fun <R> Video.mapItem(block: (Video) -> R): R = this.let(block)

fun Video.previewResId() = when (id % 2) {
    1 -> R.drawable.video2_preview
    else -> R.drawable.video1_preview
}

fun Video.asAdtItem() = AdtItem(id, name, previewResId())
