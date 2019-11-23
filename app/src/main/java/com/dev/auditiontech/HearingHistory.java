package com.dev.auditiontech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.firebase.client.Firebase;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class HearingHistory extends AppCompatActivity {

    private static final String TAG = "History";
    private LineChart mpLineChart;
    private Button historyDayDiscrete;
    private Button historyDayCumulative;
    private Button historyWeekCumulative;
    private Button volumeDay;
    int exposure;

    DatabaseReference mReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        getSupportActionBar().setTitle("Day Cumulative");
        Firebase.setAndroidContext(this);
        setupUIViews();
        Calendar calendar1 = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");

        String date = simpleDateFormat.format(calendar1.getTime());
        mReference = FirebaseDatabase.getInstance().getReference(getID())
                .child(getString(R.string.firebase_keyword_ambient_volume)).child(date);
        plot();

        historyDayDiscrete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDD();
            }
        });

//        volumeDay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(HearingHistory.this, MediaVolume.class);
//                startActivity(intent);
//            }
//        });

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
                    int decibel = ds.getValue(Integer.class);

                    double reference =(8/(Math.pow(2, (decibel-90)/5)));
                    double ratio = 1/(3600*reference) * Math.pow(10,7);
                    Log.d(TAG, Double.toString(ratio));

                    //Toast.makeText(HearingHistory.this,Double.toString(secExposure), Toast.LENGTH_SHORT).show();

                    int intSecExposure = (int)Math.round(ratio);
                    exposure = exposure + intSecExposure;

                    dataValues1.add(new Entry(secCount, exposure));


                }
                final LineDataSet lineDataSet1 = new LineDataSet(dataValues1, "Decibel");
                ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                dataSets.add(lineDataSet1);
                LineData data = new LineData(dataSets);
                YAxis yAxis = mpLineChart.getAxisLeft();
                yAxis.setAxisMaximum(10000000f);
                yAxis.setMinWidth(0f);
                Description description = mpLineChart.getDescription();
                description.setText("Daily Cumulative Exposure");
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
        volumeDay = findViewById(R.id.volumeDay);
    }

    private String getID() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        return uid;
    }


    public void openDD() {
        Intent intent = new Intent(HearingHistory.this, DayDiscrete.class);
        startActivity(intent);
    }
}