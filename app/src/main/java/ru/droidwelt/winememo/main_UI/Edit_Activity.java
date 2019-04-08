package ru.droidwelt.winememo.main_UI;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;

import com.ortiz.touchview.TouchImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import ru.droidwelt.winememo.R;
import ru.droidwelt.winememo.WMA;
import ru.droidwelt.winememo.common.ExtendedViewPager;

@SuppressWarnings("deprecation")
public class Edit_Activity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    private final String imageFilename_photo = "winmemo_photo.jpg";
    private final String imageFilename_crop = "winmemo_crop.jpg";
    private static int contact_ID = 0; // id изменяемого контакта
    private EditText nameEditText;
    private EditText tipEditText;
    private EditText countryEditText;
    private EditText regionEditText;
    private EditText consistEditText;
    private EditText firmaEditText;
    private EditText godEditText;
    private EditText emkEditText;
    private EditText alkEditText;
    private EditText sugEditText;
    private EditText commentEditText;
    private EditText priceEditText;
    private EditText datesEditText;
    private EditText barcodeEditText;
    private ImageView ratingImageView;
    private static Uri outputFileUri_photo; // куда сохраняется наше фото
    private static Uri outputFileUri_crop; // куда сохраняется наше фото
    private static ExtendedViewPager mViewPager;
    private static TouchImageAdapterEdit _touchImageAdapter;

    private AlertDialog.Builder adb_xxx;
    private static int choice_mode = 0;

    private static Bitmap imageBig1;
    private static Bitmap imageBig2;

    private static final int GALLERY_REQUEST = 1;
    private static final int CAMERA_CAPTURE = 2;
    private static final int PIC_CROP = 3;

    private String __name = "";
    private String __tip = "";
    private String __country = "";
    private String __region = "";
    private String __consist = "";
    private String __firma = "";
    private String __god = "";
    private String __emk = "";
    private String __alk = "";
    private String __sug = "";
    private String __rating = "";
    private String ratingCurrent = "";
    private String __comment = "";
    private String __price = "";
    private String __dates = "";
    private String __code = "";
    private String __nameup = "";
    private String __searchup = "";
    private String __barcode = "";
    private static boolean __imagemodified1, __imagemodified2;

    private int myYear = 0;
    private int myMonth = 0;
    private int myDay = 0;

    private Cursor choice_cursor;

    private static class TouchImageAdapterEdit extends PagerAdapter {
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
                if (imageBig1 != null) {
                    img.setImageBitmap(imageBig1);
                } else {
                    img.setImageResource(images[position]);
                }
            }

            if (position == 1) {
                if (imageBig2 != null)
                    img.setImageBitmap(imageBig2);
                else
                    img.setImageResource(images[position]);
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
        choice_mode = 0;
        setContentView(R.layout.edit_activity);

        Toolbar tb_edit = findViewById(R.id.tb_edit);
        setSupportActionBar(tb_edit);

        LinearLayout ly_graphic = findViewById(R.id.ly_graphic);
        ViewGroup.LayoutParams params = ly_graphic.getLayoutParams();
        params.height = WMA.getDisplaySizeX() * 4 / 3;
        ly_graphic.setLayoutParams(new LinearLayout.LayoutParams(params));

        ImageButton btn_choice_tip = findViewById(R.id.edit_choice_tip);
        btn_choice_tip.setOnClickListener(oclBtnOk);

        ImageButton btn_choice_country = findViewById(R.id.edit_choice_country);
        btn_choice_country.setOnClickListener(oclBtnOk);

        ImageButton btn_choice_region = findViewById(R.id.edit_choice_region);
        btn_choice_region.setOnClickListener(oclBtnOk);

        ImageButton btn_choice_consist = findViewById(R.id.edit_choice_consist);
        btn_choice_consist.setOnClickListener(oclBtnOk);

        ImageButton btn_choice_firma = findViewById(R.id.edit_choice_firma);
        btn_choice_firma.setOnClickListener(oclBtnOk);

        ImageButton btn_choice_god = findViewById(R.id.edit_choice_god);
        btn_choice_god.setOnClickListener(oclBtnOk);

        ImageButton btn_choice_emk = findViewById(R.id.edit_choice_emk);
        btn_choice_emk.setOnClickListener(oclBtnOk);

        ImageButton btn_choice_alk = findViewById(R.id.edit_choice_alk);
        btn_choice_alk.setOnClickListener(oclBtnOk);

        ImageButton btn_choice_sug = findViewById(R.id.edit_choice_sug);
        btn_choice_sug.setOnClickListener(oclBtnOk);

        ImageButton btn_choice_rating = findViewById(R.id.edit_choice_rating);
        btn_choice_rating.setOnClickListener(oclBtnOk);

        ImageButton btn_choice_scan = findViewById(R.id.edit_choice_scan);
        btn_choice_scan.setOnClickListener(oclBtnOk);

        ImageButton btn_choice_paste = findViewById(R.id.edit_choice_paste);
        btn_choice_paste.setOnClickListener(oclBtnOk);

        ImageButton btn_choice_date = findViewById(R.id.edit_choice_date);
        btn_choice_date.setOnClickListener(oclBtnOk);

        nameEditText = findViewById(R.id.edit_name);
        tipEditText = findViewById(R.id.edit_tip);
        countryEditText = findViewById(R.id.edit_country);
        regionEditText = findViewById(R.id.contact_EditText_region);
        consistEditText = findViewById(R.id.edit_consist);
        firmaEditText = findViewById(R.id.edit_firma);
        godEditText = findViewById(R.id.edit_god);
        emkEditText = findViewById(R.id.edit_emk);
        alkEditText = findViewById(R.id.edit_alk);
        sugEditText = findViewById(R.id.edit_sug);
        ratingImageView = findViewById(R.id.edit_rating);
        commentEditText = findViewById(R.id.edit_comment);
        priceEditText = findViewById(R.id.edit_price);
        datesEditText = findViewById(R.id.edit_dt);
        barcodeEditText = findViewById(R.id.edit_barcode);

        String currentDateTimeString = (String) DateFormat.format("yyyy-MM-dd", new Date());
        datesEditText.setText(currentDateTimeString);
        mViewPager = findViewById(R.id.edit_graphic);

        switch (WMA.get_picture_bgr(Edit_Activity.this)) {
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
        if (extras != null) {
            contact_ID = extras.getInt("CONTACT_ID");
            loadRecord();
            loadRecordEditTask.execute();
        } else {
            if (_touchImageAdapter == null) {
                imageBig1 = null;
                imageBig2 = null;
                _touchImageAdapter = new TouchImageAdapterEdit();
            }
            mViewPager.setAdapter(_touchImageAdapter);
        }

        getSupportLoaderManager().initLoader(0, null, this);
        adb_xxx = new AlertDialog.Builder(Edit_Activity.this, R.style.AlertDialogColorButton);
        adb_xxx.setIcon(R.drawable.ic_launcher);
    }

    private void loadRecord() {
        Cursor c = WMA.getDatabase().query("contacts",
                new String[]{"_id", "name", "tip", "country", "region",
                        "consist", "firma", "god", "emk", "alk", "sug",
                        "rating", "price", "dates", "code", "comment",
                        "barcode", "nameup", "searchup"}, "_id=" + contact_ID,
                null, null, null, null);
        c.moveToFirst();

        int nameIndex = c.getColumnIndex("name");
        int tipIndex = c.getColumnIndex("tip");
        int countyIndex = c.getColumnIndex("country");
        int regionIndex = c.getColumnIndex("region");
        int consistIndex = c.getColumnIndex("consist");
        int firmaIndex = c.getColumnIndex("firma");
        int godIndex = c.getColumnIndex("god");
        int emkIndex = c.getColumnIndex("emk");
        int alkIndex = c.getColumnIndex("alk");
        int sugIndex = c.getColumnIndex("sug");
        int ratingIndex = c.getColumnIndex("rating");
        int commentIndex = c.getColumnIndex("comment");
        int priceIndex = c.getColumnIndex("price");
        int datesIndex = c.getColumnIndex("dates");
        int codeIndex = c.getColumnIndex("code");
        int searchupIndex = c.getColumnIndex("searchup");
        int nameupIndex = c.getColumnIndex("nameup");
        int barcodeIndex = c.getColumnIndex("barcode");

        __name = WMA.strnormalize(c.getString(nameIndex));
        __tip = WMA.strnormalize(c.getString(tipIndex));
        __country = WMA.strnormalize(c.getString(countyIndex));
        __region = WMA.strnormalize(c.getString(regionIndex));
        __consist = WMA.strnormalize(c.getString(consistIndex));
        __firma = WMA.strnormalize(c.getString(firmaIndex));
        __god = WMA.strnormalize(c.getString(godIndex));
        __emk = WMA.strnormalize(c.getString(emkIndex));
        __alk = WMA.strnormalize(c.getString(alkIndex));
        __sug = WMA.strnormalize(c.getString(sugIndex));
        __rating = WMA.strnormalize(c.getString(ratingIndex));
        __comment = WMA.strnormalize(c.getString(commentIndex));
        __price = WMA.strnormalize(c.getString(priceIndex));
        __dates = WMA.strnormalize(c.getString(datesIndex));
        __code = WMA.strnormalize(c.getString(codeIndex));
        __nameup = WMA.strnormalize(c.getString(nameupIndex));
        __searchup = WMA.strnormalize(c.getString(searchupIndex));
        __barcode = WMA.strnormalize(c.getString(barcodeIndex));

        nameEditText.setText(__name);
        tipEditText.setText(__tip);
        countryEditText.setText(__country);
        regionEditText.setText(__region);
        consistEditText.setText(__consist);
        firmaEditText.setText(__firma);
        godEditText.setText(__god);
        emkEditText.setText(__emk);
        alkEditText.setText(__alk);
        sugEditText.setText(__sug);
        ratingCurrent = (__rating);
        showRating();
        commentEditText.setText(__comment);
        priceEditText.setText(__price);
        datesEditText.setText(__dates);
        barcodeEditText.setText(__barcode);

        __imagemodified1 = false;
        __imagemodified2 = false;
        c.close();
    }

    @SuppressLint("StaticFieldLeak")
    private final
    AsyncTask<Long, Object, Object> saveRecordEditTask = new AsyncTask<Long, Object, Object>() {
        @Override
        protected Object doInBackground(Long... params) {
            saveRecordExecute();
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            Intent intent = new Intent();
            intent.putExtra("CONTACT_NEW_ID", contact_ID);
            setResult(RESULT_OK, intent);
            deleteTempFile();
            finish();
            WMA.animateFinish(Edit_Activity.this);
        }
    };

    @SuppressLint("DefaultLocale")
    private void saveRecordPrepare() {
        __name = nameEditText.getText().toString().trim();
        __tip = tipEditText.getText().toString().trim();
        __country = countryEditText.getText().toString().trim();
        __region = regionEditText.getText().toString().trim();
        __consist = consistEditText.getText().toString().trim();
        __firma = firmaEditText.getText().toString().trim();
        __god = godEditText.getText().toString().trim();
        __emk = emkEditText.getText().toString().trim();
        __alk = alkEditText.getText().toString().trim()
                .toLowerCase(java.util.Locale.ROOT);
        __sug = sugEditText.getText().toString().trim()
                .toLowerCase(java.util.Locale.ROOT);
        __rating = ratingCurrent;
        __comment = commentEditText.getText().toString().trim();
        __price = priceEditText.getText().toString().trim();
        __dates = datesEditText.getText().toString().trim();
        __barcode = barcodeEditText.getText().toString().trim();

        if ((!__alk.equals("")) & (!__alk.contains("%")))
            __alk = __alk + " %";
        if ((!__sug.equals(""))
                & (!__sug.contains(getString(R.string.s_sug_contain))))
            __sug = __sug + " " + getString(R.string.s_sug_contain);

        __nameup = __name.toUpperCase();
        __searchup = (__name + __tip + __country + __region + __consist
                + __firma + __god + __alk + __sug).toUpperCase();
    }

    private void saveRecordExecute() {
        byte[] imageInByte1 = null;
        byte[] imageInByte2 = null;
        byte[] imageInByteSmall = null;
        int quality_big = 75;
        int quality_small = 75;

        if ((__imagemodified1) && (imageBig1 != null)) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBig1.compress(Bitmap.CompressFormat.JPEG, quality_big, stream);
            imageInByte1 = stream.toByteArray();

            ByteArrayOutputStream streamSmall = new ByteArrayOutputStream();
            Bitmap imageSmall = Bitmap.createScaledBitmap(imageBig1,
                    WMA.get_picturesmall_screen_x(Edit_Activity.this),
                    WMA.get_picturesmall_screen_y(Edit_Activity.this), false);
            imageSmall.compress(Bitmap.CompressFormat.JPEG, quality_small, streamSmall);
            imageInByteSmall = streamSmall.toByteArray();
        }

        if ((__imagemodified2) && (imageBig2 != null)) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBig2.compress(Bitmap.CompressFormat.JPEG, quality_big, stream);
            imageInByte2 = stream.toByteArray();
        }

        if (getIntent().getExtras() == null) {
            contact_ID = insertRecord(__name, __tip, __country, __region,
                    __consist, __firma, __god, __emk, __alk, __sug, __rating,
                    __price, __comment, __dates, __code, __barcode, __nameup,
                    __searchup, imageInByte1, imageInByte2, imageInByteSmall,
                    __imagemodified1, __imagemodified2);
        } else {
            updateRecord(contact_ID, __name, __tip, __country, __region,
                    __consist, __firma, __god, __emk, __alk, __sug, __rating,
                    __price, __comment, __dates, __code, __barcode, __nameup,
                    __searchup, imageInByte1, imageInByte2, imageInByteSmall,
                    __imagemodified1, __imagemodified2);
        }

        Intent intent = new Intent();
        intent.putExtra("CONTACT_NEW_ID", contact_ID);
        setResult(RESULT_OK, intent);
    }

    // сохранение записи в базе данных----------------------------
    private void saveRecord() {
        saveRecordPrepare();
        saveRecordEditTask.execute();
    }

    // --------------------------------------------------------------------------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent returnedIntent) {
        super.onActivityResult(requestCode, resultCode, returnedIntent);

        if (resultCode == RESULT_OK) {

            switch (requestCode) {

                case GALLERY_REQUEST:
                    outputFileUri_photo = returnedIntent.getData();
                    if (outputFileUri_photo == null) {
                        WMA.DisplayToastError(R.string.s_no_request_image_from_gallery_7);
                        break;
                    }
                    performCrop();
                    break;

                case CAMERA_CAPTURE: // после CAMERA
                    performCrop();
                    break;

                case PIC_CROP: // после CROP
                    int itemVP = mViewPager.getCurrentItem();
                    // Log.i ("EEE","itemVP="+itemVP);
                    try {
                        if (itemVP == 0) {
                            imageBig1 = Media.getBitmap(getContentResolver(), outputFileUri_crop);
                            __imagemodified1 = true;
                        } else {
                            imageBig2 = Media.getBitmap(getContentResolver(), outputFileUri_crop);
                            __imagemodified2 = true;
                        }
                    } catch (Exception e) {
                        WMA.DisplayToastError(R.string.s_no_prepate_after_crop_5);
                        break;
                    }
                    mViewPager.setCurrentItem(itemVP);
                    mViewPager.setAdapter(_touchImageAdapter);
                    break;

                case WMA.EXIT_SCANNER_CODE:
                    if (returnedIntent != null) {
                        String contents = returnedIntent.getStringExtra("SCAN_RESULT");
                        if ((contents != null) & (!(contents != null && contents.equals(""))))
                            barcodeEditText.setText(contents.trim());
                    }
                    break;

            }
        }
    }

    // ------------------------------------------------------------------------------------------------------------------------
    private void performCrop() {

        File file = new File(Environment.getExternalStorageDirectory(), imageFilename_crop);
        if (file.exists())
            if (!file.delete()) return;
        outputFileUri_crop = Uri.fromFile(file);
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        cropIntent.setDataAndType(outputFileUri_photo, "image/*");
        cropIntent.putExtra("crop", "true");
        cropIntent.putExtra("aspectX", 3);
        cropIntent.putExtra("aspectY", 4);
        cropIntent.putExtra("outputX", WMA.get_picturebig_screen_x(Edit_Activity.this));
        cropIntent.putExtra("outputY", WMA.get_picturebig_screen_y(Edit_Activity.this));
        cropIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        cropIntent.putExtra("output", outputFileUri_crop);
        cropIntent.putExtra("noFaceDetection", true);
        startActivityForResult(cropIntent, PIC_CROP);
    }

    // подключение меню----------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_menu, menu);
        return true;
    }

    // -------------------меню-----------------------------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.edit_menuItem_save:
                if (nameEditText.getText().length() == 0) {
                    String s = "_" + DateFormat.format("yyyy-MM-dd hh:mm:ss", new Date());
                    nameEditText.setText(s);
                }
                saveRecord();
                return true;

            case R.id.edit_menuItem_pic_choice:
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
                return true;

            case R.id.edit_menuItem_pic_photo:
                Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                deleteTempFile();
                File file = new File(Environment.getExternalStorageDirectory(), imageFilename_photo);
                outputFileUri_photo = Uri.fromFile(file);
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri_photo);

                startActivityForResult(captureIntent, CAMERA_CAPTURE);
                return true;

            case R.id.edit_menuItem_pic_clear:
                int itemVP = mViewPager.getCurrentItem();
                if (itemVP == 0)
                    imageBig1 = null;
                else
                    imageBig2 = null;
                mViewPager.setAdapter(_touchImageAdapter);
                mViewPager.setCurrentItem(itemVP);
                return true;

            case android.R.id.home:
                if (isRecordModified()) {
                    openQuitDialogMy();
                } else {
                    deleteTempFile();
                    finish();
                    WMA.animateFinish(Edit_Activity.this);
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showRating() {
        ratingImageView.setImageResource(R.drawable.ic_rating_0);
        if (ratingCurrent != null) {
            if (ratingCurrent.equals("1")) ratingImageView.setImageResource(R.drawable.ic_rating_1);
            if (ratingCurrent.equals("2")) ratingImageView.setImageResource(R.drawable.ic_rating_2);
            if (ratingCurrent.equals("3")) ratingImageView.setImageResource(R.drawable.ic_rating_3);
            if (ratingCurrent.equals("4")) ratingImageView.setImageResource(R.drawable.ic_rating_4);
            if (ratingCurrent.equals("5")) ratingImageView.setImageResource(R.drawable.ic_rating_5);
        }
    }

    // ------------------------------------------------------------------------------------------------------------------------
    private boolean isRecordModified() {
        return __imagemodified1
                | __imagemodified2
                | !(__name.compareToIgnoreCase(nameEditText.getText().toString()) == 0)
                | !(__tip.compareToIgnoreCase(tipEditText.getText().toString()) == 0)
                | !(__country.compareToIgnoreCase(countryEditText.getText().toString()) == 0)
                | !(__region.compareToIgnoreCase(regionEditText.getText().toString()) == 0)
                | !(__consist.compareToIgnoreCase(consistEditText.getText().toString()) == 0)
                | !(__firma.compareToIgnoreCase(firmaEditText.getText().toString()) == 0)
                | !(__god.compareToIgnoreCase(godEditText.getText().toString()) == 0)
                | !(__emk.compareToIgnoreCase(emkEditText.getText().toString()) == 0)
                | !(__alk.compareToIgnoreCase(alkEditText.getText().toString()) == 0)
                | !(__sug.compareToIgnoreCase(sugEditText.getText().toString()) == 0)
                | !(__rating.compareToIgnoreCase(ratingCurrent) == 0)
                | !(__comment.compareToIgnoreCase(commentEditText.getText().toString()) == 0)
                | !(__barcode.compareToIgnoreCase(barcodeEditText.getText().toString()) == 0)
                | !(__price.compareToIgnoreCase(priceEditText.getText().toString()) == 0)
                | !(__dates.compareToIgnoreCase(datesEditText.getText().toString()) == 0)
                | (__nameup.equals(""))
                | (__searchup.equals(""));
    }

    // защита от закрытия по Back------------------------------------
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {

            case KeyEvent.KEYCODE_BACK:
                if (isRecordModified()) {
                    openQuitDialogMy();
                } else {
                    finish();
                    WMA.animateFinish(Edit_Activity.this);
                }
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void deleteTempFile() {
        try {
            File file = new File(Environment.getExternalStorageDirectory(), imageFilename_photo);
            if (file.exists())
                if (!file.delete()) return;
        } catch (Exception ignored) {
        }
        try {
            File file = new File(Environment.getExternalStorageDirectory(), imageFilename_crop);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception ignored) {
        }
    }

    // --------Диалог переспроса о выходе-----------------
    private void openQuitDialogMy() {
        final Timer timer = new Timer();
        AlertDialog.Builder builder = new AlertDialog.Builder(Edit_Activity.this, R.style.AlertDialogColorButton);
        builder.setTitle(R.string.s_exit_wo_save);

        builder.setPositiveButton(R.string.s_yes, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                timer.purge();
                timer.cancel();
                deleteTempFile();
                finish();
                WMA.animateFinish(Edit_Activity.this);
            }
        });

        builder.setNegativeButton(R.string.s_no, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                timer.purge();
                timer.cancel();
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

    // обработчик нажатия на пункт списка диалога
    private final OnClickListener tip_choice_ClickListener = new OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            choice_cursor.moveToPosition(which);
            tipEditText.setText(choice_cursor.getString(choice_cursor.getColumnIndex("res")));
            dialog.dismiss();
        }
    };

    private final OnClickListener country_choice_ClickListener = new OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            choice_cursor.moveToPosition(which);
            countryEditText.setText(choice_cursor.getString(choice_cursor.getColumnIndex("res")));
            dialog.dismiss();
        }
    };

    private final OnClickListener region_choice_ClickListener = new OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            choice_cursor.moveToPosition(which);
            regionEditText.setText(choice_cursor.getString(choice_cursor.getColumnIndex("res")));
            dialog.dismiss();
        }
    };

    private final OnClickListener consist_choice_ClickListener = new OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            choice_cursor.moveToPosition(which);
            consistEditText.setText(choice_cursor.getString(choice_cursor.getColumnIndex("res")));
            dialog.dismiss();
        }
    };

    private final OnClickListener consistAdd_choice_ClickListener = new OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            choice_cursor.moveToPosition(which);
            String s = consistEditText.getText().toString() + " " + choice_cursor.getString(choice_cursor.getColumnIndex("res"));
            consistEditText.setText(s);
            dialog.dismiss();
        }
    };

    private final OnClickListener firma_choice_ClickListener = new OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            choice_cursor.moveToPosition(which);
            firmaEditText.setText(choice_cursor.getString(choice_cursor.getColumnIndex("res")));
            dialog.dismiss();
        }
    };

    private final OnClickListener god_choice_ClickListener = new OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            choice_cursor.moveToPosition(which);
            godEditText.setText(choice_cursor.getString(choice_cursor.getColumnIndex("res")));
            dialog.dismiss();
        }
    };

    private final OnClickListener emk_choice_ClickListener = new OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            choice_cursor.moveToPosition(which);
            emkEditText.setText(choice_cursor.getString(choice_cursor.getColumnIndex("res")));
            dialog.dismiss();
        }
    };

    private final OnClickListener alk_choice_ClickListener = new OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            choice_cursor.moveToPosition(which);
            alkEditText.setText(choice_cursor.getString(choice_cursor.getColumnIndex("res")));
            dialog.dismiss();
        }
    };

    private final OnClickListener sug_choice_ClickListener = new OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            choice_cursor.moveToPosition(which);
            sugEditText.setText(choice_cursor.getString(choice_cursor.getColumnIndex("res")));
            dialog.dismiss();
        }
    };

    // ------------------------------слушатель установки даты---------------------------
    private final OnDateSetListener myCallBack = new OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            myYear = year;
            myMonth = monthOfYear + 1;
            myDay = dayOfMonth;
            String s = myYear + "-" + myMonth + '-' + myDay;
            datesEditText.setText(s);
        }
    };

    // -------------------------------------------------------------------------------------------------------------------------
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new MyCursorLoader(this);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> arg0) {
    }

    private static class MyCursorLoader extends CursorLoader {

        MyCursorLoader(Context context) {
            super(context);
        }

        @Override
        public Cursor loadInBackground() {
            return getChoiceDict("xxx");
        }

    }

    private static Cursor getChoiceDict(String tbl) {
        String sql = "select max(_id) as _id,name as res from dict where tablename='"
                + tbl + "' and name<>'' " + " group by  res " + " order by res";
        return WMA.getDatabase().rawQuery(sql, null);
    }

    private static Cursor getChoiceList(String tbl) {
        String sql = "select max(_id) as _id, res  from "
                + "(select _id+1000000 as _id,name as res from dict where tablename='"
                + tbl + "' and name<>''  " + " and (lang='' or lang='"
                + WMA.getLangprefix() + "') " + " union " + " select _id,"
                + tbl
                + " as res from contacts where not (name like '%***%') and "
                + tbl + "<>'') " + " group by  res " + " order by res";
        return WMA.getDatabase().rawQuery(sql, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> arg0, Cursor arg1) {

        switch (choice_mode) {
            case 1:
                adb_xxx.setTitle(R.string.s_fld_tip);
                adb_xxx.setCursor(choice_cursor, tip_choice_ClickListener, "res");
                adb_xxx.show();
                break;

            case 2:
                adb_xxx.setTitle(R.string.s_fld_country);
                adb_xxx.setCursor(choice_cursor, country_choice_ClickListener, "res");
                adb_xxx.show();
                break;

            case 3:
                adb_xxx.setTitle(R.string.s_fld_region);
                adb_xxx.setCursor(choice_cursor, region_choice_ClickListener, "res");
                adb_xxx.show();
                break;

            case 4:
                adb_xxx.setTitle(R.string.s_fld_contens);
                if (consistEditText.getText().toString().equals("")) {
                    adb_xxx.setCursor(choice_cursor, consist_choice_ClickListener, "res");
                } else {
                    adb_xxx.setCursor(choice_cursor, consistAdd_choice_ClickListener, "res");
                }
                adb_xxx.show();
                break;

            case 5:
                adb_xxx.setTitle(R.string.s_fld_firma);
                adb_xxx.setCursor(choice_cursor, firma_choice_ClickListener, "res");
                adb_xxx.show();
                break;

            case 6:
                adb_xxx.setTitle(R.string.s_fld_god);
                adb_xxx.setCursor(choice_cursor, god_choice_ClickListener, "res");
                adb_xxx.show();
                break;

            case 7:
                adb_xxx.setTitle(R.string.s_fld_emk);
                adb_xxx.setCursor(choice_cursor, emk_choice_ClickListener, "res");
                adb_xxx.show();
                break;

            case 8:
                adb_xxx.setTitle(R.string.s_fld_alk);
                adb_xxx.setCursor(choice_cursor, alk_choice_ClickListener, "res");
                adb_xxx.show();
                break;

            case 9:
                adb_xxx.setTitle(R.string.s_fld_sug);
                adb_xxx.setCursor(choice_cursor, sug_choice_ClickListener, "res");
                adb_xxx.show();
                break;

            default:
                break;
        }
    }


    private void choiceRating() {
        final AlertDialog.Builder ratingdialog = new AlertDialog.Builder(Edit_Activity.this, R.style.AlertDialogColorButton);
        @SuppressWarnings("Annotator") @SuppressLint("InflateParams")
        View rating_ll = getLayoutInflater().inflate(R.layout.ratingdialog, null);
        ratingdialog.setTitle(R.string.s_fld_rating);
        ratingdialog.setIcon(android.R.drawable.btn_star_big_on);
        ratingdialog.setView(rating_ll);
        final RatingBar rating = rating_ll.findViewById(R.id.ratingbar);
        float f_rating;
        try {
            f_rating = Float.valueOf(ratingCurrent);
        } catch (Exception e) {
            f_rating = 0;
        }

        rating.setRating(f_rating);
        ratingdialog.setPositiveButton(R.string.s_ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        float frating = rating.getRating();
                        int irating = Math.round(frating);
                        ratingCurrent = String.valueOf(irating);
                        showRating();
                        dialog.dismiss();
                    }
                })

                .setNegativeButton(R.string.s_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        ratingdialog.create();
        ratingdialog.show();
    }


    // ----------------слушатель нажатия на кнопку
    private final View.OnClickListener oclBtnOk = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {

                case R.id.edit_choice_tip:
                    choice_cursor = getChoiceList("tip");
                    choice_mode = 1;
                    Objects.requireNonNull(getSupportLoaderManager().getLoader(0)).forceLoad();
                    break;

                case R.id.edit_choice_country:
                    choice_cursor = getChoiceList("country");
                    choice_mode = 2;
                    Objects.requireNonNull(getSupportLoaderManager().getLoader(0)).forceLoad();
                    break;

                case R.id.edit_choice_region:
                    choice_cursor = getChoiceList("region");
                    choice_mode = 3;
                    Objects.requireNonNull(getSupportLoaderManager().getLoader(0)).forceLoad();
                    break;

                case R.id.edit_choice_consist:
                    if (consistEditText.getText().toString().equals("")) {
                        choice_cursor = getChoiceList("consist");
                    } else {
                        choice_cursor = getChoiceDict("consist");
                    }
                    choice_mode = 4;
                    Objects.requireNonNull(getSupportLoaderManager().getLoader(0)).forceLoad();
                    break;

                case R.id.edit_choice_firma:
                    choice_cursor = getChoiceList("firma");
                    choice_mode = 5;
                    Objects.requireNonNull(getSupportLoaderManager().getLoader(0)).forceLoad();
                    break;

                case R.id.edit_choice_god:
                    choice_cursor = getChoiceList("god");
                    choice_mode = 6;
                    Objects.requireNonNull(getSupportLoaderManager().getLoader(0)).forceLoad();
                    break;

                case R.id.edit_choice_emk:
                    choice_cursor = getChoiceList("emk");
                    choice_mode = 7;
                    Objects.requireNonNull(getSupportLoaderManager().getLoader(0)).forceLoad();
                    break;

                case R.id.edit_choice_alk:
                    choice_cursor = getChoiceList("alk");
                    choice_mode = 8;
                    Objects.requireNonNull(getSupportLoaderManager().getLoader(0)).forceLoad();
                    break;

                case R.id.edit_choice_sug:
                    choice_cursor = getChoiceList("sug");
                    choice_mode = 9;
                    Objects.requireNonNull(getSupportLoaderManager().getLoader(0)).forceLoad();
                    break;

                case R.id.edit_choice_rating:
                    choiceRating();
                    break;

                case R.id.edit_choice_paste:
                    try {
                        if (!(WMA.getLast_scanned_code() == null) & !(WMA.getLast_scanned_code().equals(""))) {
                            barcodeEditText.setText(WMA.getLast_scanned_code());
                        }
                    } catch (Exception ignored) {
                    }
                    break;

                case R.id.edit_choice_scan:
                    WMA.startScanner(Edit_Activity.this);
                    break;

                case R.id.edit_choice_date:
                    if (myYear == 0) {
                        long currentTime = System.currentTimeMillis();
                        Calendar cal = Calendar.getInstance();
                        cal.setTimeInMillis(currentTime);
                        myYear = cal.get(Calendar.YEAR);
                        myMonth = cal.get(Calendar.MONTH);
                        myDay = cal.get(Calendar.DAY_OF_MONTH);
                    }
                    DatePickerDialog tpd = new DatePickerDialog(Edit_Activity.this, myCallBack, myYear, myMonth, myDay);
                    tpd.show();
                    break;

                default:
                    break;
            }
        }
    };

    @SuppressLint("StaticFieldLeak")
    private final
    AsyncTask<Long, Object, Object> loadRecordEditTask = new AsyncTask<Long, Object, Object>() {
        @Override
        protected Object doInBackground(Long... params) {
            imageBig1 = getOneRecordImageValue("graphic1", contact_ID);
            imageBig2 = getOneRecordImageValue("graphic2", contact_ID);
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            _touchImageAdapter = new TouchImageAdapterEdit();
            mViewPager.setAdapter(_touchImageAdapter);
        }
    };

    private static int insertRecord(String name, String tip, String country,
                                    String region, String consist, String firma, String god,
                                    String emk, String alk, String sug, String rating, String price,
                                    String comment, String dates, String code, String barcode,
                                    String nameup, String searchup, byte[] graphic1, byte[] graphic2,
                                    byte[] pict, boolean imagemodified1, boolean imagemodified2) {
        ContentValues newContact = new ContentValues();
        newContact.put("name", name);
        newContact.put("tip", tip);
        newContact.put("country", country);
        newContact.put("region", region);
        newContact.put("consist", consist);
        newContact.put("firma", firma);
        newContact.put("god", god);
        newContact.put("emk", emk);
        newContact.put("alk", alk);
        newContact.put("sug", sug);
        newContact.put("rating", rating);
        newContact.put("price", price);
        newContact.put("comment", comment);
        newContact.put("dates", dates);
        newContact.put("code", code);
        newContact.put("barcode", barcode);
        newContact.put("nameup", nameup);
        newContact.put("searchup", searchup);
        if (imagemodified1) {
            newContact.put("graphic1", graphic1);
            newContact.put("pict", pict);
        }
        if (imagemodified2) {
            newContact.put("graphic2", graphic2);
        }

        int new_id = (int) WMA.getDatabase().insert("contacts", null, newContact);
        WMA.MSA_ID = new_id;
        return new_id;
    }

    private static void updateRecord(int id, String name, String tip,
                                     String country, String region, String consist, String firma,
                                     String god, String emk, String alk, String sug, String rating,
                                     String price, String comment, String dates, String code,
                                     String barcode, String nameup, String searchup, byte[] graphic1,
                                     byte[] graphic2, byte[] pict, boolean imagemodified1,
                                     boolean imagemodified2) {
        ContentValues editContact = new ContentValues();
        editContact.put("name", name);
        editContact.put("tip", tip);
        editContact.put("country", country);
        editContact.put("region", region);
        editContact.put("consist", consist);
        editContact.put("firma", firma);
        editContact.put("dates", dates);
        editContact.put("god", god);
        editContact.put("emk", emk);
        editContact.put("alk", alk);
        editContact.put("sug", sug);
        editContact.put("rating", rating);
        editContact.put("price", price);
        editContact.put("comment", comment);
        editContact.put("dates", dates);
        editContact.put("code", code);
        editContact.put("barcode", barcode);
        editContact.put("nameup", nameup);
        editContact.put("searchup", searchup);
        if (imagemodified1) {
            editContact.put("graphic1", graphic1);
            editContact.put("pict", pict);
        }
        if (imagemodified2) {
            editContact.put("graphic2", graphic2);
        }
        WMA.getDatabase().update("contacts", editContact, "_id=" + id, null);
    }

    private Bitmap getOneRecordImageValue(String fieldName, int ID) {
        int i = 0;
        int rdbytes = 200000;
        Bitmap theImage = null;
        byte[] resall = null;
        int theImagePos = 0;
        try {
            while (rdbytes == 200000 & i < 30) {
                Cursor cursor = WMA.getDatabase().rawQuery(
                        "select substr(" + fieldName + ",1+"
                                + String.valueOf(i)
                                + "*200000,200000) from contacts where _id = "
                                + ID, null);
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
            if ((resall != null ? resall.length : 0) > 0) {
                theImage = BitmapFactory.decodeByteArray(resall, 0, resall.length);
            }

        } catch (Exception ignored) {
        }
        return theImage;
    }

}
