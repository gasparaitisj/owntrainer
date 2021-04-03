package com.gasparaiciukas.owntrainer.database;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class User extends RealmObject {

    @PrimaryKey
    @Required
    private String userId;

    // Basic information
    private String sex;
    private int ageInYears;
    private int heightInCm;
    private double weightInKg;
    private String lifestyle;
    private double avgWalkingSpeedInKmH = 5;
    private int dailyStepGoal = 10000;

    // Calculated information using formulas
    private double bmr;                 // basal metabolic rate
    private double kcalBurnedPerStep;
    private double dailyKcalIntake;
    private double dailyProteinIntakeInG;
    private double dailyFatIntakeInG;
    private double dailyCarbsIntakeInG;
    private double stepLengthInCm;

    public double calculateBmr(double weightInKg, double heightInCm, int age, String sex) {
        if (sex.equals("Male"))
            return ((10 * weightInKg) + (6.25 * heightInCm) - (5 * age) + 5);
        else
            return ((10 * weightInKg) + (6.25 * heightInCm) - (5 * age) - 161);
    }

    public double calculateDailyKcalIntake(double bmr, String lifestyle) {
        switch (lifestyle) {
            case "Sedentary":
                return bmr * 1.2;
            case "Lightly active":
                return bmr * 1.375;
            case "Moderately active":
                return bmr * 1.55;
            case "Very active":
                return bmr * 1.725;
            case "Extra active":
                return bmr * 1.9;
            default:
                return 0;
        }
    }

    public double calculateKcalBurnedPerStep(double weightInKg, double heightInCm, double avgWalkingSpeedInKmH) {
        return (((0.035 * weightInKg) + ((avgWalkingSpeedInKmH / heightInCm) * 0.029 * weightInKg)) / 100);
    }

    public double calculateDailyProteinIntakeInG(double weightInKg) {
        return weightInKg;
    }

    public double calculateDailyFatIntake(double dailyKcalIntake) {
        return ((dailyKcalIntake * 0.3) / 9);
    }

    public double calculateDailyCarbsIntake(double dailyKcalIntake) {
        return ((dailyKcalIntake * 0.55) / 4);
    }

    public double calculateStepLengthInCm(double heightInCm, String sex) {
        if (sex.equals("Male"))
            return 0.415 * heightInCm;
        else
            return 0.413 * heightInCm;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getAgeInYears() {
        return ageInYears;
    }

    public void setAgeInYears(int ageInYears) {
        this.ageInYears = ageInYears;
    }

    public int getHeightInCm() {
        return heightInCm;
    }

    public void setHeightInCm(int heightInCm) {
        this.heightInCm = heightInCm;
    }

    public double getWeightInKg() {
        return weightInKg;
    }

    public void setWeightInKg(double weightInKg) {
        this.weightInKg = weightInKg;
    }

    public String getLifestyle() {
        return lifestyle;
    }

    public void setLifestyle(String lifestyle) {
        this.lifestyle = lifestyle;
    }

    public double getAvgWalkingSpeedInKmH() {
        return avgWalkingSpeedInKmH;
    }

    public void setAvgWalkingSpeedInKmH(double avgWalkingSpeedInKmH) {
        this.avgWalkingSpeedInKmH = avgWalkingSpeedInKmH;
    }

    public int getDailyStepGoal() {
        return dailyStepGoal;
    }

    public void setDailyStepGoal(int dailyStepGoal) {
        this.dailyStepGoal = dailyStepGoal;
    }

    public double getBmr() {
        return bmr;
    }

    public void setBmr(double bmr) {
        this.bmr = bmr;
    }

    public double getKcalBurnedPerStep() {
        return kcalBurnedPerStep;
    }

    public void setKcalBurnedPerStep(double kcalBurnedPerStep) {
        this.kcalBurnedPerStep = kcalBurnedPerStep;
    }

    public double getDailyKcalIntake() {
        return dailyKcalIntake;
    }

    public void setDailyKcalIntake(double dailyKcalIntake) {
        this.dailyKcalIntake = dailyKcalIntake;
    }

    public double getDailyProteinIntakeInG() {
        return dailyProteinIntakeInG;
    }

    public void setDailyProteinIntakeInG(double dailyProteinIntakeInG) {
        this.dailyProteinIntakeInG = dailyProteinIntakeInG;
    }

    public double getDailyFatIntakeInG() {
        return dailyFatIntakeInG;
    }

    public void setDailyFatIntakeInG(double dailyFatIntakeInG) {
        this.dailyFatIntakeInG = dailyFatIntakeInG;
    }

    public double getDailyCarbsIntakeInG() {
        return dailyCarbsIntakeInG;
    }

    public void setDailyCarbsIntakeInG(double dailyCarbsIntakeInG) {
        this.dailyCarbsIntakeInG = dailyCarbsIntakeInG;
    }

    public double getStepLengthInCm() {
        return stepLengthInCm;
    }

    public void setStepLengthInCm(double stepLengthInCm) {
        this.stepLengthInCm = stepLengthInCm;
    }
}
