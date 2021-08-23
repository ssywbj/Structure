package com.suheng.structure.view;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.ref.WeakReference;

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

        /*DateTimePicker picker = findViewById(R.id.datePicker);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 16);
        calendar.set(Calendar.MINUTE, 30);
        picker.init(calendar, DateTimePicker.FORMAT_H_M);
        picker.setOnDateChangeListener(new DateTimePicker.OnDateTimeChangeListener() {
            @Override
            public void onChange(DateTimePicker picker, Calendar calendar) {
            }
        });*/

        /*textView.post(new Runnable() {
            @Override
            public void run() {

            }
        });*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Log.isLoggable("wbj_maine", Log.INFO)) {
            Log.i("wbj_maine", "INFO-, INFO-, INFO-");
        }
    }

    private static WeakReference<Activity> mCurrentActivity;

    Application.ActivityLifecycleCallbacks mActivityLifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
            mCurrentActivity = new WeakReference<>(activity);
        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {

        }

        @Override
        public void onActivityResumed(@NonNull Activity activity) {

        }

        @Override
        public void onActivityPaused(@NonNull Activity activity) {

        }

        @Override
        public void onActivityStopped(@NonNull Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {

        }
    };

}
