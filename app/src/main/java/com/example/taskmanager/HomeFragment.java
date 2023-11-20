package com.example.taskmanager;

import static android.content.Intent.getIntent;
import static com.example.taskmanager.Utility.CalculateDate.monthFromDate;
import static com.example.taskmanager.Utility.CalculateDate.monthYearFromDate;
import static com.example.taskmanager.Utility.CalculateDate.yearFromDate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

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
import com.example.taskmanager.TaskList.TaskClass;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;


public class HomeFragment extends Fragment implements MyCalendarAdapter.OnItemClickListener {

    Context thisFragmentContext, context;

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
    //private ArrayList<TaskClass> taskList;

    //------------------------------------------------------------------
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        thisFragmentContext = requireContext();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //------------------------------------------------------------------
        //Load user name and uer id
        tvUserName = view.findViewById(R.id.tvUserName);

        // Since we are using Fragment which does not have its own Intent.
        // We need to access the intent from the host activity that contains the Fragment
        //Intent intent = getActivity().getIntent();
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
        setMonthView();

        tvFwd = view.findViewById(R.id.tvFwd);
        tvBwd = view.findViewById(R.id.tvBwd);
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
        calendarAdapter = new MyCalendarAdapter(dateClass, thisFragmentContext);
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
            }
            else {
                daysInMonthArray.add(String.valueOf(i - dayOfWeek));
            }
        }
        return daysInMonthArray;
    }

    // subtract the month by 1 when the user click backward button of the calendar
    public void previousMonthAction(View view) {
        selectedDate = selectedDate.minusMonths(1);
        setMonthView();
    }

    // add the month by 1 when the user click forward button of the calendar
    public void nextMonthAction(View view) {
        selectedDate = selectedDate.plusMonths(1);
        setMonthView();
    }

    // do something if the user click any one of the day on the calendar
    @Override
    public void onItemClick(String day) {
        Toast.makeText(thisFragmentContext, "Selected Day: " + day + " " + monthFromDate(selectedDate) + " " + yearFromDate(selectedDate), Toast.LENGTH_SHORT).show();

        // save the information to intent
        Intent i = new Intent(getActivity(), TaskDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("userName", userName);
        bundle.putString("userId", userId);
        bundle.putString("userEmail", userEmail);
        bundle.putString("userRole", userRole);
        i.putExtras(bundle);

        //redirect to MainActivity
        //context.startActivity(i);
        startActivity(i);
    }

    //set the content of Task List
    private void setTaskList() {
        // define data sources
        ArrayList<TaskClass> taskList = new ArrayList<>();
        TaskClass taskClass = new TaskClass("Task 1", "manager1", "member1", 2023, 11, 13);
        TaskClass taskClass1 = new TaskClass("Task 2", "manager2", "member2", 2023, 11, 14);
        TaskClass taskClass2 = new TaskClass("Task 3", "manager3", "member3", 2023, 11, 15);
        TaskClass taskClass3 = new TaskClass("Task 4", "manager4", "member4", 2023, 11, 16);
        taskList.add(taskClass);
        taskList.add(taskClass1);
        taskList.add(taskClass2);
        taskList.add(taskClass3);

        taskListAdapter = new MyTaskListAdapter(taskList, thisFragmentContext);
        taskListLayoutManager = new LinearLayoutManager(thisFragmentContext);
        rvDashboardTaskList.setLayoutManager(taskListLayoutManager);
        rvDashboardTaskList.setAdapter(taskListAdapter);
        taskListAdapter.notifyDataSetChanged();
    }
}