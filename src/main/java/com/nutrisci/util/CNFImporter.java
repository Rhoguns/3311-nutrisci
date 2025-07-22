package com.nutrisci.util;

import com.nutrisci.connector.DatabaseConnector;
import java.io.*;
import java.sql.*;

public class CNFImporter {
    public static void main(String[] args) {
        try {
            // Don't clear existing data since we have partial imports
            // clearExistingData();
            
            importFoodNames();
            // Skip nutrient amounts since we already have 290,000+ records
            // importNutrientAmounts(); 
            importFoodGroups();
            importNutrientNames();
            
            testImport();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void importNutrientAmounts() throws SQLException, IOException {
        String csvFile = "sql/NUTRIENT AMOUNT.csv";
        String sql = "INSERT IGNORE INTO cnf_nutrient_amount (FoodID, NutrientID, NutrientValue, StandardError, NumberOfObservations, NutrientSourceID) VALUES (?,?,?,?,?,?)";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            
            String line = br.readLine(); // Skip header
            int rowCount = 0;
            int duplicateCount = 0;
            
            while ((line = br.readLine()) != null) {
                String[] data = parseCSVLine(line);
                if (data.length >= 6) {
                    try {
                        ps.setInt(1, Integer.parseInt(data[0])); 
                        ps.setInt(2, Integer.parseInt(data[1])); 
                        ps.setBigDecimal(3, new java.math.BigDecimal(data[2])); 
                        ps.setBigDecimal(4, data[3].isEmpty() ? null : new java.math.BigDecimal(data[3])); 
                        ps.setInt(5, data[4].isEmpty() ? 0 : Integer.parseInt(data[4])); 
                        ps.setInt(6, Integer.parseInt(data[5])); 
                        
                        int result = ps.executeUpdate();
                        if (result == 0) {
                            duplicateCount++;
                        }
                        rowCount++;
                        
                        if (rowCount % 10000 == 0) {
                            System.out.println("Processed " + rowCount + " nutrient amount records... (" + duplicateCount + " duplicates skipped)");
                        }
                    } catch (SQLException e) {
                        // Log the problematic record and continue
                        System.err.println("Error inserting record at line " + rowCount + ": " + e.getMessage());
                        System.err.println("Data: " + String.join(",", data));
                    }
                }
            }
            System.out.println("Completed processing " + rowCount + " nutrient amount records (" + duplicateCount + " duplicates skipped)");
        }
    }
    
    private static void importFoodNames() throws SQLException, IOException {
        String csvFile = "sql/FOOD NAME.csv";
        // Use INSERT IGNORE to skip duplicates
        String sql = "INSERT IGNORE INTO cnf_food_name (FoodID, FoodCode, FoodGroupID, FoodSourceID, FoodDescription, FoodDescriptionF, DateOfEntry, DateOfPublication, CountryCode, ScientificName) VALUES (?,?,?,?,?,?,?,?,?,?)";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            
            String line = br.readLine(); // Skip header
            int rowCount = 0;
            while ((line = br.readLine()) != null) {
                String[] data = parseCSVLine(line);
                if (data.length >= 10) { // Fixed: Match actual CSV columns
                    ps.setInt(1, Integer.parseInt(data[0]));     // FoodID
                    ps.setInt(2, Integer.parseInt(data[1]));     // FoodCode  
                    ps.setInt(3, Integer.parseInt(data[2]));     // FoodGroupID
                    ps.setInt(4, Integer.parseInt(data[3]));     // FoodSourceID
                    ps.setString(5, data[4]);                    // FoodDescription
                    ps.setString(6, data[5]);                    // FoodDescriptionF
                    ps.setString(7, data[6]);                    // DateOfEntry
                    ps.setString(8, data[7]);                    // DateOfPublication
                    ps.setString(9, data[8]);                    // CountryCode
                    ps.setString(10, data[9]);                   // ScientificName
                    ps.executeUpdate();
                    
                    rowCount++;
                    if (rowCount % 1000 == 0) {
                        System.out.println("Imported " + rowCount + " food names...");
                    }
                }
            }
            System.out.println("Completed importing " + rowCount + " food names");
        }
    }

