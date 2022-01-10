package com.suheng.structure.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public abstract class RecyclerAdapter<T, VH extends RecyclerAdapter.Holder> extends RecyclerView.Adapter<VH> {
    private final List<T> mDataList;

    public RecyclerAdapter(List<T> dataList) {
        mDataList = dataList;
    }

    public VH getNewObject(Class<VH> clazz) throws InstantiationException, IllegalAccessException {
        return clazz.newInstance();
    }

    public VH getNewObject(Constructor<VH> cls, ViewGroup viewGroup) {
        VH vh = null;
        try {
            vh = cls.newInstance(viewGroup);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return vh;
    }

    public Class<VH> getClazz() throws IllegalAccessException, InstantiationException {
        Class<VH> clazz = null;
        Type superclass = getClass().getGenericSuperclass();
        ParameterizedType parameterizedType;
        if (superclass instanceof ParameterizedType) {
            parameterizedType = (ParameterizedType) superclass;
            Type[] typeArray = parameterizedType.getActualTypeArguments();
            if (typeArray.length > 0) {
                clazz = (Class<VH>) typeArray[0];
            }
        }

        return clazz;
    }

    //https://my.oschina.net/superise/blog/681042
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

        public Holder(View view) {
            super(view);
        }

        public Holder(ViewGroup viewGroup, int layoutId) {
            this(LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, viewGroup, false));
        }

        public void setSetOnClickListener(boolean setOnClickListener) {
            mIsSetOnClickListener = setOnClickListener;
        }

        public void setSetOnLongClickListener(boolean setOnLongClickListener) {
            mIsSetOnLongClickListener = setOnLongClickListener;
        }
    }

}
