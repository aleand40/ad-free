package ch.abertschi.adfree.crashhandler

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Build.MANUFACTURER
import android.os.Build.MODEL
import android.util.Log
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess

/**
 * Capture app crashes and launch Activity to report error
 * @author abertschi
 */
class CrashExceptionHandler(val context: Context) : Thread.UncaughtExceptionHandler {

    override fun uncaughtException(t: Thread, e: Throwable) {
        e.printStackTrace() // Not all Android versions print the stack trace automatically

        val (summary, logcat) = generateReport(e)
        val filename = writeLogfile(logcat)

                val i = Intent(context, SendCrashReportActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        i.putExtra(SendCrashReportActivity.EXTRA_LOGFILE, filename)
        i.putExtra(SendCrashReportActivity.EXTRA_SUMMARY, summary)
        context.startActivity(i)

        exitProcess(1)
    }

    private fun writeLogfile(logcat: String): String {
        val time = SimpleDateFormat("yyyy-MM-dd-HH:mm:ss", Locale.US).format(Date())
        val filename = "adfree-crashlog-${time}.txt"

        val file = File(context.filesDir, filename)
        file.writeText(logcat)
        return filename
    }

    private fun generateReport(th: Throwable?): Pair<String, String> {
        val manager = context.packageManager
        var info: PackageInfo? = null
        try {
            info = manager.getPackageInfo(context.packageName, 0)
        } catch (_: PackageManager.NameNotFoundException) {
        }

        var model = MODEL
        if (!model.startsWith(MANUFACTURER)) {
            model = "$MANUFACTURER $model"
        }

        val summary = StringBuilder()

        val time = SimpleDateFormat("yyyy-MM-dd-HH:mm:ss", Locale.US).format(Date())

        summary.append("Android version: ").append(Build.VERSION.SDK_INT).append("\n")
        summary.append("Device: ").append(model).append("\n")
        val version = info?.let { androidx.core.content.pm.PackageInfoCompat.getLongVersionCode(it) } ?: "(null)"
        summary.append("App version: ").append(version).append("\n")
        summary.append("Time: ").append(time).append("\n")
        summary.append("Root cause: \n").append(Log.getStackTraceString(th))

        val logcat = StringBuilder()
        logcat.append("Logcat messages: \n").append(th?.message)
        logcat.append(readLogcat())
        return Pair(summary.toString(), logcat.toString())
    }

    private fun readLogcat(): String {
        return try {
            val process = Runtime.getRuntime().exec("logcat -d")
            val bufferedReader = BufferedReader(InputStreamReader(process.inputStream))
            bufferedReader.readText()
        } catch (e: Exception) {
            "Could not read logcat: ${e.message}"
        }
    }
}