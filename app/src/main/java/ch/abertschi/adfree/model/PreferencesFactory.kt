/*
 * Ad Free
 * Copyright (c) 2017 by abertschi, www.abertschi.ch
 * See the file "LICENSE" for the full license governing this code.
 */

package ch.abertschi.adfree.model

import android.content.Context
import android.content.SharedPreferences
import ch.abertschi.adfree.detector.AdDetectable
import androidx.core.content.edit
import ch.abertschi.adfree.util.AppLogger

/**
 * Created by abertschi on 15.04.17.
 */
class PreferencesFactory(context: Context) : AppLogger {
    private val prefsKey = "PREFS"
    private val prefIsEnabled = "IS_ENABLED"
    private val prefsAudioVolume: String = "AUDIO_KEY"
    private val prefsActivePlugin: String = "ACTIVE_PLUGIN"
    private val prefsLocalMusic: String = "location_local_music"
    private val prefsPlayUntilEnd: String = "location_local_music_play_until_end"
    private val prefsAdDetectableMetaPrefix: String = "detectable_"

    private val prefsDelaySound = "DELAY_SOUND"
    private val prefsAlwaysOnNotification = "ALWAYS_ON_NOTIFICATION"
    private val prefsIsDebugDetectors = "DEBUG_DETECTORS_ENABLED"
    private val prefsGoogleCast = "CAST_ENABLED"
    private val prefsLoopPlayback: String = "location_local_music_loop"

    private val prefs: SharedPreferences = context.getSharedPreferences(prefsKey, Context.MODE_PRIVATE)

    fun isBlockingEnabled(): Boolean {
        return prefs.getBoolean(prefIsEnabled, true)
    }

    fun setBlockingEnabled(enabled: Boolean) {
        prefs.edit { putBoolean(prefIsEnabled, enabled) }
    }

    fun isGoogleCastEnabled(): Boolean = prefs.getBoolean(prefsGoogleCast, false)

    fun setGoogleCastEnabled(e: Boolean) = prefs.edit { putBoolean(prefsGoogleCast, e) }

    fun storeVoiceCallAudioVolume(volume: Int) =
        prefs.edit { putInt(prefsAudioVolume, volume) }

    fun loadVoiceCallAudioVolume(): Int =
        prefs.getInt(prefsAudioVolume, 100)

    fun setPlayUntilEnd(flag: Boolean) =
        prefs.edit { putBoolean(prefsPlayUntilEnd, flag) }

    fun getPlayUntilEnd(): Boolean =
        prefs.getBoolean(prefsPlayUntilEnd, false)

    fun setLoopMusicPlayback(flag: Boolean) =
        prefs.edit { putBoolean(prefsLoopPlayback, flag) }

    fun getLoopMusicPlayback(): Boolean =
        prefs.getBoolean(prefsLoopPlayback, false)

    fun getLocalMusicDirectory(): String =
        prefs.getString(prefsLocalMusic, "not set yet")!!

    fun setLocalMusicDirectory(value: String) =
        prefs.edit { putString(prefsLocalMusic, value) }

    @Deprecated("Don't use shared prefs outside this class anymore")
    fun getPreferences(): SharedPreferences = prefs

    fun getActivePlugin(): String? {
        return prefs.getString(prefsActivePlugin, null)
    }

    fun setActivePlugin(plugin: String) {
        prefs.edit { putString(prefsActivePlugin, plugin) }
    }

    fun isAlwaysOnNotificationEnabled() =
        prefs.getBoolean(prefsAlwaysOnNotification, false)

    fun setAlwaysOnNotification(enable: Boolean) =
        prefs.edit { putBoolean(prefsAlwaysOnNotification, enable) }

    fun getDelaySeconds(): Int =
        prefs.getInt(prefsDelaySound, 0)

    fun setDelaySeconds(s: Int) =
        prefs.edit { putInt(prefsDelaySound, s) }

    fun isAdDetectableEnabled(d: AdDetectable) =
        prefs.getBoolean(
            prefsAdDetectableMetaPrefix + d.javaClass.canonicalName,
            d.getMeta().enabledByDef
        )

    fun saveAdDetectableEnable(enable: Boolean, d: AdDetectable) {
        prefs.edit { putBoolean(prefsAdDetectableMetaPrefix + d.javaClass.canonicalName, enable) }
    }

    fun isDeveloperModeEnabled() = prefs.getBoolean(prefsIsDebugDetectors, false)

    fun setDeveloperMode(isDebug: Boolean) =
        prefs.edit { putBoolean(prefsIsDebugDetectors, isDebug) }

    fun getString(key: String, defValue: String? = null): String? = prefs.getString(key, defValue)

    fun storeString(key: String, value: String) {
        prefs.edit { putString(key, value) }
    }
}