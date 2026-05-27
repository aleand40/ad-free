package ch.abertschi.adfree.detector

import android.service.notification.StatusBarNotification
import ch.abertschi.adfree.util.AppLogger
import ch.abertschi.adfree.util.info
import ch.abertschi.adfree.util.warn
import com.thoughtworks.xstream.XStream
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

abstract class AbstractDebugTracer(val storageFolder: File?) : AdDetectable, AppLogger {

    abstract fun getPackage(): String
    abstract fun getFileName(): String

    override fun canHandle(payload: AdPayload): Boolean {
        if (storageFolder == null) {
            warn { "Given storageFolder is null, cant work. Disabling functionality ..." }
            return false
        }

        if (payload.statusbarNotification.key?.lowercase(Locale.ROOT)
                ?.contains(getPackage()) == true
        ) {
            recordNotification(payload.statusbarNotification)
        }
        return false
    }

    private fun recordNotification(sbn: StatusBarNotification) {
        val file = File(storageFolder, getFileName())
        info { XStream().toXML(sbn) }
        info("writing notification content to $file}")

        val stream = FileOutputStream(file, true)
        try {
            stream.write(XStream().toXML(sbn).toByteArray())
        } catch (e: Exception) {
            info(e)
        } finally {
            stream.close()
        }
    }
}
