package com.dev.auditiontech;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.LayoutRes;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public abstract class OnBoardingBaseFragment extends Fragment {
    protected EditText emailEditText;
    protected EditText passwordEditText;
    protected Button submitButton;
    protected FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(getLayout(), container, false);
        emailEditText = view.findViewById(R.id.editTextLogin);
        passwordEditText = view.findViewById(R.id.editTextPassword);
        submitButton = view.findViewById(R.id.submit);
        firebaseAuth = FirebaseAuth.getInstance();
        return view;

    }

    @LayoutRes
    protected abstract int getLayout();
}
