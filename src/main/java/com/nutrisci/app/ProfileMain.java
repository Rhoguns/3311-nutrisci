package main.java.com.nutrisci.app;
// NutriSci - Nutrition and Exercise Tracking Application

import main.java.com.nutrisci.model.Profile;

/**
 * A simple console application entry point that demonstrates
 * creating and printing a Profile object.
 */
public class ProfileMain {
    /**
     * Creates a sample Profile and prints its details (including BMI).
     *
     * @param args command-line arguments (ignored)
     */
    public static void main(String[] args) {
        Profile p = new Profile("Phil", "Male", 25, 180.0, 75.0, "metric");
        System.out.println(p);
    }
}