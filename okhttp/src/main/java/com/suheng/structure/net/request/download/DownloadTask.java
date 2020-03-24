package com.suheng.structure.net.request.download;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.suheng.structure.net.callback.OnDownloadListener;
import com.suheng.structure.net.request.basic.BasicTask;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;

import okhttp3.ResponseBody;

public abstract class DownloadTask extends BasicTask {
    private static final int MSG_DOWNLOADING = 0;
    private static final int MSG_DOWNLOAD_FINISH = 1;

    private long mTotal, mProgress;
    private double mPercentage, mTakeTime;
    private File mFile;

    private UIHandler mUIHandler = new UIHandler(this);
    private OnDownloadListener mOnDownloadListener;

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
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = responseBody.byteStream();
            mTotal = responseBody.contentLength();

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

            byte[] buffer = new byte[100 * 1024];
            int len;
            long currentTimeMillis = System.currentTimeMillis();
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
                mProgress += len;
                mPercentage = 1.0 * mProgress / mTotal;

                if (mOnDownloadListener != null) {
                    mUIHandler.sendEmptyMessage(MSG_DOWNLOADING);
                }
            }

            outputStream.flush();

            mTakeTime = 1.0 * (System.currentTimeMillis() - currentTimeMillis) / 1000;
            if (mOnDownloadListener != null) {
                mUIHandler.sendEmptyMessage(MSG_DOWNLOAD_FINISH);
            }
        } catch (IOException e) {
            setErrorCodeAndMsg(ERROR_CODE_DOWNLOAD_EXCEPTION, "onResponse download exception: " + e.toString());
            sendFailureMessage();
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

            responseBody.close();
        }
    }

    @NotNull
    private String getFileName() {
        return getURL().substring(getURL().lastIndexOf("/") + 1);
    }

    private void onTaskDownloading() {
        mOnDownloadListener.onDownloading(mPercentage, mProgress, mTotal);
    }

    private void onTaskDownloadFinish() {
        mOnDownloadListener.onDownloadFinish(mFile, mTakeTime);
    }

    public void setOnDownloadListener(OnDownloadListener onDownloadListener) {
        mOnDownloadListener = onDownloadListener;
    }

    private static class UIHandler extends Handler {
        private WeakReference<DownloadTask> mTaskReference;

        private UIHandler(DownloadTask task) {
            mTaskReference = new WeakReference<>(task);
        }

        @Override
        public void dispatchMessage(@NonNull Message msg) {
            super.dispatchMessage(msg);
            DownloadTask task = mTaskReference.get();
            if (task == null) {
                return;
            }

            switch (msg.what) {
                case MSG_DOWNLOADING:
                    task.onTaskDownloading();
                    break;
                case MSG_DOWNLOAD_FINISH:
                    task.onTaskDownloadFinish();
                    break;
                default:
                    break;
            }
        }
    }

}
