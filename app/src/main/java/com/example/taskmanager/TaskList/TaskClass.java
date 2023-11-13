package com.example.taskmanager.TaskList;

public class TaskClass {
    private String taskTitle;
    private int dueYear, dueMonth, dueDay;

    public TaskClass(String taskTitle, int dueYear, int dueMonth, int dueDay) {
        this.taskTitle = taskTitle;
        this.dueYear = dueYear;
        this.dueMonth = dueMonth;
        this.dueDay = dueDay;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public int getDueYear() {
        return dueYear;
    }

    public void setDueYear(int dueYear) {
        this.dueYear = dueYear;
    }

    public int getDueMonth() {
        return dueMonth;
    }

    public void setDueMonth(int dueMonth) {
        this.dueMonth = dueMonth;
    }

    public int getDueDay() {
        return dueDay;
    }

    public void setDueDay(int dueDay) {
        this.dueDay = dueDay;
    }
}
