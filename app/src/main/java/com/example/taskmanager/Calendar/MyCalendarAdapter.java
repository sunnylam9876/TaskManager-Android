package com.example.taskmanager.Calendar;

import static com.example.taskmanager.Utility.CalculateDate.dayFromDate;
import static com.example.taskmanager.Utility.CalculateDate.monthFromDate;
import static com.example.taskmanager.Utility.CalculateDate.yearFromDate;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanager.R;
import com.example.taskmanager.TaskList.TaskCategoryClass;

import java.time.LocalDate;
import java.util.ArrayList;

public class MyCalendarAdapter extends RecyclerView.Adapter<MyCalendarAdapter.MyViewHolder> {
    //1 - define the data data source
    //private final ArrayList<String> daysOfMonth;
    private DateClass dateClass;
    private Context context;

    private ArrayList<String> daysOfMonth;

    private ArrayList<TaskCategoryClass> taskInDay;

    private LocalDate today;

    private static OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(String day);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


/*    public MyCalendarAdapter(ArrayList<String> daysOfMonth, Context context) {
        this.daysOfMonth = daysOfMonth;
        this.context = context;
    }*/
    public MyCalendarAdapter(DateClass dateClass, ArrayList<TaskCategoryClass> taskInDay, Context context) {
        this.dateClass = dateClass;
        this.context = context;
        this.daysOfMonth = dateClass.getDaysInMonth();
        this.taskInDay = taskInDay;
    }

    //3- Implementing the methods

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_calendar_cell, parent,false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        String day = daysOfMonth.get(position);

        //allocate day array to each TextView
        holder.cellDayText.setText(day);

        // For testing purpose:
        /*holder.tvNumberOfTask.setText("A: " + taskInDay.get(position).getAppointment() +
                                        "; M: " + taskInDay.get(position).getMedicine() +
                                        "; W: " + taskInDay.get(position).getWorkout() +
                                        "; O: " + taskInDay.get(position).getOthers());*/

        //get today date, then highlight today on calendar
        today = LocalDate.now();
        int today_year = yearFromDate(today);
        int today_month = monthFromDate(today);
        int today_day = dayFromDate(today);
        //highlight Today
        if (holder.cellDayText.getText().toString().equals(Integer.toString(today_day)) &&
                dateClass.getYear() == today_year && dateClass.getMonth() == today_month) {
            int color = ContextCompat.getColor(holder.itemView.getContext(), R.color.lightblue);
            holder.cellDayText.setBackgroundColor(color);
        }

        // to be amended to not get the value of a TextView to increase efficiency
        //int holderDay = Integer.parseInt(holder.cellDayText.getText().toString());
/*        if (holder.cellDayText.getText().toString().equals("")) {
            holder.ivAppointment.setVisibility(View.INVISIBLE);
            holder.ivMedicine.setVisibility(View.INVISIBLE);
            holder.ivWorkout.setVisibility(View.INVISIBLE);
            holder.ivOthers.setVisibility(View.INVISIBLE);
        }*/

        if (taskInDay.get(position).getAppointment() > 0)
            holder.ivAppointment.setVisibility(View.VISIBLE);
        else
            holder.ivAppointment.setVisibility(View.INVISIBLE);

        if (taskInDay.get(position).getMedicine() > 0)
            holder.ivMedicine.setVisibility(View.VISIBLE);
        else
            holder.ivMedicine.setVisibility(View.INVISIBLE);

        if (taskInDay.get(position).getWorkout() > 0)
            holder.ivWorkout.setVisibility(View.VISIBLE);
        else
            holder.ivWorkout.setVisibility(View.INVISIBLE);

        if (taskInDay.get(position).getOthers() > 0)
            holder.ivOthers.setVisibility(View.VISIBLE);
        else
            holder.ivOthers.setVisibility(View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return daysOfMonth.size();
    }

    //2- View holder class
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView cellDayText;
        public TextView tvNumberOfTask;
        public ImageView ivAppointment, ivMedicine, ivWorkout, ivOthers;

        public LinearLayout calendarCell;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            cellDayText = itemView.findViewById(R.id.cellDayText);
            tvNumberOfTask = itemView.findViewById(R.id.tvNumberOfTask);
            //cellTask = itemView.findViewById(R.id.cellTask_1);
            calendarCell = itemView.findViewById(R.id.calendarCell);
            ivAppointment = itemView.findViewById(R.id.ivAppointment);
            ivMedicine = itemView.findViewById(R.id.ivMedicine);
            ivWorkout = itemView.findViewById(R.id.ivWorkout);
            ivOthers = itemView.findViewById(R.id.ivOthers);


            calendarCell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Change the bg color of the selected day
                    /*int color = ContextCompat.getColor(itemView.getContext(), R.color.pink);
                    // Check if there's a previously selected TextView
                    if (cellDayText != null) {
                        // Revert the background color of the previously selected TextView
                        cellDayText.setBackgroundColor(Color.WHITE);
                    }

                    // Set the background color of the clicked TextView
                    cellDayText.setBackgroundColor(color);
                    cellDayText = cellDayText; // Update the currently selected TextView*/

                    // call the onItemClick method in HomeFragment.java
                    if (onItemClickListener != null && !cellDayText.getText().equals("")) {
                        onItemClickListener.onItemClick(cellDayText.getText().toString());
                    }
                }
            });
        }
    }
}
