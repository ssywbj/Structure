package com.suheng.structure.wallpaperpicker;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.os.Build;
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
    @NonNull
    private final PackageManager mPackageManager;
    @Nullable
    private MediaSessionManager.OnActiveSessionsChangedListener mSessionListener;
    @Nullable
    private MediaSessionManager.OnMediaKeyEventSessionChangedListener mOnKeyEventChangedListener;
    @Nullable
    private MediaSessionManager.OnSession2TokensChangedListener mOnTokensChangedListener;

    private final List<MediaControllerHelper> mControllerHelpers = new ArrayList<>();

    public MediaSessionHelper(@NonNull Context context) {
        mSessionManager = (MediaSessionManager) context.getSystemService(Context.MEDIA_SESSION_SERVICE);
        mPackageManager = context.getPackageManager();
    }

    public @NonNull List<MediaController> getActiveSessions(@Nullable ComponentName notificationListener) {
        return mSessionManager.getActiveSessions(notificationListener);
    }

    public void addOnActiveSessionsChangedListener(@Nullable ComponentName notificationListener) {
        if (mSessionListener == null) {
            mSessionListener = controllers -> {
                if (controllers == null) {
                    Log.w(TAG, "onActiveSessionsChanged, controllers object is null");
                } else {
                    Log.i(TAG, "onActiveSessionsChanged, controllers size is " + controllers.size());
                }
            };
            mSessionManager.addOnActiveSessionsChangedListener(mSessionListener, notificationListener);
        }
    }

    public void addOnSession2TokensChangedListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (mOnTokensChangedListener == null) {
                mOnTokensChangedListener = session2Tokens -> Log.i(TAG, "onSession2TokensChanged, session2Tokens: " + session2Tokens);
                mSessionManager.addOnSession2TokensChangedListener(mOnTokensChangedListener);
            }
        }
    }

    public void addOnMediaKeyEventSessionChangedListener(@NonNull Executor executor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (mOnKeyEventChangedListener == null) {
                mOnKeyEventChangedListener = (pkg, sessionToken) -> Log.i(TAG, "onMediaKeyEventSessionChanged, pkg: " + pkg + ", sessionToken: " + sessionToken);
                mSessionManager.addOnMediaKeyEventSessionChangedListener(executor, mOnKeyEventChangedListener);
            }
        }
    }

    public void removeChangedListeners() {
        Log.d(TAG, "removeChangedListeners()");
        for (MediaControllerHelper controllerHelper : mControllerHelpers) {
            controllerHelper.unregisterCallback();
        }
        if (mSessionListener != null) {
            mSessionManager.removeOnActiveSessionsChangedListener(mSessionListener);
        }
        if (mOnTokensChangedListener != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                mSessionManager.removeOnSession2TokensChangedListener(mOnTokensChangedListener);
            }
        }
        if (mOnKeyEventChangedListener != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                mSessionManager.removeOnMediaKeyEventSessionChangedListener(mOnKeyEventChangedListener);
            }
        }
    }

}
