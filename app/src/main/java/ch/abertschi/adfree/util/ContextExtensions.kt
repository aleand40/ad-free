package ch.abertschi.adfree.util

import android.app.AlarmManager
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast


fun Context.toast(message: CharSequence) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

fun Context.toast(resourceId: Int) = Toast.makeText(this, resourceId, Toast.LENGTH_SHORT).show()

fun Context.longToast(message: CharSequence) =
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()

fun runOnUiThread(action: () -> Unit) {
    Handler(Looper.getMainLooper()).post(action)
}

val Context.alarmManager: AlarmManager
    get() = getSystemService(Context.ALARM_SERVICE) as AlarmManager