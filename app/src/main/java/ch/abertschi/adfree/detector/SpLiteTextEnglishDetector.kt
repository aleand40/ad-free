package ch.abertschi.adfree.detector

import org.jetbrains.anko.AnkoLogger
import java.util.Locale

class SpLiteTextEnglishDetector : AdDetectable, AnkoLogger, SpLiteTextDetector() {

    override fun getPackageName() = "com.spotify.lite"

    override fun detectAsAdvertisement(
        payload: AdPayload,
        title: Pair<String?, Boolean>,
        text: Pair<String?, Boolean>,
        subtext: Pair<String?, Boolean>
    ): Boolean {
        if (!title.second) {
            return false
        }
        return title.first != null && title.first!!.trim().toLowerCase(Locale.ROOT).contains("advertisement")
    }

    override fun getMeta(): AdDetectorMeta = AdDetectorMeta(
        "Text detector (English)", "detector for presence of text for spotify lite",
        true,
        category = "Spotify Lite",
        debugOnly = false
    )
}