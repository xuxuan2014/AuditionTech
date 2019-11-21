package com.dev.auditiontech;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class homepage extends AppCompatActivity {

    Button homeProfile;
    Button homeSignout;
    Button homeMeter;
    Button homeHistory;
    Button homeEssentials;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        getSupportActionBar().setTitle("Homepage");

        setupUIViews();

        homeSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                Intent intent = new Intent(homepage.this, MainActivity.class);
                startActivity(intent);
            }
        });

        homeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfile();
            }
        });

        homeMeter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMeter();
            }
        });

        homeHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHistory();
            }
        });

        homeEssentials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEssentials();
            }
        });

    }

    private void setupUIViews() {

        homeProfile = findViewById(R.id.homePROFILE);
        homeSignout = findViewById(R.id.homepageSIGNOUT);
        homeHistory = findViewById(R.id.homepageHISTORY);
        homeMeter = findViewById(R.id.homepageMeter);
        homeEssentials = findViewById(R.id.homeESSENTIALS);
        homeHistory = findViewById(R.id.homepageHISTORY);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void openProfile() {
        Intent intent = new Intent(homepage.this, profile.class);
        startActivity(intent);
    }

    public void openMeter() {
        Intent intent = new Intent(homepage.this, meter.class);
        startActivity(intent);
    }

    public void openHistory() {
        Intent intent = new Intent(homepage.this, HearingHistory.class);
        startActivity(intent);
    }

    public void openEssentials() {
        Intent intent = new Intent(homepage.this, essentials.class);
        startActivity(intent);
    }
}
