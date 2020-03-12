package com.suheng.photo;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public abstract class RecyclerAdapter<T> extends RecyclerView.Adapter {
    private List<T> mDataList;

    public RecyclerAdapter(List<T> dataList) {
        mDataList = dataList;
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public T getItem(int position) {
        return (mDataList == null || position < 0 || position >= mDataList.size()) ? null : mDataList.get(position);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        T data = this.getItem(position);
        if (data == null || holder == null || holder.itemView == null) {
            return;
        }

        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(holder.itemView, position, getItemId(position));
                }
            });
        }

        if (mOnItemLongClickListener != null) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //false表示继续往下执行(如触发onLongClick后，还会触发onClick)，true表示中断向下的执行
                    return mOnItemLongClickListener.onItemLongClick(holder.itemView, position, getItemId(position));
                }
            });
        }

        this.bindView(holder, position, data);
        //this.bindView(holder, position);
    }

    protected View getItemLayout(Context context, int layoutId) {
        try {
            return View.inflate(context, layoutId, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected abstract void bindView(RecyclerView.ViewHolder viewHolder, int position, T data);

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    private OnItemLongClickListener mOnItemLongClickListener;

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        mOnItemLongClickListener = onItemLongClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position, long id);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(View view, int position, long id);
    }

}
