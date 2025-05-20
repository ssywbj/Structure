package com.suheng.structure.wallpaperpicker.adapter;

import android.graphics.Bitmap;
import android.media.MediaMetadata;
import android.media.session.MediaController;
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

import com.suheng.structure.wallpaperpicker.R;
import com.suheng.structure.wallpaperpicker.Utils;

import java.util.List;

public final class MediaControllerAdapter extends RecyclerAdapter<MediaController, RecyclerView.ViewHolder> {
    private static final String TAG = MediaControllerAdapter.class.getSimpleName();

    public MediaControllerAdapter(List<MediaController> dataList) {
        super(dataList);
    }

    @Override
    protected void bindView(RecyclerView.ViewHolder viewHolder, final int position, final MediaController data) {
        if (viewHolder instanceof ContentHolder) {
            ContentHolder holder = (ContentHolder) viewHolder;
            PlaybackState playbackState = data.getPlaybackState();
            if (playbackState == null) {
                return;
            }
            final String playbackStateStr = playbackState.toString();
            final String stateFlag = "state=";
            final int startIndex = playbackStateStr.indexOf(stateFlag);
            final int endIndex = playbackStateStr.indexOf(")");
            final String state = playbackStateStr.substring(startIndex + stateFlag.length(), endIndex + 1);
            holder.mBtnState.setText(state);
            long pst = playbackState.getPosition();
            String fPst = Utils.formatDuration(pst);
            holder.mTvPst.setText(fPst + "(" + pst + ")");
            holder.mSeekBar.setProgress((int) (pst / 1000));
            long actions = playbackState.getActions();
            List<PlaybackState.CustomAction> customActions = playbackState.getCustomActions();
            for (PlaybackState.CustomAction customAction : customActions) {
                CharSequence name = customAction.getName();
                int icon = customAction.getIcon();
                String action = customAction.getAction();
                Log.d(TAG, "customAction, name: " + name + ", icon: " + icon
                        + ", action: " + action + ", actions: " + actions);
            }

            MediaMetadata metadata = data.getMetadata();
            if (metadata == null) {
                return;
            }
            CharSequence text = metadata.getText(MediaMetadata.METADATA_KEY_TITLE);
            holder.mTvTitle.setText(text);
            CharSequence artist = metadata.getText(MediaMetadata.METADATA_KEY_ARTIST);
            holder.mTvArtist.setText(artist);
            long duration = metadata.getLong(MediaMetadata.METADATA_KEY_DURATION);
            String fDuration = Utils.formatDuration(duration);
            holder.mTvDuration.setText(fDuration + "(" + duration + ")");
            holder.mTvDuration.setTag(duration);
            holder.mSeekBar.setMax((int) (duration / 1000));
            Bitmap bitmap = metadata.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART);
            holder.mIvAlbumArt.setImageBitmap(bitmap);
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
