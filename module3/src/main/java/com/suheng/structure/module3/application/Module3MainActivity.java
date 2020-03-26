package com.suheng.structure.module3.application;

import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.suheng.structure.common.arouter.RouteTable;
import com.suheng.structure.module3.R;
import com.suheng.structure.ui.architecture.basic.BasicActivity;

@Route(path = RouteTable.MODULE3_ATY_MODULE3_MAIN)
public class Module3MainActivity extends BasicActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module3_aty_module3_main);
    }
}
