package com.suheng.structure.module1;

import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.suheng.structure.common.arouter.RouteTable;
import com.suheng.structure.ui.architecture.basic.BasicActivity;

@Route(path = RouteTable.MODULE1_ATY_MODULE1_MAIN)
public class Module1MainActivity extends BasicActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module1_aty_module1_main);
    }

}
