/*
 * Decompiled with CFR 0.152.
 */
package com.nutrisci.ui;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

class ComparisonPanelCellRenderer
extends DefaultTableCellRenderer {
    ComparisonPanelCellRenderer() {
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (!isSelected) {
            String status = (String)table.getValueAt(row, 4);
            switch (status) {
                case "Added in Meal 2":
                    this.setBackground(new Color(220, 255, 220));
                    break;
                case "Removed from Meal 1":
                    this.setBackground(new Color(255, 220, 220));
                    break;
                case "Increased":
                    this.setBackground(new Color(255, 255, 200));
                    break;
                case "Decreased":
                    this.setBackground(new Color(200, 220, 255));
                    break;
                default:
                    this.setBackground(Color.WHITE);
                    break;
            }
        }

        if (column >= 1 && column <= 3) {
            this.setHorizontalAlignment(0);
        } else {
            this.setHorizontalAlignment(2);
        }
        return this;
    }
}
