/*
 * Decompiled with CFR 0.152.
 */
package com.nutrisci.dao;

import java.sql.SQLException;
import java.util.Map;

public interface NutritionDAO {
    public double getCaloriesPerGram(String var1) throws SQLException;

    public Map<String, Double> getNutrientBreakdown(String var1) throws SQLException;
}
