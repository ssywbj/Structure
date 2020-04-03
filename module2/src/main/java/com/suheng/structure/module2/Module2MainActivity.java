package com.suheng.structure.module2;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.documentfile.provider.DocumentFile;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.suheng.structure.common.arouter.RouteTable;
import com.suheng.structure.data.DataManager;
import com.suheng.structure.data.net.URLConstants;
import com.suheng.structure.data.net.bean.UserInfo;
import com.suheng.structure.module2.request.BeautyTask;
import com.suheng.structure.module2.utils.DocumentsUtils;
import com.suheng.structure.module2.utils.NetWorkUtil;
import com.suheng.structure.net.callback.OnFailureListener;
import com.suheng.structure.net.callback.OnFinishListener;
import com.suheng.structure.net.callback.OnProgressListener;
import com.suheng.structure.ui.architecture.basic.BasicActivity;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Route(path = RouteTable.MODULE2_ATY_MODULE2_MAIN)
public class Module2MainActivity extends BasicActivity {

    @Autowired
    DataManager mDataManager;
    private Button mBtnLoginStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module2_aty_module2_main);

        ARouter.getInstance().inject(this);

        mBtnLoginStatus = findViewById(R.id.btn_login_status);
        mBtnLoginStatus.setText(mDataManager.isLoginSuccessful() ? "退出" : "登录");

        mBtnLoginStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog("");
                if (mDataManager.isLoginSuccessful()) {
                    /*mDataManager.doExitLoginRequest().setOnResultListener(new OnResultListener<UserInfo, String>() {
                        @Override
                        public void onRightResult(UserInfo data) {
                            dismissProgressDialog();
                            mDataManager.setLoginSuccessful(false);
                            mBtnLoginStatus.setText("登录");
                        }

                        @Override
                        public void onErrorResult(int code, String msg, String data) {
                            dismissProgressDialog();
                            showToast("退出登录失败！");
                        }
                    });*/

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            /*OkHttpClient httpClient = new OkHttpClient.Builder().connectionSpecs(Collections.singletonList(new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                                    .allEnabledTlsVersions().build())).build();*/
                            OkHttpClient httpClient = new OkHttpClient();
                            Request request = new Request.Builder().url(URLConstants.URL_LOGIN_REQUEST).build();
                            httpClient.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure( Call call,  IOException e) {
                                    new Handler(getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            dismissProgressDialog();
                                            showToast("退出登录失败！");
                                        }
                                    });
                                }

                                @Override
                                public void onResponse( Call call,  Response response) throws IOException {
                                    new Handler(getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            dismissProgressDialog();
                                            mDataManager.setLoginSuccessful(false);
                                            mBtnLoginStatus.setText("登录");
                                        }
                                    });
                                }
                            });

                        }
                    }).start();
                } else {
                    mDataManager.doLoginRequest("Wbj韦帮杰", "wbj89").addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(int code, String errorMsg) {
                            dismissProgressDialog();
                        }
                    }).addOnFinishListener(new OnFinishListener<UserInfo>() {
                        @Override
                        public void onFinish(UserInfo data) {
                            dismissProgressDialog();

                            mDataManager.setLoginSuccessful(true);
                            mBtnLoginStatus.setText("退出");
                        }
                    });

                    /*final LoginTask2 loginTask = new LoginTask2("Wbj", "wbj89");
                    loginTask.doRequest().addOnFinishListener(new OnFinishListener<UserInfo>() {
                        @Override
                        public void onFinish(UserInfo data) {
                            dismissProgressDialog();

                            mDataManager.setLoginSuccessful(true);
                            mBtnLoginStatus.setText("退出");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(int code, String errorMsg) {
                            dismissProgressDialog();
                            if (code == 1) {
                                showToast(errorMsg);
                            }
                        }
                    });*/
                }
            }
        });

        findViewById(R.id.btn_download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestExternalStoragePermission(R.id.btn_download);
            }
        });

        findViewById(R.id.btn_upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestExternalStoragePermission(R.id.btn_upload);
                //startService(new Intent(Module2MainActivity.this, Module2Service.class));
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("LoginTask", "--hostIp-->" + NetWorkUtil.getHostIp());
            }
        }).start();
    }

    //String sdcardFilePath = "/storage/5285-8EF6/stealAccountRisks1.apk";//Gionee M100
    //String sdcardFilePath = "/storage/14CB-D108/11.apk";//Gionee M100 android9
    String sdcardFilePath = "/sdcard/virus/病毒/商友网.apk";//Gionee M100 android9
    //String sdcardFilePath = "/storage/14CB-D108/virus/支付风险/伪淘宝.apk";//Gionee M100
    //String sdcardFilePath = "/storage/sdcard1/apks/10.apk";//台电T98 android 4.4.2
    //String sdcardFilePath = "/storage/otg/sdb/apks/10.apk";//vivo Xplay android 4.2.2
    //String sdcardFilePath = "/storage/sdcard1/360.apk";//vivo Y51A android5.0.2

    private Uri mSDCardUri;

    @Override
    public void openedExternalStoragePermission(int businessId) {
        if (businessId == R.id.btn_download) {
            /*final BeautyTask beautyTask = new BeautyTask(Environment.getExternalStoragePublicDirectory
                    (Environment.DIRECTORY_MOVIES));
            final BeautyTask beautyTask = new BeautyTask(Environment.getExternalStoragePublicDirectory
                    (Environment.DIRECTORY_PICTURES).getPath());*/
            final BeautyTask beautyTask = new BeautyTask(Environment.getExternalStoragePublicDirectory
                    (Environment.DIRECTORY_DOWNLOADS).getPath(), System.currentTimeMillis() + ".jpg");
            beautyTask.doPostRequest().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(int code, String error) {
                    showToast("下载失败");
                }
            }).addOnProgressListener(new OnProgressListener() {
                @Override
                public void onProgress(double percentage, long progress, long total) {
                }
            }).addOnFinishListener(new OnFinishListener<File>() {
                @Override
                public void onFinish(File data) {
                    showToast("下载完成");
                }
            });
        } else if (businessId == R.id.btn_upload) {
            String sdcardPath = DocumentsUtils.getStoragePath(this, true);
            String internalPath = DocumentsUtils.getStoragePath(this, false);
            String[] sdcardExtPath = DocumentsUtils.getExtSdCardPaths(this);
            Log.d(mTag, "internalPath: " + internalPath + ", sdcard path: " + sdcardPath + ", " + sdcardExtPath[0]);

            File file = new File(sdcardFilePath);
            if (file.exists()) {
                Log.d(mTag, file + ", canRead: " + file.canRead() + ", canWrite: " + file.canWrite()
                        + ", canExecute: " + file.canExecute());

                Intent intent = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    StorageManager storageManager = getSystemService(StorageManager.class);
                    StorageVolume volume = null;
                    if (storageManager != null) {
                        volume = storageManager.getStorageVolume(file);
                    }
                    if (volume != null) {
                        intent = volume.createAccessIntent(null);
                    }
                }
                if (intent == null) {
                    /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                    }*/

                    //获取到指定文件夹，这里为：/storage/emulated/0/Android/data/你的包	名/files/Download
                    intent = new Intent(Intent.ACTION_GET_CONTENT);
                    //7.0以上跳转系统文件需用FileProvider，参考链接：https://blog.csdn.net/growing_tree/article/details/71190741
                    Uri uri = DocumentsUtils.getFileUri(this, file);
                    intent.setData(uri);
                    //intent.setDataAndType(uri,"file/*.apk");
                    //intent.setDataAndType(uri,"application/vnd.ms-powerpoint");
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(intent, 2);
                }
                if (intent != null) {
                    startActivityForResult(intent, 2);
                } else {
                    boolean delete = file.delete();
                    if (delete) {
                        Log.d(mTag, file + ", delete successful.");
                    } else {
                        Log.w(mTag, file + ", delete fail!");
                    }

                    delete = DocumentFile.fromFile(file).delete();
                    if (delete) {
                        Log.d(mTag, file + ", delete successful.");
                    } else {
                        Log.w(mTag, file + ", delete fail!");
                    }

                    Uri uri = DocumentFile.fromFile(file).getUri();
                    delete = DocumentsUtils.delete(this, file, uri);
                    if (delete) {
                        Log.d(mTag, file + ", delete successful.");
                    } else {
                        Log.w(mTag, file + ", delete fail!");
                    }

                }
            } else {
                Log.d(mTag, file + " is not exists.");
            }

            /*//能正常删除内置存储空间上的文件
            file = new File(Environment.getExternalStorageDirectory(), "otherRisks2.apk");
            if (file.exists()) {
                boolean delete = file.delete();
                if (delete) {
                    Log.d(mTag, file + ", delete successful.");
                } else {
                    Log.w(mTag, file + ", delete fail!");
                }
            } else {
                Log.d(mTag, file + " is not exists.");
            }*/
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 2:
                if (data != null && data.getData() != null) {
                    mSDCardUri = data.getData();
                    //DocumentsUtils.saveTreeUri(this, sdcardFilePath, uri);
                    File file = new File(sdcardFilePath);

                    //stealAccountRisks1.apk URI: SDCardUri: content://com.android.externalstorage.documents/tree/14CB-D108%3A, DocumentFile.fromFile(File).getUri(): file:///storage/14CB-D108/stealAccountRisks1.apk, Uri.fromFile(file): file:///storage/14CB-D108/stealAccountRisks1.apk
                    //stealAccountRisks2.apk URI: SDCardUri: content://com.android.externalstorage.documents/tree/14CB-D108%3A, DocumentFile.fromFile(File).getUri(): file:///storage/14CB-D108/stealAccountRisks2.apk, Uri.fromFile(file): file:///storage/14CB-D108/stealAccountRisks2.apk
                    //stealAccountRisks3.apk URI: SDCardUri: content://com.android.externalstorage.documents/tree/14CB-D108%3A, DocumentFile.fromFile(File).getUri(): file:///storage/14CB-D108/stealAccountRisks3.apk, Uri.fromFile(file): file:///storage/14CB-D108/stealAccountRisks3.apk
                    Log.d(mTag, "SDCardUri: " + mSDCardUri + ", DocumentFile.fromFile(File).getUri(): " +
                            DocumentFile.fromFile(file).getUri() + ", Uri.fromFile(file): " + Uri.fromFile(file));

                    /*if (file.exists()) {
                        boolean delete = file.delete();
                        if (delete) {
                            Log.d(mTag, file + ", delete successful.");
                        } else {
                            Log.w(mTag, file + ", delete fail!");
                        }

                        delete = DocumentFile.fromFile(file).delete();
                        if (delete) {
                            Log.d(mTag, file + ", delete successful.");
                        } else {
                            Log.w(mTag, file + ", delete fail!");
                        }

                        //delete = DocumentsUtils.delete(this, file);
                        delete = DocumentsUtils.delete(this, file, uri);
                        if (delete) {
                            Log.d(mTag, file + ", delete successful.");
                        } else {
                            Log.w(mTag, file + ", delete fail!");
                        }
                    } else {
                        Log.d(mTag, file + " is not exists.");
                    }*/
                }
                break;
            default:
                break;
        }

    }

}
