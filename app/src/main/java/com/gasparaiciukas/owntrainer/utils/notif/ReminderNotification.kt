package com.gasparaiciukas.owntrainer.utils.notif

import android.annotation.SuppressLint
import android.app.Notification.DEFAULT_ALL
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.gasparaiciukas.owntrainer.R
import com.gasparaiciukas.owntrainer.ui.main.MainActivity
import com.gasparaiciukas.owntrainer.utils.Constants
import com.gasparaiciukas.owntrainer.utils.Constants.NOTIFICATION_REMINDER_ID
import com.gasparaiciukas.owntrainer.utils.Constants.NOTIFICATION_REMINDER_TITLE_EXTRA
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class ReminderNotification : BroadcastReceiver() {
    @SuppressLint("UnspecifiedImmutableFlag")
    @Suppress("DEPRECATION")
    // suppressing deprecation of PowerManager.FULL_WAKE_LOCK due to
    // notification not working properly when using alternatives
    override fun onReceive(context: Context, intent: Intent) {
        val contentText = context.getString(
            R.string.daily_reminder,
            intent.getStringExtra(NOTIFICATION_REMINDER_TITLE_EXTRA)
        )

        val contentIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            0
        )
        val notification = NotificationCompat.Builder(
            context,
            context.getString(R.string.notification_reminder_channel_id)
        )
            .setSmallIcon(R.drawable.ic_notification)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(contentText)
            )
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(contentText)
            .setContentIntent(contentIntent)
            .setDefaults(DEFAULT_ALL)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        powerManager.run {
            newWakeLock(
                PowerManager.FULL_WAKE_LOCK or
                    PowerManager.ACQUIRE_CAUSES_WAKEUP or
                    PowerManager.ON_AFTER_RELEASE,
                Constants.REMINDER_WAKE_LOCK_TAG
            ).apply {
                acquire(1000L)
                release()
            }
        }
        manager.notify(NOTIFICATION_REMINDER_ID, notification)
    }
}
