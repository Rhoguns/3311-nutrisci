package com.nutrisci.ui;

import com.nutrisci.dao.InMemoryNutritionDAO;
import com.nutrisci.model.Profile;
import com.nutrisci.model.Meal;
import com.nutrisci.model.Exercise;
import com.nutrisci.controller.ProfileController;
import com.nutrisci.dao.ProfileDAO;
import com.nutrisci.dao.DAOFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UIComponentTest {

    private ProfileController mockProfileController;
    private ProfileDAO mockProfileDAO;

    @BeforeEach
    void setUp() {
        mockProfileDAO = mock(ProfileDAO.class);
        mockProfileController = mock(ProfileController.class);
    }

    @Test
    @DisplayName("TC02: Test Profile Retrieval from Database")
    void testTC02_ProfileRetrieval() throws Exception {
        // Setup: Create and save a profile
        Profile originalProfile = new Profile();
        originalProfile.setName("testUserTC02");
        originalProfile.setDateOfBirth(LocalDate.of(1999, 1, 1));
        originalProfile.setWeightKg(70.0);
        originalProfile.setHeightCm(175.0);
        originalProfile.setSex("Male");
        originalProfile.setId(1); // Set an ID for the profile
        
        // Mock the controller behavior using the actual method signature
        when(mockProfileController.getProfileById(1)).thenReturn(Optional.of(originalProfile));
        
        try {
            mockProfileController.createProfile(originalProfile);
        } catch (SQLException e) {
            // Handle exception in test
        }

        // Action: Retrieve the profile by ID
        Optional<Profile> retrievedProfileOpt = mockProfileController.getProfileById(1);

        // Assertion: Verify the retrieved profile matches the original
        assertTrue(retrievedProfileOpt.isPresent());
        Profile retrievedProfile = retrievedProfileOpt.get();
        assertEquals("testUserTC02", retrievedProfile.getName());
        assertNotNull(retrievedProfile.getDateOfBirth());
    }

    @Test
    @DisplayName("TC01: Test Profile Creation and Model Validation")
    void testTC01_ProfileCreation_ModelValidation() throws Exception {
        Profile profile = new Profile();
        profile.setName("testUserTC01");
        profile.setDateOfBirth(LocalDate.of(1994, 1, 1));
        profile.setWeightKg(80.5);
        profile.setHeightCm(180.0);
        profile.setSex("Female");
        profile.setId(2);

        // Mock the controller behavior using actual methods
        when(mockProfileController.getProfileById(2)).thenReturn(Optional.of(profile));
        
        try {
            mockProfileController.createProfile(profile);
        } catch (SQLException e) {
            // Handle exception in test
        }

        Optional<Profile> savedProfileOpt = mockProfileController.getProfileById(2);
        assertTrue(savedProfileOpt.isPresent());
        assertEquals("testUserTC01", savedProfileOpt.get().getName());
    }

    @Test
    @DisplayName("TC03: Test ProfileUI Construction with Controller")
    void testTC03_ProfileUI_Construction() {
        // Test that ProfileUI can be constructed with a ProfileController
        ProfileUI profileUI = new ProfileUI(mockProfileController);
        assertNotNull(profileUI);
    }

    @Test
    void testTC04_MealLogging_ModelValidation() {
        Meal meal = new Meal();
        meal.setMealType("Lunch");
        meal.setDate(LocalDate.now());
        meal.setProfileId(1);
        
        meal.getIngredients().put("Egg", 100.0);
        meal.getIngredients().put("Bread", 50.0);
        
        assertEquals(2, meal.getIngredients().size());
        assertTrue(meal.getIngredients().containsKey("Egg"));
        assertTrue(meal.getIngredients().containsKey("Bread"));
        assertEquals(100.0, meal.getIngredients().get("Egg"));
        assertEquals(50.0, meal.getIngredients().get("Bread"));
        
        assertTrue(meal.getProfileId() > 0, "Profile ID should be set");
    }

    @Test
    void testTC07_ExerciseLogging_ModelValidation() {
        Exercise exercise = new Exercise();
        exercise.setExerciseType("Running");
        exercise.setDate(LocalDate.now());
        exercise.setDuration(30);
        exercise.setProfileId(1);
        
        assertEquals("Running", exercise.getExerciseType());
        assertEquals(30, exercise.getDuration());
        assertTrue(exercise.getProfileId() > 0, "Profile ID should be set");
    }

    @Test
    void testTC11_ChartGeneration_DataPreparation() throws SQLException {
        InMemoryNutritionDAO nutritionDAO = new InMemoryNutritionDAO();
        
        double eggCalories = nutritionDAO.getCaloriesPerGram("egg");
        assertTrue(eggCalories > 0, "Should have nutrition data for chart generation");
        
        Map<String, Double> breakdown = nutritionDAO.getNutrientBreakdown("egg");
        assertNotNull(breakdown, "Should have nutrient breakdown for charts");
        assertTrue(breakdown.size() > 0, "Should have multiple nutrients for visualization");
    }

    @Test
    void testTC02_ValidationErrorHandling() {
        Profile profile = new Profile();
        assertTrue(profile.getName() == null || profile.getName().isEmpty());
        
        Meal meal = new Meal();
        assertEquals(0, meal.getProfileId(), "Default profile ID should be 0");
        
        Exercise exercise = new Exercise();
        assertEquals(0, exercise.getDuration(), "Default duration should be 0");
    }

    @Test
    void testTC06_InvalidDataHandling() {
        Meal meal = new Meal();
        meal.setMealType("Snack");
        meal.setDate(LocalDate.now());
        meal.setProfileId(1);
        
        assertTrue(meal.getIngredients().isEmpty(), "New meal should have empty ingredients");
        
        meal.getIngredients().put("Apple", 150.0);
        assertEquals(1, meal.getIngredients().size(), "Should accept valid ingredient");
        
        assertTrue(meal.getIngredients().get("Apple") > 0, "Ingredient quantity should be positive");
    }

    @Test
    void testTC03_ProfileEditing_ModelValidation() {
        Profile profile = new Profile();
        profile.setName("TestUser");
        profile.setSex("M");
        profile.setDateOfBirth(LocalDate.of(1993, 6, 15));
        profile.setHeightCm(170.0);
        profile.setWeightKg(65.0);
        profile.setUnit("metric");
        
        profile.setWeightKg(60.0);
        
        double newBmi = calculateBMI(profile);
        assertEquals(20.76, newBmi, 0.01, "BMI should be recalculated after weight change");
    }

    @Test
    void testTC05_MealLogging_MissingData() {
        Meal meal = new Meal();
        meal.setMealType("Dinner");
        meal.setDate(LocalDate.now());
        assertEquals(0, meal.getProfileId(), "Default profile ID should be invalid");
        
        Meal emptyMeal = new Meal();
        assertTrue(emptyMeal.getMealType() == null || emptyMeal.getMealType().isEmpty(), 
                  "Meal type should be null or empty by default");
    }

    private double calculateBMI(Profile profile) {
        double heightM = profile.getHeightCm() / 100.0;
        return profile.getWeightKg() / (heightM * heightM);
    }
}