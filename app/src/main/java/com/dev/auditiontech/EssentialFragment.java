package com.dev.auditiontech;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class EssentialFragment extends Fragment {
    private static EssentialFragment instance;


    static EssentialFragment getInstance() {
        if (instance == null) {
            instance = new EssentialFragment();
        }
        return instance;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        return inflater.inflate(R.layout.fragment_essentials, container, false);
    }


}
