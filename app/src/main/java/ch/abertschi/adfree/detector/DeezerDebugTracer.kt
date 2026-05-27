package ch.abertschi.adfree.detector

import ch.abertschi.adfree.util.AppLogger
import java.io.File

class DeezerDebugTracer(storageFolder: File?) : AdDetectable, AppLogger,

    AbstractDebugTracer(storageFolder) {

    override fun getPackage() = "deezer.android"
    override fun getFileName() = "adfree-deezer.txt"

    override fun getMeta(): AdDetectorMeta = AdDetectorMeta(
        "Deezer tracer",
        "dump deezer notifications to a file. This is for debugging only. ", false,
        category = "Developer",
        debugOnly = true
    )
}
