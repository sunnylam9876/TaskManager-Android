package com.example.taskmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.taskmanager.TaskList.MyTaskListAdapter;
import com.example.taskmanager.TaskList.TaskClass;

import java.util.ArrayList;

public class WholeListActivity extends AppCompatActivity {
    //set Task List
    private RecyclerView rvDashboardTaskList;
    private RecyclerView.LayoutManager taskListLayoutManager;
    private RecyclerView.Adapter taskListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whole_list);

        //set Task List
        rvDashboardTaskList = findViewById(R.id.rvWholeList);
        setTaskList();

        //------------------------------------------------------------------
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

        taskListAdapter = new MyTaskListAdapter(taskList, WholeListActivity.this);
        taskListLayoutManager = new LinearLayoutManager(this);
        rvDashboardTaskList.setLayoutManager(taskListLayoutManager);
        rvDashboardTaskList.setAdapter(taskListAdapter);
        taskListAdapter.notifyDataSetChanged();
    }
}