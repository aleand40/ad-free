/*
 * Ad Free
 * Copyright (c) 2017 by abertschi, www.abertschi.ch
 * See the file "LICENSE" for the full license governing this code.
 */

package ch.abertschi.adfree.model

import android.content.Context
import android.content.SharedPreferences
import org.jetbrains.anko.AnkoLogger

/**
 * Created by abertschi on 15.04.17.
 */
open class TrackRepository: AnkoLogger {

    private val context: Context
    private val tracks: String = "tracks"
    private var sharedPreferences: SharedPreferences

    constructor(context: Context, sharedPreferences: PreferencesFactory) {
        this.context = context
        this.sharedPreferences = sharedPreferences.getPreferences()
    }

    private fun getTracks(): MutableSet<String> {
        return sharedPreferences.getStringSet(tracks, HashSet<String>())?.toMutableSet() ?: mutableSetOf()
    }

    open fun getAllTracks(): Set<String> {
        val tracks = getTracks()
        return tracks
    }

}