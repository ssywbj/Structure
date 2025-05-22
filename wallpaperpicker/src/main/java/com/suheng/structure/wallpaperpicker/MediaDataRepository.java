package com.suheng.structure.wallpaperpicker;

import android.content.ComponentName;
import android.content.Context;
import android.media.session.MediaController;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.suheng.structure.wallpaperpicker.bean.MediaData;

import java.util.ArrayList;
import java.util.List;

public class MediaDataRepository {
    public static final String TAG = MediaDataRepository.class.getSimpleName();

    private static volatile MediaDataRepository sInstance;
    private final MediaSessionHelper mMediaSessionHelper;
    private final MediaControllerHelper mControllerHelper;

    private MediaDataRepository(@NonNull Context context) {
        mMediaSessionHelper = new MediaSessionHelper(context);
        mControllerHelper = new MediaControllerHelper(context);
    }

    public static MediaDataRepository getInstance(@NonNull Context context) {
        if (sInstance == null) {
            synchronized (MediaDataRepository.class) {
                if (sInstance == null) {
                    sInstance = new MediaDataRepository(context);
                }
            }
        }
        return sInstance;
    }

    public List<MediaData> getMediaDataList(@Nullable ComponentName notificationListener, @Nullable OnDataChangedListener onDataChangedListener) {
        List<MediaController> mediaControllers = mMediaSessionHelper.getActiveSessions(notificationListener);
        List<MediaData> mediaDataList = new ArrayList<>();
        Log.d(TAG, "getActiveSessions, mediaControllers: " + mediaControllers.size());
        for (MediaController controller : mediaControllers) {
            mediaDataList.add(mControllerHelper.resolveMediaController(controller));
            mControllerHelper.registerCallback(controller, onDataChangedListener);
        }
        return mediaDataList;
    }

    public List<MediaData> getMediaDataList() {
        return getMediaDataList(null, null);
    }

    public void seekTo(MediaController mediaController, long pst) {
        mControllerHelper.seekTo(mediaController, pst);
    }

}
