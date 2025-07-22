/*
 * Decompiled with CFR 0.152.
 */
package com.nutrisci.model;

import java.time.LocalDate;

public class AppliedSwap {
    private int id;
    private int profileId;
    private int swapRuleId;
    private double originalQty;
    private double newQty;
    private LocalDate date;

    public AppliedSwap() {
    }

    public AppliedSwap(int profileId, int swapRuleId, double originalQty, double newQty, LocalDate date) {
        this.profileId = profileId;
        this.swapRuleId = swapRuleId;
        this.originalQty = originalQty;
        this.newQty = newQty;
        this.date = date;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProfileId() {
        return this.profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    public int getSwapRuleId() {
        return this.swapRuleId;
    }

    public void setSwapRuleId(int swapRuleId) {
        this.swapRuleId = swapRuleId;
    }

    public double getOriginalQty() {
        return this.originalQty;
    }

    public void setOriginalQty(double originalQty) {
        this.originalQty = originalQty;
    }

    public double getNewQty() {
        return this.newQty;
    }

    public void setNewQty(double newQty) {
        this.newQty = newQty;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
