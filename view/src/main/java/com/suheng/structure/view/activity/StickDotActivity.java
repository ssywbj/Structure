package com.suheng.structure.view.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.suheng.structure.view.R;
import com.suheng.structure.view.bezier.StickDotView;

public class StickDotActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stick_dot);

        StickDotView stickDotView = findViewById(R.id.stick_dot_view);
        stickDotView.setText("99+");
        stickDotView.setOnDragListener(new StickDotView.onDragStatusListener() {
            @Override
            public void onDrag() {
            }

            @Override
            public void onMove() {

            }

            @Override
            public void onRestore() {

            }

            @Override
            public void onDismiss() {
                stickDotView.setVisibility(View.GONE);
            }
        });

        findViewById(R.id.btn_reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stickDotView.setVisibility(View.VISIBLE);
            }
        });
    }

}
