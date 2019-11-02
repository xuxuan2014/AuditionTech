package com.dev.auditiontech;

import android.content.Context;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Handler;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class VolumeChangeObserver extends ContentObserver {
    Context context;
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();


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
        Toast toast = Toast.makeText(context, "The current volume is "+currentVolume, Toast.LENGTH_SHORT);
        toast.show();

        String date = getDate();
        int sec = getHour()*3600 + getMinute()*60+getSecond();
        String secStr = Integer.toString(sec);
        String name = getID();
        mDatabase.child(name).child("music_volume").child(date).child(secStr).setValue(currentVolume);

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

    public String getID() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        return uid;
    }
}

