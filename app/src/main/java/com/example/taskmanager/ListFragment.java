package com.example.taskmanager;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taskmanager.TaskList.MyTaskListAdapter;
import com.example.taskmanager.TaskList.TaskClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class ListFragment extends Fragment {

    Context thisFragmentContext, context;

    // Set user variables
    String userId, userName, userEmail, userRole;

    //set Task List
    private RecyclerView rvList;
    private RecyclerView.LayoutManager taskListLayoutManager;
    private RecyclerView.Adapter taskListAdapter;
    private ArrayList<TaskClass> taskList;

//---------------------------------------------------------------
    // connection to Firebase Realtime database
    FirebaseDatabase realtime_db = FirebaseDatabase.getInstance();

    // connection to Firebase Firestore database
    private FirebaseFirestore firestore_db = FirebaseFirestore.getInstance();
    private CollectionReference taskCollection = firestore_db.collection("Tasks");  // tasks collection
    private CollectionReference userCollection = firestore_db.collection("Users");  // users collection
//------------------------------------------------------------------

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        thisFragmentContext = requireContext();
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        rvList = view.findViewById(R.id.rvList);

        taskList = new ArrayList<>();

        //------------------------------------------------------------------
        // Load user name and uer id
        // Since we are using Fragment which does not have its own Intent.
        // We need to access the intent from the host activity that contains the Fragment
        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle != null) {
            userId = bundle.getString("userId");
            //userName = bundle.getString("userName");
            //userEmail = bundle.getString("userEmail");
            userRole = bundle.getString("userRole");

        }
        LoadDataFromDB();


        return view;
    }

    private void LoadDataFromDB() {
        // Load all data in the selected month for the logged in user

        //int year = selectedDate.getYear();
        //int month = selectedDate.getMonthValue();
        //int day = selectedDate.getDayOfMonth();
        String patientId = "UTQYAjSOYmbFWBXryzHuRtvOcAF2";
        //String status = "Pending";


        // Clear the lists first
        taskList.clear();

        Query query = taskCollection
                //.whereEqualTo("year", year)
                //.whereEqualTo("month", month)
                //.whereEqualTo("day", day)
                .whereEqualTo("patientId", patientId)
                //.whereEqualTo("status", status)
                .orderBy("year", Query.Direction.ASCENDING)
                .orderBy("month", Query.Direction.ASCENDING)
                .orderBy("day", Query.Direction.ASCENDING)      // need to create index in Firestore first, or click the link in the error msg
                .orderBy("hour", Query.Direction.ASCENDING)
                .orderBy("minute", Query.Direction.ASCENDING);
        //.orderBy("day");
        //.whereEqualTo("category", category);

        // Execute the query to get the matching documents
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        TaskClass eachTask = document.toObject(TaskClass.class);
                        eachTask.setId(document.getId());       // To get document id for further update or delete
                        taskList.add(eachTask);
                    }


                } else {
                    // Display the error
                    Toast.makeText(thisFragmentContext, task.getException().toString(), Toast.LENGTH_LONG).show();
                    Log.d("Firestore error", task.getException().toString());
                }
            }
        });

        // Feed the task list to RecyclerView
        setTaskList();

    }

//------------------------------------------------------------------
    //set the content of Task List
    private void setTaskList() {
        // sources is 'taskList'

        //taskListAdapter = new MyTaskListAdapter(taskList, thisFragmentContext);   // for displaying all data in the selected month, testing only

        taskListAdapter = new MyTaskListAdapter(taskList, thisFragmentContext);
        taskListLayoutManager = new LinearLayoutManager(thisFragmentContext);
        rvList.setLayoutManager(taskListLayoutManager);
        rvList.setAdapter(taskListAdapter);
        taskListAdapter.notifyDataSetChanged();
    }

}