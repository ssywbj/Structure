package com.suheng.wallpaper.roamimg;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.suheng.wallpaper.roamimg.adapter.RecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

public class RoamingWatchFaceConfigActivity extends AppCompatActivity {
    private CityAdapter mCityAdapter;
    private List<SsCity> mSsCities = new ArrayList<>();

    public static final String PREFS_FILE = "file_roaming_watchface_config";
    public static final String PREFS_KEY_GMT = "prefs_key_gmt";
    public static final String PREFS_KEY_CITY = "prefs_key_city";
    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roaming_watch_face_config);
        this.initView();
        this.initData();
    }

    private void initView() {
        mCityAdapter = new CityAdapter(mSsCities);
        mCityAdapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener<SsCity>() {
            @Override
            public void onItemClick(View view, SsCity data, int position) {
                SharedPreferences.Editor edit = mPrefs.edit();
                edit.putString(PREFS_KEY_GMT, data.getGmt());
                String name = data.getName();
                edit.putString(PREFS_KEY_CITY, name.substring(0, name.indexOf(",")).trim());
                edit.apply();

                finish();
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recycler_view_city);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mCityAdapter);
    }

    private void initData() {
        mSsCities.addAll(SsWorldTimeData.getWorldTimeZones(this));
        for (SsCity ssCity : mSsCities) {
            Log.d("Wbj", "timezone city: gmt: " + ssCity.getGmt() + ", name: " + ssCity.getName() + ", id: " + ssCity.getId());
        }
        mCityAdapter.notifyDataSetChanged();

        mPrefs = getSharedPreferences(PREFS_FILE, MODE_PRIVATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SsWorldTimeData.clearSearchList();
        mSsCities.clear();
    }

    private static final class CityAdapter extends RecyclerAdapter<SsCity> {

        CityAdapter(List<SsCity> dataList) {
            super(dataList);
        }

        @Override
        protected void bindView(RecyclerView.ViewHolder viewHolder, final int position, final SsCity data) {
            if (viewHolder instanceof ContentHolder) {
                ContentHolder contentHolder = (ContentHolder) viewHolder;
                contentHolder.textCity.setText(data.getName());
                contentHolder.textSetting.setText(data.getGmt());
            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ContentHolder(getItemLayout(parent.getContext(), R.layout.activity_roaming_watch_face_config_adt));
        }

        static class ContentHolder extends RecyclerView.ViewHolder {
            TextView textCity, textSetting;

            ContentHolder(View view) {
                super(view);
                textCity = view.findViewById(R.id.text_city);
                textSetting = view.findViewById(R.id.text_gmt);
            }
        }
    }
}
