package com.wiz.watchface;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    //private WatchFaceView mWatchFaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //mWatchFaceView = findViewById(R.id.watch_face);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //mWatchFaceView.onVisibilityChanged(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //mWatchFaceView.onVisibilityChanged(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //mWatchFaceView.destroy();
    }
}
