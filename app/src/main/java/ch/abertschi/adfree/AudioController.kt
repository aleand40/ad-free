/*
 * Ad Free
 * Copyright (c) 2017 by abertschi, www.abertschi.ch
 * See the file "LICENSE" for the full license governing this code.
 */

package ch.abertschi.adfree

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioManager
import ch.abertschi.adfree.model.PreferencesFactory
import ch.abertschi.adfree.util.AppLogger
import ch.abertschi.adfree.util.debug
import ch.abertschi.adfree.util.info
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * Created by abertschi on 16.04.17.
 */
class AudioController(val context: Context, val prefs: PreferencesFactory) : AppLogger {

    private var musicStreamVolume = 0
    private var musicStreamIsMuted = false

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

    @SuppressLint("CheckResult")
    fun showVoiceCallVolume() {
        val am = context.applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        am.setStreamVolume(
            AudioManager.STREAM_VOICE_CALL,
            prefs.loadVoiceCallAudioVolume(),
            AudioManager.FLAG_SHOW_UI
        )
        Observable.just(true)
            .delay(8000, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe {
                val volume = am.getStreamVolume(AudioManager.STREAM_VOICE_CALL)
                prefs.storeVoiceCallAudioVolume(volume)
                info("Storing audio volume with value $volume")
            }
    }

    @SuppressLint("CheckResult")
    fun fadeOffVoiceCallVolume(callback: (() -> Unit)?) {
        val am = context.applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val times: Long = 20
        var counter = 0
        Observable.just(1).delay(25, TimeUnit.MILLISECONDS)
            .repeat(times)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
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
                counter += 1
            }
    }
}