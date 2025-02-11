package com.suheng.structure.view.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

object NotificationTools {

    @RequiresApi(Build.VERSION_CODES.O)
    fun groupNotification(context: Context, title: String, id: Int) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "test-group"
        val channel = NotificationChannel(
            channelId, "test-group-char", NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)

        val builder = Notification.Builder(context, channelId)
        builder.setSmallIcon(android.R.drawable.stat_notify_more)
        builder.setContentTitle(title)
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

    //https://github.com/android/socialite
    @RequiresApi(Build.VERSION_CODES.O)
    fun bubbleNotification(context: Context) {

    }

}