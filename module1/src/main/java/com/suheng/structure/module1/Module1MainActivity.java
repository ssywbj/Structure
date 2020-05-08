package com.suheng.structure.module1;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.google.gson.Gson;
import com.suheng.structure.common.arouter.RouteTable;
import com.suheng.structure.module1.request.DownloadTaskImpl;
import com.suheng.structure.module1.request.DownloadTaskImpl2;
import com.suheng.structure.module1.request.JsonTaskImpl;
import com.suheng.structure.module1.request.JsonTaskImpl2;
import com.suheng.structure.module1.request.StringTaskImpl;
import com.suheng.structure.module1.request.StringTaskImpl2;
import com.suheng.structure.module1.request.bean.JsonTaskErrorBean;
import com.suheng.structure.module1.request.bean.StringTaskBean;
import com.suheng.structure.net.callback.OnFailureListener;
import com.suheng.structure.net.callback.OnFinishListener;
import com.suheng.structure.ui.architecture.basic.BasicActivity;

import java.io.File;

@Route(path = RouteTable.MODULE1_ATY_MODULE1_MAIN)
public class Module1MainActivity extends BasicActivity {

    private DownloadTaskImpl2 mDownloadTaskImpl2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module1_aty_module1_main);

        findViewById(R.id.text_test_string_task).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                //intent.setType("*/*");//设置类型，这里是任意类型
                //intent.addCategory(Intent.CATEGORY_OPENABLE);
                //startActivityForResult(intent, 1);

                showProgressDialog("");
                StringTaskImpl stringTask = new StringTaskImpl("韦小宝", "adce1234");
                stringTask.addOnFinishListener(new OnFinishListener<String>() {
                    @Override
                    public void onFinish(String data) {
                        dismissProgressDialog();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(int code, String errorMsg) {
                        dismissProgressDialog();
                    }
                }).doPostRequest();
            }
        });

        findViewById(R.id.text_test_string_task2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog("");
                final StringTaskImpl2 stringTask = new StringTaskImpl2("韦小宝", "adce1234");
                stringTask.addOnFinishListener(new OnFinishListener<StringTaskBean>() {
                    @Override
                    public void onFinish(StringTaskBean data) {
                        Log.d(stringTask.getLogTag(), "data: " + data);
                        dismissProgressDialog();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(int code, String errorMsg) {
                        dismissProgressDialog();
                    }
                }).doPostRequest();
            }
        });

        findViewById(R.id.text_test_json_task).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog("");
                final JsonTaskImpl jsonTask = new JsonTaskImpl("韦小宝", "adce1234");
                jsonTask.addOnFinishListener(new OnFinishListener<StringTaskBean>() {
                    @Override
                    public void onFinish(StringTaskBean data) {
                        Log.d(jsonTask.getLogTag(), "data: " + data);
                        dismissProgressDialog();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(int code, String errorMsg) {
                        dismissProgressDialog();
                    }
                }).doPostRequest();
            }
        });

        findViewById(R.id.text_test_json_task2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog("");
                final JsonTaskImpl2 jsonTask = new JsonTaskImpl2("满意", "adce1234");
                jsonTask.addOnFinishListener(new OnFinishListener<StringTaskBean>() {
                    @Override
                    public void onFinish(StringTaskBean data) {
                        Log.d(jsonTask.getLogTag(), "data: " + data);
                        dismissProgressDialog();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(int code, String errorMsg) {
                        dismissProgressDialog();
                        showToast(errorMsg);

                        if (code == -1) {
                            Gson gson = new Gson();
                            JsonTaskErrorBean errorBean = gson.fromJson(jsonTask.getErrorData(), JsonTaskErrorBean.class);
                            Log.d(jsonTask.getLogTag(), "error bean: " + errorBean);
                        }
                    }
                }).doPostRequest();
            }
        });

        findViewById(R.id.text_test_download_task).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestExternalStoragePermission(v.getId());
            }
        });

        findViewById(R.id.text_test_download_task2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestExternalStoragePermission(v.getId());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDownloadTaskImpl2 != null) {
            mDownloadTaskImpl2.cancelTask();
        }
    }

    @Override
    public void openedExternalStoragePermission(int businessId) {
        super.openedExternalStoragePermission(businessId);
        if (businessId == R.id.text_test_download_task) {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                final DownloadTaskImpl downloadTask = new DownloadTaskImpl(Environment.getExternalStoragePublicDirectory
                        (Environment.DIRECTORY_DOWNLOADS).getPath(), System.currentTimeMillis() + ".png");
                downloadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(int code, String error) {
                        showToast("下载失败");
                    }
                }).addOnFinishListener(new OnFinishListener<File>() {
                    @Override
                    public void onFinish(File data) {
                        showToast("下载完成，路径：" + data.getPath());
                    }
                }).doRequest();
            }
        } else if (businessId == R.id.text_test_download_task2) {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                mDownloadTaskImpl2 = new DownloadTaskImpl2(Environment.getExternalStoragePublicDirectory
                        (Environment.DIRECTORY_DOWNLOADS).getPath(), System.currentTimeMillis() + ".png");
                mDownloadTaskImpl2.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(int code, String error) {
                        showToast("下载失败");
                    }
                }).addOnFinishListener(new OnFinishListener<File>() {
                    @Override
                    public void onFinish(File data) {
                        showToast("下载完成，路径：" + data.getPath());
                    }
                }).doRequest();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == 1) {
            if (data == null) {
                showToast("data is null");
                return;
            }
            Uri uri = data.getData();
            if (uri == null) {
                showToast("uri is null");
                return;
            }

            String path;
            if ("file".equalsIgnoreCase(uri.getScheme())) {//使用第三方应用打开
                path = uri.getPath();
                Toast.makeText(this, path + "11111", Toast.LENGTH_SHORT).show();
                return;
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                path = getPath(this, uri);
                Toast.makeText(this, path, Toast.LENGTH_SHORT).show();
            } else {//4.4以下下系统调用方法
                path = getPathFromUri(uri);
                Toast.makeText(this, path + "222222", Toast.LENGTH_SHORT).show();
            }

            /*String path = uri.getPath();
            if (path == null) {
                showToast("file path is null");
                return;
            }

            File file = new File(path);
            if (file.exists()) {
                Toast.makeText(this, "文件路径：" + file.getPath(), Toast.LENGTH_SHORT).show();
            } else {
                showToast(file + ": is not is exists");
            }*/
        }
    }

    public String getPathFromUri(Uri uri) {
        String path = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (null != cursor && cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            path = cursor.getString(column_index);
            cursor.close();
        }
        return path;
    }

    public String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {// DownloadsProvider
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {// MediaProvider
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {// MediaStore (and general)
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {// File
            return uri.getPath();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
