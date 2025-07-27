/*
 * Decompiled with CFR 0.152.
 */
package com.nutrisci.model;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Meal {
    private int id;
    private int profileId;
    private String type;
    private LocalDateTime loggedAt;
    private Map<String, Double> ingredients = new HashMap<>();

    public Meal() {
    }

    public Meal(String type, LocalDateTime loggedAt) {
        this.type = type;
        this.loggedAt = loggedAt;
    }

    // Getters and Setters
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getLoggedAt() {
        return loggedAt;
    }

    public void setLoggedAt(LocalDateTime loggedAt) {
        this.loggedAt = loggedAt;
    }

    public Map<String, Double> getIngredients() {
        return ingredients;
    }

    public void setIngredients(Map<String, Double> ingredients) {
        this.ingredients = ingredients;
    }

    public void addIngredient(String foodName, double quantity) {
        this.ingredients.put(foodName, quantity);
    }

    // This is for compatibility with older DAO code that might return java.util.Date
    public Date getDate() {
        return (loggedAt != null) ? java.sql.Timestamp.valueOf(loggedAt) : null;
    }

    @Override
    public String toString() {
        return "Meal{id=" + id + ", type='" + type + "', loggedAt=" + loggedAt + "}";
    }
}
