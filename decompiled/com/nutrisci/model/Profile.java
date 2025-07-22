/*
 * Decompiled with CFR 0.152.
 */
package com.nutrisci.model;

import java.time.LocalDate;

public class Profile {
    private int id;
    private String name;
    private String sex;
    private LocalDate dateOfBirth;
    private double heightCm;
    private double weightKg;
    private String unit;
    private String email;

    public Profile() {
    }

    public Profile(String name, String sex, LocalDate dateOfBirth, double heightCm, double weightKg, String unit, String email) {
        this.name = name;
        this.sex = sex;
        this.dateOfBirth = dateOfBirth;
        this.heightCm = heightCm;
        this.weightKg = weightKg;
        this.unit = unit;
        this.email = email;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return this.sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public LocalDate getDateOfBirth() {
        return this.dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public double getHeightCm() {
        return this.heightCm;
    }

    public void setHeightCm(double heightCm) {
        this.heightCm = heightCm;
    }

    public double getWeightKg() {
        return this.weightKg;
    }

    public void setWeightKg(double weightKg) {
        this.weightKg = weightKg;
    }

    public String getUnit() {
        return this.unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String toString() {
        return "Profile{id=" + this.id + ", name='" + this.name + "', sex='" + this.sex + "', dateOfBirth=" + String.valueOf(this.dateOfBirth) + ", heightCm=" + this.heightCm + ", weightKg=" + this.weightKg + ", unit='" + this.unit + "', email='" + this.email + "'}";
    }
}
