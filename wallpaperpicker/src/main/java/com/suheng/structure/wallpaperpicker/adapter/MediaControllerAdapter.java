package com.suheng.structure.wallpaperpicker.adapter;

import android.media.session.PlaybackState;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.suheng.structure.wallpaperpicker.MediaDataRepository;
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
            holder.mBtnState.setText(data.stateText);
            long pst = data.position;
            String fPst = Utils.formatDuration(pst);
            holder.mTvPst.setText(fPst + "(" + pst + ")");
            holder.mSeekBar.setProgress(data.progress);
            holder.mTvTitle.setText(data.title);
            holder.mTvArtist.setText(data.artist);
            String duration = Utils.formatDuration(data.duration);
            holder.mTvDuration.setText(duration + "(" + data.duration + ")");
            holder.mSeekBar.setMax(data.progressMax);
            holder.mIvAlbumArt.setImageBitmap(data.albumArt);

            holder.mBtnPre.setOnClickListener(v -> data.transportControls.skipToPrevious());
            holder.mBtnNext.setOnClickListener(v -> data.transportControls.skipToNext());
            holder.mBtnState.setOnClickListener(v -> {
                if (data.state == PlaybackState.STATE_PLAYING) {
                    data.transportControls.pause();
                } else if (data.state == PlaybackState.STATE_PAUSED
                        || data.state == PlaybackState.STATE_NONE) {
                    data.transportControls.play();
                } else {
                    Log.w(TAG, "Neither in play state nor in pause/none state");
                }
            });
            holder.mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    final int progress = seekBar.getProgress();
                    final int max = seekBar.getMax();
                    final long pst = (long) (1.0 * progress / max * data.duration);
                    String pstFormat = Utils.formatDuration(pst);
                    Log.i(TAG, "onStopTrackingTouch, progress:" + progress + ", max: " + max + ", seekTo: " + pst + "(" + pstFormat + ")");
                    data.transportControls.seekTo(pst);
                    //data.transportControls.pause();
                    //data.transportControls.play();
                    MediaDataRepository.getInstance(holder.itemView.getContext()).seekTo(data.mediaController, pst);
                }
            });
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ContentHolder(getItemLayout(parent.getContext(), R.layout.wallpaperpick_activity_media_controller_adt));
    }

    static class ContentHolder extends RecyclerView.ViewHolder {
        Button mBtnState;
        Button mBtnPre;
        Button mBtnNext;
        TextView mTvPst;
        TextView mTvDuration;
        TextView mTvTitle;
        TextView mTvArtist;
        ImageView mIvAlbumArt;
        SeekBar mSeekBar;

        ContentHolder(View view) {
            super(view);
            mBtnState = view.findViewById(R.id.btn_state);
            mBtnPre = view.findViewById(R.id.btn_previous);
            mBtnNext = view.findViewById(R.id.btn_next);
            mTvPst = view.findViewById(R.id.tv_pst);
            mTvDuration = view.findViewById(R.id.tv_duration);
            mTvTitle = view.findViewById(R.id.tv_title);
            mTvArtist = view.findViewById(R.id.tv_artist);
            mIvAlbumArt = view.findViewById(R.id.tv_album_art);
            mSeekBar = view.findViewById(R.id.seekBar);
        }
    }
}
