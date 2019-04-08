package ru.droidwelt.winememo.main_UI;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;

import ru.droidwelt.winememo.R;
import ru.droidwelt.winememo.WMA;
import ru.droidwelt.winememo.common.ExtendedViewPager;

import com.ortiz.touchview.TouchImageView;

public class View_Activity extends AppCompatActivity {

    private final String sendFilename = "winmemo_send.jpg";
    private static int contact_ID; // ID
    private TextView nameTextView; // name
    private TextView labelTextView1;
    private TextView labelTextView2;
    private ExtendedViewPager mViewPager;
    private static Bitmap imageBig1 = null;
    private static Bitmap imageBig2 = null;
    private static final int CONT_EDIT = 301;
    private AlertDialog dlgView;
    private String __name = "";
    private String __tip = "";
    private String __country = "";
    private String __consist = "";
    private String __god = "";
    private String __alk = "";
    private String __sug = "";
    private String __rating = "";
    private String __barcode = "";
    private String SendDBFileName = "";

    private static class TouchImageAdapterView extends PagerAdapter {

        private static final int[] images = {R.drawable.ic_bgr_front, R.drawable.ic_bgr_back};

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
            container.addView(img, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
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
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_activity);

        Toolbar tb_view = findViewById(R.id.tb_view);
        setSupportActionBar(tb_view);
        //	TextView tv_view_title = (TextView)  findViewById(R.id.tv_view_title);

        nameTextView = findViewById(R.id.view_name);
        labelTextView1 = findViewById(R.id.view_label1);
        labelTextView2 = findViewById(R.id.view_label2);
        mViewPager = findViewById(R.id.view_graphic);

        switch (WMA.get_picture_bgr(View_Activity.this)) {
            case 0:
                mViewPager.setBackgroundColor(getResources().getColor(R.color.c_bgr_0));
                break;
            case 1:
                mViewPager.setBackgroundColor(getResources().getColor(R.color.c_bgr_1));
                break;
            case 2:
                mViewPager.setBackgroundColor(getResources().getColor(R.color.c_bgr_2));
                break;
        }

        Bundle extras = getIntent().getExtras();
        contact_ID = extras != null ? extras.getInt("CONTACT_ID") : 0;
        loadRecord();

        AlertDialog.Builder builder = new AlertDialog.Builder(View_Activity.this);
        builder.setTitle(R.string.s_loadimages);
        builder.setMessage(R.string.s_wait);
        builder.setCancelable(true);
        dlgView = builder.create();
        dlgView.show();
        loadRecordViewTask.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private final
    AsyncTask<Long, Object, Object> loadRecordViewTask = new AsyncTask<Long, Object, Object>() {
        @Override
        protected Object doInBackground(Long... params) {
            imageBig1 = getOneRecordImageValue("graphic1", contact_ID);
            imageBig2 = getOneRecordImageValue("graphic2", contact_ID);
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            TouchImageAdapterView _touchImageAdapter = new TouchImageAdapterView();
            mViewPager.setAdapter(_touchImageAdapter);
            dlgView.dismiss();
        }
    };

    // ------------------------------------------------------------------------------------------------------------------------
    private void loadRecord() {
        Cursor cursor = WMA.getDatabase().query(
                "contacts",
                new String[]{"_id", "name", "tip", "country", "region",
                        "consist", "barcode", "firma", "god", "emk", "alk",
                        "sug", "rating", "price", "dates", "code", "comment"},
                "_id=" + contact_ID, null, null, null, null);

        cursor.moveToFirst();
        int nameIndex = cursor.getColumnIndex("name");
        int tipIndex = cursor.getColumnIndex("tip");
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

        __name = WMA.strnormalize(cursor.getString(nameIndex));
        __tip = WMA.strnormalize(cursor.getString(tipIndex));
        __country = WMA.strnormalize(cursor.getString(countyIndex));
        __consist = WMA.strnormalize(cursor.getString(consistIndex));
        __god = WMA.strnormalize(cursor.getString(godIndex));
        __alk = WMA.strnormalize(cursor.getString(alkIndex));
        __sug = WMA.strnormalize(cursor.getString(sugIndex));
        __rating = WMA.strnormalize(cursor.getString(ratingIndex));
        __barcode = WMA.strnormalize(cursor.getString(barcodeIndex));
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

        ImageView imgrating = findViewById(R.id.view_rating);
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
        inflater.inflate(R.menu.view_menu, menu);
        return true;
    }

    private String prepareTextToSend() {
        String res = "";
        if (!__name.equals("")) res = res + __name + ";";
        if (!__tip.equals("")) res = res + __tip + ";";
        if (!__country.equals("")) res = res + __country + ";";
        if (!__god.equals("")) res = res + __god + ";";
        if (!__consist.equals("")) res = res + __consist + ";";
        if (!__alk.equals("")) res = res + __alk + ";";
        if (!__sug.equals("")) res = res + __sug + ";";
        if (!__rating.equals("")) res = res + __rating + ";";
        if (!__barcode.equals("")) res = res + __barcode + "; ";
        res = res + getString(R.string.s_prepare_to_share);
        return res;
    }

    // ------------------------------------------------------------------------------------------------------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.view_menuItem_edit:
                Log.i("EEE", "contact_ID=" + contact_ID);
                Intent addEditContact = new Intent(this, Edit_Activity.class);
                addEditContact.putExtra("CONTACT_ID", contact_ID);
                startActivityForResult(addEditContact, CONT_EDIT);
                WMA.animateStart(View_Activity.this);
                return true;

            case R.id.view_menuItem_delete:
                deleteRecord();
                return true;

            case R.id.view_menuItem_export:
                exportDetRecord(__name, prepareTextToSend());
                return true;

