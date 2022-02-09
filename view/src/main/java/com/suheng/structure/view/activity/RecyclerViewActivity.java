package com.suheng.structure.view.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.suheng.structure.view.R;
import com.suheng.structure.view.adapter.RecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewActivity extends AppCompatActivity {
    private static final String ATY_PKG_PREFIX = MainActivity.ATY_PKG_PREFIX;
    private final ArrayMap<String, String> mStringArrayMap = new ArrayMap<>();
    private final List<String> mStringList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view2);

        RecyclerView recyclerView = findViewById(R.id.recycler_view_rview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        ContentAdapter adapter = new ContentAdapter(mStringList);

        /*for (int i = 0; i < 7; i++) {
            STRING_LIST.add(String.valueOf(i + 1));
        }
        //LinearLayout中若Top View为TextView时，RecyclerView上移会跑到TextView上面，而若为Button时则会跑到Button
        //下面。目前未知其中原理，只是猜想到Button会主动夺取焦点的原因。
        recyclerView.setTranslationY(-100); //负数：RecyclerView上移，正数下移*/

        mStringList.add("PictureManager");
        mStringArrayMap.put(mStringList.get(mStringList.size() - 1), ATY_PKG_PREFIX + "PictureManagerActivity");
        recyclerView.setAdapter(adapter);

        final ImageView imageView = findViewById(R.id.recycler_view_image_view);
        int[][] states = new int[2][];
        states[0] = new int[]{android.R.attr.state_selected};
        states[1] = new int[]{};
        int[] colors = new int[]{Color.BLUE, Color.BLACK};
        ColorStateList colorStateList = new ColorStateList(states, colors);
        imageView.setImageTintList(colorStateList);
        findViewById(R.id.recycler_view_top).setOnClickListener(v -> imageView.setSelected(true));
        findViewById(R.id.recycler_view_bottom).setOnClickListener(v -> imageView.setSelected(false));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mStringArrayMap.clear();
        mStringList.clear();
    }

    private final class ContentAdapter extends RecyclerAdapter<String, ContentHolder> {

        private ContentAdapter(List<String> dataList) {
            super(dataList);
        }

        @NonNull
        @Override
        public ContentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ContentHolder(parent);
        }

        @Override
        public void onBindViewHolder(@NonNull ContentHolder holder, int pst, String data) {
            holder.textName.setText(data);
            if (pst % 2 == 0) {
                holder.itemView.setBackgroundColor(Color.parseColor("#CCCCCC"));
            } else {
                holder.itemView.setBackgroundColor(Color.parseColor("#AAAAAA"));
            }
        }

        @Override
        public void onItemClick(View view, String data, int pst) {
            String className = mStringArrayMap.get(mStringList.get(pst));
            if (className != null && !className.isEmpty()) {
                Intent intent = new Intent();
                intent.setClassName(getPackageName(), className);
                startActivity(intent);
            }
        }

        @Override
        public boolean onLongClick(View view, String data, int pst) {
            Toast.makeText(RecyclerViewActivity.this, "long click: " + pst, Toast.LENGTH_SHORT).show();
            return true; //true:长按后拦截点击事件，使它不再往下执行；false:长按后不拦截点击事件，它还可以向下执行onClick方法
        }
    }

    private final static class ContentHolder extends RecyclerAdapter.Holder {
        TextView textName;

        ContentHolder(ViewGroup viewGroup) {
            super(viewGroup, R.layout.activity_main_adt);
            textName = itemView.findViewById(R.id.view_main_rvt_title);

            mIsSetOnClickListener = true;
            mIsSetOnLongClickListener = true;
        }
    }

}
