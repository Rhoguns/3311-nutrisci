package main.java.com.nutrisci.app;
// NutriSci - Nutrition and Exercise Tracking Application

import main.java.com.nutrisci.model.Profile;

public class ProfileMain {
    public static void main(String[] args) {
        Profile p = new Profile("Phil", "Male", 25, 180.0, 75.0, "metric");
        System.out.println(p);
    }
}
