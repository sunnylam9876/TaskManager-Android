package com.example.taskmanager;

import static com.example.taskmanager.Utility.CalculateDate.yearFromDate;
import static com.example.taskmanager.Utility.CalculateDate.dayFromDate;
import static com.example.taskmanager.Utility.CalculateDate.monthFromDate;
import static com.example.taskmanager.Utility.CalculateDate.monthYearFromDate;
import static com.example.taskmanager.Utility.CalculateDate.yearFromDate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taskmanager.Class.DateClass;
import com.example.taskmanager.calendar.CalendarAdapter;
import com.example.taskmanager.calendar.MyCalendarAdapter;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

public class DashBoard extends AppCompatActivity implements MyCalendarAdapter.OnItemClickListener {

    //------------------------------------------------------------------
    // for calendar recycler view
    private TextView tvMonthYear, tvFwd, tvBwd;     // TextView for choosing the month of calendar
    private RecyclerView rvCalendar;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter calendarAdapter;
    private ArrayList<String> daysInMonth;
    private LocalDate selectedDate;
    private DateClass dateClass;      //custom date class
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


    }

    // set the recycler view to display the calendar
    private void setMonthView() {
        daysInMonth = daysInMonthArray(selectedDate);
        dateClass = new DateClass(yearFromDate(selectedDate), monthFromDate(selectedDate), daysInMonth);
        tvMonthYear.setText(monthYearFromDate(selectedDate));
        //calendarAdapter = new MyCalendarAdapter(daysInMonth, DashBoard.this);
        calendarAdapter = new MyCalendarAdapter(dateClass, DashBoard.this);
        layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        rvCalendar.setLayoutManager(layoutManager);
        rvCalendar.setAdapter(calendarAdapter);
        calendarAdapter.notifyDataSetChanged();

        //calendarAdapter = new MyCalendarAdapter(daysInMonth, DashBoard.this);
        calendarAdapter = new MyCalendarAdapter(dateClass, DashBoard.this);
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