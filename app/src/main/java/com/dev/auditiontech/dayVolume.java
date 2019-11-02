package com.dev.auditiontech;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class dayVolume  extends AppCompatActivity {
    private LineChart VolumeLineChart;
    DatabaseReference mReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_volumn);

        getSupportActionBar().setTitle("Day Volume");
        Firebase.setAndroidContext(this);
        setupUIViews();
        Calendar calendar1 = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");

        String date = simpleDateFormat.format(calendar1.getTime());
        mReference = FirebaseDatabase.getInstance().getReference(getID()).child(date).child("music_volume");
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

                    Log.w("Firebase",ds.toString());
                    secCount++;
                    int volume = ds.getValue(Integer.class);
                    dataValues1.add(new Entry(secCount, volume));


                }
                final LineDataSet lineDataSet1 = new LineDataSet(dataValues1, "Volume");
                ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                dataSets.add(lineDataSet1);
                LineData data = new LineData(dataSets);
                YAxis yAxis = VolumeLineChart.getAxisLeft();
                yAxis.setAxisMaximum(140f);
                yAxis.setMinWidth(0f);
                Description description = VolumeLineChart.getDescription();
                description.setText("Daily Volume Level");

                VolumeLineChart.setData(data);
                VolumeLineChart.invalidate();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void setupUIViews() {
        VolumeLineChart = findViewById(R.id.VolumeLineChart);
    }

    private String getID() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        return uid;
    }

}
