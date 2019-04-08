package ru.droidwelt.winememo.additional_UI;


import android.os.Bundle;
import android.preference.PreferenceActivity;

import ru.droidwelt.winememo.R;


public class Pref_Activity extends PreferenceActivity {


    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref);
    }


}
