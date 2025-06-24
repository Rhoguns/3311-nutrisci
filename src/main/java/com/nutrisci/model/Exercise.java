package main.java.com.nutrisci.model;


import java.util.Date;

public class Exercise {
    private String type;
    private Date date;
    private int durationMinutes;

    public Exercise(String type, Date date, int durationMinutes) {
        this.type = type;
        this.date = date;
        this.durationMinutes = durationMinutes;
    }

    public double getCaloriesBurned() {
        double burnRate = 5.0; // kcal per minute stub
        return durationMinutes * burnRate;
    }

    public String summary() {
        return String.format(
            "Exercise: %s%nDate: %s%nDuration: %d min%nCalories Burned: %.2f kcal",
            type, date, durationMinutes, getCaloriesBurned()
        );
    }
}