package com.nutrisci.model;

import java.time.LocalDate;

public class Profile {
    // Unique identifier for the user profile.
    private int id;
    // Name of the user.
    private String name;
    // Sex of the user (e.g., "Male", "Female").
    private String sex;
    // Date of birth of the user.
    private LocalDate dateOfBirth;
    // Height of the user in centimeters.
    private double heightCm;
    // Weight of the user in kilograms.
    private double weightKg;
    // Preferred unit system for display (e.g., "metric", "imperial").
    private String unit;

    public Profile() {
        // default constructor
    }

    public Profile(String name, String sex, LocalDate dateOfBirth,
                   double heightCm, double weightKg, String unit) {
        this.name = name;
        this.sex = sex;
        this.dateOfBirth = dateOfBirth;
        this.heightCm = heightCm;
        this.weightKg = weightKg;
        this.unit = unit;
    }

    /**
 * Getters and Setters for all fields.
 */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
 * Returns the name of the profile.
 * @return the name
 */
    public String getName() {
        return name;
    }

    /**
 * Sets the name of the profile.
 * @param name the name to set
 */
    public void setName(String name) {
        this.name = name;
    }

    /**
 * Returns the sex of the profile.
 * @return the sex
 */
    public String getSex() {
        return sex;
    }

    /**
 * Sets the sex of the profile.
 * @param sex the sex to set
 */
    public void setSex(String sex) {
        this.sex = sex;
    }

    /**
 * Returns the date of birth of the profile.
 * @return the date of birth
 */
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    /**
 * Sets the date of birth of the profile.
 * @param dateOfBirth the date of birth to set
 */
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    /**
 * Returns the height of the profile in centimeters.
 * @return the height in cm
 */
    public double getHeightCm() {
        return heightCm;
    }

    /**
 * Sets the height of the profile in centimeters.
 * @param heightCm the height in cm to set
 */
    public void setHeightCm(double heightCm) {
        this.heightCm = heightCm;
    }

    /**
 * Returns the weight of the profile in kilograms.
 * @return the weight in kg
 */
    public double getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(double weightKg) {
        this.weightKg = weightKg;
    }

    /**
 * Returns the preferred unit system of the profile.
 * @return the unit system
 */
    public String getUnit() {
        return unit;
    }

    /**
 * Sets the preferred unit system of the profile.
 * @param unit the unit system to set
 */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        return "Profile{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", sex='" + sex + '\'' +
               ", dateOfBirth=" + dateOfBirth +
               ", heightCm=" + heightCm +
               ", weightKg=" + weightKg +
               ", unit='" + unit + '\'' +
               '}';
    }
}