/*
 * Ad Free
 * Copyright (c) 2017 by abertschi, www.abertschi.ch
 * See the file "LICENSE" for the full license governing this code.
 */

package ch.abertschi.adfree.plugin

import android.content.Context
import android.view.View

/**
 * Created by abertschi on 21.04.17.
 */
interface AdPlugin {

    fun title(): String
    fun hasSettingsView(): Boolean = false
    fun settingsView(context: Context, activityActions: PluginActivityAction): View? = null

    fun play()
    fun playTrial()

    /**
     * run plugin until the end and call callback on stop
     */
    fun requestStop(onStopped: () -> Unit)

    /**
     * Forcefully stop plugin
     */
    fun forceStop(onStopped: () -> Unit)

    /**
     * Signalize plugin to stop now. Call callback on done
     */
    fun stop(onStopped: () -> Unit)

    fun onPluginLoaded()
    fun onPluginActivated()
    fun onPluginDeactivated()
}
