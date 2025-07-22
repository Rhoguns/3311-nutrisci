package com.nutrisci.app;

import com.nutrisci.controller.NutritionController;
import com.nutrisci.dao.MySQLNutritionDAO;
import java.util.Map;

public class CNFDemo {
    public static void main(String[] args) throws Exception {
        NutritionController ctrl = new NutritionController(new MySQLNutritionDAO());
        String[] foods = new String[]{"Apple", "Banana", "Bread", "Cauliflower"};
        System.out.println("=== Canada Nutrient File Demo ===\n");
        String[] stringArray = foods;
        int n = foods.length;
        int n2 = 0;
        while (n2 < n) {
            String food = stringArray[n2];
            double kcal = ctrl.getCaloriesPerGram(food);
            Map<String, Double> nut = ctrl.getNutrientBreakdown(food);
            System.out.printf("%-15s \u2192 %6.2f kcal/g, P:%6.3f, C:%6.3f, F:%6.3f, Fi:%6.3f%n", food, kcal, nut.getOrDefault("protein", 0.0), nut.getOrDefault("carbs", 0.0), nut.getOrDefault("fat", 0.0), nut.getOrDefault("fibre", 0.0));
            ++n2;
        }
    }
}
