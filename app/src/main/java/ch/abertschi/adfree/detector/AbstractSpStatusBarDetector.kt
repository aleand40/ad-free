/*
 * Ad Free
 * Copyright (c) 2017 by abertschi, www.abertschi.ch
 * See the file "LICENSE" for the full license governing this code.
 */

package ch.abertschi.adfree.detector

import org.jetbrains.anko.AnkoLogger
import java.util.Locale

/**
 * Created by abertschi on 13.12.16.
 */
abstract class AbstractSpStatusBarDetector : AdDetectable, AnkoLogger {

    companion object {
        private val spotifyPackageName = "com.spotify"
    }

    override fun canHandle(payload: AdPayload): Boolean {
        return payload?.statusbarNotification?.key?.toLowerCase(Locale.ROOT)?.contains(spotifyPackageName) ?: false
    }


}

