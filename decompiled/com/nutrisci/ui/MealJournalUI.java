/*
 * Decompiled with CFR 0.152.
 */
package com.nutrisci.ui;

import com.nutrisci.dao.MealDAO;
import com.nutrisci.dao.SwapRuleDAO;
import com.nutrisci.logic.SwapEngine;
import com.nutrisci.model.Meal;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

public class MealJournalUI
extends JPanel {
    private JLabel statusLabel;
    private JComboBox<Meal> cbMeals;
    private JComboBox<String> cbGoal;
    private JTextField tfProfileId;
    private MealDAO mealDao;
    private SwapRuleDAO swapRuleDao;
    private SwapEngine swapEngine;

    private void applySingleSwap(String string, double d, String string2, Meal meal, String string3) {
        throw new Error("Unresolved compilation problems: \n\tThe method insert(SwapRule) is undefined for the type SwapRuleDAO\n\tThe method setMealId(int) is undefined for the type AppliedSwap\n\tThe method setAppliedAt(LocalDateTime) is undefined for the type AppliedSwap\n\tThe method onLoadMeals(null) is undefined for the type MealJournalUI\n");
    }

    private void suggestAndPersistSwap(String string, double d, String string2, Meal meal) {
        throw new Error("Unresolved compilation problems: \n\tThe method suggestMultipleSwaps(String, String, int) is undefined for the type SwapEngine\n\tThe method formatSuggestionClean(String) is undefined for the type MealJournalUI\n");
    }

    public void setProfile(int n) {
        throw new Error("Unresolved compilation problem: \n\tThe method findByProfileId(int) is undefined for the type MealDAO\n");
    }

    private String getSelectedFood() {
        Meal selectedMeal = (Meal)this.cbMeals.getSelectedItem();
        if (selectedMeal == null) {
            return null;
        }
        if (selectedMeal.getIngredients().isEmpty()) {
            return null;
        }
        String firstIngredient = selectedMeal.getIngredients().keySet().iterator().next();
        return firstIngredient;
    }

    private String getSelectedGoal() {
        return (String)this.cbGoal.getSelectedItem();
    }

    private void setupMealRenderer() {
        this.cbMeals.setRenderer(new BasicComboBoxRenderer(){

            @Override
            public Component getListCellRendererComponent(JList<?> jList, Object object, int n, boolean bl, boolean bl2) {
                throw new Error("Unresolved compilation problem: \n\tdtf cannot be resolved to a variable\n");
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            try {
                MealJournalUI ui = new MealJournalUI();
                ui.setProfile(1);
                JFrame frame = new JFrame("NutriSci - Meal Journal & Food Swaps");
                frame.setDefaultCloseOperation(3);
                frame.setResizable(true);
                frame.add(ui);
                frame.pack();
                frame.setMinimumSize(new Dimension(650, 500));
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
            catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error starting application: " + e.getMessage(), "Startup Error", 0);
            }
        });
    }
}
