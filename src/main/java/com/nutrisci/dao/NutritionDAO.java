package com.nutrisci.dao;

import com.nutrisci.info.NutrientInfo;
import java.sql.SQLException;

/**
 * Food nutrition data access.
 */
public interface NutritionDAO {
    /**
     * Gets calories per gram.
     */
    @Deprecated
    double getCaloriesPerGram(String foodName) throws SQLException;

    /**
     * Gets nutrition info for food.
     */
    NutrientInfo getNutrientInfo(String foodName) throws SQLException;
}
