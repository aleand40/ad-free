/*
 * Ad Free
 * Copyright (c) 2017 by abertschi, www.abertschi.ch
 * See the file "LICENSE" for the full license governing this code.
 */

package ch.abertschi.adfree.model

import com.github.kittinunf.fuel.httpGet
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.representer.Representer

/**
 * Created by abertschi on 26.04.17.
 */
class YamlRemoteConfigFactory<MODEL>(
    val downloadUrl: String,
    val modelType: Class<MODEL>,
    val preferences: PreferencesFactory
) {

    private val settingPersistenceLocalKey = "YAML_CONFIG_FACTORY_PERSISTENCE_${modelType.canonicalName}"

    fun downloadObservable(): Observable<Pair<MODEL, String>> =
        Observable.create<Pair<MODEL, String>> { source ->
            downloadUrl.httpGet().responseString { _, _, result ->
                val (data, error) = result
                if (error == null) {
                    try {
                        val yaml = createYamlInstance()
                        val model = yaml.loadAs(data, modelType)
                        source.onNext(Pair(model, data ?: ""))
                    } catch (exception: Exception) {
                        source.onError(exception)
                    }
                } else {
                    source.onError(error)
                }
                source.onComplete()
            }
        }.observeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun loadFromLocalStore(defaultReturn: MODEL? = null): MODEL? {
        val yaml = createYamlInstance()
        val content = preferences.getString(settingPersistenceLocalKey, null)

        return content?.let { yaml.loadAs(it, modelType) } ?: defaultReturn
    }

    fun storeToLocalStore(model: MODEL) {
        val yaml = createYamlInstance()
        preferences.storeString(settingPersistenceLocalKey, yaml.dump(model))
    }

    private fun createYamlInstance(): Yaml {
        val representer = Representer()
        representer.propertyUtils.setSkipMissingProperties(true)
        return Yaml(representer)
    }
}