package com.suheng.wallpaper.myhealth.bean

import androidx.annotation.DrawableRes
import com.suheng.wallpaper.myhealth.R

data class Video(val id: Int = 0, val url: String, val path: String, val name: String)

data class AdtItem(val id: Int = 0, val name: String, @DrawableRes val preview: Int)

inline fun <R> Video.mapItem(block: (Video) -> R): R = this.let(block)

fun Video.previewResId() = when (id) {
    1 -> R.drawable.video2_preview
    else -> R.drawable.video1_preview
}

fun Video.asAdtItem() = mapItem { AdtItem(id, name, previewResId()) }
