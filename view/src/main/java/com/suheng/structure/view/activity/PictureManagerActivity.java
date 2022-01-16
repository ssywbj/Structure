package com.suheng.structure.view.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.suheng.structure.view.R;
import com.suheng.structure.view.adapter.RecyclerAdapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class PictureManagerActivity extends AppCompatActivity {
    private final List<String> mArraylist = new ArrayList<>();
    private GridLayoutManager mGridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_manager);

        RecyclerView recyclerView = findViewById(R.id.view_picture_manager_recycler_view);
        //recyclerView.setLayoutManager(new HoneycombLayoutManager());
        //recyclerView.setLayoutManager(new MagicManager());

        mGridLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(mGridLayoutManager);
        mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 0) {
                    return 2;
                } else {
                    RecyclerView.Adapter<?> adapter = recyclerView.getAdapter();
                    if (adapter != null && position == adapter.getItemCount() - 1) {
                        return 3;
                    }
                }
                return 1;
            }
        });

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        ContentAdapter adapter = new ContentAdapter(mArraylist, this);
        recyclerView.setAdapter(adapter);
        for (int i = 0; i < 30; i++) {
            mArraylist.add("Text-" + i);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mArraylist.clear();
    }

    private static final class ContentAdapter extends RecyclerAdapter<String, ContentHolder> {
        private final WeakReference<PictureManagerActivity> mWeakReference;

        private ContentAdapter(List<String> dataList, PictureManagerActivity activity) {
            super(dataList);
            mWeakReference = new WeakReference<>(activity);
        }

        @NonNull
        @Override
        public ContentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ContentHolder(parent);
        }

        @Override
        public void onBindViewHolder(@NonNull ContentHolder holder, int pst, String data) {
            holder.mTextView.setText(data);
        }

        @Override
        public void onItemClick(View view, String data, int pst) {
            if (mWeakReference == null || mWeakReference.get() == null) {
                return;
            }

            PictureManagerActivity activity = mWeakReference.get();
            Toast.makeText(activity, String.valueOf(pst), Toast.LENGTH_SHORT).show();
            if (pst == 0) {
                activity.mGridLayoutManager.setSpanCount(4);
            } else if (pst == 1) {
                activity.mGridLayoutManager.setSpanCount(5);
            } else if (pst == 2) {
                activity.mGridLayoutManager.setSpanCount(3);
            }
        }

        @Override
        public boolean onLongClick(View view, String data, int pst) {
            if (mWeakReference == null || mWeakReference.get() == null) {
                return super.onLongClick(view, data, pst);
            }

            PictureManagerActivity activity = mWeakReference.get();
            Toast.makeText(activity, "long click: " + pst, Toast.LENGTH_SHORT).show();
            return true; //true:长按后拦截点击事件，使它不再往下执行；false:长按后不拦截点击事件，它还可以向下执行onClick方法
        }
    }

    private final static class ContentHolder extends RecyclerAdapter.Holder {
        TextView mTextView;

        ContentHolder(ViewGroup viewGroup) {
            super(viewGroup, R.layout.activity_picture_manager_adt);
            mTextView = itemView.findViewById(R.id.view_picture_manager_list_text);

            mIsSetOnClickListener = true;
            mIsSetOnLongClickListener = true;
        }
    }

}
