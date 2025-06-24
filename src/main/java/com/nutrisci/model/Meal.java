package main.java.com.nutrisci.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a meal with a type, date, and a list of ingredients.
 * Provides methods to add ingredients and calculate the total calories.
 */
public class Meal {
    /** The meal type (e.g., "Breakfast", "Lunch", "Dinner", "Snack"). */
    private String type;

    /** The date and time when the meal was logged. */
    private Date date;

    /** Maps ingredient names to their quantities (in grams). */
    private Map<String, Double> ingredients = new HashMap<>();

    /**
     * Constructs a new Meal.
     *
     * @param type the meal type
     * @param date the date and time of the meal
     */
    public Meal(String type, Date date) {
        this.type = type;
        this.date = date;
    }

    /**
     * Adds or updates an ingredient quantity for this meal.
     *
     * @param name  the ingredient name
     * @param grams the amount in grams
     */
    public void addIngredient(String name, double grams) {
        ingredients.put(name, grams);
    }

    /**
     * Calculates the total calories in this meal by summing each
     * ingredient’s calories-per-gram × its quantity.
     *
     * @return the total calorie count
     */
    public double getTotalCalories() {
        return ingredients.entrySet().stream()
            .mapToDouble(e -> e.getValue() * getCaloriesPerGram(e.getKey()))
            .sum();
    }

    /**
     * Returns the calories-per-gram for a given ingredient.
     * Falls back to a default rate if the ingredient is unrecognized.
     *
     * @param name the ingredient name
     * @return calories per gram
     */
    private double getCaloriesPerGram(String name) {
        switch (name.toLowerCase()) {
            case "egg":    return 1.55;
            case "bread":  return 2.5;
            case "tomato": return 0.18;
            default:       return 1.0;
        }
    }

    /**
     * Builds a multi-line summary of this meal, listing each
     * ingredient and the total calories.
     *
     * @return a formatted summary string
     */
    public String summary() {
        StringBuilder sb = new StringBuilder();
        sb.append("Meal: ").append(type)
          .append(" | Date: ").append(date).append("\n");
        ingredients.forEach((k, v) ->
            sb.append("- ").append(k).append(": ").append(v).append("g\n")
        );
        sb.append(String.format("Total Calories: %.2f kcal", getTotalCalories()));
        return sb.toString();
    }
}
