package com.example.taskmanager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taskmanager.CustomerClass.MsgClass;
import com.example.taskmanager.service.MyForegroundService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingFragment extends Fragment {

    private static final String CHANNEL_ID = "task4175";

    Context thisFragmentContext;
    Button btnSignout;

    //TextView tvMsg;

    // Initialize Firebase Authentication
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String userId, userName, userRole;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        thisFragmentContext = requireContext();
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

//--------------------------------------------------------------------------------------------------------------------
        // Load user name and uer id
        // Since we are using Fragment which does not have its own Intent.
        // We need to access the intent from the host activity that contains the Fragment
        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle != null) {
            userId = bundle.getString("userId");
            userName = bundle.getString("userName");
            //userEmail = bundle.getString("userEmail");
            userRole = bundle.getString("userRole");
            if (userRole.equals("Doctor")) {
                thisFragmentContext.setTheme(R.style.Doctor_Theme);
            }
        }
//--------------------------------------------------------------------------------------------------------------------
        btnSignout = view.findViewById(R.id.btnSignout);



        btnSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Build an AlertDialog to confirm sign out
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Confirm Sign Out");
                builder.setMessage("Are you sure you want to sign out?");

                // Add positive button (Yes)
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Sign out
                        auth.signOut();
                        Toast.makeText(thisFragmentContext, "Signed out", Toast.LENGTH_LONG).show();

                        // Redirect to the login activity
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                    }
                });

                // Add negative button (No)
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked "No," do nothing or dismiss the dialog
                        dialog.dismiss();
                    }
                });

                // Create and show the AlertDialog
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });


        return view;

    }   //end of onCreate()
//--------------------------------------------------------------------------------------------------------------------




}