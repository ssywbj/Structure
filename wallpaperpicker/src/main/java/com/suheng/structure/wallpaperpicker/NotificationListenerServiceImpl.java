package com.suheng.structure.wallpaperpicker;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.content.Intent;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.os.IBinder;
import android.os.UserHandle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.suheng.structure.wallpaperpicker.bean.MediaData;

public class NotificationListenerServiceImpl extends NotificationListenerService {

    public static final String TAG = "NotificationListenerServiceImpl";
    private final int mHashCode = System.identityHashCode(this);

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: " + mHashCode);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: " + mHashCode);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: " + mHashCode);
        return super.onBind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind: " + mHashCode);
        return super.onUnbind(intent);
    }

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        Log.i(TAG, "onListenerConnected");
    }

    @Override
    public void onListenerDisconnected() {
        super.onListenerDisconnected();
        Log.i(TAG, "onListenerDisconnected");
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        Log.i(TAG, "onNotificationPosted, sbn: " + sbn);

        final Notification notification = sbn.getNotification();
        final boolean isMediaStyle = NotificationToolkit.isMediaStyle(notification.extras);
        if (isMediaStyle) {
            final MediaSession.Token mediaToken = NotificationToolkit.getSessionToken(notification.extras);
            MediaData cacheMediaData = null;
            final NotificationListenerServiceImpl context = NotificationListenerServiceImpl.this;
            if (mediaToken != null) {
                final MediaController mediaController = new MediaController(context, mediaToken);
                cacheMediaData = MediaDataRepository.getInstance(context).getCacheMediaData(mediaController);
            }
            final Notification.Action[] actions = notification.actions;
            if (actions == null) {
                Log.w(TAG, "Notification actions is null");
            } else {
                final int length = actions.length;
                String pkg = sbn.getPackageName();
                Log.d(TAG, "pkg: " + pkg + ", actions: " + length);
                if (cacheMediaData != null) {
                    cacheMediaData.notiActions = actions;
                    MediaDataRepository.getInstance(context).onMediaUpdated(cacheMediaData);
                }
                for (Notification.Action action : actions) {
                    Log.d(TAG, "action, title: " + action.title + ", icon: " + action.getIcon()
                            + ", pendingIntent: " + action.actionIntent);
                }
            }
        }
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn, RankingMap rankingMap) {
        super.onNotificationPosted(sbn, rankingMap);
        Log.i(TAG, "onNotificationPosted, rankingMap: " + rankingMap);
    }

    @Override
    public void onNotificationRankingUpdate(RankingMap rankingMap) {
        super.onNotificationRankingUpdate(rankingMap);
        Log.i(TAG, "onNotificationRankingUpdate, rankingMap: " + rankingMap);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn, RankingMap rankingMap, int reason) {
        super.onNotificationRemoved(sbn, rankingMap, reason);
        Log.i(TAG, "onNotificationRemoved, sbn, rankingMap, reason: " + reason);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn, RankingMap rankingMap) {
        super.onNotificationRemoved(sbn, rankingMap);
        Log.i(TAG, "onNotificationRemoved, sbn, rankingMap: " + rankingMap);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
        Log.i(TAG, "onNotificationRemoved, sbn: " + sbn);
    }

    @Override
    public void onSilentStatusBarIconsVisibilityChanged(boolean hideSilentStatusIcons) {
        super.onSilentStatusBarIconsVisibilityChanged(hideSilentStatusIcons);
        Log.d(TAG, "onSilentStatusBarIconsVisibilityChanged, hideSilentStatusIcons: " + hideSilentStatusIcons);
    }

    @Override
    public void onInterruptionFilterChanged(int interruptionFilter) {
        super.onInterruptionFilterChanged(interruptionFilter);
        Log.d(TAG, "onInterruptionFilterChanged, interruptionFilter: " + interruptionFilter);
    }

    @Override
    public void onNotificationChannelModified(String pkg, UserHandle user, NotificationChannel channel, int modificationType) {
        super.onNotificationChannelModified(pkg, user, channel, modificationType);
        Log.d(TAG, "onNotificationChannelModified, pkg: " + pkg + ", user: " + user + "\nchannel: " + channel + ", modificationType: " + modificationType);
    }

    @Override
    public void onNotificationChannelGroupModified(String pkg, UserHandle user, NotificationChannelGroup group, int modificationType) {
        super.onNotificationChannelGroupModified(pkg, user, group, modificationType);
        Log.d(TAG, "onNotificationChannelGroupModified, pkg: " + pkg + ", user: " + user + "\ngroup: " + group + ", modificationType: " + modificationType);
    }

    @Override
    public void onListenerHintsChanged(int hints) {
        super.onListenerHintsChanged(hints);
        Log.d(TAG, "onListenerHintsChanged, hints: " + hints);
    }
}
