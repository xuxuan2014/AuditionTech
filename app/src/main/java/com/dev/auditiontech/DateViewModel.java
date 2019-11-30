package com.dev.auditiontech;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Date;

public class DateViewModel extends ViewModel {
    private MutableLiveData<Date> date;

    public MutableLiveData<Date> getDate() {
        if (date == null) {
            date = new MutableLiveData<>();
            date.setValue(new Date());
        }

        return date;
    }
}
