package com.example.taskmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taskmanager.CustomerClass.UserClass;
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
    String userId, userName, userRole;

    boolean selectable = false;      // indicate whether the Patient drop-down menu is selectable or not

    //set Task List

    EditText etSearch;
    AutoCompleteTextView tvPatientFilter, tvCategoryFilter;
    private RecyclerView rvList;
    private RecyclerView.LayoutManager taskListLayoutManager;
    private RecyclerView.Adapter taskListAdapter;
    private ArrayList<TaskClass> taskList;
    private ArrayList<TaskClass> filteredTaskList;

//--------------------------------------------------------------------------------------------------------------------
    // connection to Firebase Realtime database
    FirebaseDatabase realtime_db = FirebaseDatabase.getInstance();

    // connection to Firebase Firestore database
    private FirebaseFirestore firestore_db = FirebaseFirestore.getInstance();
    private CollectionReference taskCollection = firestore_db.collection("Tasks");  // tasks collection
    private CollectionReference userCollection = firestore_db.collection("Users");  // users collection

//--------------------------------------------------------------------------------------------------------------------
// Setup Patient drop-down menu
    ArrayList<UserClass> patientList = new ArrayList<>();  // to store patient list get from Firestore db
    ArrayList<String> patientNameList = new ArrayList<>();  // to store patient name
    ArrayAdapter<String> patientAdapter;
    String selectedPatient = "All";
    int selectedPatientIndex;
//--------------------------------------------------------------------------------------------------------------------
    // Setup Category drop-down menu
    String[] categories = {"All", "Appointment", "Medicine", "Workout", "Others"};
    AutoCompleteTextView tvCategory;
    ArrayAdapter<String> categoryAdapter;
    String selectedCategory = "All";
//--------------------------------------------------------------------------------------------------------------------
    // this part is to receive message from the foreground service
    // once receive foreground service call, LoadDataFromDB() will be triggered to update data
    private BroadcastReceiver dataUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals("LOAD_DATA_FROM_DB")) {
                // Call your function when the broadcast is received
                LoadDataFromDB();
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter("LOAD_DATA_FROM_DB");
        requireContext().registerReceiver(dataUpdateReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        requireContext().unregisterReceiver(dataUpdateReceiver);
    }

//--------------------------------------------------------------------------------------------------------------------
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        thisFragmentContext = requireContext();
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        etSearch = view.findViewById(R.id.etSearch);
        rvList = view.findViewById(R.id.rvList);
        tvPatientFilter = view.findViewById(R.id.tvHomePatientFilter);
        tvCategoryFilter = view.findViewById(R.id.tvCategoryFilter);

        taskList = new ArrayList<>();
        filteredTaskList = new ArrayList<>();

//--------------------------------------------------------------------------------------------------------------------
        // Load user name and uer id
        // Since we are using Fragment which does not have its own Intent.
        // We need to access the intent from the host activity that contains the Fragment
        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle != null) {
            userId = bundle.getString("userId");
            userName = bundle.getString("userName");
            //userEmail = bundle.getString("userEmail");
            userRole = bundle.getString("userRole");
            if (userRole.equals("Doctor"))
                selectable = true;
        }
//--------------------------------------------------------------------------------------------------------------------
        // get Patient list from Firestore
        // and set the Patient drop-down menu
        if (!selectable) {
            // if the user is a patient, show the user's name disable the drop-down menu
            tvPatientFilter.setText(userName);
            tvPatientFilter.setFocusable(false);
            tvPatientFilter.setFocusableInTouchMode(false);
            tvPatientFilter.setInputType(InputType.TYPE_NULL);
            tvPatientFilter.setOnClickListener(null);
            tvPatientFilter.setOnTouchListener(null);
        }
        else {
            // if the user is a doctor, enable the patient drop-down menu
            patientList.clear();
            patientNameList.clear();
            userCollection
                    .whereEqualTo("userRole", "Patient")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                //patientNameList.add("All");
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    UserClass user = document.toObject(UserClass.class);
                                    patientList.add(user);
                                    patientNameList.add(user.getUserName());
                                    // Add Patient to the Patient drop-down menu
                                    // Use the new dropdown_item_layout.xml for the adapter
                                    patientAdapter = new ArrayAdapter<String>(thisFragmentContext, R.layout.dropdown_item_layout, patientNameList);

                                    // Specify the layout resource for dropdown items
                                    patientAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                                    tvPatientFilter.setAdapter(patientAdapter);
                                    //tvPatientFilter.setAdapter(patientAdapter);
                                    tvPatientFilter.setText(patientAdapter.getItem(0), false);


                                    tvPatientFilter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            selectedPatient = parent.getItemAtPosition(position).toString();
                                            Toast.makeText(thisFragmentContext, selectedPatient + " : " + selectedCategory, Toast.LENGTH_LONG).show();
                                            filterTask(selectedPatient, selectedCategory);
                                            setTaskList(filteredTaskList);  // Load the filtered data to RecyclerView
                                        }
                                    });
                                }
                            } else {
                                Toast.makeText(thisFragmentContext, "Error getting user data", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
//--------------------------------------------------------------------------------------------------------------------
        // Add categories to the Category drop-down menu
        // Use the new dropdown_item_layout.xml for the adapter
        categoryAdapter = new ArrayAdapter<String>(thisFragmentContext, R.layout.dropdown_item_layout, categories);

        // Specify the layout resource for dropdown items
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        tvCategoryFilter.setAdapter(categoryAdapter);
        //tvCategoryFilter.setText(categoryAdapter.getItem(0), false);
        tvCategoryFilter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory =  parent.getItemAtPosition(position).toString();
                //Toast.makeText(thisFragmentContext, selectedCategory, Toast.LENGTH_SHORT).show();
                filterTask(selectedPatient, selectedCategory);
                setTaskList(filteredTaskList);  // Load the filtered data to RecyclerView
            }
        });

