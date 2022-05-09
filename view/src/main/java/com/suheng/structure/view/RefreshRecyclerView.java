package com.suheng.structure.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;

public class RefreshRecyclerView extends RecyclerView {
    private View mViewHeader;

    public RefreshRecyclerView(@NonNull Context context) {
        super(context);
        this.init();
    }

    public RefreshRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public RefreshRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    private void init() {
        mViewHeader = View.inflate(getContext(), R.layout.damping_layout_refresh_title, null);
    }

    @Override
    public void setAdapter(@Nullable Adapter adapter) {
        adapter = new WrapperAdapter(this, adapter);
        super.setAdapter(adapter);

        LayoutManager layoutManager = getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            int spanCount = gridLayoutManager.getSpanCount();
            /*gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    RecyclerView.Adapter<?> adapter = getAdapter();
                    if (adapter == null) {
                        return 1;
                    }

                    int itemViewType = adapter.getItemViewType(position);
                    return (itemViewType == WrapperAdapter.VIEW_TYPE_HEADER) ? spanCount : 1;
                }
            });*/
        }
    }

    /*@Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
    }*/

    /*private static class WrapperAdapter<VH extends ViewHolder> extends Adapter<VH> {
        private final WeakReference<RefreshRecyclerView> mReference;
        private final Adapter<VH> mSrcAdapter;

        public WrapperAdapter(RefreshRecyclerView recyclerView, Adapter<VH> adapter) {
            mReference = new WeakReference<>(recyclerView);
            mSrcAdapter = adapter;
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return (VH) mSrcAdapter.createViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            //mSrcAdapter.onBindViewHolder(holder, position);
            mSrcAdapter.onBindViewHolder(holder, position);
        }

        @Override
        public int getItemCount() {
            if (mSrcAdapter == null) {
                return 0;
            }

            return mSrcAdapter.getItemCount();
        }
    }*/

    public final static class WrapperAdapter extends Adapter<ViewHolder> {
        public static final int VIEW_TYPE_HEADER = INVALID_TYPE;
        private final WeakReference<RefreshRecyclerView> mReference;
        private final Adapter<ViewHolder> mSrcAdapter;

        public WrapperAdapter(RefreshRecyclerView recyclerView, Adapter<ViewHolder> adapter) {
            mReference = new WeakReference<>(recyclerView);
            mSrcAdapter = adapter;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_HEADER) {
                View viewHeader = mReference.get().mViewHeader;
                //viewHeader.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 0));
                viewHeader.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                return new HeaderHolder(viewHeader);
            }
            return mSrcAdapter.createViewHolder(parent, viewType);
        }

        /*@Override
        public long getItemId(int position) {
            if (position == 0) {
                return NO_ID - 1;
            } else {
                return mSrcAdapter.getItemId(position);
            }
        }*/

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return VIEW_TYPE_HEADER;
            }
            return mSrcAdapter.getItemViewType(position);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            if (holder instanceof HeaderHolder) {
                return;
            }
            mSrcAdapter.onBindViewHolder(holder, position);
        }

        @Override
        public int getItemCount() {
            /*if (mSrcAdapter == null) {
                return 0;
            }*/

            return mSrcAdapter.getItemCount() + 1;
        }
    }

    private static class HeaderHolder extends RecyclerView.ViewHolder {

        public HeaderHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

}
