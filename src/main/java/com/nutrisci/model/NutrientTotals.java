/*
 * Decompiled with CFR 0.152.
 */
package com.nutrisci.model;

public class NutrientTotals {
    private double calories;
    private double protein;
    private double carbs;
    private double fat;
    private double fiber;

    public NutrientTotals() {
    }

    public NutrientTotals(double calories, double protein, double carbs, double fat) {
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
    }

    public NutrientTotals(double calories, double protein, double carbs, double fat, double fiber) {
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
        this.fiber = fiber;
    }

    // Getters and setters
    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public double getProtein() {
        return protein;
    }

    public void setProtein(double protein) {
        this.protein = protein;
    }

    public double getCarbs() {
        return carbs;
    }

    public void setCarbs(double carbs) {
        this.carbs = carbs;
    }

    public double getFat() {
        return fat;
    }

    public void setFat(double fat) {
        this.fat = fat;
    }

    public double getFiber() {
        return fiber;
    }

    public void setFiber(double fiber) {
        this.fiber = fiber;
    }

    @Override
    public String toString() {
        return String.format("NutrientTotals{calories=%.1f, protein=%.1f, carbs=%.1f, fat=%.1f, fiber=%.1f}", 
                           calories, protein, carbs, fat, fiber);
    }
}
