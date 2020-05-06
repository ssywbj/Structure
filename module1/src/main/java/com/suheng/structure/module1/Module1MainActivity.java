package com.suheng.structure.module1;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.suheng.structure.common.arouter.RouteTable;
import com.suheng.structure.ui.architecture.basic.BasicActivity;

import java.io.File;

@Route(path = RouteTable.MODULE1_ATY_MODULE1_MAIN)
public class Module1MainActivity extends BasicActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module1_aty_module1_main);

        findViewById(R.id.text_test_string_task).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, 1);

                /*StringTaskImpl stringTask = new StringTaskImpl();
                stringTask.addOnFinishListener(new OnFinishListener<String>() {
                    @Override
                    public void onFinish(String data) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(int code, String errorMsg) {

                    }
                });*/
            }
        });
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
            String path = uri.getPath();
            if (path == null) {
                showToast("file path is null");
                return;
            }

            File file = new File(path);
            if (file.exists()) {
                Toast.makeText(this, "文件路径：" + file.getPath(), Toast.LENGTH_SHORT).show();
            } else {
                showToast(file + ": is not is exists");
            }
        }
    }
}
