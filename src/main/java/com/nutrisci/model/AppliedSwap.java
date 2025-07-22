/*
 * Decompiled with CFR 0.152.
 */
package com.nutrisci.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AppliedSwap {
    private int id;
    private int profileId;
    private int swapRuleId;
    private double originalQty;
    private double newQty;
    private LocalDate date;
    private LocalDateTime appliedAt;
    private int mealId;

    public AppliedSwap() {
    }

    public AppliedSwap(int profileId, int swapRuleId, double originalQty, double newQty, LocalDate date) {
        this.profileId = profileId;
        this.swapRuleId = swapRuleId;
        this.originalQty = originalQty;
        this.newQty = newQty;
        this.date = date;
    }

    // All getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProfileId() { return profileId; }
    public void setProfileId(int profileId) { this.profileId = profileId; }

    public int getSwapRuleId() { return swapRuleId; }
    public void setSwapRuleId(int swapRuleId) { this.swapRuleId = swapRuleId; }

    public double getOriginalQty() { return originalQty; }
    public void setOriginalQty(double originalQty) { this.originalQty = originalQty; }

    public double getNewQty() { return newQty; }
    public void setNewQty(double newQty) { this.newQty = newQty; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalDateTime getAppliedAt() { return appliedAt; }
    public void setAppliedAt(LocalDateTime appliedAt) { this.appliedAt = appliedAt; }

    public int getMealId() { return mealId; }
    public void setMealId(int mealId) { this.mealId = mealId; }

    @Override
    public String toString() {
        return String.format("AppliedSwap{id=%d, profileId=%d, swapRuleId=%d, mealId=%d, appliedAt=%s}", 
                id, profileId, swapRuleId, mealId, appliedAt);
    }
}
