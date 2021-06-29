package com.suheng.damping;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startActivity(new Intent(this, DampingActivity3.class));
    }

    public void openSwipeRefreshActivity(View view) {
        startActivity(new Intent(this, SwipeRefreshActivity.class));
    }

    public void openNestedScrollViewActivity(View view) {
        startActivity(new Intent(this, NestedScrollViewActivity.class));
    }

    public void openSpringAnimActivity(View view) {
        startActivity(new Intent(this, SpringAnimActivity.class));
    }

    public void openFlingAnimActivity(View view) {
        startActivity(new Intent(this, FlingAnimActivity.class));
    }

    public void openScrollGestureActivity(View view) {
        startActivity(new Intent(this, ScrollGestureActivity.class));
    }

    public void openDampingViewActivity(View view) {
        startActivity(new Intent(this, DampingViewActivity.class));
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
}