package com.suheng.structure.wallpaperpicker.adapter;

import android.media.MediaRoute2Info;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.suheng.structure.wallpaperpicker.R;

import java.util.List;

public final class MediaRouteAdapter extends RecyclerAdapter<MediaRoute2Info, RecyclerView.ViewHolder> {

    public MediaRouteAdapter(List<MediaRoute2Info> dataList) {
        super(dataList);
    }

    @Override
    protected void bindView(RecyclerView.ViewHolder viewHolder, final int position, final MediaRoute2Info data) {
        if (viewHolder instanceof ContentHolder) {
            ((ContentHolder) viewHolder).textName.setText(data.getName());
            //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            String status = "Idle"; //
            if (data.getVolumeHandling() == MediaRoute2Info.PLAYBACK_VOLUME_VARIABLE) {
                status = "Using";
            }
            String info = "(" + data.getVolume() + ", " + data.getVolumeMax() + ")" + ", " + data.getConnectionState() + ", " + status/* + ", " + data.getSuitabilityStatus() + "," + data.getType()*/;
            ((ContentHolder) viewHolder).textInfo.setText(info);
            //}
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ContentHolder(getItemLayout(parent.getContext(), R.layout.wallpaperpick_activity_route_adt));
    }

    static class ContentHolder extends RecyclerView.ViewHolder {
        TextView textName, textInfo;

        ContentHolder(View view) {
            super(view);
            textName = view.findViewById(R.id.text_route_name);
            textInfo = view.findViewById(R.id.text_route_info);
        }
    }

}