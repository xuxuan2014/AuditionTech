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
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

public class MonitorService extends Service {

    private NotificationManager nm;
    private NotificationCompat.Builder nCB;
    private String channelId;
    private int notificationID = 114514;
    private MediaRecorder mediaRecorder;

    //TODO: implement notification
    //private int NOTIFICATION = R.string.local_service_started;
    public MonitorService() {
    }

    @Override
    public void onCreate() {
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        channelId = getString(R.string.notification_channel_id);
        createNotificationChannel();
        nCB = new NotificationCompat.Builder(this, channelId);
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);

        //TODO determine how to save media path.
        String pathSave = "/dev/null";
        mediaRecorder.setOutputFile(pathSave);

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

    private void showNotification() {

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
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
