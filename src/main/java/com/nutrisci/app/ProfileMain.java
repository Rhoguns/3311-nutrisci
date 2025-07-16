package com.nutrisci.app;
// NutriSci - Nutrition and Exercise Tracking Application
// This class demonstrates the creation and management of user profiles within the NutriSci application.

import com.nutrisci.controller.ProfileController;
import com.nutrisci.dao.ProfileDAO;
import com.nutrisci.model.Profile;

import java.time.LocalDate;
import java.time.Period;

public class ProfileMain {
    public static void main(String[] args) {
        // Initialize the ProfileController with a new ProfileDAO.
        // The ProfileDAO is responsible for interacting with the database to persist and retrieve profile data.
        ProfileController controller = new ProfileController(new ProfileDAO());

        // Create a new Profile object with sample data.
        // This demonstrates how a user's personal information can be captured.
        Profile p = new Profile(
            "Alice", // Name of the profile owner
            "F", // Sex (e.g., "M" for Male, "F" for Female)
            LocalDate.of(1990, 5, 20), // Date of birth (Year, Month, Day)
            165.0, // Height in centimeters
            60.0, // Weight in kilograms
            "metric" // Preferred unit system (e.g., "metric" or "imperial")
        );

        // Attempt to persist the newly created profile to the database.
        // The createProfile method in the controller handles the database insertion
        // and, if successful, sets the ID of the profile object.
        try {
            controller.createProfile(p);
        } catch (Exception e) {
            // If an error occurs during profile saving, print an error message
            // and terminate the program gracefully.
            System.err.println("Error saving profile: " + e.getMessage());
            return;
        }

        // Calculate the age of the profile owner.
        // Period.between calculates the duration between two dates.
        int age = Period.between(p.getDateOfBirth(), LocalDate.now()).getYears();
        // Convert height from centimeters to meters for BMI calculation.
        double heightM = p.getHeightCm() / 100.0;
        // Calculate the Body Mass Index (BMI) using the formula: weight (kg) / (height (m))^2.
        double bmi = p.getWeightKg() / (heightM * heightM);

        // Print a summary of the saved profile and calculated metrics.
        System.out.println("Profile saved with ID: " + p.getId());
        System.out.println("Name: " + p.getName());
        System.out.println("Age: " + age + " years");
        System.out.printf("BMI: %.2f%n", bmi);
    }
}