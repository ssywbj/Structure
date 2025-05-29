package com.suheng.structure.wallpaperpicker.adapter;

import android.content.Context;
import android.media.MediaRoute2Info;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.suheng.structure.wallpaperpicker.MediaRouteRepository;
import com.suheng.structure.wallpaperpicker.R;
import com.suheng.structure.wallpaperpicker.bean.RouteData;

import java.util.List;

public final class MediaRouteAdapter extends RecyclerAdapter<RouteData, RecyclerView.ViewHolder> {

    public MediaRouteAdapter(List<RouteData> dataList) {
        super(dataList);
    }

    @Override
    protected void bindView(RecyclerView.ViewHolder viewHolder, final int position, final RouteData data) {
        if (viewHolder instanceof ContentHolder) {
            Context ctx = viewHolder.itemView.getContext();

            ContentHolder holder = (ContentHolder) viewHolder;
            holder.textName.setText(data.getName());
            String status;
            if (data.getVolumeHandling() == MediaRoute2Info.PLAYBACK_VOLUME_VARIABLE) {
                status = "Using";
                holder.seekBarVolume.setVisibility(View.VISIBLE);
                holder.seekBarVolume.setProgress(data.getVolume());
                holder.seekBarVolume.setMax(data.getVolumeMax());
                holder.seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        MediaRouteRepository.getInstance(ctx).setVolume(data, progress);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });
            } else {
                status = "Idle";
                holder.seekBarVolume.setVisibility(View.GONE);
            }
            String info = "(" + data.getVolume() + ", " + data.getVolumeMax() + ")" + ", " + data.getConnectionState() + ", " + status;
            holder.textInfo.setText(info);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ContentHolder(getItemLayout(parent.getContext(), R.layout.wallpaperpick_activity_route_adt));
    }

    static class ContentHolder extends RecyclerView.ViewHolder {
        TextView textName, textInfo;
        SeekBar seekBarVolume;

        ContentHolder(View view) {
            super(view);
            textName = view.findViewById(R.id.text_route_name);
            textInfo = view.findViewById(R.id.text_route_info);
            seekBarVolume = view.findViewById(R.id.seekBarVolume);
        }
    }

}