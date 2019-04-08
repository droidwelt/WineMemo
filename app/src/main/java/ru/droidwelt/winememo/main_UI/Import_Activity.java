package ru.droidwelt.winememo.main_UI;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import ru.droidwelt.winememo.R;
import ru.droidwelt.winememo.WMA;
import ru.droidwelt.winememo.common.ExtendedViewPager;

import com.ortiz.touchview.TouchImageView;

public class Import_Activity extends AppCompatActivity {

    private static long contact_ID; // ID
    private TextView nameTextView; // name
    private TextView labelTextView1;
    private TextView labelTextView2;
    private ExtendedViewPager mViewPager;
    private static Bitmap imageBig1 = null;
    private static Bitmap imageBig2 = null;

    private boolean inProgress = false;
    private static SQLiteDatabase databaseImport;

    private static class TouchImageAdapterView extends PagerAdapter {

        private static final int[] images = {R.drawable.ic_bgr_front,
                R.drawable.ic_bgr_back};

        @Override
        public int getCount() {
            return images.length;
        }

        @NonNull
        @Override
        public View instantiateItem(@NonNull ViewGroup container, int position) {
            TouchImageView img = new TouchImageView(container.getContext());

            if (position == 0) {
                Bitmap bm = null;
                if (contact_ID >= 0) {
                    bm = imageBig1;
                }
                if (bm != null) {
                    img.setImageBitmap(bm);
                } else {
                    img.setImageResource(images[position]);
                }
            }

            if (position == 1) {
                Bitmap bm = null;
                if (contact_ID >= 0) {
                    bm = imageBig2;
                }
                if (bm != null) {
                    img.setImageBitmap(bm);
                } else {
                    img.setImageResource(images[position]);
                }
            }
            container.addView(img, LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            return img;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }
    }

    // ------------------------------------------------------------------------------------------------------------------------
    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.import_view);

        Toolbar tb_main = findViewById(R.id.tb_main);
        setSupportActionBar(tb_main);

        nameTextView = findViewById(R.id.import_name);
        labelTextView1 = findViewById(R.id.import_label1);
        labelTextView2 = findViewById(R.id.import_label2);
        mViewPager = findViewById(R.id.import_graphic);

        switch (WMA.get_picture_bgr(Import_Activity.this)) {
            case 0:
                mViewPager.setBackgroundColor(getResources().getColor(
                        R.color.c_bgr_0));
                break;
            case 1:
                mViewPager.setBackgroundColor(getResources().getColor(
                        R.color.c_bgr_1));
                break;
            case 2:
                mViewPager.setBackgroundColor(getResources().getColor(
                        R.color.c_bgr_2));
                break;
        }

        Intent intent = getIntent();
        String action = intent.getAction();

        String dbNameImport;

