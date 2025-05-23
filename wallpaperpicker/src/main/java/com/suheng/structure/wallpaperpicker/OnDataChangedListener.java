package com.suheng.structure.wallpaperpicker;

import androidx.annotation.NonNull;

import com.suheng.structure.wallpaperpicker.bean.MediaData;

public interface OnDataChangedListener {
    void onDataChanged(@NonNull MediaData data);

    void onDataAdded(@NonNull MediaData data);

    void onDataRemoved(@NonNull MediaData data);
}
