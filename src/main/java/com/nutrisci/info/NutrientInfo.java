package com.nutrisci.info;

/**
 * Food nutrition data.
 */
public class NutrientInfo {
    
    /** Calories per gram */
    private double caloriesPerGram;
    
    /** Protein per gram */
    private double proteinPerGram;
    
    /** Fat per gram */
    private double fatPerGram;
    
    /** Carbs per gram */
    private double carbsPerGram;

    public NutrientInfo() {
    }

    public NutrientInfo(double caloriesPerGram, double proteinPerGram, double fatPerGram, double carbsPerGram) {
        this.caloriesPerGram = caloriesPerGram;
        this.proteinPerGram = proteinPerGram;
        this.fatPerGram = fatPerGram;
        this.carbsPerGram = carbsPerGram;
    }

    public double getCaloriesPerGram() {
        return caloriesPerGram;
    }

    public double getProteinPerGram() {
        return proteinPerGram;
    }

    public double getFatPerGram() {
        return fatPerGram;
    }

    public double getCarbsPerGram() {
        return carbsPerGram;
    }

    public void setCaloriesPerGram(double caloriesPerGram) {
        this.caloriesPerGram = caloriesPerGram;
    }

    public void setProteinPerGram(double proteinPerGram) {
        this.proteinPerGram = proteinPerGram;
    }

    public void setFatPerGram(double fatPerGram) {
        this.fatPerGram = fatPerGram;
    }

    public void setCarbsPerGram(double carbsPerGram) {
        this.carbsPerGram = carbsPerGram;
    }
}