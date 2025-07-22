/*
 * Decompiled with CFR 0.152.
 */
package com.nutrisci.model;

import java.time.LocalDateTime;

public class SwapRule {
    private int id;
    private String goal;
    private String originalFood;
    private String suggestedFood;
    private LocalDateTime createdAt;

    public SwapRule() {
    }

    public SwapRule(String goal, String originalFood, String suggestedFood, LocalDateTime createdAt) {
        this.goal = goal;
        this.originalFood = originalFood;
        this.suggestedFood = suggestedFood;
        this.createdAt = createdAt;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGoal() {
        return this.goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getOriginalFood() {
        return this.originalFood;
    }

    public void setOriginalFood(String originalFood) {
        this.originalFood = originalFood;
    }

    public String getSuggestedFood() {
        return this.suggestedFood;
    }

    public void setSuggestedFood(String suggestedFood) {
        this.suggestedFood = suggestedFood;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String toString() {
        return "SwapRule{id=" + this.id + ", goal='" + this.goal + "', originalFood='" + this.originalFood + "', suggestedFood='" + this.suggestedFood + "', createdAt=" + String.valueOf(this.createdAt) + "}";
    }
}
