/*
 * Ad Free
 * Copyright (c) 2017 by abertschi, www.abertschi.ch
 * See the file "LICENSE" for the full license governing this code.
 */

package ch.abertschi.adfree

import android.content.Context
import android.media.AudioManager
import ch.abertschi.adfree.model.PreferencesFactory
import ch.abertschi.adfree.util.AppLogger
import ch.abertschi.adfree.util.debug
import ch.abertschi.adfree.util.info
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

/**
 * Created by abertschi on 16.04.17.
 */
class AudioController(val context: Context, val prefs: PreferencesFactory) : AppLogger {

    private var musicStreamVolume = 0
    private var musicStreamIsMuted = false

    private val audioScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun muteMusicStream() {
        debug { "current volume $musicStreamVolume" }
        info("muting audio")

        if (musicStreamIsMuted) {
            return
        }
        musicStreamIsMuted = true
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        musicStreamVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC)

        am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0)
    }

    fun unmuteMusicStream() {
        info("Unmuting audio....")
        if (!musicStreamIsMuted) {
            return
        }
        musicStreamIsMuted = false
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        am.setStreamVolume(AudioManager.STREAM_MUSIC, musicStreamVolume, 0)
    }

    fun showVoiceCallVolume() {
        val am = context.applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        am.setStreamVolume(
            AudioManager.STREAM_VOICE_CALL,
            prefs.loadVoiceCallAudioVolume(),
            AudioManager.FLAG_SHOW_UI
        )

        audioScope.launch {
            delay(8000.milliseconds)
            val volume = am.getStreamVolume(AudioManager.STREAM_VOICE_CALL)
            prefs.storeVoiceCallAudioVolume(volume)
            info("Storing audio volume with value $volume")
        }
    }

    fun fadeOffVoiceCallVolume(callback: (() -> Unit)?) {
        val am = context.applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val times = 20

        audioScope.launch {
            for (counter in 0 until times) {
                delay(25.milliseconds)
                info { counter }

                if (counter < times - 1) {
                    am.adjustStreamVolume(
                        AudioManager.STREAM_VOICE_CALL,
                        AudioManager.ADJUST_LOWER,
                        0
                    )
                } else {
                    am.adjustStreamVolume(
                        AudioManager.STREAM_VOICE_CALL,
                        AudioManager.ADJUST_MUTE,
                        0
                    )
                    callback?.invoke()
                }
            }
        }
    }
}