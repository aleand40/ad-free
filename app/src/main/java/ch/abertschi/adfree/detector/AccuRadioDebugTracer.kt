package ch.abertschi.adfree.detector

import ch.abertschi.adfree.util.AppLogger
import java.io.File

class AccuRadioDebugTracer(storageFolder: File?) : AdDetectable, AppLogger,
    AbstractDebugTracer(storageFolder) {

    private val packageName = "com.slipstream.accuradio"
    private val fileName = "adfree-accuradio.txt"

    override fun getPackage() = packageName
    override fun getFileName() = fileName

    override fun getMeta(): AdDetectorMeta = AdDetectorMeta(
        "Accuradio tracer",
        "dump accuradio notifications to a file. This is for debugging only. ", false,
        category = "Developer",
        debugOnly = true
    )
}