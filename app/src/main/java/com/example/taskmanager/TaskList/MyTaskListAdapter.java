package com.example.taskmanager.TaskList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.taskmanager.R;

import java.util.List;

public class MyTaskListAdapter extends RecyclerView.Adapter<MyTaskListAdapter.MyViewHolder> {
    // 1 - define data sources
    private List<TaskClass> taskList;
    private Context context;

    // constructor
    public MyTaskListAdapter(List<TaskClass> taskList, Context context) {
        this.taskList = taskList;
        this.context = context;
    }

    //2 define view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public CheckedTextView checkedTextView;
        public TextView tvTaskListDue;

        public ImageView ivCategory;

        // constructor
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            checkedTextView = itemView.findViewById(R.id.checkedTextView);
            tvTaskListDue = itemView.findViewById(R.id.tvTaskListDue);
            ivCategory = itemView.findViewById(R.id.ivCategory);


            // add OnClickListener:

        }
    }

    // 3- implementing the methods
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_task_cell, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Assign title, due date and color for the list
        TaskClass eachTask = taskList.get(position);
        holder.checkedTextView.setText(eachTask.getTaskTitle());
        holder.tvTaskListDue.setText(eachTask.getMonth() + "-" + eachTask.getDay() + "-" + eachTask.getYear() + " (" +
                                        eachTask.getHour() + ":" + eachTask.getMinute() + ")");

        switch(eachTask.getCategory()) {
            case "Appointment":
                holder.ivCategory.setImageDrawable(context.getDrawable(R.drawable.baseline_circle_appointment_24));
                break;

            case "Medicine":
                holder.ivCategory.setImageDrawable(context.getDrawable(R.drawable.baseline_circle_medicine_24));
                break;

            case "Workout":
                holder.ivCategory.setImageDrawable(context.getDrawable(R.drawable.baseline_circle_workout_24));
                break;

            case "Others":
                holder.ivCategory.setImageDrawable(context.getDrawable(R.drawable.baseline_circle_others_24));
                break;

        }

        //holder.tvTaskListDue.setText(eachTask.getDueYear() + " " + eachTask.getDueMonth() + eachTask.getDueDay());
        //holder.tvTaskBy.setText(eachTask.getTeamMember());
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }
}
