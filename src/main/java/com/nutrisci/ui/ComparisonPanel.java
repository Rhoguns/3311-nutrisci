package com.nutrisci.ui;

import com.nutrisci.model.Meal;
import com.nutrisci.dao.NutritionDAO;
import com.nutrisci.dao.DAOFactory;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;


public class ComparisonPanel extends JPanel {
    private final Meal meal1;
    private final Meal meal2;
    private final NutritionDAO nutritionDao = DAOFactory.getNutritionDAO();
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ComparisonPanel(Meal meal1, Meal meal2) {
        this.meal1 = meal1;
        this.meal2 = meal2;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        
        JPanel headerPanel = createMealHeaders();
        add(headerPanel, BorderLayout.NORTH);

        
        JPanel comparisonPanel = createComparisonTable();
        add(comparisonPanel, BorderLayout.CENTER);

        
        JPanel summaryPanel = createSummaryPanel();
        add(summaryPanel, BorderLayout.SOUTH);
    }

    private JPanel createMealHeaders() {
        JPanel headerPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        
        
        JPanel meal1Header = new JPanel(new BorderLayout());
        meal1Header.setBorder(new TitledBorder("Meal #1"));
        meal1Header.setBackground(new Color(240, 248, 255)); 
        
        JLabel meal1Info = new JLabel(String.format(
            "<html><div style='text-align: center;'>" +
            "<b>%s</b><br/>" +
            "ID: %d<br/>" +
            "Logged: %s" +
            "</div></html>",
            meal1.getType(), meal1.getId(), meal1.getLoggedAt().format(dtf)
        ));
        meal1Info.setHorizontalAlignment(SwingConstants.CENTER);
        meal1Header.add(meal1Info, BorderLayout.CENTER);
        
        
        JPanel meal2Header = new JPanel(new BorderLayout());
        meal2Header.setBorder(new TitledBorder("Meal #2"));
        meal2Header.setBackground(new Color(240, 255, 240)); 
        
        JLabel meal2Info = new JLabel(String.format(
            "<html><div style='text-align: center;'>" +
            "<b>%s</b><br/>" +
            "ID: %d<br/>" +
            "Logged: %s" +
            "</div></html>",
            meal2.getType(), meal2.getId(), meal2.getLoggedAt().format(dtf)
        ));
        meal2Info.setHorizontalAlignment(SwingConstants.CENTER);
        meal2Header.add(meal2Info, BorderLayout.CENTER);
        
        headerPanel.add(meal1Header);
        headerPanel.add(meal2Header);
        
        return headerPanel;
    }

    private JPanel createComparisonTable() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Ingredient Comparison"));

        
        String[] columns = {"Ingredient", "Meal 1 (g)", "Meal 2 (g)", "Difference (g)", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };

        
        Set<String> allIngredients = new LinkedHashSet<>();
        allIngredients.addAll(meal1.getIngredients().keySet());
        allIngredients.addAll(meal2.getIngredients().keySet());

        
        for (String ingredient : allIngredients) {
            double amount1 = meal1.getIngredients().getOrDefault(ingredient, 0.0);
            double amount2 = meal2.getIngredients().getOrDefault(ingredient, 0.0);
            double difference = amount2 - amount1;
            
            String status;
            if (amount1 == 0) {
                status = "Added in Meal 2";
            } else if (amount2 == 0) {
                status = "Removed from Meal 1";
            } else if (difference > 0) {
                status = "Increased";
            } else if (difference < 0) {
                status = "Decreased";
            } else {
                status = "Same";
            }

            model.addRow(new Object[]{
                ingredient,
                amount1 == 0 ? "—" : String.format("%.1f", amount1),
                amount2 == 0 ? "—" : String.format("%.1f", amount2),
                difference == 0 ? "—" : String.format("%+.1f", difference),
                status
            });
        }

