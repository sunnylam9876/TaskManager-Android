package com.example.taskmanager.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.taskmanager.MainActivity;
import com.example.taskmanager.NotificationDetails;
import com.example.taskmanager.R;

public class MyNotificationReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "task4175";

    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, MainActivity.class);      //NotificationDetails.class
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_IMMUTABLE);

        int notificationId = intent.getIntExtra("notification_id", 4000);
        String msg = intent.getStringExtra("msg");

        // Create and show the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_access_alarm_24)
                .setContentTitle("Reminder: You have an activity now")
                .setContentText(msg)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_ALL) // Add default notification sound, vibration, etc.
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationId, builder.build());
        //Toast.makeText(context, "notification sent", Toast.LENGTH_SHORT).show();
        //sendNotification(context);
    }

    @SuppressLint("MissingPermission")
    public void sendNotification(Context context) {
        // Create an explicit intent for an Activity in your app.
        Intent intent = new Intent(context, NotificationDetails.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("Scheduled message")
                        .setContentText("Scheduled")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        // Set the intent that fires when the user taps the notification.
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define.
        int notificationId = 1;
        notificationManager.notify(notificationId, builder.build());

        Toast.makeText(context, "Notification sent!", Toast.LENGTH_LONG).show();
    }
}
