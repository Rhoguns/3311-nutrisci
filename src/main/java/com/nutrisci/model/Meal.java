package main.java.com.nutrisci.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a meal with its type, date, and ingredients.
 * Provides methods to add ingredients and calculate total calories.
 */
public class Meal {
    private String type;
    private Date date;
    private Map<String, Double> ingredients = new HashMap<>();

    public Meal(String type, Date date) {
        this.type = type;
        this.date = date;
    }

    public void addIngredient(String name, double grams) {
        ingredients.put(name, grams);
    }

    public double getTotalCalories() {
        return ingredients.entrySet().stream()
            .mapToDouble(e -> e.getValue() * getCaloriesPerGram(e.getKey()))
            .sum();
    }

    private double getCaloriesPerGram(String name) {
        switch (name.toLowerCase()) {
            case "egg":    return 1.55;
            case "bread":  return 2.5;
            case "tomato": return 0.18;
            default:       return 1.0;
        }
    }

    public String summary() {
        StringBuilder sb = new StringBuilder();
        sb.append("Meal: ").append(type).append(" | Date: ").append(date).append("\n");
        ingredients.forEach((k, v) -> sb.append("- ").append(k).append(": ").append(v).append("g\n"));
        sb.append(String.format("Total Calories: %.2f kcal", getTotalCalories()));
        return sb.toString();
    }
}