//--------------------------------------------------------------------------------------------------------------------
        LoadDataFromDB();

        // Handle search bar function
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String userInput = etSearch.getText().toString();
                    searchTask(userInput);
                    setTaskList(filteredTaskList);  // Load the filtered data to RecyclerView
                    //Toast.makeText(thisFragmentContext, userInput, Toast.LENGTH_LONG).show();
                    return true;
                }
                return false;
            }
        });

        return view;
    }

    private void LoadDataFromDB() {
        // Load all data in the selected month for the logged-in user

        //int year = selectedDate.getYear();
        //int month = selectedDate.getMonthValue();
        //int day = selectedDate.getDayOfMonth();
        //String patientId = "UTQYAjSOYmbFWBXryzHuRtvOcAF2";
        String patientId = userId;
        //String status = "Pending";


        // Clear the lists first
        taskList.clear();
        Query query;
        if (userRole.equals("Doctor")) {    //doctor can see all the data
            // need to create index in Firestore first, click the link in the error msg
            query = taskCollection
                    //.whereEqualTo("year", year)
                    //.whereEqualTo("month", month)
                    //.whereEqualTo("day", day)
                    //.whereEqualTo("patientId", patientId)
                    .whereEqualTo("status", "Pending")
                    .orderBy("year", Query.Direction.ASCENDING)
                    .orderBy("month", Query.Direction.ASCENDING)
                    .orderBy("day", Query.Direction.ASCENDING)
                    .orderBy("hour", Query.Direction.ASCENDING)
                    .orderBy("minute", Query.Direction.ASCENDING);
            //.orderBy("day");
            //.whereEqualTo("category", category);
        } else {    // patient only view their own data
            // need to create index in Firestore first, click the link in the error msg
            query = taskCollection
                    //.whereEqualTo("year", year)
                    //.whereEqualTo("month", month)
                    //.whereEqualTo("day", day)
                    .whereEqualTo("patientId", patientId)
                    .whereEqualTo("status", "Pending")
                    .orderBy("year", Query.Direction.ASCENDING)
                    .orderBy("month", Query.Direction.ASCENDING)
                    .orderBy("day", Query.Direction.ASCENDING)      // need to create index in Firestore first, or click the link in the error msg
                    .orderBy("hour", Query.Direction.ASCENDING)
                    .orderBy("minute", Query.Direction.ASCENDING);
            //.orderBy("day");
            //.whereEqualTo("category", category);
        }



        // Execute the query to get the matching documents
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        TaskClass eachTask = document.toObject(TaskClass.class);
                        eachTask.setId(document.getId());       // To get document id for further update or delete
                        taskList.add(eachTask);     // Load the data to taskList
                        setTaskList(taskList);  // Load the filtered data to RecyclerView
                    }


                } else {
                    // Display the error
                    Toast.makeText(thisFragmentContext, task.getException().toString(), Toast.LENGTH_LONG).show();
                    Log.d("Firestore error", task.getException().toString());
                }
            }
        });

        // Feed the task list to RecyclerView


    }

//--------------------------------------------------------------------------------------------------------------------
    //set the content of Task List
    private void setTaskList(ArrayList<TaskClass> taskList) {

        taskListAdapter = new MyTaskListAdapter(taskList, userRole, thisFragmentContext);
        taskListLayoutManager = new LinearLayoutManager(thisFragmentContext);
        rvList.setLayoutManager(taskListLayoutManager);
        rvList.setAdapter(taskListAdapter);
        taskListAdapter.notifyDataSetChanged();
    }

    private void filterTask(String patientName, String category) {
        //ArrayList<TaskClass> filteredTaskList = new ArrayList<>();

        if (filteredTaskList != null)
            filteredTaskList.clear();

        if (selectedPatient.equals("All")) {
            if (selectedCategory.equals("All")) {  // Patient = All and Category = All
                for (TaskClass eachTask : taskList) {
                    filteredTaskList.add(eachTask);
                }
            } else {    // Patient = All and Category = not All
                for (TaskClass eachTask : taskList) {
                    if (eachTask.getCategory().equals(selectedCategory)) {
                        filteredTaskList.add(eachTask);
                    }
                }
            }
        }
        else {
            if (selectedCategory.equals("All")) {   // Patient = not All and Category = All
                for (TaskClass eachTask : taskList) {
                    if (eachTask.getPatientName().equals(selectedPatient)) {
                        filteredTaskList.add(eachTask);
                    }
                }
            } else {    // Patient = not All and Category = not All
                for (TaskClass eachTask : taskList) {
                    if (eachTask.getPatientName().equals(selectedPatient) && eachTask.getCategory().equals(selectedCategory)) {
                        filteredTaskList.add(eachTask);
                    }
                }
            }
        }
    }

    private void searchTask(String searchString) {
        if (filteredTaskList != null)
            filteredTaskList.clear();

        for (TaskClass eachTask : taskList) {
            if (eachTask.getTaskTitle().contains(searchString)) {
                filteredTaskList.add(eachTask);
            }
        }
    }

}