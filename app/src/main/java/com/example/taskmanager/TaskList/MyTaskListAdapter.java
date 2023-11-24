package com.example.taskmanager.TaskList;

import android.content.Context;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;


import com.example.taskmanager.AddItemFragment;
import com.example.taskmanager.ListFragment;
import com.example.taskmanager.MainActivity;
import com.example.taskmanager.R;
import com.example.taskmanager.Utility.FragmentUtility;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Locale;

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

        public ImageButton btnDelete, btnViewMore;

        // constructor
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            checkedTextView = itemView.findViewById(R.id.checkedTextView);
            tvTaskListDue = itemView.findViewById(R.id.tvTaskListDue);
            ivCategory = itemView.findViewById(R.id.ivCategory);
            btnDelete = itemView.findViewById(R.id.btnDelete);
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
        holder.checkedTextView.setText(eachTask.getTaskTitle());

        String hourString = String.format(Locale.getDefault(), "%02d", eachTask.getHour());
        String minuteString = String.format(Locale.getDefault(), "%02d", eachTask.getMinute());

        holder.tvTaskListDue.setText(eachTask.getMonth() + "-" + eachTask.getDay() + "-" + eachTask.getYear() + " (" +
                hourString + ":" + minuteString + ")");

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
        // To delete a task
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    TaskClass taskToDelete = taskList.get(adapterPosition);

                    // Ge the document id of the Firestore document to delete
                    String documentId = taskToDelete.getId();
                    FirebaseFirestore firestore_db = FirebaseFirestore.getInstance();
                    CollectionReference taskCollection = firestore_db.collection("Tasks");  // tasks collection

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
                    bundle.putString("test", "testing");
                    bundle.putBoolean("update", true);

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
