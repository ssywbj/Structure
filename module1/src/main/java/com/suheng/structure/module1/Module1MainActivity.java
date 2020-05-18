package com.suheng.structure.module1;

import android.os.Bundle;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.suheng.structure.common.arouter.RouteTable;
import com.suheng.structure.ui.architecture.basic.BasicActivity;

@Route(path = RouteTable.MODULE1_ATY_MODULE1_MAIN)
public class Module1MainActivity extends BasicActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module1_aty_module1_main);

        SocketClient.getInstance().connect(RouteTable.SOCKET_HOST, RouteTable.SOCKET_PORT);
    }

    public void sendString(View view) {
        SocketClient.getInstance().sendRequest("ni hao");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SocketClient.getInstance().disconnect();
    }
}
