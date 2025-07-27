package com.nutrisci.util;

import com.nutrisci.ui.MainDashboard;
import javax.swing.SwingUtilities;

public class MainDashboardTest {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                MainDashboard dashboard = new MainDashboard(1);
                dashboard.setVisible(true);
            } catch (Exception err) {
                System.err.println("Dashboard failed to load");
            }
        });
    }
}