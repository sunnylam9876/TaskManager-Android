package com.example.taskmanager;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
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

    TextView tvMsg;

    // Initialize Firebase Authentication
    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        thisFragmentContext = requireContext();
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        btnSignout = view.findViewById(R.id.btnSignout);

        tvMsg = view.findViewById(R.id.tvMsg);

        btnSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // sign out
                auth.signOut();
                Toast.makeText(thisFragmentContext, "Signed out", Toast.LENGTH_SHORT).show();

                //redirect to login activity
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });

        //createNotificationChannel();
        //sendNotification(requireContext());

        //readRealTimeDb();

        //Intent serviceIntent = new Intent(requireContext(), MyForegroundService.class);
        //thisFragmentContext.startService(serviceIntent);


        return view;

    }   //end of onCreate()
//--------------------------------------------------------------------------------------------------------------------
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Task Management System";
            String description = "test_channel_for_csis_4175";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this.
            NotificationManager notificationManager = requireActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @SuppressLint("MissingPermission")
    public void sendNotification(Context context, String title, String msg) {
        // Create an explicit intent for an Activity in your app.
        Intent intent = new Intent(context, NotificationDetails.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle(title)
                        .setContentText(msg)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        // Set the intent that fires when the user taps the notification.
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define.
        int notificationId = 1;
        notificationManager.notify(notificationId, builder.build());

        Toast.makeText(context, "Notification sent from SettingFragment!", Toast.LENGTH_LONG).show();
    }

    public void readRealTimeDb() {
        // connection to Firebase Realtime database
        FirebaseDatabase realtime_db = FirebaseDatabase.getInstance();
        DatabaseReference myRef = realtime_db.getReference("wmDAr2fnh0RVouy0hwlAtCFoaCs2");


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                MsgClass newValue = snapshot.getValue(MsgClass.class);
                tvMsg.setText(newValue.getTitle() + "; " + newValue.getMsg() + "; Id: " + newValue.getDocumentId());
                //sendNotification(requireContext(), newValue.getTitle(), newValue.getMsg());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //myRef.setValue("Test msg");
    }



}