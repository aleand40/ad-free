package ch.abertschi.adfree.detector

import ch.abertschi.adfree.util.AppLogger
import java.util.Locale

class AccuradioDetector : AdDetectable, AppLogger, AbstractNotificationDetector() {

    override fun getPackageName(): String {
        return "com.slipstream.accuradio"
    }

    override fun getMeta(): AdDetectorMeta = AdDetectorMeta(
        "Accuradio", "notification text based detector for accuradio",
        true,
        category = "Accuradio",
        debugOnly = false
    )

    override fun flagAsAdvertisement(payload: AdPayload): Boolean {
        val notification = payload.statusbarNotification.notification ?: return false

        val extras = notification.extras ?: return false

        val texts = listOf(
            extras.getCharSequence(android.app.Notification.EXTRA_TITLE),
            extras.getCharSequence(android.app.Notification.EXTRA_TEXT),
            extras.getCharSequence(android.app.Notification.EXTRA_SUB_TEXT),
            extras.getCharSequence(android.app.Notification.EXTRA_BIG_TEXT),
            notification.tickerText
        )

        for (text in texts) {
            if (text?.toString()?.trim()?.lowercase(Locale.ROOT)
                    ?.contains("music will resume shortly") == true
            ) {
                return true
            }
        }

        return false
    }
}