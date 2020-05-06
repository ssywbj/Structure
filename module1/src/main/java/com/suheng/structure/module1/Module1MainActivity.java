package com.suheng.structure.module1;

import android.os.Bundle;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.suheng.structure.common.arouter.RouteTable;
import com.suheng.structure.module1.request.StringTaskImpl;
import com.suheng.structure.net.callback.OnFailureListener;
import com.suheng.structure.net.callback.OnFinishListener;
import com.suheng.structure.ui.architecture.basic.BasicActivity;

@Route(path = RouteTable.MODULE1_ATY_MODULE1_MAIN)
public class Module1MainActivity extends BasicActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module1_aty_module1_main);

        findViewById(R.id.text_test_string_task).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringTaskImpl stringTask = new StringTaskImpl();
                stringTask.addOnFinishListener(new OnFinishListener<String>() {
                    @Override
                    public void onFinish(String data) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(int code, String errorMsg) {

                    }
                });
            }
        });
    }

}
