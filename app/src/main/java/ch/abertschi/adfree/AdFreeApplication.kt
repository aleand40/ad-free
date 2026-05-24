/*
 * Ad Free
 * Copyright (c) 2017 by abertschi, www.abertschi.ch
 * See the file "LICENSE" for the full license governing this code.
 */

package ch.abertschi.adfree

import android.app.Activity
import android.app.Application
import ch.abertschi.adfree.ad.AdDetector
import ch.abertschi.adfree.plugin.AdPlugin
import ch.abertschi.adfree.plugin.PluginHandler
import ch.abertschi.adfree.plugin.localmusic.LocalMusicPlugin
import ch.abertschi.adfree.plugin.mute.MutePlugin
import ch.abertschi.adfree.util.NotificationUtils
import ch.abertschi.adfree.crashhandler.CrashExceptionHandler
import ch.abertschi.adfree.model.PreferencesFactory
import ch.abertschi.adfree.model.AdDetectableFactory
import ch.abertschi.adfree.model.TextRepository
import ch.abertschi.adfree.model.RemoteManager
import ch.abertschi.adfree.util.AppLogger

class AdFreeApplication : Application(), AppLogger {

    lateinit var prefs: PreferencesFactory
    lateinit var adDetectors: AdDetectableFactory
    lateinit var adDetector: AdDetector
    lateinit var audioManager: AudioController
    lateinit var pluginHandler: PluginHandler
    lateinit var adPlugins: List<AdPlugin>
    lateinit var adStateController: AdStateController
    lateinit var notificationUtils: NotificationUtils
    lateinit var notificationChannel: NotificationChannel
    lateinit var remoteManager: RemoteManager
    lateinit var notificationStatus: NotificationStatusManager
    lateinit var googleCast: GoogleCastManager
    lateinit var textRepository: TextRepository

    lateinit var mainActivity: Activity

    override fun onCreate() {
        super.onCreate()

        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            CrashExceptionHandler(this).uncaughtException(thread, throwable)
            defaultHandler?.uncaughtException(thread, throwable)
        }

        prefs = PreferencesFactory(applicationContext)
        textRepository = TextRepository(this, prefs)

        googleCast = GoogleCastManager(prefs)
        notificationStatus = NotificationStatusManager(applicationContext)

        adDetectors = AdDetectableFactory(applicationContext, prefs)

        audioManager = AudioController(applicationContext, prefs)
        remoteManager = RemoteManager(prefs)
        adDetector = AdDetector(adDetectors, remoteManager)

        notificationUtils = NotificationUtils(applicationContext)
        notificationChannel = NotificationChannel(notificationUtils, prefs)

        adPlugins = listOf(
            MutePlugin(),
            LocalMusicPlugin(applicationContext, prefs, audioManager)
        )
        pluginHandler = PluginHandler(prefs, adPlugins)

        adStateController = AdStateController(
            audioManager,
            pluginHandler, notificationChannel, googleCast, prefs
        )
        adDetector.addObserver(adStateController)

        notificationStatus.restartNotificationListener()

        Thread {
            if (prefs.isAlwaysOnNotificationEnabled()) {
                notificationStatus.forceTimedRestart()
            }
        }.start()
    }
}