package com.example.taskmanager.TaskList;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;


import com.example.taskmanager.AddItemFragment;
import com.example.taskmanager.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MyTaskListAdapter extends RecyclerView.Adapter<MyTaskListAdapter.MyViewHolder> {

    FirebaseFirestore firestore_db = FirebaseFirestore.getInstance();
    CollectionReference taskCollection = firestore_db.collection("Tasks");  // tasks collection

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
        public CheckedTextView ctvComplete;
        public TextView tvTaskListDue;
        public ImageView ivCategory;

        public ImageButton btnDelete, btnViewMore;

        // constructor
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            ctvComplete = itemView.findViewById(R.id.ctvComplete);
            tvTaskListDue = itemView.findViewById(R.id.tvTaskListDue);
            ivCategory = itemView.findViewById(R.id.ivCategory);
            btnDelete = itemView.findViewById(R.id.btnDelete_temp);
            btnViewMore = itemView.findViewById(R.id.btnViewMore);
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
        holder.ctvComplete.setText(eachTask.getTaskTitle());

        String hourString = String.format(Locale.getDefault(), "%02d", eachTask.getHour());
        String minuteString = String.format(Locale.getDefault(), "%02d", eachTask.getMinute());

        holder.tvTaskListDue.setText(eachTask.getMonth() + "-" + eachTask.getDay() + "-" + eachTask.getYear() + " (" +
                hourString + ":" + minuteString + ")");

        String status = eachTask.getStatus();
        if (status.equals("Completed")) {
            // set the text to strike through if the task is completed
            holder.ctvComplete.setChecked(true);
            holder.ctvComplete.setPaintFlags(holder.ctvComplete.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvTaskListDue.setPaintFlags(holder.ctvComplete.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        if (status.equals("Pending")) {
            // set the text to red if the task is overdue
            LocalDate currentDate = LocalDate.now();
            LocalDate taskDate = LocalDate.of(eachTask.getYear(), eachTask.getMonth(), eachTask.getDay());

            // compare the dates
            int comparison = currentDate.compareTo(taskDate);
            if (comparison > 0) {
                holder.ctvComplete.setTextColor(Color.RED);
                holder.tvTaskListDue.setTextColor(Color.RED);
            }
        }

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
//---------------------------------------------------------------------------------------------------------------------------------
        // set the task to complete
        holder.ctvComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = holder.ctvComplete.isChecked();
                String statusString;
                if (isChecked) {
                    // if the check box is unchecked by user
                    // set the checkbox be unchecked
                    holder.ctvComplete.setChecked(false);
                    statusString = "Pending";
                    // set text style to not strike through
                    holder.ctvComplete.setPaintFlags(holder.ctvComplete.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    holder.tvTaskListDue.setPaintFlags(holder.ctvComplete.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    if (status.equals("Pending")) {
                        // set the text to red if the task is overdue
                        LocalDate currentDate = LocalDate.now();
                        LocalDate taskDate = LocalDate.of(eachTask.getYear(), eachTask.getMonth(), eachTask.getDay());

                        // compare the dates
                        int comparison = currentDate.compareTo(taskDate);
                        if (comparison > 0) {
                            holder.ctvComplete.setTextColor(Color.RED);
                            holder.tvTaskListDue.setTextColor(Color.RED);
                        }
                    }
                    //Toast.makeText(context, "isChecked", Toast.LENGTH_LONG).show();
                } else {
                    // if the check box is checked by user
                    // mark the checkbox be checked
                    holder.ctvComplete.setChecked(true);
                    statusString = "Completed";
                    // set text style to strike through
                    holder.ctvComplete.setPaintFlags(holder.ctvComplete.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.tvTaskListDue.setPaintFlags(holder.ctvComplete.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.ctvComplete.setTextColor(Color.BLACK);
                    holder.tvTaskListDue.setTextColor(Color.BLACK);
                    //Toast.makeText(context, "not isChecked", Toast.LENGTH_LONG).show();
                }
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    TaskClass taskToDelete = taskList.get(adapterPosition);

                    // Ge the document id of the Firestore document to delete
                    String documentId = taskToDelete.getId();


                    Map<String, Object> updatedData = new HashMap<>();
                    updatedData.put("status", statusString);

                    // Update the document in Firestore
                    taskCollection.document(documentId)
                            .update(updatedData)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(context, "Task updated", Toast.LENGTH_LONG).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(context, "Error on updating task:" + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                }
            }
        });

//---------------------------------------------------------------------------------------------------------------------------------
        // To delete a task
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    TaskClass taskToDelete = taskList.get(adapterPosition);

                    // Ge the document id of the Firestore document to delete
                    String documentId = taskToDelete.getId();


                    taskCollection.document(documentId)
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    // Document successfully deleted
                                    taskList.remove(adapterPosition);
                                    notifyItemRemoved(adapterPosition);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(context, "Error: " + e.toString(), Toast.LENGTH_LONG).show();
                                }
                            });
                }
            }
        });
//---------------------------------------------------------------------------------------------------------------------------------
        // To view the task details
        holder.btnViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    TaskClass taskToShowDetails = taskList.get(adapterPosition);

                    // Create a bundle to pass task details to other Fragment
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("taskDetails", taskToShowDetails);
                    //bundle.putString("test", "testing");
                    bundle.putBoolean("update", true);
                    bundle.putString("newOrUpdate", "update");

                    // Navigate to other Fragment
                    AddItemFragment addItemFragment = new AddItemFragment();
                    //ListFragment addItemFragment = new ListFragment();
                    addItemFragment.setArguments(bundle);

                    // Use FragmentManager to replace the current fragment with AddItemFragment
                    FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.frame_layout, addItemFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }


}
