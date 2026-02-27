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
import java.util.Objects;

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

    public List<MediaData> getMediaDataList(@Nullable OnDataChangedListener onDataChangedListener) {
        return getMediaDataList(null, onDataChangedListener);
    }

    private OnDataChangedListener mOnDataChangedListener;

    public void addOnActiveSessionsChangedListener(@Nullable OnDataChangedListener onDataChangedListener) {
        mOnDataChangedListener = onDataChangedListener;
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
                        onDataChangedListener.onMediaAdded(mediaData);
                    }
                }

                if (controllerSize == 0 && !mPkgList.isEmpty()) {
                    Log.i(TAG, "onActiveSessionsChanged, remove all players");
                    mPkgList.clear();
                    mControllerHelper.unregisterCallbacks();
                    List<MediaData> cacheMediaData = mControllerHelper.getCacheMediaData();
                    if (onDataChangedListener != null && cacheMediaData != null) {
                        for (MediaData mediaData : cacheMediaData) {
                            onDataChangedListener.onMediaRemoved(mediaData);
                        }
                    }
                } else {
                    Iterator<String> iterator = mPkgList.iterator();
                    while (iterator.hasNext()) {
                        final String pkg = iterator.next(); //0,1
                        boolean exclude = true;
                        for (MediaController controller : controllers) { //0
                            if (Objects.equals(pkg, controller.getPackageName())) {
                                exclude = false;
                                break;
                            }
                        }

                        if (exclude) {
                            iterator.remove();
                            MediaController cacheController = mControllerHelper.getCacheController(pkg);
                            if (cacheController != null) {
                                mControllerHelper.unregisterCallback(cacheController);
                                MediaData cacheMediaData = mControllerHelper.getCacheMediaData(cacheController);
                                Log.i(TAG, "onActiveSessionsChanged, remove player: " + pkg + ", cache pkg: "
                                        + cacheController.getPackageName() + ", cacheMediaData: " + cacheMediaData);
                                if (onDataChangedListener != null && cacheMediaData != null) {
                                    onDataChangedListener.onMediaRemoved(cacheMediaData);
                                }
                            }
                        }
                    }
                }

            }
        }, null);
    }

    public void removeOnActiveSessionsChangedListener() {
        mMediaSessionHelper.removeOnActiveSessionsChangedListener(null);
    }

    public void seekTo(String pkg, long pos) {
        mControllerHelper.seekTo(pkg, pos);
    }

    public void actionClick(@NonNull MediaData.Action action) {
        action.runnable.run();
    }

    public boolean existPlayingPlayer() {
        return mMediaSessionHelper.existPlayingPlayer();
    }

    public @Nullable MediaData getCacheMediaData(MediaController mediaController) {
        return mControllerHelper.getCacheMediaData(mediaController);
    }

    public void onMediaUpdated(MediaData mediaData) {
        if (mOnDataChangedListener != null && mediaData != null) {
            mOnDataChangedListener.onMediaUpdated(mediaData);
        }
    }

    public abstract static class OnDataChangedListener {
        abstract void onMediaUpdated(@NonNull MediaData data);

        abstract void onMediaAdded(@NonNull MediaData data);

        abstract void onMediaRemoved(@NonNull MediaData data);
    }
}
