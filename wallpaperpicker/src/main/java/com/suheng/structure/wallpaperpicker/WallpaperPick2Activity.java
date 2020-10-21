package com.suheng.structure.wallpaperpicker;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class WallpaperPick2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wallpaperpick_activity_wallpaper_pick);
        findViewById(R.id.root_layout).setBackgroundColor(Color.BLUE);
    }

}
