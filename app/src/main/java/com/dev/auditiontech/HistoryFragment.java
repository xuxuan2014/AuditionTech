package com.dev.auditiontech;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

public class HistoryFragment extends Fragment implements ToolbarCustomizable {
    public static HistoryFragment instance;
    public static Toolbar toolbar;
    private static CharSequence previousToolbarTitle;

    public static HistoryFragment getInstance() {
        if (instance == null) {
            instance = new HistoryFragment();
        }
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (toolbar == null) {
            toolbar = getActivity().findViewById(R.id.main_toolbar);
        }

        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        setToolbar();
    }

    @Override
    public void onStop() {
        resetToolbar();
        super.onStop();

    }

    @Override
    public void setToolbar() {
        previousToolbarTitle = ((AppCompatActivity)getActivity()).getSupportActionBar().getTitle();
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void resetToolbar() {
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(previousToolbarTitle);
        toolbar.setTitle(previousToolbarTitle);
    }


}
