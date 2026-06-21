/*
 * Ad Free
 * Copyright (c) 2017 by abertschi, www.abertschi.ch
 * See the file "LICENSE" for the full license governing this code.
 */

package ch.abertschi.adfree.view.about

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.about_view, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        typeFace = ViewSettings.instance(this.requireContext()).typeFace

        presenter = AboutModul(this.requireActivity(), this).provideAboutPresenter()

        val textView = view.findViewById<TextView>(R.id.authorTitle)
        textView.typeface = typeFace
        val text = getString(R.string.about_author_message)

        textView.text = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)

        view.findViewById<ImageView>(R.id.twitter).setOnClickListener {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                "https://twitter.com/andrinbertschi?rel=adfree".toUri()
            )
            this.requireContext().startActivity(browserIntent)
        }

        view.findViewById<ImageView>(R.id.website).setOnClickListener {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                "http://abertschi.ch?rel=adfree".toUri()
            )
            this.requireContext().startActivity(browserIntent)
        }

        view.findViewById<ImageView>(R.id.more_settings).setOnClickListener {
            presenter.showMoreSettings()
        }
    }
}