package ru.droidwelt.winememo.main_UI;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ru.droidwelt.winememo.R;
import ru.droidwelt.winememo.WMA;
import ru.droidwelt.winememo.additional_UI.About_Activity;
import ru.droidwelt.winememo.additional_UI.Diagramm_Activity;
import ru.droidwelt.winememo.additional_UI.Help_Activity;
import ru.droidwelt.winememo.additional_UI.Pref_Activity;
import ru.droidwelt.winememo.database.Backup_Activity;
import ru.droidwelt.winememo.database.Restore_Activity;

@SuppressLint("StaticFieldLeak")
public class Main_Activity extends AppCompatActivity  {

    static final int REC_MODIFIED = 201;
    private static final int CONT_INSERTED = 202;
    private static final int CONT_EDIT = 301;
    private static final int MAS_IMPORT = 305;

    private AlertDialog.Builder adb_order;
    private String[] sort_list_field;
    private String[] sort_list_name;
    private static String sort_field = "";

    private static String myfilter = "";
    private static EditText et_msa_text_filter;

    private AlertDialog.Builder adb_import;
    private static String fn_import = "";
    private final List<String> filenames_display = new ArrayList<>();

    static List<MainDataStructure> list_msa;
    private static RecyclerView mRecyclerView;
    public static RecyclerView.Adapter mAdapter;


    private static Main_Activity instance;


    public static synchronized Main_Activity getInstance() {
        return instance;
    }


    @SuppressLint("ResourceAsColor")    // TODO
    private int findRecordByScanCode(String scan_code) {
        for (int i = 0; i < list_msa.size(); i++) {
            MainDataStructure mes;
            mes = list_msa.get(i);
            if ((mes.barcode != null) & (scan_code.equals(mes.barcode))) {
                mRecyclerView.scrollToPosition(i);
                WMA.MSA_POS = i;
                WMA.MSA_ID = Integer.parseInt(mes._id);
                return Integer.parseInt(mes._id);
            }
        }
        return -1;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.main_activity);
        //  context = getContext();
        instance = this;

        Toolbar tb_main = findViewById(R.id.tb_main);
        setSupportActionBar(tb_main);

        list_msa = new ArrayList<>();

        sort_list_field = getResources().getStringArray(R.array.sort_list_field);
        sort_list_name = getResources().getStringArray(R.array.sort_list_name);
        if (sort_field.equals(""))
            sort_field = sort_list_field[11];

