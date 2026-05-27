package ch.abertschi.adfree

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ch.abertschi.adfree.util.AppLogger
import ch.abertschi.adfree.util.info

class StartOnBootListener : BroadcastReceiver(), AppLogger {

    override fun onReceive(context: Context?, intent: Intent?) {
        // check that the action is really the one to boot the system
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) {
            return
        }

        info { "launching ad-free on boot. Hello world" }

        val app = context?.applicationContext as AdFreeApplication
        // launching ad-free application class on boot to initialize ad-free
        // see AdFreeApplication
        info { app }
    }
}