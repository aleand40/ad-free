package ch.abertschi.adfree

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.service.notification.NotificationListenerService
import androidx.core.app.NotificationManagerCompat
import ch.abertschi.adfree.util.AppLogger
import ch.abertschi.adfree.util.info
import ch.abertschi.adfree.util.warn

private const val TIMER_INTERVAL_MS: Long = 60 * 1000

class NotificationStatusManager(val context: Context) : AppLogger {

    private var lastStatus: ListenerStatus = ListenerStatus.UNKNOWN
    private var observers: MutableList<NotificationStatusObserver> = ArrayList()

    fun addObserver(o: NotificationStatusObserver) {
        observers.add(o)
    }

    fun notifyStatusChanged(s: ListenerStatus) {
        info { "Notification Listener Status Changed: $s" }
        lastStatus = s
        observers.forEach { it.onStatusChanged(s) }
    }

    fun getStatus(): ListenerStatus {
        val names = NotificationManagerCompat.getEnabledListenerPackages(context)
        lastStatus = if (names.contains(context.packageName)) {
            ListenerStatus.CONNECTED
        } else {
            ListenerStatus.DISCONNECTED
        }

        info { "Notification Listener Status : $lastStatus" }
        return lastStatus
    }

    fun forceTimedRestart() {
        // TODO: option to remove timer once enabled?
        val serviceIntent = Intent(this.context, NotificationsListeners::class.java)
        val pendingIntent = PendingIntent.getService(
            this.context, 0, serviceIntent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarm = this.context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarm.cancel(pendingIntent)
        alarm.setRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis(),
            TIMER_INTERVAL_MS,
            pendingIntent
        )
        info { "Setting wakeup with alarm manager every $TIMER_INTERVAL_MS ms" }
    }

    fun restartNotificationListener() {
        info { "restarting notification listener" }
        restartComponentService()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val componentName = ComponentName(
                context.applicationContext,
                NotificationsListeners::class.java
            )
            NotificationListenerService.requestRebind(componentName)
        } else {
            warn { "restart notification listener is not supported for current v. of android" }
        }
    }

    private fun restartComponentService() {
        val pm = context.packageManager
        pm.setComponentEnabledSetting(
            ComponentName(
                this.context,
                NotificationsListeners::class.java
            ), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP
        )

        pm.setComponentEnabledSetting(
            ComponentName(
                this.context,
                NotificationsListeners::class.java
            ), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP
        )
    }
}

enum class ListenerStatus {
    CONNECTED, DISCONNECTED, UNKNOWN
}

interface NotificationStatusObserver {
    fun onStatusChanged(status: ListenerStatus)
}