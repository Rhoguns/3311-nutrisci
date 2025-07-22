/*
 * Decompiled with CFR 0.152.
 */
package com.nutrisci.ui;

import com.nutrisci.dao.DAOFactory;
import com.nutrisci.dao.NutritionDAO;
import com.nutrisci.model.Meal;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class ComparisonPanel
extends JPanel {
    private final Meal meal1;
    private final Meal meal2;
    private final NutritionDAO nutritionDao = DAOFactory.getNutritionDAO();
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ComparisonPanel(Meal meal1, Meal meal2) {
        this.meal1 = meal1;
        this.meal2 = meal2;
        this.initComponents();
    }

    private void initComponents() {
        this.setLayout(new BorderLayout(10, 10));
        this.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JPanel headerPanel = this.createMealHeaders();
        this.add((Component)headerPanel, "North");
        JPanel comparisonPanel = this.createComparisonTable();
        this.add((Component)comparisonPanel, "Center");
        JPanel summaryPanel = this.createSummaryPanel();
        this.add((Component)summaryPanel, "South");
    }

    private JPanel createMealHeaders() {
        JPanel headerPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        JPanel meal1Header = new JPanel(new BorderLayout());
        meal1Header.setBorder(new TitledBorder("Meal #1"));
        meal1Header.setBackground(new Color(240, 248, 255));
        JLabel meal1Info = new JLabel(String.format("<html><div style='text-align: center;'><b>%s</b><br/>ID: %d<br/>Logged: %s</div></html>", this.meal1.getType(), this.meal1.getId(), this.meal1.getLoggedAt().format(this.dtf)));
        meal1Info.setHorizontalAlignment(0);
        meal1Header.add((Component)meal1Info, "Center");
        JPanel meal2Header = new JPanel(new BorderLayout());
        meal2Header.setBorder(new TitledBorder("Meal #2"));
        meal2Header.setBackground(new Color(240, 255, 240));
        JLabel meal2Info = new JLabel(String.format("<html><div style='text-align: center;'><b>%s</b><br/>ID: %d<br/>Logged: %s</div></html>", this.meal2.getType(), this.meal2.getId(), this.meal2.getLoggedAt().format(this.dtf)));
        meal2Info.setHorizontalAlignment(0);
        meal2Header.add((Component)meal2Info, "Center");
        headerPanel.add(meal1Header);
        headerPanel.add(meal2Header);
        return headerPanel;
    }

    private JPanel createComparisonTable() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Ingredient Comparison"));
        Object[] columns = new String[]{"Ingredient", "Meal 1 (g)", "Meal 2 (g)", "Difference (g)", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0){

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        LinkedHashSet<String> allIngredients = new LinkedHashSet<String>();
        allIngredients.addAll(this.meal1.getIngredients().keySet());
        allIngredients.addAll(this.meal2.getIngredients().keySet());
        for (String ingredient : allIngredients) {
            double amount1 = this.meal1.getIngredients().getOrDefault(ingredient, 0.0);
            double amount2 = this.meal2.getIngredients().getOrDefault(ingredient, 0.0);
            double difference = amount2 - amount1;
            String status = amount1 == 0.0 ? "Added in Meal 2" : (amount2 == 0.0 ? "Removed from Meal 1" : (difference > 0.0 ? "Increased" : (difference < 0.0 ? "Decreased" : "Same")));
            model.addRow(new Object[]{ingredient, amount1 == 0.0 ? "\u2014" : String.format("%.1f", amount1), amount2 == 0.0 ? "\u2014" : String.format("%.1f", amount2), difference == 0.0 ? "\u2014" : String.format("%+.1f", difference), status});
        }
        JTable table = new JTable(model);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                block18: {
                    String status;
                    super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    if (isSelected) break block18;
                    String string = status = (String)table.getValueAt(row, 4);
                    int n = -1;
                    switch (status.hashCode()) {
                        case -1965173936: {
                            if (string.equals("Added in Meal 2")) {
                                n = 1;
                            }
                            break;
                        }
                        case -1689080986: {
                            if (string.equals("Decreased")) {
                                n = 2;
                            }
                            break;
                        }
                        case 67393674: {
                            if (string.equals("Removed from Meal 1")) {
                                n = 3;
                            }
                            break;
                        }
                        case 663972418: {
                            if (string.equals("Increased")) {
                                n = 4;
                            }
                            break;
                        }
                    }
                    switch (n) {
                        case 1: {
                            this.setBackground(new Color(220, 255, 220));
                            break;
                        }
                        case 3: {
                            this.setBackground(new Color(255, 220, 220));
                            break;
                        }
                        case 4: {
                            this.setBackground(new Color(255, 255, 200));
                            break;
                        }
                        case 2: {
                            this.setBackground(new Color(200, 220, 255));
                            break;
                        }
                        default: {
                            this.setBackground(Color.WHITE);
                        }
                    }
                }
                if (column >= 1 && column <= 3) {
                    this.setHorizontalAlignment(0);
                } else {
                    this.setHorizontalAlignment(2);
                }
                return this;
            }
        });
        table.setRowHeight(28);
        table.getColumnModel().getColumn(0).setPreferredWidth(150);
        table.getColumnModel().getColumn(1).setPreferredWidth(80);
        table.getColumnModel().getColumn(2).setPreferredWidth(80);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(120);
        table.setGridColor(Color.LIGHT_GRAY);
        table.setShowGrid(true);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(600, 200));
        panel.add((Component)scrollPane, "Center");
        return panel;
    }

    private JPanel createSummaryPanel() {
        JPanel summaryPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        summaryPanel.setBorder(new TitledBorder("Nutritional Summary"));
        try {
            double calories1 = this.calculateTotalCalories(this.meal1);
            double calories2 = this.calculateTotalCalories(this.meal2);
            double calorieDiff = calories2 - calories1;
            JPanel meal1Summary = new JPanel(new GridLayout(3, 1, 5, 5));
            meal1Summary.setBorder(BorderFactory.createLineBorder(new Color(100, 149, 237), 2));
            meal1Summary.setBackground(new Color(240, 248, 255));
            meal1Summary.add(new JLabel("  Total Calories: " + String.format("%.1f kcal", calories1), 2));
            meal1Summary.add(new JLabel("  Ingredients: " + this.meal1.getIngredients().size(), 2));
            meal1Summary.add(new JLabel("  Total Weight: " + String.format("%.1f g", this.getTotalWeight(this.meal1)), 2));
            JPanel meal2Summary = new JPanel(new GridLayout(3, 1, 5, 5));
            meal2Summary.setBorder(BorderFactory.createLineBorder(new Color(34, 139, 34), 2));
            meal2Summary.setBackground(new Color(240, 255, 240));
            meal2Summary.add(new JLabel("  Total Calories: " + String.format("%.1f kcal", calories2), 2));
            meal2Summary.add(new JLabel("  Ingredients: " + this.meal2.getIngredients().size(), 2));
            meal2Summary.add(new JLabel("  Total Weight: " + String.format("%.1f g", this.getTotalWeight(this.meal2)), 2));
            summaryPanel.add(meal1Summary);
            summaryPanel.add(meal2Summary);
            JPanel diffPanel = new JPanel(new FlowLayout());
            JLabel diffLabel = new JLabel(String.format("\u0394 Calories: %+.1f kcal", calorieDiff));
            diffLabel.setFont(diffLabel.getFont().deriveFont(1, 14.0f));
            if (calorieDiff > 0.0) {
                diffLabel.setForeground(new Color(220, 20, 60));
            } else if (calorieDiff < 0.0) {
                diffLabel.setForeground(new Color(34, 139, 34));
            } else {
                diffLabel.setForeground(Color.BLACK);
            }
            diffPanel.add(diffLabel);
            JPanel bottomPanel = new JPanel(new BorderLayout());
            bottomPanel.add((Component)summaryPanel, "Center");
            bottomPanel.add((Component)diffPanel, "South");
            return bottomPanel;
        }
        catch (SQLException e) {
            JLabel errorLabel = new JLabel("Error calculating nutritional summary: " + e.getMessage());
            errorLabel.setForeground(Color.RED);
            JPanel errorPanel = new JPanel(new FlowLayout());
            errorPanel.add(errorLabel);
            return errorPanel;
        }
    }

    private double calculateTotalCalories(Meal meal) throws SQLException {
        double total = 0.0;
        for (Map.Entry<String, Double> entry : meal.getIngredients().entrySet()) {
            double caloriesPerGram = this.nutritionDao.getCaloriesPerGram(entry.getKey());
            total += caloriesPerGram * entry.getValue();
        }
        return total;
    }

    private double getTotalWeight(Meal meal) {
        return meal.getIngredients().values().stream().mapToDouble(Double::doubleValue).sum();
    }
}