        JTable table = new JTable(model);
        
        
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    String status = (String) table.getValueAt(row, 4); 
                    switch (status) {
                        case "Added in Meal 2":
                            setBackground(new Color(220, 255, 220)); 
                            break;
                        case "Removed from Meal 1":
                            setBackground(new Color(255, 220, 220)); 
                            break;
                        case "Increased":
                            setBackground(new Color(255, 255, 200)); 
                            break;
                        case "Decreased":
                            setBackground(new Color(200, 220, 255)); 
                            break;
                        default:
                            setBackground(Color.WHITE);
                    }
                }
                
                
                if (column >= 1 && column <= 3) {
                    setHorizontalAlignment(SwingConstants.CENTER);
                } else {
                    setHorizontalAlignment(SwingConstants.LEFT);
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
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSummaryPanel() {
        JPanel summaryPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        summaryPanel.setBorder(new TitledBorder("Nutritional Summary"));

        try {
            
            double calories1 = calculateTotalCalories(meal1);
            double calories2 = calculateTotalCalories(meal2);
            double calorieDiff = calories2 - calories1;

            
            JPanel meal1Summary = new JPanel(new GridLayout(3, 1, 5, 5));
            meal1Summary.setBorder(BorderFactory.createLineBorder(new Color(100, 149, 237), 2));
            meal1Summary.setBackground(new Color(240, 248, 255));
            
            meal1Summary.add(new JLabel("  Total Calories: " + String.format("%.1f kcal", calories1), SwingConstants.LEFT));
            meal1Summary.add(new JLabel("  Ingredients: " + meal1.getIngredients().size(), SwingConstants.LEFT));
            meal1Summary.add(new JLabel("  Total Weight: " + String.format("%.1f g", getTotalWeight(meal1)), SwingConstants.LEFT));

            
            JPanel meal2Summary = new JPanel(new GridLayout(3, 1, 5, 5));
            meal2Summary.setBorder(BorderFactory.createLineBorder(new Color(34, 139, 34), 2));
            meal2Summary.setBackground(new Color(240, 255, 240));
            
            meal2Summary.add(new JLabel("  Total Calories: " + String.format("%.1f kcal", calories2), SwingConstants.LEFT));
            meal2Summary.add(new JLabel("  Ingredients: " + meal2.getIngredients().size(), SwingConstants.LEFT));
            meal2Summary.add(new JLabel("  Total Weight: " + String.format("%.1f g", getTotalWeight(meal2)), SwingConstants.LEFT));

            summaryPanel.add(meal1Summary);
            summaryPanel.add(meal2Summary);

            
            JPanel diffPanel = new JPanel(new FlowLayout());
            JLabel diffLabel = new JLabel(String.format("Δ Calories: %+.1f kcal", calorieDiff));
            diffLabel.setFont(diffLabel.getFont().deriveFont(Font.BOLD, 14f));
            
            if (calorieDiff > 0) {
                diffLabel.setForeground(new Color(220, 20, 60)); 
            } else if (calorieDiff < 0) {
                diffLabel.setForeground(new Color(34, 139, 34)); 
            } else {
                diffLabel.setForeground(Color.BLACK);
            }
            
            diffPanel.add(diffLabel);
            
            JPanel bottomPanel = new JPanel(new BorderLayout());
            bottomPanel.add(summaryPanel, BorderLayout.CENTER);
            bottomPanel.add(diffPanel, BorderLayout.SOUTH);
            
            return bottomPanel;

        } catch (SQLException e) {
            JLabel errorLabel = new JLabel("Error calculating nutritional summary: " + e.getMessage());
            errorLabel.setForeground(Color.RED);
            JPanel errorPanel = new JPanel(new FlowLayout());
            errorPanel.add(errorLabel);
            return errorPanel;
        }
    }

    private double calculateTotalCalories(Meal meal) throws SQLException {
        double total = 0;
        for (Map.Entry<String, Double> entry : meal.getIngredients().entrySet()) {
            double caloriesPerGram = nutritionDao.getCaloriesPerGram(entry.getKey());
            total += caloriesPerGram * entry.getValue();
        }
        return total;
    }

    private double getTotalWeight(Meal meal) {
        return meal.getIngredients().values().stream().mapToDouble(Double::doubleValue).sum();
    }

    private String getTooltipForFood(String foodName) {
        try {
            double caloriesPerGram = this.nutritionDao.getCaloriesPerGram(foodName);
            return String.format("%.2f kcal/g", caloriesPerGram);
        } catch (SQLException e) {
            return "No data";
        }
    }

    /**
     * Main method to run this panel as a standalone application for testing.
     */
    public static void main(String[] args) {
        // Create sample meals for demonstration purposes
        Meal meal1 = new Meal("Original Lunch", LocalDateTime.now().minusHours(1));
        meal1.setId(101);
        meal1.getIngredients().put("Chicken breast", 150.0);
        meal1.getIngredients().put("White Rice", 200.0);
        meal1.getIngredients().put("Broccoli", 100.0);

        Meal meal2 = new Meal("Swapped Lunch", LocalDateTime.now());
        meal2.setId(102);
        meal2.getIngredients().put("Chicken breast", 150.0);
        meal2.getIngredients().put("Cauliflower rice", 200.0); // Swapped ingredient
        meal2.getIngredients().put("Broccoli", 100.0);
        meal2.getIngredients().put("Olive oil", 10.0);      // Added ingredient

        // Run the UI creation on the Event Dispatch Thread for thread safety
        SwingUtilities.invokeLater(() -> {
            try {
                // Use a modern look and feel
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            JFrame frame = new JFrame("Meal Comparison Tool");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Create an instance of the panel with the sample meals
            ComparisonPanel panel = new ComparisonPanel(meal1, meal2);
            frame.add(panel);

            frame.pack(); // Automatically size the window to fit the components
            frame.setLocationRelativeTo(null); // Center the window on the screen
            frame.setVisible(true);
        });
    }
}
