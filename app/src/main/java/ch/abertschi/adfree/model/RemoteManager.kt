/*
 * Ad Free
 * Copyright (c) 2017 by abertschi, www.abertschi.ch
 * See the file "LICENSE" for the full license governing this code.
 */

package ch.abertschi.adfree.model

import ch.abertschi.adfree.util.AppLogger
import ch.abertschi.adfree.util.info
import ch.abertschi.adfree.view.ViewSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Created by abertschi on 28.04.17.
 */
class RemoteManager(prefFactory: PreferencesFactory) : AppLogger {

    private val url: String = ViewSettings.AD_FREE_RESOURCE_ADDRESS +
            "settings.yaml" + ViewSettings.GITHUB_RAW_SUFFIX

    var remoteSettings: RemoteSetting? = null
    var configFactory: YamlRemoteConfigFactory<RemoteSetting> =
        YamlRemoteConfigFactory(url, RemoteSetting::class.java, prefFactory)

    suspend fun getRemoteSettings(): RemoteSetting = withContext(Dispatchers.IO) {
        info("fetching settings getRemoteSettings")

        remoteSettings = configFactory.loadFromLocalStore()

        try {
            val downloadedSettings = configFactory.download()
            remoteSettings = downloadedSettings
            configFactory.storeToLocalStore(downloadedSettings)

        } catch (err: Exception) {
            info("Error updating remote settings: ${err.message}")
        }

        return@withContext remoteSettings!!
    }
}