package com.suheng.structure.wallpaperpicker;

import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media.MediaBrowserServiceCompat;

import java.util.List;

//https://developer.android.com/media/legacy/audio/mediabrowserservice?hl=zh-cn#java
//https://github.com/android/uamp.git
//public class MediaPlayService extends MediaBrowserService {
public class MediaPlayService extends MediaBrowserServiceCompat {
    public static final String TAG = MediaPlayService.class.getSimpleName();

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        Log.i(TAG, "onGetRoot: " + clientPackageName + ", " + clientUid);
        return null;
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        Log.i(TAG, "onLoadChildren: " + parentId);
    }

}
