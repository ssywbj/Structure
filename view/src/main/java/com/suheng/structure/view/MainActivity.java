package com.suheng.structure.view;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*AnimationDrawable drawable = (AnimationDrawable) ContextCompat.getDrawable(this, R.drawable.map_my_location_img);
        //drawable.start();
        ImageView imageView = findViewById(R.id.view_image);
        imageView.setImageDrawable(drawable);
        drawable.start();
        Log.d("SVGView", "drawable, NumberOfFrames: " + drawable.getNumberOfFrames() + ", Duration: " + drawable.getDuration(0));*/
    }

}
