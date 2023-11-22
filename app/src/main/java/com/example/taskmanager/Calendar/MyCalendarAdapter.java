package com.example.taskmanager.Calendar;

import static com.example.taskmanager.Utility.CalculateDate.dayFromDate;
import static com.example.taskmanager.Utility.CalculateDate.monthFromDate;
import static com.example.taskmanager.Utility.CalculateDate.yearFromDate;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanager.R;

import java.time.LocalDate;
import java.util.ArrayList;

public class MyCalendarAdapter extends RecyclerView.Adapter<MyCalendarAdapter.MyViewHolder> {
    //1 - define the data data source
    //private final ArrayList<String> daysOfMonth;
    private DateClass dateClass;
    private Context context;

    private ArrayList<String> daysOfMonth;

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
    public MyCalendarAdapter(DateClass dateClass, Context context) {
        this.dateClass = dateClass;
        this.context = context;
        this.daysOfMonth = dateClass.getDaysInMonth();
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

        holder.cellDayText.setText(day);    //allocate day array to each TextView

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
/*        if (Integer.parseInt(holder.cellTask.getText().toString()) > 0) {
            Drawable drawable = ContextCompat.getDrawable(holder.cellTask.getContext(), R.drawable.task_cell_background);
            holder.cellTask.setBackground(drawable);
        }*/
    }

    @Override
    public int getItemCount() {
        return daysOfMonth.size();
    }

    //2- View holder class
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView cellDayText;
        public TextView cellTask;

        public LinearLayout calendarCell;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            cellDayText = itemView.findViewById(R.id.cellDayText);
            //cellTask = itemView.findViewById(R.id.cellTask_1);
            calendarCell = itemView.findViewById(R.id.calendarCell);


            calendarCell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(cellDayText.getText().toString());
                    }
                }
            });

           /* cellDayText.setOnClickListener(new View.OnClickListener() {
                @Override
                // if user click the day of calendar
                public void onClick(View v) {
                    //Toast.makeText(v.getContext(),dayOfMonth.getText(), Toast.LENGTH_LONG).show();
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(cellDayText.getText().toString());
                    }
                }
            });*/
        }
    }
}
