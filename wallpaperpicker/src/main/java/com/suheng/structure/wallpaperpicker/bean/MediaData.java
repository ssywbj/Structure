package com.suheng.structure.wallpaperpicker.bean;

import android.graphics.Bitmap;
import android.media.session.MediaController;
import android.media.session.PlaybackState;

public class MediaData {
    public MediaController mediaController;
    public MediaController.TransportControls transportControls;
    public String stateText;
    public int state = PlaybackState.STATE_NONE;
    public long position;

    public String title;
    public String artist;
    public long duration;
    public Bitmap albumArt;

    public int progress;
    public int progressMax;
}
