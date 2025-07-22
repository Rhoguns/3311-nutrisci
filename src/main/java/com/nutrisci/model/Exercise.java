/*
 * Decompiled with CFR 0.152.
 */
package com.nutrisci.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Exercise {
    private int id;
    private int profileId;
    private String name;
    private String exerciseType;  // Add this field
    private double durationMinutes;
    private int duration;  // Add this field for integer duration
    private double caloriesBurned;
    private LocalDateTime performedAt;
    private LocalDate date;  // Add this field

    public Exercise() {
    }

    public Exercise(int profileId, String name, double durationMinutes, double caloriesBurned, LocalDateTime performedAt) {
        this.profileId = profileId;
        this.name = name;
        this.durationMinutes = durationMinutes;
        this.caloriesBurned = caloriesBurned;
        this.performedAt = performedAt;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProfileId() {
        return this.profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getDurationMinutes() {
        return this.durationMinutes;
    }

    public void setDurationMinutes(double durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public double getCaloriesBurned() {
        return this.caloriesBurned;
    }

    public void setCaloriesBurned(double caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }

    public LocalDateTime getPerformedAt() {
        return this.performedAt;
    }

    public void setPerformedAt(LocalDateTime performedAt) {
        this.performedAt = performedAt;
    }

    // Add missing methods for tests
    public String getExerciseType() {
        return this.exerciseType;
    }

    public void setExerciseType(String exerciseType) {
        this.exerciseType = exerciseType;
    }

    public int getDuration() {
        return this.duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String toString() {
        return "Exercise{id=" + this.id + ", profileId=" + this.profileId + ", name='" + this.name + "', durationMinutes=" + this.durationMinutes + ", caloriesBurned=" + this.caloriesBurned + ", performedAt=" + String.valueOf(this.performedAt) + "}";
    }
}
