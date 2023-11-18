package com.example.taskmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignupActivity extends AppCompatActivity {

    private EditText etSignupName, etSignupEmail, etSignupPassword;
    private Button btnSignup;
    private TextView tvSignupMsg;

    String[] roles = {"Patient", "Doctor"};
    AutoCompleteTextView tvSignupRole;
    ArrayAdapter<String> roleAdapter;

    // for Firebase Authentication
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    //Firebase connection
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etSignupName = findViewById(R.id.etSignupName);
        etSignupEmail = findViewById(R.id.etSignupEmail);
        etSignupPassword = findViewById(R.id.etSignupPassword);
        btnSignup = findViewById(R.id.btnSignup);
        tvSignupMsg = findViewById(R.id.tvSignupMsg);

        // assign items to Role field
        tvSignupRole = findViewById(R.id.tvSignupRole);
        roleAdapter = new ArrayAdapter<String>(this, R.layout.background_memberlist, roles);
        tvSignupRole.setAdapter(roleAdapter);
        //tvSignupRole.setText("Patient");

        auth = FirebaseAuth.getInstance();


        // sign up function
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = etSignupName.getText().toString().trim();
                String userEmail = etSignupEmail.getText().toString().trim();
                String pass = etSignupPassword.getText().toString().trim();
                String userRole = tvSignupRole.getText().toString();

                //clear previous error msg
                tvSignupMsg.setText("");

                if (userName.isEmpty()) {
                    etSignupName.setError("Role cannot be empty");
                }

                if (userEmail.isEmpty()) {
                    etSignupEmail.setError("Email cannot be empty");
                }

                if (pass.isEmpty()) {
                    etSignupPassword.setError("Password cannot be empty");
                }

                if (userRole.isEmpty()) {
                    tvSignupRole.setError("Role cannot be empty");
                }

                if (!userName.isEmpty() && !userEmail.isEmpty() && !pass.isEmpty() && !userRole.isEmpty()) {
                    // create an user account
                    auth.createUserWithEmailAndPassword(userEmail, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                currentUser = auth.getCurrentUser();
                                assert currentUser != null;
                                final String currentUserId = currentUser.getUid();

                                // create a userMap so that we can create a user in the User Collection in Firebase
                                Map<String, String> userObj = new HashMap<>();
                                userObj.put("userId", currentUserId);
                                userObj.put("userName", userName);
                                userObj.put("userEmail", userEmail);
                                userObj.put("userRole", userRole);

                                // adding users to Firestore
                                collectionReference.add(userObj)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                documentReference.get()
                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                if (Objects.requireNonNull(task.getResult()).exists()) {
                                                                    String name = task.getResult().getString("userName");

                                                                    Toast.makeText(SignupActivity.this, "SignUp Successful", Toast.LENGTH_LONG).show();
                                                                    // if the user is registered successfully, redirect the user to DashBoardActivity
                                                                    Intent i = new Intent(SignupActivity.this, DashBoardActivity.class);
                                                                    i.putExtra("userName", name);
                                                                    i.putExtra("userId", currentUserId);
                                                                    startActivity(i);
                                                                }
                                                            }
                                                        });
                                            }
                                        });


                                // display successful msg and redirect user to login page
                                //Toast.makeText(SignupActivity.this, "SignUp Successful", Toast.LENGTH_LONG).show();
                                //startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                            } else {
                                // otherwise, display error msg
                                //Toast.makeText(SignupActivity.this, "SignUp Failed" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                tvSignupMsg.setText("SignUp Failed: " + task.getException().getMessage());
                            }
                        }
                    });
                }



            }
        });
    }
}