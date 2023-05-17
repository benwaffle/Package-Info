package me.iofel.packagelist

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

data class AppInfo(val name: String, val pkg: String, val icon: Drawable)

class AppInfoAdapter(context: Context, private val layoutInflater: LayoutInflater) : ArrayAdapter<AppInfo>(context, R.layout.list_item) {
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
}


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val adapter = AppInfoAdapter(this, this.layoutInflater)
        val lv: ListView = findViewById(R.id.listView)
        lv.adapter = adapter

        Thread {
            val applications = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
            for (app in applications) {
                val info = AppInfo(
                    app.loadLabel(packageManager).toString(),
                    app.packageName,
                    app.loadIcon(packageManager),
                )
                runOnUiThread { adapter.add(info) }
            }
        }.start()
    }
}