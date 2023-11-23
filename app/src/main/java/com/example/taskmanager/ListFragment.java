package com.example.taskmanager;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.taskmanager.TaskList.TaskClass;


public class ListFragment extends Fragment {

    Context thisFragmentContext, context;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        thisFragmentContext = requireContext();
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        //Bundle bundle = getActivity().getIntent().getExtras();
        Bundle bundle = getArguments();
        if (bundle != null) {

            Boolean update = bundle.getBoolean("update");
            TaskClass taskDetail = bundle.getParcelable("taskDetails");
            String test = bundle.getString("test");

            //for testing only
            TextView tvHeading = view.findViewById(R.id.tvHeading);
            tvHeading.setText("update: " + update + "; test: " + test);
        }

        return view;
    }
}