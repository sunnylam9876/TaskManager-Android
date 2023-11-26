package com.example.taskmanager.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.taskmanager.CustomerClass.MsgClass;
import com.example.taskmanager.LoginActivity;
import com.example.taskmanager.MainActivity;
import com.example.taskmanager.NotificationDetails;
import com.example.taskmanager.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyForegroundService extends Service {
    private static final String CHANNEL_ID = "task4175";
    private static final int NOTIFICATION_ID = 4175; // Unique ID for the notification

    private static final String SERVICE_CHANNEL_ID = "SERVICE_TASK4175";
    private static final int SERVICE_NOTIFICATION_ID = 750; // Unique ID for the notification

    private boolean isServiceStarted = false;
    private boolean isFirstLoad = true;

    private String userId, userRole;

        public MyForegroundService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Log.d("MyService", "onStartCommand called, Intent: " + intent + ", Flags: " + flags + ", StartId: " + startId);
        // get the user information from MainActivity
        if (intent != null && intent.getExtras() != null) {
            Bundle receivedBundle = intent.getExtras();
            userId = receivedBundle.getString("userId");
            userRole = receivedBundle.getString("userRole");
        }


        if (!isServiceStarted) {
            // Create a notification for the foreground service
            Notification notification = buildNotification(this, "App is running", "");

            // Start the service in the foreground with the notification
            startForeground(SERVICE_NOTIFICATION_ID, notification);

            // Set the flag indicating the service has been started
            isServiceStarted = true;

            // connection to Firebase Realtime database
            FirebaseDatabase realtime_db = FirebaseDatabase.getInstance();
            DatabaseReference myRef = realtime_db.getReference(userId);

            // set realtime database event listener
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (isFirstLoad) {
                        isFirstLoad = false; // Set the flag to false after the first load
                    } else {
                        MsgClass newValue = snapshot.getValue(MsgClass.class);
                        //tvMsg.setText(newValue.getTitle() + "; " + newValue.getMsg() + "; Id: " + newValue.getDocumentId());
                        //showNotification(newValue.getTitle(), newValue.getMsg());
                        showFloatingNotification(newValue.getTitle(), newValue.getMsg());
                        Intent intent = new Intent("LOAD_DATA_FROM_DB");
                        sendBroadcast(intent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        return START_STICKY; // Service will be restarted if it's killed by the system
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    // according to Android's policy, a notification must be shown for starting foreground service
    @SuppressLint("MissingPermission")
    private Notification buildNotification(Context context, String title, String msg) {
        // Create an explicit intent for an Activity in your app.
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Create a notification channel (for Android 8.0 and higher)
        //String channelId = "my_notification_channel";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Foreground Service";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel notificationChannel = new NotificationChannel(SERVICE_CHANNEL_ID, channelName, importance);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, SERVICE_CHANNEL_ID)
                        .setSmallIcon(R.drawable.baseline_access_alarm_24)
                        .setContentTitle(title)
                        .setContentText(msg)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        // Set the intent that fires when the user taps the notification.
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define.
        // Generate a unique notification ID (e.g., using a timestamp)
        long notificationId = System.currentTimeMillis(); // Use a timestamp as a unique ID
        notificationManager.notify((int) notificationId, builder.build());

        //Toast.makeText(context, "Notification sent from buildNotification!", Toast.LENGTH_LONG).show();

        return builder.build();
    }

    // this notification is to alert user if there is any update on database
    @SuppressLint("MissingPermission")
    private void showNotification(String title, String msg) {
        // Create an intent to open your app when the notification is tapped
        Intent intent = new Intent(this, LoginActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Create a notification channel (for Android 8.0 and higher)
        //String channelId = "my_notification_channel";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "My Notification Channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, channelName, importance);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_access_alarm_24)
                .setContentTitle(title)
                .setContentText(msg)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // Generate a unique notification ID (e.g., using a timestamp)
        long notificationId = System.currentTimeMillis(); // Use a timestamp as a unique ID
        notificationManager.notify((int) notificationId, builder.build());
    }

    // show floating notification
    @SuppressLint("MissingPermission")
    private void showFloatingNotification(String title, String msg) {
        // Create an intent to open your app when the notification is tapped
        Intent intent = new Intent(this, LoginActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Create a notification channel (for Android 8.0 and higher)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Task Update Channel";
            int importance = NotificationManager.IMPORTANCE_HIGH; // Use HIGH importance for floating notifications
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, channelName, importance);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        // Build the notification with additional properties for heads-up notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_access_alarm_24)
                .setContentTitle(title)
                .setContentText(msg)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH) // Set HIGH priority for heads-up notification
                .setDefaults(Notification.DEFAULT_ALL); // Add default notification sound, vibration, etc.

        // Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // Generate a unique notification ID (e.g., using a timestamp)
        long notificationId = System.currentTimeMillis(); // Use a timestamp as a unique ID
        notificationManager.notify((int) notificationId, builder.build());
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        // TODO: Remove the ValueEventListener and clean up resources
    }

}