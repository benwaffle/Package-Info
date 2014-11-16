package com.uubmub.packageinfo;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<String> pkgs = new ArrayList<>();
        for (PackageInfo p : getPackageManager().getInstalledPackages(0))
            pkgs.add(p.packageName);
        ((ListView)findViewById(R.id.listView)).setAdapter(
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, pkgs)
        );
    }
}
