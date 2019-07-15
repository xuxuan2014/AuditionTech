package com.dev.auditiontech;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private TextView MainActivityLogo;
    private TextView MainActivityTitle;
    private TextView MainActivityWelcome;
    private Button buttonLogin;
    private Button buttonSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Welcome");
        setupUIViews();
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLoginPage();
            }
        });

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAgreement();
            }
        });
    }
    private void setupUIViews() {
        //MainActivityLogo = findViewById(R.id.MainActivityLOGO);
        MainActivityTitle = findViewById(R.id.MainActivityTITLE);
        MainActivityWelcome = findViewById(R.id.MainActivityWELCOME);
        buttonLogin = findViewById(R.id.buttonLOGIN);
        buttonSignUp = findViewById(R.id.buttonSIGNUP);
    }

    public void openLoginPage() {
        Intent intent1 = new Intent(MainActivity.this, signin.class);
        startActivity(intent1);
    }

    public void openAgreement() {
        Intent intent2 = new Intent(MainActivity.this, signup.class);
        startActivity(intent2);

    }


}
