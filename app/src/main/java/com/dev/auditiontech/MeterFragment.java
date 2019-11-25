package com.dev.auditiontech;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.button.MaterialButton;

public class MeterFragment extends Fragment {
    private static MeterFragment instance;
    private static boolean isMonitoring;
    private MonitorService mService;
    private int db;
    public static final int INTERVAL = 1000;
    final int REQUEST_PERMISSION_CODE = 1000;

    private TextView meterDB;
    private TextView meterReminder;
    private MaterialButton meterButton;
    private boolean mBound;
    private Intent intent;

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
        public void onBindingDied(ComponentName name) {
            stopMonitorService();
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

    static MeterFragment getInstance() {
        if (instance == null) {
            instance = new MeterFragment();
        }
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        isMonitoring = false;
        return inflater.inflate(R.layout.fragment_meter, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        meterDB = getView().findViewById(R.id.meterDB);
        meterReminder = getView().findViewById(R.id.meterREMINDER);
        intent = new Intent(getContext(), MonitorService.class);
        meterButton = getView().findViewById(R.id.btnRecord);
        meterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startMonitorService();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isMonitoring && MonitorService.service == null) {
            resetUI();
        } else if (!isMonitoring && MonitorService.service != null) {
            startMonitorService();
        }
    }

    private void stopMonitorService() {

        isMonitoring = false;
        mHandler.removeCallbacks(mUpdateMicStatusTimer);
        if (MonitorService.service != null) {
            getActivity().unbindService(connection);
            getActivity().stopService(intent);
        }
        mBound = false;
        mService = null;
        resetUI();
    }

    private void startMonitorService() {

        isMonitoring = true;

        getActivity().startService(intent);

        // TODO:Test which one does not allow auto create.
        getActivity().bindService(intent, connection, 0);
        mHandler.postDelayed(mUpdateMicStatusTimer, INTERVAL);

        meterButton.setIconResource(R.drawable.ic_stop_recording);
        meterButton.setText("STOP");
        meterButton.setBackgroundColor(getResources().getColor(R.color.primaryLightColor));
        meterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopMonitorService();
            }
        });
    }

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
        } else {
            meterReminder.setText("Please leave in 2 minutes/take hearing protection measures/lower the volume or your hearing could be damaged");
            meterReminder.setTextColor(Color.rgb(139, 0, 0));
        }
        mHandler.postDelayed(mUpdateMicStatusTimer, INTERVAL);
    }

    /**
     * This method reset UI back to original state, i.e. not monitoring state.
     */
    private void resetUI() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (Build.VERSION.SDK_INT >= 26) {
            ft.setReorderingAllowed(false);
        }
        ft.detach(this).attach(this).commitAllowingStateLoss();
    }
}
