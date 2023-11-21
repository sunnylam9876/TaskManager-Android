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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.taskmanager.CustomerClass.UserClass;
import com.example.taskmanager.TaskList.TaskClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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
    // Setup Patient drop-down menu

    ArrayList<UserClass> patientList = new ArrayList<>();  // to store patient list get from Firestore db
    ArrayList<String> patientNameList = new ArrayList<>();  // to store patient name
    AutoCompleteTextView tvSelectPatient;
    ArrayAdapter<String> memberAdapter;

    int selectedPatientIndex;   // to store the index of drop-down menu

//---------------------------------------------------------------
    // Setup Category drop-down menu
    String[] categories = {"Appointment", "Medicine", "Workout", "Others"};
    AutoCompleteTextView tvCategory;
    ArrayAdapter<String> categoryAdapter;

    String selectedCategory;

//---------------------------------------------------------------
    EditText etInputTaskTitle, etInputDate, etInputTime, etInputDescription;
    Button btnSelectDate, btnSelectTime, btnSubmit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        thisFragmentContext = requireContext();
        View view = inflater.inflate(R.layout.fragment_add_item, container, false);

        etInputTaskTitle = view.findViewById(R.id.etInputTaskTitle);
        etInputDate = view.findViewById(R.id.etInputDate);
        etInputTime = view.findViewById(R.id.etInputTime);
        etInputDescription = view.findViewById(R.id.etInputDescription);
        btnSelectDate = view.findViewById(R.id.btnSelectDate);
        btnSelectTime = view.findViewById(R.id.btnSelectTime);
        btnSubmit =view.findViewById(R.id.btnSubmit);

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
        tvSelectPatient = view.findViewById(R.id.tvInputPatient);

        // Use the new dropdown_item_layout.xml for the adapter
        memberAdapter = new ArrayAdapter<String>(thisFragmentContext, R.layout.dropdown_item_layout, patientNameList);

        // Specify the layout resource for dropdown items
        memberAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        tvSelectPatient.setAdapter(memberAdapter);

        tvSelectPatient.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //String item = parent.getItemAtPosition(position).toString();
                selectedPatientIndex = position;
                Toast.makeText(thisFragmentContext, "Position: " + selectedPatientIndex + "; Name: " + patientList.get(selectedPatientIndex).getUserName() +
                                                            "; Email: " + patientList.get(selectedPatientIndex).getUserEmail(), Toast.LENGTH_SHORT).show();

            }
        });

        //------------------------------------------------------------------
        // Add categories to the Category drop-down menu
        tvCategory = view.findViewById(R.id.tvInputCategory);

        // Use the new dropdown_item_layout.xml for the adapter
        categoryAdapter = new ArrayAdapter<String>(thisFragmentContext, R.layout.dropdown_item_layout, categories);

        // Specify the layout resource for dropdown items
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        tvCategory.setAdapter(categoryAdapter);

        tvCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory =  parent.getItemAtPosition(position).toString();
                Toast.makeText(thisFragmentContext, selectedCategory, Toast.LENGTH_SHORT).show();
            }
        });

        // Set Date picker onClick listener
        btnSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Select Date")
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build();
                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long selection) {
                        String date = new SimpleDateFormat("MM-dd-yyy", Locale.getDefault()).format(new Date(selection));
                        etInputDate.setText(date);
                    }
                });
                materialDatePicker.show(getActivity().getSupportFragmentManager(), "tag");
            }
        });

        // Set submit button onClick listener
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddData();
            }

        });
        return view;
    }

    private void AddData() {
        // Add data to database
        //public TaskClass(String taskTitle, String doctorId, String patientName, String patientEmail,
        //        String patientId, String description, String category, String status,
        //int year, int month, int day, int hour, int minute) {
        //String userName = etSignupName.getText().toString().trim();

        String taskTitle = etInputTaskTitle.getText().toString().trim();
        String doctorId = userId;
        String patientName = patientList.get(selectedPatientIndex).getUserName();
        String patientEmail = patientList.get(selectedPatientIndex).getUserEmail();
        String patientId = patientList.get(selectedPatientIndex).getUserId();
        String description = etInputDescription.getText().toString().trim();
        String category = selectedCategory;
        String status = "Pending";
        int year = 2023;
        int month = 11;
        int day = 20;
        int hour = 8;
        int minute = 20;

        TaskClass newTask = new TaskClass(taskTitle, doctorId, patientName, patientEmail, patientId,
                description, category, status, year, month, day, hour, minute);

        taskCollection.add(newTask)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getActivity(), "Tasked add", Toast.LENGTH_LONG).show();
                    }
                });

        //write to realtime database
        //myRef.setValue(task);
    }
}