package com.suheng.wallpaper.myhealth.repository

import android.util.Log
import com.suheng.wallpaper.myhealth.app.App
import com.suheng.wallpaper.myhealth.file.FileUtil
import kotlinx.coroutines.flow.flow
import java.io.File

object FileRepository {

    private const val TAG = "SimpleVapWallpaper"
    private val context = App.appCtx()
    private const val FILE_NAME = "demo.mp4"
    private const val FILE_MD5 = "3132824326bb07a1143739863e1e5762"
    //private const val FILE_MD5 = "d993c3e9ecde8a1e73e7db01f36e2c0e"
    private val destFile = File(context.cacheDir.absolutePath + File.separator + FILE_NAME)

    fun loadVideoFile() = flow {
        if (destFile.exists()) {
            FileUtil.getMD5(destFile).onSuccess {
                val isSameMd5 = (it == FILE_MD5)
                Log.d(TAG, "exists file: $destFile, isSameMd5: $isSameMd5")
                if (isSameMd5) {
                    emit(destFile)
                } else {
                    destFile.delete()
                    if (copyAndCheckFile()) {
                        emit(destFile)
                    }
                }
            }
        } else {
            if (copyAndCheckFile()) {
                emit(destFile)
            }
        }
    }

    private fun copyAndCheckFile(): Boolean {
        if (FileUtil.copyFile(context.assets.open(FILE_NAME), destFile).isSuccess) {
            FileUtil.getMD5(destFile).onSuccess {
                val isSameMd5 = it == FILE_MD5
                Log.d(TAG, "copyAndCheckFile, file: $destFile, isSameMd5: $isSameMd5")
                return isSameMd5
            }
        }
        return false
    }

}