package com.dev.auditiontech;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class questionnaireResult extends AppCompatActivity {
    TextView resultAge;
    TextView resultUse;
    TextView resultLoss;
    TextView resultQuestion0;
    TextView resultQuestion1;
    TextView resultQuestion2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire_result);
        setUpUIViews();

        //Toast.makeText(questionnaireResult.this, "use" + questionnaire.use + "age" + questionnaire.use + "loss" + questionnaire.loss, Toast.LENGTH_SHORT).show();


        if (questionnaire.use == 1)
            resultQuestion0.setText("Recreational");
        else if (questionnaire.use == 2)
            resultQuestion0.setText("Occupational");
        else if (questionnaire.use == 3)
            resultQuestion0.setText("Occupational(Military)");

        if (questionnaire.age == 1)
            resultQuestion1.setText("18-30");
        else if (questionnaire.age == 2)
            resultQuestion1.setText("30-60");
        else if (questionnaire.age == 3)
            resultQuestion1.setText("60+");

        if (questionnaire.loss == 1)
            resultQuestion2.setText("Yes");
        else if (questionnaire.loss == 2)
            resultQuestion2.setText("No");
        else if (questionnaire.loss == 3)
            resultQuestion2.setText("Unknown by user");

    }

    private void setUpUIViews() {
        resultQuestion0 = findViewById(R.id.useResult);
        resultQuestion1 = findViewById(R.id.ageResult);
        resultQuestion2 = findViewById(R.id.lossResult);
    }
}
