/*
 * Decompiled with CFR 0.152.
 */
package com.nutrisci.ui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

class ProfileSelector.1
extends WindowAdapter {
    ProfileSelector.1() {
    }

    @Override
    public void windowClosed(WindowEvent we) {
        ProfileSelector.this.refreshProfiles();
    }
}
