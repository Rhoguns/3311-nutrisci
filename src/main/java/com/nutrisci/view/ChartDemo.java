package main.java.com.nutrisci.view;
// NutriSci - Nutrition and Exercise Tracking Application

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.JFrame;

public class ChartDemo {
    public static void main(String[] args) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Protein", 25);
        dataset.setValue("Carbs",   50);
        dataset.setValue("Fats",    20);
        dataset.setValue("Other",    5);

        JFreeChart chart = ChartFactory.createPieChart(
            "Nutrient Breakdown", dataset, true, true, false
        );

        JFrame frame = new JFrame("Chart Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new ChartPanel(chart));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
