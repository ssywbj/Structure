package com.suheng.damping;

import android.graphics.Color;
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

import com.suheng.damping.view.DampingLayout;
import com.suheng.damping.view.RecyclerItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class DampingActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity2_damping);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());//设置Item增加、移除动画
        recyclerView.addItemDecoration(new RecyclerItemDecoration(this, true));//设置Item分隔线
        List<String> datas = new ArrayList<>();
        ContentAdapter adapter = new ContentAdapter(datas);
        recyclerView.setAdapter(adapter);

        for (int i = 0; i < 10; i++) {
            datas.add(String.valueOf(i));
        }
        adapter.notifyDataSetChanged();

        DampingLayout dampingView = findViewById(R.id.damping_view);
        dampingView.setTextColor(Color.BLUE);
        dampingView.setProgressColor(Color.BLUE);
        dampingView.setOnRefreshListener(() -> {
            Toast.makeText(DampingActivity2.this, "Refreshing", Toast.LENGTH_SHORT).show();

            dampingView.postDelayed(() -> {
                Toast.makeText(DampingActivity2.this, "Refresh Finish", Toast.LENGTH_SHORT).show();
                dampingView.setRefreshing(false);
                int size = datas.size();
                for (int i = size; i < size + 10; i++) {
                    datas.add(String.valueOf(i));
                }
                adapter.notifyDataSetChanged();
            }, 3000);

        });
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