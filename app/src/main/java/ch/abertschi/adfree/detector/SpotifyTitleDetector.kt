/*
 * Ad Free
 * Copyright (c) 2017 by abertschi, www.abertschi.ch
 * See the file "LICENSE" for the full license governing this code.
 */

package ch.abertschi.adfree.detector

import android.app.Notification
import ch.abertschi.adfree.model.TrackRepository
import ch.abertschi.adfree.util.AppLogger
import java.util.Locale

/**
 * AdDetectable that checks for the Keyword Spotify
 *
 * Created by abertschi on 15.04.17.
 */
class SpotifyTitleDetector(val trackRepository: TrackRepository) :
    AbstractSpStatusBarDetector(), AppLogger {

    private val keywords = listOf(
        "spotify —", "advertisement —"
    )

    override fun canHandle(payload: AdPayload): Boolean {
        getTitle(payload).let { payload.ignoreKeys.add(it) }
        return super.canHandle(payload)
    }

    override fun flagAsAdvertisement(payload: AdPayload): Boolean {
        val title = getTitle(payload).lowercase(Locale.ROOT).trim()
        if (title.isEmpty()) return false

        return keywords.any { title.contains(it) }
    }

    override fun flagAsMusic(payload: AdPayload): Boolean =
        trackRepository.getAllTracks().contains(getTitle(payload))

    fun getTitle(payload: AdPayload): String {
        val notification = payload.statusbarNotification.notification ?: return ""

        val titleFromExtras = notification.extras?.getCharSequence(Notification.EXTRA_TITLE)?.toString()
        if (!titleFromExtras.isNullOrBlank()) {
            return titleFromExtras
        }

        return notification.tickerText?.toString() ?: ""
    }

    override fun getMeta(): AdDetectorMeta = AdDetectorMeta(
        "Notification text",
        "spotify detector for text in notification",
        category = "Spotify"
    )
}