package ch.abertschi.adfree.view

import android.content.Context
import android.graphics.Typeface

class ViewSettings private constructor(context: Context) {

    var typeFace: Typeface = Typeface.createFromAsset(context.applicationContext.assets, "fonts/Raleway-ExtraLight.ttf")

    companion object {
        const val AD_FREE_RESOURCE_ADDRESS: String = "https://github.com/abertschi/ad-free-resources/blob/master/"
        const val GITHUB_RAW_SUFFIX: String = "?raw=true"

        private var cachedInstance: ViewSettings? = null

        fun instance(context: Context): ViewSettings {
            if (cachedInstance == null) {
                cachedInstance = ViewSettings(context)
            }
            return cachedInstance!!
        }
    }
}