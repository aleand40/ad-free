/*
 * Ad Free
 * Copyright (c) 2017 by abertschi, www.abertschi.ch
 * See the file "LICENSE" for the full license governing this code.
 */

package ch.abertschi.adfree.model

import ch.abertschi.adfree.util.AppLogger
import ch.abertschi.adfree.util.info
import ch.abertschi.adfree.view.ViewSettings
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by abertschi on 28.04.17.
 */

class RemoteManager(prefFactory: PreferencesFactory) : AppLogger {

    private val url: String = ViewSettings.AD_FREE_RESOURCE_ADDRESS +
            "settings.yaml" + ViewSettings.GITHUB_RAW_SUFFIX

    var remoteSettings: RemoteSetting? = null
    var configFactory: YamlRemoteConfigFactory<RemoteSetting> =
        YamlRemoteConfigFactory(url, RemoteSetting::class.java, prefFactory)

    fun getRemoteSettingsObservable(): Observable<RemoteSetting> {
        info("fetching settings getRemoteSettingsObservable")
        remoteSettings = configFactory.loadFromLocalStore()

        return Observable.create<RemoteSetting> { source ->
            // Link the subscription to the source lifecycle to prevent memory leaks
            val disposable = configFactory.downloadObservable()
                .map { it.first }
                .doOnNext {
                    remoteSettings = it
                    configFactory.storeToLocalStore(it) // Persist state locally
                }
                .subscribe(
                    { _ -> source.onNext(remoteSettings!!) },
                    { err ->
                        info(err)
                    })

            source.setDisposable(disposable)
        }
            .observeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }
}