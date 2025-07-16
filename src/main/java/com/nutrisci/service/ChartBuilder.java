package com.nutrisci.service;

import com.nutrisci.model.Meal;
import java.util.List;

/**
 * The `ChartBuilder` class is responsible for generating various types of charts
 * or summaries based on nutritional data, such as meal information.
 * It acts as a utility for visualizing data.
 * Stub for building chart data.
 */
public class ChartBuilder {

    /**
     * Example: build a simple text-based summary chart.
     */
    /**
     * Builds a text-based summary chart for a given list of meals.
     * This method iterates through the provided meals and formats their
     * logging time, type, and the number of ingredients into a readable string.
     * @param meals A list of `Meal` objects to be summarized.
     * @return A `String` representing the text-based summary chart of the meals.
     */
    public String buildMealSummaryChart(List<Meal> meals) {
        StringBuilder sb = new StringBuilder();
        sb.append("Meal Summary:\n");
        for (Meal m : meals) {
            sb.append(m.getLoggedAt()).append(" → ").append(m.getType())
              .append(" (").append(m.getIngredients().size()).append(" items)\n");
        }
        return sb.toString();
    }
}