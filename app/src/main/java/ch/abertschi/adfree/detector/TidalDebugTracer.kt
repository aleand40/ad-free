package ch.abertschi.adfree.detector

import ch.abertschi.adfree.util.AppLogger
import java.io.File

class TidalDebugTracer(storageFolder: File?) : AdDetectable, AppLogger,
    AbstractDebugTracer(storageFolder) {

    private val packageName = "com.aspiro.tidal"
    private val fileName = "adfree-tidal.txt"

    override fun getPackage() = packageName
    override fun getFileName() = fileName

    override fun getMeta(): AdDetectorMeta = AdDetectorMeta(
        "Tidal tracer",
        "dump tidal notifications to a file. This is for debugging only. ", false,
        category = "Developer",
        debugOnly = true
    )
}
