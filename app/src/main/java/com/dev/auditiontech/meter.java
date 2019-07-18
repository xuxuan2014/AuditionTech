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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;
import android.util.Log;

public class meter extends AppCompatActivity {


    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();


    int db;
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

                    //pathSave = Environment.getExternalStorageDirectory().getAbsolutePath() +
                    //        "/" + UUID.randomUUID().toString() + "_audio_record.3gp";

                    pathSave = "/dev/null";


                    setupMediaRecorder();
                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                        Toast.makeText(meter.this, "Measuring...", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    meterStart.setEnabled(false);
                    meterStop.setEnabled(true);

                } else {
                    requestPermissions();
                }

                updateMicStatus();


            }
        });

        meterStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaRecorder != null) {
                    try {
                        mediaRecorder.stop();
                    } catch (IllegalStateException e) {

                        //e.printStackTrace();
                        mediaRecorder = null;
                        mediaRecorder = new MediaRecorder();
                    }
                    mediaRecorder.release();
                    mediaRecorder = null;

                }

                meterStart.setEnabled(true);
                meterStop.setEnabled(false);

                //mediaRecorder.stop();

            }
        });

    }

    private void setupUIViews() {
        meterStart = findViewById(R.id.btnRecord);
        meterStop = findViewById(R.id.btnStopRecord);
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
    private int SPACE = 1000;

    public void updateMicStatus() {
        if (mediaRecorder != null) {

            double amplitude = (double)mediaRecorder.getMaxAmplitude();
            double ratio = amplitude /BASE;

            if (ratio > 1)
            {
                double decibel = 20 * Math.log10(ratio);
                db = (int)decibel;

                String date = getDate();
                int sec = getHour()*3600 + getMinute()*60+getSecond();
                String secStr = Integer.toString(sec);
                mDatabase.child(date).child(secStr).setValue(db);

                meterDB.setText(Integer.toString(db));
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
            //Log.d(TAG,"amplitude:"+amplitude);
            mHandler.postDelayed(mUpdateMicStatusTimer, SPACE);
        }
    }


    public String getDate() {
        Calendar calendar1 = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");

        String date = simpleDateFormat.format(calendar1.getTime());
        return date;
    }

    public Integer getHour() {
        Calendar calendar1 = Calendar.getInstance();
        int hour = calendar1.get(Calendar.HOUR_OF_DAY);
        return hour;
    }

    public Integer getMinute() {
        Calendar calendar1 = Calendar.getInstance();
        int minute = calendar1.get(Calendar.MINUTE);
        return minute;
    }

    public Integer getSecond() {
        Calendar calendar1 = Calendar.getInstance();
        int second = calendar1.get(Calendar.SECOND);
        return second;
    }

}
