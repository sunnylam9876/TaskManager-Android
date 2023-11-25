package com.example.taskmanager.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class MyBgServices extends Service {
    String userName, userId, userEmail, userRole;

    public MyBgServices() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // get data from Bundle

        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            userId = bundle.getString("userId");
            userName = bundle.getString("userName");
            userEmail = bundle.getString("userEmail");
            userRole = bundle.getString("userRole");
        }

        FirebaseFirestore firestore_db = FirebaseFirestore.getInstance();
        CollectionReference taskCollection = firestore_db.collection("Tasks");  // tasks collection



        return START_STICKY;
    }
}