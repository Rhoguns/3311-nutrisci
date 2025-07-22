package com.nutrisci.ui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import java.time.LocalDate;
import java.util.Map;

public class ChartFactoryUtil {

    public static JFreeChart createMacroPieChart(double protein, double carbs, double fat) {
        DefaultPieDataset dataset = new DefaultPieDataset();
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
