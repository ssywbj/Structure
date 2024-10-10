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

    fun streamInputToFile(input: InputStream, dest: File): Result<Unit> {
        return streamInputToOutput(input, FileOutputStream(dest))
    }

    fun streamInputToFile(input: InputStream, dest: String): Result<Unit> {
        return streamInputToOutput(input, FileOutputStream(dest))
    }

    fun copyFile(source: File, dest: File): Result<Unit> {
        return streamInputToFile(FileInputStream(source), dest)
    }

    fun copyFile(source: String, dest: String): Result<Unit> {
        return streamInputToFile(FileInputStream(source), dest)
    }

}

val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

inline fun runOnUiThread(crossinline block: () -> Unit) {
    coroutineScope.launch(Dispatchers.Main) { block() }
}

inline fun runOnWorkThread(crossinline block: () -> Unit) {
    coroutineScope.launch(Dispatchers.IO) { block() }
}

