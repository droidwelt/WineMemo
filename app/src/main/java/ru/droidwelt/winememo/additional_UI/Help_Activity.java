package ru.droidwelt.winememo.additional_UI;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.droidwelt.winememo.R;

public class Help_Activity extends AppCompatActivity {

    private class MySimpleAdapter extends SimpleAdapter {

        MySimpleAdapter(Context context, List<? extends Map<String, ?>> data, String[] from, int[] to) {
            super(context, data, R.layout.help_item, from, to);
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_activity);

        String[] helplist_qwe = getResources().getStringArray(R.array.help_list_qwe);
        String[] helplist_ans = getResources().getStringArray(R.array.help_list_ans);

        ArrayList<Map<String, Object>> data = new ArrayList<>(helplist_qwe.length);
        Map<String, Object> m;

        for (int i = 0; i < helplist_qwe.length; i++) {
            m = new HashMap<>();
            m.put("QWE", helplist_qwe[i].trim());
            m.put("ANS", helplist_ans[i].trim());
            data.add(m);
        }

        String[] from = {"QWE", "ANS"};
        int[] to = {R.id.help_item1, R.id.help_item2};

        MySimpleAdapter sAdapter = new MySimpleAdapter(this, data, from, to);

        ListView lvMain = findViewById(R.id.lv_help);
        lvMain.setAdapter(sAdapter);
    }


}
