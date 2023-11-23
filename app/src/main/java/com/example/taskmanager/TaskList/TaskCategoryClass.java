package com.example.taskmanager.TaskList;

public class TaskCategoryClass {
    int appointment, medicine, workout, others;

    public TaskCategoryClass() {
        this.appointment = 0;
        this.medicine = 0;
        this.workout = 0;
        this.others = 0;
    }

    public TaskCategoryClass(int appointment, int medicine, int workout, int others) {
        this.appointment = appointment;
        this.medicine = medicine;
        this.workout = workout;
        this.others = others;
    }

    public int getAppointment() {
        return appointment;
    }

    public void setAppointment(int appointment) {
        this.appointment = appointment;
    }

    public int getMedicine() {
        return medicine;
    }

    public void setMedicine(int medicine) {
        this.medicine = medicine;
    }

    public int getWorkout() {
        return workout;
    }

    public void setWorkout(int workout) {
        this.workout = workout;
    }

    public int getOthers() {
        return others;
    }

    public void setOthers(int others) {
        this.others = others;
    }
}
