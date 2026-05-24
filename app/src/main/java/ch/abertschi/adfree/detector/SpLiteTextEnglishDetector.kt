package ch.abertschi.adfree.detector

import ch.abertschi.adfree.util.AppLogger
import java.util.Locale

class SpLiteTextEnglishDetector : AdDetectable, AppLogger, SpLiteTextDetector() {

    override fun getPackageName() = "com.spotify.lite"

    override fun detectAsAdvertisement(
        title: Pair<String?, Boolean>,
        text: Pair<String?, Boolean>,
        subtext: Pair<String?, Boolean>
    ): Boolean {
        if (!title.second) {
            return false
        }
        return title.first != null && title.first!!.trim().lowercase(Locale.ROOT)
            .contains("advertisement")
    }

    override fun getMeta(): AdDetectorMeta = AdDetectorMeta(
        "Text detector (English)", "detector for presence of text for spotify lite",
        true,
        category = "Spotify Lite",
        debugOnly = false
    )
}