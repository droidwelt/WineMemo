package ru.droidwelt.winememo.additional_UI;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

import ru.droidwelt.winememo.R;
import ru.droidwelt.winememo.WMA;
import ru.droidwelt.winememo.database.DB_OpenHelper_Dict;
import ru.droidwelt.winememo.main_UI.Main_Activity;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class Start_Activity extends AppCompatActivity {

    private AlertDialog dlgLoadDict;
    private final Timer timer = new Timer();
    private boolean inProgress = false;
    private static final int RequestPermissionCode = 1;


    private boolean checkMyPremissoins() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                !((ContextCompat.checkSelfPermission(this, CAMERA) != PackageManager.PERMISSION_GRANTED) ||
                        (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ||
                        (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean CameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean ReadExternalStoragePermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean WriteExternalStoragePermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    if (CameraPermission & ReadExternalStoragePermission & WriteExternalStoragePermission) {
                        myStart();
                    } else {
                        finish();
                    }
                }
                break;
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);
        WMA.animateStart(Start_Activity.this);

        if (checkMyPremissoins()) {
            myStart();
        } else {
            ActivityCompat.requestPermissions(this, new String[]
                    {CAMERA, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, RequestPermissionCode);
        }
    }


    private void myStart() {
        WMA.startDbHelper();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(Start_Activity.this);
        String sLastRunningBuild = sp.getString("AWine_LastRunningBuild", "0");

        PackageInfo pinfo;
        String sCurrentRunningBuild = "";
        try {
            pinfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            sCurrentRunningBuild = Integer.toString(pinfo.versionCode);
        } catch (NameNotFoundException ignored) {
        }

        if (!sLastRunningBuild.equals(sCurrentRunningBuild)) {
            new DB_OpenHelper_Dict(this, "");
            loadDictionatyTask.execute();
            AlertDialog.Builder builder = new AlertDialog.Builder(Start_Activity.this);
            builder.setTitle(R.string.s_refresh_dictionary);
            builder.setMessage(R.string.s_wait);
            builder.setCancelable(true);
            dlgLoadDict = builder.create();
            dlgLoadDict.show();
            Editor editor = sp.edit();
            editor.putString("AWine_LastRunningBuild", sCurrentRunningBuild);
            editor.apply();
        } else {
            timer.schedule(new TimerTask() {
                public void run() {
                    timer.purge();
                    timer.cancel();
                    Intent intent = new Intent(Start_Activity.this, Main_Activity.class);
                    startActivity(intent);
                    finish();
                    WMA.animateStart(Start_Activity.this);
                }
            }, 5000);
        }

        ImageView iv_start = findViewById(R.id.start_image);
        iv_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.purge();
                timer.cancel();
                Intent intent = new Intent(Start_Activity.this, Main_Activity.class);
                startActivity(intent);
                WMA.animateStart(Start_Activity.this);
                finish();
            }
        });
    }


    @SuppressLint("StaticFieldLeak")
    private final
    AsyncTask<Long, Object, Object> loadDictionatyTask = new AsyncTask<Long, Object, Object>() {
        @Override
        protected Object doInBackground(Long... params) {
            fillDictFormEtalon();
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            try {
                dlgLoadDict.dismiss();
            } catch (Exception ignored) {
            }
        }
    };

    // Наполнение таблицы DICT данными из дополнительной базы
    // -------------------------------
    private void fillDictFormEtalon() {
        // Log.i("XXX", "fillDictFormEtalon");
        timer.purge();
        timer.cancel();
        inProgress = true;
        DB_OpenHelper_Dict dbh_d;
        dbh_d = new DB_OpenHelper_Dict(Start_Activity.this, WMA.DB_NAMEMODEL);

        // WMA.getDatabase().delete("dict", null, null);
        String sql = "drop  table if exists dict; ";
        WMA.getDatabase().execSQL(sql);
        sql = "CREATE TABLE dict	(_id integer primary key autoincrement,"
                + "tablename TEXT, name TEXT, lang TEXT);";
        WMA.getDatabase().execSQL(sql);
        // WMA.getDatabase().execSQL("VACUUM;");

        sql = "select _id,tablename,name,lang from dict";
        dbh_d.openDataBase_dict();
        Cursor rdcursor = dbh_d.getDatabase_dict().rawQuery(sql, null);
        rdcursor.moveToFirst();
        int i_tablename = rdcursor.getColumnIndex("tablename");
        int i_name = rdcursor.getColumnIndex("name");
        int i_lang = rdcursor.getColumnIndex("lang");
        String tablename, name, lang;
        ContentValues newRecord = new ContentValues();

        if (rdcursor.getCount() > 0) {
            do {
                tablename = rdcursor.getString(i_tablename);
                name = rdcursor.getString(i_name);
                lang = rdcursor.getString(i_lang);
                newRecord.put("tablename", tablename);
                newRecord.put("name", name);
                newRecord.put("lang", lang);
                WMA.getDatabase().insert("dict", null, newRecord);
                // Log.i("XXX", " data="+tablename +";"+ name);
            } while (rdcursor.moveToNext());
        }
        rdcursor.close();
        dbh_d.closeDatabase_dict();
        inProgress = false;

        Intent intent = new Intent(Start_Activity.this, Main_Activity.class);
        startActivity(intent);
        finish();
        WMA.animateStart(Start_Activity.this);
    }

    @Override
    public void onBackPressed() {
        if (!inProgress) {
            timer.purge();
            timer.cancel();
            finish();
            WMA.animateFinish(Start_Activity.this);
        }
    }

}
