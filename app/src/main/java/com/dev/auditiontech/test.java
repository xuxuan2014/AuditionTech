package com.dev.auditiontech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
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
        readData();


        LineDataSet lineDataSet1 = new LineDataSet(dataValues1(), "Threshold");
        LineDataSet lineDataSet2 = new LineDataSet(dataValues2(), "cumulative");
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
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

        historyWeekCumulative = findViewById(R.id.historyWeekCumulative);
    }

    private ArrayList<Entry> dataValues1() {
        ArrayList<Entry> dataVals = new ArrayList<Entry>();

        for (int i = 17967; i < 17980; i++) {
            dataVals.add(new Entry(i, 161));
        }

        return dataVals;
    }

    private ArrayList<Entry> dataValues2() {
        ArrayList<Entry> dataVals = new ArrayList<>();

        for (int i = 17967; i < 17980; i++) {


            //Log.d(TAG, Integer.toString(map.get(secCount)));
            //String yVal = map.get(i).toString();
            //Toast.makeText(this, yVal, Toast.LENGTH_LONG).show();
            dataVals.add(new Entry(i, 100));
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


        mReference = new Firebase("https://auditiontechapp-b09c3.firebaseio.com/17-Jul-2019/");
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.firebase.client.DataSnapshot dataSnapshot) {

                //yData = new ArrayList<>();
                float i = 0;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    i = i + 1;
                    String SV = ds.child("P1").getValue().toString();
                    Float SensorValue = Float.parseFloat(SV);
                    //yData.add(new Entry(i, SensorValue));
                }
                //final LineDataSet lineDataSet = new LineDataSet(yData, "Temp");
                //LineData data = new LineData(lineDataSet);
                //mpLineChart.setData(data);
                mpLineChart.notifyDataSetChanged();
                mpLineChart.invalidate();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


//            mReference = new Firebase("https://auditiontechapp-b09c3.firebaseio.com/17-Jul-2019/");
//            mReference.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
//
//                    map = new HashMap<Integer,Integer>();
//
//                    int decibel =dataSnapshot.getValue(Integer.class);
//                    map.put(secCount,decibel);
//                }
//
//                @Override
//                public void onCancelled(FirebaseError firebaseError) {
//                }
//            });
//        Log.d(TAG, "decibel" + map.get(secCount));
//
//    }


    }
}
