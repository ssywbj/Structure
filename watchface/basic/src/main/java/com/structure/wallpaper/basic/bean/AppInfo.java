package com.structure.wallpaper.basic.bean;

import android.content.ComponentName;
import android.graphics.drawable.Drawable;

public class AppInfo {
    public String pkg;
    public String label;
    public ComponentName componentName;
    public Drawable drawable;
    public boolean isSelected;

    public AppInfo(String label, String pkg, ComponentName componentName, Drawable drawable) {
        this.label = label;
        this.pkg = pkg;
        this.componentName = componentName;
        this.drawable = drawable;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
