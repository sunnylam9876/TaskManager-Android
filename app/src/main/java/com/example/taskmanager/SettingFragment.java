package com.example.taskmanager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class SettingFragment extends Fragment {

    Context thisFragmentContext;
    Button btnSignout;

    // Initialize Firebase Authentication
    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        thisFragmentContext = requireContext();
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        btnSignout = view.findViewById(R.id.btnSignout);

        btnSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // sign out
                auth.signOut();
                Toast.makeText(thisFragmentContext, "Signed out", Toast.LENGTH_SHORT).show();

                //redirect to login activity
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}