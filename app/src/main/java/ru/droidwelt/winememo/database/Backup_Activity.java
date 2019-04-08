package ru.droidwelt.winememo.database;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import ru.droidwelt.winememo.R;
import ru.droidwelt.winememo.WMA;

@SuppressLint("StaticFieldLeak")
public class Backup_Activity extends Activity {


    private static EditText mEditText;
    private static Button btn_Ok;
    private static Button btn_Cancel;
    private static String fn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.backup_activity);
        setTitle(getString(R.string.s_backupDB));

        mEditText = findViewById(R.id.backup_filename);
        if (savedInstanceState != null)
            mEditText.setText(fn);

        btn_Ok = findViewById(R.id.button_Ok);
        btn_Cancel = findViewById(R.id.button_Cancel);
        btn_Ok.setOnClickListener(oclBtnOk);
        btn_Cancel.setOnClickListener(oclBtnOk);
    }

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
                    btn_Ok.setEnabled(false);
                    btn_Cancel.setEnabled(false);
                    ProgressBar   pb = findViewById(R.id.wait_operation_pb);
                    pb.setIndeterminate(true);
                    fn = mEditText.getText().toString();
                    if (fn.equals(""))
                        fn = "Winememo_DB " + DateFormat.format("yyyy-MM-dd hh-mm-ss", new Date());
                    if (!fn.endsWith(".db3")) {
                        fn = fn.concat(".db3");
                        mEditText.setText(fn);
                    }

                    backupDB_exec(fn);
                    break;

                default:
                    break;
            }
        }
    };


    private void backupDB_exec(final String DBN) {
        File PATH_TO = new File(WMA.DB_DOWNLOAD); // куда

        if (!(PATH_TO.isDirectory() && PATH_TO.canExecute() && PATH_TO.canRead() && PATH_TO.canWrite())) {
            if (!PATH_TO.mkdir()) return;
        }

        if (!(PATH_TO.isDirectory() && PATH_TO.canExecute() && PATH_TO.canRead() && PATH_TO.canWrite())) {
            WMA.DisplayToastError(getResources().getString(R.string.s_access_folder_error) + PATH_TO);
        } else {

            final String fn_from = WMA.DB_PATH + WMA.DB_NAME;
            final String fn_to = PATH_TO + "//" + DBN;

            class WorkingThread extends Thread {
                @Override
                public void run() {
                    super.run();
                    try {
                        final InputStream DbStream_from = new FileInputStream(fn_from);
                        final OutputStream DbStream_to = new FileOutputStream(fn_to);
                        byte[] buffer = new byte[1024 * 100];
                        int bytesRead;

                        while ((bytesRead = DbStream_from.read(buffer)) > 0) {
                            DbStream_to.write(buffer, 0, bytesRead);
                            db_backup_Handler.sendEmptyMessage(1);
                        }

                        DbStream_to.close();
                        DbStream_from.close();
                        db_backup_Handler.sendEmptyMessage(3);
                        //Log.i("XXX", "База выгружена в " + fn_to);
                    } catch (IOException e) {
                        //Log.i("XXX", "IOException ");
                        e.printStackTrace();
                    }
                }
            }
            new WorkingThread().start();
        }
    }


    @SuppressLint("HandlerLeak")
    private final
    Handler db_backup_Handler = new Handler() {
        double f = 0.0d;

        @Override
        @SuppressLint({"DefaultLocale", "Assert"})
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case 1:
                    f = (float) (f + 0.1);
                    String S = getString(R.string.s_unloaded )+ "  "+String.format("%.1f", f)+" Mb";
                    btn_Ok.setText(S);
                    break;

                case 3:
                    f = 0.0d;
                    finish();
                    break;

                default:
                    assert false;
                    break;
            }
        }
    };


}
