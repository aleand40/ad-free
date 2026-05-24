/*
 * Ad Free
 * Copyright (c) 2017 by abertschi, www.abertschi.ch
 * See the file "LICENSE" for the full license governing this code.
 */

package ch.abertschi.adfree.model

import ch.abertschi.adfree.util.AppLogger
import android.content.Context
import org.json.JSONArray
import java.io.IOException
import java.nio.charset.Charset


/**
 * Created by abertschi on 01.09.17.
 */
@Deprecated("no longer needed")
class YesNoModel(val context: Context) : AppLogger {

    var yes: List<String> = listOf()
    var no: List<String> = listOf()

    init {
        yes = loadJSONFromAsset("yes.json")
        no = loadJSONFromAsset("no.json")
    }

    fun loadJSONFromAsset(assetLocation: String): List<String> {
        var json: String?
        try {

            val stream = context.assets.open(assetLocation)
            val size = stream.available()
            val buffer = ByteArray(size)
            stream.read(buffer)
            stream.close()
            json = buffer.toString(Charset.defaultCharset())
            val words = JSONArray(json)
            val result = ArrayList<String>()
            (0 until words.length()).mapTo(result) { words[it] as String }
            return result
        } catch (ex: IOException) {
            ex.printStackTrace()
            return listOf("")
        }
    }
}