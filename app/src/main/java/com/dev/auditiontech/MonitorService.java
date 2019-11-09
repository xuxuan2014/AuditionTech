package com.dev.auditiontech;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MonitorService extends Service {

    private NotificationManager nm;
    private NotificationCompat.Builder nCB;
    private String channelId;
    private String uid;
    private int notificationID = 114514;
    private MediaRecorder mediaRecorder;
    private VolumeChangeObserver mVolumeChangeObserver;
    private Calendar calendar;
    public static final double BASE = 0.8;
    public static final int INTERVAL = 1000;
    private int db = 30;
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private final IBinder binder = new LocalBinder();
    private FirebaseUser user;
    private Handler mHandler;
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            updateMicStatus();
        }
    };

    public class LocalBinder extends Binder {
        MonitorService getService() {
            // Return this instance of LocalService so clients can call public methods
            return MonitorService.this;
        }
    }

    @Override
    public void onCreate() {
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        channelId = getString(R.string.notification_channel_id);
        createNotificationChannel();
        nCB = new NotificationCompat.Builder(this, channelId);
        calendar = Calendar.getInstance();
        initNotification();
        initMediaRecorder();

        startMonitoring();
        mHandler = new Handler(Looper.getMainLooper());
        mHandler.postDelayed(mRunnable, INTERVAL);

    }

    private void initMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);


        //TODO determine how to save media path.
        String pathSave = "/dev/null";
        mediaRecorder.setOutputFile(pathSave);
        Log.e("mediaRecorder", "mediaRecorder initialized.");
    }

    private void startMonitoring() {

        mVolumeChangeObserver = new VolumeChangeObserver(this, new Handler());

        getApplicationContext().getContentResolver()
                .registerContentObserver(android.provider.Settings.System.CONTENT_URI,
                        true, mVolumeChangeObserver);

        try {
            Log.e("monitoring", "get into try loop");
            mediaRecorder.prepare();
            mediaRecorder.start();
            Toast.makeText(this, "Measuring...", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "VolumeMonitor";
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);

            //Configure Notification Channel
            notificationChannel.setDescription("Notify if volume monitoring is running in foreground");
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);

            nm.createNotificationChannel(notificationChannel);
        }

    }

    private void initNotification() {

        Intent meterIntent = new Intent(this, meter.class);
        meterIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingStartMeterIntent = PendingIntent.getActivity(this, 0, meterIntent, 0);

        Intent monitorIntent = new Intent(this, MonitorService.class).setAction(getString(R.string.stop_monitor_action_command));
        PendingIntent pendingStopMonitorIntent = PendingIntent.getService(this, 0, monitorIntent, 0);
        nCB.setSmallIcon(R.drawable.ic_hearing_24px)
                .setContentTitle(getString(R.string.notification_title_default) + "0 db")
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_MAX)
                .setContentIntent(pendingStartMeterIntent)
                .setAutoCancel(false)
                .addAction(R.drawable.ic_mic_off_24px, "Stop monitoring", pendingStopMonitorIntent);
        startForeground(notificationID, nCB.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction() != null && intent.getAction().equals(getString(R.string.stop_monitor_action_command))) {
            stopForeground(true);
            stopSelf();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        // TODO: check if it truly unregister
        getApplicationContext().getContentResolver().unregisterContentObserver(mVolumeChangeObserver);

        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();
            } catch (IllegalStateException e) {

                mediaRecorder = null;
                mediaRecorder = new MediaRecorder();
            }
            mediaRecorder.release();
            mediaRecorder = null;

        }

        stopForeground(true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public int getDb() {
        return db;
    }


    // TODO: check if the logic works
    public void updateMicStatus() {
        if (mediaRecorder != null) {

            double amplitude = (double) mediaRecorder.getMaxAmplitude();
            double ratio = amplitude / BASE;

            if (ratio > 1) {
                double decibel = 20 * Math.log10(ratio);
                db = (int) decibel;

                String date = getDate();
                int sec = getHour() * 3600 + getMinute() * 60 + getSecond();
                String secStr = Integer.toString(sec);
                String name = getID();
                mDatabase.child(name).child(date).child(secStr).setValue(db);
                nCB.setContentTitle(getString(R.string.notification_title_default) + db + " db");
                nm.notify(notificationID, nCB.build());
            }
            Log.d("mic", "amplitude:" + amplitude);
        }
        mHandler.postDelayed(mRunnable, INTERVAL);
    }


    public String getDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        return simpleDateFormat.format(calendar.getTime());
    }

    public Integer getHour() {
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public Integer getMinute() {
        return calendar.get(Calendar.MINUTE);
    }

    public Integer getSecond() {
        return calendar.get(Calendar.SECOND);
    }

    // TODO: change ID into whatever passed in
    public String getID() {
        if (user == null) {
            user = FirebaseAuth.getInstance().getCurrentUser();
        }
        return user.getUid();
    }
}
