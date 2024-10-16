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

object VideoRepository {

    private const val TAG = "SimpleVapWallpaper"
    private val context = App.appCtx()
    private var assetsPath = ""
    private var md5: String? = null
    private var selectedVideo: Video? = null
    private val filePairList = mutableListOf<Pair<Int, MutableList<FileInfo>>>()
    private val videoList = mutableListOf<Video>()

    fun loadVideoFile() = flow {
        filePairList.clear()
        filePairList.addAll(parseFileConfig())
        videoList.clear()
        videoList.addAll(parseVideoConfig())
        selectedVideo = getSelectedVideo()

        val cacheFile = selectedVideo?.let {
            val assetsDir = it.url + it.path
            val fileName = getVideoFileName()
            assetsPath = assetsDir + File.separator + fileName
            md5 = getVideoFileMD5(it.id, fileName)
            val dir = File(context.cacheDir, assetsDir)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            File(dir, fileName)
        }
        Log.d(TAG, "cacheFile: $cacheFile, assetsPath: $assetsPath, md5: $md5")
        if (cacheFile == null) {
            return@flow
        }

        if (cacheFile.exists()) {
            FileUtil.getMD5(cacheFile).onSuccess {
                val isSameMd5 = (it == md5)
                Log.d(TAG, "exists cacheFile, isSameMd5: $isSameMd5")
                if (isSameMd5) {
                    emit(cacheFile)
                } else {
                    cacheFile.delete()
                    if (copyAndCheckFile(cacheFile)) {
                        emit(cacheFile)
                    }
                }
            }
        } else {
            if (copyAndCheckFile(cacheFile)) {
                emit(cacheFile)
            }
        }
    }

    private fun copyAndCheckFile(destFile: File): Boolean {
        FileUtil.copyAssetsFile(assetsPath, destFile).onSuccess {
            return (md5 == FileUtil.getMD5(destFile).getOrNull()).also {
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

    fun parseVideoConfig2() = flow {
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

        emit(videoList)
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

    fun parseFileConfig2() = flow {
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

        emit(filePairList)
    }

    fun getSelectedVideo(): Video? {
        val videoId = PrefsUtils.loadSelectedVideoId(context)
        return videoList.find { videoId == it.id }
    }

    private fun getVideoFileName() = when ((0..1).random()) {
        1 -> context.getString(R.string.video_demo2)
        else -> context.getString(R.string.video_demo)
    }

    private fun getVideoFileMD5(videoId: Int, fileName: String): String? {
        return filePairList.find { it.first == videoId }?.second?.find { it.name == fileName }?.md5
    }
}