package com.suheng.structure.wallpaperpicker;

import android.content.ComponentName;
import android.content.Context;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;

public class MediaSessionHelper {
    private static final String TAG = MediaSessionHelper.class.getSimpleName();

    @NonNull
    private final MediaSessionManager mSessionManager;
    @Nullable
    private List<MediaSessionManager.OnActiveSessionsChangedListener> mOnSessionsChangedListener;
    @Nullable
    private List<MediaSessionManager.OnSession2TokensChangedListener> mOnTokensChangedListener;
    @Nullable
    private List<MediaSessionManager.OnMediaKeyEventSessionChangedListener> mOnKeyEventChangedListener;

    public MediaSessionHelper(@NonNull Context context) {
        mSessionManager = (MediaSessionManager) context.getSystemService(Context.MEDIA_SESSION_SERVICE);
    }

    public @NonNull List<MediaController> getActiveSessions(@Nullable ComponentName notificationListener) {
        return mSessionManager.getActiveSessions(notificationListener);
    }

    public void addOnActiveSessionsChangedListener(@NonNull MediaSessionManager.OnActiveSessionsChangedListener sessionListener
            , @Nullable ComponentName notificationListener) {
        mSessionManager.addOnActiveSessionsChangedListener(sessionListener, notificationListener);
        if (mOnSessionsChangedListener == null) {
            mOnSessionsChangedListener = new ArrayList<>();
        }
        mOnSessionsChangedListener.add(sessionListener);
    }

    public void addOnSession2TokensChangedListener(@NonNull MediaSessionManager.OnSession2TokensChangedListener listener) {
        mSessionManager.addOnSession2TokensChangedListener(listener);
        if (mOnTokensChangedListener == null) {
            mOnTokensChangedListener = new ArrayList<>();
        }
        mOnTokensChangedListener.add(listener);
    }

    public void addOnMediaKeyEventSessionChangedListener(@NonNull Executor executor
            , @NonNull MediaSessionManager.OnMediaKeyEventSessionChangedListener listener) {
        mSessionManager.addOnMediaKeyEventSessionChangedListener(executor, listener);
        if (mOnKeyEventChangedListener == null) {
            mOnKeyEventChangedListener = new ArrayList<>();
        }
        mOnKeyEventChangedListener.add(listener);
    }

    public void removeChangedListeners() {
        Log.d(TAG, "removeChangedListeners()");
        if (mOnSessionsChangedListener != null) {
            Iterator<MediaSessionManager.OnActiveSessionsChangedListener> iterator = mOnSessionsChangedListener.iterator();
            while (iterator.hasNext()) {
                mSessionManager.removeOnActiveSessionsChangedListener(iterator.next());
                iterator.remove();
            }
        }
        if (mOnTokensChangedListener != null) {
            Iterator<MediaSessionManager.OnSession2TokensChangedListener> iterator = mOnTokensChangedListener.iterator();
            while (iterator.hasNext()) {
                mSessionManager.removeOnSession2TokensChangedListener(iterator.next());
                iterator.remove();
            }
        }
        if (mOnKeyEventChangedListener != null) {
            Iterator<MediaSessionManager.OnMediaKeyEventSessionChangedListener> iterator = mOnKeyEventChangedListener.iterator();
            while (iterator.hasNext()) {
                mSessionManager.removeOnMediaKeyEventSessionChangedListener(iterator.next());
                iterator.remove();
            }
        }
    }

}
