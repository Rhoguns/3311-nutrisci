
package com.nutrisci.app;

import com.nutrisci.dao.MySQLNutritionDAO;
import com.nutrisci.model.Meal;
import com.nutrisci.service.AnalysisModule;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MealMain {
    public static void main(String[] args) {
        try {
            Meal meal = new Meal("Lunch", LocalDateTime.now());
            meal.getIngredients().put("Egg", 100.0);
            meal.getIngredients().put("Bread", 50.0);
            System.out.println("=== Meal Summary ===");
            System.out.println("Type: " + meal.getType());
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            System.out.println("Logged at: " + meal.getLoggedAt().format(fmt));
            System.out.println("Ingredients:");
            meal.getIngredients().forEach((name, qty) -> {
                PrintStream printStream = System.out.printf("  - %s: %.1fg%n", name, qty);
            });
            MySQLNutritionDAO nutritionDao = new MySQLNutritionDAO();
            AnalysisModule analysis = new AnalysisModule(nutritionDao);
            double totalCalories = analysis.computeTotalCalories(List.of(meal));
            System.out.printf("Total Calories: %.2f kcal%n", totalCalories);
        }
        catch (Exception e) {
            System.err.println("Error in MealMain: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
