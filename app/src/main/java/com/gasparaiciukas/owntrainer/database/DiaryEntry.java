package com.gasparaiciukas.owntrainer.database;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class DiaryEntry extends RealmObject {
    @PrimaryKey
    @Required
    private String yearAndDayOfYear;
    private int year;
    private int dayOfYear;
    private int dayOfWeek;
    private int monthOfYear;
    private int dayOfMonth;
    private RealmList<Meal> meals = new RealmList<>();

    public double calculateTotalCalories(List<Meal> meals) {
        double mCalories = 0;
        for (Meal m : meals) {
            mCalories += m.calculateCalories();
        }
        return mCalories;
    }

    public double calculateTotalProtein(List<Meal> meals) {
        double mProtein = 0;
        for (Meal m : meals) {
            mProtein += m.calculateProtein();
        }
        return mProtein;
    }

    public double calculateTotalFat(List<Meal> meals) {
        double mFat = 0;
        for (Meal m : meals) {
            mFat += m.calculateFat();
        }
        return mFat;
    }

    public double calculateTotalCarbs(List<Meal> meals) {
        double mCarbs = 0;
        for (Meal m : meals) {
            mCarbs += m.calculateCarbs();
        }
        return mCarbs;
    }


    public String getYearAndDayOfYear() {
        return yearAndDayOfYear;
    }

    public void setYearAndDayOfYear(String yearAndDayOfYear) {
        this.yearAndDayOfYear = yearAndDayOfYear;
    }

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

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public int getMonthOfYear() {
        return monthOfYear;
    }

    public void setMonthOfYear(int monthOfYear) {
        this.monthOfYear = monthOfYear;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public RealmList<Meal> getMeals() {
        return meals;
    }

    public void setMeals(RealmList<Meal> meals) {
        this.meals = meals;
    }
}
