/*
 * Ad Free
 * Copyright (c) 2017 by abertschi, www.abertschi.ch
 * See the file "LICENSE" for the full license governing this code.
 */

package ch.abertschi.adfree.plugin

import ch.abertschi.adfree.model.PreferencesFactory
import ch.abertschi.adfree.plugin.mute.MutePlugin
import ch.abertschi.adfree.util.AppLogger
import ch.abertschi.adfree.util.info

/**
 * Created by abertschi on 21.04.17.
 */
class PluginHandler(
    val prefs: PreferencesFactory,
    val plugins: List<AdPlugin>
) : AppLogger {

    private var activePlugin: AdPlugin = loadActivePlugin()

    fun getActivePlugin(): AdPlugin {
        return activePlugin
    }

    private fun loadActivePlugin(): AdPlugin {
        val key: String? = prefs.getActivePlugin()
        val active = plugins.firstOrNull { serializeActivePluginId(it) == key }
        return active ?: MutePlugin() // default plugin
    }

    fun setActivePlugin(plugin: AdPlugin): AdPlugin {
        prefs.setActivePlugin(serializeActivePluginId(plugin))
        val oldPlugin = activePlugin
        activePlugin = plugin
        return oldPlugin
    }

    fun runPlugin() = activePlugin.play()

    fun trialRunPlugin() {
        activePlugin.playTrial()
    }

    fun stopPlugin(onStopped: () -> Unit) {
        info { "Stopping plugin " + activePlugin.javaClass.canonicalName }
        activePlugin.stop(onStopped)
    }

    fun forceStopPlugin(onStopped: () -> Unit) = activePlugin.forceStop(onStopped)

    private fun serializeActivePluginId(plugin: AdPlugin): String =
        plugin.javaClass.canonicalName ?: ""
}