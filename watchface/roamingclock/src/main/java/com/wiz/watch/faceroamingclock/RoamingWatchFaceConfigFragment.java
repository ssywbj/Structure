package com.wiz.watch.faceroamingclock;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wiz.watch.faceroamingclock.adapter.RecyclerAdapter;
import com.wiz.watch.faceroamingclock.time.SsCity;
import com.wiz.watch.faceroamingclock.time.SsWorldTimeData;
import com.wiz.watch.faceroamingclock.view.WizScaleScrollbar;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class RoamingWatchFaceConfigFragment extends Fragment {
    private CityAdapter mCityAdapter;
    private final List<SsCity> mSsCities = new ArrayList<>();

    public static final String PREFS_FILE = "file_roaming_watchface_config";
    public static final String PREFS_KEY_GMT = "prefs_key_gmt";
    public static final String PREFS_KEY_CITY_ID = "prefs_key_city_id";
    public static final String ACTION_UPDATE_FACE = "com.wiz.watch.faceroamingclock.action.UPDATE_FACE";
    private SharedPreferences mPrefs;
    private final LoadAsyncTask mLoadAsyncTask = new LoadAsyncTask(this);

    private Activity mActivity;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Context context = getContext();
        return LayoutInflater.from(context).inflate(R.layout.fragment_roaming_watch_face_config, container, false);
        //return inflater.inflate(R.layout.fragment_roaming_watch_face_config, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.initData();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final WizScaleScrollbar scaleScrollbar = view.findViewById(R.id.wiz_scale_scrollbar);
        mCityAdapter = new CityAdapter(mSsCities);
        mCityAdapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener<SsCity>() {
            @Override
            public void onItemClick(View view, final SsCity data, int position) {
                SharedPreferences.Editor edit = mPrefs.edit();
                edit.putString(PREFS_KEY_GMT, data.getGmt());
                edit.putString(PREFS_KEY_CITY_ID, data.getId());
                edit.apply();

                scaleScrollbar.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                    }
                }, 128);

            }
        });

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_city);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mCityAdapter);
        scaleScrollbar.attachToRecyclerView(recyclerView);

        view.findViewById(R.id.title_bar_root).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    private void initData() {
        mPrefs = mActivity.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        mLoadAsyncTask.execute();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SsWorldTimeData.clearSearchList();
        mSsCities.clear();
        mLoadAsyncTask.cancel(true);
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
                contentHolder.textGmt.setText(data.getGmt());
            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ContentHolder(getItemLayout(parent.getContext(), R.layout.fragment_roaming_watch_face_config_adt));
        }

        static class ContentHolder extends RecyclerView.ViewHolder {
            TextView textCity, textGmt;

            ContentHolder(View view) {
                super(view);
                textCity = view.findViewById(R.id.text_city);
                textGmt = view.findViewById(R.id.text_gmt);
            }
        }
    }

    private static class LoadAsyncTask extends AsyncTask<Void, Void, List<SsCity>> {

        private WeakReference<RoamingWatchFaceConfigFragment> mWeakReference;

        public LoadAsyncTask(RoamingWatchFaceConfigFragment activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        protected List<SsCity> doInBackground(Void... voids) {
            return SsWorldTimeData.getWorldTimeZones(mWeakReference.get().getContext());
        }

        @Override
        protected void onPostExecute(List<SsCity> result) {
            super.onPostExecute(result);
            mWeakReference.get().mSsCities.addAll(result);
            mWeakReference.get().mCityAdapter.notifyDataSetChanged();
        }
    }

}
