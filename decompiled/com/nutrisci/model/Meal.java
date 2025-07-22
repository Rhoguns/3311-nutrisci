/*
 * Decompiled with CFR 0.152.
 */
package com.nutrisci.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Meal {
    private int id;
    private int profileId;
    private String type;
    private LocalDateTime loggedAt;
    private Map<String, Double> ingredients = new HashMap<String, Double>();

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

    public String toString() {
        return "Meal{id=" + this.id + ", profileId=" + this.profileId + ", type='" + this.type + "', loggedAt=" + String.valueOf(this.loggedAt) + ", ingredients=" + String.valueOf(this.ingredients) + "}";
    }
}
