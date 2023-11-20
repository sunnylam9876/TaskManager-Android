package com.example.taskmanager;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AddItemFragment extends Fragment {
    Context thisFragmentContext, context;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        thisFragmentContext = requireContext();
        View view = inflater.inflate(R.layout.fragment_add_item, container, false);



        return view;
    }
}