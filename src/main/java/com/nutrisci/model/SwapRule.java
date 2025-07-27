/*
 * Decompiled with CFR 0.152.
 */
package com.nutrisci.model;

public class SwapRule {
    private int id;
    private String originalFood;
    private String suggestedFood;
    private String goal;
    private double improvementValue;

    public SwapRule() {
    }

    public SwapRule(String originalFood, String suggestedFood, String goal) {
        this.originalFood = originalFood;
        this.suggestedFood = suggestedFood;
        this.goal = goal;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOriginalFood() {
        return originalFood;
    }

    public void setOriginalFood(String originalFood) {
        this.originalFood = originalFood;
    }

    public String getSuggestedFood() {
        return suggestedFood;
    }

    public void setSuggestedFood(String suggestedFood) {
        this.suggestedFood = suggestedFood;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public void setImprovementValue(double improvementValue) {
        this.improvementValue = improvementValue;
    }

    public double getImprovementValue() {
        return improvementValue;
    }

    @Override
    public String toString() {
        return String.format("%s → %s (%s)", originalFood, suggestedFood, goal);
    }
}
