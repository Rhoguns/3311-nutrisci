package main.java.com.nutrisci.ui;


import main.java.com.nutrisci.model.Meal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

/**
 * A Swing UI for logging meals. 
 * <p>
 * Allows the user to enter a meal type, ingredients, and quantities,
 * then displays a running list and total calories.
 */
public class MealLoggerUI extends JFrame {
    /** Text field for the meal type (e.g., Breakfast, Lunch). */
    private JTextField typeField;

    /** Text field for the ingredient name. */
    private JTextField ingredientField;

    /** Text field for the ingredient quantity (in grams). */
    private JTextField qtyField;

    /** Button to add the current ingredient to the meal. */
    private JButton addBtn;

    /** Button to save the meal and display total calories. */
    private JButton saveBtn;

    /** Text area to log ingredients and totals. */
    private JTextArea logArea;

    /** The current Meal object being built. */
    private Meal current;

    /**
     * Constructs the MealLoggerUI, laying out fields, buttons, and the log area.
     */
    public MealLoggerUI() {
        super("Log Meal");
        setLayout(new BorderLayout());

        // Top panel with entry fields
        JPanel top = new JPanel(new GridLayout(3, 2));
        top.add(new JLabel("Type:"));
        typeField = new JTextField();
        top.add(typeField);

        top.add(new JLabel("Ingredient:"));
        ingredientField = new JTextField();
        top.add(ingredientField);

        top.add(new JLabel("Qty (g):"));
        qtyField = new JTextField();
        top.add(qtyField);

        add(top, BorderLayout.NORTH);

        // Center log area
        logArea = new JTextArea(10, 30);
        add(new JScrollPane(logArea), BorderLayout.CENTER);

        // Bottom panel with buttons
        JPanel btnp = new JPanel();
        addBtn = new JButton("Add Ingredient");
        saveBtn = new JButton("Save Meal");
        btnp.add(addBtn);
        btnp.add(saveBtn);
        add(btnp, BorderLayout.SOUTH);

        // Register listeners
        addBtn.addActionListener(new AddListener());
        saveBtn.addActionListener(new SaveListener());

        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Listener for the “Add Ingredient” button.
     * Creates a Meal if needed, adds the ingredient, and logs it.
     */
    private class AddListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (current == null) {
                current = new Meal(typeField.getText(), new Date());
            }
            String ing = ingredientField.getText();
            double qty = Double.parseDouble(qtyField.getText());
            current.addIngredient(ing, qty);
            logArea.append(ing + ": " + qty + "g\n");
            ingredientField.setText("");
            qtyField.setText("");
        }
    }

    /**
     * Listener for the “Save Meal” button.
     * Calculates and logs the total calories, then clears the current meal.
     */
    private class SaveListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (current != null) {
                logArea.append("TOTAL: " + current.getTotalCalories() + " kcal\n");
                current = null;
            }
        }
    }

    /**
     * Launches the Meal Logger UI.
     *
     * @param args ignored
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MealLoggerUI ui = new MealLoggerUI();
            ui.setLocationRelativeTo(null);
            ui.setVisible(true);
        });
    }
}