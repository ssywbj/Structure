package com.suheng.structure.wallpaperpicker.bean;

import android.app.Notification;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.session.PlaybackState;

import androidx.annotation.Nullable;

import java.util.List;

public class MediaData {
    public String pkg;

    public String label;
    public Drawable icon;

    public String stateText;
    public int state = PlaybackState.STATE_NONE;
    public long position;
    public List<Action> actions;

    public String title;
    public String artist;
    public long duration;
    @Nullable
    public Bitmap albumArt;

    public int progress;
    public int progressMax;

    @Nullable
    public Notification.Action[] notiActions;

    public static class Action {
        public boolean isCustom;

        public CharSequence name;
        //public Drawable icon;
        public int icon;
        public Runnable runnable;
    }

}
