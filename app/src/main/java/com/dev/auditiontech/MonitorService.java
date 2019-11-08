package com.dev.auditiontech;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
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
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    public MonitorService() {
    }

    @Override
    public void onCreate() {
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        channelId = getString(R.string.notification_channel_id);
        createNotificationChannel();
        nCB = new NotificationCompat.Builder(this, channelId);
        initNotification();
        initMediaRecorder();

        // TODO: start recording and monitoring
        startMonitoring();

    }

    private void initMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);


        //TODO determine how to save media path.
        String pathSave = "/dev/null";
        mediaRecorder.setOutputFile(pathSave);
    }

    private void startMonitoring() {

        mVolumeChangeObserver = new VolumeChangeObserver(this, new Handler());

        getApplicationContext().getContentResolver()
                .registerContentObserver(android.provider.Settings.System.CONTENT_URI,
                        true, mVolumeChangeObserver);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            Toast.makeText(this, "Measuring...", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //TODO：add volume change update in notification
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

        Intent intent = new Intent(this, meter.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        nCB.setSmallIcon(R.drawable.ic_hearing_24px)
                .setContentTitle(getString(R.string.notification_title_default) + "0 db")
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false);

        nm.notify(notificationID, nCB.build());

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
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
    }

    @Override
    public IBinder onBind(Intent intent) {

        // TODO: figure out how to bind back to meter
        return null;
    }

    private final Handler mHandler = new Handler();
    private Runnable mUpdateMicStatusTimer = new Runnable() {
        public void run() {
            updateMicStatus();
        }
    };

    private double BASE = 0.8;
    private int SPACE = 1000;

    //TODO: the mic status now pass in from MonitorService
    public void updateMicStatus() {
        if (mediaRecorder != null) {

            double amplitude = (double)mediaRecorder.getMaxAmplitude();
            double ratio = amplitude /BASE;

            if (ratio > 1)
            {
                // TODO: is decibel necessary or redundant?
                double decibel = 20 * Math.log10(ratio);
                int db = (int) decibel;

                String date = getDate();
                int sec = getHour()*3600 + getMinute()*60+getSecond();
                String secStr = Integer.toString(sec);
                String name = getID();
                mDatabase.child(name).child(date).child(secStr).setValue(db);
            }
            //Log.d(TAG,"amplitude:"+amplitude);
            mHandler.postDelayed(mUpdateMicStatusTimer, SPACE);
        }
    }


    public String getDate() {
        Calendar calendar1 = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");

        return simpleDateFormat.format(calendar1.getTime());
    }

    public Integer getHour() {
        Calendar calendar1 = Calendar.getInstance();
        int hour = calendar1.get(Calendar.HOUR_OF_DAY);
        return hour;
    }

    public Integer getMinute() {
        Calendar calendar1 = Calendar.getInstance();
        return calendar1.get(Calendar.MINUTE);
    }

    public Integer getSecond() {
        Calendar calendar1 = Calendar.getInstance();
        return calendar1.get(Calendar.SECOND);
    }

    public String getID() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user.getUid();
    }
}
