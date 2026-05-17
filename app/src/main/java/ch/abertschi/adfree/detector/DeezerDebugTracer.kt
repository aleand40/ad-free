package ch.abertschi.adfree.detector
import org.jetbrains.anko.AnkoLogger
import java.io.File

class DeezerDebugTracer(storageFolder: File?) : AdDetectable, AnkoLogger,

    AbstractDebugTracer(storageFolder) {

    val packageName = "deezer.android"
    val fileName = "adfree-deezer.txt"

    override fun getPackage() = packageName
    override fun getFileName() = fileName

    override fun getMeta(): AdDetectorMeta = AdDetectorMeta(
        "Deezer tracer",
        "dump deezer notifications to a file. This is for debugging only. ", false,
        category = "Developer",
        debugOnly = true
    )
}
