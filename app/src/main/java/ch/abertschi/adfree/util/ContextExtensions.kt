package ch.abertschi.adfree.util

import android.app.AlarmManager
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.fragment.app.Fragment


fun Context.toast(message: CharSequence) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

@Suppress("unused")
fun Fragment.toast(message: CharSequence) = requireContext().toast(message)

fun Context.toast(resourceId: Int) = Toast.makeText(this, resourceId, Toast.LENGTH_SHORT).show()

@Suppress("unused")
fun Fragment.toast(resourceId: Int) = requireContext().toast(resourceId)

fun Context.longToast(message: CharSequence) =
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()

@Suppress("unused")
fun Context.longToast(resourceId: Int) = Toast.makeText(this, resourceId, Toast.LENGTH_LONG).show()


fun runOnUiThread(action: () -> Unit) {
    Handler(Looper.getMainLooper()).post(action)
}

@Suppress("unused")
fun Fragment.runOnUiThread(action: () -> Unit) {
    runOnUiThread(action)
}

val Context.alarmManager: AlarmManager
    get() = getSystemService(Context.ALARM_SERVICE) as AlarmManager