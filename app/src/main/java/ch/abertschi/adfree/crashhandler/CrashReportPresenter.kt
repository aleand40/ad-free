package ch.abertschi.adfree.crashhandler

import android.content.Context
import ch.abertschi.adfree.util.AppLogger
import ch.abertschi.adfree.util.info
import ch.abertschi.adfree.util.warn
import java.io.File


class CrashReportPresenter(
    private val view: CrashReportView,
    private val context: Context
) : AppLogger {

    fun onSendCrashReportClicked(logfile: String?, summary: String?) {
        info { "clicking view for crashReport" }

        val safeSummary = summary ?: ""

        if (logfile == null) {
            view.showError("No crash report available.")
            return
        }

        try {
            val file = File(context.filesDir, logfile)
            val log = file.readText()
            info { "sending report with $file $log" }

            val fullMessage = "--- Crash Summary ---\n$safeSummary\n\n--- Logcat Output ---\n$log"

            view.launchEmailClient(fullMessage)

        } catch (e: Exception) {
            warn { "cant send crash report" }
            warn { e }
            e.printStackTrace()

            view.launchEmailClient(safeSummary.ifEmpty { "No summary available" })
        }
    }
}