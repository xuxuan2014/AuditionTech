package com.dev.auditiontech;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class test extends AppCompatActivity {

    private TextView calendar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        setupUIViews();

        Calendar calendar1 = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        String dateTime = simpleDateFormat.format(calendar1.getTime());
        calendar.setText(dateTime);
    }

    public void setupUIViews() {
        calendar = findViewById(R.id.calender);
    }
}
