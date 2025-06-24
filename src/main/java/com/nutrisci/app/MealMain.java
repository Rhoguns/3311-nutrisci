package main.java.com.nutrisci.app;
// NutriSci - Nutrition and Exercise Tracking Application

import main.java.com.nutrisci.model.Meal;
import java.util.Date;

public class MealMain {
    public static void main(String[] args) {
        Meal m = new Meal("Lunch", new Date());
        m.addIngredient("Egg", 100);
        m.addIngredient("Tomato", 50);
        m.addIngredient("Bread", 60);
        System.out.println(m.summary());
    }
}

