/*
 * Decompiled with CFR 0.152.
 */
package com.nutrisci.ui;

import com.nutrisci.model.NutrientTotals;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import javax.swing.JOptionPane;

private class SwapImpactPanel.AnalyzeActionListener
implements ActionListener {
    private SwapImpactPanel.AnalyzeActionListener() {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (SwapImpactPanel.this.profileId <= 0) {
            JOptionPane.showMessageDialog(SwapImpactPanel.this, "Please select a valid profile first.", "No Profile Selected", 2);
            return;
        }
        try {
            Date selectedDate = (Date)SwapImpactPanel.this.dateSpinner.getValue();
            LocalDate analysisDate = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            Map<String, NutrientTotals> results = SwapImpactPanel.this.analysisModule.computeSwapBeforeAfter(SwapImpactPanel.this.profileId, analysisDate);
            if (results.isEmpty() || !results.containsKey("before") || !results.containsKey("after")) {
                JOptionPane.showMessageDialog(SwapImpactPanel.this, "No swap data found for the selected date.\nTry date: 2025-07-20 for test data.", "No Data", 1);
                return;
            }
            SwapImpactPanel.this.createComparisonChart(results.get("before"), results.get("after"), analysisDate);
        }
        catch (SQLException ex) {
            JOptionPane.showMessageDialog(SwapImpactPanel.this, "Database error: " + ex.getMessage(), "Error", 0);
        }
        catch (Exception ex) {
            JOptionPane.showMessageDialog(SwapImpactPanel.this, "Analysis error: " + ex.getMessage(), "Error", 0);
        }
    }
}
