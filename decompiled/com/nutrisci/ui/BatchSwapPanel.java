/*
 * Decompiled with CFR 0.152.
 */
package com.nutrisci.ui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class BatchSwapPanel
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
            JFrame frame = new JFrame("NutriSci - Batch Apply Swaps");
            frame.setDefaultCloseOperation(3);
            BatchSwapPanel panel = new BatchSwapPanel();
            panel.setProfile(1);
            frame.add(panel);
            frame.setSize(700, 500);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
