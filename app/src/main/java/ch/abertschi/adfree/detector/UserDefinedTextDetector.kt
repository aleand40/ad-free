package ch.abertschi.adfree.detector

import android.app.Notification
import android.os.Bundle
import ch.abertschi.adfree.model.TextRepository
import ch.abertschi.adfree.util.AppLogger
import ch.abertschi.adfree.util.warn
import java.util.Locale

class UserDefinedTextDetector(private val repo: TextRepository) : AdDetectable, AppLogger {

    override fun canHandle(payload: AdPayload): Boolean {
        val notificationKey: String =
            payload.statusbarNotification.key?.lowercase(Locale.ROOT) ?: return false

        var canHandle = false
        for (entry in repo.getAllEntries()) {
            val key = entry.packageName
            if (key.isEmpty() || key.isBlank()) continue
            if (notificationKey.contains(key.lowercase(Locale.ROOT).trim())) {
                payload.matchedTextDetectorEntries.add(entry)
                canHandle = true
            }
        }
        return canHandle
    }

    private fun extractString(extras: Bundle?, key: String): String? {
        return try {
            extras?.getCharSequence(key)
                ?.toString()?.trim()?.lowercase(Locale.ROOT)
        } catch (e: Exception) {
            warn { e }
            null
        }
    }

    private fun extractNotificationTexts(payload: AdPayload): List<String> {
        val extras = payload.statusbarNotification.notification?.extras
        val notification = payload.statusbarNotification.notification

        val candidates = mutableListOf<String?>()

        // Standard extras fields
        candidates += extractString(extras, Notification.EXTRA_TITLE)
        candidates += extractString(extras, Notification.EXTRA_TITLE_BIG)
        candidates += extractString(extras, Notification.EXTRA_TEXT)
        candidates += extractString(extras, Notification.EXTRA_BIG_TEXT)
        candidates += extractString(extras, Notification.EXTRA_SUB_TEXT)
        candidates += extractString(extras, Notification.EXTRA_INFO_TEXT)
        candidates += extractString(extras, Notification.EXTRA_SUMMARY_TEXT)

        // Ticker text (lives on the Notification object directly)
        candidates += notification?.tickerText
            ?.toString()?.trim()?.lowercase(Locale.ROOT)

        // Multi-line text style (InboxStyle): array of CharSequence
        try {
            val lines = extras?.getCharSequenceArray(Notification.EXTRA_TEXT_LINES)
            lines?.forEach { line ->
                candidates += line?.toString()?.trim()?.lowercase(Locale.ROOT)
            }
        } catch (e: Exception) {
            warn { e }
        }

        return candidates.filterNotNull()
    }

    override fun flagAsAdvertisement(payload: AdPayload): Boolean {
        val texts = extractNotificationTexts(payload)

        for (entry in payload.matchedTextDetectorEntries) {
            for (entryLine in entry.content) {
                val needle = entryLine.trim().lowercase(Locale.ROOT)
                if (needle.isEmpty()) continue
                if (texts.any { it.contains(needle) }) return true
            }
        }
        return false
    }

    override fun getMeta(): AdDetectorMeta = AdDetectorMeta(
        "User defined text", "flag a notification based on the presence of text",
        false,
        category = "General",
        debugOnly = false
    )
}