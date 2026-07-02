/*
 * Ad Free
 * Copyright (c) 2017 by abertschi, www.abertschi.ch
 * See the file "LICENSE" for the full license governing this code.
 */

package ch.abertschi.adfree.model

import com.github.kittinunf.fuel.httpGet
import kotlinx.coroutines.suspendCancellableCoroutine
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.representer.Representer
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Created by abertschi on 26.04.17.
 */
class YamlRemoteConfigFactory<MODEL>(
    val downloadUrl: String,
    val modelType: Class<MODEL>,
    val preferences: PreferencesFactory
) {

    private val settingPersistenceLocalKey =
        "YAML_CONFIG_FACTORY_PERSISTENCE_${modelType.canonicalName}"

    suspend fun download(): MODEL = suspendCancellableCoroutine { continuation ->

        // 2. Fem la petició d'internet
        val request = downloadUrl.httpGet().responseString { _, _, result ->
            val (data, error) = result
            if (error == null) {
                try {
                    val yaml = createYamlInstance()
                    val model = yaml.loadAs(data, modelType)

                    continuation.resume(model)
                } catch (exception: Exception) {
                    continuation.resumeWithException(exception)
                }
            } else {
                continuation.resumeWithException(error)
            }
        }

        continuation.invokeOnCancellation {
            request.cancel()
        }
    }

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