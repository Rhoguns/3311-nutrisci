
package com.nutrisci.dao;

import com.nutrisci.dao.ExerciseDAO;
import com.nutrisci.dao.InMemoryExerciseDAO;
import com.nutrisci.dao.MealDAO;
import com.nutrisci.dao.MySQLMealDAO;
import com.nutrisci.dao.MySQLNutritionDAO;
import com.nutrisci.dao.NutritionDAO;
import com.nutrisci.dao.ProfileDAO;
import com.nutrisci.dao.SwapRuleDAO;
import com.nutrisci.model.SwapRule;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DAOFactory {
    public static MealDAO getMealDAO() {
        return new MySQLMealDAO();
    }

    public static NutritionDAO getNutritionDAO() {
        return new MySQLNutritionDAO();
    }

    public static ProfileDAO getProfileDAO() {
        return new MySQLProfileDAO();
    }

    public static SwapRuleDAO getSwapRuleDAO() {
        return new MySQLSwapRuleDAO();
    }

    public static ExerciseDAO getExerciseDAO() {
        return new InMemoryExerciseDAO();
    }

    private DAOFactory() {
    }
}
