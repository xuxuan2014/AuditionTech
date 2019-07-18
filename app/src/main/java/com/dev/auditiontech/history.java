package com.dev.auditiontech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class history extends AppCompatActivity {

    private static final String TAG = "History";
    private LineChart mpLineChart;
    private Button historyDayDiscrete;
    private Button historyDayCumulative;
    private Button historyWeekCumulative;


    DatabaseReference mReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        getSupportActionBar().setTitle("Day Discrete");
        Firebase.setAndroidContext(this);
        setupUIViews();
        mReference = FirebaseDatabase.getInstance().getReference("18-Jul-2019");
        plot();

    }

    private void plot() {



        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Entry> dataValues1 = new ArrayList<Entry>();

                //int i=0;
                Calendar calendar1 = Calendar.getInstance();
                int hour = calendar1.get(Calendar.HOUR_OF_DAY);
                int minute = calendar1.get(Calendar.MINUTE);
                int second = calendar1.get(Calendar.SECOND);
                int secCount = 3600* hour+60*minute+second;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                   secCount++;
                    //i=i+1;
                    int decibel = ds.getValue(Integer.class);
                    dataValues1.add(new Entry(secCount, decibel));



                }
                final LineDataSet lineDataSet1 = new LineDataSet(dataValues1, "Decibel");
                ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                dataSets.add(lineDataSet1);
                LineData data = new LineData(dataSets);
                mpLineChart.setData(data);
                mpLineChart.invalidate();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void setupUIViews() {
        mpLineChart = findViewById(R.id.lineChart);
        historyDayDiscrete = findViewById(R.id.historyDayDiscrete);
        historyDayCumulative = findViewById(R.id.historyDayCumulative);
        historyWeekCumulative = findViewById(R.id.historyWeekCumulative);
    }

}