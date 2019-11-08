package com.dev.auditiontech;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

public class MonitorService extends Service {

    private NotificationManager nm;
    private NotificationCompat.Builder nCB;
    private String channelId;

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

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
