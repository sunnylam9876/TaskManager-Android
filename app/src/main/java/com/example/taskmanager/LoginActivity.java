package com.example.taskmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private Button btnLogin;

    private EditText etLoginEmail, etLoginPassword;

    private TextView tvSignup, tvLoginMsg;

    // for Firebase Login / Signup authentication
    private FirebaseAuth auth;
    private FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        etLoginEmail = findViewById(R.id.etLoginEmail);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignup = findViewById(R.id.tvSignup);
        tvLoginMsg = findViewById(R.id.tvLoginMsg);

        currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            // user is already logged in
            final String currentUserId = currentUser.getUid();
            startActivity(new Intent(LoginActivity.this, DashBoardActivity.class));
        }

        // set login button onClick listener
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = etLoginEmail.getText().toString();
                String pass = etLoginPassword.getText().toString();

                if (!userEmail.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                    if (!pass.isEmpty()) {
                        auth.signInWithEmailAndPassword(userEmail, pass)
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(LoginActivity.this, DashBoardActivity.class));
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                                        tvLoginMsg.setText("Login Failed. Incorrect password or user does not exist.");
                                    }
                                });
                    } else {
                        etLoginPassword.setError("Password cannot be empty");
                    }
                } else if(userEmail.isEmpty()) {
                    etLoginEmail.setError("Email cannot be empty");
                } else {
                    etLoginEmail.setError("Please enter valid email");
                }
            }
        });

        // set signup textView onClick listener
        tvSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });
    }
}