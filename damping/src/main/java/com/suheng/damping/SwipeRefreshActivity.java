package com.suheng.damping;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.suheng.damping.view.RecyclerItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class SwipeRefreshActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_refresh);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());//设置Item增加、移除动画
        recyclerView.addItemDecoration(new RecyclerItemDecoration(this, true));//设置Item分隔线
        List<String> datas = new ArrayList<>();
        ContentAdapter adapter = new ContentAdapter(datas);
        recyclerView.setAdapter(adapter);

        for (int i = 0; i < 20; i++) {
            datas.add(String.valueOf(i));
        }
        adapter.notifyDataSetChanged();

        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        //设置圆圈进度条的背景颜色
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(getResources().getColor(android.R.color.holo_blue_dark));
        //设置进度条变化的颜色
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_red_dark,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark);
    }

    @Override
    public void onRefresh() {
        final SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        Toast.makeText(this, "Refreshing", Toast.LENGTH_SHORT).show();
        swipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(SwipeRefreshActivity.this, "Refresh Finish", Toast.LENGTH_SHORT).show();
            }
        }, 3000);
    }

    private static final class ContentAdapter extends RecyclerView.Adapter<ContentHolder> {

        private final List<String> mDataList;

        public ContentAdapter(List<String> dataList) {
            mDataList = dataList;
        }

        @Override
        public ContentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ContentHolder(View.inflate(parent.getContext(), R.layout.recycler_view_adt, null));
        }

        @Override
        public void onBindViewHolder(@NonNull ContentHolder holder, int position) {
            holder.textName.setText(mDataList.get(position));
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
            textName = view.findViewById(R.id.text_bluetooth_name);
        }
    }

}