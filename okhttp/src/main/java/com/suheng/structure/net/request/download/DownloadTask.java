package com.suheng.structure.net.request.download;

import android.os.Environment;

import com.suheng.structure.net.request.basic.OkHttpTask;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;

public abstract class DownloadTask extends OkHttpTask<File> {

    private File mFile;

    public DownloadTask(File file) {
        mFile = file;
    }

    public DownloadTask(String path) {
        this(new File(path));
    }

    public DownloadTask(String path, String fileName) {
        this(path + File.separator + fileName);
    }

    @Override
    protected void parseResponseBody(@NotNull ResponseBody responseBody) {
        mTotal = responseBody.contentLength();
        if (mTotal < 0) {
            setFailureCallback(ERROR_CODE_DOWNLOAD_EXCEPTION, "content length smaller than 0, total = " + mTotal);
            return;
        }

        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = responseBody.byteStream();

            if (mFile == null) {
                if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                    File dirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    mFile = new File(dirPath, this.getFileName());
                }
            }
            if (mFile.isDirectory()) {
                mFile = new File(mFile, this.getFileName());
            }

            outputStream = new FileOutputStream(mFile);
            byte[] buffer = new byte[1024 * 1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
                mProgress += len;
                mPercentage = 1.0 * mProgress / mTotal;

                setProgressCallback();
            }

            outputStream.flush();

            setFinishCallback(mFile);
        } catch (IOException e) {
            setFailureCallback(ERROR_CODE_DOWNLOAD_EXCEPTION, "onResponse download exception: " + e.toString());
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @NotNull
    private String getFileName() {
        return getURL().substring(getURL().lastIndexOf("/") + 1);
    }
}
