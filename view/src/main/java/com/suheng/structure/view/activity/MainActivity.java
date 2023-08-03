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
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
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
        mStringList.add("AnimImageView");
        mStringArrayMap.put(mStringList.get(mStringList.size() - 1), ATY_PKG_PREFIX + "AnimImageViewActivity");
        mStringList.add("ListItemLayout");
        mStringArrayMap.put(mStringList.get(mStringList.size() - 1), ATY_PKG_PREFIX + "ListItemLayoutActivity");
        mStringList.add("StickDot");
        mStringArrayMap.put(mStringList.get(mStringList.size() - 1), ATY_PKG_PREFIX + "StickDotActivity");
        mStringList.add("Shader");
        mStringArrayMap.put(mStringList.get(mStringList.size() - 1), ATY_PKG_PREFIX + "ShaderActivity");
        mStringList.add("ColorFilter");
        mStringArrayMap.put(mStringList.get(mStringList.size() - 1), ATY_PKG_PREFIX + "ColorFilterActivity");
        mStringList.add("GuaguaLe");
        mStringArrayMap.put(mStringList.get(mStringList.size() - 1), ATY_PKG_PREFIX + "GuaguaLeActivity");
        mStringList.add("Bezier");
        mStringArrayMap.put(mStringList.get(mStringList.size() - 1), ATY_PKG_PREFIX + "BezierActivity");
        mStringList.add("FontView");
        mStringArrayMap.put(mStringList.get(mStringList.size() - 1), ATY_PKG_PREFIX + "FontViewActivity");
        mStringList.add("Scroll+VelocityTracker");
        mStringArrayMap.put(mStringList.get(mStringList.size() - 1), ATY_PKG_PREFIX + "ScrollTrackerActivity");
        mStringList.add("InfiniteLine");
        mStringArrayMap.put(mStringList.get(mStringList.size() - 1), ATY_PKG_PREFIX + "InfiniteLineActivity");
        mStringList.add("HexagonProgressBar");
        mStringArrayMap.put(mStringList.get(mStringList.size() - 1), ATY_PKG_PREFIX + "HexagonProgressBarActivity");
        mStringList.add("SVG");
        mStringArrayMap.put(mStringList.get(mStringList.size() - 1), ATY_PKG_PREFIX + "SVGActivity");
        mStringList.add("Drawable");
        mStringArrayMap.put(mStringList.get(mStringList.size() - 1), ATY_PKG_PREFIX + "DrawableActivity");
        mStringList.add("Spring");
        mStringArrayMap.put(mStringList.get(mStringList.size() - 1), ATY_PKG_PREFIX + "SpringAnimActivity");
        mStringList.add("Fling");
        mStringArrayMap.put(mStringList.get(mStringList.size() - 1), ATY_PKG_PREFIX + "FlingAnimActivity");
        mStringList.add("Gesture");
        mStringArrayMap.put(mStringList.get(mStringList.size() - 1), ATY_PKG_PREFIX + "ScrollGestureActivity");
        mStringList.add("Damping");
        mStringArrayMap.put(mStringList.get(mStringList.size() - 1), ATY_PKG_PREFIX + "DampingActivity");
        mStringList.add("Blur");
        mStringArrayMap.put(mStringList.get(mStringList.size() - 1), ATY_PKG_PREFIX + "BlurActivity");
        mStringList.add("CheckedBox");
        mStringArrayMap.put(mStringList.get(mStringList.size() - 1), ATY_PKG_PREFIX + "CheckedActivity");
        mStringList.add("Kotlin Grammar");
        mStringArrayMap.put(mStringList.get(mStringList.size() - 1), ATY_PKG_PREFIX + "KotlinActivity");

        startActivity(new Intent(this, KotlinActivity.class));

        /*AnimationDrawable drawable = (AnimationDrawable) ContextCompat.getDrawable(this, R.drawable.map_my_location_img);
        //drawable.start();
        ImageView imageView = findViewById(R.id.view_image);
        imageView.setImageDrawable(drawable);
        drawable.start();
        Log.d("SVGView", "drawable, NumberOfFrames: " + drawable.getNumberOfFrames() + ", Duration: " + drawable.getDuration(0));*/

        //log的等级(LEVEL)由低到高：V(VERBOSE,2), D(DEBUG,3), I(INFO,4), W(WARN,5), E(ERROR,6), A(ASSERT,7)
        Log.v("wbj_main", "VERBOSE, VERBOSE"); //V
        Log.d("wbj_main", "DEBUG, DEBUG"); //D
        Log.i("wbj_main", "INFO, INFO"); //I
        Log.w("wbj_main", "WARN, WARN"); //W
        Log.e("wbj_main", "ERROR, ERROR"); //E

        int resId = R.anim.layout_animation_fall_down;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this, resId);
        recyclerView.setLayoutAnimation(animation);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //动态开启或关闭：adb shell setprop log.tag.LOG_TAG LEVEL

        String tagLoggable = "WbjLoggable";
        //adb shell setprop log.tag.WbjLoggable V，输出v、d、i、w、e
        Log.v(tagLoggable, "VERBOSE+++");
        if (Log.isLoggable(tagLoggable, Log.VERBOSE)) {
            Log.v(tagLoggable, "VERBOSE---");
        }
        //adb shell setprop log.tag.WbjLoggable D，输出d、i、w、e；以上的"VERBOSE+++"和"VERBOSE---"均不输出，与是否用"Log.isLoggable"判断无关
        Log.d(tagLoggable, "DEBUG+++");
        if (Log.isLoggable(tagLoggable, Log.DEBUG)) {
            Log.d(tagLoggable, "DEBUG---");
        }
        //adb shell setprop log.tag.WbjLoggable I，输出i、w、e；以上的"VERBOSE+++"、"VERBOSE---"、"DEBUG+++"、"DEBUG---"均不输出，与是否用"Log.isLoggable"判断无关
        Log.i(tagLoggable, "INFO+++");
        if (Log.isLoggable(tagLoggable, Log.INFO)) {
            Log.i(tagLoggable, "INFO---");
        }
        //adb shell setprop log.tag.WbjLoggable W，输出w、e；同理，以上的log均不输出，与是否用"Log.isLoggable"判断无关
        Log.w(tagLoggable, "WARN+++");
        if (Log.isLoggable(tagLoggable, Log.WARN)) {
            Log.w(tagLoggable, "WARN---");
        }
        //adb shell setprop log.tag.WbjLoggable E，只输出e；同理，以上的log均不输出，，与是否用"Log.isLoggable"判断无关
        Log.e(tagLoggable, "ERROR+++");
        if (Log.isLoggable(tagLoggable, Log.ERROR)) {
            Log.e(tagLoggable, "ERROR---");
        }

        //adb shell setprop log.tag.WbjLoggable A，关闭全部；同理，以上的log均不输出，，与是否用"Log.isLoggable"判断无关
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
