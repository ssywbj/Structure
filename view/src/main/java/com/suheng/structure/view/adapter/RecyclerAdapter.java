package com.suheng.structure.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public abstract class RecyclerAdapter<T, VH extends RecyclerAdapter.Holder> extends RecyclerView.Adapter<VH> {
    private final List<T> mDataList;

    protected RecyclerAdapter(List<T> dataList) {
        mDataList = dataList;
    }

    /*@NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //若能在父类中自动实现对象的创建就更完美了，联想下泛型实例化：https://www.jb51.net/article/190614.htm
        return new RecyclerAdapter.Holder(parent.getContext(), 1);
    }*/

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        T data = mDataList.get(position);
        if (data == null) {
            return;
        }

        this.onBindViewHolder(holder, position, data);

        if (holder.mIsSetOnClickListener) {
            View view = holder.itemView;
            view.setOnClickListener(v -> this.onItemClick(view, data, position));
        }

        if (holder.mIsSetOnLongClickListener) {
            View view = holder.itemView;
            view.setOnLongClickListener(v -> this.onLongClick(view, data, position));
        }
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    public abstract void onBindViewHolder(@NonNull VH holder, int pst, T data);

    public void onItemClick(View view, T data, int pst) {
    }

    public boolean onLongClick(View view, T data, int pst) {
        return false;
    }

    public List<T> getDataList() {
        return mDataList;
    }

    public abstract static class Holder extends RecyclerView.ViewHolder {
        protected boolean mIsSetOnClickListener;
        protected boolean mIsSetOnLongClickListener;

        public Holder(ViewGroup viewGroup, int layoutId) {
            super(LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, viewGroup, false));
        }
    }

}
