package com.suheng.structure.wallpaperpicker.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.suheng.structure.wallpaperpicker.R;
import com.suheng.structure.wallpaperpicker.Utils;
import com.suheng.structure.wallpaperpicker.bean.MediaData;

import java.util.List;

public final class MediaControllerAdapter extends RecyclerAdapter<MediaData, RecyclerView.ViewHolder> {
    private static final String TAG = MediaControllerAdapter.class.getSimpleName();

    public MediaControllerAdapter(List<MediaData> dataList) {
        super(dataList);
    }

    @Override
    protected void bindView(RecyclerView.ViewHolder viewHolder, final int position, final MediaData data) {
        if (viewHolder instanceof ContentHolder) {
            ContentHolder holder = (ContentHolder) viewHolder;
            holder.mBtnState.setText(data.state);
            long pst = data.position;
            String fPst = Utils.formatDuration(pst);
            holder.mTvPst.setText(fPst + "(" + pst + ")");
            holder.mSeekBar.setProgress(data.progress);
            holder.mTvTitle.setText(data.title);
            holder.mTvArtist.setText(data.artist);
            long duration = data.duration;
            String fDuration = Utils.formatDuration(duration);
            holder.mTvDuration.setText(fDuration + "(" + duration + ")");
            holder.mSeekBar.setMax(data.progressMax);
            holder.mIvAlbumArt.setImageBitmap(data.albumArt);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ContentHolder(getItemLayout(parent.getContext(), R.layout.wallpaperpick_activity_media_controller_adt));
    }

    static class ContentHolder extends RecyclerView.ViewHolder {
        Button mBtnState;
        TextView mTvPst;
        TextView mTvDuration;
        TextView mTvTitle;
        TextView mTvArtist;
        ImageView mIvAlbumArt;
        SeekBar mSeekBar;

        ContentHolder(View view) {
            super(view);
            mBtnState = view.findViewById(R.id.btn_state);
            //mBtnState.setOnClickListener(onClickListener);
            //findViewById(R.id.btn_previous).setOnClickListener(onClickListener);
            //findViewById(R.id.btn_next).setOnClickListener(onClickListener);
            mTvPst = view.findViewById(R.id.tv_pst);
            mTvDuration = view.findViewById(R.id.tv_duration);
            mTvTitle = view.findViewById(R.id.tv_title);
            mTvArtist = view.findViewById(R.id.tv_artist);
            mIvAlbumArt = view.findViewById(R.id.tv_album_art);
            mSeekBar = view.findViewById(R.id.seekBar);
        }
    }
}
