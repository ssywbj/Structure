package com.suheng.wallpaper.myhealth.repository

import com.suheng.wallpaper.myhealth.app.App
import com.suheng.wallpaper.myhealth.file.FileUtil
import kotlinx.coroutines.flow.flow
import java.io.File

object FileRepository {

    private val context = App.appCtx()
    private const val FILE_NAME = "demo.mp4"
    private val destFile = File(context.cacheDir.absolutePath + File.separator + FILE_NAME)

    fun loadVideoFile() = flow {
        if (destFile.exists()) {
            emit(destFile)
        } else {
            if (FileUtil.streamInputToFile(context.assets.open(FILE_NAME), destFile).isSuccess) {
                emit(destFile)
            }
        }
    }

}