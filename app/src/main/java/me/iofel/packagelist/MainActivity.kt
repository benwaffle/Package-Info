package me.iofel.packagelist

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView

data class AppInfo(val name: String, val pkg: String, val icon: Drawable)

class AppInfoAdapter(context: Context, private val layoutInflater: LayoutInflater) : ArrayAdapter<AppInfo>(context, R.layout.list_item), Filterable {
    val infos = ArrayList<AppInfo>();

    override fun add(info: AppInfo?) {
        if (info != null) {
            infos.add(info)
            super.add(info)
        }
    }

    override fun getView(pos: Int, v: View?, parent: ViewGroup): View {
        val view = v ?: layoutInflater.inflate(R.layout.list_item, parent, false)

        val app = getItem(pos)
        if (app != null) {
            view.findViewById<TextView>(R.id.app_name).text = app.name
            view.findViewById<TextView>(R.id.app_pkg).text = app.pkg
            view.findViewById<ImageView>(R.id.app_icon).setImageDrawable(app.icon)
        }
        return view
    }

    override fun notifyDataSetChanged() {
        setNotifyOnChange(false)
        this.sort { lhs, rhs -> lhs.name.compareTo(rhs.name) }
        setNotifyOnChange(true)
        super.notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return MyFilter()
    }

    inner class MyFilter : Filter() {
        override fun performFiltering(query: CharSequence): FilterResults {
            val matches = infos.filter { info -> info.name.contains(query) || info.pkg.contains(query) }

            val results = FilterResults()
            results.values = matches
            results.count = matches.size
            return results
        }

        override fun publishResults(query: CharSequence, results: FilterResults) {
            clear()
            addAll(results.values as List<AppInfo>)
            notifyDataSetChanged()
        }

    }
}


class MainActivity : AppCompatActivity() {
    var adapter: AppInfoAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.toolbar))

        adapter = AppInfoAdapter(this, this.layoutInflater)
        val lv: ListView = findViewById(R.id.listView)
        lv.adapter = adapter
        lv.setOnItemClickListener { parent, view, position, id ->
            val item = adapter!!.getItem(position)
            val clip = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clip.setPrimaryClip(
                ClipData.newPlainText(item!!.name, item.pkg)
            )
        }

        Thread {
            val applications = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
            for (app in applications) {
                val info = AppInfo(
                    app.loadLabel(packageManager).toString(),
                    app.packageName,
                    app.loadIcon(packageManager),
                )
                runOnUiThread { adapter!!.add(info) }
            }
        }.start()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar, menu)

        (menu.findItem(R.id.search).actionView as SearchView).apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextChange(query: String): Boolean {
                    adapter!!.filter.filter(query)
                    return true
                }

                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }
            })
        }

        return true
    }
}