package com.nutrisci.util;

import com.nutrisci.dao.MySQLNutritionDAO;
import com.nutrisci.dao.NutritionDAO;
import com.nutrisci.info.NutrientInfo;

public class NutritionTest {
    public static void main(String[] args) {
        try {
            NutritionDAO nutritionDAO = new MySQLNutritionDAO();
            
            String testFood = "apple";
            System.out.println("Testing nutrition lookup for: " + testFood);
            
            double calories = nutritionDAO.getNutrientInfo(testFood).getCaloriesPerGram();
            System.out.println("Calories per gram: " + calories);
            
            NutrientInfo nutrients = nutritionDAO.getNutrientInfo(testFood);
            System.out.println("Nutrient info retrieved: " + nutrients);
            
            System.out.println("Nutrition DAO is working!");
            
        } catch (Exception err) {
            System.err.println("Nutrition test failed: " + err.getMessage());
        }
    }
}