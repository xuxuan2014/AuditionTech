package com.dev.auditiontech;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class questionnaire extends AppCompatActivity {

    private TextView profileQuestion;
    private CheckBox profileOption1;
    private CheckBox profileOption2;
    private CheckBox profileOption3;
    private Button profileBack;
    private Button profileNext;
    int questionNum = 0;

    static int use = 0;
    static int age = 0;
    static int loss = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);
        getSupportActionBar().setTitle("Let's create your hearing profile now!");

        setupUIViews();

        profileNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (profileOption1.isChecked() || profileOption2.isChecked() || profileOption3.isChecked()) {
                    if (questionNum == 0) {
                        if (profileOption1.isChecked()) {
                            use = 1;
                        }
                        else if (profileOption2.isChecked()) {
                            use = 2;
                        }
                        else if (profileOption3.isChecked()) {
                            use = 3;
                        }
                        //Toast.makeText(questionnaire.this, Integer.toString(use), Toast.LENGTH_SHORT).show();
                        questionNum++;
                        updateQuestion();
                        profileOption1.setChecked(false);
                        profileOption2.setChecked(false);
                        profileOption3.setChecked(false);

                    }
                    else if (questionNum ==1) {
                        if (profileOption1.isChecked()) {
                            age = 1;
                        }
                        else if (profileOption2.isChecked()) {
                            age = 2;
                        }
                        else if (profileOption3.isChecked()) {
                            age = 3;
                        }
                        //Toast.makeText(questionnaire.this, Integer.toString(age), Toast.LENGTH_SHORT).show();
                        questionNum++;
                        updateQuestion();
                        profileOption1.setChecked(false);
                        profileOption2.setChecked(false);
                        profileOption3.setChecked(false);

                    }
                    else if (questionNum ==2) {
                        if (profileOption1.isChecked()) {
                            loss = 1;
                        }
                        else if (profileOption2.isChecked()) {
                            loss = 2;
                        }
                        else if (profileOption3.isChecked()) {
                            loss = 3;
                        }
                        //Toast.makeText(questionnaire.this, Integer.toString(loss), Toast.LENGTH_SHORT).show();
                        openResult();
                    }

                }
            }
        });

        profileBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


    }

    private void setupUIViews() {
        profileQuestion = findViewById(R.id.profileQuestion);
        profileOption1 = findViewById(R.id.profileOption1);
        profileOption2 = findViewById(R.id.profileOption2);
        profileOption3 = findViewById(R.id.profileOption3);
        profileBack = findViewById(R.id.profileBack);
        profileNext = findViewById(R.id.profileNext);

    }

    private void updateQuestion() {
        if (questionNum == 1) {
            profileQuestion.setText("What's your age range?");
            profileOption1.setText("18-30");
            profileOption2.setText("30-60");
            profileOption3.setText("Over 60");
        }
        else if (questionNum ==2) {
            profileQuestion.setText("Do you have any perceived hearing loss?");
            profileOption1.setText("Yes");
            profileOption2.setText("No");
            profileOption3.setText("I don't know");
        }

    }

    private void openResult() {
        Intent intent = new Intent(questionnaire.this, questionnaireResult.class);
        startActivity(intent);
    }
}
