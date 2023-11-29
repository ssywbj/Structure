package com.suheng.structure.view.activity;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.suheng.structure.view.R;
import com.suheng.structure.view.wheel.CameraRotateAnimation;

public class GraphicsCameraActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphic_camera);

        findViewById(R.id.iView).setOnClickListener(v -> {
            Object tag = v.getTag();
            if (tag == null) {
                v.setTag(0);
            }
            int status = 0;
            if (tag instanceof Integer) {
                status = (int) tag;
            }
            final float fromDegrees = status == 0 ? 0 : -180;
            final float toDegrees = status == 0 ? -180 : 0;
            final CameraRotateAnimation rotation = new CameraRotateAnimation(v, fromDegrees, toDegrees);
            rotation.setDuration(1000);
            rotation.setFillAfter(true);
            rotation.setInterpolator(new LinearInterpolator());
            rotation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    Object tag = v.getTag();
                    if (tag instanceof Integer) {
                        int status = (int) tag;
                        v.setTag(status == 0 ? 1 : 0);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            rotation.startAnimation();
        });

    }

}
