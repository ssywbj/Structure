package com.suheng.wallpaper.myhealth.repository

import android.util.Log
import com.suheng.wallpaper.myhealth.R
import com.suheng.wallpaper.myhealth.app.App
import com.suheng.wallpaper.myhealth.bean.FileInfo
import com.suheng.wallpaper.myhealth.bean.Video
import com.suheng.wallpaper.myhealth.file.FileUtil
import com.suheng.wallpaper.myhealth.file.PrefsUtils
import kotlinx.coroutines.flow.flow
import org.xmlpull.v1.XmlPullParser
import java.io.File

object FileRepository {

    private const val TAG = "SimpleVapWallpaper"
    private val context = App.appCtx()
    private var fileName = ""
    private const val FILE_MD5 = "3132824326bb07a1143739863e1e5762"
    private var selectedVideo: Video? = null

    fun loadVideoFile() = flow {
        val destFile = selectedVideo?.let {
            val assetsDir = it.url + it.path
            val videoFile = "demo.mp4"
            fileName = assetsDir + File.separator + videoFile
            val dir = File(context.cacheDir, assetsDir)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            File(dir, videoFile)
        }
        Log.d(TAG, "destFile: $destFile, fileName: $fileName")
        if (destFile == null) {
            return@flow
        }

        if (destFile.exists()) {
            FileUtil.getMD5(destFile).onSuccess {
                val isSameMd5 = (it == FILE_MD5)
                Log.d(TAG, "exists file: $destFile, isSameMd5: $isSameMd5")
                if (isSameMd5) {
                    emit(destFile)
                } else {
                    destFile.delete()
                    if (copyAndCheckFile(destFile)) {
                        emit(destFile)
                    }
                }
            }
        } else {
            if (copyAndCheckFile(destFile)) {
                emit(destFile)
            }
        }
    }

    private fun copyAndCheckFile(destFile: File): Boolean {
        FileUtil.copyAssetsFile(fileName, destFile).onSuccess {
            return (FILE_MD5 == FileUtil.getMD5(destFile).getOrNull()).also {
                Log.d(TAG, "copyAndCheckFile success, check md5 result: $it")
            }
        }.onFailure {
            Log.e(TAG, "copyAndCheckFile error: $it")
        }
        return false
    }

    fun parseVideoConfig(): MutableList<Video> {
        val videoList = mutableListOf<Video>()
        val resources = context.resources
        val parser = resources.getXml(R.xml.video_config)

        while (parser.eventType != XmlPullParser.END_DOCUMENT) {
            if (parser.eventType == XmlPullParser.START_TAG) {
                if ("video" == parser.name) {
                    val id = resources.getInteger(
                        parser.getAttributeResourceValue(
                            null, "id", R.integer.video_demo_id
                        )
                    )
                    val url = parser.getAttributeValue(null, "url")
                    val path = parser.getAttributeValue(null, "path")
                    val video = Video(id, url, path)
                    //Log.v(TAG, "video: $video")
                    videoList.add(video)
                }
            }

            parser.next()
        }

        parser.close()

        return videoList
    }

    fun parseFileConfig(): MutableList<Pair<Int, MutableList<FileInfo>>> {
        val filePairList = mutableListOf<Pair<Int, MutableList<FileInfo>>>()
        val resources = context.resources
        val parser = resources.getXml(R.xml.file_config)

        var videoId: Int? = null
        var fileList: MutableList<FileInfo>? = null
        while (parser.eventType != XmlPullParser.END_DOCUMENT) {
            if (parser.eventType == XmlPullParser.START_TAG) {
                if ("item" == parser.name) {
                    videoId = resources.getInteger(
                        parser.getAttributeResourceValue(
                            null, "video_id", R.integer.video_demo_id
                        )
                    )
                    //Log.d(TAG, "item start tag, video_id: $videoId")
                    fileList = mutableListOf()
                }

                if ("file" == parser.name) {
                    val name = resources.getString(
                        parser.getAttributeResourceValue(
                            null, "name", R.string.video_demo
                        )
                    )
                    val md5 = parser.getAttributeValue(null, "md5")
                    val fileInfo = FileInfo(name, md5)
                    //Log.v(TAG, "fileInfo: $fileInfo")
                    fileList?.add(fileInfo)
                }
            }

            if (parser.eventType == XmlPullParser.END_TAG) {
                if ("item" == parser.name) {
                    if (videoId != null && fileList != null) {
                        //Log.d(TAG, "item end tag, add list")
                        filePairList.add(Pair(videoId, fileList))
                    }
                }
            }

            parser.next()
        }

        parser.close()

        return filePairList
    }

    fun getSelectedVideo(): Video? {
        val videoId = PrefsUtils.loadSelectedVideoId(context)
        val videos = parseVideoConfig()
        for (video in videos) {
            if (videoId == video.id) {
                return video.also { selectedVideo = it }
            }
        }

        return null
    }

}