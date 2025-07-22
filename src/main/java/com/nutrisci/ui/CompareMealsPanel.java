/*
 * Decompiled with CFR 0.152.
 */
package com.nutrisci.ui;

import com.nutrisci.dao.DAOFactory;
import com.nutrisci.dao.MealDAO;
import com.nutrisci.dao.NutritionDAO;
import com.nutrisci.model.Meal;
import com.nutrisci.service.AnalysisModule;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

public class CompareMealsPanel
extends JPanel {
    private JComboBox<Meal> meal1ComboBox;
    private JComboBox<Meal> meal2ComboBox;
    private JTable comparisonTable;
    private JLabel calorieDeltaLabel;
    private JTextField profileIdField;

    private MealDAO mealDao;
    private AnalysisModule analysisModule;

    public CompareMealsPanel() {
        // Initialize DAOs and Services
        this.mealDao = DAOFactory.getMealDAO();
        NutritionDAO nutritionDao = DAOFactory.getNutritionDAO();
        this.analysisModule = new AnalysisModule(nutritionDao);

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Setup input panel
        setupInputPanel();

        // Setup comparison table
        setupComparisonTable();

        // Setup result panel
        setupResultPanel();
    }

    private void setupInputPanel() {
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        profileIdField = new JTextField(5);
        JButton loadButton = new JButton("Load Meals");
        meal1ComboBox = new JComboBox<>();
        meal2ComboBox = new JComboBox<>();
        JButton compareButton = new JButton("Compare");

        inputPanel.add(new JLabel("Profile ID:"));
        inputPanel.add(profileIdField);
        inputPanel.add(loadButton);
        inputPanel.add(new JSeparator(SwingConstants.VERTICAL));
        // More descriptive labels for clarity
        inputPanel.add(new JLabel("Compare Meal:"));
        inputPanel.add(meal1ComboBox);
        inputPanel.add(new JLabel("With Meal:"));
        inputPanel.add(meal2ComboBox);
        inputPanel.add(compareButton);

        add(inputPanel, BorderLayout.NORTH);

        // Action Listeners
        loadButton.addActionListener(e -> loadMeals());
        compareButton.addActionListener(e -> compareMeals());
    }

    private void setupComparisonTable() {
        comparisonTable = new JTable();
        comparisonTable.setFillsViewportHeight(true);
        comparisonTable.setRowHeight(25);
        comparisonTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        add(new JScrollPane(comparisonTable), BorderLayout.CENTER);
    }

    private void setupResultPanel() {
        JPanel resultPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        calorieDeltaLabel = new JLabel("Δ Calories: ---");
        calorieDeltaLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        resultPanel.add(calorieDeltaLabel);
        add(resultPanel, BorderLayout.SOUTH);
    }

    private void loadMeals() {
        try {
            int profileId = Integer.parseInt(profileIdField.getText());
            List<Meal> meals = mealDao.findAll().stream()
                .filter(meal -> meal.getProfileId() == profileId)
                .collect(java.util.stream.Collectors.toList());
            
            // Use a custom renderer to show meal details
            DefaultListCellRenderer renderer = new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof Meal) {
                        Meal meal = (Meal) value;
                        setText(String.format("%d: %s (%s)", meal.getId(), meal.getType(), meal.getLoggedAt().toLocalDate()));
                    }
                    return this;
                }
            };
            meal1ComboBox.setRenderer(renderer);
            meal2ComboBox.setRenderer(new DefaultListCellRenderer() { // Need a separate instance
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof Meal) {
                        Meal meal = (Meal) value;
                        setText(String.format("%d: %s (%s)", meal.getId(), meal.getType(), meal.getLoggedAt().toLocalDate()));
                    }
                    return this;
                }
            });

            meal1ComboBox.setModel(new DefaultComboBoxModel<>(meals.toArray(new Meal[0])));
            meal2ComboBox.setModel(new DefaultComboBoxModel<>(meals.toArray(new Meal[0])));

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Profile ID.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error loading meals: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void compareMeals() {
        Meal mealA = (Meal) meal1ComboBox.getSelectedItem();
        Meal mealB = (Meal) meal2ComboBox.getSelectedItem();

        if (mealA == null || mealB == null) {
            JOptionPane.showMessageDialog(this, "Please select two meals to compare.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Automatically determine which meal is the original and which is the swapped one
        // based on their creation timestamp.
        Meal originalMeal;
        Meal swappedMeal;
        if (mealA.getLoggedAt().isBefore(mealB.getLoggedAt())) {
            originalMeal = mealA;
            swappedMeal = mealB;
        } else {
            originalMeal = mealB;
            swappedMeal = mealA;
        }

        // Create table model with dynamic headers to identify the meals
        DefaultTableModel model = new DefaultTableModel(
            new String[]{"Ingredient", "Original: " + originalMeal.getType(), "Swapped: " + swappedMeal.getType()}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };

        // Get all unique ingredients from both meals
        Set<String> allIngredients = new TreeSet<>();
        allIngredients.addAll(originalMeal.getIngredients().keySet());
        allIngredients.addAll(swappedMeal.getIngredients().keySet());

        Map<String, Double> ingredientsOriginal = originalMeal.getIngredients();
        Map<String, Double> ingredientsSwapped = swappedMeal.getIngredients();

        for (String ingredient : allIngredients) {
            Vector<Object> row = new Vector<>();
            row.add(ingredient);
            row.add(String.format("%.1f g", ingredientsOriginal.getOrDefault(ingredient, 0.0)));
            row.add(String.format("%.1f g", ingredientsSwapped.getOrDefault(ingredient, 0.0)));
            model.addRow(row);
        }

        comparisonTable.setModel(model);

        // Highlight changed rows
        comparisonTable.setDefaultRenderer(Object.class, new DifferenceRenderer());

        // Calculate and display calorie delta with a more descriptive label
        try {
            double caloriesOriginal = analysisModule.computeTotalCalories(List.of(originalMeal));
            double caloriesSwapped = analysisModule.computeTotalCalories(List.of(swappedMeal));
            double delta = caloriesSwapped - caloriesOriginal;
            calorieDeltaLabel.setText(String.format("Δ Calories (Swapped - Original): %+.2f kcal", delta));
            calorieDeltaLabel.setForeground(delta >= 0 ? new Color(0, 150, 0) : Color.RED);
        } catch (SQLException ex) {
            calorieDeltaLabel.setText("Δ Calories: Error");
        }
    }

    public void setProfile(int profileId) {
        profileIdField.setText(String.valueOf(profileId));
        loadMeals();
    }

    // Custom renderer to highlight differences
    private static class DifferenceRenderer
    extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            // Comparison logic remains the same as it's based on column index
            Object val1 = table.getValueAt(row, 1);
            Object val2 = table.getValueAt(row, 2);

            if (!val1.equals(val2)) {
                c.setBackground(new Color(255, 255, 224)); // Light yellow for changed rows
                c.setFont(c.getFont().deriveFont(Font.BOLD));
            } else {
                c.setBackground(table.getBackground());
                c.setFont(c.getFont().deriveFont(Font.PLAIN));
            }
            return c;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            JFrame frame = new JFrame("NutriSci - Compare Meals");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            CompareMealsPanel panel = new CompareMealsPanel();
            
            // Pre-load profile 1 for demonstration
            panel.setProfile(1);
            
            frame.add(panel);
            frame.setSize(1000, 700);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
