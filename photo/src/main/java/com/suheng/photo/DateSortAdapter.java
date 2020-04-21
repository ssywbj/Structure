package com.suheng.photo;

import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.suheng.photo.model.ImageInfo;

import java.util.List;

public class DateSortAdapter extends PictureAdapter<ImageInfo> {
    private PhotoActivity mActivity;

    public DateSortAdapter(PhotoActivity activity, List<ImageInfo> dataList) {
        super(dataList);
        mActivity = activity;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getItemType();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_CONTENT) {
            return new ContentHolder(getItemLayout(parent.getContext(), R.layout.compress_aty_adapter_content));
        } else if (viewType == VIEW_TYPE_TITLE) {
            return new TitleHolder(getItemLayout(parent.getContext(), R.layout.compress_aty_adapter_title));
        } else {
            return new FooterHolder(getItemLayout(parent.getContext(), R.layout.compress_aty_adapter_footer));
        }
    }

    @Override
    protected void bindView(final RecyclerView.ViewHolder viewHolder, final int position, ImageInfo data) {
        if (mActivity == null) {
            return;
        }
        if (viewHolder instanceof ContentHolder) {
            ContentHolder contentHolder = (ContentHolder) viewHolder;
            Object tag = contentHolder.ivShowImage.getTag(R.id.glide_item_tag_key);
            if (data.getPath() != null && !data.getPath().equals(tag)) {
                contentHolder.ivShowImage.setTag(R.id.glide_item_tag_key, data.getPath());
                Glide.with(mActivity).load(data.getPath())/*.placeholder(R.color.main_menu_frg_bg)
                            .error(R.color.colorPrimaryDark)*/.into(contentHolder.ivShowImage);
            }

            if (mActivity.mIsEditMode) {
                contentHolder.cBox.setVisibility(View.VISIBLE);
                //contentHolder.cBox.setChecked(mActivity.mSelectedList.contains(data));
                contentHolder.cBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewHolder.itemView.performClick();
                    }
                });
            } else {
                contentHolder.cBox.setVisibility(View.GONE);
            }
        } else if (viewHolder instanceof FooterHolder) {
            ((FooterHolder) viewHolder).tvShowNumber.setText(mActivity.getString(R.string.picture_bottom_number, data.getContentLength()));
            viewHolder.itemView.setOnClickListener(null);
        } else if ((viewHolder instanceof TitleHolder)) {
            final TitleHolder titleHolder = (TitleHolder) viewHolder;
            titleHolder.tvTitle.setText(data.getTitle());
            if (mActivity.mIsEditMode) {
                titleHolder.checkBox.setVisibility(View.VISIBLE);
                //titleHolder.checkBox.setChecked(mActivity.mSelectedList.contains(data));
                titleHolder.checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        titleHolder.itemView.performClick();
                    }
                });
            } else {
                titleHolder.checkBox.setVisibility(View.GONE);
            }
        }
    }

    class ContentHolder extends RecyclerView.ViewHolder {
        ImageView ivShowImage;
        CheckBox cBox;

        ContentHolder(View view) {
            super(view);
            ivShowImage = view.findViewById(R.id.image_item);
            if (mActivity != null) {
                ViewGroup.LayoutParams layoutParams = ivShowImage.getLayoutParams();
                layoutParams.width = mActivity.mItemWidth;
                layoutParams.height = mActivity.mItemWidth;
                ivShowImage.setLayoutParams(layoutParams);
            }

            cBox = view.findViewById(R.id.cb_select);
        }
    }

    static class FooterHolder extends RecyclerView.ViewHolder {
        TextView tvShowNumber;

         FooterHolder(View view) {
            super(view);
            tvShowNumber = view.findViewById(R.id.tv_show_number);
        }
    }

    static class TitleHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        CheckBox checkBox;

         TitleHolder(View view) {
            super(view);
            tvTitle = view.findViewById(R.id.tv_title);
            checkBox = view.findViewById(R.id.checkbox);
        }
    }
}
