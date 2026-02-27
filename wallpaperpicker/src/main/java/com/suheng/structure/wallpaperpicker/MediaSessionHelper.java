package com.suheng.structure.wallpaperpicker;

import android.content.ComponentName;
import android.content.Context;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;

public class MediaSessionHelper {
    private static final String TAG = MediaSessionHelper.class.getSimpleName();

    private static final HashSet<Integer> PAUSED_MEDIA_STATES = new HashSet<>();
    private static final HashSet<Integer> CONNECTING_MEDIA_STATES = new HashSet<>();

    static {
        PAUSED_MEDIA_STATES.add(PlaybackState.STATE_NONE);
        PAUSED_MEDIA_STATES.add(PlaybackState.STATE_STOPPED);
        PAUSED_MEDIA_STATES.add(PlaybackState.STATE_PAUSED);
        PAUSED_MEDIA_STATES.add(PlaybackState.STATE_ERROR);
        CONNECTING_MEDIA_STATES.add(PlaybackState.STATE_CONNECTING);
        CONNECTING_MEDIA_STATES.add(PlaybackState.STATE_BUFFERING);
    }

    @NonNull
    private final MediaSessionManager mSessionManager;
    private final List<MediaSessionManager.OnActiveSessionsChangedListener> mOnSessionsChangedListeners = new CopyOnWriteArrayList<>();
    private final List<MediaSessionManager.OnSession2TokensChangedListener> mOnTokensChangedListeners = new CopyOnWriteArrayList<>();
    private final List<MediaSessionManager.OnMediaKeyEventSessionChangedListener> mOnKeyEventChangedListeners = new CopyOnWriteArrayList<>();

    public MediaSessionHelper(@NonNull Context context) {
        mSessionManager = (MediaSessionManager) context.getSystemService(Context.MEDIA_SESSION_SERVICE);
    }

    public @NonNull List<MediaController> getActiveSessions(@Nullable ComponentName notificationListener) {
        return mSessionManager.getActiveSessions(notificationListener);
    }

    public void addOnActiveSessionsChangedListener(@NonNull MediaSessionManager.OnActiveSessionsChangedListener sessionListener
            , @Nullable ComponentName notificationListener) {
        mSessionManager.addOnActiveSessionsChangedListener(sessionListener, notificationListener);
        mOnSessionsChangedListeners.add(sessionListener);
    }

    public void removeOnActiveSessionsChangedListener(@Nullable MediaSessionManager.OnActiveSessionsChangedListener changedListener) {
        if (changedListener == null) {
            for (MediaSessionManager.OnActiveSessionsChangedListener listener : mOnSessionsChangedListeners) {
                mSessionManager.removeOnActiveSessionsChangedListener(listener);
            }
            mOnSessionsChangedListeners.clear();
        } else {
            mOnSessionsChangedListeners.remove(changedListener);
            mSessionManager.removeOnActiveSessionsChangedListener(changedListener);
        }
    }

    public void addOnSession2TokensChangedListener(@NonNull MediaSessionManager.OnSession2TokensChangedListener listener) {
        mSessionManager.addOnSession2TokensChangedListener(listener);
        mOnTokensChangedListeners.add(listener);
    }

    public void removeOnSession2TokensChangedListener(@Nullable MediaSessionManager.OnSession2TokensChangedListener changedListener) {
        if (changedListener == null) {
            for (MediaSessionManager.OnSession2TokensChangedListener listener : mOnTokensChangedListeners) {
                mSessionManager.removeOnSession2TokensChangedListener(listener);
            }
            mOnTokensChangedListeners.clear();
        } else {
            mOnTokensChangedListeners.remove(changedListener);
            mSessionManager.removeOnSession2TokensChangedListener(changedListener);
        }
    }

    public void addOnMediaKeyEventSessionChangedListener(@NonNull Executor executor
            , @NonNull MediaSessionManager.OnMediaKeyEventSessionChangedListener listener) {
        mSessionManager.addOnMediaKeyEventSessionChangedListener(executor, listener);
        mOnKeyEventChangedListeners.add(listener);
    }

    public void removeOnMediaKeyEventSessionChangedListener(@Nullable MediaSessionManager.OnMediaKeyEventSessionChangedListener changedListener) {
        if (changedListener == null) {
            for (MediaSessionManager.OnMediaKeyEventSessionChangedListener listener : mOnKeyEventChangedListeners) {
                mSessionManager.removeOnMediaKeyEventSessionChangedListener(listener);
            }
            mOnKeyEventChangedListeners.clear();
        } else {
            mOnKeyEventChangedListeners.remove(changedListener);
            mSessionManager.removeOnMediaKeyEventSessionChangedListener(changedListener);
        }
    }

    public boolean existPlayingPlayer() {
        List<MediaController> mediaControllers = getActiveSessions(null);
        for (MediaController controller : mediaControllers) {
            final PlaybackState playbackState = controller.getPlaybackState();
            if (playbackState == null) {
                continue;
            }

            if (isPlayingState(playbackState.getState())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isPlayingState(int state) {
        return !PAUSED_MEDIA_STATES.contains(state) && !CONNECTING_MEDIA_STATES.contains(state);
    }

}
