package ch.abertschi.adfree.view.mod

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.abertschi.adfree.R
import ch.abertschi.adfree.detector.AdDetectable
import ch.abertschi.adfree.util.AppLogger
import ch.abertschi.adfree.util.info
import ch.abertschi.adfree.util.longToast

class ActiveDetectorActivity : AppCompatActivity(), AppLogger {

    private lateinit var detectorRecyclerView: RecyclerView
    private lateinit var detectorViewAdapter: RecyclerView.Adapter<*>
    private lateinit var detectorViewManager: RecyclerView.LayoutManager

    private lateinit var presenter: ActiveDetectorPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mod_active_detectors)

        val textView = findViewById<TextView>(R.id.detectors_activity_title)

        presenter = ActiveDetectorPresenter(this)

        val category: String = intent.extras?.getString(CategoriesPresenter.BUNDLE_CATEGORY_KEY)
            ?: throw IllegalStateException("must set category")

        val text = "fine-tune detectors for <font color=#FFFFFF>$category</font>."

        textView.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY)
        } else {
            @Suppress("DEPRECATION")
            Html.fromHtml(text)
        }

        detectorViewManager = LinearLayoutManager(this)
        detectorViewAdapter = DetectorAdapter(presenter.getDetectors(category), presenter)
        detectorRecyclerView = findViewById<RecyclerView>(R.id.detector_recycle_view).apply {
            layoutManager = detectorViewManager
            adapter = detectorViewAdapter
        }
    }

    fun showInfo(info: String) {
        longToast(info)
    }
}

class DetectorAdapter(
    private val detectors: List<AdDetectable>,
    private val presenter: ActiveDetectorPresenter
) : RecyclerView.Adapter<DetectorAdapter.MyViewHolder>(), AppLogger {

    class MyViewHolder(
        val view: View,
        val title: TextView,
        val subtitle: TextView,
        val switch: SwitchCompat,
        val sepView: View
    ) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.mod_active_detectors_view_element, parent, false)

        val title = view.findViewById<TextView>(R.id.det_title)
        val subtitle = view.findViewById<TextView>(R.id.det_subtitle)
        val switch = view.findViewById<SwitchCompat>(R.id.det_switch)
        val sep = view.findViewById<View>(R.id.mod_det_separation)

        return MyViewHolder(view, title, subtitle, switch, sep)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val rawTitle = detectors[position].getMeta().title
        holder.title.text = holder.title.context.getString(R.string.detector_title_format, rawTitle)

        holder.subtitle.text = detectors[position].getMeta().description
        holder.switch.isChecked = presenter.isEnabled(detectors[position])

        holder.title.setOnClickListener {
            holder.switch.toggle()
        }
        holder.subtitle.setOnClickListener {
            holder.switch.toggle()
        }
        holder.view.setOnClickListener {
            holder.switch.toggle()
        }

        holder.switch.setOnCheckedChangeListener { _, isChecked ->
            presenter.onDetectorToggled(isChecked, detectors[position])
            info(detectors[position].javaClass.canonicalName)
        }
        holder.sepView.visibility =
            if (position == detectors.size - 1) View.INVISIBLE else View.VISIBLE
    }

    override fun getItemCount() = detectors.size
}