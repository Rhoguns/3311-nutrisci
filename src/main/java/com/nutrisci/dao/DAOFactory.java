package com.nutrisci.dao;

public class DAOFactory {
    public static MealDAO getMealDAO() {
        return new MySQLMealDAO();
    }

    public static NutritionDAO getNutritionDAO() {
        return new MySQLNutritionDAO();
    }

    public static ProfileDAO getProfileDAO() {
        return new ProfileDAOImpl();  
    }

    public static ExerciseDAO getExerciseDAO() {
        return new MySQLExerciseDAO();  
    }

    public static SwapRuleDAO getSwapRuleDAO() {
        return new MySQLSwapRuleDAO();
    }
    public static AppliedSwapDAO getAppliedSwapDAO() {
        return new MySQLAppliedSwapDAO();
    }
}
