package com.nutrisci.view;

import com.nutrisci.controller.NutritionController;
import com.nutrisci.dao.MySQLNutritionDAO;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.util.Map;

public class ChartDemo extends JFrame {
    public ChartDemo(String title) {
        super(title);

        // Create the dataset
        // Use the real CNF-backed nutrition DAO for calorie calculations
        var nutritionDao = new MySQLNutritionDAO();
        // Create a NutritionController to interact with nutrition data.
        var nutritionCtrl = new NutritionController(nutritionDao);
        // Define an array of food items for which to display calorie information.
        String[] foods = { "Apple", "Banana", "Bread", "Cauliflower" };

        DefaultPieDataset dataset = new DefaultPieDataset();
        // Iterate through each food item to retrieve its calorie information and add it to the dataset.
        for (String food : foods) {
            double kcal = 0.0;
            try {
                kcal = nutritionCtrl.getCaloriesPerGram(food);
            } catch (Exception e) {
                e.printStackTrace();
            }
            dataset.setValue(food, kcal);
        }

        // Create the pie chart
        JFreeChart chart = ChartFactory.createPieChart(
            "Calories per gram (CNF)",   // chart title
            dataset,                     // data
            true,                        // include legend
            true,
            false
        );

        // Wrap it in a panel and put it on the frame
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));
        setContentPane(chartPanel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ChartDemo demo = new ChartDemo("NutriSci: Calorie Breakdown");
            demo.pack();
            demo.setLocationRelativeTo(null);
            demo.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            demo.setVisible(true);
        });
    }
}
