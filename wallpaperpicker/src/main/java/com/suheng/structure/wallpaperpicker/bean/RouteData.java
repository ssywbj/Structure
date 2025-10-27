package com.suheng.structure.wallpaperpicker.bean;

import android.media.MediaRoute2Info;

import androidx.annotation.NonNull;

public class RouteData {

    private String id;
    private int type;
    private String name;
    private int volume;
    private int volumeMax;
    private int volumeHandling;
    private int connectionState;

    public RouteData() {
    }

    public RouteData(MediaRoute2Info route2Info) {
        id = route2Info.getId();
        updateData(route2Info);
    }

    public void updateData(MediaRoute2Info route2Info) {
        name = route2Info.getName().toString();
        volume = route2Info.getVolume();
        volumeMax = route2Info.getVolumeMax();
        volumeHandling = route2Info.getVolumeHandling();
        connectionState = route2Info.getConnectionState();
        type = route2Info.getType();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getVolume() {
        return volume;
    }

    public int getVolumeMax() {
        return volumeMax;
    }

    public int getVolumeHandling() {
        return volumeHandling;
    }

    public int getConnectionState() {
        return connectionState;
    }

    public int getType() {
        return type;
    }

    @NonNull
    @Override
    public String toString() {
        return "RouteData{" + "id='" + id + '\'' + ", name='" + name + '\'' + ", volume=" + volume +
                ", volumeMax=" + volumeMax + ", volumeHandling=" + volumeHandling +
                ", connectionState=" + connectionState + '}';
    }
}
