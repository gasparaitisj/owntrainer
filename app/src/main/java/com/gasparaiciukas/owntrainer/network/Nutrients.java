package com.gasparaiciukas.owntrainer.network;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Nutrients {

    @SerializedName("ENERC_KCAL")
    @Expose
    private double calories;
    @SerializedName("CHOCDF")
    @Expose
    private double carbs;
    @SerializedName("FAT")
    @Expose
    private double fat;
    @SerializedName("PROCNT")
    @Expose
    private double protein;

    public double getCalories() {
        return calories;
    }

    public double getCarbs() {
        return carbs;
    }

    public double getFat() {
        return fat;
    }

    public double getProtein() {
        return protein;
    }

}