/*
 * Ad Free
 * Copyright (c) 2017 by abertschi, www.abertschi.ch
 * See the file "LICENSE" for the full license governing this code.
 */

package ch.abertschi.adfree.view.setting

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.core.net.toUri
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import ch.abertschi.adfree.R
import ch.abertschi.adfree.di.SettingsModul
import ch.abertschi.adfree.plugin.PluginActivityAction
import ch.abertschi.adfree.presenter.SettingsPresenter
import ch.abertschi.adfree.util.AppLogger
import ch.abertschi.adfree.util.toast
import ch.abertschi.adfree.view.ViewSettings

/**
 * Created by abertschi on 21.04.17.
 */

class SettingsActivity : Fragment(), SettingsView, AppLogger, PluginActivityAction {

    override fun activity(): Activity = requireActivity()

    private lateinit var typeFace: Typeface
    private var rootView: View? = null
    private var settingsTitle: TextView? = null
    private var spinner: Spinner? = null
    private var pluginViewContainer: LinearLayout? = null
    private var spinnerAdapter: PluginSpinnerAdapter? = null
    private var init: Boolean = false
    private val callablesOnActivityResult:
            MutableList<(requestCode: Int, resultCode: Int, data: Intent?) -> Unit> = ArrayList()

    lateinit var settingPresenter: SettingsPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.setting_view, container, false)
    }

    override fun clearPluginView() {
        pluginViewContainer?.removeAllViews()
    }

    override fun setPluginView(view: View) {
        view.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        pluginViewContainer = rootView?.findViewById(R.id.setting_plugin_view)
        clearPluginView()
        pluginViewContainer?.addView(view)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.rootView = view

        typeFace = ViewSettings.instance(requireContext()).typeFace
        settingsTitle = view.findViewById(R.id.settingsTitle)
        settingsTitle?.typeface = typeFace

        settingPresenter = SettingsModul(requireContext(), this).provideSettingsPresenter()

        val text = getString(R.string.settings_ads_action_question)
        settingsTitle?.text = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)

        spinner = view.findViewById(R.id.spinner)
        spinnerAdapter = PluginSpinnerAdapter(
            requireContext(), R.layout.replacer_setting_item,
            settingPresenter.getStringEntriesOfModel(), spinner!!
        )
        spinner?.adapter = spinnerAdapter

        spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (init) settingPresenter.onPluginSelected(position)
                spinnerAdapter?.notifyDataSetChanged()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        view.findViewById<ImageView>(R.id.try_plugin_button).setOnClickListener {
            settingPresenter.tryPlugin()
        }
        view.findViewById<LinearLayout>(R.id.setting_spinner_item_container)
            ?.setOnTouchListener { _, _ ->
                spinner?.performClick()
                false
            }

        settingPresenter.onCreate()
        init = true
    }

    override fun onResume() {
        super.onResume()
        settingPresenter.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        rootView = null
        settingsTitle = null
        spinner = null
        pluginViewContainer = null
        spinnerAdapter = null
        init = false
    }

    override fun setActivePlugin(index: Int) {
        spinner?.setSelection(index, true)
    }

    override fun provideContext(): Context = requireContext()

    override fun showSuggestNewPlugin() {
        val browserIntent =
            Intent(Intent.ACTION_VIEW, "https://github.com/abertschi/ad-free/issues".toUri())
        startActivity(browserIntent)
    }

    override fun showTryOutMessage() {
        requireContext().toast(getString(R.string.trying_out_plugin))
    }

    @Suppress("DEPRECATION") @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callablesOnActivityResult.forEach { it(requestCode, resultCode, data) }
    }

    override fun addOnActivityResult(
        callable: (requestCode: Int, resultCode: Int, data: Intent?) -> Unit
    ) {
        callablesOnActivityResult.add(callable)
    }
}