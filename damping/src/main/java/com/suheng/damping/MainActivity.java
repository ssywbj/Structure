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
    }

    public void openSwipeRefreshActivity(View view) {
        startActivity(new Intent(this, SwipeRefreshActivity.class));
    }

    public void openBounceEffectActivity(View view) {
        startActivity(new Intent(this, BounceEffectActivity.class));
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