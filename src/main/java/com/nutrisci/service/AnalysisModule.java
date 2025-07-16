package com.nutrisci.service;

import com.nutrisci.model.Meal;
import com.nutrisci.dao.NutritionDAO;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class AnalysisModule {
    // Data Access Object for retrieving nutrition information.
    private final NutritionDAO nutritionDao;

    /**
     * Constructs an AnalysisModule with the specified NutritionDAO.
     *
     * @param nutritionDao The NutritionDAO implementation to be used for
     *                     accessing nutrition data.
     */
    public AnalysisModule(NutritionDAO nutritionDao) {
        this.nutritionDao = nutritionDao;
    }

    /**
     * Compute total calories for a list of meals.
     */
    public double computeTotalCalories(List<Meal> meals) throws SQLException {
        double total = 0.0;
        for (Meal m : meals) {
            for (Map.Entry<String, Double> ing : m.getIngredients().entrySet()) {
                double cpg = nutritionDao.getCaloriesPerGram(ing.getKey());
                total += cpg * ing.getValue();
            }
        }
        return total;
    }
}
