package ch.abertschi.adfree.detector

import android.os.Bundle
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.warn
import java.util.Locale

abstract class AbstractNotificationBundleAndroidTextDetector : AdDetectable, AnkoLogger, AbstractNotificationDetector() {

    open fun extractString(extras: Bundle?, key: String): Pair<String?, Boolean> {
        return try {
            Pair(
                extras?.getCharSequence(key)?.toString()?.trim()?.lowercase(Locale.ROOT),
                true
            )
        } catch (e: Exception) {
            warn { e }
            Pair(null, false)
        }
    }


    override fun flagAsAdvertisement(payload: AdPayload): Boolean {
        val extras = payload.statusbarNotification.notification?.extras
        val title = extractString(extras, "android.title")
        val text = extractString(extras, "android.text")
        val subtext = extractString(extras, "android.subText")
        return detectAsAdvertisement(title, text, subtext)
    }


    abstract fun detectAsAdvertisement(
        title: Pair<String?, Boolean>,
        text: Pair<String?, Boolean>,
        subtext: Pair<String?, Boolean>
    ): Boolean
}