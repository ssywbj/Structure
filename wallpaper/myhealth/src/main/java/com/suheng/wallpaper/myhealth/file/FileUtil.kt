package com.suheng.wallpaper.myhealth.file

import android.util.Log
import com.suheng.wallpaper.myhealth.app.App
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.security.MessageDigest

object FileUtil {

    private const val TAG = "FileUtil"

    @Throws(IOException::class)
    private fun streamInputToOutput(
        input: InputStream, output: OutputStream, needClose: Boolean = true,
    ) {
        Log.v(TAG, "streamInputToOutput, ${Thread.currentThread().name}")
        val buffer = ByteArray(2048 * 2)
        var len: Int
        while (input.read(buffer).also { len = it } != -1) {
            output.write(buffer, 0, len)
        }
        if (needClose) {
            output.close()
            input.close()
        }
    }

    fun streamInputToByte(input: InputStream): ByteArray? {
        return kotlin.runCatching {
            val output = ByteArrayOutputStream()
            streamInputToOutput(input, output, false)
            output.use {
                output.toByteArray()
            }.also { input.close() }
        }.getOrNull()
    }

    fun assetsStreamInputToByte(assetsPath: String): ByteArray? {
        return try {
            streamInputToByte(App.appCtx().assets.open(assetsPath))
        } catch (e: IOException) {
            null
        }
    }

    fun copyFile(source: InputStream, dest: File): Result<Unit> {
        return kotlin.runCatching { streamInputToOutput(source, FileOutputStream(dest)) }
    }

    fun copyFile(source: InputStream, dest: String): Result<Unit> {
        return kotlin.runCatching { streamInputToOutput(source, FileOutputStream(dest)) }
    }

    fun copyFile(source: File, dest: File): Result<Unit> {
        return copyFile(FileInputStream(source), dest)
    }

    fun copyFile(source: String, dest: String): Result<Unit> {
        return copyFile(FileInputStream(source), dest)
    }

    fun copyAssetsFile(absPath: String, dest: String): Result<Unit> {
        return kotlin.runCatching {
            streamInputToOutput(App.appCtx().assets.open(absPath), FileOutputStream(dest))
        }
    }

    fun copyAssetsFile(assetsPath: String, dest: File): Result<Unit> {
        return kotlin.runCatching {
            streamInputToOutput(App.appCtx().assets.open(assetsPath), FileOutputStream(dest))
        }
    }

    fun getMD5(file: File): Result<String> {
        return kotlin.runCatching {
            val md = MessageDigest.getInstance("MD5")
            val inputStream = FileInputStream(file)
            val buffer = ByteArray(4096)
            var len = inputStream.read(buffer)
            while (len != -1) {
                md.update(buffer, 0, len)
                len = inputStream.read(buffer)
            }
            inputStream.close()
            bufferToHex(md.digest())
        }
    }

    private fun bufferToHex(bytes: ByteArray): String {
        return bufferToHex(bytes, 0, bytes.size)
    }

    private fun bufferToHex(bytes: ByteArray, m: Int, n: Int): String {
        val sb = StringBuffer(2 * n)
        val k = m + n
        for (l in m until k) {
            appendHexPair(bytes[l], sb)
        }
        return sb.toString()
    }

    private val hexDigits =
        charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')

    private fun appendHexPair(bt: Byte, sb: StringBuffer) {
        val c0 = hexDigits[bt.toInt() and 0xf0 ushr 4]
        val c1 = hexDigits[bt.toInt() and 0x0f]
        sb.append(c0)
        sb.append(c1)
    }
}

var wallpaperScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

inline fun runOnUiThread(scope: CoroutineScope = wallpaperScope, crossinline block: () -> Unit) {
    scope.launch(Dispatchers.Main) { block() }
}

inline fun runOnWorkThread(scope: CoroutineScope = wallpaperScope, crossinline block: () -> Unit) {
    scope.launch(Dispatchers.IO) { block() }
}

