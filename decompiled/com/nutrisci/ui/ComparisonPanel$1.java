/*
 * Decompiled with CFR 0.152.
 */
package com.nutrisci.ui;

import javax.swing.table.DefaultTableModel;

class ComparisonPanel.1
extends DefaultTableModel {
    ComparisonPanel.1(Object[] $anonymous0, int $anonymous1) {
        super($anonymous0, $anonymous1);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
