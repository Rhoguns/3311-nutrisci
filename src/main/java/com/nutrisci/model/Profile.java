package com.nutrisci.model;

import java.time.LocalDate;

/**
 * User profile data.
 */
public class Profile {
    /** Profile ID */
    private int id;
    
    /** User's name */
    private String name;
    
    /** User's gender */
    private String sex;
    
    /** Birth date */
    private LocalDate dateOfBirth;
    
    /** Height in cm */
    private double heightCm;
    
    /** Weight in kg */
    private double weightKg;
    
    /** Units (metric/imperial) */
    private String unit;
    
    /** Email */
    private String email;

    /**
     * Creates empty profile.
     */
    public Profile() {
    }

    /**
     * Creates profile with data.
     */
    public Profile(String name, String sex, LocalDate dateOfBirth, double heightCm, double weightKg, String unit, String email) {
        this.name = name;
        this.sex = sex;
        this.dateOfBirth = dateOfBirth;
        this.heightCm = heightCm;
        this.weightKg = weightKg;
        this.unit = unit;
        this.email = email;
    }

    /**
     * Returns the unique identifier for this profile.
     * 
     * @return the profile ID
     */
    public int getId() {
        return this.id;
    }

    /**
     * Sets the unique identifier for this profile.
     * 
     * @param id the profile ID to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the full name of the user.
     * 
     * @return the user's name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the full name of the user.
     * 
     * @param name the user's name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the gender/sex of the user.
     * 
     * @return the user's sex (M, F, or Other)
     */
    public String getSex() {
        return this.sex;
    }

    /**
     * Sets the gender/sex of the user.
     * 
     * @param sex the user's sex to set (M, F, or Other)
     */
    public void setSex(String sex) {
        this.sex = sex;
    }

    /**
     * Returns the date of birth of the user.
     * 
     * @return the user's date of birth
     */
    public LocalDate getDateOfBirth() {
        return this.dateOfBirth;
    }

    /**
     * Sets the date of birth of the user.
     * 
     * @param dateOfBirth the user's date of birth to set
     */
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    /**
     * Returns the height of the user in centimeters.
     * 
     * @return the user's height in cm
     */
    public double getHeightCm() {
        return this.heightCm;
    }

    /**
     * Sets the height of the user in centimeters.
     * 
     * @param heightCm the user's height in cm to set
     */
    public void setHeightCm(double heightCm) {
        this.heightCm = heightCm;
    }

    /**
     * Returns the weight of the user in kilograms.
     * 
     * @return the user's weight in kg
     */
    public double getWeightKg() {
        return this.weightKg;
    }

    /**
     * Sets the weight of the user in kilograms.
     * 
     * @param weightKg the user's weight in kg to set
     */
    public void setWeightKg(double weightKg) {
        this.weightKg = weightKg;
    }

    /**
     * Returns the preferred unit system for measurements.
     * 
     * @return the unit system (metric or imperial)
     */
    public String getUnit() {
        return this.unit;
    }

    /**
     * Sets the preferred unit system for measurements.
     * 
     * @param unit the unit system to set (metric or imperial)
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * Returns the email address of the user.
     * 
     * @return the user's email address
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Sets the email address of the user.
     * 
     * @param email the user's email address to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns a string representation of this profile.
     * 
     * @return a formatted string containing all profile information
     */
    public String toString() {
        return "Profile{id=" + this.id + ", name='" + this.name + "', sex='" + this.sex + "', dateOfBirth=" + String.valueOf(this.dateOfBirth) + ", heightCm=" + this.heightCm + ", weightKg=" + this.weightKg + ", unit='" + this.unit + "', email='" + this.email + "'}";
    }
}
