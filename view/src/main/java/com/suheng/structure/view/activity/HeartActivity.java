package com.suheng.structure.view.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.suheng.structure.view.R;
import com.suheng.structure.view.bezier.HeartView;

public class HeartActivity extends AppCompatActivity {

    private HeartView heartView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bezier_heart);

        heartView = findViewById(R.id.heart_view);

    }

    public void onStart(View view) {
        heartView.start();
    }
}
