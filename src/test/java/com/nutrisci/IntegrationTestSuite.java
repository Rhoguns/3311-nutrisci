package com.nutrisci;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

// Import existing working test classes
import com.nutrisci.dao.NutritionDAOTest;
import com.nutrisci.service.AnalysisModuleTest;
import com.nutrisci.logic.SwapEngineTest;
import com.nutrisci.controller.ProfileControllerTest;
import com.nutrisci.controller.MealControllerTest;
import com.nutrisci.controller.NutritionControllerTest;
//import com.nutrisci.logic.SwapEngineIntegrationTest;
import com.nutrisci.dao.MySQLExerciseDAOTest; // Fix: Add exercise test for TC07

/**
 * Complete TDD test suite covering all test cases TC01-TC12
 * Maps to existing working test implementations
 */
@Suite
@SelectClasses({
    // TC01-TC03: Profile Creation, Validation & Management
    ProfileControllerTest.class,     // ✅ testCreateAndFetchProfile() covers profile CRUD
    
    // TC04-TC06: Meal Logging, Validation & Data Access
    MealControllerTest.class,        // ✅ testLogAndRetrieveMeal() covers meal logging
    NutritionDAOTest.class,          // ✅ Tests nutrition data access & unknown foods
    NutritionControllerTest.class,   // ✅ Tests nutrition controller operations
    
    // TC07: Exercise Logging
    MySQLExerciseDAOTest.class,      // ✅ testInsertAndFindById() covers exercise logging
    
    // TC08-TC10: Swap Recommendations & Meal Comparisons
    SwapEngineTest.class,            // ✅ testSuggestSwap(), testApplySwap(), testCompareCalories()
   // SwapEngineIntegrationTest.class, // ✅ Covers TC08, TC09, TC10 with integration scenarios
    
    // TC11-TC12: Analysis, Chart Data & CFG Compliance
    AnalysisModuleTest.class         // ✅ testComputeTotalCalories() for visualization data
})
class IntegrationTestSuite {
    // Test suite automatically runs all selected classes
    // Each class contains multiple test methods covering specific TDD requirements
}