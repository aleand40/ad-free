/*
 * Ad Free
 * Copyright (c) 2017 by abertschi, www.abertschi.ch
 * See the file "LICENSE" for the full license governing this code.
 */

package ch.abertschi.adfree.view.about

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import ch.abertschi.adfree.R
import ch.abertschi.adfree.di.AboutModul
import ch.abertschi.adfree.presenter.AboutPresenter
import ch.abertschi.adfree.view.ViewSettings

/**
 * Created by abertschi on 21.04.17.
 */
class AboutActivity : Fragment(), AboutView {

    lateinit var typeFace: Typeface
    lateinit var presenter: AboutPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.about_view, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        typeFace = ViewSettings.instance(this.context!!).typeFace

        presenter = AboutModul(this.activity!!, this).provideAboutPresenter()

        val textView = view.findViewById<TextView>(R.id.authorTitle)
        textView.typeface = typeFace
        val text =
            "built with much &lt;3 by <font color=#FFFFFF>abertschi</font>. " +
                    "get my latest hacks and follow me on twitter."

        textView.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY)
        } else {
            @Suppress("DEPRECATION")
            Html.fromHtml(text)
        }

        view.findViewById<ImageView>(R.id.twitter).setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW,
                Uri.parse("https://twitter.com/andrinbertschi?rel=adfree"))
            this.context!!.startActivity(browserIntent)
        }

        view.findViewById<ImageView>(R.id.website).setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW,
                Uri.parse("http://abertschi.ch?rel=adfree"))
            this.context!!.startActivity(browserIntent)
        }

        view.findViewById<ImageView>(R.id.more_settings).setOnClickListener {
            presenter.showMoreSettings()
        }
    }
}