/*
 * Ad Free
 * Copyright (c) 2017 by abertschi, www.abertschi.ch
 * See the file "LICENSE" for the full license governing this code.
 */

package ch.abertschi.adfree.model

import ch.abertschi.adfree.util.AppLogger

/**
 * Created by abertschi on 15.04.17.
 */
open class TrackRepository(
    private val prefsFactory: PreferencesFactory
) : AppLogger {

    private val tracksKey: String = "tracks"

    private fun getTracks(): MutableSet<String> {
        return prefsFactory.getStringSet(tracksKey, HashSet())?.toMutableSet()
            ?: mutableSetOf()
    }

    open fun getAllTracks(): Set<String> {
        return getTracks()
    }
}