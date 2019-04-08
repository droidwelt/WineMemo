package ru.droidwelt.winememo.additional_UI;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.achartengine.ChartFactory;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import ru.droidwelt.winememo.R;
import ru.droidwelt.winememo.WMA;


public class Diagramm_Activity extends Activity {

    private String[] diagramm_list_field;
    private String[] diagramm_list_name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diagramm_activity);

        // находим список
        ListView lvMain = findViewById(R.id.lv_diagramm);

        // устанавливаем режим выбора пунктов списка
        lvMain.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        // Создаем адаптер, используя массив из файла ресурсов
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.diagramm_list_name, android.R.layout.simple_list_item_single_choice);
        lvMain.setAdapter(adapter);

        // получаем массив из файла ресурсов
        diagramm_list_name = getResources().getStringArray(R.array.diagramm_list_name);
        diagramm_list_field = getResources().getStringArray(R.array.diagramm_list_field);

        lvMain.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                openChart(position);
            }
        });

    }


    private void openChart(int position) {

        Cursor diagramm_cursor = getDiagrammData(diagramm_list_field[position]);
        int count = diagramm_cursor.getCount();
        // Pie Chart Section Value
        double[] values = new double[count];
        // Pie Chart Section Names
        String[] categoryNames = new String[count];
        for (int i = 0; i < count; i++) {
            diagramm_cursor.moveToNext();
            values[i] = diagramm_cursor.getDouble(0);
            String categ = diagramm_cursor.getString(1);
            if (!categ.equals("")) {
                categoryNames[i] = categ;
            } else {
                categoryNames[i] = getResources().getString(R.string.s_not_class);
            }
        }

        diagramm_cursor.close();

        // Color of each Pie Chart Sections
        int[] COLORS = new int[]{getResources().getColor(R.color.c_digr_1),
                getResources().getColor(R.color.c_digr_2),
                getResources().getColor(R.color.c_digr_3),
                getResources().getColor(R.color.c_digr_4),
                getResources().getColor(R.color.c_digr_5),
                getResources().getColor(R.color.c_digr_6),
                getResources().getColor(R.color.c_digr_7),
                getResources().getColor(R.color.c_digr_8),
                getResources().getColor(R.color.c_digr_9),
                getResources().getColor(R.color.c_digr_10)};


        // Instantiating CategorySeries to plot Pie Chart
        CategorySeries distributionSeries = new CategorySeries(
                "");
        for (int i = 0; i < count; i++) {
            // Adding a slice with its values and name to the Pie Chart
            distributionSeries.add(categoryNames[i], values[i]);
        }

        // Instantiating a renderer for the Pie Chart
        DefaultRenderer defaultRenderer = new DefaultRenderer();
        for (int i = 0; i < count; i++) {
            SimpleSeriesRenderer seriesRenderer = new SimpleSeriesRenderer();
            seriesRenderer.setColor(COLORS[(i) % COLORS.length]);
            seriesRenderer.setDisplayChartValues(true);
            // Adding a renderer for a slice
            defaultRenderer.addSeriesRenderer(seriesRenderer);
        }

        defaultRenderer.setChartTitle("");
        defaultRenderer.setChartTitleTextSize(20);
        defaultRenderer.setZoomButtonsVisible(true);
        defaultRenderer.setLabelsColor(Color.BLACK);
        defaultRenderer.setLabelsTextSize(12);
        defaultRenderer.setLegendTextSize(16);
        defaultRenderer.setShowLegend(false);
        defaultRenderer.setApplyBackgroundColor(true);
        defaultRenderer.setBackgroundColor(Color.LTGRAY);
        defaultRenderer.setStartAngle(180);


        // Creating an intent to plot bar chart using dataset and
        // multipleRenderer
        Intent intent = ChartFactory.getPieChartIntent(getBaseContext(), distributionSeries,
                defaultRenderer, diagramm_list_name[position]);

        // Start Activity
        startActivity(intent);

    }

    private static Cursor getDiagrammData(String fld) {
        String sql = "select count(*) as n, " + fld + " as res from contacts group by  res order by res";
        return WMA.getDatabase().rawQuery(sql, null);
    }


}
