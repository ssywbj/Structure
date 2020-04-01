package com.suheng.structure.net.request.upload;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.suheng.structure.net.request.basic.OkHttpTask;

import java.io.File;
import java.lang.ref.WeakReference;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

public abstract class UploadTask extends OkHttpTask {
    private UIHandler mUIHandler = new UIHandler(this);

    protected void onUpload(String fileName, String path) {
        this.onUpload(fileName, path, this);
    }

    protected void onUpload(String fileName, String path, Object tag) {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addPart(getRequestBody())
                .addFormDataPart("desc", "identity face")
                .addFormDataPart("file", fileName, RequestBody.create(new File(path),
                        MediaType.parse("multipart/form-data"))).build();

        Request request = new Request.Builder().tag(tag)
                .url(this.getURL())
                .post(requestBody)
                .build();
        enqueue(request);

        //doPostRequest(tag);
    }

    private static class UIHandler extends Handler {
        private WeakReference<UploadTask> mTaskReference;

        private UIHandler(UploadTask task) {
            mTaskReference = new WeakReference<>(task);
        }

        @Override
        public void dispatchMessage(@NonNull Message msg) {
            super.dispatchMessage(msg);
            UploadTask task = mTaskReference.get();
            if (task == null) {
                return;
            }

            if (msg.what == 1) {
                if (msg.obj instanceof String) {
                    //task.mOnFailureListener.onFailure((String) msg.obj);
                }
            }
        }
    }
}