package com.dev.auditiontech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.dev.auditiontech.utils.TimeUtil;
import com.firebase.client.Firebase;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
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

import java.util.ArrayList;

public class DayDiscrete extends AppCompatActivity {

    private LineChart lineChart;
    private Button dayCumulativeButton;
    private Button DDWeekCumulative;
    DatabaseReference mReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_discrete);

        getSupportActionBar().setTitle("Day Discrete");
        Firebase.setAndroidContext(this);
        setupUIViews();
        setupPlotStyles();

        mReference = FirebaseDatabase.getInstance().getReference(getID())
                .child(getString(R.string.firebase_keyword_ambient_volume)).child(TimeUtil.getDate());

        plot();

        dayCumulativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openHistory();
            }
        });
    }

    private void plot() {

        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Entry> dataValues1 = new ArrayList<Entry>();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    int decibel = ds.getValue(Integer.class);

                    dataValues1.add(new Entry(Long.parseLong(ds.getKey()), decibel));
                }
                final LineDataSet lineDataSet1 = new LineDataSet(dataValues1, "Decibel");
                ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                dataSets.add(lineDataSet1);
                LineData data = new LineData(dataSets);
                YAxis yAxis = lineChart.getAxisLeft();
                yAxis.setAxisMaximum(140f);
                yAxis.setMinWidth(0f);
                Description description = lineChart.getDescription();
                description.setText("Daily Noise Level");

                lineChart.setData(data);
                lineChart.invalidate();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void setupPlotStyles() {

        lineChart.setScaleEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setDragDecelerationFrictionCoef(0.9f);
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setGranularity(1000f);
    }


    private void setupUIViews() {
        lineChart = findViewById(R.id.DDLineChart);
        dayCumulativeButton = findViewById(R.id.DDDayCumulative);
        DDWeekCumulative = findViewById(R.id.DDWeekCumulative);
    }

    private String getID() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        return uid;
    }

    private void openHistory() {
        Intent intent = new Intent(DayDiscrete.this, HearingHistory.class);
        startActivity(intent);
    }

}
