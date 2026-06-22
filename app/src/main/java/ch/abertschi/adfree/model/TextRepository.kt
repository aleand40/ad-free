/*
 * Ad Free
 * Copyright (c) 2017 by abertschi, www.abertschi.ch
 * See the file "LICENSE" for the full license governing this code.
 */

package ch.abertschi.adfree.model

import ch.abertschi.adfree.util.AppLogger
import ch.abertschi.adfree.util.info
import com.thoughtworks.xstream.XStream
import java.util.UUID

data class TextRepositoryData(
    var packageName: String = "",
    var content: List<String> = ArrayList(),
    var subTitleNull: Boolean = false,
    var id: String = UUID.randomUUID().toString()
) {

    fun serializeToString(): String {
        return serializeToString(this)
    }

    companion object {
        val serial: XStream = XStream()

        fun serializeToString(data: TextRepositoryData): String {
            return serial.toXML(data)
        }

        fun deserializeFromString(s: String?): TextRepositoryData {
            return serial.fromXML(s) as TextRepositoryData
        }
    }
}

class TextRepository(
    private val prefsFactory: PreferencesFactory
) : AppLogger {

    private val idKey: String = "k_"
    private val idKeys: String = "keys"

    private var dataEntries: ArrayList<TextRepositoryData>

    private fun formatKey(id: String) = idKey + "_" + id

    init {
        dataEntries = deserializeAllEntries()
    }

    private fun getKeys(): MutableSet<String> {
        return prefsFactory.getStringSet(idKeys, HashSet())?.toMutableSet()
            ?: mutableSetOf()
    }

    private fun getEntryByFormattedKey(key: String): TextRepositoryData? {
        val dataStr: String = prefsFactory.getString(key, null) ?: return null
        return TextRepositoryData.deserializeFromString(dataStr)
    }

    private fun deserializeAllEntries(): ArrayList<TextRepositoryData> {
        val entries = ArrayList<TextRepositoryData>()
        for (key in getKeys()) {
            val entry: TextRepositoryData? = getEntryByFormattedKey(key)
            if (entry != null) {
                entries.add(entry)
            }
        }
        return entries
    }

    fun getAllEntries(): ArrayList<TextRepositoryData> {
        return ArrayList(dataEntries)
    }

    fun createNewEntry(): TextRepositoryData {
        val d = TextRepositoryData()
        dataEntries.add(d)
        return d
    }

    fun updateEntry(data: TextRepositoryData) {
        if (!dataEntries.contains(data)) {
            throw IllegalStateException("data entry not known")
        }
        val key = formatKey(data.id)
        info("storing text: $key")
        info("storing text: $data")

        val keys = getKeys()
        keys.add(key)
        setAllKeys(keys)
        prefsFactory.storeString(key, data.serializeToString())
    }

    fun removeEntry(data: TextRepositoryData) {
        if (!dataEntries.contains(data)) {
            return
        }
        dataEntries.remove(data)

        val key = formatKey(data.id)
        val keys = getKeys()
        keys.remove(key)
        setAllKeys(keys)
        prefsFactory.remove(key)
    }

    private fun setAllKeys(keys: Set<String>) {
        prefsFactory.storeStringSet(idKeys, keys)
    }
}