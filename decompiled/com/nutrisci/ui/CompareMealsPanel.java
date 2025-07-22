/*
 * Decompiled with CFR 0.152.
 */
package com.nutrisci.ui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class CompareMealsPanel
extends JPanel {
    public void setProfile(int profileId) {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            JFrame frame = new JFrame("NutriSci - Compare Meals");
            frame.setDefaultCloseOperation(3);
            CompareMealsPanel panel = new CompareMealsPanel();
            panel.setProfile(1);
            frame.add(panel);
            frame.setSize(1000, 700);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
