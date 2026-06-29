/*
 * Ad Free
 * Copyright (c) 2017 by abertschi, www.abertschi.ch
 * See the file "LICENSE" for the full license governing this code.
 */

package ch.abertschi.adfree.plugin.localmusic

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import ch.abertschi.adfree.R
import ch.abertschi.adfree.plugin.PluginActivityAction
import ch.abertschi.adfree.util.AppLogger
import ch.abertschi.adfree.util.longToast
import ch.abertschi.adfree.util.runOnUiThread
import ch.abertschi.adfree.view.ViewSettings

/**
 * Created by abertschi on 29.08.17.
 */
class LocalMusicView(val context: Context, val action: PluginActivityAction) : AppLogger {
    private lateinit var viewInstance: View

    private lateinit var presenter: LocalMusicPlugin

    fun onCreate(presenter: LocalMusicPlugin): View {
        this.presenter = presenter

        val tempParent = android.widget.FrameLayout(context)

        val inflater = LayoutInflater.from(context)
        viewInstance = inflater.inflate(R.layout.plugin_localmusic, tempParent, false)

        setupUi()
        return viewInstance
    }

    private fun setupUi() {
        viewInstance.findViewById<View>(R.id.layout_play_until_end).run {
            setOnClickListener { presenter.onPlayUntilEndChanged() }
        }
        viewInstance.findViewById<TextView>(R.id.local_music_title_play_until_end).run {
            setOnClickListener { presenter.onPlayUntilEndChanged() }
            typeface = ViewSettings.instance(context).typeFace
        }
        viewInstance.findViewById<TextView>(R.id.local_music_play_until_end_subtext).run {
            setOnClickListener { presenter.onPlayUntilEndChanged() }
            typeface = ViewSettings.instance(context).typeFace
        }
        viewInstance.findViewById<View>(R.id.local_music_play_until_end_switch)
            .setOnClickListener { presenter.onPlayUntilEndChanged() }

        viewInstance.findViewById<View>(R.id.layout_loop)
            .setOnClickListener { presenter.onLoopPlaybackChanged() }
        viewInstance.findViewById<TextView>(R.id.local_music_title_loop).run {
            setOnClickListener { presenter.onLoopPlaybackChanged() }
            typeface = ViewSettings.instance(context).typeFace
        }
        viewInstance.findViewById<TextView>(R.id.local_music_loop_subtext).run {
            setOnClickListener { presenter.onLoopPlaybackChanged() }
            typeface = ViewSettings.instance(context).typeFace
        }
        viewInstance.findViewById<View>(R.id.local_music_loop_switch)
            .setOnClickListener { presenter.onLoopPlaybackChanged() }
        viewInstance.findViewById<View>(R.id.layout_configure_audio)
            .setOnClickListener { presenter.onConfigureAudioVolume() }
        viewInstance.findViewById<TextView>(R.id.configure_audio_title).run {
            setOnClickListener { presenter.onConfigureAudioVolume() }
            typeface = ViewSettings.instance(context).typeFace
        }
        viewInstance.findViewById<TextView>(R.id.configure_audio_subtitle).run {
            setOnClickListener { presenter.onConfigureAudioVolume() }
            typeface = ViewSettings.instance(context).typeFace
        }
        viewInstance.findViewById<View>(R.id.layout_music_dir).setOnClickListener {
            presenter.onChooseDirectory()
        }
        viewInstance.findViewById<TextView>(R.id.music_dir_title).run {
            setOnClickListener { presenter.onChooseDirectory() }
            typeface = ViewSettings.instance(context).typeFace
        }
        viewInstance.findViewById<TextView>(R.id.music_dir_subtitle).run {
            setOnClickListener { presenter.onChooseDirectory() }
            typeface = ViewSettings.instance(context).typeFace
        }
        action.addOnActivityResult { requestCode, resultCode, data ->
            presenter.onActivityResult(requestCode, resultCode, data)
        }
    }

    fun showPlayUntilEndEnabled(e: Boolean) {
        viewInstance.findViewById<SwitchCompat>(R.id.local_music_play_until_end_switch).isChecked =
            e
    }

    fun showLoopEnabled(e: Boolean) {
        viewInstance.findViewById<SwitchCompat>(R.id.local_music_loop_switch).isChecked = e
    }

    private fun showDirectoryChooser() {
        val i = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        i.addCategory(Intent.CATEGORY_DEFAULT)
        val chooser = Intent.createChooser(i, "Choose directory")
        startActivityForResult(chooser, LocalMusicPlugin.PICK_DIRECTORY, null)
    }

    fun showFolderSelectionDialog() {
        showDirectoryChooser()
    }

    fun startActivityForResult(intent: Intent?, requestCode: Int, options: Bundle?) {
        action.startActivityForResult(intent, requestCode, options)
    }

    fun showErrorInChoosingDirectory(hint: String = "") {
        runOnUiThread {
            context.longToast(context.getString(R.string.whoops_error_chosen_directory, hint))
        }
    }

    fun showNoAudioTracksFoundMessage() {
        runOnUiThread {
            context.longToast(context.getString(R.string.error_no_music_found_directory))
        }
    }

    fun showAudioError() {
        runOnUiThread {
            context.longToast(context.getString(R.string.error_audio))
        }
    }

    fun hideLoopMusic(hide: Boolean) {
        viewInstance.findViewById<View>(R.id.layout_loop).visibility =
            if (hide) View.GONE else View.VISIBLE
    }

    fun showAudioDirectoryPath(s: String) {
        viewInstance.findViewById<TextView>(R.id.music_dir_subtitle).text = s
    }
}