/*
 * Ad Free
 * Copyright (c) 2017 by abertschi, www.abertschi.ch
 * See the file "LICENSE" for the full license governing this code.
 */

package ch.abertschi.adfree.plugin

import android.app.Activity
import android.content.Intent

/**
 * Created by abertschi on 30.08.17.
 */
interface PluginActivityAction {

    fun launchPluginIntent(intent: Intent?, requestCode: Int)

    fun addOnActivityResult(callable: (requestCode: Int, resultCode: Int, data: Intent?) -> Unit)

    fun activity(): Activity

    fun requestPermission(permission: String, onResult: (Boolean) -> Unit)
}