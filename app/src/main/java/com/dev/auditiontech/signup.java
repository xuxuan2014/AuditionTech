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

public class signup extends AppCompatActivity {

    EditText signupEmail;
    EditText signupPassword;
    EditText signupPassword2;
    Button signupConfirm;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().setTitle("Sign up your account");

        setupUIViews();

        signupConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = signupEmail.getText().toString().trim();
                String password = signupPassword.getText().toString().trim();
                String confirmPassword = signupPassword2.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(signup.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(signup.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                }
                if (TextUtils.isEmpty(confirmPassword)) {
                    Toast.makeText(signup.this, "Please Re-enter Password", Toast.LENGTH_SHORT).show();
                }

                if (password.length() < 6) {
                    Toast.makeText(signup.this, "Your password is too short", Toast.LENGTH_SHORT).show();
                }


                if (password.equals(confirmPassword)) {
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(signup.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        startActivity(new Intent(getApplicationContext(), signin.class));
                                        Toast.makeText(signup.this, "Registration successful", Toast.LENGTH_SHORT).show();

                                    } else {
                                        Toast.makeText(signup.this, "Password is wrong", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    public void setupUIViews() {
        signupEmail = (EditText) findViewById(R.id.signupEMAIL);
        signupPassword = (EditText) findViewById(R.id.signupPASSWORD);
        signupPassword2 = (EditText) findViewById(R.id.signupPASSWORD2);
        signupConfirm = (Button) findViewById(R.id.signupCONFIRM);
        firebaseAuth = FirebaseAuth.getInstance();
    }


}
