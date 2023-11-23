package com.example.taskmanager;

import static com.example.taskmanager.Utility.CalculateDate.monthFromDate;
import static com.example.taskmanager.Utility.CalculateDate.monthYearFromDate;
import static com.example.taskmanager.Utility.CalculateDate.yearFromDate;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taskmanager.Calendar.DateClass;
import com.example.taskmanager.Calendar.MyCalendarAdapter;
import com.example.taskmanager.TaskList.MyTaskListAdapter;
import com.example.taskmanager.TaskList.TaskCategoryClass;
import com.example.taskmanager.TaskList.TaskClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;


public class HomeFragment extends Fragment implements MyCalendarAdapter.OnItemClickListener {

    Context thisFragmentContext, context;

    View view;
    public String userName, userId, userEmail, userRole;

    private TextView tvUserName;

//------------------------------------------------------------------
    // for calendar recycler view
    private TextView tvMonthYear, tvFwd, tvBwd;     // TextView for choosing the month of calendar
    private RecyclerView rvCalendar;
    private RecyclerView.LayoutManager calendarLayoutManager;
    private RecyclerView.Adapter calendarAdapter;
    private ArrayList<String> daysInMonth;


    private LocalDate selectedDate;
    private DateClass dateClass;      //custom date class
//------------------------------------------------------------------

    //set Task List
    private RecyclerView rvDashboardTaskList;
    private RecyclerView.LayoutManager taskListLayoutManager;
    private RecyclerView.Adapter taskListAdapter;
    private ArrayList<TaskClass> taskList;
    ArrayList<TaskCategoryClass> taskInDay;
//    private ArrayList<TaskCategoryClass> taskInDay;
    int[] appointment = new int[32];
    int[] medicine = new int[32];
    int[] workout = new int[32];
    int[] others = new int[32];

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

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);

        taskList = new ArrayList<>();
        taskInDay = new ArrayList<>();



        //------------------------------------------------------------------
        //Load user name and uer id
        tvUserName = view.findViewById(R.id.tvUserName);

        // Since we are using Fragment which does not have its own Intent.
        // We need to access the intent from the host activity that contains the Fragment
        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle != null) {
            userId = bundle.getString("userId");
            userName = bundle.getString("userName");
            userEmail = bundle.getString("userEmail");
            userRole = bundle.getString("userRole");

            tvUserName.setText(userName + " (" + userRole +")");
        }

        //------------------------------------------------------------------


        //set calendar
        rvCalendar = view.findViewById(R.id.rvCalendar);
        tvMonthYear = view.findViewById(R.id.tvMonthYear);
        selectedDate = LocalDate.now();
        //setMonthView();

        tvFwd = view.findViewById(R.id.tvFwd);
        tvBwd = view.findViewById(R.id.tvBwd);

        LoadDataFromDB();   // load data from Firestore

        tvFwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextMonthAction(v);
            }
        });

        tvBwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousMonthAction(v);
            }
        });

        //------------------------------------------------------------------
        //set Task List
        rvDashboardTaskList = view.findViewById(R.id.rvDashboardTaskList);
        setTaskList();

        //------------------------------------------------------------------


        return view;
    }

    // set the recycler view to display the calendar
    private void setMonthView() {


        //define sources
        daysInMonth = daysInMonthArray(selectedDate);
        dateClass = new DateClass(yearFromDate(selectedDate), monthFromDate(selectedDate), daysInMonth);

        // set the title of the calendar
        tvMonthYear.setText(monthYearFromDate(selectedDate));

        // feed the data to recycler view
        calendarAdapter = new MyCalendarAdapter(dateClass, taskInDay, thisFragmentContext);
        calendarLayoutManager = new GridLayoutManager(thisFragmentContext.getApplicationContext(), 7);
        rvCalendar.setLayoutManager(calendarLayoutManager);
        rvCalendar.setAdapter(calendarAdapter);
        calendarAdapter.notifyDataSetChanged();
        ((MyCalendarAdapter) calendarAdapter).setOnItemClickListener(this);
    }

    // get the days in a month and convert them to an array
    private ArrayList<String> daysInMonthArray(LocalDate date) {

        ArrayList<String> daysInMonthArray = new ArrayList<>();

        YearMonth yearMonth = YearMonth.from(date);

        int daysInMonth = yearMonth.lengthOfMonth();
        LocalDate firstOfMonth = selectedDate.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

        for (int i = 1; i <= 42; i++) {
            if (i <= dayOfWeek || i > daysInMonth + dayOfWeek) {
                daysInMonthArray.add("");
                TaskCategoryClass task = new TaskCategoryClass();
                taskInDay.add(task);
            }
            else {
                int day = i - dayOfWeek;
                daysInMonthArray.add(String.valueOf(day));

                // Insert no. of task
                TaskCategoryClass task = new TaskCategoryClass(appointment[day], medicine[day], workout[day], others[day]);
                taskInDay.add(task);
            }
        }

        // delete empty row of the calendar
        if (daysInMonthArray.get(35) == "") {   // delete end portion
            for (int i = 41; i >= 35; i--) {
                daysInMonthArray.remove(i);
                taskInDay.remove(i);
            }
        }

        if (daysInMonthArray.get(6) == "") {   // delete start portion
            for (int i = 6; i >= 0; i--) {
                daysInMonthArray.remove(i);
                taskInDay.remove(i);
            }
        }
        return daysInMonthArray;
    }

    // subtract the month by 1 when the user click backward button of the calendar
    public void previousMonthAction(View view) {
        selectedDate = selectedDate.minusMonths(1);
        LoadDataFromDB();   // load data from Firestore
        //setMonthView();
    }

    // add the month by 1 when the user click forward button of the calendar
    public void nextMonthAction(View view) {
        selectedDate = selectedDate.plusMonths(1);
        LoadDataFromDB();   // load data from Firestore
        //setMonthView();
    }

    // do something if the user click any one of the day on the calendar
    @Override
    public void onItemClick(String day) {
        Toast.makeText(thisFragmentContext, "Selected Day: " + day + " " + monthFromDate(selectedDate) + " " + yearFromDate(selectedDate), Toast.LENGTH_SHORT).show();


        // save the information to intent
/*        Intent i = new Intent(getActivity(), TaskDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("userName", userName);
        bundle.putString("userId", userId);
        bundle.putString("userEmail", userEmail);
        bundle.putString("userRole", userRole);
        i.putExtras(bundle);

        //redirect to MainActivity
        //context.startActivity(i);
        startActivity(i);*/
    }

    //set the content of Task List
    private void setTaskList() {
        // sources is taskList

        taskListAdapter = new MyTaskListAdapter(taskList, thisFragmentContext);
        taskListLayoutManager = new LinearLayoutManager(thisFragmentContext);
        rvDashboardTaskList.setLayoutManager(taskListLayoutManager);
        rvDashboardTaskList.setAdapter(taskListAdapter);
        taskListAdapter.notifyDataSetChanged();
    }

