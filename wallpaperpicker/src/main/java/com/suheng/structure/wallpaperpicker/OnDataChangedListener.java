package com.suheng.structure.wallpaperpicker;

import com.suheng.structure.wallpaperpicker.bean.MediaData;

public interface OnDataChangedListener {
    void onDataChanged(MediaData data);

    void onDataAdded(MediaData data);

    void onDataRemoved(MediaData data);
}
