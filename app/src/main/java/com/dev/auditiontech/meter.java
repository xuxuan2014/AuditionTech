package com.dev.auditiontech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

import android.util.Log;

public class meter extends AppCompatActivity {

    int db;
    private Button meterStart;
    private Button meterStop;
    private TextView meterDB;
    private TextView meterReminder;
    boolean mBound;
    MonitorService mService;
    private Intent intent;

    final int REQUEST_PERMISSION_CODE = 1000;
    public static final int INTERVAL = 1000;

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MonitorService.LocalBinder binder = (MonitorService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    private final Handler mHandler = new Handler();
    private Runnable mUpdateMicStatusTimer = new Runnable() {
        public void run() {
            updateUI();
        }
    };

    // TODO: decide what method should be used to pass db value back to meter activity
    // Now I use a public method from service but there's lagging due to asynchronous.
    private void updateUI() {
        db = mService.getDb();
        meterDB.setText(Integer.toString(db));
        //Toast.makeText(this, "Decibel" + db, Toast.LENGTH_SHORT).show();
        if (db < 80) {
            meterReminder.setText("You are in a safe listening environment.");
            meterReminder.setTextColor(Color.BLACK);
        } else if (80 <= db && db < 85) {
            meterReminder.setText("Please leave in 7-8 hours/take hearing protection measures/lower the volume or your hearing could be damaged.");
            meterReminder.setTextColor(Color.rgb(0, 128, 0));
        } else if (db >= 85 && db < 100) {
            meterReminder.setText("Please leave in 15 minutes/take hearing protection measures/lower the volume or your hearing could be damaged");
            meterReminder.setTextColor(Color.rgb(255, 0, 0));
        } else if (db > 100) {
            meterReminder.setText("Please leave in 2 minutes/take hearing protection measures/lower the volume or your hearing could be damaged");
            meterReminder.setTextColor(Color.rgb(139, 0, 0));
        }
        mHandler.postDelayed(mUpdateMicStatusTimer, INTERVAL);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = new Intent(this, MonitorService.class);
        if (!checkPermissionFromDevice())
            requestPermissions();
        setupUIViews();


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (MonitorService.service != null) {
            startMonitorService();
        } else {
            stopMonitorService();
        }
    }

    private void stopMonitorService() {
        mHandler.removeCallbacks(mUpdateMicStatusTimer);
        if (MonitorService.service != null) {
            unbindService(connection);
            stopService(intent);
        }
        mBound = false;
        setupUIViews();
        meterStart.setEnabled(true);
        meterStop.setEnabled(false);
    }

    private void startMonitorService() {

        startService(intent);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
        mHandler.postDelayed(mUpdateMicStatusTimer, INTERVAL);
        meterStart.setEnabled(false);
        meterStop.setEnabled(true);
    }

    private void setupUIViews() {
        setContentView(R.layout.activity_meter);
        getSupportActionBar().setTitle("Decibel Meter");
        meterStart = findViewById(R.id.btnRecord);
        meterStop = findViewById(R.id.btnStopRecord);
        meterDB = findViewById(R.id.meterDB);
        meterReminder = findViewById(R.id.meterREMINDER);

        meterStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (checkPermissionFromDevice()) {

                    startMonitorService();


                } else {
                    requestPermissions();
                }

            }
        });

        meterStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopMonitorService();
                //mediaRecorder.stop();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBound) {
            mHandler.removeCallbacks(mUpdateMicStatusTimer);
            unbindService(connection);
            mBound = false;
        }
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(meter.this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        }, REQUEST_PERMISSION_CODE);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
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

}
