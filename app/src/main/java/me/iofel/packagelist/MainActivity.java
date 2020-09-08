package me.iofel.packagelist;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

class AppInfo {
    String name, pname;
    Drawable icon;
}

public class MainActivity extends AppCompatActivity {
    AppInfoAdapter adapter;
    ListView lv;

    class AppInfoAdapter extends ArrayAdapter<AppInfo> {
        public AppInfoAdapter() {
            super(MainActivity.this, R.layout.list_item);
        }

        @Override
        public View getView(int pos, View v, ViewGroup parent) {
            if (v == null) {
                v = MainActivity.this.getLayoutInflater().inflate(R.layout.list_item, parent, false);
            }

            AppInfo app = getItem(pos);
            v.<TextView>findViewById(R.id.app_name).setText(app.name);
            v.<TextView>findViewById(R.id.app_pkg).setText(app.pname);
            v.<ImageView>findViewById(R.id.app_icon).setImageDrawable(app.icon);
            return v;
        }

        @Override
        public void notifyDataSetChanged() {
            this.setNotifyOnChange(false);

            this.sort((lhs, rhs) -> lhs.name.compareTo(rhs.name));

            this.setNotifyOnChange(true);

            super.notifyDataSetChanged();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_main);

        adapter = new AppInfoAdapter();
        lv = findViewById(R.id.listView);
        lv.setAdapter(adapter);
        new Thread(() -> {
            List<ApplicationInfo> applications = getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);

            for (ApplicationInfo app : applications) {
                final AppInfo info = new AppInfo();
                info.name = app.loadLabel(getPackageManager()).toString();
                info.pname = app.packageName;
                info.icon = app.loadIcon(getPackageManager());
                runOnUiThread(() -> adapter.add(info));
            }
        }).start();
    }

}

