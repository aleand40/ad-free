package ch.abertschi.adfree.util

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import ch.abertschi.adfree.R

class NotificationUtils(val context: Context) : AppLogger {

    companion object {
        const val ACTION_DISMISS = "actionDismiss"
        const val CHANNEL_ID = "ad_channel"
        private val actionDismissCallables: ArrayList<() -> Unit> = ArrayList()
    }

    private val updateNotificationMap: MutableMap<Int, NotificationCompat.Builder> = HashMap()

    init {
        createChannel()
    }

    fun updateTextNotificationIfAvailable(id: Int, title: String? = null, content: String? = null) {
        val builder = updateNotificationMap[id]
        builder?.let {
            if (title != null) it.setContentTitle(title)
            if (content != null) it.setContentText(content)

            val manager = NotificationManagerCompat.from(context)

            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                manager.notify(id, builder.build())
            }
        }
    }

    fun showTextNotification(
        id: Int, title: String, content: String = "",
        dismissCallable: () -> Unit = {},
        priority: Int = NotificationCompat.PRIORITY_DEFAULT, notify: Boolean = true
    ): Notification {

        val dismissIntent = PendingIntent.getBroadcast(
            context, 0,
            Intent(context, NotificationInteractionReceiver::class.java).setAction(ACTION_DISMISS),
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val openAppIntent = PendingIntent.getActivity(
            context, 0,
            Intent(context, ch.abertschi.adfree.view.MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setSmallIcon(R.mipmap.adfree_logo)
            .setPriority(priority)
            .setContentIntent(openAppIntent)
            .setDeleteIntent(dismissIntent)

        builder.setSmallIcon(R.drawable.ic_icon_logo)
        if (content != "") {
            builder.setContentText(content)
        }

        updateNotificationMap[id] = builder
        val notification = builder.build()

        synchronized(actionDismissCallables) {
            actionDismissCallables.add(dismissCallable)
        }

        val manager = NotificationManagerCompat.from(context)
        if (notify) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                manager.notify(id, notification)
            }
        }
        return notification
    }

    fun hideNotification(id: Int) {
        val manager = NotificationManagerCompat.from(context)
        updateNotificationMap.remove(id)
        manager.cancel(id)
    }

    private fun createChannel() {
        val notificationManager = context
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Ad blocking",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = context.getString(R.string.ad_blocking_notification)
            lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        }
        notificationManager.createNotificationChannel(channel)
    }

    class NotificationInteractionReceiver : BroadcastReceiver(), AppLogger {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent == null || intent.action == null) {
                return
            }
            if (intent.action == ACTION_DISMISS) {
                info("Notification dismissed via BroadcastReceiver")
                synchronized(actionDismissCallables) {
                    actionDismissCallables.forEach { it() }
                    actionDismissCallables.clear()
                }
            }
        }
    }
}