package ch.abertschi.adfree.detector

import android.widget.RemoteViews
import ch.abertschi.adfree.util.AppLogger
import ch.abertschi.adfree.util.warn
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


    private fun extractObject(target: Any, declaredField: String): Any? {
        return try {
            val f = target.javaClass.getDeclaredField(declaredField) //NoSuchFieldException
            f.isAccessible = true
            return f.get(target)
        } catch (e: Exception) {
            warn("Can not access $declaredField with reflection, $e")
            null
        }
    }


    @Suppress("unused")
    private fun inspectContentViews(contentView: RemoteViews?): Boolean {
        try {
            if (contentView != null) {
                val actions = extractObject(contentView, "mActions") as List<*>?
                if (actions != null) {
                    for (a in actions) {
                        if (a == null) {
                            continue
                        }
                        val methodName: Any = extractObject(a, "methodName") ?: continue
                        if (methodName !is CharSequence) {
                            continue
                        }
                        if (methodName != "setText") {
                            continue
                        }
                        val value: Any = extractObject(a, "value") ?: continue
                        if (value !is CharSequence) {
                            continue
                        }
                        if (value.toString().trim().lowercase(Locale.ROOT)
                                .contains("music will resume shortly")
                        ) {
                            return true
                        }
                    }
                }
            }
        } catch (e: Exception) {
            warn(e)
        }
        return false
    }

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