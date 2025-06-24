package main.java.com.nutrisci.model;
// NutriSci - Nutrition and Exercise Tracking Application

public class Profile {
    private String name;
    private String sex;
    private int age;
    private double heightCm;
    private double weightKg;
    private String unit;

    public Profile(String name, String sex, int age, double heightCm, double weightKg, String unit) {
        this.name = name;
        this.sex = sex;
        this.age = age;
        this.heightCm = heightCm;
        this.weightKg = weightKg;
        this.unit = unit;
    }

    public String getName() { return name; }
    public double getBMI() { return weightKg / Math.pow(heightCm / 100.0, 2); }

    @Override
    public String toString() {
        return String.format(
            "Name: %s%nSex: %s%nAge: %d%nHeight: %.1f cm%nWeight: %.1f kg%nBMI: %.2f",
            name, sex, age, heightCm, weightKg, getBMI()
        );
    }
}
