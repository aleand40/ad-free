/*
 * Ad Free
 * Copyright (c) 2017 by abertschi, www.abertschi.ch
 * See the file "LICENSE" for the full license governing this code.
 */

package ch.abertschi.adfree.view.home

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import ch.abertschi.adfree.R
import ch.abertschi.adfree.di.HomeModule
import ch.abertschi.adfree.presenter.HomePresenter
import ch.abertschi.adfree.util.AppLogger
import ch.abertschi.adfree.view.ViewSettings

class HomeActivity : Fragment(), HomeView, AppLogger {

    private lateinit var typeFace: Typeface
    private lateinit var enjoySloganText: TextView
    private lateinit var homePresenter: HomePresenter
    private lateinit var updateMessageInfo: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.home_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        homePresenter = HomeModule(this.requireActivity(), this).provideSettingsPresenter()
        typeFace = ViewSettings.instance(this.requireContext()).typeFace

        enjoySloganText = view.findViewById(R.id.enjoy)
        updateMessageInfo = view.findViewById(R.id.version_update_reminder)

        view.findViewById<TextView>(R.id.troubleshooting).setOnClickListener {
            homePresenter.onTroubleshooting()
        }

        homePresenter.onCreate(this.requireContext())
    }

    override fun showUpdateMessage(show: Boolean) {
        if (show) {
            updateMessageInfo.visibility = View.VISIBLE
            updateMessageInfo.setOnClickListener {
                homePresenter.onUpdateMessageClicked()
            }
        } else {
            updateMessageInfo.visibility = View.GONE
        }
    }

    override fun onResume() {
        homePresenter.onResume(this.requireContext())
        super.onResume()
    }

    override fun showPermissionRequired() {
        setSloganText(getString(R.string.home_permission_required))
        enjoySloganText.setOnClickListener {
            showNotificationPermissionSettings()
        }
    }

    override fun showNotificationPermissionSettings() {
        startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
    }

    private fun setSloganText(text: String) {
        enjoySloganText.typeface = typeFace

        enjoySloganText.text  = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    override fun showEnjoyAdFree() {
        setSloganText(getString(R.string.home_enjoy_adfree))
        enjoySloganText.setOnClickListener(null)
    }
}