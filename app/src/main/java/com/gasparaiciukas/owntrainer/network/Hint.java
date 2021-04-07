package com.gasparaiciukas.owntrainer.network;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Hint {

    @SerializedName("food")
    @Expose
    private FoodApi foodApi;

    public FoodApi getFoodApi() {
        return foodApi;
    }

    public void setFoodApi(FoodApi foodApi) {
        this.foodApi = foodApi;
    }

}