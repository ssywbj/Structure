package com.suheng.structure.wallpaperpicker.adapter;

import android.app.WallpaperInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.suheng.structure.wallpaperpicker.R;

import java.util.List;

public final class LivePaperAdapter extends RecyclerAdapter<WallpaperInfo, RecyclerView.ViewHolder> {
    public static final String TAG = LivePaperAdapter.class.getSimpleName();

    public LivePaperAdapter(List<WallpaperInfo> dataList) {
        super(dataList);
    }

    @Override
    protected void bindView(RecyclerView.ViewHolder viewHolder, final int position, final WallpaperInfo data) {
        if (viewHolder instanceof ContentHolder) {
            Context ctx = viewHolder.itemView.getContext();
            PackageManager packageManager = ctx.getPackageManager();
            ContentHolder contentHolder = (ContentHolder) viewHolder;

            contentHolder.textName.setText(data.loadLabel(packageManager));
            contentHolder.imageThumb.setImageDrawable(data.loadThumbnail(packageManager));

            final String settingsActivity = data.getSettingsActivity();
            if (settingsActivity == null) {
                contentHolder.textSetting.setVisibility(View.GONE);
                contentHolder.textSetting.setOnClickListener(null);
            } else {
                contentHolder.textSetting.setVisibility(View.VISIBLE);
                contentHolder.textSetting.setOnClickListener(v -> {
                    try {
                        Intent intent = new Intent();
                        intent.setClassName(data.getPackageName(), settingsActivity);
                        ctx.startActivity(intent);
                    } catch (Exception e) {
                        Log.e(TAG, "open " + settingsActivity + " fail", e);
                    }
                });
            }
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ContentHolder(getItemLayout(parent.getContext(), R.layout.wallpaperpick_activity_wallpaper_pick_adt));
    }

    static class ContentHolder extends RecyclerView.ViewHolder {
        TextView textName, textSetting;
        ImageView imageThumb;

        ContentHolder(View view) {
            super(view);
            textName = view.findViewById(R.id.text_wallpaper_name);
            imageThumb = view.findViewById(R.id.image_wallpaper_thumb);
            textSetting = view.findViewById(R.id.text_wallpaper_setting);
        }
    }
}