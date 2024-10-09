package com.suheng.wallpaper.myhealth.file

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import kotlin.properties.Delegates

object FileUtil {

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

    @Throws(IOException::class)
    private fun streamInputToOutput(
        inputStream: InputStream,
        outputStream: OutputStream,
        outputStreamCloseBefore: (() -> Unit)? = null,
    ) {
        val buffer = ByteArray(2048 * 2)
        var len: Int
        while (inputStream.read(buffer).also { len = it } != -1) {
            outputStream.write(buffer, 0, len)
        }
        outputStreamCloseBefore?.invoke()
        outputStream.close()
        inputStream.close()
    }

    @Throws(IOException::class)
    fun streamInputToByte(inputStream: InputStream): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        var data by Delegates.notNull<ByteArray>()
        streamInputToOutput(inputStream, byteArrayOutputStream) {
            data = byteArrayOutputStream.toByteArray()
        }
        return data
    }

    @Throws(IOException::class, FileNotFoundException::class)
    fun streamInputToFile(inputStream: InputStream, file: File) {
        streamInputToOutput(inputStream, FileOutputStream(file))
    }

    @Throws(IOException::class, FileNotFoundException::class)
    fun streamInputToFile(inputStream: InputStream, path: String) {
        streamInputToOutput(inputStream, FileOutputStream(path))
    }

    @Throws(IOException::class, FileNotFoundException::class)
    fun copyFile(inFile: File, outfile: File) {
        streamInputToFile(FileInputStream(inFile), outfile)
    }

    @Throws(IOException::class, FileNotFoundException::class)
    fun copyFile(inPath: String, outPath: String) {
        streamInputToFile(FileInputStream(inPath), outPath)
    }

}
