/*
 * Decompiled with CFR 0.152.
 */
package com.nutrisci.service;

import com.nutrisci.dao.NutritionDAO;
import com.nutrisci.model.Meal;
import com.nutrisci.model.NutrientTotals;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class AnalysisModule {
    private final NutritionDAO nutritionDao;

    public AnalysisModule(NutritionDAO nutritionDao) {
        this.nutritionDao = nutritionDao;
    }

    public double computeTotalCalories(List<Meal> meals) throws SQLException {
        double total = 0.0;
        for (Meal m : meals) {
            for (Map.Entry<String, Double> ing : m.getIngredients().entrySet()) {
                double cpg = this.nutritionDao.getCaloriesPerGram(ing.getKey());
                total += cpg * ing.getValue();
            }
        }
        return total;
    }

    public Map<String, Double> computeCfgCompliance(int profileId, LocalDate date) throws SQLException {
        return Map.of("Vegetables and Fruits", 40.0, "Grain Products", 30.0, "Milk and Alternatives", 15.0, "Meat and Alternatives", 15.0);
    }

    public Map<String, NutrientTotals> computeSwapBeforeAfter(int n, LocalDate localDate) throws SQLException {
        throw new Error("Unresolved compilation problems: \n\tThe constructor NutrientTotals(int, int, int, int) is undefined\n\tThe constructor NutrientTotals(int, int, int, int) is undefined\n");
    }
}
