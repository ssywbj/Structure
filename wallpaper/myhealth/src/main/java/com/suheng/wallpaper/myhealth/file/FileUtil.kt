package com.suheng.wallpaper.myhealth.file

import android.util.Log
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

    fun readInputStream(inputStream: InputStream): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val buffer = ByteArray(2048 * 2)
        var len: Int
        while ((inputStream.read(buffer).also { len = it }) != -1) {
            byteArrayOutputStream.write(buffer, 0, len)
        }
        val data = byteArrayOutputStream.toByteArray()
        byteArrayOutputStream.close()
        inputStream.close()
        return data
    }

    private fun streamInputToOutput(
        input: InputStream, output: OutputStream, needClose: Boolean = true,
    ): Result<Unit> {
        return kotlin.runCatching {
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

    }

    fun streamInputToByte(input: InputStream): ByteArray? {
        val output = ByteArrayOutputStream()
        var data: ByteArray? = null
        if (streamInputToOutput(input, output, false).isSuccess) {
            data = output.toByteArray()
        }
        try {
            output.close()
            input.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return data
    }

    fun copyFile(source: InputStream, dest: File): Result<Unit> {
        return streamInputToOutput(source, FileOutputStream(dest))
    }

    fun copyFile(source: InputStream, dest: String): Result<Unit> {
        return streamInputToOutput(source, FileOutputStream(dest))
    }

    fun copyFile(source: File, dest: File): Result<Unit> {
        return copyFile(FileInputStream(source), dest)
    }

    fun copyFile(source: String, dest: String): Result<Unit> {
        return copyFile(FileInputStream(source), dest)
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

val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

inline fun runOnUiThread(crossinline block: () -> Unit) {
    coroutineScope.launch(Dispatchers.Main) { block() }
}

inline fun runOnWorkThread(crossinline block: () -> Unit) {
    coroutineScope.launch(Dispatchers.IO) { block() }
}

