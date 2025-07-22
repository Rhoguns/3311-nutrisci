/*
 * Decompiled with CFR 0.152.
 */
package com.nutrisci.service;

import com.nutrisci.model.Meal;
import java.util.List;

public class ChartBuilder {
    public String buildMealSummaryChart(List<Meal> meals) {
        StringBuilder sb = new StringBuilder();
        sb.append("Meal Summary:\n");
        for (Meal m : meals) {
            sb.append(m.getLoggedAt()).append(" \u2192 ").append(m.getType()).append(" (").append(m.getIngredients().size()).append(" items)\n");
        }
        return sb.toString();
    }
}
