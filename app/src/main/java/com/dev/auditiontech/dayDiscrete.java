package com.dev.auditiontech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

public class dayDiscrete extends AppCompatActivity {

    private LineChart DDLineChart;
    private Button DDDayCumulative;
    private Button DDWeekCumulative;

    DatabaseReference mReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_discrete);

        getSupportActionBar().setTitle("Day Discrete");
        Firebase.setAndroidContext(this);
        setupUIViews();
        Calendar calendar1 = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");

        String date = simpleDateFormat.format(calendar1.getTime());
        mReference = FirebaseDatabase.getInstance().getReference(getID()).child(date);
        plot();

        DDDayCumulative.setOnClickListener(new View.OnClickListener() {
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

                //int i=0;

                Calendar calendar1 = Calendar.getInstance();
                int hour = calendar1.get(Calendar.HOUR_OF_DAY);
                int minute = calendar1.get(Calendar.MINUTE);
                int second = calendar1.get(Calendar.SECOND);
                int secCount = 3600* hour+60*minute+second;

                for (DataSnapshot ds : dataSnapshot.getChildren()) {


                    secCount++;
                    int decibel = ds.getValue(Integer.class);
                    dataValues1.add(new Entry(secCount, decibel));


                }
                final LineDataSet lineDataSet1 = new LineDataSet(dataValues1, "Decibel");
                ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                dataSets.add(lineDataSet1);
                LineData data = new LineData(dataSets);
                YAxis yAxis = DDLineChart.getAxisLeft();
                yAxis.setAxisMaximum(140f);
                yAxis.setMinWidth(0f);
                Description description = DDLineChart.getDescription();
                description.setText("Daily Noise Level");

                DDLineChart.setData(data);
                DDLineChart.invalidate();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void setupUIViews() {
        DDLineChart = findViewById(R.id.DDLineChart);
        DDDayCumulative = findViewById(R.id.DDDayCumulative);
        DDWeekCumulative = findViewById(R.id.DDWeekCumulative);
    }

    private String getID() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        return uid;
    }

    private void openHistory() {
        Intent intent = new Intent(dayDiscrete.this, history.class);
        startActivity(intent);
    }

}
