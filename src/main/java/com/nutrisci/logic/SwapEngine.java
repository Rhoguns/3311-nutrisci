package main.java.com.nutrisci.logic;
// NutriSci - Nutrition and Exercise Tracking Application

public class SwapEngine {
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

