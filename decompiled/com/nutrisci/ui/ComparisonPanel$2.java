/*
 * Decompiled with CFR 0.152.
 */
package com.nutrisci.ui;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

class ComparisonPanel.2
extends DefaultTableCellRenderer {
    ComparisonPanel.2() {
    }

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
}
