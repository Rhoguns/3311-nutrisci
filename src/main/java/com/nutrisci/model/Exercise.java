package com.nutrisci.model;

import java.time.LocalDateTime;

public class Exercise {
    // Unique identifier for the exercise record.
    private int id;
    // ID of the user profile associated with this exercise.
    private int profileId;
    // Name or description of the exercise (e.g., "Running", "Weightlifting").
    private String name;
    // Duration of the exercise in minutes.
    private double durationMinutes;
    // Estimated calories burned during the exercise.
    private double caloriesBurned;
    // Timestamp indicating when the exercise was performed.
    private LocalDateTime performedAt;

    public Exercise() {
        // default constructor
    }

    public Exercise(int profileId, String name, double durationMinutes, double caloriesBurned, LocalDateTime performedAt) {
        this.profileId = profileId;
        this.name = name;
        this.durationMinutes = durationMinutes;
        this.caloriesBurned = caloriesBurned;
        this.performedAt = performedAt;
    }

    /**
     * Getters and Setters for all fields.
     */

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProfileId() {
        return profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(double durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public double getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(double caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }

    public LocalDateTime getPerformedAt() {
        return performedAt;
    }

    public void setPerformedAt(LocalDateTime performedAt) {
        this.performedAt = performedAt;
    }

    @Override
    public String toString() {
        return "Exercise{" +
               "id=" + id +
               ", profileId=" + profileId +
               ", name='" + name + '\'' +
               ", durationMinutes=" + durationMinutes +
               ", caloriesBurned=" + caloriesBurned +
               ", performedAt=" + performedAt +
               '}';
    }
}