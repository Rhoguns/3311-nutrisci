package main.java.com.nutrisci.model;
// NutriSci - Nutrition and Exercise Tracking Application

/**
 * Represents a user’s profile, including basic demographics
 * and methods to compute derived metrics like BMI.
 */

public class Profile {
   /** The user’s full name. */
    private String name;

    /** Sex or gender of the user (e.g. "Male", "Female"). */
    private String sex;

    /** Age in years. */
    private int age;

    /** Height in centimetres. */
    private double heightCm;

    /** Weight in kilograms. */
    private double weightKg;

    /** Unit system: "metric" or "imperial". */
    private String unit;
    
    /**
     * Creates a complete Profile.
     *
     * @param name     the user’s name
     * @param sex      the user’s sex or gender
     * @param age      the user’s age in years
     * @param heightCm the user’s height in centimetres
     * @param weightKg the user’s weight in kilograms
     * @param unit     the unit system, e.g. "metric" or "imperial"
     */

    // Constructor
    public Profile(String name, String sex, int age, double heightCm, double weightKg, String unit) {
        this.name = name;
        this.sex = sex;
        this.age = age;
        this.heightCm = heightCm;
        this.weightKg = weightKg;
        this.unit = unit;
    }

    // Getters
    /**
     * @return the user’s name
     */

    public String getName() { return name; }

    /**
     * Calculates the Body Mass Index (BMI).
     *
     * <p>Formula: weightKg / (heightCm/100)&sup2;</p>
     *
     * @return the BMI value
     */
    public double getBMI() { return weightKg / Math.pow(heightCm / 100.0, 2); }

    /**
     * @return a multi-line string summarizing all profile fields
     */
    @Override
    public String toString() {
        return String.format(
            "Name: %s%nSex: %s%nAge: %d%nHeight: %.1f cm%nWeight: %.1f kg%nBMI: %.2f",
            name, sex, age, heightCm, weightKg, getBMI()
        );
    }
}
