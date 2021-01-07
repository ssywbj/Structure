package com.suheng.structure.view;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AnimationDrawable drawable = (AnimationDrawable) ContextCompat.getDrawable(this, R.drawable.map_my_location_img);
        //drawable.start();
        ImageView imageView = findViewById(R.id.view_image);
        imageView.setImageDrawable(drawable);
        drawable.start();
    }

}
