package com.dev.auditiontech;

import android.content.Context;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Handler;
import android.widget.Toast;

public class VolumeChangeObserver extends ContentObserver {
    private Context context;
    private int threshold = 12;

    public VolumeChangeObserver(Context c, Handler handler) {
        super(handler);
        context = c;

        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

    }

    @Override
    public boolean deliverSelfNotifications() {
        return super.deliverSelfNotifications();
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);

        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);

        if (currentVolume >= threshold) {
            Toast toast = Toast.makeText(context, "Warning! The current volume is " + currentVolume + ". Please be aware of high volume risk.", Toast.LENGTH_SHORT);
            toast.show();
        }

    }

}

