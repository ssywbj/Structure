package com.suheng.structure.view.activity;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.suheng.structure.view.R;
import com.suheng.structure.view.utils.XmlSaxParser;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String ATY_PKG_PREFIX = "com.suheng.structure.view.activity.";
    private final ArrayMap<String, String> mStringArrayMap = new ArrayMap<>();
    private final List<String> mStringList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.view_main_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());//设置Item增加、移除动画
        ContentAdapter adapter = new ContentAdapter(mStringList);
        recyclerView.setAdapter(adapter);

        mStringList.add("LetterSelect");
        mStringArrayMap.put(mStringList.get(mStringList.size() - 1), ATY_PKG_PREFIX + "LetterSelectActivity");
        mStringList.add("RecyclerView");
        mStringArrayMap.put(mStringList.get(mStringList.size() - 1), ATY_PKG_PREFIX + "RecyclerViewActivity");

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

        /*ImageView imageView = findViewById(R.id.image_svg);

        //mVectorDrawable = AnimatedVectorDrawableCompat.create(this, R.drawable.water_drop_anim);
        //mVectorDrawable = AnimatedVectorDrawableCompat.create(this, R.drawable.search_anim);
        mVectorDrawable = AnimatedVectorDrawableCompat.create(this, R.drawable.tt_search_anim);
        if (mVectorDrawable != null) {
            imageView.setImageDrawable(mVectorDrawable);
            mVectorDrawable.registerAnimationCallback(new Animatable2Compat.AnimationCallback() {
                @Override
                public void onAnimationStart(Drawable drawable) {
                    super.onAnimationStart(drawable);
                    Log.d("Wbj", "-----svg anim start-----");
                }

                @Override
                public void onAnimationEnd(Drawable drawable) {
                    super.onAnimationEnd(drawable);
                    Log.d("Wbj", "-----svg anim end-----");
                }
            });

            //mVectorDrawable.start();
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Log.isLoggable("wbj_maine", Log.INFO)) {
            Log.i("wbj_maine", "INFO-, INFO-, INFO-");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mStringArrayMap.clear();
        mStringList.clear();
    }

    private final class ContentAdapter extends RecyclerView.Adapter<ContentHolder> {

        private final List<String> mDataList;

        public ContentAdapter(List<String> dataList) {
            mDataList = dataList;
        }

        @Override
        public ContentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ContentHolder(View.inflate(parent.getContext(), R.layout.activity_main_adt, null));
        }

        @Override
        public void onBindViewHolder(@NonNull ContentHolder holder, int position) {
            String item = mDataList.get(position);
            holder.textName.setText(item);
            if (position % 2 == 0) {
                holder.itemView.setBackgroundColor(Color.parseColor("#CCCCCC"));
            } else {
                holder.itemView.setBackgroundColor(Color.parseColor("#AAAAAA"));
            }

            holder.textName.setOnClickListener(v -> {
                String className = mStringArrayMap.get(mStringList.get(position));
                if (className != null && !className.isEmpty()) {
                    Intent intent = new Intent();
                    intent.setClassName(getPackageName(), className);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mDataList == null ? 0 : mDataList.size();
        }
    }

    private final static class ContentHolder extends RecyclerView.ViewHolder {
        TextView textName;

        ContentHolder(View view) {
            super(view);
            textName = view.findViewById(R.id.view_main_rvt_title);
        }
    }

    private AnimatedVectorDrawableCompat mVectorDrawable;

    public void onPlayAnim(View view) {
        if (mVectorDrawable == null || mVectorDrawable.isRunning()) {
            return;
        }
        mVectorDrawable.start();

        XmlSaxParser saxParser = new XmlSaxParser();
        try {
            //saxParser.getChannelList(this, R.raw.xml_temp);
            saxParser.getChannelList(this);
        } catch (Exception e) {
            e.printStackTrace();
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
