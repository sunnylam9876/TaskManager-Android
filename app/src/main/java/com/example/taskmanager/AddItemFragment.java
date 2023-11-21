package com.example.taskmanager;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.taskmanager.CustomerClass.UserClass;
import com.example.taskmanager.TaskList.TaskClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AddItemFragment extends Fragment {
    Context thisFragmentContext, context;
    public String userId, userRole;

    //---------------------------------------------------------------
    // connection to Firebase Realtime database
    FirebaseDatabase realtime_db = FirebaseDatabase.getInstance();

    // get a reference to a specific node in the db
    DatabaseReference myRef = realtime_db.getReference("message");

    // connection to Firebase Firestore database
    private FirebaseFirestore firestore_db = FirebaseFirestore.getInstance();
    private CollectionReference taskCollection = firestore_db.collection("Tasks");  // tasks collection
    private CollectionReference userCollection = firestore_db.collection("Users");  // users collection

    //---------------------------------------------------------------
    // setup Patient drop-down menu

    //String[] members = {"John", "Peter", "Mary", "Jim"};    // dummy data for testing

    ArrayList<UserClass> patientList = new ArrayList<>();  // to store patient list get from Firestore db
    ArrayList<String> patientNameList = new ArrayList<>();  // to store patient name
    AutoCompleteTextView tvSelectPatient;
    ArrayAdapter<String> memberAdapter;
    //---------------------------------------------------------------


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        thisFragmentContext = requireContext();
        View view = inflater.inflate(R.layout.fragment_add_item, container, false);

        //------------------------------------------------------------------
        //Load user name and uer id
         TextView tvHeading = view.findViewById(R.id.tvHeading);

        // Since we are using Fragment which does not have its own Intent.
        // We need to access the intent from the host activity that contains the Fragment
        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle != null) {
            userId = bundle.getString("userId");
            //userName = bundle.getString("userName");
            //userEmail = bundle.getString("userEmail");
            userRole = bundle.getString("userRole");

            //for testing only
            //tvHeading.setText(userName + " (" + userRole +")");
        }

        //------------------------------------------------------------------
        // get Patient list from Firestore
        userCollection
                .whereEqualTo("userRole", "Patient")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                UserClass user = document.toObject(UserClass.class);
                                patientList.add(user);
                                patientNameList.add(user.getUserName());
                            }
                        } else {
                            Toast.makeText(thisFragmentContext, "Error getting user data", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        // Add Patient to the Patient drop-down menu
        tvSelectPatient = view.findViewById(R.id.tvSelectPatient);
        //memberAdapter = new ArrayAdapter<String>(thisFragmentContext, R.layout.fragment_add_item, members);

        // Use the new dropdown_item_layout.xml for the adapter
        memberAdapter = new ArrayAdapter<String>(thisFragmentContext, R.layout.dropdown_item_layout, patientNameList);

        // Specify the layout resource for dropdown items
        memberAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        tvSelectPatient.setAdapter(memberAdapter);

        tvSelectPatient.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //String item = parent.getItemAtPosition(position).toString();
                Toast.makeText(thisFragmentContext, "Position: " + position + "; Name: " + patientList.get(position).getUserName() +
                                                            "; Email: " + patientList.get(position).getUserEmail(), Toast.LENGTH_SHORT).show();

            }
        });
        //------------------------------------------------------------------
        //------------------------------------------------------------------
        // add data to database
        //public TaskClass(String taskTitle, String doctorId, String patientName, String patientEmail,
        //                     String patientId, String year, String month, String day, String hour, String minute,
        //                     String description, String category)


        /*taskCollection.add(task)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getActivity(), "Tasked add", Toast.LENGTH_LONG).show();
                    }
                });*/

        //write to realtime database
        //myRef.setValue(task);

        return view;
    }
}