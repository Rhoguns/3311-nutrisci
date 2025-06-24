package test.java.com.nutrisci.test;
// NutriSci - Nutrition and Exercise Tracking Application

import main.java.com.nutrisci.model.Profile;
import main.java.com.nutrisci.model.Meal;
import main.java.com.nutrisci.model.Exercise;
import main.java.com.nutrisci.logic.SwapEngine;

import java.util.Date;

public class TestModules {
    public static void main(String[] args) {
        System.out.println("=== Profile Test ===");
        Profile p = new Profile("TestUser", "Female", 30, 165.0, 65.0, "metric");
        System.out.println(p + "\n");

        System.out.println("=== Meal Test ===");
        Meal m = new Meal("Dinner", new Date());
        m.addIngredient("Egg", 80);
        m.addIngredient("Bread", 50);
        System.out.println(m.summary() + "\n");

        System.out.println("=== Exercise Test ===");
        Exercise e = new Exercise("Running", new Date(), 30);
        System.out.println(e.summary() + "\n");

        System.out.println("=== SwapEngine Test ===");
        System.out.println(
            "Bread → reduce calories: " +
            SwapEngine.suggestSwap("Bread", "reduce calories")
        );
        System.out.println(
            "White Rice → increase fiber: " +
            SwapEngine.suggestSwap("White Rice", "increase fiber")
        );
    }
}