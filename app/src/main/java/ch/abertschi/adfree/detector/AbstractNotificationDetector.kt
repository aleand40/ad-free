package ch.abertschi.adfree.detector

import ch.abertschi.adfree.util.*
import java.util.Locale


abstract class AbstractNotificationDetector : AdDetectable, AppLogger {

    // get package lower case
    abstract fun getPackageName(): String

    override fun canHandle(payload: AdPayload): Boolean {
        return payload.statusbarNotification.key?.lowercase(Locale.ROOT)
            ?.contains(getPackageName())
            ?: false
    }


}