    private static void importFoodGroups() throws SQLException, IOException {
        String csvFile = "sql/FOOD GROUP.csv";
        String sql = "INSERT IGNORE INTO cnf_food_group (FoodGroupID, FoodGroupCode, FoodGroupName, FoodGroupNameF) VALUES (?,?,?,?)";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            
            String line = br.readLine(); 
            int rowCount = 0;
            int duplicateCount = 0;
            
            while ((line = br.readLine()) != null) {
                String[] data = parseCSVLine(line);
                if (data.length >= 4) {
                    try {
                        ps.setInt(1, Integer.parseInt(data[0])); 
                        ps.setString(2, data[1]); 
                        ps.setString(3, data[2]); 
                        ps.setString(4, data[3]); 
                        
                        int result = ps.executeUpdate();
                        if (result == 0) {
                            duplicateCount++;
                        }
                        rowCount++;
                    } catch (SQLException e) {
                        System.err.println("Error inserting food group record: " + e.getMessage());
                        System.err.println("Data: " + String.join(",", data));
                    }
                }
            }
            System.out.println("Completed processing " + rowCount + " food groups (" + duplicateCount + " duplicates skipped)");
        }
    }
    
    private static void importNutrientNames() throws SQLException, IOException {
        String csvFile = "sql/NUTRIENT NAME.csv";
        // Use INSERT IGNORE to skip duplicates
        String sql = "INSERT IGNORE INTO cnf_nutrient_name (NutrientID, NutrientCode, NutrientSymbol, NutrientUnit, NutrientName, NutrientNameF, Tagname, NutrientDecimals) VALUES (?,?,?,?,?,?,?,?)";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            
            String line = br.readLine(); // Skip header
            int rowCount = 0;
            while ((line = br.readLine()) != null) {
                String[] data = parseCSVLine(line);
                if (data.length >= 8) { // Fixed: Match actual CSV columns
                    ps.setInt(1, Integer.parseInt(data[0]));     // NutrientID
                    ps.setInt(2, Integer.parseInt(data[1]));     // NutrientCode
                    ps.setString(3, data[2]);                    // NutrientSymbol
                    ps.setString(4, data[3]);                    // NutrientUnit
                    ps.setString(5, data[4]);                    // NutrientName
                    ps.setString(6, data[5]);                    // NutrientNameF
                    ps.setString(7, data[6]);                    // Tagname
                    ps.setInt(8, data[7].isEmpty() ? 0 : Integer.parseInt(data[7])); // NutrientDecimals
                    ps.executeUpdate();
                    
                    rowCount++;
                }
            }
            System.out.println("Completed importing " + rowCount + " nutrient names");
        }
    }
    
    private static String[] parseCSVLine(String line) {
        
        return line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
    }
    
    private static void testImport() throws SQLException {
        try (Connection conn = DatabaseConnector.getConnection()) {
            
            PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM cnf_food_name");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println("Food Names imported: " + rs.getInt(1)); // Add output
            }
            
            ps = conn.prepareStatement("SELECT COUNT(*) FROM cnf_nutrient_amount");
            rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println("Nutrient Amounts imported: " + rs.getInt(1)); // Add output
            }
            
            ps = conn.prepareStatement("SELECT COUNT(*) FROM cnf_food_group");
            rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println("Food Groups imported: " + rs.getInt(1)); // Add output
            }
            
            ps = conn.prepareStatement("SELECT COUNT(*) FROM cnf_nutrient_name");
            rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println("Nutrient Names imported: " + rs.getInt(1)); // Add output
            }
            
            ps = conn.prepareStatement("SELECT FoodDescription FROM cnf_food_name WHERE FoodDescription LIKE '%apple%' LIMIT 1");
            rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println("Sample food: " + rs.getString(1)); // Add output
            }
            
            ps = conn.prepareStatement("SELECT FoodGroupName FROM cnf_food_group LIMIT 1");
            rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println("Sample food group: " + rs.getString(1)); // Add output
            }
            
            ps = conn.prepareStatement("SELECT NutrientName FROM cnf_nutrient_name WHERE NutrientSymbol = 'ENERC' LIMIT 1");
            rs = ps.executeQuery(); // Fix: remove duplicate assignment
            if (rs.next()) {
                System.out.println("Sample nutrient: " + rs.getString(1)); // Add output
            }
        }
    }
    
    private static void clearExistingData() throws SQLException {
        try (Connection conn = DatabaseConnector.getConnection()) {
            // Disable foreign key checks
            conn.createStatement().execute("SET FOREIGN_KEY_CHECKS=0");
            
            // Truncate tables
            conn.createStatement().execute("TRUNCATE TABLE cnf_food_name");
            conn.createStatement().execute("TRUNCATE TABLE cnf_nutrient_amount");
            conn.createStatement().execute("TRUNCATE TABLE cnf_food_group");
            conn.createStatement().execute("TRUNCATE TABLE cnf_nutrient_name");
            
            // Enable foreign key checks
            conn.createStatement().execute("SET FOREIGN_KEY_CHECKS=1");
        }
    }
}