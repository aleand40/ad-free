package ch.abertschi.adfree.crashhandler


interface CrashReportView {
    fun launchEmailClient(message: String)

    fun showError(message: String)
}