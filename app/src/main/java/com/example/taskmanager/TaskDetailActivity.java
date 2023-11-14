package com.example.taskmanager;

import static android.widget.AdapterView.*;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

public class TaskDetailActivity extends AppCompatActivity {

    String[] members = {"John", "Peter", "Mary", "Jim"};
    AutoCompleteTextView tvAddMember;
    ArrayAdapter<String> memberAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        tvAddMember = findViewById(R.id.tvAddMember);
        memberAdapter = new ArrayAdapter<String>(this, R.layout.background_memberlist, members);
        tvAddMember.setAdapter(memberAdapter);

        tvAddMember.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Toast.makeText(TaskDetailActivity.this, "Item" + item, Toast.LENGTH_SHORT).show();
            }
        });

    }
}