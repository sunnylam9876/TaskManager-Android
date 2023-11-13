package com.example.taskmanager;

import static com.example.taskmanager.Utility.CalculateDate.yearFromDate;
import static com.example.taskmanager.Utility.CalculateDate.monthFromDate;
import static com.example.taskmanager.Utility.CalculateDate.monthYearFromDate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taskmanager.Calendar.DateClass;
import com.example.taskmanager.Calendar.MyCalendarAdapter;
import com.example.taskmanager.TaskList.MyTaskListAdapter;
import com.example.taskmanager.TaskList.TaskClass;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;

public class DashBoard extends AppCompatActivity implements MyCalendarAdapter.OnItemClickListener {

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //------------------------------------------------------------------
        //set calendar
        rvCalendar = findViewById(R.id.rvCalendar);
        tvMonthYear = findViewById(R.id.tvMonthYear);
        selectedDate = LocalDate.now();
        setMonthView();

        tvFwd = findViewById(R.id.tvFwd);
        tvBwd = findViewById(R.id.tvBwd);
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
        rvDashboardTaskList = findViewById(R.id.rvDashboardTaskList);
        setTaskList();

        //------------------------------------------------------------------
        


    }

    //set the content of Task List
    private void setTaskList() {
        // define data sources
        ArrayList<TaskClass> taskList = new ArrayList<>();
        TaskClass taskClass = new TaskClass("Task 1", 2023, 11, 13);
        taskList.add(taskClass);

        taskListAdapter = new MyTaskListAdapter(taskList, DashBoard.this);
        taskListLayoutManager = new LinearLayoutManager(this);
        rvDashboardTaskList.setLayoutManager(taskListLayoutManager);
        rvDashboardTaskList.setAdapter(taskListAdapter);
        taskListAdapter.notifyDataSetChanged();
    }

    // set the recycler view to display the calendar
    private void setMonthView() {
        //define sources
        daysInMonth = daysInMonthArray(selectedDate);
        dateClass = new DateClass(yearFromDate(selectedDate), monthFromDate(selectedDate), daysInMonth);

        // set the title of the calendar
        tvMonthYear.setText(monthYearFromDate(selectedDate));

        // feed the data to recycler view
        calendarAdapter = new MyCalendarAdapter(dateClass, DashBoard.this);
        calendarLayoutManager = new GridLayoutManager(getApplicationContext(), 7);
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
        Toast.makeText(this, "Selected Day: " + day + " " + monthFromDate(selectedDate) + " " + yearFromDate(selectedDate), Toast.LENGTH_SHORT).show();
    }
}