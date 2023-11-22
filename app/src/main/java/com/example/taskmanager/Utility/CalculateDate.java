package com.example.taskmanager.Utility;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CalculateDate {
    public static String monthYearFromDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");
        return date.format(formatter);
    }

    /*public static String monthFromDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM");
        return date.format(formatter);
    }*/

    public static int dayFromDate(LocalDate date) {
        return(date.getDayOfMonth());
    }

    public static int monthFromDate(LocalDate date) {
        return(date.getMonthValue());
    }

/*    public static String YearFromDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
        return date.format(formatter);
    }*/

    public static int yearFromDate(LocalDate date) {
        return(date.getYear());
    }
}
