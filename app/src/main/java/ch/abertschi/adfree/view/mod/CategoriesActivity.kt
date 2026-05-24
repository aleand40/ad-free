package ch.abertschi.adfree.view.mod

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.abertschi.adfree.R
import ch.abertschi.adfree.util.AppLogger
import ch.abertschi.adfree.util.longToast

class CategoriesActivity : AppCompatActivity(), AppLogger {
    private lateinit var categoriesRecyclerView: RecyclerView
    private lateinit var categoriesViewAdapter: RecyclerView.Adapter<*>
    private lateinit var categoriesViewManager: RecyclerView.LayoutManager

    private lateinit var presenter: CategoriesPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mod_active_detectors)

        val textView = findViewById<TextView>(R.id.detectors_activity_title)
        val text = "detectors <font color=#FFFFFF>find ads</font>. choose what's active."

        textView.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY)
        } else {
            @Suppress("DEPRECATION")
            Html.fromHtml(text)
        }

        presenter = CategoriesPresenter(this)

        textView.setOnClickListener {
            presenter.onTabTitle()
        }

        initRecycleView()
    }

    private fun initRecycleView() {
        categoriesViewManager = LinearLayoutManager(this)
        categoriesViewAdapter = CategoryAdapter(presenter.getCategories(), presenter)
        categoriesRecyclerView = findViewById<RecyclerView>(R.id.detector_recycle_view).apply {
            layoutManager = categoriesViewManager
            adapter = categoriesViewAdapter
        }
    }

    fun hideEnabledDebug() {
        longToast("So Long, and Thanks for All the Fish")
        initRecycleView()
    }

    fun showEnabledDebug() {
        longToast("With great power comes great responsibility")
        initRecycleView()
    }
}

class CategoryAdapter(
    private val categories: List<String>,
    private val presenter: CategoriesPresenter
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>(), AppLogger {

    class CategoryViewHolder(
        val view: View,
        val title: TextView,
        val subtitle: TextView,
        val sepView: View
    ) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.mod_active_detectors_view_element, parent, false)

        val title = view.findViewById<TextView>(R.id.det_title)
        val subtitle = view.findViewById<TextView>(R.id.det_subtitle)
        val sep = view.findViewById<View>(R.id.mod_det_separation)
        val switch = view.findViewById<SwitchCompat>(R.id.det_switch)

        // This layout element is reused; the switch is not needed for categories view
        switch.visibility = View.GONE
        return CategoryViewHolder(view, title, subtitle, sep)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val context = holder.view.context
        val categoryName = categories[position]

        holder.view.setOnClickListener {
            presenter.onCategorySelected(categoryName)
        }
        holder.title.setOnClickListener {
            presenter.onCategorySelected(categoryName)
        }
        holder.subtitle.setOnClickListener {
            presenter.onCategorySelected(categoryName)
        }

        holder.title.text = context.getString(R.string.detector_title_format, categoryName)
        holder.subtitle.text = context.getString(R.string.category_subtitle_format, categoryName)

        holder.sepView.visibility =
            if (position == categories.size - 1) View.INVISIBLE else View.VISIBLE
    }

    override fun getItemCount() = categories.size
}