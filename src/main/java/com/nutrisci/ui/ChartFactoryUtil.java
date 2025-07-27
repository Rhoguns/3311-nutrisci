package com.nutrisci.ui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

public class ChartFactoryUtil {

    public static JFreeChart createMacroPieChart(double protein, double carbs, double fat) {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        dataset.setValue("Protein", protein);
        dataset.setValue("Carbs",   carbs);
        dataset.setValue("Fat",     fat);
        return ChartFactory.createPieChart(
            "Macronutrient Breakdown", 
            dataset,                   
            true,                      
            true,                      
            false                      
        );
    }
}
