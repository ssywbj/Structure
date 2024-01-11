package com.suheng.structure.wallpaperpicker;

import android.app.Activity;
import android.app.WallpaperManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class WallpaperPick2Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wallpaperpick_activity_wallpaper_pick2);

        final TextView textView = findViewById(R.id.tv_title);
        findViewById(R.id.root_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("version_name", "1.0.0.a");
                bundle.putInt("version_code", 10000000);
                WallpaperManager.getInstance(WallpaperPick2Activity.this)
                        .sendWallpaperCommand(v.getWindowToken(), "com.suheng.wallpaper.CLICK"
                                , (int) textView.getX(), (int) textView.getY(), 0, bundle);
            }
        });
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(WallpaperPick2Activity.this, textView.getText(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
