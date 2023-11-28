package com.suheng.structure.view.activity;

import android.os.Bundle;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.suheng.structure.view.R;
import com.suheng.structure.view.wheel.CameraRotateAnimation;

public class ScrollTrackerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll_tracker);

        findViewById(R.id.iView).setOnClickListener(v -> {
            //括号内参数分别为（上下文，开始角度，结束角度，x轴中心点，y轴中心点，深度，是否扭曲）
            final CameraRotateAnimation rotation = new CameraRotateAnimation(v, 0, 180);
            rotation.setDuration(3000);
            rotation.setFillAfter(true);
            rotation.setInterpolator(new LinearInterpolator());
            rotation.startAnimation();
        });
    }

}
