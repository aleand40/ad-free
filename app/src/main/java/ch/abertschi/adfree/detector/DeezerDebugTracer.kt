package ch.abertschi.adfree.detector
import org.jetbrains.anko.AnkoLogger
import java.io.File

class DeezerDebugTracer(storageFolder: File?) : AdDetectable, AnkoLogger,

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
