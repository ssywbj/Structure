package com.suheng.structure.view.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.media.utils.MediaConstants.SESSION_EXTRAS_KEY_SLOT_RESERVATION_SKIP_TO_NEXT
import androidx.media.utils.MediaConstants.SESSION_EXTRAS_KEY_SLOT_RESERVATION_SKIP_TO_PREV
import com.suheng.structure.view.R
import com.suheng.structure.view.activity.BubbleActivity
import com.suheng.structure.view.activity.ConstraintLayoutActivity
import com.suheng.structure.view.activity.vap.VapMainActivity

object NotificationTools {

    @RequiresApi(Build.VERSION_CODES.O)
    fun groupNotification(context: Context, id: Int) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "test-group"
        val channel = NotificationChannel(
            channelId, "test-group-char", NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)

        val builder = Notification.Builder(context, channelId)
        builder.setSmallIcon(android.R.drawable.stat_notify_more)
        builder.setContentTitle("title_$id")
        notificationManager.notify("TEST_GROUP", id, builder.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun callNotification(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notifyId = 0x111
        val channelId = notifyId.toString()
        //NotificationManager.IMPORTANCE_HIGH:sound notify, NotificationManager.IMPORTANCE_LOW: mute notify
        val channel = NotificationChannel(
            channelId, "test-call-notification", NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)

        //Intent.ACTION_DIAL: phone dialer, Intent.ACTION_CALL:make a phone call
        //dynamic permission request: android.permission.CALL_PHONE
        val intent = Intent(Intent.ACTION_CALL).apply {
            setData(Uri.parse("tel:1008611"))
        }
        val pendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val action = NotificationCompat.Action.Builder(null, "Answer it", pendingIntent).build()

        val builder = NotificationCompat.Builder(context, channelId)
        builder.setSmallIcon(android.R.drawable.sym_call_incoming)
        builder.setContentTitle("Incoming call")
        builder.setContentText("You have a silent incoming call.")
        builder.setCategory(NotificationCompat.CATEGORY_CALL)
        builder.setWhen(System.currentTimeMillis()).addAction(action)

        notificationManager.notify("TEST_CALL_NOTIFICATION", notifyId, builder.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun mediaNotification(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notifyId = 0x222
        val channelId = notifyId.toString()
        //NotificationManager.IMPORTANCE_HIGH:sound notify, NotificationManager.IMPORTANCE_LOW: mute notify
        val channel = NotificationChannel(
            channelId, "test-media-notification", NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)

        val builder = NotificationCompat.Builder(context, channelId)
        builder.setSmallIcon(android.R.drawable.sym_call_missed)
        builder.setContentTitle("Media notification")
        builder.setContentText("You have a media notification.")
        builder.setWhen(System.currentTimeMillis())

        val session = MediaSessionCompat(context, "Wbj")
        val playbackStateBuilder = PlaybackStateCompat.Builder()
        val style = androidx.media.app.NotificationCompat.MediaStyle()

        // For this example, the media is currently paused:
        //val state = PlaybackStateCompat.STATE_PAUSED
        val state = PlaybackStateCompat.STATE_NONE
        val position = 0L
        val playbackSpeed = 1f
        playbackStateBuilder.setState(state, position, playbackSpeed)

        val stateActions = PlaybackStateCompat.ACTION_PLAY or
                PlaybackStateCompat.ACTION_PLAY_PAUSE //or
                //PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                //PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                //PlaybackStateCompat.ACTION_SEEK_TO // adding the seek action enables seeking with the seekbar
        playbackStateBuilder.setActions(stateActions)

        session.setExtras(Bundle().apply {
            putBoolean(SESSION_EXTRAS_KEY_SLOT_RESERVATION_SKIP_TO_PREV, true)
            putBoolean(SESSION_EXTRAS_KEY_SLOT_RESERVATION_SKIP_TO_NEXT, true)
        })

        session.setPlaybackState(playbackStateBuilder.build())
        style.setMediaSession(session.sessionToken)
        builder.setStyle(style)

        notificationManager.notify("TEST_MEDIA_NOTIFICATION", notifyId, builder.build())

        /*val customAction = PlaybackStateCompat.CustomAction.Builder(
            "com.example.MY_CUSTOM_ACTION", // action ID
            "Custom Action", // title - used as content description for the button
            R.drawable.alphabet_uppercase_b
        ).build()
        playbackStateBuilder.addCustomAction(customAction)*/


        /*val callback = object : MediaSessionCompat.Callback() {
            override fun onPlay() {
                // start playback
            }

            override fun onPause() {
                // pause playback
            }

            override fun onSkipToPrevious() {
                // skip to previous
            }

            override fun onSkipToNext() {
                // skip to next
            }

            override fun onSeekTo(pos: Long) {
                // jump to position in track
            }

            override fun onCustomAction(action: String, extras: Bundle?) {
                when (action) {
                    "CUSTOM_ACTION_1" -> "doCustomAction1(extras)"
                    "CUSTOM_ACTION_2" -> "doCustomAction2(extras)"
                    else -> {
                        Log.w("TAG", "Unknown custom action $action")
                    }
                }
            }
        }
        session.setCallback(callback)*/
    }

    //https://github.com/android/socialite
    @RequiresApi(Build.VERSION_CODES.O)
    fun bubbleNotification(context: Context) {
        ContextCompat.startActivity(context, Intent(context, BubbleActivity::class.java), null)
    }

    private var isPlaying = false

    @RequiresApi(Build.VERSION_CODES.O)
    fun mediaNotification2(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notifyId = 0x333
        val channelId = notifyId.toString()
        val channel = NotificationChannel(
            channelId, "test-media-notification", NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)

        val builder = NotificationCompat.Builder(context, channelId)
        // Show controls on lock screen even when user hides sensitive content.
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        builder.setSmallIcon(android.R.drawable.sym_call_outgoing)
        builder.setContentTitle("Media2 notification")
        builder.setContentText("You have a media2 notification.")
        builder.setWhen(System.currentTimeMillis())

        val previousAction = NotificationCompat.Action(
            R.drawable.alphabet_uppercase_b,
            "previous",
            retrievePlaybackAction(context, "actionPrev", BubbleActivity::class.java)
        )
        val actionState = buildStateAction(context, isPlaying.also { isPlaying = !it })
        val actionNext = NotificationCompat.Action(
            R.drawable.alphabet_uppercase_c,
            "Next",
            retrievePlaybackAction(context, "actionNext", ConstraintLayoutActivity::class.java)
        )
        builder.addAction(previousAction).addAction(actionState).addAction(actionNext)

        val mediaStyle = androidx.media.app.NotificationCompat.MediaStyle()
        val session = MediaSessionCompat(context, "Wbj2")
        mediaStyle.setMediaSession(session.sessionToken)
        mediaStyle.setShowActionsInCompactView(1)
        builder.setStyle(mediaStyle)

        notificationManager.notify("TEST_MEDIA2_NOTIFICATION", notifyId, builder.build())
    }

    private fun retrievePlaybackAction(
        context: Context,
        action: String,
        cls: Class<*>
    ): PendingIntent {
        val componentName = ComponentName(context, cls)
        val intent = Intent(action)
        intent.component = componentName
        //return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    private fun buildStateAction(context: Context, isPlaying: Boolean): NotificationCompat.Action {
        val btnResId =
            if (isPlaying) R.drawable.alphabet_uppercase_d else R.drawable.alphabet_uppercase_e
        return NotificationCompat.Action.Builder(
            btnResId,
            if (isPlaying) "Pause" else "Play",
            retrievePlaybackAction(context, "actionPlayPause", VapMainActivity::class.java)
        ).build()
    }

}