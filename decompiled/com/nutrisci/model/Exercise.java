/*
 * Decompiled with CFR 0.152.
 */
package com.nutrisci.model;

import java.time.LocalDateTime;

public class Exercise {
    private int id;
    private int profileId;
    private String name;
    private double durationMinutes;
    private double caloriesBurned;
    private LocalDateTime performedAt;

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

    public String toString() {
        return "Exercise{id=" + this.id + ", profileId=" + this.profileId + ", name='" + this.name + "', durationMinutes=" + this.durationMinutes + ", caloriesBurned=" + this.caloriesBurned + ", performedAt=" + String.valueOf(this.performedAt) + "}";
    }
}
