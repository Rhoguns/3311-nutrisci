package com.nutrisci.app;
// NutriSci - Nutrition and Exercise Tracking Application

import com.nutrisci.model.Meal;
import com.nutrisci.dao.MySQLNutritionDAO;
import com.nutrisci.dao.NutritionDAO;
import com.nutrisci.service.AnalysisModule;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MealMain {
    public static void main(String[] args) {
        // Start of the main execution block, wrapped in a try-catch for error handling.
        try {
            // Create a Meal object with type "Lunch" and the current timestamp.
            Meal meal = new Meal("Lunch", LocalDateTime.now());

            // Add ingredients to the meal with their respective quantities in grams.
            meal.getIngredients().put("Egg",   100.0);
            meal.getIngredients().put("Bread", 50.0); 

            // Print a header for the meal summary.
            System.out.println("=== Meal Summary ===");
            // Display the type of the meal.
            System.out.println("Type: " + meal.getType());
            // Define a date-time formatter for consistent output.
            // Example: "yyyy-MM-dd HH:mm" will format as "2023-10-27 14:30".
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            // Display the timestamp when the meal was logged, formatted.
            System.out.println("Logged at: " + meal.getLoggedAt().format(fmt));

            // Print a header for the ingredients list.
            System.out.println("Ingredients:");
            // Iterate through the meal's ingredients map.
            // For each entry, print the ingredient name and its quantity in grams.
            meal.getIngredients().forEach((name, qty) ->
                System.out.printf("  - %s: %.1fg%n", name, qty)
            );

            // Initialize a NutritionDAO to access nutrition data, using MySQL as the backend.
            // This DAO provides methods to retrieve nutritional information for food items.
            NutritionDAO nutritionDao = new MySQLNutritionDAO();
            // Create an AnalysisModule, which uses the NutritionDAO to perform calculations.
            // The AnalysisModule encapsulates the logic for nutritional analysis,
            // such as calculating total calories or nutrient breakdowns.
            AnalysisModule analysis = new AnalysisModule(nutritionDao);
            // Compute the total calories for the meal.
            // The meal is wrapped in a List as computeTotalCalories expects a list of meals.
            // This allows the method to be flexible and calculate calories for multiple meals if needed.
            double totalCalories = analysis.computeTotalCalories(List.of(meal));
            // Print the calculated total calories, formatted to two decimal places.
            System.out.printf("Total Calories: %.2f kcal%n", totalCalories);

        } catch (Exception e) {
            // Catch any exceptions that occur during the execution.
            System.err.println("Error in MealMain: " + e.getMessage());
            // Print the stack trace for debugging purposes.
            e.printStackTrace();
        }
    }
}