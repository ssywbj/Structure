package com.suheng.damping;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FlingAnimation;
import androidx.dynamicanimation.animation.SpringForce;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startActivity(new Intent(this, DampingActivity2.class));

        View view = findViewById(R.id.view_fling);
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

    public void openSwipeRefreshActivity(View view) {
        startActivity(new Intent(this, SwipeRefreshActivity.class));
    }

    public void openDampingViewActivity(View view) {
        startActivity(new Intent(this, DampingViewActivity.class));

        //SpringAnimation springAnimation = new SpringAnimation(view, DynamicAnimation.TRANSLATION_Y);
        /*SpringAnimation springAnimation = new SpringAnimation(view, DynamicAnimation.TRANSLATION_Y, 0);
        springAnimation.setSpring(setupSpringAnimation());
        springAnimation.start();*/
    }

    public void openDampingActivity(View view) {
        startActivity(new Intent(this, DampingActivity.class));
    }

    public void openDampingActivity2(View view) {
        startActivity(new Intent(this, DampingActivity2.class));
    }

    public void openDampingActivity3(View view) {
        startActivity(new Intent(this, DampingActivity3.class));
    }

    private SpringForce setupSpringAnimation() {
        SpringForce springForce = new SpringForce();
        springForce.setDampingRatio(SpringForce.DAMPING_RATIO_HIGH_BOUNCY)
                .setStiffness(SpringForce.STIFFNESS_LOW).setFinalPosition(200f);
        return springForce;
    }

}