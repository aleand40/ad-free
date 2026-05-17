package ch.abertschi.adfree.detector

import org.jetbrains.anko.AnkoLogger
import java.io.File

class TidalDebugTracer(storageFolder: File?) : AdDetectable, AnkoLogger,
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
