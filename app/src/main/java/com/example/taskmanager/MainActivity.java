package com.example.taskmanager;

import static com.example.taskmanager.Utility.CalculateDate.monthFromDate;
import static com.example.taskmanager.Utility.CalculateDate.yearFromDate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.taskmanager.Calendar.MyCalendarAdapter;
import com.example.taskmanager.Utility.FragmentUtility;
import com.example.taskmanager.service.MyForegroundService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements MyCalendarAdapter.OnItemClickListener{

    // for view binding
    //ActivityMainBinding binding;

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);   // disabled since we use view binding
        // for view binding
        //binding = ActivityMainBinding.inflate(getLayoutInflater());
        //setContentView(binding.getRoot());

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        //replaceFragment(new HomeFragment());    // set default view
        FragmentUtility.replaceFragment(MainActivity.this, new HomeFragment());

        // assign Fragment to each bottom navigation button
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                //replaceFragment(new HomeFragment());  //if don't use FragmentUtility class
                FragmentUtility.replaceFragment(MainActivity.this, new HomeFragment());
            }
            if (item.getItemId() == R.id.list) {
                //replaceFragment(new ListFragment());
                FragmentUtility.replaceFragment(MainActivity.this, new ListFragment());
            }
            if (item.getItemId() == R.id.AddItem) {
                //replaceFragment(new ListFragment());
                //FragmentUtility.replaceFragment(MainActivity.this, new ListFragment());
                FragmentUtility.replaceFragment(MainActivity.this, new AddItemFragment());

            }
            if (item.getItemId() == R.id.settings) {
                //replaceFragment(new SettingFragment());
                FragmentUtility.replaceFragment(MainActivity.this, new SettingFragment());
            }
            return true;
        });

        // for testing
        Intent intent = getIntent();
        if (intent != null) {
            String userId = intent.getStringExtra("userId");
            String userName = intent.getStringExtra("userName");
            String userEmail = intent.getStringExtra("userEmail");
            String userRole = intent.getStringExtra("userRole");
        }

        // start foreground service
        Intent serviceIntent = new Intent(this, MyForegroundService.class);
        startService(serviceIntent);
    }

    @Override
    public void onItemClick(String day) {
        //Toast.makeText(this, "Selected Day: " + day + " " + monthFromDate(selectedDate) + " " + yearFromDate(selectedDate), Toast.LENGTH_SHORT).show();
    }

    // if don't use FragmentUtility class, un-comment the below statements
    /*    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }*/


}