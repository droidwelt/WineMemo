package ru.droidwelt.winememo.database;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ru.droidwelt.winememo.R;
import ru.droidwelt.winememo.WMA;

@SuppressLint("StaticFieldLeak")
public class DB_OpenHelper extends SQLiteOpenHelper {

    private static DB_OpenHelper sDB_OpenHelper;
    private static Context context;
    private static String DB_NAME;
    private static SQLiteDatabase database;

    // ----------------------------------------------------------------------------------------------------------------------

    public static DB_OpenHelper get(Context context, String dbName) {
        if (sDB_OpenHelper == null) {
            sDB_OpenHelper = new DB_OpenHelper(context.getApplicationContext(), dbName);
        }
        return sDB_OpenHelper;
    }

    public DB_OpenHelper(Context context, String dbName) {
        super(context, dbName, null, 1);
        DB_OpenHelper.context = context;
        File PATH_TO = new File(WMA.DB_PATH); // куда
        if (!(PATH_TO.isDirectory() && PATH_TO.canExecute() && PATH_TO.canRead() && PATH_TO.canWrite())) {
            if (!PATH_TO.mkdir()) return;
        }
        DB_NAME = dbName;
        database = openDataBase();

        try {
            database.query("contacts", new String[]{"_id", "name", "tip", "country", "region", "consist", "barcode",
                            "firma", "god", "emk", "alk", "sug", "rating", "price", "dates", "code", "comment"}, "_id=0",
                    null, null, null, null);
        } catch (SQLException e) {
            //Log.i("ERR", "Android database corrupt 2");
            WMA.DisplayToastError("Структура базы программы повреждена. Производится восстановление из эталона");
            database.close();
            copyDataBase();
            database = openDataBase();
        }
    }

    public SQLiteDatabase getDatabase() {
        return database;
    }

    public long getDatabaseSize() {
        File file = new File(WMA.DB_PATH + DB_NAME);
        return file.length() / 1024 / 1024;
    }

    private void copyDataBase() {
        try {
            InputStream externalDbStream = context.getAssets().open(WMA.DB_NAMEMODEL);
            String outFileName = WMA.DB_PATH + DB_NAME;
            OutputStream localDbStream = new FileOutputStream(outFileName);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = externalDbStream.read(buffer)) > 0) {
                localDbStream.write(buffer, 0, bytesRead);
            }
            localDbStream.close();
            externalDbStream.close();
        } catch (IOException ignored) {
        }
    }

    // Создаст базу, если она не создана
    private void createDataBase() {
        boolean dbExist = checkDataBase();
        if (!dbExist) copyDataBase();
    }

    // Проверка существования базы данных
    private boolean checkDataBase() {
        SQLiteDatabase checkDb = null;
        try {
            checkDb = SQLiteDatabase.openDatabase(WMA.DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLException ignored) {
        }
        if (checkDb != null) checkDb.close();
        return checkDb != null;
    }

    private SQLiteDatabase openDataBase() throws SQLException {
        if (database == null) {
            createDataBase();
            try {
                database = SQLiteDatabase.openDatabase(WMA.DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READWRITE);
            } catch (SQLException e) {
                WMA.DisplayToastError(context.getResources().getString(R.string.s_db_recovery));
                createDataBase();
            }
        }
        return database;
    }


    @Override
    public synchronized void close() {
        if (database != null) database.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }



}