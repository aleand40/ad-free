/*
 * Ad Free
 * Copyright (c) 2017 by abertschi, www.abertschi.ch
 * See the file "LICENSE" for the full license governing this code.
 */

package ch.abertschi.adfree.plugin

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import ch.abertschi.adfree.AudioController
import ch.abertschi.adfree.model.PreferencesFactory
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.util.concurrent.TimeUnit

open class AudioPlayer(
    val context: Context,
    val prefs: PreferencesFactory,
    val audioController: AudioController
) : AnkoLogger {

    private var isPlaying: Boolean = false
    private var onStopCallables: MutableList<() -> Unit> = ArrayList()
    private var player: MediaPlayer? = null

    var trackPreparationDelayCallable: (() -> Unit)? = null

    fun play(url: String, loop: Boolean = false) {
        playAudio(url, loop)
    }

    @SuppressLint("CheckResult")
    private fun playAudio(url: String, loop: Boolean = false) {
        initializeMediaPlayerObservable(url).subscribe { initializedPlayer ->
            this.player = initializedPlayer
            initializedPlayer.setOnErrorListener { _, what, _ ->
                throw RuntimeException("Problem with audio player, code: $what")
            }
            initializedPlayer.isLooping = loop
            initializedPlayer.start()
            isPlaying = true
        }
    }

    fun requestStop(onStopped: () -> Unit) {
        if (!isPlaying) onStopped()
        else onStopCallables.add(onStopped)
    }

    fun forceStop(onStopped: () -> Unit) {
        closePlayer()
        onStopped.invoke()
    }

    fun stop(onStopped: () -> Unit) {
        audioController.fadeOffVoiceCallVolume {
            closePlayer()
            onStopped.invoke()
        }
    }

    @SuppressLint("CheckResult")
    private fun initializeMediaPlayerObservable(url: String): Observable<MediaPlayer> =
        Observable.create { source ->
            player = MediaPlayer()
            player?.setDataSource(url)

            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                .build()
            player?.setAudioAttributes(audioAttributes)

            var asyncPreparationDone = false
            info { "$asyncPreparationDone / $trackPreparationDelayCallable" }
            trackPreparationDelayCallable?.let { callable ->
                info { "creating observable" }
                Observable.just(true)
                    .delay(500, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe {
                        info { "executing observable: $asyncPreparationDone" }
                        if (!asyncPreparationDone) {
                            info { "invoking observable" }
                            callable.invoke()
                        }
                    }
            }
            player?.prepareAsync()
            player?.setOnPreparedListener {
                asyncPreparationDone = true
                audioController.showVoiceCallVolume()
                player?.setOnCompletionListener {
                    closePlayer()
                    synchronized(onStopCallables) {
                        onStopCallables.forEach { it() }
                        onStopCallables.clear()
                    }
                }
                source.onNext(player!!)
            }
        }

    private fun closePlayer() {
        isPlaying = false
        player?.stop()
        player?.reset()
        player?.release()
        player = null
    }
}