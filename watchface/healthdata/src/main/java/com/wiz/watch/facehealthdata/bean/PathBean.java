package com.wiz.watch.facehealthdata.bean;

import android.graphics.Path;

public class PathBean {
    public final static int TYPE_DASH_LINE = 0;
    public final static int TYPE_WAVE = 1;
    private Path mPath;
    private int type;

    public PathBean(Path path, int type) {
        mPath = path;
        this.type = type;
    }

    public Path getPath() {
        return mPath;
    }

    public int getType() {
        return type;
    }

    public void setPath(Path path) {
        mPath = path;
    }

    public void setType(int type) {
        this.type = type;
    }
}
