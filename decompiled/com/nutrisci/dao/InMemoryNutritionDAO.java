/*
 * Decompiled with CFR 0.152.
 */
package com.nutrisci.dao;

import com.nutrisci.dao.NutritionDAO;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class InMemoryNutritionDAO
implements NutritionDAO {
    private final Map<String, Double> caloriesMap = Map.of("Apple", 0.5, "Banana", 0.89);
    private final Map<String, Map<String, Double>> breakdownMap = new HashMap<String, Map<String, Double>>();

    public InMemoryNutritionDAO() {
        HashMap<String, Double> appleNutrients = new HashMap<String, Double>();
        appleNutrients.put("protein", 0.003);
        appleNutrients.put("carbs", 0.14);
        appleNutrients.put("fat", 0.001);
        appleNutrients.put("fibre", 0.02);
        this.breakdownMap.put("Apple", appleNutrients);
        HashMap<String, Double> bananaNutrients = new HashMap<String, Double>();
        bananaNutrients.put("protein", 0.011);
        bananaNutrients.put("carbs", 0.23);
        bananaNutrients.put("fat", 0.003);
        bananaNutrients.put("fibre", 0.025);
        this.breakdownMap.put("Banana", bananaNutrients);
    }

    @Override
    public double getCaloriesPerGram(String foodName) throws SQLException {
        return this.caloriesMap.getOrDefault(foodName, 0.0);
    }

    @Override
    public Map<String, Double> getNutrientBreakdown(String foodName) throws SQLException {
        return this.breakdownMap.getOrDefault(foodName, Map.of());
    }
}
