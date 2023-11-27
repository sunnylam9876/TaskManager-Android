package com.example.taskmanager.service;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
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
import com.example.taskmanager.R;
import com.example.taskmanager.TaskList.TaskClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MyForegroundService extends Service {
    private static final String CHANNEL_ID = "task4175";
    private static final int NOTIFICATION_ID = 4175; // Unique ID for the notification

    private static final String SERVICE_CHANNEL_ID = "SERVICE_TASK4175";
    private static final int SERVICE_NOTIFICATION_ID = 750; // Unique ID for the notification

    private boolean isServiceStarted = false;
    private boolean isFirstLoad = true;

    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private String userId, userRole;
    FirebaseFirestore firestore_db = FirebaseFirestore.getInstance();
    CollectionReference taskCollection = firestore_db.collection("Tasks");  // tasks collection

    private CollectionReference userCollection = firestore_db.collection("Users");


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

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();


        if (intent != null && intent.getExtras() != null) {
            Bundle receivedBundle = intent.getExtras();
            userId = receivedBundle.getString("userId");
            userRole = receivedBundle.getString("userRole");
        } else {
            if (currentUser != null) {
                // if user is already logged in, get the user name and user id
                //get the user Id
                final String currentUserId = currentUser.getUid();
                Task<QuerySnapshot> querySnapshotTask = userCollection.whereEqualTo("userId", currentUserId)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    // Get the first document (there should be only one with the provided UID)
                                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);

                                    // Access user information fields from the document
                                    userId = documentSnapshot.getString("userId");
                                    userRole = documentSnapshot.getString("userRole");
                                } else {
                                    // No user document found for the given UID
                                    // Handle the case where the user data is missing
                                    Toast.makeText(getApplicationContext(), "Error getting user information", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        }


        if (!isServiceStarted) {
            // Create a notification for the foreground service
            Notification notification = buildNotification(this, "Task Management App is running", "");

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


                        // send a broadcast msg, the HomeFragment will update the calendar
                        // once it receive the intent
                        Intent intent = new Intent("LOAD_DATA_FROM_DB");
                        sendBroadcast(intent);
                        //LoadDataInBackground();
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Foreground Service";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel notificationChannel = new NotificationChannel(SERVICE_CHANNEL_ID, channelName, importance);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            // Check if the channel already exists. If no, create a new channel
            if (notificationManager.getNotificationChannel(SERVICE_CHANNEL_ID) == null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
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


    // show floating notification whenever there is update from realtime database
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
            // Check if the channel already exists. If no, create the channel
            if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
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

    private void LoadDataInBackground() {

            ArrayList<TaskClass> taskList = new ArrayList<>();
            Query query = taskCollection
                    .whereEqualTo("patientId", userId)
                    .whereEqualTo("setAlarm", false);

            // Execute the query to get the matching documents
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            TaskClass eachTask = document.toObject(TaskClass.class);
                            //eachTask.setId(document.getId());       // To get document id for further update or delete
                            //originalTaskList.add(eachTask);
                            //taskList.add(eachTask);
                            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                            Intent intent = new Intent(MyForegroundService.this, MyNotificationReceiver.class); // Replace with your BroadcastReceiver class
                            long notificationId = System.currentTimeMillis(); // Use a timestamp as a unique ID
                            intent.putExtra("msg", eachTask.getTaskTitle());
                            intent.putExtra("notification_id", (int) notificationId); // Use a unique ID for each notification

                            PendingIntent pendingIntent = PendingIntent.getBroadcast(MyForegroundService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(System.currentTimeMillis());

                            // Set the desired time for the notification (replace with your desired time logic)
                            calendar.set(Calendar.YEAR, eachTask.getYear());
                            calendar.set(Calendar.MONTH, eachTask.getMonth() - 1);  //Note: Months are zero-based (0 for January, 1 for February, etc.)
                            calendar.set(Calendar.DAY_OF_MONTH, eachTask.getDay());
                            calendar.set(Calendar.HOUR_OF_DAY, eachTask.getHour());
                            calendar.set(Calendar.MINUTE, eachTask.getMinute());
                            calendar.set(Calendar.SECOND, 0);
                            calendar.set(Calendar.MILLISECOND, 0);

                            // Create an AlarmClockInfo object
                            AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), pendingIntent);

                            // Set the alarm using setAlarmClock()
                            alarmManager.setAlarmClock(alarmClockInfo, pendingIntent);


                            //update the field in database
                            eachTask.setSetAlarm(true);     // mark the field to indicate alarm was set for this task
                            Map<String, Object> updatedData = new HashMap<>();
                            updatedData.put("setAlarm", true);
                            taskCollection.document(eachTask.getId())
                                    .update(updatedData)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(getApplicationContext(), "Alarm set in foreground", Toast.LENGTH_LONG).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(), "Error on updating task:" + e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }


                    } else {
                        // Display the error
                        Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_LONG).show();
                        Log.d("Firestore error", task.getException().toString());
                    }
                }
            });
    }

}