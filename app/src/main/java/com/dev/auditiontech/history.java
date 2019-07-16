package com.dev.auditiontech;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.HashMap;

public class history extends AppCompatActivity {

    private static final String TAG = "History";
    private LineChart mpLineChart;
    private Button historyDayDiscrete;
    private Button historyDayCumulative;
    private Button historyWeekCumulative;
    Firebase mReference;
    HashMap<Integer,Integer> map;
    int i = 17967;

    int sum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        getSupportActionBar().setTitle("Day Cumulative");
        Firebase.setAndroidContext(this);
        setupUIViews();

        map = new HashMap<Integer,Integer>();

        LineDataSet lineDataSet1 = new LineDataSet(dataValues1(), "Threshold");
        LineDataSet lineDataSet2 = new LineDataSet(dataValues2(), "cumulative");
        ArrayList<ILineDataSet> dataSets =  new ArrayList<>();
        dataSets.add(lineDataSet1);
        dataSets.add(lineDataSet2);

        LineData data = new LineData(dataSets);
        mpLineChart.setData(data);
        mpLineChart.invalidate();

        historyDayDiscrete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //openDayDiscrete();
            }
        });

        historyDayCumulative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //openDayCumulative();
            }
        });

        historyWeekCumulative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //openWeekCumulative();
            }
        });

    }

    private void setupUIViews() {
        mpLineChart = findViewById(R.id.lineChart);
        historyDayDiscrete = findViewById(R.id.historyDayDiscrete);
        historyDayCumulative = findViewById(R.id.historyDayCumulative);
        historyWeekCumulative = findViewById(R.id.historyWeekCumulative);
    }

    private ArrayList<Entry> dataValues1() {
        ArrayList<Entry> dataVals = new ArrayList<Entry>();

        for (i=17967; i<17980; i++) {
            dataVals.add(new Entry(i,161));
        }

        return dataVals;
    }

    private ArrayList<Entry> dataValues2() {
        ArrayList<Entry> dataVals = new ArrayList<>();

        for (i=17967; i<17980; i++) {
            readData();
            //String yVal = map.get(i).toString();
            //Toast.makeText(this, yVal, Toast.LENGTH_LONG).show();
            dataVals.add(new Entry(i,map.get(i)));
        }

        return dataVals;
    }

//    private void openDayDiscrete() {
//        Intent intent = new Intent(history.this, dayDiscrete.class);
//        startActivity(intent);
//    }
//
//    private void openDayCumulative() {
//        Intent intent = new Intent(history.this, dayCumulative.class);
//        startActivity(intent);
//    }
//
//    private void openWeekCumulative() {
//        Intent intent = new Intent(history.this, weekCumulative.class);
//        startActivity(intent);
//    }

    public void readData() {

            mReference = new Firebase("https://auditiontechapp-b09c3.firebaseio.com/16-Jul-2019/" +i);
            mReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                    int decibel =dataSnapshot.getValue(Double.class).intValue();
                    Toast.makeText(history.this, Double.toString(decibel), Toast.LENGTH_LONG).show();
                    //map.put(i,decibel);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
            //i++;

    }




}
