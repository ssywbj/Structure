package com.suheng.structure.view.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FlingAnimation;

import com.suheng.structure.view.R;

public class FlingAnimActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fling_anim);

        View view = findViewById(R.id.fling_aty_root);
        GestureDetector.OnGestureListener mOnGestureListener
                = new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                //return super.onDown(e);
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                FlingAnimation flingAnimation;
                if (Math.abs(velocityX) > Math.abs(velocityY)) {
                    flingAnimation = new FlingAnimation(view, DynamicAnimation.TRANSLATION_X);
                    flingAnimation.setStartVelocity(velocityX);
                    flingAnimation.setMinValue(0f);
                    flingAnimation.setMaxValue(100);
                } else {
                    flingAnimation = new FlingAnimation(view, DynamicAnimation.TRANSLATION_Y);
                    flingAnimation.setStartVelocity(velocityY);
                    flingAnimation.setMinValue(0f);
                    flingAnimation.setMaxValue(200);
                }
                flingAnimation.setFriction(1.1f);
                flingAnimation.start();
                //return super.onFling(e1, e2, velocityX, velocityY);
                return true;
            }
        };

        GestureDetector gestureDetector = new GestureDetector(this, mOnGestureListener);
        view.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });

    }

}