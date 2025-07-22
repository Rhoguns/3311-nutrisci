/*
 * Decompiled with CFR 0.152.
 */
package com.nutrisci.dao;

import com.nutrisci.dao.NutritionDAO;
import java.sql.SQLException;
import java.util.Map;

public class MySQLNutritionDAO
implements NutritionDAO {
    @Override
    public double getCaloriesPerGram(String foodName) throws SQLException {
        throw new IllegalStateException("Decompilation failed");
    }

    @Override
    public Map<String, Double> getNutrientBreakdown(String foodName) throws SQLException {
        throw new IllegalStateException("Decompilation failed");
    }
}
