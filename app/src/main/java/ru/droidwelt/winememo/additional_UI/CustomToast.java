package ru.droidwelt.winememo.additional_UI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ru.droidwelt.winememo.R;


@SuppressLint({"InflateParams","StaticFieldLeak"})
public class CustomToast extends android.widget.Toast {

    private static TextView toastText;
    private static ImageView toastImage;

     private CustomToast(Context context) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;

         @SuppressWarnings("Annotator") View rootView = inflater.inflate(R.layout.toast_info, null);
        toastImage = rootView.findViewById(R.id.iv_mesedit_lbl);
        toastImage.setImageResource(R.drawable.ic_launcher);
        toastText = rootView.findViewById(R.id.textView1);
        this.setView(rootView);
        this.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
        this.setDuration(android.widget.Toast.LENGTH_LONG);
    }


    public static CustomToast makeText(Context context, CharSequence text, int duration, int resId) {
        CustomToast result = new CustomToast(context);
        result.setDuration(duration);
        toastText.setText(text);
        toastImage.setImageResource(resId);
        return result;
    }

    public static CustomToast makeText(Context context, CharSequence text, int duration, Drawable dr) {
        CustomToast result = new CustomToast(context);
        result.setDuration(duration);
        toastText.setText(text);
        toastImage.setImageDrawable(dr);
        return result;
    }


}

