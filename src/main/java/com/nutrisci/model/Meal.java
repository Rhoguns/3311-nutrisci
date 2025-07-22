/*
 * Decompiled with CFR 0.152.
 */
package com.nutrisci.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Meal {
    private int id;
    private int profileId;
    private String type;
    private String mealType;  // Add this field
    private LocalDateTime loggedAt;
    private LocalDate date;  // Add this field
    private Map<String, Double> ingredients = new HashMap<>();

    public Meal() {
    }

    public Meal(String type, LocalDateTime loggedAt) {
        this.type = type;
        this.loggedAt = loggedAt;
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

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getLoggedAt() {
        return this.loggedAt;
    }

    public void setLoggedAt(LocalDateTime loggedAt) {
        this.loggedAt = loggedAt;
    }

    public Map<String, Double> getIngredients() {
        return this.ingredients;
    }

    public void setIngredients(Map<String, Double> ingredients) {
        this.ingredients = ingredients;
    }

    // Add missing methods for tests
    public String getMealType() {
        return mealType != null ? mealType : type;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
        if (this.type == null) this.type = mealType; 
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String toString() {
        return "Meal{id=" + this.id + ", profileId=" + this.profileId + ", type='" + this.type + "', loggedAt=" + String.valueOf(this.loggedAt) + ", ingredients=" + String.valueOf(this.ingredients) + "}";
    }
}
