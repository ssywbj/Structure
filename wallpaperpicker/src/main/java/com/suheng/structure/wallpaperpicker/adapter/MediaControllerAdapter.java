package com.suheng.structure.wallpaperpicker.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.session.PlaybackState;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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
            Context context = holder.itemView.getContext();

            holder.tvPlayer.setText(data.label);
            Drawable icon = data.icon;
            icon.setBounds(0, 0, (int) (icon.getIntrinsicWidth() / 2.5f), (int) (icon.getIntrinsicHeight() / 2.5f));
            holder.tvPlayer.setCompoundDrawablesRelative(null, icon, null, null);

            holder.btnState.setText(data.stateText);
            long pst = data.position;
            String fPst = Utils.formatDuration(pst);
            holder.tvPst.setText(fPst + "(" + pst + ")");
            holder.seekBar.setProgress(data.progress);
            holder.tvTitle.setText(data.title);
            holder.tvArtist.setText(data.artist);
            String duration = Utils.formatDuration(data.duration);
            holder.tvDuration.setText(duration + "(" + data.duration + ")");
            holder.seekBar.setMax(data.progressMax);
            holder.ivAlbumArt.setImageBitmap(data.albumArt);

            List<PlaybackState.CustomAction> customActions = data.customActions;
            if (customActions != null && !customActions.isEmpty()) {
                holder.layoutActions.setVisibility(View.VISIBLE);

                final int len = customActions.size();
                if (holder.layoutActions.getTag() == null) {
                    holder.layoutActions.setTag("Inflate");
                    for (int i = 0; i < len; i++) {
                        createViewAction(context, holder, i);
                    }
                }

                final int countCompareLen = holder.layoutActions.getChildCount();
                if (countCompareLen < len) { //example: 4<6
                    for (int i = countCompareLen; i < len; i++) {
                        createViewAction(context, holder, i);
                    }
                } else if (countCompareLen > len) { //example: 4>2
                    for (int i = len; i < countCompareLen; i++) {
                        holder.layoutActions.removeViewAt(len);
                    }
                }

                final int childCount = holder.layoutActions.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    if (i >= len) {
                        break;
                    }

                    View child = holder.layoutActions.getChildAt(i);
                    if (!(child instanceof TextView)) {
                        break;
                    }

                    PlaybackState.CustomAction customAction = customActions.get(i);
                    TextView textView = (TextView) child;
                    CharSequence actionName = customAction.getName();
                    textView.setText(actionName);
                    Drawable actionIcon = Utils.getIconFromPackage(context, data.pkg, customAction.getIcon());
                    if (actionIcon != null) {
                        final float dimension = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP
                                , 24f, context.getResources().getDisplayMetrics());
                        int intrinsicWidth = (int) dimension;
                        int intrinsicHeight = (int) (dimension * actionIcon.getIntrinsicWidth() / actionIcon.getIntrinsicHeight());
                        actionIcon.setBounds(0, 0, intrinsicWidth, intrinsicHeight);
                        textView.setCompoundDrawables(null, actionIcon, null, null);
                    }

                    textView.setOnClickListener(v -> {
                        Toast.makeText(context, actionName, Toast.LENGTH_SHORT).show();
                        data.transportControls.sendCustomAction(customAction.getAction(), null); //test app: Spotify
                    });
                }
            } else {
                holder.layoutActions.setVisibility(View.GONE);
            }

            holder.btnPrevious.setEnabled(data.existsPrevious);
            if (data.existsPrevious) {
                holder.btnPrevious.setOnClickListener(v -> data.transportControls.skipToPrevious());
            } else {
                holder.btnPrevious.setOnClickListener(null);
            }
            holder.btnNext.setEnabled(data.existsNext);
            if (data.existsNext) {
                holder.btnNext.setOnClickListener(v -> data.transportControls.skipToNext());
            } else {
                holder.btnNext.setOnClickListener(null);
            }
            holder.btnState.setOnClickListener(v -> {
                if (data.state == PlaybackState.STATE_PLAYING) {
                    data.transportControls.pause();
                } else if (data.state == PlaybackState.STATE_PAUSED
                        || data.state == PlaybackState.STATE_NONE) {
                    data.transportControls.play();
                } else {
                    Log.w(TAG, "Neither in play state nor in pause/none state");
                }
            });
            holder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
                    MediaDataRepository.getInstance(context).seekTo(data.mediaController, pst);
                }
            });
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ContentHolder(getItemLayout(parent.getContext(), R.layout.wallpaperpick_activity_media_controller_adt));
    }

    private void createViewAction(Context context, ContentHolder holder, int index) {
        TextView textView = new TextView(context);
        textView.setGravity(Gravity.CENTER);
        textView.setPaddingRelative(10, 6, 10, 6);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        textView.setTextColor(Color.CYAN);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        holder.layoutActions.addView(textView, index, layoutParams);
    }

    static class ContentHolder extends RecyclerView.ViewHolder {
        TextView tvPlayer;
        Button btnState;
        Button btnPrevious;
        Button btnNext;
        TextView tvPst;
        TextView tvDuration;
        TextView tvTitle;
        TextView tvArtist;
        ImageView ivAlbumArt;
        SeekBar seekBar;
        LinearLayout layoutActions;

        ContentHolder(View view) {
            super(view);
            tvPlayer = view.findViewById(R.id.tv_player);
            btnState = view.findViewById(R.id.btn_state);
            btnPrevious = view.findViewById(R.id.btn_previous);
            btnNext = view.findViewById(R.id.btn_next);
            tvPst = view.findViewById(R.id.tv_pst);
            tvDuration = view.findViewById(R.id.tv_duration);
            tvTitle = view.findViewById(R.id.tv_title);
            tvArtist = view.findViewById(R.id.tv_artist);
            ivAlbumArt = view.findViewById(R.id.tv_album_art);
            seekBar = view.findViewById(R.id.seekBar);
            layoutActions = view.findViewById(R.id.layout_actions);
        }
    }
}
