package com.suheng.structure.wallpaperpicker;

import android.content.Context;
import android.media.session.MediaController;

import androidx.annotation.NonNull;

import java.util.List;

public class MediaDataRepository {
    public static final String TAG = MediaDataRepository.class.getSimpleName();

    private static volatile MediaDataRepository sInstance;
    private final MediaSessionHelper mMediaSessionHelper;

    private MediaDataRepository(@NonNull Context context) {
        mMediaSessionHelper = new MediaSessionHelper(context);
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

    public List<MediaController> getMediaDataList() {
        List<MediaController> activeSessions = mMediaSessionHelper.getActiveSessions(null);
        for (MediaController mediaController : activeSessions) {
            mMediaSessionHelper.resolveMediaController(mediaController);
        }
        return activeSessions;
        //return null;
    }

}
