package com.dev.auditiontech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class test extends AppCompatActivity {

    private static final String TAG = "History";
    private LineChart mpLineChart;
    private Button historyDayDiscrete;
    private Button historyDayCumulative;
    private Button historyWeekCumulative;
    Firebase mReference;

    int sum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        getSupportActionBar().setTitle("Day Cumulative");
        Firebase.setAndroidContext(this);
        setupUIViews();

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
        mpLineChart = findViewById(R.id.LineChart);
        historyDayDiscrete = findViewById(R.id.testDayDiscrete);
        historyDayCumulative = findViewById(R.id.testDayCumulative);
        historyWeekCumulative = findViewById(R.id.testWeekCumulative);
    }

    private ArrayList<Entry> dataValues1() {
        ArrayList<Entry> dataVals = new ArrayList<Entry>();

        for (int i =0; i<24; i++) {
            dataVals.add(new Entry(i,161));
        }

        return dataVals;
    }

    private ArrayList<Entry> dataValues2() {
        ArrayList<Entry> dataVals = new ArrayList<>();

        for (int i = 0; i<24; i++) {
            dataVals.add(new Entry(i,sum));
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


        mReference = new Firebase("https://auditiontechapp-b09c3.firebaseio.com/15-Jul-2019/" );
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                String decibel = dataSnapshot.getValue(Double.class).toString();
                Toast.makeText(test.this, decibel, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });
    }




}
