package com.suheng.structure.module3;

import android.os.Bundle;

import com.suheng.structure.ui.architecture.basic.BasicActivity;

public class FragmentActivity extends BasicActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module3_aty_fragment);
    }
}
