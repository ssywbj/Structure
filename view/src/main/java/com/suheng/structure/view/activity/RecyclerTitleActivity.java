package com.suheng.structure.view.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.suheng.structure.view.R;
import com.suheng.structure.view.adapter.RecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

public class RecyclerTitleActivity extends AppCompatActivity {
    private final List<ItemBean> mStringList = new ArrayList<>();
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_title);

        mRecyclerView = findViewById(R.id.recycler_title_rview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        ContentAdapter adapter = new ContentAdapter(mStringList);

        ItemBean itemBean = new ItemBean(ItemBean.TYPE_TITLE, "标题1");
        mStringList.add(itemBean);
        mStringList.add(new ItemBean("PictureManager"));
        mStringList.add(new ItemBean("PictureTitle"));
        mStringList.add(new ItemBean("LetterSelect"));
        mStringList.add(new ItemBean("RecyclerView"));
        mStringList.add(new ItemBean("AnimImageView"));

        itemBean = new ItemBean(ItemBean.TYPE_TITLE, "标题2");
        mStringList.add(itemBean);
        mStringList.add(new ItemBean("ListItemLayout"));
        mStringList.add(new ItemBean("StickDot"));
        mStringList.add(new ItemBean("CircleToHeart"));

        itemBean = new ItemBean(ItemBean.TYPE_TITLE, "标题3");
        mStringList.add(itemBean);
        mStringList.add(new ItemBean("ListItemLayout"));
        mStringList.add(new ItemBean("StickDot"));
        mStringList.add(new ItemBean("CircleToHeart"));
        mStringList.add(new ItemBean("PictureManager"));
        mStringList.add(new ItemBean("PictureTitle"));
        mStringList.add(new ItemBean("LetterSelect"));
        mStringList.add(new ItemBean("RecyclerView"));
        mStringList.add(new ItemBean("AnimImageView"));

        mRecyclerView.setAdapter(adapter);

        int resId = R.anim.layout_animation_fall_down;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this, resId);
        mRecyclerView.setLayoutAnimation(animation);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mStringList.clear();
    }

    private final class ContentAdapter extends RecyclerAdapter<ItemBean, RecyclerAdapter.Holder> {

        private ContentAdapter(List<ItemBean> dataList) {
            super(dataList);
        }

        @Override
        public int getItemViewType(int position) {
            return getDataList().get(position).getType();
        }

        @NonNull
        @Override
        public RecyclerAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == ItemBean.TYPE_TITLE) {
                return new TitleHolder(parent);
            } else {
                return new ContentHolder(parent);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerAdapter.Holder holder, int pst, ItemBean data) {
            if (holder instanceof ContentHolder) {
                ContentHolder contentHolder = (ContentHolder) holder;
                contentHolder.textName.setText(data.getContent());

                if (pst % 2 == 0) {
                    holder.itemView.setBackgroundColor(Color.parseColor("#CCCCCC"));
                } else {
                    holder.itemView.setBackgroundColor(Color.parseColor("#AAAAAA"));
                }
            }

            if (holder instanceof TitleHolder) {
                TitleHolder titleHolder = (TitleHolder) holder;
                titleHolder.textTitle.setText(data.getTitle());
            }
        }

        @Override
        public void onItemClick(View view, ItemBean data, int pst) {
            if (data.getType() == ItemBean.TYPE_TITLE) {
                /*int resId = R.anim.layout_animation_fall_down;
                LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(RecyclerTitleActivity.this, resId);
                mRecyclerView.setLayoutAnimation(animation);*/

                mRecyclerView.scheduleLayoutAnimation();
                RecyclerView.Adapter<?> adapter = mRecyclerView.getAdapter();
                if (adapter != null) {
                    adapter.notifyItemRangeChanged(0, adapter.getItemCount());
                }
            }
        }
    }

    private final static class ContentHolder extends RecyclerAdapter.Holder {
        TextView textName;

        ContentHolder(ViewGroup viewGroup) {
            super(viewGroup, R.layout.activity_recycler_title_adt_content);
            textName = itemView.findViewById(R.id.recycler_title_rview_adt_content);

            mIsSetOnClickListener = true;
            mIsSetOnLongClickListener = true;
        }
    }

    private final static class TitleHolder extends RecyclerAdapter.Holder {
        TextView textTitle;

        TitleHolder(ViewGroup viewGroup) {
            super(viewGroup, R.layout.activity_recycler_title_adt_title);
            textTitle = itemView.findViewById(R.id.recycler_title_rview_adt_title);

            mIsSetOnClickListener = true;
            mIsSetOnLongClickListener = true;
        }
    }

    private static final class ItemBean {
        public static final int TYPE_CONTENT = 0;
        public static final int TYPE_TITLE = 1;
        private final int mType;
        private String mTitle;
        private String mContent;

        public ItemBean(int type, String title) {
            mType = type;
            mTitle = title;
        }

        public ItemBean(String content) {
            mType = TYPE_CONTENT;
            mContent = content;
        }

        public int getType() {
            return mType;
        }

        public String getTitle() {
            return mTitle;
        }

        public String getContent() {
            return mContent;
        }
    }
}
