/*
 * Ad Free
 * Copyright (c) 2017 by abertschi, www.abertschi.ch
 * See the file "LICENSE" for the full license governing this code.
 */

package ch.abertschi.adfree.plugin.mute

import android.content.Context
import ch.abertschi.adfree.R
import ch.abertschi.adfree.plugin.AdPlugin

/**
 * Created by abertschi on 21.04.17.
 */
class MutePlugin(private val context: Context) : AdPlugin {

    override fun stop(onStopped: () -> Unit) {
        onStopped()
    }

    override fun title(): String = context.getString(R.string.mute_audio)

    override fun play() {
    }

    override fun playTrial() {
    }

    override fun requestStop(onStopped: () -> Unit) {
        onStopped()
    }

    override fun forceStop(onStopped: () -> Unit) {
        onStopped()
    }

    override fun onPluginDeactivated() {
    }
}