//------------------------------------------------------------------
    private void LoadDataFromDB() {

        int year = selectedDate.getYear();
        int month = selectedDate.getMonthValue();
        int day = 21;
        String patientId = "UTQYAjSOYmbFWBXryzHuRtvOcAF2";
        String status = "Pending";
        //String category = "Others";

        // Clear the lists first
        taskList.clear();
        taskInDay.clear();

        Query query = taskCollection
                .whereEqualTo("year", year)
                .whereEqualTo("month", month)
                //.whereEqualTo("day", day)
                .whereEqualTo("patientId", patientId)
                .whereEqualTo("status", status);
                //.orderBy("day");
                //.whereEqualTo("category", category);

        // Execute the query to get the matching documents
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        TaskClass eachTask = document.toObject(TaskClass.class);
                        taskList.add(eachTask);
                    }


                    for (int i = 0; i <= 31; i++) {
                        //TaskCategoryClass taskCategoryClass = new TaskCategoryClass();
                        //taskInDay.add(taskCategoryClass);
                        appointment[i] = 0;
                        medicine[i] = 0;
                        workout[i] = 0;
                        others[i] = 0;
                    }

                    // Loop through the downloaded data
                    for (int i = 0; i < taskList.size(); i++) {
                        int day = taskList.get(i).getDay();
                        String status = taskList.get(i).getCategory();
                        if (status.equals("Appointment"))
                            appointment[day]++;
                        if (status.equals("Medicine"))
                            medicine[day]++;
                        if (status.equals("Workout"))
                            workout[day]++;
                        if (status.equals("Others"))
                            others[day]++;
                    }

                    setMonthView();

                    setTaskList();

                    /*// Get the result of the query
                    QuerySnapshot querySnapshot = task.getResult();

                    // Get the number of matching documents
                    int numberOfDocuments = querySnapshot.size();*/

                    // Now, numberOfDocuments contains the count of documents that meet the conditions
                    //Toast.makeText(thisFragmentContext, Integer.toString(numberOfDocuments), Toast.LENGTH_LONG).show();

                } else {
                    // Handle the error
                    Toast.makeText(thisFragmentContext, "Error: " + task.getException().toString(), Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}