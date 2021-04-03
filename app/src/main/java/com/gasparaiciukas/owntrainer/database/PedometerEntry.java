package com.gasparaiciukas.owntrainer.database;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class PedometerEntry extends RealmObject {

    @PrimaryKey
    @Required
    private String yearAndDayOfYear;
    private int year;
    private int dayOfYear;
    private int steps;
    private int calories;
    private double distanceInKm;
    private int timeElapsedInS;

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getDayOfYear() {
        return dayOfYear;
    }

    public void setDayOfYear(int dayOfYear) {
        this.dayOfYear = dayOfYear;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public double getDistanceInKm() {
        return distanceInKm;
    }

    public void setDistanceInKm(double distanceInKm) {
        this.distanceInKm = distanceInKm;
    }

    public int getTimeElapsedInS() {
        return timeElapsedInS;
    }

    public void setTimeElapsedInS(int timeElapsedInS) {
        this.timeElapsedInS = timeElapsedInS;
    }

    public String getYearAndDayOfYear() {
        return yearAndDayOfYear;
    }

    public void setYearAndDayOfYear(String yearAndDayOfYear) {
        this.yearAndDayOfYear = yearAndDayOfYear;
    }
}
