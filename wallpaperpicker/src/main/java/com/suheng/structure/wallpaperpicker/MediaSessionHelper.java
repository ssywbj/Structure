package com.suheng.structure.wallpaperpicker;

import android.content.ComponentName;
import android.content.Context;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class MediaSessionHelper {
    private static final String TAG = MediaSessionHelper.class.getSimpleName();

    @NonNull
    private final MediaSessionManager mSessionManager;
    @Nullable
    private MediaSessionManager.OnMediaKeyEventSessionChangedListener mOnKeyEventChangedListener;
    @Nullable
    private MediaSessionManager.OnSession2TokensChangedListener mOnTokensChangedListener;

    public MediaSessionHelper(@NonNull Context context) {
        mSessionManager = (MediaSessionManager) context.getSystemService(Context.MEDIA_SESSION_SERVICE);
    }

    public @NonNull List<MediaController> getActiveSessions(@Nullable ComponentName notificationListener) {
        return mSessionManager.getActiveSessions(notificationListener);
    }

    public @NonNull List<MediaController> getActiveSessions() {
        return getActiveSessions(null);
    }

    public void addOnActiveSessionsChangedListener(@NonNull MediaSessionManager.OnActiveSessionsChangedListener sessionListener
            , @Nullable ComponentName notificationListener) {
        mSessionManager.addOnActiveSessionsChangedListener(sessionListener, notificationListener);
    }

    public void addOnSession2TokensChangedListener() {
        if (mOnTokensChangedListener == null) {
            mOnTokensChangedListener = session2Tokens -> Log.i(TAG, "onSession2TokensChanged, session2Tokens: " + session2Tokens);
            mSessionManager.addOnSession2TokensChangedListener(mOnTokensChangedListener);
        }
    }

    public void addOnMediaKeyEventSessionChangedListener(@NonNull Executor executor) {
        if (mOnKeyEventChangedListener == null) {
            mOnKeyEventChangedListener = (pkg, sessionToken) -> Log.i(TAG, "onMediaKeyEventSessionChanged, pkg: " + pkg + ", sessionToken: " + sessionToken);
            mSessionManager.addOnMediaKeyEventSessionChangedListener(executor, mOnKeyEventChangedListener);
        }
    }

    public void removeChangedListeners() {
        Log.d(TAG, "removeChangedListeners()");
        if (mOnTokensChangedListener != null) {
            mSessionManager.removeOnSession2TokensChangedListener(mOnTokensChangedListener);
        }
        if (mOnKeyEventChangedListener != null) {
            mSessionManager.removeOnMediaKeyEventSessionChangedListener(mOnKeyEventChangedListener);
        }
    }

    public boolean existActivePlayer() {
        return !getActiveSessions().isEmpty();
    }

    public boolean existPlayingPlayer(final boolean includeBuffering) {
        List<MediaController> activeSessions = getActiveSessions();
        boolean isPlaying = false;
        for (MediaController controller : activeSessions) {
            final PlaybackState playbackState = controller.getPlaybackState();
            if (playbackState == null) {
                continue;
            }

            final int state = playbackState.getState();
            if (state == PlaybackState.STATE_PLAYING) {
                isPlaying = true;
                break;
            }

            if (includeBuffering && state == PlaybackState.STATE_BUFFERING) {
                isPlaying = true;
                break;
            }
        }

        return isPlaying;
    }

    public boolean existPlayingPlayer() {
        return this.existPlayingPlayer(false);
    }

    public @Nullable List<String> getPlayingPlayers(final boolean includeBuffering) {
        List<MediaController> activeSessions = getActiveSessions();
        List<String> pkgs = null;
        boolean isPlaying;
        for (MediaController controller : activeSessions) {
            final PlaybackState playbackState = controller.getPlaybackState();
            if (playbackState == null) {
                continue;
            }

            isPlaying = false;

            final int state = playbackState.getState();
            if (state == PlaybackState.STATE_PLAYING) {
                isPlaying = true;
            }

            if (includeBuffering && state == PlaybackState.STATE_BUFFERING) {
                isPlaying = true;
            }

            if (isPlaying) {
                if (pkgs == null) {
                    pkgs = new ArrayList<>();
                }
                pkgs.add(controller.getPackageName());
            }
        }

        return pkgs;
    }

    public @Nullable List<String> getPlayingPlayers() {
        return this.getPlayingPlayers(false);
    }

}
