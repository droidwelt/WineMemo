package ru.droidwelt.winememo.additional_UI;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.widget.TextView;

import ru.droidwelt.winememo.R;
import ru.droidwelt.winememo.WMA;
import ru.droidwelt.winememo.database.DB_OpenHelper;
import ru.droidwelt.winememo.main_UI.Main_Activity;

public class About_Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity);
        setTitle(getString(R.string.s_about));
        TextView versionTextView = findViewById(R.id.about_version);
        TextView dbsizeTextView = findViewById(R.id.about_dbsize);

        PackageInfo pinfo;
        try {
            pinfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            String s = "Version " + pinfo.versionName + " build " + pinfo.versionCode;
            versionTextView.setText(s);
        } catch (NameNotFoundException e) {
            versionTextView.setText("");
            e.printStackTrace();
        }

        try {
            DB_OpenHelper dbh = new DB_OpenHelper(WMA.getContext(), WMA.DB_NAME);
            String s = getString(R.string.s_about_records) + " " + Main_Activity.mAdapter.getItemCount() + ";  " + dbh.getDatabaseSize() + " Mb";
            dbsizeTextView.setText(s);
        } catch (Exception e) {
            dbsizeTextView.setText("");
        }


    }
}
