package com.suheng.wallpaper.myhealth.repository

import com.suheng.wallpaper.myhealth.app.App
import com.suheng.wallpaper.myhealth.bean.FileInfo
import com.suheng.wallpaper.myhealth.bean.Video
import com.suheng.wallpaper.myhealth.file.VideoLoader
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import java.io.File

object VideoRepository {

    private const val TAG = "SimpleVapWallpaper"

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getVideoFile(): Flow<File> {
        var parseVideoFlow: Flow<MutableList<Video>>? = null
        if (VideoLoader.getVideoList().isEmpty()) {
            parseVideoFlow = parseVideoConfig()
        }
        var parseFileFlow: Flow<MutableList<Pair<Int, MutableList<FileInfo>>>>? = null
        if (VideoLoader.getFilePairList().isEmpty()) {
            parseFileFlow = parseFileConfig()
        }

        return if (parseVideoFlow != null && parseFileFlow != null) {
            combine(parseVideoFlow, parseFileFlow) { videos, filePairs ->
                VideoLoader.setVideoList(videos)
                VideoLoader.setFilePairList(filePairs)
            }.flatMapConcat { _ ->
                loadVideoFile()
            }
        } else parseVideoFlow?.flatMapConcat { videos ->
            VideoLoader.setVideoList(videos)
            loadVideoFile()
        } ?: (parseFileFlow?.flatMapConcat { filePairs ->
            VideoLoader.setFilePairList(filePairs)
            loadVideoFile()
        } ?: loadVideoFile())
    }

    private fun loadVideoFile() = flow {
        VideoLoader.loadVideoFile(App.appCtx())?.let {
            emit(it)
        }
    }

    private fun parseVideoConfig() = flow {
        emit(VideoLoader.parseVideoConfig(App.appCtx()))
    }

    private fun parseFileConfig() = flow {
        emit(VideoLoader.parseFileConfig(App.appCtx()))
    }

}