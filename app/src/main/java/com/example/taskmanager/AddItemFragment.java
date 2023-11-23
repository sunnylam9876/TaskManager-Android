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
import com.example.taskmanager.Utility.CalculateDate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
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
    EditText etInputTaskTitle, etInputDescription;
    Button  btnSubmit;
    TextView tvInputTime, tvInputDate;
    int inputYear, inputMonth, inputDay, inputHour, inputMinute;

    Boolean update;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        thisFragmentContext = requireContext();
        View view = inflater.inflate(R.layout.fragment_add_item, container, false);

        etInputTaskTitle = view.findViewById(R.id.etInputTaskTitle);
        etInputDescription = view.findViewById(R.id.etInputDescription);
        btnSubmit =view.findViewById(R.id.btnSubmit);
        tvInputDate = view.findViewById(R.id.tvInputDate);
        tvInputTime = view.findViewById(R.id.tvInputTime);

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
        }

        Bundle bundle_fragment = getArguments();
        if (bundle_fragment != null) {
            update = bundle_fragment.getBoolean("update");
            TaskClass taskDetail = bundle_fragment.getParcelable("taskDetails");
            //for testing only
            //tvHeading.setText("update: " + update + "; title: " + taskDetail.getTaskTitle());
            etInputTaskTitle.setText(taskDetail.getTaskTitle());
            etInputDescription.setText(taskDetail.getDescription());
            //tvSelectPatient.setText(taskDetail.getPatientName());
            //tvCategory.setText(taskDetail.getCategory());

            //tvInputDate = view.findViewById(R.id.tvInputDate);
            //tvInputTime = view.findViewById(R.id.tvInputTime);
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
        // Set Date picker onClick listener
        tvInputDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Select Date")
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build();
                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long selection) {
                        String date = new SimpleDateFormat("MMM-dd-yyyy", Locale.getDefault()).format(new Date(selection));
                        tvInputDate.setText(date);

                        // Convert the selected timestamp to Calendar
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(selection);

                        // Extract month, day, and year values
                        inputYear = calendar.get(Calendar.YEAR);
                        inputMonth = calendar.get(Calendar.MONTH) + 1; // Calendar months are 0-based
                        inputDay = calendar.get(Calendar.DAY_OF_MONTH);

                        //Toast.makeText(thisFragmentContext, inputYear + "-" + inputMonth + "-" + inputDay, Toast.LENGTH_LONG).show();
                    }
                });
                materialDatePicker.show(getActivity().getSupportFragmentManager(), "tag");
            }
        });

//------------------------------------------------------------------
        // Set Time picker onClick listener
        tvInputTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_24H)
                        .setHour(12)
                        .setMinute(0)
                        .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
                        .setTitleText("Pick Time (24 hrs format)")
                        .build();
                timePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String hourString = String.format(Locale.getDefault(), "%02d", timePicker.getHour());
                        String minuteString = String.format(Locale.getDefault(), "%02d", timePicker.getMinute());
                        tvInputTime.setText(hourString + ":" + minuteString);
                        //int hour = Integer.parseInt(tvInputTime.getText().toString().substring(0,2));
                        //int minute = Integer.parseInt(tvInputTime.getText().toString().substring(3));
                        inputHour = Integer.parseInt(hourString);
                        inputMinute = Integer.parseInt(minuteString);
                        //Toast.makeText(thisFragmentContext, inputHour + " : " + inputMinute, Toast.LENGTH_LONG).show();
                    }
                });
                timePicker.show(getActivity().getSupportFragmentManager(), "tag");

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

//------------------------------------------------------------------
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

        TaskClass newTask = new TaskClass(taskTitle, doctorId, patientName, patientEmail, patientId,
                description, category, status, inputYear, inputMonth, inputDay, inputHour, inputMinute);

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