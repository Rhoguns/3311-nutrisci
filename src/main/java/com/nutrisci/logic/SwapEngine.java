package main.java.com.nutrisci.logic;
// NutriSci - Nutrition and Exercise Tracking Application

/**
 * Provides suggestions for food swaps based on a given food item and a nutritional goal.
 */
public class SwapEngine {
    /**
     * Suggests a food swap to help achieve a specific nutritional goal.
     * @param food The original food item to be swapped.
     * @param goal The nutritional goal (e.g., "reduce calories", "increase fiber").
     * @return A suggested food item for the swap, or "no suggestion" if no suitable swap is found.
     */
    public static String suggestSwap(String food, String goal) {
        String f = food.toLowerCase();
        if (goal.equalsIgnoreCase("reduce calories")) {
            switch (f) {
                case "bread": return "lettuce wrap";
                case "beef":  return "tofu";
                default:      return "no suggestion";
            }
        }
        if (goal.equalsIgnoreCase("increase fiber")) {
            switch (f) {
                case "white rice": return "brown rice";
                case "white bread":return "whole wheat bread";
                default:           return "no suggestion";
            }
        }
        return "no suggestion";
    }
}
