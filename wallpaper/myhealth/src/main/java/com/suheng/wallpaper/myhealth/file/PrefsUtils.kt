package com.suheng.wallpaper.myhealth.file

import android.content.Context
import android.content.SharedPreferences
import com.suheng.wallpaper.myhealth.R

object PrefsUtils {

    private const val VAP_WALLPAPER_CONFIGS = "vap_wallpaper_configs"
    const val RENDER_WAY_KEY = "key_render_way"
    const val RENDER_WAY_VALUE_DEF = 0
    const val RENDER_WAY_VALUE_1 = 1
    private const val SELECTED_VIDEO = "key_selected_video"

    fun sharedPrefs(context: Context): SharedPreferences =
        context.getSharedPreferences(VAP_WALLPAPER_CONFIGS, Context.MODE_PRIVATE)

    fun saveRenderWay(context: Context, way: Int) =
        sharedPrefs(context).edit().putInt(RENDER_WAY_KEY, way).apply()

    fun loadRenderWay(context: Context): Int =
        sharedPrefs(context).getInt(RENDER_WAY_KEY, RENDER_WAY_VALUE_DEF)

    fun saveSelectedVideoId(context: Context, videoId: Int) =
        sharedPrefs(context).edit().putInt(SELECTED_VIDEO, videoId).apply()

    fun loadSelectedVideoId(context: Context): Int =
        sharedPrefs(context).getInt(
            SELECTED_VIDEO,
            context.resources.getInteger(R.integer.video_demo_id)
        )
}