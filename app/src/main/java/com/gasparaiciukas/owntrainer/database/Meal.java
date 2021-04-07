package com.gasparaiciukas.owntrainer.database;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Meal extends RealmObject {

    @PrimaryKey
    @Required
    private String title;
    private RealmList<Food> foodList;
    private double calories = 0;
    private double carbs = 0;
    private double fat = 0;
    private double protein = 0;
    private String instructions;

    public double calculateCalories() {
        double mCalories = 0;
        for (Food f : foodList) {
            mCalories += f.getCalories();
        }
        return mCalories;
    }

    public double calculateCarbs() {
        double mCarbs = 0;
        for (Food f : foodList) {
            mCarbs += f.getCarbs();
        }
        return mCarbs;
    }

    public double calculateFat() {
        double mFat = 0;
        for (Food f : foodList) {
            mFat += f.getFat();
        }
        return mFat;
    }

    public double calculateProtein() {
        double mProtein = 0;
        for (Food f : foodList) {
            mProtein += f.getProtein();
        }
        return mProtein;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public RealmList<Food> getFoodList() {
        return foodList;
    }

    public void setFoodList(RealmList<Food> foodList) {
        this.foodList = foodList;
    }

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public double getCarbs() {
        return carbs;
    }

    public void setCarbs(double carbs) {
        this.carbs = carbs;
    }

    public double getFat() {
        return fat;
    }

    public void setFat(double fat) {
        this.fat = fat;
    }

    public double getProtein() {
        return protein;
    }

    public void setProtein(double protein) {
        this.protein = protein;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
}
