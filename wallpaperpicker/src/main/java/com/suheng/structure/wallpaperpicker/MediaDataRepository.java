package com.suheng.structure.wallpaperpicker;

import android.content.ComponentName;
import android.content.Context;
import android.media.session.MediaController;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.suheng.structure.wallpaperpicker.bean.MediaData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MediaDataRepository {
    public static final String TAG = MediaDataRepository.class.getSimpleName();

    private static volatile MediaDataRepository sInstance;
    private final MediaSessionHelper mMediaSessionHelper;
    private final MediaControllerHelper mControllerHelper;
    private final List<String> mPkgList = new ArrayList<>();

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
            if (onDataChangedListener != null) {
                mControllerHelper.registerCallback(controller, onDataChangedListener);
            }
            mPkgList.add(controller.getPackageName());
        }
        return mediaDataList;
    }

    public void addOnActiveSessionsChangedListener(@Nullable OnDataChangedListener onDataChangedListener) {
        mMediaSessionHelper.addOnActiveSessionsChangedListener(controllers -> {
            if (controllers == null) {
                Log.w(TAG, "onActiveSessionsChanged, controllers object is null");
            } else {
                final int controllerSize = controllers.size();
                Log.i(TAG, "onActiveSessionsChanged, controllers size is " + controllerSize
                        + ", pkg size: " + mPkgList.size());
                for (MediaController controller : controllers) {
                    String packageName = controller.getPackageName();
                    if (mPkgList.contains(packageName)) {
                        continue;
                    }

                    Log.i(TAG, "onActiveSessionsChanged, add player: " + packageName);
                    mPkgList.add(packageName);
                    if (onDataChangedListener != null) {
                        MediaData mediaData = mControllerHelper.resolveMediaController(controller);
                        mControllerHelper.registerCallback(controller, onDataChangedListener);
                        onDataChangedListener.onDataAdded(mediaData);
                    }
                }

                if (controllerSize == 0) {
                    Log.i(TAG, "onActiveSessionsChanged, remove all players");
                    mPkgList.clear();
                    mControllerHelper.unregisterCallbacks();
                    if (onDataChangedListener != null) {
                        List<MediaData> cacheMediaData = mControllerHelper.getCacheMediaData();
                        if (cacheMediaData != null) {
                            for (MediaData mediaData : cacheMediaData) {
                                onDataChangedListener.onDataRemoved(mediaData);
                            }
                        }
                    }
                } else {
                    Iterator<String> iterator = mPkgList.iterator();
                    while (iterator.hasNext()) {
                        final String pkg = iterator.next(); //0,1,2
                        MediaController mediaController = null;
                        for (MediaController controller : controllers) { //0, 1
                            if (pkg.equals(controller.getPackageName())) {
                                mediaController = null;
                                break;
                            }
                            mediaController = controller;
                        }

                        if (mediaController != null) {
                            Log.i(TAG, "onActiveSessionsChanged, remove player: " + pkg);
                            iterator.remove();
                            mControllerHelper.unregisterCallback(mediaController);
                            if (onDataChangedListener != null) {
                                MediaData mediaData = mControllerHelper.getCacheMediaData(mediaController);
                                onDataChangedListener.onDataRemoved(mediaData);
                            }
                        }
                    }
                }

            }
        }, null);
    }

    public List<MediaData> getMediaDataList() {
        return getMediaDataList(null, null);
    }

    public void seekTo(MediaController mediaController, long pst) {
        mControllerHelper.seekTo(mediaController, pst);
    }

    public void removePlayer(String pkg) {
        mPkgList.remove(pkg);
    }
}
