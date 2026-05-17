package ch.abertschi.adfree.detector

import org.jetbrains.anko.AnkoLogger
import java.io.File

class SpotifyNotificationDebugTracer(storageFolder: File?) : AdDetectable, AnkoLogger,
    AbstractDebugTracer(storageFolder) {

    override fun getPackage() = "com.spotify"
    override fun getFileName() = "adfree-spotify.txt"

    override fun getMeta(): AdDetectorMeta = AdDetectorMeta(
        "Spotify tracer",
        "dump spotify notifications to a file. This is for debugging only. ", false,
        category = "Developer",
        debugOnly = true
    )
}
