/*
 * Decompiled with CFR 0.152.
 */
package com.nutrisci.controller;

import com.nutrisci.dao.NutritionDAO;
import java.sql.SQLException;
import java.util.Map;

public class NutritionController {
    private final NutritionDAO dao;

    public NutritionController(NutritionDAO dao) {
        this.dao = dao;
    }

    public double getCaloriesPerGram(String foodName) throws SQLException {
        return this.dao.getCaloriesPerGram(foodName);
    }

    public Map<String, Double> getNutrientBreakdown(String foodName) throws SQLException {
        return this.dao.getNutrientBreakdown(foodName);
    }
}
