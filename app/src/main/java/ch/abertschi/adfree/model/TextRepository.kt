package ch.abertschi.adfree.model

import android.content.Context
import android.content.SharedPreferences
import com.thoughtworks.xstream.XStream
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.lang.IllegalStateException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet
import androidx.core.content.edit

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


class TextRepository : AnkoLogger {
    private val context: Context
    private val idKey: String = "k_"
    private val idKeys: String = "keys"

    private var dataEntries: ArrayList<TextRepositoryData>

    private fun formatKey(id: String) = idKey + "_" + id

    private var sharedPreferences: SharedPreferences

    constructor(context: Context, sharedPreferences: PreferencesFactory) {
        this.context = context
        this.sharedPreferences = sharedPreferences.getPreferences()
        dataEntries = deserializeAllEntries()
    }

    private fun getKeys(): MutableSet<String> {
        return sharedPreferences.getStringSet(idKeys, HashSet<String>())?.toMutableSet() ?: mutableSetOf()
    }



    private fun getEntryByFormattedKey(key: String): TextRepositoryData? {
        val dataStr: String = sharedPreferences.getString(key, null) ?: return null
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
        sharedPreferences.edit { putString(key, data.serializeToString()) }
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
        sharedPreferences.edit { remove(key) }
    }

    private fun setAllKeys(keys: Set<String>) {
        sharedPreferences.edit { putStringSet(idKeys, keys) }
    }
}