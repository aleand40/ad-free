package ch.abertschi.adfree.detector

import org.jetbrains.anko.AnkoLogger
import java.io.File

class SpotifyLiteDebugTracer(storageFolder: File?) : AdDetectable, AnkoLogger,
    AbstractDebugTracer(storageFolder) {

    private val packageName = "com.spotify.lite"
    private val fileName = "adfree-spotify-lite.txt"

    override fun getPackage() = packageName
    override fun getFileName() = fileName

    override fun getMeta(): AdDetectorMeta = AdDetectorMeta(
        "Spotify Lite tracer",
        "dump spotify lite notifications to a file. This is for debugging only. ", false,
        category = "Developer",
        debugOnly = true
    )
}
