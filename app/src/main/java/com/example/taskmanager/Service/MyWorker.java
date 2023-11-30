package com.example.taskmanager.Service;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class MyWorker extends Worker {
    private static final String TAG = "MyWorker";

    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        //fetchDataFromDb();
        Log.v(TAG, "Performing long runnign task....");
        return Result.success();
    }

    private void fetchDataFromDb() {
        FirebaseFirestore firestore_db = FirebaseFirestore.getInstance();
        CollectionReference taskCollection = firestore_db.collection("Tasks");  // tasks collection
        CollectionReference userCollection = firestore_db.collection("Users");  // users collection
        FirebaseAuth auth  = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        final String currentUserId = currentUser.getUid();
    }
}
