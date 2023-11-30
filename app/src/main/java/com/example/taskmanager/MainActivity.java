package com.example.taskmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.example.taskmanager.Calendar.MyCalendarAdapter;
import com.example.taskmanager.Utility.FragmentUtility;
import com.example.taskmanager.Service.MyForegroundService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements MyCalendarAdapter.OnItemClickListener{

    BottomNavigationView bottomNavigationView;

    String userId, userName, userEmail, userRole;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // get user information
        Intent intent = getIntent();
        if (intent != null) {
            userId = intent.getStringExtra("userId");
            //userName = intent.getStringExtra("userName");
            //userEmail = intent.getStringExtra("userEmail");
            userRole = intent.getStringExtra("userRole");

            if (userRole.equals("Doctor")) {
                setTheme(R.style.Doctor_Theme);
            }

            //if (userRole.equals("Patient")) {
                // start foreground service
                // pass data to foreground service using bundle
                Bundle bundle = new Bundle();
                bundle.putString("userId", userId);
                bundle.putString("userRole", userRole);
                Intent serviceIntent = new Intent(this, MyForegroundService.class);
                serviceIntent.putExtras(bundle);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // Start foreground service for Android 8.0 and higher
                    startForegroundService(serviceIntent);
                } else {
                    // Start foreground service for Android versions lower than 8.0
                    startService(serviceIntent);
                }
                //startService(serviceIntent);
            //}
        }

        setContentView(R.layout.activity_main);   // disabled since we use view binding

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
            /*if (item.getItemId() == R.id.AddItem) {
                //replaceFragment(new ListFragment());
                //FragmentUtility.replaceFragment(MainActivity.this, new ListFragment());
                FragmentUtility.replaceFragment(MainActivity.this, new AddItemFragment());

            }*/
            if (item.getItemId() == R.id.settings) {
                //replaceFragment(new SettingFragment());
                FragmentUtility.replaceFragment(MainActivity.this, new SettingFragment());
            }
            return true;
        });
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