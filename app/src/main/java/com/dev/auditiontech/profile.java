package com.dev.auditiontech;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class profile extends AppCompatActivity {

    Button profileFirst;
    Button profileReturn;
    Button profileHome;
    Button profileSignout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getSupportActionBar().setTitle("Listening Profile");

        setupUIViews();

        profileFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openQuestionnaire();
            }
        });

        profileReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openResult();
            }
        });

        profileHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHomepage();
            }
        });

        profileSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMain();
            }
        });

    }

    private void setupUIViews() {
        profileFirst = (Button) findViewById(R.id.profileFIRST);
        profileReturn = (Button) findViewById(R.id.profileRETURN);
        profileHome = findViewById(R.id.profileHOME);
        profileSignout = findViewById(R.id.profileSIGNOUT);
    }

    public void openQuestionnaire() {
        Intent intent1 = new Intent(profile.this, questionnaire.class);
        startActivity(intent1);
    }

    public void openHomepage() {
        Intent intent2 = new Intent(profile.this, homepage.class);
        startActivity(intent2);
    }

    public void openMain() {
        Intent intent3 = new Intent(profile.this, OnBoardingActivity.class);
        startActivity(intent3);
    }

    public void openResult() {
        Intent intent4 = new Intent(profile.this, questionnaireResult.class);
        startActivity(intent4);
    }


}
