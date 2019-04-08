package ru.droidwelt.winememo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.io.File;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.droidwelt.winememo.additional_UI.CustomToast;
import ru.droidwelt.winememo.database.DB_OpenHelper;

public class WMA extends Application {

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    public static final String DB_PATH = Environment.getExternalStorageDirectory().toString() + "/Winememo/";
    public static final String DB_DOWNLOAD = Environment.getExternalStorageDirectory().toString() + "/Download/";
    public static final String DB_BLUETOOTH = Environment.getExternalStorageDirectory().toString() + "/Bluetooth/";
    final public static String DB_NAMEMODEL = "winememo_etal.db3";
    final public static String DB_NAME = "winememo.db3";
    final public static String DB_NAMEDICT2 = "winememo_dict2.db3";
    final public static String DB_NAMEEXPORT = "winememo_export.db3";

    public static final String TYPE_WME = "WME_";
    public static final String TYPE_WMDE = ".wmde";

    private static SQLiteDatabase database = null;

    public static final int EXIT_SCANNER_CODE = 10001;
    private static String last_scanned_code = "";
    private static String langprefix;

    private static Boolean fileImportMode = false;
    private static String myImportFileName = "";

    public static int MSA_ID = 0, MSA_POS = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        String land = getResources().getConfiguration().locale.getLanguage();
        Locale locale = new Locale(land);
        langprefix = locale.getLanguage();
    }


    public static Context getContext() {
        return context;
    }

    public static void startDbHelper() {
        if (database == null) {
            DB_OpenHelper dbh = new DB_OpenHelper(context, DB_NAME);
            database = dbh.getDatabase();
        }
    }


    private static Context getAppContext() {
        return WMA.context;
    }


    public static String AddString(String s1, String s2) {
        if (s1 == null) s1 = "";
        if (s2 == null) s2 = "";
        if (!s2.equals("")) {
            if (!(s1.equals(""))) s1 = s1 + "; ";
            s1 = s1 + s2;
        }
        return s1;
    }

    public static byte[] concatArray(byte[] a, byte[] b) {
        if (a == null) return b;
        if (b == null) return a;
        byte[] r = new byte[a.length + b.length];
        System.arraycopy(a, 0, r, 0, a.length);
        System.arraycopy(b, 0, r, a.length, b.length);
        return r;
    }

    public static String strnormalize(String s) {
        if (s == null)
            return "";
        else
            return s;
    }


    public static SQLiteDatabase getDatabase() {
        if (database == null) {
            DB_OpenHelper dbh = new DB_OpenHelper(context, DB_NAME);
            database = dbh.getDatabase();
        }
        return database;
    }

    public static void setDatabase(SQLiteDatabase database) {
        WMA.database = database;
    }

    public static String getLast_scanned_code() {
        return last_scanned_code;
    }

    public static void setLast_scanned_code(String last_scanned_code) {
        WMA.last_scanned_code = last_scanned_code;
    }


    public static String getLangprefix() {
        return langprefix;
    }

    private static int getAnimation_mode() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getAppContext());
        String animation_mode = sp.getString("animation_mode", "0");
        return Integer.parseInt(animation_mode);
    }


    public static void animateStart(Activity a) {
        switch (getAnimation_mode()) {

            case 1:
                a.overridePendingTransition(R.anim.activity_down_up_enter, R.anim.activity_down_up_exit);
                break;

            case 2:
                a.overridePendingTransition(R.anim.activity_up_down_enter, R.anim.activity_up_down_exit);
                break;

            case 3:
                a.overridePendingTransition(R.anim.activity_left_rigth_enter, R.anim.activity_left_rigth_exit);
                break;

            case 4:
                a.overridePendingTransition(R.anim.activity_rigth_left_enter, R.anim.activity_rigth_left_exit);
                break;

            default:
                break;
        }
    }

    public static void animateFinish(Activity a) {
        switch (getAnimation_mode()) {

            case 1:
                a.overridePendingTransition(R.anim.activity_down_up_close_enter, R.anim.activity_down_up_close_exit);
                break;

            case 2:
                a.overridePendingTransition(R.anim.activity_up_down_close_enter, R.anim.activity_up_down_close_exit);
                break;

            case 3:
                a.overridePendingTransition(R.anim.activity_left_rigth_close_enter, R.anim.activity_left_rigth_close_exit);
                break;

            case 4:
                a.overridePendingTransition(R.anim.activity_rigth_left_close_enter, R.anim.activity_rigth_left_close_exit);
                break;

            default:
                break;
        }
    }

    public static int get_picturebig_screen_x(Context c) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        String picturebig_screen_val = sp.getString("picturebig_screen", "1");
        int picturebig_screen_x = 960;
        if (picturebig_screen_val.equals("0")) picturebig_screen_x = 600;
        if (picturebig_screen_val.equals("1")) picturebig_screen_x = 960;
        if (picturebig_screen_val.equals("2")) picturebig_screen_x = 1440;
        if (picturebig_screen_val.equals("3")) picturebig_screen_x = 1920;
        return picturebig_screen_x;
    }

    public static int get_picturebig_screen_y(Context c) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        String picturebig_screen_val = sp.getString("picturebig_screen", "1");
        int picturebig_screen_y = 1280;
        if (picturebig_screen_val.equals("0")) picturebig_screen_y = 800;
        if (picturebig_screen_val.equals("1")) picturebig_screen_y = 1280;
        if (picturebig_screen_val.equals("2")) picturebig_screen_y = 1920;
        if (picturebig_screen_val.equals("3")) picturebig_screen_y = 2560;
        return picturebig_screen_y;
    }

    public static boolean get_choice_allfield() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(WMA.getContext());
        return sp.getBoolean("choice_allfield", false);
    }

    public static int get_picture_bgr(Context c) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        String picture_bgr_val = sp.getString("picture_bgr", "1");
        int picture_bgr = 1;
        if (picture_bgr_val.equals("0")) picture_bgr = 0;
        if (picture_bgr_val.equals("1")) picture_bgr = 1;
        if (picture_bgr_val.equals("2")) picture_bgr = 2;
        return picture_bgr;
    }

    public static int get_picturesmall_screen_x(Context c) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        String picturesmall_screen_val = sp.getString("picturesmall_screen", "1");
        int picturesmall_screen_x = 120;
        if (picturesmall_screen_val.equals("0")) picturesmall_screen_x = 90;
        if (picturesmall_screen_val.equals("1")) picturesmall_screen_x = 120;
        if (picturesmall_screen_val.equals("2")) picturesmall_screen_x = 180;
        return picturesmall_screen_x;
    }

    public static int get_picturesmall_screen_y(Context c) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        String picturesmall_screen_val = sp.getString("picturesmall_screen", "1");
        int picturesmall_screen_y = 160;
        if (picturesmall_screen_val.equals("0")) picturesmall_screen_y = 120;
        if (picturesmall_screen_val.equals("1")) picturesmall_screen_y = 160;
        if (picturesmall_screen_val.equals("2")) picturesmall_screen_y = 240;
        return picturesmall_screen_y;
    }


    public static Boolean getchoice_deleteevent() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getAppContext());
        return sp.getBoolean("choice_deleteevent", false);
    }


    public static void deleteTempFile(String fn) {
        if (!fn.equals("")) {
            File file = new File(DB_PATH, fn);
            if (file.exists())
                file.delete();
        }
    }

    @SuppressLint("DefaultLocale")
    public static String generValidFileName(String s_in) {
        StringBuilder s_out = new StringBuilder(TYPE_WME);
        try {
            if (!(s_in == null) & !(s_in != null && s_in.isEmpty())) {
                String expression = "[ #~+=?0123456789QWERTYUIOPASDFGHJKLZXCVBNM_-ЙЦУКЕНГШЩЗХЪФЫВАПРОЛДЖЭЯЧСМИТЬБЮЁ]";
                Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
                int l = s_in.length();
                for (int i = 0; i < l; i = i + 1) {
                    String s = s_in.substring(i, i + 1).toUpperCase();
                    Matcher matcher = pattern.matcher(s);
                    if (matcher.matches()) s_out.append(s);
                    else s_out.append("_");
                }
            }
        } catch (Exception ignored) {
        }
        return s_out + TYPE_WMDE;
    }


    public static String getMyImportFileName() {
        return myImportFileName;
    }

    public static void setMyImportFileName(String myImportFileName) {
        WMA.myImportFileName = myImportFileName;
    }

    public static Boolean getFileImportMode() {
        return fileImportMode;
    }

    public static void setFileImportMode(Boolean fileImportMode) {
        WMA.fileImportMode = fileImportMode;
    }

    public static void deleteFileByName(String fn) {
        if (!fn.equals("")) {
            File file = new File(fn, "");
            if (file.exists()) file.delete();
        }
    }


    public static boolean isNetworkAvailable() {
        int[] networkTypes = {ConnectivityManager.TYPE_MOBILE, ConnectivityManager.TYPE_WIFI};
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            for (int networkType : networkTypes) {
                assert connectivityManager != null;
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                if (activeNetworkInfo != null && activeNetworkInfo.getType() == networkType)
                    return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }


    public static void DisplayToastError(String result) {
        Toast toast3 = CustomToast.makeText(WMA.context, result, Toast.LENGTH_LONG, R.mipmap.ic_cancel);
        toast3.show();
    }

    public static void DisplayToastError(int resID) {
        Toast toast3 = CustomToast.makeText(WMA.context, WMA.context.getString(resID), Toast.LENGTH_LONG, R.mipmap.ic_cancel);
        toast3.show();
    }


    public static int getDisplaySizeX() {
        return getContext().getResources().getDisplayMetrics().widthPixels;
    }

  /*  public static int getDisplaySizeY() {
        return getContext().getResources().getDisplayMetrics().heightPixels;
    }

    public static int getDensityDpi() {
        return getContext().getResources().getDisplayMetrics().densityDpi;
    }
*/

    public static void startScanner(Activity a) {
        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        if (getContext().getPackageManager().queryIntentActivities(intent, 0).size() > 0) {
         //   intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            // intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            a.startActivityForResult(intent, WMA.EXIT_SCANNER_CODE);
        }  else {
            DisplayToastError(getContext().getString(R.string.s_scanner_not_installed));
        }
    }

}