        if  ((action != null) && (action.compareTo(Intent.ACTION_VIEW) == 0)) {
            String scheme = intent.getScheme();
            ContentResolver resolver = getContentResolver();

            assert scheme != null;
            if (scheme.compareTo(ContentResolver.SCHEME_CONTENT) == 0) {
                Uri uri = intent.getData();
                String name = getContentName(resolver, uri);

                //   Log.v("tag", "Content intent detected: " + action + " : " + intent.getDataString() + " : " + intent.getType() + " : " + name);
                InputStream input = null;
                try {
                    assert uri != null;
                    input = resolver.openInputStream(uri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                dbNameImport = WMA.DB_DOWNLOAD + name;
                InputStreamToFile(input, dbNameImport);
                // WMA.setMyImportFileName(name);
            }
       /*     else if (scheme.compareTo(ContentResolver.SCHEME_FILE) == 0) {
                Uri uri = intent.getData();
                String name = uri.getLastPathSegment();

                Log.v("tag" , "File intent detected: " + action + " : " + intent.getDataString() + " : " + intent.getType() + " : " + name);
                InputStream input = resolver.openInputStream(uri);
                String importfilepath = "/sdcard/My Documents/" + name;
                InputStreamToFile(input, importfilepath);
            }
            else if (scheme.compareTo("http") == 0) {
                // TODO Import from HTTP!
            }
            else if (scheme.compareTo("ftp") == 0) {
                // TODO Import from FTP!
            }*/
        }


        if (WMA.getFileImportMode()) {
            dbNameImport = WMA.getMyImportFileName();
            WMA.setFileImportMode(false);
        } else {
            Uri uri = getIntent().getData();
            assert uri != null;
            dbNameImport = uri.getPath();
        }

        if (dbNameImport.isEmpty()) {
            Uri uri = getIntent().getData();
            assert uri != null;
            dbNameImport = uri.getPath();
        }

        WMA.setMyImportFileName(dbNameImport);

        //  Log.i("dbNameImport", dbNameImport);
        databaseImport = SQLiteDatabase.openDatabase(dbNameImport, null, SQLiteDatabase.OPEN_READONLY);

        loadRecord();
        loadRecordViewTask.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private final
    AsyncTask<Long, Object, Object> loadRecordViewTask = new AsyncTask<Long, Object, Object>() {
        @Override
        protected Object doInBackground(Long... params) {
            imageBig1 = getOneContactImageValue("graphic1", contact_ID);
            imageBig2 = getOneContactImageValue("graphic2", contact_ID);
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            TouchImageAdapterView _touchImageAdapter = new TouchImageAdapterView();
            mViewPager.setAdapter(_touchImageAdapter);
        }
    };

    // ------------------------------------------------------------------------------------------------------------------------
    private void loadRecord() {
        String sql = "select _id,name,tip,country,region,consist,barcode,"
                + "firma,god,emk,alk,sug,rating,price,dates,code,comment "
                + " from contacts";
        Cursor cursor = databaseImport.rawQuery(sql, null);

        cursor.moveToFirst();
        int _idIndex = cursor.getColumnIndex("_id");
        contact_ID = cursor.getLong(_idIndex);
        int tipIndex = cursor.getColumnIndex("tip");
        int nameIndex = cursor.getColumnIndex("name");
        int countyIndex = cursor.getColumnIndex("country");
        int regionIndex = cursor.getColumnIndex("region");
        int consistIndex = cursor.getColumnIndex("consist");
        int firmaIndex = cursor.getColumnIndex("firma");
        int godIndex = cursor.getColumnIndex("god");
        int emkIndex = cursor.getColumnIndex("emk");
        int alkIndex = cursor.getColumnIndex("alk");
        int sugIndex = cursor.getColumnIndex("sug");
        int ratingIndex = cursor.getColumnIndex("rating");
        int commentIndex = cursor.getColumnIndex("comment");
        int priceIndex = cursor.getColumnIndex("price");
        int barcodeIndex = cursor.getColumnIndex("barcode");

        nameTextView.setText(cursor.getString(nameIndex));

        String s = cursor.getString(tipIndex);
        s = WMA.AddString(s, cursor.getString(countyIndex));
        s = WMA.AddString(s, cursor.getString(regionIndex));
        labelTextView1.setText(s);

        String s1 = "";
        s1 = WMA.AddString(s1, cursor.getString(consistIndex));
        s1 = WMA.AddString(s1, cursor.getString(firmaIndex));
        s1 = WMA.AddString(s1, cursor.getString(godIndex));
        s1 = WMA.AddString(s1, cursor.getString(emkIndex));
        s1 = WMA.AddString(s1, cursor.getString(alkIndex));
        s1 = WMA.AddString(s1, cursor.getString(sugIndex));
        s1 = WMA.AddString(s1, cursor.getString(priceIndex));
        s1 = WMA.AddString(s1, cursor.getString(barcodeIndex));
        if (!s1.equals(""))
            s1 = s1 + "\n";
        String s2 = "";
        s2 = WMA.AddString(s2, cursor.getString(commentIndex));
        if (!s2.equals(""))
            s1 = s1 + s2;
        labelTextView2.setText(s1);

        ImageView imgrating = findViewById(R.id.import_rating);
        String rat = cursor.getString(ratingIndex);
        imgrating.setImageResource(R.drawable.ic_rating_0);
        if (rat != null) {
            if (rat.equals("1")) imgrating.setImageResource(R.drawable.ic_rating_1);
            if (rat.equals("2")) imgrating.setImageResource(R.drawable.ic_rating_2);
            if (rat.equals("3")) imgrating.setImageResource(R.drawable.ic_rating_3);
            if (rat.equals("4")) imgrating.setImageResource(R.drawable.ic_rating_4);
            if (rat.equals("5")) imgrating.setImageResource(R.drawable.ic_rating_5);
        }
        cursor.close();
    }

    // ------------------------------------------------------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.import_menu, menu);
        return true;
    }

    // ------------------------------------------------------------------------------------------------------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.import_menuItem_save:
                new AlertDialog.Builder(this, R.style.AlertDialogColorButton)
                        .setTitle(getString(R.string.s_import_qwe))
                        .setMessage(getString(R.string.s_confirm_request))
                        .setNegativeButton((getString(R.string.s_cancel)), null)
                        .setPositiveButton((getString(R.string.s_ok)),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        execImportEvent();
                                    }
                                }).create().show();
                return true;

            case android.R.id.home:
                finish();
                WMA.animateFinish(Import_Activity.this);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Bitmap getOneContactImageValue(String FieldName, long ID) {
        int i = 0;
        int rdbytes = 200000;
        Bitmap theImage = null;
        byte[] resall = null;
        int theImagePos = 0;
        try {
            while (rdbytes == 200000 & i < 30) {
                Cursor cursor = databaseImport.rawQuery(
                        "select substr(" + FieldName + ",1+"
                                + String.valueOf(i)
                                + "*200000,200000) from contacts where _id = "
                                + String.valueOf(ID), null);
                i = i + 1;
                cursor.moveToFirst();
                byte[] res;

                rdbytes = cursor.getBlob(0).length;
                if (rdbytes > 0) {
                    res = cursor.getBlob(0);
                    resall = WMA.concatArray(resall, res);
                    theImagePos = theImagePos + res.length;
                }
                cursor.close();
            }
            if ((resall != null ? resall.length : 0) > 0) {
                theImage = BitmapFactory.decodeByteArray(resall, 0,
                        resall.length); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            }

        } catch (Exception ignored) {
        }
        return theImage;
    }

    // защита от закрытия по Back------------------------------------
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Обработка нажатия, возврат true, если обработка выполнена
        switch (keyCode) {

            case KeyEvent.KEYCODE_BACK:
                finish();
                WMA.animateFinish(Import_Activity.this);
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    private void execImportEvent() {
        inProgress = true;

        String sql_mas = "select _id,name,tip,country,region,consist,barcode,firma,"
                + "god,emk,alk,sug,rating,price,dates,code,comment,username,geo,nameup,searchup,"
                + "datetimes,pict from contacts where _id=" + contact_ID;
        Cursor c_mas = databaseImport.rawQuery(sql_mas, null);
        c_mas.moveToFirst();

        ContentValues recMaster = new ContentValues();
        recMaster.put("name", c_mas.getString(c_mas.getColumnIndex("name")));
        recMaster.put("tip", c_mas.getString(c_mas.getColumnIndex("tip")));
        recMaster.put("country", c_mas.getString(c_mas.getColumnIndex("country")));
        recMaster.put("region", c_mas.getString(c_mas.getColumnIndex("region")));
        recMaster.put("consist", c_mas.getString(c_mas.getColumnIndex("consist")));
        recMaster.put("barcode", c_mas.getString(c_mas.getColumnIndex("barcode")));
        recMaster.put("firma", c_mas.getString(c_mas.getColumnIndex("firma")));
        recMaster.put("god", c_mas.getString(c_mas.getColumnIndex("god")));
        recMaster.put("emk", c_mas.getString(c_mas.getColumnIndex("emk")));
        recMaster.put("alk", c_mas.getString(c_mas.getColumnIndex("alk")));
        recMaster.put("sug", c_mas.getString(c_mas.getColumnIndex("sug")));
        recMaster.put("rating", c_mas.getString(c_mas.getColumnIndex("rating")));
        recMaster.put("price", c_mas.getString(c_mas.getColumnIndex("price")));
        recMaster.put("dates", c_mas.getString(c_mas.getColumnIndex("dates")));
        recMaster.put("code", c_mas.getString(c_mas.getColumnIndex("code")));
        recMaster.put("comment", c_mas.getString(c_mas.getColumnIndex("comment")));
        recMaster.put("username", c_mas.getString(c_mas.getColumnIndex("username")));
        recMaster.put("geo", c_mas.getString(c_mas.getColumnIndex("geo")));
        recMaster.put("nameup", c_mas.getString(c_mas.getColumnIndex("nameup")));
        recMaster.put("searchup", c_mas.getString(c_mas.getColumnIndex("searchup")));
        recMaster.put("datetimes", c_mas.getString(c_mas.getColumnIndex("datetimes")));
        recMaster.put("pict", c_mas.getBlob(c_mas.getColumnIndex("pict")));

        byte[] im1 = getOneByteArrayImageValue("graphic1", contact_ID);
        byte[] im2 = getOneByteArrayImageValue("graphic2", contact_ID);
        if (im1.length > 0)
            recMaster.put("graphic1", im1);
        if (im2.length > 0)
            recMaster.put("graphic2", im2);

        long new_id = WMA.getDatabase().insert("contacts", null, recMaster);
        c_mas.close();

        databaseImport.close();

        String dbNameImport = "";
        if (WMA.getchoice_deleteevent())
            WMA.deleteFileByName(dbNameImport);

        Intent intent = new Intent();
        intent.putExtra("CONTACT_IMPORT_ID", new_id);
        setResult(RESULT_OK, intent);
        inProgress = false;
        finish();
        WMA.animateFinish(Import_Activity.this);
    }


    private byte[] getOneByteArrayImageValue(String FieldName, long ID) {
        int i = 0;
        int rdbytes = 200000;
        byte[] resall = null;
        int theImagePos = 0;
        try {
            while (rdbytes == 200000 & i < 30) {
                Cursor cursor = databaseImport.rawQuery(
                        "select substr(" + FieldName + ",1+"
                                + String.valueOf(i)
                                + "*200000,200000) from contacts where _id = "
                                + String.valueOf(ID), null);
                i = i + 1;
                cursor.moveToFirst();

                rdbytes = cursor.getBlob(0).length;
                if (rdbytes > 0) {
                    byte[] res = cursor.getBlob(0);
                    resall = WMA.concatArray(resall, res);
                    theImagePos = theImagePos + res.length;
                }
                cursor.close();
            }

        } catch (Exception ignored) {
        }
        return resall;
    }

    @Override
    public void onBackPressed() {
        if (!inProgress) {
            Import_Activity.super.onBackPressed();
            WMA.animateFinish(Import_Activity.this);
        }

    }


    private String getContentName(ContentResolver resolver, Uri uri) {
        @SuppressLint("Recycle") Cursor cursor = resolver.query(uri, null, null, null, null);
        assert cursor != null;
        cursor.moveToFirst();
        int nameIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME);
        if (nameIndex >= 0) {
            return cursor.getString(nameIndex);
        } else {
            return null;
        }
    }

    private void InputStreamToFile(InputStream in, String file) {
        try {
            OutputStream out = new FileOutputStream(new File(file));
            int size;
            byte[] buffer = new byte[1024];
            while ((size = in.read(buffer)) != -1) {
                out.write(buffer, 0, size);
            }
            out.close();
        } catch (Exception e) {
            //  Log.e("MainActivity", "InputStreamToFile exception: " + e.getMessage());
        }
    }

}