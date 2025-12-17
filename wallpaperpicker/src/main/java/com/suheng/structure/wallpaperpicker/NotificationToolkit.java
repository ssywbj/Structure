package com.suheng.structure.wallpaperpicker;

import android.app.Notification;
import android.media.session.MediaSession;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.List;

public class NotificationToolkit {

    private static final String TAG = NotificationToolkit.class.getSimpleName();

    private static final List<Class<? extends Notification.Style>> PLATFORM_STYLE_CLASSES = Arrays.asList(
            Notification.BigTextStyle.class, Notification.BigPictureStyle.class, Notification.InboxStyle.class, Notification.MediaStyle.class,
            Notification.DecoratedCustomViewStyle.class, Notification.DecoratedMediaCustomViewStyle.class,
            Notification.MessagingStyle.class, Notification.CallStyle.class);

    private static @Nullable Class<? extends Notification.Style> getNotificationStyleClass(@NonNull String templateClass) {
        for (Class<? extends Notification.Style> innerClass : PLATFORM_STYLE_CLASSES) {
            if (templateClass.equals(innerClass.getName())) {
                return innerClass;
            }
        }
        return null;
    }

    public static @Nullable Class<? extends Notification.Style> getNotificationStyle(@NonNull Bundle extras) {
        String templateClass = extras.getString(Notification.EXTRA_TEMPLATE);
        Log.d(TAG, "templateClass: " + templateClass);
        if (!TextUtils.isEmpty(templateClass)) {
            return getNotificationStyleClass(templateClass);
        }
        return null;
    }

    public static boolean isMediaStyle(@NonNull Bundle extras) {
        final Class<? extends Notification.Style> style = getNotificationStyle(extras);
        final boolean isMediaStyle = Notification.MediaStyle.class.equals(style)
                || Notification.DecoratedMediaCustomViewStyle.class.equals(style);
        Log.i(TAG, "isMediaStyle: " + isMediaStyle);
        return isMediaStyle;
    }

    public static boolean isMediaNotification(@NonNull Bundle extras) {
        return isMediaStyle(extras) && (getSessionToken(extras) != null);
    }

    public static @Nullable MediaSession.Token getSessionToken(@NonNull Bundle extras) {
        MediaSession.Token token = extras.getParcelable(Notification.EXTRA_MEDIA_SESSION, MediaSession.Token.class);
        Log.i(TAG, "media token: " + token);
        return token;
    }
}
