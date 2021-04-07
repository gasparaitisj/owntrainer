package com.gasparaiciukas.owntrainer.database;

import io.realm.RealmObject;

public class Food extends RealmObject {
    private String title;
    private double caloriesPer100G;
    private double carbsPer100G;
    private double fatPer100G;
    private double proteinPer100G;
    private double calories;
    private double carbs;
    private double fat;
    private double protein;
    private double quantityInG;

    public double calculateCarbs(double carbsPer100G, double quantity) {
        return ((carbsPer100G / 100) * quantity);
    }

    public double calculateCalories(double caloriesPer100G, double quantity) {
        return ((caloriesPer100G / 100) * quantity);
    }

    public double calculateFat(double fatPer100G, double quantity) {
        return ((fatPer100G / 100) * quantity);
    }

    public double calculateProtein(double proteinPer100G, double quantity) {
        return ((proteinPer100G / 100) * quantity);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public double getQuantityInG() {
        return quantityInG;
    }

    public void setQuantityInG(double quantityInG) {
        this.quantityInG = quantityInG;
    }

    public double getCaloriesPer100G() {
        return caloriesPer100G;
    }

    public void setCaloriesPer100G(double caloriesPer100G) {
        this.caloriesPer100G = caloriesPer100G;
    }

    public double getCarbsPer100G() {
        return carbsPer100G;
    }

    public void setCarbsPer100G(double carbsPer100G) {
        this.carbsPer100G = carbsPer100G;
    }

    public double getFatPer100G() {
        return fatPer100G;
    }

    public void setFatPer100G(double fatPer100G) {
        this.fatPer100G = fatPer100G;
    }

    public double getProteinPer100G() {
        return proteinPer100G;
    }

    public void setProteinPer100G(double proteinPer100G) {
        this.proteinPer100G = proteinPer100G;
    }
}
