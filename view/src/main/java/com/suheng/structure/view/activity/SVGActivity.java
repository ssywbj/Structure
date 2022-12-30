package com.suheng.structure.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.suheng.structure.view.R;

public class SVGActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_svg);

        findViewById(R.id.btn_svg_demo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SVGActivity.this, SVGDemoActivity.class));
            }
        });

        findViewById(R.id.btn_china_map).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SVGActivity.this, ChinaMapActivity.class));
            }
        });

        findViewById(R.id.btn_path_scale).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SVGActivity.this, SVGPathActivity.class));
            }
        });
    }

}
