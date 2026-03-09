package com.suheng.structure.wallpaperpicker.adapter;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
            Context context = holder.itemView.getContext();
            MediaDataRepository dataRepository = MediaDataRepository.getInstance(context);

            holder.tvPlayer.setText(data.label);
            Drawable icon = data.icon;
            final int iconWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP
                    , 20f, context.getResources().getDisplayMetrics());
            final int iconHeight = iconWidth * icon.getIntrinsicWidth() / icon.getIntrinsicHeight();
            icon.setBounds(0, 0, iconWidth, iconHeight);
            holder.tvPlayer.setCompoundDrawablesRelative(null, icon, null, null);

            long pst = data.position;
            String fPst = Utils.formatDuration(pst);
            holder.tvPst.setText(fPst + "(" + pst + ")");
            holder.tvTitle.setText(data.title);
            holder.tvArtist.setText(data.artist);
            String duration = Utils.formatDuration(data.duration);
            holder.tvDuration.setText(duration + "(" + data.duration + ")");
            holder.ivAlbumArt.setImageBitmap(data.albumArt);

            if (data.actions != null && !data.actions.isEmpty()) {
                holder.layoutActions.setVisibility(View.VISIBLE);

                final int len = data.actions.size();
                if (holder.layoutActions.getTag() == null) {
                    holder.layoutActions.setTag("Inflate");
                    for (int i = 0; i < len; i++) {
                        createViewAction(context, holder.layoutActions, i);
                    }
                }

                final int countCompareLen = holder.layoutActions.getChildCount();
                if (countCompareLen < len) { //example: 4<6
                    for (int i = countCompareLen; i < len; i++) {
                        createViewAction(context, holder.layoutActions, i);
                    }
                } else if (countCompareLen > len) { //example: 4>2
                    for (int i = len; i < countCompareLen; i++) {
                        holder.layoutActions.removeViewAt(len);
                    }
                }

                final int childCount = holder.layoutActions.getChildCount();
                for (int i = 0; i < childCount && i < len; i++) {
                    View child = holder.layoutActions.getChildAt(i);
                    MediaData.Action action = data.actions.get(i);
                    child.setOnClickListener(v -> dataRepository.actionClick(action));
                    if (child instanceof ViewGroup) {
                        ViewGroup pLayout = (ViewGroup) child;
                        View child0 = pLayout.getChildAt(0);
                        if (child0 instanceof TextView) {
                            TextView tv = (TextView) child0;
                            tv.setText(action.name);
                        }
                        View child1 = pLayout.getChildAt(1);
                        if (child1 instanceof ImageView) {
                            ImageView iv = (ImageView) child1;
                            iv.setImageDrawable(Utils.getIconFromPackage(context, data.pkg, action.icon));
                        }
                    }
                }
            } else {
                holder.layoutActions.setVisibility(View.GONE);
            }

            if (data.notiActions != null && data.notiActions.length > 0) {
                holder.layoutNotiActions.setVisibility(View.VISIBLE);

                final int len = data.notiActions.length;
                if (holder.layoutNotiActions.getTag() == null) {
                    holder.layoutNotiActions.setTag("Inflate");
                    for (int i = 0; i < len; i++) {
                        createViewAction(context, holder.layoutNotiActions, i);
                    }
                }

                final int countCompareLen = holder.layoutNotiActions.getChildCount();
                if (countCompareLen < len) { //example: 4<6
                    for (int i = countCompareLen; i < len; i++) {
                        createViewAction(context, holder.layoutNotiActions, i);
                    }
                } else if (countCompareLen > len) { //example: 4>2
                    for (int i = len; i < countCompareLen; i++) {
                        holder.layoutNotiActions.removeViewAt(len);
                    }
                }

                final int childCount = holder.layoutNotiActions.getChildCount();
                for (int i = 0; i < childCount && i < len; i++) {
                    View child = holder.layoutNotiActions.getChildAt(i);
                    Notification.Action action = data.notiActions[i];
                    child.setOnClickListener(v -> {
                        try {
                            action.actionIntent.send();
                        } catch (PendingIntent.CanceledException e) {
                            e.fillInStackTrace();
                        }
                    });
                    if (child instanceof ViewGroup) {
                        ViewGroup pLayout = (ViewGroup) child;
                        View child0 = pLayout.getChildAt(0);
                        if (child0 instanceof TextView) {
                            TextView tv = (TextView) child0;
                            tv.setText(action.title);
                        }
                        View child1 = pLayout.getChildAt(1);
                        if (child1 instanceof ImageView) {
                            ImageView iv = (ImageView) child1;
                            iv.setImageDrawable(Utils.getIconFromPackage(context, data.pkg, action.getIcon().getResId()));
                        }
                    }
                }
            } else {
                holder.layoutNotiActions.setVisibility(View.GONE);
            }

            holder.seekBar.setEnabled(data.isSeekAvailable);
            holder.seekBar.setProgress(data.progress);
            holder.seekBar.setMax(data.progressMax);
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
                    dataRepository.seekTo(data.pkg, pst);
                }
            });
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = getItemLayout(parent.getContext(), R.layout.wallpaperpick_activity_media_controller_adt);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new ContentHolder(view);
    }

    private void createViewAction(Context context, ViewGroup layout, int index) {
        LinearLayout pLayout = new LinearLayout(context);
        //pLayout.setBackgroundColor(Color.RED);
        pLayout.setPaddingRelative(10, 10, 10, 10);
        pLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 1;
        layout.addView(pLayout, index, layoutParams);

        TextView textView = new TextView(context);
        textView.setPaddingRelative(0, 8, 0, 8);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        textView.setTextColor(Color.CYAN);
        textView.setMaxLines(1);
        textView.setGravity(Gravity.CENTER);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        pLayout.addView(textView);

        ImageView iv = new ImageView(context);
        iv.setImageTintList(ColorStateList.valueOf(Color.WHITE));
        LinearLayout.LayoutParams ivParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                , ViewGroup.LayoutParams.WRAP_CONTENT);
        ivParams.gravity = Gravity.CENTER_HORIZONTAL;
        pLayout.addView(iv, ivParams);
    }

    static class ContentHolder extends RecyclerView.ViewHolder {
        private final Context ctx;
        TextView tvPlayer;
        TextView tvPst;
        TextView tvDuration;
        TextView tvTitle;
        TextView tvArtist;
        ImageView ivAlbumArt;
        SeekBar seekBar;
        LinearLayout layoutActions;
        LinearLayout layoutNotiActions;

        ContentHolder(View view) {
            super(view);
            ctx = view.getContext();

            tvPlayer = view.findViewById(R.id.tv_player);
            tvPst = view.findViewById(R.id.tv_pst);
            tvDuration = view.findViewById(R.id.tv_duration);
            tvTitle = view.findViewById(R.id.tv_title);
            tvArtist = view.findViewById(R.id.tv_artist);
            ivAlbumArt = view.findViewById(R.id.tv_album_art);
            seekBar = view.findViewById(R.id.seekBar);
            layoutActions = view.findViewById(R.id.layout_actions);
            layoutNotiActions = view.findViewById(R.id.layout_noti_actions);

            this.init();
        }

        private void init() {
            DisplayMetrics metrics = ctx.getResources().getDisplayMetrics();
            final float albumArtRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, metrics);
            ivAlbumArt.setClipToOutline(true);
            ivAlbumArt.setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), albumArtRadius);
                }
            });
        }
    }
}
