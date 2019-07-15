package com.dev.auditiontech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class signin extends AppCompatActivity {

    private EditText loginEmail;
    private EditText loginPassword;
    private Button loginLogin;
    private Button loginForget;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        getSupportActionBar().setTitle("Log into your account");

        setupUIViews();

//        loginLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                validate(loginEmail.getText().toString(), loginPassword.getText().toString());
//            }
//        });

        loginForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // not sure yet
            }
        });

        loginLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = loginEmail.getText().toString().trim();
                String password = loginPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(signin.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(signin.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                }
                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(signin.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    startActivity(new Intent(getApplicationContext(), homepage.class));
                                    Toast.makeText(signin.this, "Login successful", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(signin.this, "Login failed", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
            }
        });
    }

    private void setupUIViews() {
        loginEmail = (EditText) findViewById(R.id.loginEMAIL);
        loginPassword = (EditText) findViewById(R.id.loginPASSWORD);
        loginLogin = (Button) findViewById(R.id.loginLOGIN);
        loginForget = (Button) findViewById(R.id.loginFORGET);
        firebaseAuth = FirebaseAuth.getInstance();
    }
}
