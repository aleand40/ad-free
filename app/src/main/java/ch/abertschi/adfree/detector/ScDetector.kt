package ch.abertschi.adfree.detector

import android.app.Notification
import org.jetbrains.anko.AnkoLogger
import java.util.Locale

class ScDetector : AdDetectable, AnkoLogger {

    private val keyword: String = "advertisement"
    private val pack = "com.soundcloud.android"

    override fun canHandle(payload: AdPayload): Boolean {
        return payload?.statusbarNotification?.key?.toLowerCase(java.util.Locale.ROOT)?.contains(pack) ?: false
    }

    override fun flagAsAdvertisement(payload: AdPayload): Boolean {
        val extras = payload.statusbarNotification?.notification?.extras
        val title: String? = extras?.getString(Notification.EXTRA_TITLE)?.trim()?.toLowerCase(Locale.ROOT)
        val subTitle: String? = extras?.getString(Notification.EXTRA_SUB_TEXT)

        return title != null && title == keyword
                && subTitle == null
    }

    override fun getMeta(): AdDetectorMeta = AdDetectorMeta(
        "Soundcloud", "experimental detector for soundcloud (english)",
        true,
        category = "Soundcloud",
        debugOnly = false
    )
}