package com.suheng.structure.module1;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import com.suheng.structure.module1.utils.FileUtil;
import com.suheng.structure.net.callback.OnFailureListener;
import com.suheng.structure.net.callback.OnFinishListener;
import com.suheng.structure.ui.architecture.basic.BasicActivity;

import java.io.File;

@Route(path = RouteTable.MODULE1_ATY_MODULE1_MAIN)
public class Module1MainActivity extends BasicActivity {

    private static final int REQUEST_CODE_OPEN_FILE_MANAGER = 1;

    private DownloadTaskImpl2 mDownloadTaskImpl2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module1_aty_module1_main);

        findViewById(R.id.text_test_string_task).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        findViewById(R.id.text_test_upload_task).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestExternalStoragePermission(v.getId());//打开系统文件管理器之前先申请存储权限
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
        } else if (businessId == R.id.text_test_upload_task) {//打开系统文件管理器
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");//设置类型："*/*"代表任意类型
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, REQUEST_CODE_OPEN_FILE_MANAGER);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_CODE_OPEN_FILE_MANAGER) {
            if (data == null) {
                return;
            }
            Uri uri = data.getData();
            if (uri == null) {
                return;
            }

            String path;
            if ("file".equalsIgnoreCase(uri.getScheme())) {//使用第三方应用打开
                path = uri.getPath();
            } else {
                try {
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                        path = FileUtil.getPath(this, uri);
                    } else {//4.4之前
                        path = FileUtil.getPathFromUri(this, uri);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    path = "error file path";
                }
            }
            Toast.makeText(this, path, Toast.LENGTH_SHORT).show();
        }
    }
}
