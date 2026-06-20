/*
 * Ad Free
 * Copyright (c) 2017 by abertschi, www.abertschi.ch
 * See the file "LICENSE" for the full license governing this code.
 */

package ch.abertschi.adfree.crashhandler

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_LEGACY
import ch.abertschi.adfree.R
import ch.abertschi.adfree.util.AppLogger
import ch.abertschi.adfree.util.warn

class SendCrashReportActivity : AppCompatActivity(), View.OnClickListener, AppLogger, CrashReportView {

    companion object {
        const val EXTRA_LOGFILE = "ch.abertschi.adfree.extra.logfile"
        const val EXTRA_SUMMARY = "ch.abertschi.adfree.extra.summary"
        const val MAIL_ADDRESS = "apps@abertschi.ch"
        const val SUBJECT = "[ad-free-crash-report]"
    }

    private var logfile: String? = null
    private var summary: String? = null

    private lateinit var presenter: CrashReportPresenter

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = CrashReportPresenter(this, applicationContext)

        try {
            logfile = intent?.extras?.getString(EXTRA_LOGFILE)
            summary = intent?.extras?.getString(EXTRA_SUMMARY) ?: ""
            setupUI()
        } catch (e: Exception) {
            warn(e)
            showError("Error: $e")
        }
    }

    private fun setupUI() {
        setContentView(R.layout.crash_view)
        setFinishOnTouchOutside(false)
        val v = findViewById<View>(R.id.crash_container)
        v.setOnClickListener(this)

        val typeFace = Typeface.createFromAsset(baseContext.assets, "fonts/Raleway-ExtraLight.ttf")

        val title = findViewById<TextView>(R.id.crash_Title)
        title.typeface = typeFace
        title.setOnClickListener(this)

        val textQuote = getString(R.string.crash_title_quote)
        title.text = HtmlCompat.fromHtml(textQuote, FROM_HTML_MODE_LEGACY)

        val subtitle = findViewById<TextView>(R.id.debugSubtitle)
        subtitle.typeface = typeFace
        subtitle.setOnClickListener(this)

        val textSubtitle = getString(R.string.crash_subtitle_text)
        subtitle.text = HtmlCompat.fromHtml(textSubtitle, FROM_HTML_MODE_LEGACY)
    }

    override fun launchEmailClient(message: String) {
        val sendIntent = Intent(Intent.ACTION_SEND)
        sendIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(MAIL_ADDRESS))
        sendIntent.putExtra(Intent.EXTRA_TEXT, message)
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, SUBJECT)
        sendIntent.type = "text/plain"
        this.applicationContext
            .startActivity(Intent.createChooser(sendIntent, "Choose an Email client"))
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onClick(v: View) {
        presenter.onSendCrashReportClicked(logfile, summary)
    }
}