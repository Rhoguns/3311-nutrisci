package com.nutrisci.controller;

import com.nutrisci.dao.NutritionDAO;
import java.sql.SQLException;
import java.util.Map;
import java.util.HashMap;
import com.nutrisci.info.NutrientInfo;

public class NutritionController {
    private final NutritionDAO dao;

    public NutritionController(NutritionDAO dao) {
        this.dao = dao;
    }

    public double getCaloriesPerGram(String foodName) throws SQLException {
        return dao.getNutrientInfo(foodName).getCaloriesPerGram();
    }

    public Map<String, Double> getNutrientBreakdown(String foodName) throws SQLException {

        NutrientInfo nutrientInfo = dao.getNutrientInfo(foodName);
        
        Map<String, Double> breakdown = new HashMap<>();
        breakdown.put("calories", nutrientInfo.getCaloriesPerGram());
        breakdown.put("protein", nutrientInfo.getProteinPerGram());
        breakdown.put("carbs", nutrientInfo.getCarbsPerGram());
        breakdown.put("fat", nutrientInfo.getFatPerGram());
        
        return breakdown;
    }

    public double getFoodCalories(String foodName) throws SQLException {
        return dao.getNutrientInfo(foodName).getCaloriesPerGram() * 100.0; // Example: return per 100g
    }
}
