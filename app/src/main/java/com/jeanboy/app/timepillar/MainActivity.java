package com.jeanboy.app.timepillar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.jeanboy.component.timepillar.TimePillarView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private List<TimePillarView.DataModel> dataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TimePillarView time_pillar = findViewById(R.id.time_pillar);

        // 6:00-9:00 good
        dataList.add(new TimePillarView.DataModel(1591999200000L, 1592010000000L, false));
        // 15:00-18:00 good
        dataList.add(new TimePillarView.DataModel(1592031600000L, 1592042400000L, false));
        // 22:00-23:00 good
        dataList.add(new TimePillarView.DataModel(1592056800000L, 1592060400000L, false));

        // 3:00-4:00 bad
        dataList.add(new TimePillarView.DataModel(1591988400000L, 1591992000000L, true));
        // 11:00-13:00 bad
        dataList.add(new TimePillarView.DataModel(1592017200000L, 1592024400000L, true));
        // 19:00-23:00 bad
        dataList.add(new TimePillarView.DataModel(1592046000000L, 1592060400000L, true));

        time_pillar.setDataList(dataList);
    }
}