package com.nutrisci.dao;

import com.nutrisci.info.NutrientInfo;
import java.sql.SQLException;

/**
 * Food nutrition data access.
 */
public interface NutritionDAO {
    //Gets nutrition info for food.
    NutrientInfo getNutrientInfo(String nutrientType) throws SQLException;
    
    //Gets total calories for food.

    double getTotalCalories(String nutrientType) throws SQLException;
    
    //Gets total grams for food.
    double getTotalGrams(String nutrientType) throws SQLException;

    //Gets calories per gram.
    double getCaloriesPerGram(String nutrientType) throws SQLException;
    
    // Helper method for defaults.
    double getDefaultCaloriesPerGram(String nutrientType);
}
