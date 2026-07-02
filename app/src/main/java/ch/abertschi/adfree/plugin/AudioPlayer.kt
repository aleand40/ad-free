/*
 * Ad Free
 * Copyright (c) 2017 by abertschi, www.abertschi.ch
 * See the file "LICENSE" for the full license governing this code.
 */

package ch.abertschi.adfree.plugin

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import ch.abertschi.adfree.AudioController
import ch.abertschi.adfree.model.PreferencesFactory
import ch.abertschi.adfree.util.AppLogger
import ch.abertschi.adfree.util.info
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.time.Duration.Companion.milliseconds

open class AudioPlayer(
    val context: Context,
    val prefs: PreferencesFactory,
    val audioController: AudioController
) : AppLogger {

    private var isPlaying: Boolean = false
    private var onStopCallables: MutableList<() -> Unit> = ArrayList()
    private var player: MediaPlayer? = null
    private val audioPlayerScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var playJob: Job? = null

    var trackPreparationDelayCallable: (() -> Unit)? = null

    fun play(url: String, loop: Boolean = false) {
        playAudio(url, loop)
    }

    private fun playAudio(url: String, loop: Boolean = false) {
        playJob?.cancel()

        playJob = audioPlayerScope.launch {
            try {
                val initializedPlayer = initializeMediaPlayer(url)
                this@AudioPlayer.player = initializedPlayer

                initializedPlayer.setOnErrorListener { _, what, _ ->
                    info("Problem with audio player, code: $what")
                    closePlayer()
                    true
                }
                initializedPlayer.isLooping = loop
                initializedPlayer.start()
                isPlaying = true
            } catch (e: Exception) {
                info("Error inicialitzant l'àudio: ${e.message}")
                closePlayer()
            }
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

    private suspend fun initializeMediaPlayer(url: String): MediaPlayer = suspendCancellableCoroutine { continuation ->
        val newPlayer = MediaPlayer()
        newPlayer.setDataSource(url)

        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
            .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
            .build()
        newPlayer.setAudioAttributes(audioAttributes)

        var asyncPreparationDone = false
        info { "$asyncPreparationDone / $trackPreparationDelayCallable" }

        trackPreparationDelayCallable?.let { callable ->
            info { "creating delayed task" }
            audioPlayerScope.launch {
                delay(500.milliseconds)
                info { "executing delayed task: $asyncPreparationDone" }
                if (!asyncPreparationDone) {
                    info { "invoking callable" }
                    callable.invoke()
                }
            }
        }

        newPlayer.setOnPreparedListener { preparedPlayer ->
            asyncPreparationDone = true
            audioController.showVoiceCallVolume()
            preparedPlayer.setOnCompletionListener {
                closePlayer()
                synchronized(onStopCallables) {
                    onStopCallables.forEach { it() }
                    onStopCallables.clear()
                }
            }

            continuation.resume(preparedPlayer)
        }

        newPlayer.prepareAsync()

        continuation.invokeOnCancellation {
            newPlayer.release()
        }
    }

    private fun closePlayer() {
        playJob?.cancel()
        isPlaying = false
        player?.stop()
        player?.reset()
        player?.release()
        player = null
    }
}