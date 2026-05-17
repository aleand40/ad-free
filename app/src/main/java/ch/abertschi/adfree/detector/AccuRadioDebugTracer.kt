package ch.abertschi.adfree.detector

import org.jetbrains.anko.AnkoLogger
import java.io.File

class AccuRadioDebugTracer(storageFolder: File?) : AdDetectable, AnkoLogger,
    AbstractDebugTracer(storageFolder) {

    private val `package` = "com.slipstream.accuradio"
    private val filename = "adfree-accuradio.txt"

    override fun getPackage() = `package`
    override fun getFileName() = filename

    override fun getMeta(): AdDetectorMeta = AdDetectorMeta(
        "Accuradio tracer",
        "dump accuradio notifications to a file. This is for debugging only. ", false,
        category = "Developer",
        debugOnly = true
    )
}