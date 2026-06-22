package ch.abertschi.adfree.model

import android.content.Context
import ch.abertschi.adfree.AdFreeApplication
import ch.abertschi.adfree.detector.AccuRadioDebugTracer
import ch.abertschi.adfree.detector.AccuradioDetector
import ch.abertschi.adfree.detector.AdDetectable
import ch.abertschi.adfree.detector.BestEffortTextDetector
import ch.abertschi.adfree.detector.DeezerDebugTracer
import ch.abertschi.adfree.detector.DeezerTextDetector
import ch.abertschi.adfree.detector.DummyGlobal
import ch.abertschi.adfree.detector.DummySpotifyDetector
import ch.abertschi.adfree.detector.MiuiNotificationDetector
import ch.abertschi.adfree.detector.NotificationActionDetector
import ch.abertschi.adfree.detector.NotificationBundleAndroidTextDetector
import ch.abertschi.adfree.detector.ScDetector
import ch.abertschi.adfree.detector.ScNotificationDebugTracer
import ch.abertschi.adfree.detector.SpLiteTextDetector
import ch.abertschi.adfree.detector.SpLiteTextEnglishDetector
import ch.abertschi.adfree.detector.SpotifyLiteDebugTracer
import ch.abertschi.adfree.detector.SpotifyNotificationDebugTracer
import ch.abertschi.adfree.detector.SpotifyTitleDetector
import ch.abertschi.adfree.detector.TidalDebugTracer
import ch.abertschi.adfree.detector.UserDefinedTextDetector

class AdDetectableFactory(
    var context: Context,
    val prefs: PreferencesFactory
) {

    private var enableMap = HashMap<AdDetectable, Boolean>()

    private var isGloballyEnabled = true

    private var adDetectors: List<AdDetectable> = listOf(
        NotificationActionDetector(),
        SpotifyTitleDetector(TrackRepository(prefs)),
        NotificationBundleAndroidTextDetector(),
        MiuiNotificationDetector(),
        ScDetector(),
        DummyGlobal(),
        DummySpotifyDetector(),
        SpotifyNotificationDebugTracer(context.getExternalFilesDir(null)),
        ScNotificationDebugTracer(context.getExternalFilesDir(null)),
        DeezerDebugTracer(context.getExternalFilesDir(null)),
        DeezerTextDetector(),
        AccuRadioDebugTracer(context.getExternalFilesDir(null)),
        AccuradioDetector(),
        TidalDebugTracer(context.getExternalFilesDir(null)),
        SpotifyLiteDebugTracer(context.getExternalFilesDir(null)),
        UserDefinedTextDetector((context.applicationContext as AdFreeApplication).textRepository),
        SpLiteTextDetector(),
        SpLiteTextEnglishDetector(),
        BestEffortTextDetector()
    )

    init {
        loadMeta()
    }

    private fun loadMeta() {
        isGloballyEnabled = prefs.isBlockingEnabled()
        adDetectors.forEach { enableMap[it] = prefs.isAdDetectableEnabled(it) }
    }

    fun persistMeta() {
        enableMap.entries.forEach { prefs.saveAdDetectableEnable(it.value, it.key) }
    }

    fun isAdfreeEnabled() = isGloballyEnabled

    fun setAdfreeEnabled(e: Boolean) {
        isGloballyEnabled = e
        prefs.setBlockingEnabled(e)
    }

    fun isEnabled(d: AdDetectable): Boolean {
        return enableMap[d] ?: true
    }

    fun setEnable(enable: Boolean, d: AdDetectable) {
        enableMap[d] = enable
    }

    fun getEnabledDetectors() = adDetectors.filter { isEnabled(it) }

    fun getAllDetectors() = adDetectors

    fun getDetectorsForCategory(c: String) =
        getVisibleDetectors().filter { it.getMeta().category == c }

    fun getVisibleDetectors() =
        if (prefs.isDeveloperModeEnabled()) {
            getAllDetectors()
        } else adDetectors.filter { !it.getMeta().debugOnly }

    fun getVisibleCategories() =
        getVisibleDetectors().map { it.getMeta().category }.toHashSet().toList()
}