            case R.id.view_menuItem_share:
                int ix = mViewPager.getCurrentItem();

                if (((ix == 0) & (imageBig1 != null)) | ((ix == 1) & (imageBig2 != null))) {
                    Intent i = new Intent(Intent.ACTION_SEND);

                    try {
                        FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory() + "/" + sendFilename);
                        if (ix == 0)
                            imageBig1.compress(CompressFormat.JPEG, 75, fos);
                        if (ix == 1)
                            imageBig2.compress(CompressFormat.JPEG, 75, fos);

                        fos.flush();
                        fos.close();
                    } catch (Exception e) {
                        Log.i("XXX", "FileOutputStream fos " + e.toString());
                    }

                    File file = new File(Environment.getExternalStorageDirectory(), sendFilename);
                    Uri imageUri = Uri.fromFile(file);
                    if (imageUri != null) {
                        i.setType("image/jpg");
                        i.putExtra(Intent.EXTRA_STREAM, imageUri);
                    } else {
                        i.setType("plain/text");
                    }

                    // i.setType("plain/text");
                    i.putExtra(Intent.EXTRA_TEXT, prepareTextToSend());
                    i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.s_event_report_subject) + " " + __name);

                    i = Intent.createChooser(i, getString(R.string.s_send_report));
                    startActivity(i);
                    WMA.animateStart(View_Activity.this);
                } else {
                    Toast.makeText(View_Activity.this,
                            getString(R.string.s_forbilden_wo_pic),
                            Toast.LENGTH_LONG).show();
                }
                return true;

            case android.R.id.home:
                deleteTempFile();
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
                WMA.animateFinish(View_Activity.this);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // ----------------------------------------------------------------------------------------------------
    // СѓРґР°Р»РµРЅРёРµ РєРѕРЅС‚Р°РєС‚Р°
    private void deleteRecord() {
        final Timer timer = new Timer();
        AlertDialog.Builder builder = new AlertDialog.Builder(View_Activity.this);
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
                        WMA.getDatabase().delete("contacts", "_id=" + contact_ID, null);
                        deleteTempFile();
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
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

    // ------------------------------------------------------------------------------------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {

            case CONT_EDIT:
                Intent intent = new Intent();
                intent.putExtra("CONTACT_ID", contact_ID);
                setResult(RESULT_OK, intent);
                deleteTempFile();
                finish();
                break;
        }

    }

    private void deleteTempFile() {
        File file = new File(Environment.getExternalStorageDirectory(), sendFilename);
        if (file.exists())
            file.delete();
    }

    private Bitmap getOneRecordImageValue(String FieldName, long ID) {
        int i = 0;
        int rdbytes = 200000;
        Bitmap theImage = null;
        byte[] resall = null;
        int theImagePos = 0;
        try {
            while (rdbytes == 200000 & i < 30) {
                Cursor cursor = WMA.getDatabase().rawQuery(
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
                theImage = BitmapFactory.decodeByteArray(resall, 0, resall.length);
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
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
                WMA.animateFinish(View_Activity.this);
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    // -----------------------------------------------------------------------------------------
    private void copyExportDataBase() {
        try {
            InputStream externalDbStream = this.getAssets().open(WMA.DB_NAMEEXPORT);
            String outFileName = WMA.DB_PATH + SendDBFileName;
            OutputStream localDbStream = new FileOutputStream(outFileName);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = externalDbStream.read(buffer)) > 0) {
                localDbStream.write(buffer, 0, bytesRead);
            }
            localDbStream.close();
            externalDbStream.close();
        } catch (IOException e) {
            Log.i("XXX", "_________Copying error " + e.toString());
        }
    }

    private void exportDetRecord(String __name, String __sendString) {

        WMA.deleteTempFile(SendDBFileName);
        SendDBFileName = WMA.generValidFileName(__name);
        copyExportDataBase();
        copyRecordToExport();

        File file = new File(WMA.DB_PATH, SendDBFileName);
        Uri imageUri = Uri.fromFile(file);

        Intent i = new Intent(Intent.ACTION_SEND);
        if (imageUri != null) {
            i.setType("*/*");
            i.putExtra(Intent.EXTRA_STREAM, imageUri);
        } else {
            i.setType("plain/text");
        }

        i.putExtra(Intent.EXTRA_TEXT, __sendString);
        i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.s_event_report_subject) + " " + __name);
        i = Intent.createChooser(i, getString(R.string.s_send_report));
        startActivity(i);
        WMA.animateStart(View_Activity.this);
    }

    private void copyRecordToExport() {
        String dbNameExport = WMA.DB_PATH + SendDBFileName;
        SQLiteDatabase databaseExport = SQLiteDatabase.openDatabase(dbNameExport, null, SQLiteDatabase.OPEN_READWRITE);

        String sql_mas = "select _id,name,tip,country,region,consist,barcode,firma,"
                + "god,emk,alk,sug,rating,price,dates,code,comment,username,geo,nameup,searchup,"
                + "datetimes,pict from contacts where _id=" + contact_ID;
        Cursor c_mas = WMA.getDatabase().rawQuery(sql_mas, null);
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

        databaseExport.insert("contacts", null, recMaster);
        c_mas.close();

        databaseExport.close();
    }

    private byte[] getOneByteArrayImageValue(String FieldName, long ID) {
        int i = 0;
        int rdbytes = 200000;
        byte[] resall = null;
        int theImagePos = 0;
        try {
            while (rdbytes == 200000 & i < 30) {
                Cursor cursor = WMA.getDatabase().rawQuery(
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

        } catch (Exception ignored) {
        }
        return resall;
    }

}