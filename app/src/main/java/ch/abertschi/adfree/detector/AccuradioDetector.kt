package ch.abertschi.adfree.detector

import android.widget.RemoteViews
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.warn
import java.util.Locale

class AccuradioDetector : AdDetectable, AnkoLogger, AbstractNotificationDetector() {

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
                        if (value.toString().trim().toLowerCase(Locale.ROOT).contains("music will resume shortly")) {
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
        // TODO: Support old deprecated fields
        val contentView = payload.statusbarNotification.notification?.contentView
        val bigView = payload.statusbarNotification.notification?.bigContentView
        val tickerView = payload.statusbarNotification.notification?.tickerView

        for (v in listOf(contentView, bigView, tickerView)) {
            if (inspectContentViews(v)) {
                return true
            }
        }
        return false
    }
}