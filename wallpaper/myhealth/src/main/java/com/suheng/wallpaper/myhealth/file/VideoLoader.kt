package com.suheng.wallpaper.myhealth.file

import android.content.Context
import android.util.Log
import com.suheng.wallpaper.myhealth.R
import com.suheng.wallpaper.myhealth.bean.FileInfo
import com.suheng.wallpaper.myhealth.bean.Video
import org.xmlpull.v1.XmlPullParser
import java.io.File

object VideoLoader {

    private const val TAG = "SimpleVapWallpaper"
    private var assetsPath = ""
    private var md5: String? = null
    private val filePairList = mutableListOf<Pair<Int, MutableList<FileInfo>>>()
    private val videoList = mutableListOf<Video>()

    fun loadVideoFile(ctx: Context): File? {
        return getSelected(ctx)?.let {
            buildPath(ctx, it)
        }?.let { cacheFile ->
            if (cacheFile.exists()) {
                return@let FileUtil.getMD5(cacheFile).getOrNull()?.let {
                    val isSameMd5 = (it == md5)
                    Log.d(TAG, "exists cacheFile, isSameMd5: $isSameMd5")
                    if (isSameMd5) {
                        cacheFile
                    } else {
                        cacheFile.delete()
                        if (copyAndCheckFile(cacheFile)) cacheFile else null
                    }
                }
            } else {
                return@let if (copyAndCheckFile(cacheFile)) cacheFile else null
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

    fun parseVideoConfig(ctx: Context): MutableList<Video> {
        val videoList = mutableListOf<Video>()
        val resources = ctx.resources
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
                    val name = resources.getString(
                        parser.getAttributeResourceValue(
                            null, "name", R.string.video1_name
                        )
                    )
                    val video = Video(id, url, path, name)
                    //Log.v(TAG, "video: $video")
                    videoList.add(video)
                }
            }

            parser.next()
        }

        parser.close()

        return videoList
    }

    fun parseFileConfig(ctx: Context): MutableList<Pair<Int, MutableList<FileInfo>>> {
        val filePairList = mutableListOf<Pair<Int, MutableList<FileInfo>>>()
        val resources = ctx.resources
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

    fun getSelected(ctx: Context): Video? {
        val videoId = PrefsUtils.loadSelectedVideoId(ctx)
        return videoList.find { videoId == it.id }
    }

    private fun buildPath(ctx: Context, video: Video): File {
        val assetsDir = video.url + video.path
        val cacheDir = File(ctx.cacheDir, assetsDir)
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }
        val fileName = getFileName(ctx)
        assetsPath = assetsDir + File.separator + fileName
        md5 = getMD5(video.id, fileName)
        return File(cacheDir, fileName).also { file ->
            Log.d(TAG, "cacheFile: $file, assetsPath: $assetsPath, md5: $md5")
        }
    }

    private fun getFileName(ctx: Context) = when ((0..1).random()) {
        1 -> ctx.getString(R.string.video_demo2)
        else -> ctx.getString(R.string.video_demo)
    }

    private fun getMD5(videoId: Int, fileName: String): String? {
        return filePairList.find { it.first == videoId }?.second?.find { it.name == fileName }?.md5
    }

    fun setFilePairList(filePairs: MutableList<Pair<Int, MutableList<FileInfo>>>) {
        filePairList += filePairs
    }

    fun getFilePairList(): MutableList<Pair<Int, MutableList<FileInfo>>> = filePairList

    fun setVideoList(videos: MutableList<Video>) {
        this.videoList += videos
    }

    fun getVideoList(): MutableList<Video> = videoList

}