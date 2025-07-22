/*
 * Decompiled with CFR 0.152.
 */
package com.nutrisci.logic;

import com.nutrisci.dao.NutritionDAO;
import com.nutrisci.model.Meal;
import com.nutrisci.service.AnalysisModule;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SwapEngine {
    private final AnalysisModule analysisModule;
    private final NutritionDAO nutritionDao;
    private static final Map<String, Map<String, String>> SWAP_OPTIONS = Map.of("reduce calories", Map.of("Bread", "Lettuce wrap", "White Rice", "Cauliflower rice"), "increase fiber", Map.of("White Bread", "Whole wheat bread", "Rice", "Brown rice"));

    public SwapEngine(AnalysisModule analysisModule, NutritionDAO nutritionDao) {
        this.analysisModule = analysisModule;
        this.nutritionDao = nutritionDao;
    }

    public String suggestSwap(String food, String goal) {
        // This code only checks a hardcoded map, it never uses the database!
        return SWAP_OPTIONS.getOrDefault(goal.toLowerCase(), Collections.emptyMap()).getOrDefault(food, "No suggestion available");
    }

    public double compareCalories(Meal original, Meal swapped) throws SQLException {
        double origCal = this.analysisModule.computeTotalCalories(List.of(original));
        double swapCal = this.analysisModule.computeTotalCalories(List.of(swapped));
        return swapCal - origCal;
    }

    public Meal applySwap(Meal original, String oldFood, String newFood, double newQty) {
        Meal copy = new Meal(original.getType(), original.getLoggedAt());
        copy.setProfileId(original.getProfileId());
        original.getIngredients().forEach((name, qty) -> {
            if (!name.equals(oldFood)) {
                copy.getIngredients().put((String)name, (Double)qty);
            }
        });
        copy.getIngredients().put(newFood, newQty);
        return copy;
    }
}
