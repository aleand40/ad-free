/*
 * Ad Free
 * Copyright (c) 2017 by abertschi, www.abertschi.ch
 * See the file "LICENSE" for the full license governing this code.
 */

package ch.abertschi.adfree

import ch.abertschi.adfree.ad.AdEvent
import ch.abertschi.adfree.ad.AdObservable
import ch.abertschi.adfree.ad.AdObserver
import ch.abertschi.adfree.ad.EventType
import ch.abertschi.adfree.model.PreferencesFactory
import ch.abertschi.adfree.plugin.PluginHandler
import ch.abertschi.adfree.util.AppLogger
import ch.abertschi.adfree.util.info
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

/**
 * Created by abertschi on 14.08.17.
 */
class AdStateController(
    private val audioController: AudioController,
    private val adPluginHandler: PluginHandler,
    private val notificationChannel: NotificationChannel,
    private val castManager: GoogleCastManager,
    private val prefs: PreferencesFactory
) :
    AdObserver, AppLogger {

    private var activeState: EventType? = EventType.NO_AD

    private val timeout = 120_000.milliseconds

    private val stateScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var timeoutJob: Job? = null

    override fun onAdEvent(event: AdEvent, observable: AdObservable) {
        if (activeState != EventType.IS_AD && event.eventType == EventType.IS_AD) {
            activeState = EventType.IS_AD
            onAd(observable)
        }
        if (activeState != EventType.NO_AD && event.eventType == EventType.NO_AD) {
            onNoAd()
        }
        if (activeState != EventType.IGNORE_AD && event.eventType == EventType.IGNORE_AD) {
            onIgnoreAd()
        }
        if (event.eventType == EventType.SHOWCASE) {
            onShowCase(observable)
        }
    }

    fun onShowCase(observable: AdObservable) {
        activeState = EventType.SHOWCASE
        adPluginHandler.forceStopPlugin {
            audioController.muteMusicStream()
            castManager.muteAudio()
            notificationChannel.showDefaultAdNotification {
                observable.requestIgnoreAd()
            }
            adPluginHandler.trialRunPlugin()
            resetTimeout()
            startTimeout {
                observable.requestNoAd()
            }
        }
    }

    fun onIgnoreAd() {
        info { "AdEvent Change: IGNORE_AD" }
        activeState = EventType.IGNORE_AD

        adPluginHandler.forceStopPlugin {
            audioController.unmuteMusicStream()
            castManager.unmuteAudio()
            notificationChannel.hideDefaultAdNotification()
        }
    }

    fun onNoAd() {
        info { "AdEvent Change: NO_ADD" }
        activeState = EventType.NO_AD

        val doUnmute = {
            castManager.unmuteAudio()
            adPluginHandler.stopPlugin {
                notificationChannel.hideDefaultAdNotification()
                audioController.unmuteMusicStream()
            }
        }

        val delaySeconds = prefs.getDelaySeconds()
        if (delaySeconds > 0) {
            stateScope.launch {
                info("delaying unmute by $delaySeconds seconds")
                delay(delaySeconds.seconds)
                doUnmute()
            }
        } else doUnmute()
    }

    fun onAd(observable: AdObservable) {
        info { "AdEvent Change: IS_ADD" }
        resetTimeout()
        startTimeout {
            observable.requestNoAd()
        }
        castManager.muteAudio()
        audioController.muteMusicStream()
        adPluginHandler.runPlugin()
        notificationChannel.showDefaultAdNotification {
            observable.requestIgnoreAd()
        }
    }

    private fun startTimeout(callable: () -> Unit) {
        timeoutJob = stateScope.launch {
            delay(timeout)
            callable()
        }
    }

    private fun resetTimeout() {
        timeoutJob?.cancel()
    }
}