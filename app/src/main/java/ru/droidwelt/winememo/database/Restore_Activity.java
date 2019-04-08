package ru.droidwelt.winememo.database;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.droidwelt.winememo.R;
import ru.droidwelt.winememo.WMA;
import ru.droidwelt.winememo.main_UI.Main_Activity;

@SuppressLint("StaticFieldLeak")
public class Restore_Activity extends Activity {

    private static TextView mEditText;
    private static Button btn_Cancel;
    private static Button btn_Ok;
    private static final List<String> filenames = new ArrayList<>();
    private static String fn ="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restore_activity);
        setTitle(getString(R.string.s_restoreDB));
        fn="";

        mEditText = findViewById(R.id.restore_filename);
        btn_Ok = findViewById(R.id.button_Ok);
        btn_Ok = findViewById(R.id.button_Ok);
        btn_Cancel = findViewById(R.id.button_Cancel);
        btn_Ok.setOnClickListener(oclBtnOk);
        btn_Cancel.setOnClickListener(oclBtnOk);
        ListView lv = findViewById(R.id.download_listView);

        filenames.clear();
        File dir = new File(WMA.DB_DOWNLOAD);
        if (dir.exists()) {
            for (File file : dir.listFiles()) {
                if ((file.isFile()) & (file.getName().contains(".db3")))
                    filenames.add(file.getName());
            }
        } else {
            btn_Ok.setEnabled(false);
            filenames.add(getString(R.string.s_no_folder_to_load) + " " + WMA.DB_DOWNLOAD);
        }
        Collections.sort(filenames);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filenames);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(getfilename_Listener);
    }

    private final AdapterView.OnItemClickListener getfilename_Listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            fn = filenames.get(position);
            mEditText.setText(fn);
            btn_Ok.setEnabled(true);
        }
    };


    // ----------------слушатель нажатия на кнопку
    private final View.OnClickListener oclBtnOk = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {

                case R.id.button_Cancel:
                    finish();
                    break;

                case R.id.button_Ok:
                    if (!fn.isEmpty()) {
                        btn_Ok.setEnabled(false);
                        btn_Cancel.setEnabled(false);
                        ProgressBar   pb = findViewById(R.id.wait_operation_pb);
                        pb.setIndeterminate(true);
                        restoreDB_exec(fn);
                    }
                    break;

                default:
                    break;
            }
        }
    };


    private void restoreDB_exec(final String DB_NAMEFROM) {
        class WorkingThread extends Thread {
            @Override
            public void run() {
                super.run();

                try {
                    String externalFileName = WMA.DB_DOWNLOAD + DB_NAMEFROM;
                    InputStream externalDbStream = new FileInputStream(externalFileName);
                    String outFileName = WMA.DB_PATH + WMA.DB_NAME;
                    OutputStream localDbStream = new FileOutputStream(outFileName);
                    byte[] buffer = new byte[1024 * 100];
                    int bytesRead;

                    while ((bytesRead = externalDbStream.read(buffer)) > 0) {
                        // Log.i("XXX", "externalDbStream.read");
                        localDbStream.write(buffer, 0, bytesRead);
                        // Log.i("XXX", "localDbStream.write");
                        db_restore_Handler.sendEmptyMessage(1);
                    }
                    localDbStream.close();
                    externalDbStream.close();
                    db_restore_Handler.sendEmptyMessage(3);
                    // Log.i("XXX", "Restore_DB - sendEmptyMessage(3)");

                } catch (IOException e) {
                    // Log.i("XXX", "Restore_DB Error ");
                    db_restore_Handler.sendEmptyMessage(3);
                }
            }
        }
        new WorkingThread().start();
    }

    @SuppressLint("HandlerLeak")
    private final
    Handler db_restore_Handler = new Handler() {
        double f = 0.0d;

        @Override
        @SuppressLint({"DefaultLocale", "Assert"})
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case 1:
                    f = (float) (f + 0.1);
                    String S = getString(R.string.s_loaded) +" "+ String.format("%.1f", f) + " Mb";
                    btn_Ok.setText(S);
                    break;

                case 3:
                    f = 0.0d;
                    finish();
                    Main_Activity.refreshData();
                    break;

                default:
                    assert false;
                    break;
            }
        }
    };


}
