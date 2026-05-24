package ch.abertschi.adfree.detector

import ch.abertschi.adfree.util.AppLogger
import java.io.File

class ScNotificationDebugTracer(storageFolder: File?) : AdDetectable, AppLogger,
    AbstractDebugTracer(storageFolder) {

    override fun getPackage() = "com.soundcloud.android"
    override fun getFileName() = "adfree-soundcloud.txt"

    override fun getMeta(): AdDetectorMeta = AdDetectorMeta(
        "Soundcloud tracer",
        "dump soundcloud notifications to a file. This is for debugging only and drains more battery",
        false,
        category = "Developer",
        debugOnly = true
    )
}
