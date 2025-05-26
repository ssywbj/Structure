package com.suheng.structure.wallpaperpicker.bean;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.session.MediaController;
import android.media.session.PlaybackState;

import java.util.List;

public class MediaData {
    public MediaController mediaController;
    public MediaController.TransportControls transportControls;

    public String pkg;

    public String label;
    public Drawable icon;

    public String stateText;
    public int state = PlaybackState.STATE_NONE;
    public long position;
    public List<PlaybackState.CustomAction> customActions;
    public boolean existsPrevious;
    public boolean existsNext;

    public String title;
    public String artist;
    public long duration;
    public Bitmap albumArt;

    public int progress;
    public int progressMax;
}
