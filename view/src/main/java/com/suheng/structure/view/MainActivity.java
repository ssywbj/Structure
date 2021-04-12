package com.suheng.structure.view;

import android.os.Bundle;
import android.util.Log;

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

        //动态开启或关闭：adb shell setprop log.tag.<YOUR_LOG_TAG> <LEVEL>，如：adb shell setprop log.tag.wbj_main D
        if (Log.isLoggable("wbj_main", Log.INFO)) {
            Log.i("wbj_main", "INFO, INFO, INFO");
        }

        if (Log.isLoggable("wbj_main", Log.WARN)) {
            Log.w("wbj_main", "WARN, WARN, WARN");
        }

        Log.v("wbj_main", "VERBOSE, VERBOSE, VERBOSE");
        Log.d("wbj_main", "DEBUG, DEBUG, DEBUG");
        Log.i("wbj_main", "INFO, INFO, INFO");
        Log.w("wbj_main", "WARN, WARN, WARN");
        Log.e("wbj_main", "ERROR, ERROR, ERROR");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Log.isLoggable("wbj_maine", Log.INFO)) {
            Log.i("wbj_maine", "INFO-, INFO-, INFO-");
        }
    }
}
