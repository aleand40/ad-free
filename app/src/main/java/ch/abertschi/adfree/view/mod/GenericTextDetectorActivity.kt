package ch.abertschi.adfree.view.mod

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import ch.abertschi.adfree.R
import ch.abertschi.adfree.model.TextRepositoryData
import org.jetbrains.anko.*


class GenericTextDetectorActivity : AppCompatActivity(), AnkoLogger {
    private lateinit var presenter: GenericTextDetectorPresenter
    private lateinit var viewAdapter: DetectorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mod_text_detector)
        val textView = findViewById<TextView>(R.id.textdetector_activity_title)
        val text =
            "the <font color=#FFFFFF>text detector</font> flags a notification based on the presence of text."

        // Handle HTML parsing depending on the Android version to avoid deprecation warnings
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            textView.text = Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY)
        } else {
            @Suppress("DEPRECATION")
            textView.text = Html.fromHtml(text)
        }

        findViewById<ScrollView>(R.id.mod_text_scroll).scrollTo(0, 0)

        presenter = GenericTextDetectorPresenter(this, this)


        viewAdapter = DetectorAdapter(presenter.getData(), presenter)
        findViewById<TextView>(R.id.det_title_text).setOnClickListener {
            presenter.addNewEntry()
        }
        findViewById<TextView>(R.id.det_subtitle_text).setOnClickListener {
            presenter.addNewEntry()
        }
        findViewById<TextView>(R.id.det_title_help).setOnClickListener {
            presenter.browseHelp()
        }
        findViewById<TextView>(R.id.det_subtitle_help).setOnClickListener {
            presenter.browseHelp()
        }

        val recyclerView = findViewById<RecyclerView>(R.id.detector_recycle_view)
        recyclerView.layoutManager = android.support.v7.widget.LinearLayoutManager(this)
        recyclerView.adapter = viewAdapter
    }

    @android.annotation.SuppressLint("InflateParams")
    fun showOptionDialog(entry: TextRepositoryData) {
        val d = AlertDialog.Builder(this)
            .setTitle("Options")
            // Passing null is required here because the dialog window doesn't exist yet
            .setView(LayoutInflater.from(this).inflate(R.layout.delete_dialog, null))
            .setPositiveButton(android.R.string.yes) { _, _ ->
                presenter.deleteEntry(entry)
            }
            .setNegativeButton(android.R.string.no) { dialog, _ ->
                dialog.dismiss()
            }
            .setOnDismissListener {
                it.dismiss()
            }
            .create()

        d.window?.setBackgroundDrawableResource(R.color.colorBackground)
        d.show()
    }

    fun insertData() {
        val insertIndex = viewAdapter.itemCount - 1

        if (insertIndex >= 0) {
            viewAdapter.notifyItemInserted(insertIndex)
        }
    }

    private class DetectorAdapter(
        private val data: List<TextRepositoryData>,
        private val presenter: GenericTextDetectorPresenter
    ) :
        RecyclerView.Adapter<DetectorAdapter.MyViewHolder>(), AnkoLogger {

        class MyViewHolder(
            view: View,
            val title: EditText,
            val subtitle: EditText,
            val more: ImageView,
            val sepView: View
        ) : RecyclerView.ViewHolder(view)

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): MyViewHolder {

            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.mod_text_detector_view_element, parent, false)
            val title: EditText = view.findViewById(R.id.det_title)
            val subtitle: EditText = view.findViewById(R.id.det_subtitle)
            val more: ImageView = view.findViewById(R.id.det_more)
            val sep = view.findViewById<View>(R.id.mod_det_separation)
            return MyViewHolder(view, title, subtitle, more, sep)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            // Use 'position' only for the immediate visual binding
            val entry = data[position]

            holder.title.setText(entry.packageName)
            holder.subtitle.setText(entry.content.joinToString(separator = "\n"))
            holder.sepView.visibility =
                if (position == data.size - 1) View.INVISIBLE else View.VISIBLE

            holder.more.setOnClickListener {
                // Check the real position at the exact moment of the click
                val currentPos = holder.adapterPosition
                if (currentPos != RecyclerView.NO_POSITION) {
                    presenter.onMoreClicked(data[currentPos])
                }
            }

            holder.title.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable) {
                    val currentPos = holder.adapterPosition
                    if (currentPos != RecyclerView.NO_POSITION) {
                        val currentEntry = data[currentPos]
                        currentEntry.packageName = s.toString()
                        presenter.updateEntry(currentEntry)
                    }
                }
            })

            holder.subtitle.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable) {
                    val currentPos = holder.adapterPosition
                    if (currentPos != RecyclerView.NO_POSITION) {
                        val currentEntry = data[currentPos]
                        currentEntry.content = s.toString().split("\n")
                        presenter.updateEntry(currentEntry)
                    }
                }
            })
        }

        override fun getItemCount() = data.size
    }

}