package main.java.com.nutrisci.model;

import java.util.Date;

/**
 * Represents an exercise session, including its type, date/time, and duration.
 * Provides methods to calculate calories burned and return a summary.
 */
public class Exercise {
    /** The type of exercise (e.g., "Running", "Cycling", "Yoga"). */
    private String type;

    /** The date and time when the exercise was performed. */
    private Date date;

    /** The duration of the exercise, in whole minutes. */
    private int durationMinutes;

    /**
     * Constructs a new Exercise session.
     *
     * @param type            the exercise type
     * @param date            the date and time of the session
     * @param durationMinutes the duration in minutes
     */
    public Exercise(String type, Date date, int durationMinutes) {
        this.type = type;
        this.date = date;
        this.durationMinutes = durationMinutes;
    }

    /**
     * Calculates the total calories burned.
     * <p>
     * Uses a stubbed burn rate of 5.0 kcal per minute for demonstration.
     *
     * @return total calories burned
     */
    public double getCaloriesBurned() {
        double burnRate = 5.0; // kcal per minute stub
        return durationMinutes * burnRate;
    }

    /**
     * Returns a formatted summary of this exercise session,
     * including type, date, duration, and calories burned.
     *
     * @return a multi-line summary string
     */
    public String summary() {
        return String.format(
            "Exercise: %s%nDate: %s%nDuration: %d min%nCalories Burned: %.2f kcal",
            type, date, durationMinutes, getCaloriesBurned()
        );
    }
}