package com.dev.auditiontech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.UUID;

public class meter extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private Button meterStart;
    private Button meterStop;
    private TextView meterDB;
    private TextView meterReminder;
    private String pathSave = "";
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;
    private final String TAG = "MediaRecord";
    public static final int MAX_LENGTH = 1000 * 60 * 10;

    final int REQUEST_PERMISSION_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meter);
        getSupportActionBar().setTitle("Decibel Meter");

        setupUIViews();
        if (!checkPermissionFromDevice())
            requestPermissions();
        meterStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (checkPermissionFromDevice()) {

                    pathSave = Environment.getExternalStorageDirectory().getAbsolutePath() +
                            "/" + UUID.randomUUID().toString() + "_audio_record.3gp";
                    setupMediaRecorder();
                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    meterStart.setEnabled(false);
                    meterStop.setEnabled(true);
                    Toast.makeText(meter.this, "Measuring...", Toast.LENGTH_SHORT).show();
                } else {
                    requestPermissions();
                }

                updateMicStatus();


            }
        });

        meterStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                micStop();


            }
        });

    }

    private void setupUIViews() {
        meterStart = findViewById(R.id.meterSTART);
        meterStop = findViewById(R.id.meterSTOP);
        meterDB = findViewById(R.id.meterDB);
        meterReminder = findViewById(R.id.meterREMINDER);
    }

    private void setupMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(pathSave);
    }


    private void requestPermissions() {
        ActivityCompat.requestPermissions(meter.this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        }, REQUEST_PERMISSION_CODE );
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE: {
                if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(meter.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(meter.this, "Permission Denied", Toast.LENGTH_SHORT).show();

            }
            break;
        }
    }

    private boolean checkPermissionFromDevice() {
        int write_external_storage_result = ContextCompat.checkSelfPermission(meter.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(meter.this, Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED &&
                record_audio_result == PackageManager.PERMISSION_GRANTED;
    }


    private final Handler mHandler = new Handler();
    private Runnable mUpdateMicStatusTimer = new Runnable() {
        public void run() {
            updateMicStatus();
        }
    };

    private int BASE = 1;
    private int SPACE = 100;

    public void updateMicStatus() {
        if (mediaRecorder != null) {
            double ratio = (double)mediaRecorder.getMaxAmplitude() /BASE;
            double db = 0;
            if (ratio > 1)
            {
                db = 20 * Math.log10(ratio);
                meterDB.setText(Double.toString(db));
                //Toast.makeText(this, "Decibel" + db, Toast.LENGTH_SHORT).show();
                if (db < 80) {
                    meterReminder.setText("You are in a safe listening environment.");
                    meterReminder.setTextColor(Color.BLACK);
                }
                else if (80<=db && db<85) {
                    meterReminder.setText("Please leave in 7-8 hours/take hearing protection measures/lower the volume or your hearing could be damaged.");
                    meterReminder.setTextColor(Color.rgb(0,128,0));
                }
                else if (db>=85 && db<100) {
                    meterReminder.setText("Please leave in 15 minutes/take hearing protection measures/lower the volume or your hearing could be damaged");
                    meterReminder.setTextColor(Color.rgb(255,0,0));
                }
                else if (db > 100) {
                    meterReminder.setText("Please leave in 2 minutes/take hearing protection measures/lower the volume or your hearing could be damaged");
                    meterReminder.setTextColor(Color.rgb(139,0,0));
                }
            }
            //Log.d(TAG,"Decibel:"+db);
            mHandler.postDelayed(mUpdateMicStatusTimer, SPACE);
        }
    }

    private void micStop() {
        meterStart.setEnabled(true);
        meterStop.setEnabled(false);
        mediaRecorder.stop();
    }

}
