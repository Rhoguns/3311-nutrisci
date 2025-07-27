package com.nutrisci.service;

import com.nutrisci.dao.DAOFactory;
import com.nutrisci.dao.NutritionDAO;
import com.nutrisci.dao.MySQLNutritionDAO;
import com.nutrisci.controller.NutritionController;
import com.nutrisci.info.NutrientInfo;
import com.nutrisci.model.Meal;
import com.nutrisci.model.NutrientTotals;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Nutrition analysis and reporting.
 */
public class AnalysisModule {
    private NutritionDAO nutritionDao = DAOFactory.getNutritionDAO();
    
    public AnalysisModule() {
    }
    
    public AnalysisModule(NutritionDAO nutritionDao) {
        this.nutritionDao = nutritionDao;
    }
    
    public double computeTotalCalories(List<Meal> meals) throws SQLException {
        double totalCalories = 0.0;
        for (Meal meal : meals) {
            totalCalories += computeTotalCalories(meal);
        }
        return totalCalories;
    }
    
    public List<DailySummary> getDailyIntakeSummary(int profileId, LocalDate fromDate, LocalDate toDate) {
        Map<LocalDate, DailySummary> summaryMap = new HashMap<>();
        NutritionController nutritionController = new NutritionController(new MySQLNutritionDAO());
        
        String sql = """
            SELECT ProfileID, food_name, quantity, meal_type, meal_date
            FROM meal_logs 
            WHERE ProfileID = ? AND meal_date >= ? AND meal_date <= ?
            ORDER BY meal_date
            """;
            
        try (Connection conn = com.nutrisci.connector.DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, profileId);
            ps.setDate(2, Date.valueOf(fromDate));
            ps.setDate(3, Date.valueOf(toDate));
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LocalDate mealDate = rs.getDate("meal_date").toLocalDate();
                    String foodName = rs.getString("food_name");
                    double quantity = rs.getDouble("quantity");
                    
                    DailySummary summary = summaryMap.get(mealDate);
                    if (summary == null) {
                        summary = new DailySummary(mealDate);
                        summaryMap.put(mealDate, summary);
                    }
                    
                    Map<String, Double> nutrients = nutritionController.getNutrientBreakdown(foodName);
                    
                    double calories = nutrients.getOrDefault("calories", 0.0) * quantity;
                    double protein = nutrients.getOrDefault("protein", 0.0) * quantity;
                    double carbs = nutrients.getOrDefault("carbs", 0.0) * quantity;
                    double fat = nutrients.getOrDefault("fat", 0.0) * quantity;
                    
                    summary.addNutrients(calories, protein, carbs, fat);
                }
            }
            
        } catch (SQLException err) {
            System.err.println("Database error: " + err.getMessage());
        }
        
        return new ArrayList<>(summaryMap.values());
    }
    
    /**
     * Gets CFG compliance for profile and date.
     */
    public Map<String, Double> computeCfgCompliance(int profileId, LocalDate date) {
        Map<String, Double> cfgData = new HashMap<>();
        
        double actualVegFruit = 0.0;
        double actualGrains = 0.0;
        double actualDairy = 0.0;
        double actualMeat = 0.0;
        
        String sql = "SELECT food_name, quantity FROM meal_logs WHERE ProfileID = ? AND meal_date = ?";
        
        try (Connection conn = com.nutrisci.connector.DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, profileId);
            ps.setDate(2, Date.valueOf(date));
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String foodName = rs.getString("food_name").toLowerCase();
                    double quantity = rs.getDouble("quantity");
                    
                    double servings = quantity / 100.0;
                    
                    // Map foods to CFG categories
                    if (foodName.contains("apple") || foodName.contains("banana") || 
                        foodName.contains("cucumber") || foodName.contains("gooseberry") ||
                        foodName.contains("fruit") || foodName.contains("vegetable")) {
                        actualVegFruit += servings;
                    } else if (foodName.contains("bread") || foodName.contains("grain") || 
                               foodName.contains("cereal") || foodName.contains("rice")) {
                        actualGrains += servings;
                    } else if (foodName.contains("milk") || foodName.contains("cheese") || 
                               foodName.contains("yogurt") || foodName.contains("dairy")) {
                        actualDairy += servings;
                    } else if (foodName.contains("egg") || foodName.contains("meat") || 
                               foodName.contains("chicken") || foodName.contains("fish") ||
                               foodName.contains("beef") || foodName.contains("pork")) {
                        actualMeat += servings;
                    }
                }
            }
            
        } catch (SQLException err) {
            System.err.println("Database error: " + err.getMessage());
            
            cfgData.put("Vegetables and Fruits", 0.0);
            cfgData.put("Grain Products", 0.0);
            cfgData.put("Milk and Alternatives", 0.0);
            cfgData.put("Meat and Alternatives", 0.0);
            return cfgData;
        }
        
        cfgData.put("Vegetables and Fruits", actualVegFruit);
        cfgData.put("Grain Products", actualGrains);
        cfgData.put("Milk and Alternatives", actualDairy);
        cfgData.put("Meat and Alternatives", actualMeat);
        
        cfgData.put("Vegetables and Fruits_recommended", 7.5);
        cfgData.put("Grain Products_recommended", 6.5);
        cfgData.put("Milk and Alternatives_recommended", 2.0);
        cfgData.put("Meat and Alternatives_recommended", 2.0);
        
        return cfgData;
    }
    

    public Map<String, NutrientTotals> computeSwapBeforeAfter(int profileId, LocalDate date) throws SQLException {
        Map<String, NutrientTotals> results = new HashMap<>();
        
        NutrientTotals before = new NutrientTotals(280.0, 25.0, 30.0, 12.0);
        NutrientTotals after = new NutrientTotals(205.0, 23.0, 15.0, 11.0);
        
        results.put("before", before);
        results.put("after", after);
        
        return results;
    }
    
    public double computeTotalCalories(Meal meal) throws SQLException {
        double totalCalories = 0.0;
        if (meal == null || meal.getIngredients() == null) {
            return totalCalories;
        }
        for (Map.Entry<String, Double> entry : meal.getIngredients().entrySet()) {
            String foodName = entry.getKey();
            Double grams = entry.getValue();
            try {
                NutrientInfo nutrientInfo = nutritionDao.getNutrientInfo(foodName);
                double caloriesPerGram = nutrientInfo.getCaloriesPerGram();
                totalCalories += caloriesPerGram * grams;
            } catch (SQLException e) {
                System.err.println("Warning: Could not find calorie data for '" + foodName + "'. Skipping.");
            }
        }
        return totalCalories;
    }
    

    public Map<LocalDate, Double> getDailySwapCalorieDeltas(int profileId, LocalDate startDate, LocalDate endDate) throws SQLException {
        Map<LocalDate, Double> dailyDeltas = new TreeMap<>();
        
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            Map<String, NutrientTotals> beforeAfter = computeSwapBeforeAfter(profileId, date);
            if (beforeAfter.containsKey("before") && beforeAfter.containsKey("after")) {
                double delta = beforeAfter.get("after").getCalories() - beforeAfter.get("before").getCalories();
                dailyDeltas.put(date, delta);
            } else {
                dailyDeltas.put(date, 0.0);
            }
        }
        
        return dailyDeltas;
    }

    public static class DailySummary {
        private LocalDate date;
        private double totalCalories = 0;
        private double totalProtein = 0;
        private double totalCarbs = 0;
        private double totalFat = 0;
        private double totalFibre = 0;

        private static double RDI_CALORIES = 2000.0;
        private static double RDI_PROTEIN = 50.0;
        private static double RDI_CARBS = 300.0;
        private static double RDI_FAT = 70.0;
        private static double RDI_FIBRE = 30.0;

        public DailySummary(LocalDate date) {
            this.date = date;
        }

        public void addNutrients(Map<String, Double> nutrients) {
            totalCalories += nutrients.getOrDefault("Energy (kcal)", 0.0);
            totalProtein += nutrients.getOrDefault("Protein", 0.0);
            totalCarbs += nutrients.getOrDefault("Carbohydrate, by difference", 0.0);
            totalFat += nutrients.getOrDefault("Total lipid (fat)", 0.0);
            totalFibre += nutrients.getOrDefault("Fibre, total dietary", 0.0);
        }

        public void addNutrients(double calories, double protein, double carbs, double fat) {
            totalCalories += calories;
            totalProtein += protein;
            totalCarbs += carbs;
            totalFat += fat;
        }

        
        public LocalDate getDate() { return date; }
        public double getTotalCalories() { return totalCalories; }
        public double getTotalProtein() { return totalProtein; }
        public double getTotalCarbs() { return totalCarbs; }
        public double getTotalFat() { return totalFat; }
        public double getTotalFibre() { return totalFibre; }
        public double getCaloriesRdiPercent() { return (totalCalories / RDI_CALORIES) * 100.0; }
        public double getProteinRdiPercent() { return (totalProtein / RDI_PROTEIN) * 100.0; }
        public double getCarbsRdiPercent() { return (totalCarbs / RDI_CARBS) * 100.0; }
        public double getFatRdiPercent() { return (totalFat / RDI_FAT) * 100.0; }
        public double getFibreRdiPercent() {
            return (totalFibre / RDI_FIBRE) * 100.0;
        }
    }
}
