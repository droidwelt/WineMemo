package ru.droidwelt.winememo.database;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ru.droidwelt.winememo.WMA;

@SuppressLint("StaticFieldLeak")
public class DB_OpenHelper_Dict extends SQLiteOpenHelper {

    private static DB_OpenHelper_Dict sDB_OpenHelper_Dict;
    private static Context context;
    private static SQLiteDatabase database_dict;

    public static DB_OpenHelper_Dict get(Context context, String dbName) {
        if (sDB_OpenHelper_Dict == null) {
            sDB_OpenHelper_Dict = new DB_OpenHelper_Dict(context.getApplicationContext(), dbName);
        }
        return sDB_OpenHelper_Dict;
    }

    public DB_OpenHelper_Dict(Context context, String dbName) {
        super(context, dbName, null, 1);
        DB_OpenHelper_Dict.context = context;
        database_dict = openDataBase_dict();
    }

    public SQLiteDatabase getDatabase_dict() {
        return database_dict;
    }


    @Override
    public synchronized void close() {
        if (database_dict != null) {
            database_dict.close();
        }
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }


    public void closeDatabase_dict() {
        if (database_dict != null) {
            database_dict.close();
        }
    }

    public SQLiteDatabase openDataBase_dict() throws SQLException {
        String path = WMA.DB_PATH + WMA.DB_NAMEDICT2;
        if (database_dict == null) {
            createDataBase_dict();
            //Log.i("XXX", "openDataBase_dict " + WMA.DB_PATH + WMA.DB_NAMEDICT2);
            database_dict = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
        }
        return database_dict;
    }

    private void copyDataBase_dict() throws IOException {

        InputStream externalDbStream = context.getAssets().open(WMA.DB_NAMEDICT2);
        String outFileName = WMA.DB_PATH + WMA.DB_NAMEDICT2;
        OutputStream localDbStream = new FileOutputStream(outFileName);

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = externalDbStream.read(buffer)) > 0) {
            localDbStream.write(buffer, 0, bytesRead);
        }
        localDbStream.close();
        externalDbStream.close();
    }

    private void createDataBase_dict() {
        try {
            copyDataBase_dict();
        } catch (IOException e) {
            Log.i("XXX", e.getMessage());
            throw new Error("createDataBase_dict Copying error");
        }
    }

}