        ImageButton ib_filrerclear = findViewById(R.id.ib_filrerclear);
        ib_filrerclear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myfilter = "";
                et_msa_text_filter.setText("");
            }
        });

        et_msa_text_filter = findViewById(R.id.et_filter);
        et_msa_text_filter.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                myfilter = (s.toString()).replace(" ", "");
                refreshData();
            }
        });

        LinearLayout ly_msa_rv = findViewById(R.id.ly_msa_rv);
        ly_msa_rv.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        ly_msa_rv.setBackgroundResource(R.drawable.ic_mono);
        ly_msa_rv.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        mRecyclerView = findViewById(R.id.rv_msamain);
        mRecyclerView.setHapticFeedbackEnabled(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        //   mLayoutManager = new GridLayoutManager(this, 1);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MainRecyclerAdapter();
        mRecyclerView.setAdapter(mAdapter);

        et_msa_text_filter.setText(myfilter);
        //   getFilterdRecords();

        adb_order = new AlertDialog.Builder(this, R.style.AlertDialogColorButton);
        adb_import = new AlertDialog.Builder(this, R.style.AlertDialogColorButton);
    }


    @SuppressLint("DefaultLocale")
    private static void getFilterdRecords() {
        String sqlitefilter = "'%" + myfilter.toUpperCase().trim() + "%'";
        String sql;
        if (!WMA.get_choice_allfield()) {
            sql = String.format("SELECT  _id, name, barcode, tip, country, region, consist,"
                            + " firma, god, emk, alk, sug, rating, price, dates, code from contacts "
                            + " where nameup like %1$s " + " order by %2$s ",
                    sqlitefilter, sort_field);
        } else {
            sql = String.format("SELECT  _id, name, barcode, tip, country, region, consist,"
                            + " firma, god, emk, alk, sug, rating, price, dates, code from contacts "
                            + " where searchup like %1$s " + " order by %2$s ",
                    sqlitefilter, sort_field);
        }
        // Log.i("SQL", sql);
        Cursor c = WMA.getDatabase().rawQuery(sql, null);
        c.moveToFirst();
        int index__id = c.getColumnIndex("_id");
        int index_barcode = c.getColumnIndex("barcode");
        int index_name = c.getColumnIndex("name");
        int index_tip = c.getColumnIndex("tip");
        int index_country = c.getColumnIndex("country");
        int index_region = c.getColumnIndex("region");
        int index_consist = c.getColumnIndex("consist");
        int index_firma = c.getColumnIndex("firma");
        int index_god = c.getColumnIndex("god");
        int index_emk = c.getColumnIndex("emk");
        int index_alk = c.getColumnIndex("alk");
        int index_sug = c.getColumnIndex("sug");
        int index_rating = c.getColumnIndex("rating");
        int index_price = c.getColumnIndex("price");
        int index_dates = c.getColumnIndex("dates");
        int index_code = c.getColumnIndex("code");

        list_msa.clear();
        while (!c.isAfterLast()) {
            MainDataStructure mes = new MainDataStructure();
            mes._id = c.getString(index__id);
            mes.barcode = c.getString(index_barcode);
            mes.name = c.getString(index_name);
            mes.tip = c.getString(index_tip);
            mes.country = c.getString(index_country);
            mes.region = c.getString(index_region);
            mes.consist = c.getString(index_consist);
            mes.firma = c.getString(index_firma);
            mes.god = c.getString(index_god);
            mes.emk = c.getString(index_emk);
            mes.alk = c.getString(index_alk);
            mes.sug = c.getString(index_sug);
            mes.rating = c.getString(index_rating);
            mes.price = c.getString(index_price);
            mes.dates = c.getString(index_dates);
            mes.code = c.getString(index_code);
            mes.haspict = false;
            list_msa.add(mes);
            c.moveToNext();
        }

        c.close();
    }

    //  -------------------------------------------------------------------------------------------------------------------------
    public static void refreshData() {
        getFilterdRecords();
        mAdapter.notifyDataSetChanged();
        //   mRecyclerView.setAdapter(mAdapter);
        //Log.i("EEE", "resreshData");
        if ((mRecyclerView.getAdapter() != null) && (mRecyclerView.getAdapter().getItemCount() > 0)) {
            if (WMA.MSA_POS >= 0) {
                if (mRecyclerView.getAdapter().getItemCount() > WMA.MSA_POS)
                    mRecyclerView.scrollToPosition(WMA.MSA_POS);
                else
                    mRecyclerView.scrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);
            }
        }
    }

    // -------------------------------------------------------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    // выбор из меню
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Настройки
        if (item.getItemId() == R.id.main_memuItem_pref) {
            Intent intent = new Intent(Main_Activity.this, Pref_Activity.class);
            startActivity(intent);
            WMA.animateStart(Main_Activity.this);
            return true;
        }

        // Импорт события
        if (item.getItemId() == R.id.main_memuItem_import) {
            importEvent();
            return true;
        }

        // О программе
        if (item.getItemId() == R.id.main_memuItem_about) {
            Intent intent = new Intent(this, About_Activity.class);
            startActivity(intent);
            return true;
        }

        // Диаграммы
        if (item.getItemId() == R.id.main_memuItem_diagramm) {
            Intent intent = new Intent(this, Diagramm_Activity.class);
            startActivity(intent);
            return true;
        }

        // Сортировка
        if (item.getItemId() == R.id.main_memuItem_order) {

            adb_order.setTitle(R.string.s_order);
            adb_order.setCancelable(true);
            adb_order.setItems(sort_list_name,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            String sf = sort_list_field[item];
                            if (!sf.equals(sort_field)) {
                                sort_field = sf;
                                refreshData();
                            }
                        }
                    });
            adb_order.create();
            adb_order.show();
            return true;
        }

        // Справка
        if (item.getItemId() == R.id.main_memuItem_help) {
            Intent helpintent = new Intent(Main_Activity.this, Help_Activity.class);
            startActivity(helpintent);
            WMA.animateStart(Main_Activity.this);
            return true;
        }

        // Сканнер
        if (item.getItemId() == R.id.main_memuItem_scanner) {
            WMA.startScanner(Main_Activity.this);
            return true;
        }

        // добавление новой записи
        if (item.getItemId() == R.id.main_memuItem_add) {
            Intent addNewContact = new Intent(Main_Activity.this, Edit_Activity.class);
            startActivityForResult(addNewContact, CONT_INSERTED);
            WMA.animateStart(Main_Activity.this);
            return true;
        }

        // Выгрузить базу
        if (item.getItemId() == R.id.main_memuItem_backupDB) {
            Intent backupintent = new Intent(Main_Activity.this, Backup_Activity.class);
            startActivity(backupintent);
            return true;
        }

        // Загрузить базу
        if (item.getItemId() == R.id.main_memuItem_restoreDB) {
            Intent restoreintent = new Intent(Main_Activity.this, Restore_Activity.class);
            startActivity(restoreintent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void deleteRecord() {
        final Timer timer = new Timer();
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogColorButton);
        builder.setTitle(R.string.s_confirm_request);
        builder.setMessage(R.string.s_delete_record);

        builder.setNegativeButton(R.string.s_no, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                timer.purge();
                timer.cancel();
            }
        });

        builder.setPositiveButton(R.string.s_yes,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        timer.purge();
                        timer.cancel();
                        WMA.getDatabase().delete("contacts", "_id=" + WMA.MSA_ID, null);
                        Main_Activity.refreshData();
                    }
                });

        final AlertDialog dlg = builder.create();
        dlg.show();

        timer.schedule(new TimerTask() {
            public void run() {
                dlg.dismiss();
                timer.purge();
                timer.cancel();
            }
        }, 5000);
    }


    // -------------------------------------------------------------------------------------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            switch (requestCode) {

                case REC_MODIFIED: // из экрана View
                    refreshData();
                    break;

                case CONT_EDIT: // из экрана Edit
                    refreshData();
                    break;

                case MAS_IMPORT: // из экрана Edit
                    refreshData();
                    break;

                case CONT_INSERTED: // из экрана Edit
                    refreshData();
                    break;

                case WMA.EXIT_SCANNER_CODE: // из сканера
                    if (data != null) {
                        String contents = data.getStringExtra("SCAN_RESULT");
                        if ((contents != null) & (!(contents != null && contents.equals("")))) {
                            WMA.setLast_scanned_code(contents);
                            int find_id = findRecordByScanCode(WMA.getLast_scanned_code());
                            if (find_id >= 0) {
                                Intent viewContact = new Intent(Main_Activity.this, View_Activity.class);
                                viewContact.putExtra("CONTACT_ID", find_id);
                                startActivityForResult(viewContact, REC_MODIFIED);
                                WMA.MSA_ID = find_id;
                            } else {
                                findscancodebygoogle();
                            }
                        }
                    }
                    break;
            }
        }
    }


    private void findscancodebygoogle() {
        if (WMA.isNetworkAvailable()) {
            adb_order.setTitle(getString(R.string.s_scan_dlg_title) + " " + WMA.getLast_scanned_code());
            adb_order.setMessage(R.string.s_scan_dlg_search_web);
            adb_order.setCancelable(true);
            adb_order.setNegativeButton(R.string.s_no, null);
            adb_order.setPositiveButton(R.string.s_yes,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {

                            Intent searchintent = new Intent(Intent.ACTION_WEB_SEARCH);
                            searchintent.putExtra("query", WMA.getLast_scanned_code());
                            searchintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(searchintent);

                        }
                    });
            adb_order.create();
            adb_order.show();
        } else {
            adb_order.setTitle(getString(R.string.s_scan_dlg_code) + " " + WMA.getLast_scanned_code());
            adb_order.setMessage(R.string.s_scan_dlg_no_code);
            adb_order.setCancelable(true);
            adb_order.setPositiveButton(R.string.s_scan_ok,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                        }
                    });
            adb_order.create();
            adb_order.show();
        }
    }

    @Override
    public void onBackPressed() {
        final Timer timer = new Timer();
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogColorButton);
        builder.setTitle(R.string.s_app_name);
        builder.setMessage(R.string.s_exit_appl);

        builder.setNegativeButton(R.string.s_no, new OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                timer.purge();
                timer.cancel();
            }
        });

        builder.setPositiveButton(R.string.s_yes, new OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                timer.purge();
                timer.cancel();
                Main_Activity.super.onBackPressed();
                WMA.animateFinish(Main_Activity.this);
            }
        });

        final AlertDialog dlg = builder.create();
        dlg.show();
        timer.schedule(new TimerTask() {
            public void run() {
                dlg.dismiss();
                timer.purge();
                timer.cancel();
            }
        }, 5000);
    }




    // ---------------------------------------------------------------------------------------------------------------------------
    private void importEvent() {
        filenames_display.clear();
        File dir = new File(WMA.DB_DOWNLOAD);
        if (dir.exists()) {
            for (File file : dir.listFiles()) {
                if ((file.isFile()) & (file.getName().endsWith(WMA.TYPE_WMDE)) & (file.getName().startsWith(WMA.TYPE_WME))) {
                    filenames_display.add(file.getName());
                }
            }
        }

        dir = new File(WMA.DB_BLUETOOTH);
        if (dir.exists()) {
            for (File file : dir.listFiles()) {
                if ((file.isFile()) & (file.getName().endsWith(WMA.TYPE_WMDE)) & (file.getName().startsWith(WMA.TYPE_WME))) {
                    filenames_display.add(file.getName() + ":BT");
                }
            }
        }

        if (filenames_display.isEmpty()) {
            filenames_display.add(getString(R.string.s_no_event_to_import));
        }

        Collections.sort(filenames_display);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filenames_display);

        adb_import.setTitle(R.string.s_import_select);
        adb_import.setIcon(R.drawable.ic_launcher);
        adb_import.setCancelable(true);
        adb_import.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                fn_import = filenames_display.get(item);
                if (!fn_import.equals(getString(R.string.s_no_event_to_import))) {

                    WMA.setFileImportMode(true);
                    if (fn_import.endsWith(":BT")) {
                        fn_import = fn_import.replace(":BT", "");
                        WMA.setMyImportFileName(WMA.DB_BLUETOOTH + fn_import);
                    } else {
                        WMA.setMyImportFileName(WMA.DB_DOWNLOAD + fn_import);
                    }
                    Intent intent = new Intent(Main_Activity.this, Import_Activity.class);
                    startActivityForResult(intent, MAS_IMPORT);
                    WMA.animateStart(Main_Activity.this);
                }
            }
        });
        adb_import.create();
        adb_import.show();
    }


}